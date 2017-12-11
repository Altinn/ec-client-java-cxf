package no.asf.formidling.client.ws.client;

import no.altinn.intermediaryinboundec.*;
import no.asf.formidling.client.config.ECClientConfig;
import no.asf.formidling.client.vo.SecurityCredentials;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.ws.BindingProvider;
import java.util.Properties;

public class IntermediaryInboundExternalECClient {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private ObjectFactory objectFactory;

    private IIntermediaryInboundExternalEC intermediaryInboundExternalEC;

    private SecurityCredentials credentials;

    public IntermediaryInboundExternalECClient(SecurityCredentials credentials) {
        this.credentials = credentials;
        this.objectFactory = new ObjectFactory();
        this.intermediaryInboundExternalEC = getIntermediaryInboundClient(credentials.getKeyStoreProperties());
    }

    protected ReceiptExternalBE sendFormTaskShipment(String reportee, String externalShipmentReference, String serviceCode,
                                                     int serviceEdition, String dataFormatId, int dataFormatVersion, String endUserSystemReference, String formData) throws IIntermediaryInboundExternalECSubmitFormTaskECAltinnFaultFaultFaultMessage {

        return intermediaryInboundExternalEC.submitFormTaskEC(credentials.getVirksomhetsbruker(), credentials.getVirksomhetsbrukerPassord(),
                getFormTaskShipmentBE(reportee, externalShipmentReference, serviceCode, serviceEdition, dataFormatId, dataFormatVersion, endUserSystemReference, formData));
                                                        
    }

    private IIntermediaryInboundExternalEC getIntermediaryInboundClient(Properties signatureProperties) {
        IntermediaryInboundExternalEC service = new IntermediaryInboundExternalEC();
        IIntermediaryInboundExternalEC port = service.getCustomBindingIIntermediaryInboundExternalEC();
        BindingProvider bindingProvider = (BindingProvider) port;
        bindingProvider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, ECClientConfig.INTERMEDIARY_INBOUND_EXTERNAL_EC);
        Client client = ClientProxy.getClient(port);
        client.getRequestContext().put("security.must-understand", true);
        client.getRequestContext().put("org.apache.cxf.message.Message.MAINTAIN_SESSION", true);
        client.getRequestContext().put("javax.xml.ws.session.maintain", true);
        client.getRequestContext().put("security.cache.issued.token.in.endpoint", true); //default: true
        client.getRequestContext().put("security.issue.after.failed.renew", true); //This must be set to true (default: true)
        client.getRequestContext().put("security.signature.properties", signatureProperties);
        return port;
    }

    private FormTaskShipmentBE getFormTaskShipmentBE(String reportee, String externalShipmentReference, String serviceCode,
                                                     int serviceEdition, String dataFormatId, int dataFormatVersion, String endUserSystemReference, String formData) {

        FormTaskShipmentBE formTaskShipmentBE = objectFactory.createFormTaskShipmentBE();
        formTaskShipmentBE.setReportee(reportee);
        formTaskShipmentBE.setExternalShipmentReference(externalShipmentReference);
        formTaskShipmentBE.setFormTasks(getFormTask(serviceCode, serviceEdition, dataFormatId, dataFormatVersion, endUserSystemReference, formData));
        return formTaskShipmentBE;
    }

    private FormTask getFormTask(String serviceCode, int serviceEdition, String dataFormatId, int dataFormatVersion, String endUserSystemReference, String formData) {
        FormTask formTask = objectFactory.createFormTask();
        formTask.setServiceCode(serviceCode);
        formTask.setServiceEdition(serviceEdition);
        formTask.setForms(getArrayOfForm(dataFormatId, dataFormatVersion, endUserSystemReference, formData));
        return formTask;
    }

    private ArrayOfForm getArrayOfForm(String dataFormatId, int dataFormatVersion, String endUserSystemReference, String formData) {
        ArrayOfForm arrayOfForm = objectFactory.createArrayOfForm();
        arrayOfForm.getForm().add(getForm(dataFormatId, dataFormatVersion, endUserSystemReference, formData));
        return arrayOfForm;
    }

    private Form getForm(String dataFormatId, int dataFormatVersion, String endUserSystemReference, String formData) {
        Form form = objectFactory.createForm();
        form.setDataFormatId(dataFormatId);
        form.setDataFormatVersion(dataFormatVersion);
        form.setEndUserSystemReference(endUserSystemReference);
        form.setFormData(formData);
        return form;
    }
}
