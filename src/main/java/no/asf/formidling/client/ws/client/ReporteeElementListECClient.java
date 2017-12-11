package no.asf.formidling.client.ws.client;

import no.altinn.reporteeelementlistec.*;
import no.asf.formidling.client.config.ECClientConfig;
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

public class ReporteeElementListECClient {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private ObjectFactory objectFactory;

    private IReporteeElementListEC reporteeElementListEC;

    private SecurityCredentials credentials;

    public ReporteeElementListECClient(SecurityCredentials credentials) {
        this.credentials = credentials;
        this.objectFactory = new ObjectFactory();
        this.reporteeElementListEC = getReporteeElementListEC(credentials.getKeyStoreProperties());
    }

    protected ReporteeElementBEV2Lis getReporteeElementListEC(String reportee, XMLGregorianCalendar fromDate, XMLGregorianCalendar toDate, int languageID) throws ParseException, DatatypeConfigurationException, IReporteeElementListECGetReporteeElementListECAltinnFaultFaultFaultMessage {

        return reporteeElementListEC.getReporteeElementListEC(credentials.getVirksomhetsbruker(), credentials.getVirksomhetsbrukerPassord(), getExternalSearchBEV2(reportee, fromDate, toDate), languageID);
    }

    private ExternalSearchBEV2 getExternalSearchBEV2(String reportee, XMLGregorianCalendar fromDate, XMLGregorianCalendar toDate) throws ParseException, DatatypeConfigurationException {
        ExternalSearchBEV2 externalSearchBEV2 = objectFactory.createExternalSearchBEV2();
        externalSearchBEV2.setFromDate(fromDate);
        externalSearchBEV2.setToDate(toDate);
        externalSearchBEV2.setReportee(reportee);
        externalSearchBEV2.setSentAndArchived(false);
        return externalSearchBEV2;
    }

    private IReporteeElementListEC getReporteeElementListEC(Properties signatureProperties) {
        ReporteeElementListECSF service = new ReporteeElementListECSF();
        IReporteeElementListEC port = service.getCustomBindingIReporteeElementListEC();
        BindingProvider bindingProvider = (BindingProvider) port;
        bindingProvider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, ECClientConfig.REPORTEE_ELEMENT_LIST_EC);
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
