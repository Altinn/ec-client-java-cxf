package no.asf.formidling.client.ws.client;

import no.asf.formidling.client.config.EC2ClientConfig;
import no.asf.formidling.client.vo.SecurityCredentials;
import no.asf.formidling.client.vo.ServiceCode;
import no.asf.formidling.client.vo.UploadManifest;
import no.asf.formidling.client.vo.UploadResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.fail;

/**
 * Created by shjellvi on 20.03.2017.
 */
@ContextConfiguration(classes = {EC2ClientConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class FormidlingAvgiverTest {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private BrokerEC2Client brokerEC2Client;

    private SecurityCredentials avgiverCredentials;

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

        avgiverCredentials = new SecurityCredentials(properties.getProperty("avgiver.virksomhetsbruker"),
                properties.getProperty("avgiver.virksomhetsbrukerpassord"),
                properties.getProperty("avgiver.entity"),
                properties.getProperty("avgiver.certificate"),
                properties.getProperty("avgiver.entitypassword"),
                properties.getProperty("avgiver.alias"));

        serviceCode = new ServiceCode(properties.getProperty("formidling.altinn.externalservicecode"),
                Integer.parseInt(properties.getProperty("formidling.altinn.externalserviceeditioncode")));
        brokerEC2Client = new BrokerEC2Client(avgiverCredentials, serviceCode);
    }

    /**
     * Tester opplasting av en fil til formidlingstjenesten.
     */
    @Test
    public void avgiverFormidlingTest(){
        try {
            ArrayList fileList = new ArrayList();
            fileList.add("henvendelse.xml");
            fileList.add("vedlegg.txt");

            ArrayList recipientList = new ArrayList();
            recipientList.add(properties.getProperty("mottaker.entity"));

            String sendersReference = UUID.randomUUID().toString();

            UploadManifest uploadManifest = new UploadManifest(
                    sendersReference,
                    fileList,
                    recipientList,
                    "testfileToUpload.zip",
                    null);

            ClassPathResource classPathResource = new ClassPathResource("testfileToUpload.zip");
            FileDataSource fileDataSource = new FileDataSource(classPathResource.getFile());
            DataHandler dataHandler = new DataHandler(fileDataSource);

            UploadResponse uploadResponse = brokerEC2Client.uploadFile(uploadManifest, org.apache.commons.io.IOUtils.toByteArray(dataHandler.getInputStream()));

            assertThat(uploadResponse.getFileReference(), is(notNullValue()));
            log.info("\n\nUploaded file = " + uploadResponse.getFileReference() + "\n\n");

            assertThat(uploadResponse.getReceiptStreamed(), is(notNullValue()));
            log.info("\n\nReceiptText: " + uploadResponse.getReceiptStreamed().getReceiptText().getValue() + "\n\n");

        } catch (Exception e) {
            log.error(e.getMessage());
            fail("Caught exception");
        }
    }
}
