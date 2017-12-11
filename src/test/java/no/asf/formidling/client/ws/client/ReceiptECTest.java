package no.asf.formidling.client.ws.client;

import no.altinn.receiptexternalec.Receipt;
import no.altinn.receiptexternalec.ReceiptList;
import no.altinn.receiptexternalec.ReceiptStatusEnum;
import no.asf.formidling.client.ws.client.ReceiptECClient;
import no.asf.formidling.client.config.ECClientConfig;
import no.asf.formidling.client.vo.SecurityCredentials;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.fail;

/**
 * Created by shjellvi on 20.03.2017.
 */
@ContextConfiguration(classes = {ECClientConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class ReceiptECTest {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private ReceiptECClient receiptECClient;

    private SecurityCredentials avgiverCredentials;

    @Before
    public void setup(){
        Properties properties = new Properties();
        try {
            properties.loadFromXML(new ClassPathResource("properties/avgiver-properties.xml").getInputStream());
        } catch (IOException e){
            fail("Unable to load properties");
        }
        log.info("Getting receipt for\n" +
            "Avgiver = " + properties.getProperty("avgiver.entityusername"));

        avgiverCredentials = new SecurityCredentials(properties.getProperty("avgiver.virksomhetsbruker"),
                properties.getProperty("avgiver.virksomhetsbrukerpassord"),
                properties.getProperty("avgiver.entity"),
                properties.getProperty("avgiver.certificate"),
                properties.getProperty("avgiver.entitypassword"),
                properties.getProperty("avgiver.alias"));

        receiptECClient = new ReceiptECClient(avgiverCredentials);
    }

    @Test
    public void testGetReceipt(){
        log.info("testGetReceipt");

        try {
            Receipt receipt = receiptECClient.getReceipt(6924761, null);
            assertThat(receipt, notNullValue());
            log.info("Receipt received: " + receipt.getReceiptId() + receipt.getReceiptText().getValue());

        } catch (Exception e) {
            log.error(e.getMessage());
            fail("Caught exception");
        }
    }

    @Test
    public void testGetReceiptList(){
        log.info("testGetReceiptList");

        try {
            Date from = new GregorianCalendar(2017, 0, 01).getTime();
            Date to = new GregorianCalendar(2017, 11, 31).getTime();
            ReceiptList receipts = receiptECClient.getReceiptList(from, to);
            assertThat(receipts, notNullValue());
            assertThat(receipts.getReceipt().size(), greaterThan(0));
            for (Receipt receipt : receipts.getReceipt()){
                log.info("Receipt received: " + receipt.getReceiptId() + receipt.getReceiptText().getValue());
            }

        } catch (Exception e) {
            log.error(e.getMessage());
            fail("Caught exception");
        }
    }

    @Test
    public void testUpdateReceipt(){
        log.info("testUpdateReceipt");

        try {
            Receipt receipt = receiptECClient.updateReceipt(6924761, ReceiptStatusEnum.OK, null, null, "Test av oppdatering av kvittering", null);
            assertThat(receipt, notNullValue());
            log.info("Receipt updated: " + receipt.getReceiptId() + receipt.getReceiptText().getValue());

        } catch (Exception e) {
            log.error(e.getMessage());
            fail("Caught exception");
        }
    }
}
