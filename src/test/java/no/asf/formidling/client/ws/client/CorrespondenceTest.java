package no.asf.formidling.client.ws.client;

import no.altinn.correspondenceexternalec.CorrespondenceForEndUserSystemV2;
import no.altinn.reporteeelementlistec.*;
import no.asf.formidling.client.config.ECClientConfig;
import no.asf.formidling.client.util.DateConverter;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

@ContextConfiguration(classes = {ECClientConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class CorrespondenceTest {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private ReporteeElementListECClient reporteeElementListECClient;

    private CorrespondenceECClient correspondenceECClient;

    private SecurityCredentials avgiverCredentials;

    private static int LANGUAGEID_NB = 1044;

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

        reporteeElementListECClient = new ReporteeElementListECClient(avgiverCredentials);
        correspondenceECClient = new CorrespondenceECClient(avgiverCredentials);
    }

    @Test
    public void getReporteeListAndCorrespondence() throws Exception{

        Date toDate = new Date();
        SimpleDateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy");
        Date fromDate = dateformat.parse("06/09/2017");
        String reportee = "810514442";

        List<ReporteeElementBEV2> reporteeElementBEV2List = reporteeElementListECClient.getReporteeElementListEC(reportee,
                DateConverter.convertToXMLGregorianCalendar(fromDate), DateConverter.convertToXMLGregorianCalendar(toDate), LANGUAGEID_NB).getReporteeElementBEV2();

        for(ReporteeElementBEV2 elementBEV2 : reporteeElementBEV2List) {
            if(elementBEV2.getReporteeElementType().value().equals("Correspondence")) {
                CorrespondenceForEndUserSystemV2 correspondenceForEndUserSystemV2 = correspondenceECClient.getCorrespondenceForEndUserSystemsEC(elementBEV2.getReporteeElementId(), LANGUAGEID_NB);
                assertThat(correspondenceForEndUserSystemV2.getCorrespondence().getValue(), is(notNullValue()));
            }
        }

    }
}
