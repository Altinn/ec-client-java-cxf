package no.asf.formidling.client.ws.client;

import no.altinn.brokerserviceexternalec.BrokerServiceAvailableFile;
import no.altinn.brokerserviceexternalec.BrokerServiceAvailableFileList;
import no.altinn.brokerserviceexternalec.BrokerServiceAvailableFileStatus;
import no.altinn.receiptexternalec.Receipt;
import no.altinn.receiptexternalec.ReceiptStatusEnum;
import no.asf.formidling.client.config.EC2ClientConfig;
import no.asf.formidling.client.vo.SearchCriteria;
import no.asf.formidling.client.vo.SecurityCredentials;
import no.asf.formidling.client.vo.ServiceCode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.activation.DataHandler;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.fail;

/**
 * Created by shjellvi on 20.03.2017.
 */
@ContextConfiguration(classes = {EC2ClientConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class FormidlingMottakerTest {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private BrokerEC2Client brokerEC2Client;

    private ReceiptEC2Client receiptEC2Client;

    private SecurityCredentials mottakerCredentials;

    private ServiceCode serviceCode;

    private Properties properties;

    @Before
    public void setup(){
        properties = new Properties();
        try {
            properties.loadFromXML(new ClassPathResource("properties/avgiver-properties.xml").getInputStream());
            properties.loadFromXML(new ClassPathResource("properties/mottaker-properties.xml").getInputStream());
            properties.loadFromXML(new ClassPathResource("properties/formidling-tjeneste.xml").getInputStream());
        } catch (IOException e){
            fail("Unable to load properties");
        }

        log.info("Setting up formidlingstjenesten ....");
        log.info("Avgiver = " + properties.getProperty("avgiver.virksomhetsbruker"));
        log.info("Mottaker = " + properties.getProperty("mottaker.virksomhetsbruker"));

        mottakerCredentials = new SecurityCredentials(properties.getProperty("mottaker.virksomhetsbruker"),
                properties.getProperty("mottaker.virksomhetsbrukerpassord"),
                properties.getProperty("mottaker.entity"),
                properties.getProperty("mottaker.certificate"),
                properties.getProperty("mottaker.entitypassword"),
                properties.getProperty("mottaker.alias"));

        serviceCode = new ServiceCode(properties.getProperty("formidling.altinn.externalservicecode"),
                Integer.parseInt(properties.getProperty("formidling.altinn.externalserviceeditioncode")));

        brokerEC2Client = new BrokerEC2Client(mottakerCredentials, serviceCode);

        receiptEC2Client = new ReceiptEC2Client(mottakerCredentials);
    }

    /**
     * Tester mottak av filer fra formidlingstjenesten.
     *
     * Tjenestene kalles i følgende sekvens:
     * 1. getAvailableFiles. Finn tilgjengelige filer for nedlasting.
     * 2. downloadFile. Last ned fil.
     * 3. confirmDownloaded. Bekreft fil nedlastet ok.
     */
    @Test
    public void mottakerFormidlingTest(){
        try {
            /**
             * Steg 1: Finn tilgjengelige filer for nedlasting.
             */
            SearchCriteria criteria = new SearchCriteria(BrokerServiceAvailableFileStatus.UPLOADED, null, null);
            BrokerServiceAvailableFileList brokerServiceAvailableFileList = brokerEC2Client.getAvailableFiles(criteria);

            assertThat(brokerServiceAvailableFileList, is(notNullValue()));


            log.info("There are " + brokerServiceAvailableFileList.getBrokerServiceAvailableFile().size() + " available for download");
            for (BrokerServiceAvailableFile availableFile : brokerServiceAvailableFileList.getBrokerServiceAvailableFile()){
                log.info("Available file: " + availableFile.getFileName().getValue() + " reference: " + availableFile.getFileReference());
            }

            BrokerServiceAvailableFile recentAvailableFile = brokerServiceAvailableFileList.getBrokerServiceAvailableFile().get(brokerServiceAvailableFileList.getBrokerServiceAvailableFile().size() - 1);
            assertThat(recentAvailableFile, is(notNullValue()));

            String downloadFileReference = recentAvailableFile.getFileReference();
            assertThat(downloadFileReference, is(notNullValue()));

            /**
             * Steg 2: Last ned fil.
             */
            byte[] dataHandler = brokerEC2Client.downloadFile(downloadFileReference);

            assertThat(dataHandler, is(notNullValue()));
            log.info("Downloaded file " + downloadFileReference);

            ByteArrayOutputStream buffOS = new ByteArrayOutputStream(dataHandler.length);
            buffOS.write(dataHandler,0, dataHandler.length);

            assertThat(buffOS.size(), greaterThan(0));

            File outFile = File.createTempFile(downloadFileReference, ".zip");
            FileOutputStream fos = new FileOutputStream(outFile);
            fos.write(buffOS.toByteArray());
            fos.close();

            log.info("Wrote to file " + outFile.getAbsolutePath());

            /**
             * Steg 3: Oppdater kvittering.
             */
            Integer subReceiptId = recentAvailableFile.getReceiptID();
            assertThat(subReceiptId, is(notNullValue()));

            Receipt receipt = receiptEC2Client.updateReceipt(subReceiptId, ReceiptStatusEnum.OK, null, null,
                    "Formidling mottatt og verifisert ok av " + properties.getProperty("avgiver.entityusername"), null);
            assertThat(receipt, notNullValue());
            log.info("Receipt updated: " + receipt.getReceiptId() + receipt.getReceiptText().getValue());

            /**
             * Steg 4: bekreft fil nedlastet ok.
             */
            brokerEC2Client.confirmDownloaded(downloadFileReference);

            log.info("Confirmed download of file " + downloadFileReference);

        } catch (Exception e) {
            log.error(e.getMessage());
            fail("Caught exception");
        }
    }
}
