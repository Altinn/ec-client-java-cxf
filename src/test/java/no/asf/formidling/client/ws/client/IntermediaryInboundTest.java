package no.asf.formidling.client.ws.client;

import no.altinn.intermediaryinboundec.ReceiptExternalBE;
import no.asf.formidling.client.config.ECClientConfig;
import no.asf.formidling.client.vo.SecurityCredentials;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.Properties;
import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

@ContextConfiguration(classes = {ECClientConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class IntermediaryInboundTest {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private SecurityCredentials avgiverCredentials;

    private IntermediaryInboundExternalECClient inboundExternalECClient;

    @Before
    public void setup() {
        Properties properties = new Properties();
        try {
            properties.loadFromXML(new ClassPathResource("properties/avgiver-properties.xml").getInputStream());
        } catch (IOException e) {
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

        inboundExternalECClient = new IntermediaryInboundExternalECClient(avgiverCredentials);
    }

    @Test
    public void sendFormTaskTest() throws Exception{
        ClassPathResource classPathResource = new ClassPathResource("FormTask.xml");
        String formData = FileUtils.readFileToString(classPathResource.getFile(), "utf-8");
        String externalShipmentReference = UUID.randomUUID().toString();
        String endUserSystemReference = UUID.randomUUID().toString();
        String reportee = "810514442";
        String serviceCode = "3928";
        int serviceEdition = 1;
        String dataFormatId = "3881";
        int dataFormatVersion = 34642;
        ReceiptExternalBE receiptExternalBE = inboundExternalECClient.sendFormTaskShipment(reportee, externalShipmentReference, serviceCode,
        serviceEdition, dataFormatId, dataFormatVersion, endUserSystemReference, formData);
        assertThat(receiptExternalBE.getReceiptStatusCode().value(), is("OK"));
    }
}
