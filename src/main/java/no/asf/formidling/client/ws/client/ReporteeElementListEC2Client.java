package no.asf.formidling.client.ws.client;

import no.altinn.reporteeelementlistec.*;
import no.asf.formidling.client.config.EC2ClientConfig;
import no.asf.formidling.client.util.DateConverter;
import no.asf.formidling.client.vo.SecurityCredentials;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.BindingProvider;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class ReporteeElementListEC2Client {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private ObjectFactory objectFactory;

    private IReporteeElementListEC2 reporteeElementListEC2;

    private SecurityCredentials credentials;

    public ReporteeElementListEC2Client(SecurityCredentials credentials) {
        this.credentials = credentials;
        this.objectFactory = new ObjectFactory();
        this.reporteeElementListEC2 = getReporteeElementListEC2(credentials.getKeyStoreProperties());
    }

    protected ReporteeElementBEV2Lis getReporteeElementListEC2(String reportee, XMLGregorianCalendar fromDate, XMLGregorianCalendar toDate, int languageID) throws ParseException, DatatypeConfigurationException, IReporteeElementListEC2GetReporteeElementListECAltinnFaultFaultFaultMessage {

        return reporteeElementListEC2.getReporteeElementListEC(credentials.getVirksomhetsbruker(), credentials.getVirksomhetsbrukerPassord(), getExternalSearchBEV2(reportee, fromDate, toDate), languageID);
    }

    private ExternalSearchBEV2 getExternalSearchBEV2(String reportee, XMLGregorianCalendar fromDate, XMLGregorianCalendar toDate) throws ParseException, DatatypeConfigurationException {
        ExternalSearchBEV2 externalSearchBEV2 = objectFactory.createExternalSearchBEV2();
        externalSearchBEV2.setFromDate(fromDate);
        externalSearchBEV2.setToDate(toDate);
        externalSearchBEV2.setReportee(reportee);
        externalSearchBEV2.setSentAndArchived(false);
        return externalSearchBEV2;
    }

    private IReporteeElementListEC2 getReporteeElementListEC2(Properties signatureProperties) {
        ReporteeElementListEC2SF service = new ReporteeElementListEC2SF();
        IReporteeElementListEC2 port = service.getCustomBindingIReporteeElementListEC2();
        BindingProvider bindingProvider = (BindingProvider) port;
        bindingProvider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, EC2ClientConfig.REPORTEE_ELEMENT_LIST_EC2);
        Client client = ClientProxy.getClient(port);
        client.getRequestContext().put("security.must-understand", true);
        client.getRequestContext().put("org.apache.cxf.message.Message.MAINTAIN_SESSION", true);
        client.getRequestContext().put("javax.xml.ws.session.maintain", true);
        client.getRequestContext().put("security.cache.issued.token.in.endpoint", true); //default: true
        client.getRequestContext().put("security.issue.after.failed.renew", true); //This must be set to true (default: true)
        client.getRequestContext().put("security.signature.properties", signatureProperties);
        return port;
    }
}
