package no.asf.formidling.client.ws.client;

import no.altinn.brokerserviceexternalec.*;
import no.asf.formidling.client.config.EC2ClientConfig;
import no.asf.formidling.client.util.DateConverter;
import no.asf.formidling.client.vo.SearchCriteria;
import no.asf.formidling.client.vo.SecurityCredentials;
import no.asf.formidling.client.vo.ServiceCode;
import no.asf.formidling.client.vo.UploadManifest;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.ws.BindingProvider;
import java.util.Map;
import java.util.Properties;

/**
 * Grensesnitt mot webservice BrokerServiceExternalEC. Brukes ikke direkte, men via BrokerECClient.
 *
 */
public class BrokerServiceEC2Client {

    private ObjectFactory objectFactory;

    private SecurityCredentials credentials;

    private ServiceCode serviceCode;

    private IBrokerServiceExternalEC2 brokerServiceExternalEC2;


    protected BrokerServiceEC2Client(SecurityCredentials credentials, ServiceCode serviceCode) {
        this.credentials = credentials;
        this.serviceCode = serviceCode;
        this.objectFactory = new ObjectFactory();
        this.brokerServiceExternalEC2 = getClient(credentials.getKeyStoreProperties());
    }

    private IBrokerServiceExternalEC2 getClient(Properties signatureProperties) {
        BrokerServiceExternalEC2SF service = new BrokerServiceExternalEC2SF();
        IBrokerServiceExternalEC2 port = service.getCustomBindingIBrokerServiceExternalEC2();
        BindingProvider bindingProvider = (BindingProvider) port;
        bindingProvider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, EC2ClientConfig.BROKER_SERVICE_EXTERNAL_EC2);
        Client client = ClientProxy.getClient(port);
        client.getRequestContext().put("security.signature.properties", signatureProperties);
        client.getRequestContext().put("security.must-understand", true);
        client.getRequestContext().put("org.apache.cxf.message.Message.MAINTAIN_SESSION", true);
        client.getRequestContext().put("javax.xml.ws.session.maintain", true);
        client.getRequestContext().put("security.cache.issued.token.in.endpoint", true);
        client.getRequestContext().put("security.issue.after.failed.renew", true);
        return port;
    }

    protected String initiateService(UploadManifest uploadManifest) throws IBrokerServiceExternalEC2InitiateBrokerServiceECAltinnFaultFaultFaultMessage {
        return brokerServiceExternalEC2.initiateBrokerServiceEC(credentials.getVirksomhetsbruker(), credentials.getVirksomhetsbrukerPassord(), getBrokerServiceInitiation(credentials.getEnhet(), uploadManifest));
    }

    protected BrokerServiceAvailableFileList getReferences(SearchCriteria criteria) throws IBrokerServiceExternalEC2GetAvailableFilesECAltinnFaultFaultFaultMessage, DatatypeConfigurationException {
        return brokerServiceExternalEC2.getAvailableFilesEC(credentials.getVirksomhetsbruker(), credentials.getVirksomhetsbrukerPassord(), getBrokerServiceSearch(credentials.getEnhet(), criteria));
    }

    protected void confirmDownloaded(String fileReference) throws IBrokerServiceExternalEC2ConfirmDownloadedECAltinnFaultFaultFaultMessage {
        brokerServiceExternalEC2.confirmDownloadedEC(credentials.getVirksomhetsbruker(), credentials.getVirksomhetsbrukerPassord(), fileReference, credentials.getEnhet());
    }

    private BrokerServiceInitiation getBrokerServiceInitiation(String enhet, UploadManifest uploadManifest) {
        BrokerServiceInitiation brokerServiceInitiation = objectFactory.createBrokerServiceInitiation();
        brokerServiceInitiation.setManifest(getManifest(enhet, uploadManifest));
        brokerServiceInitiation.setRecipientList(getArrayOfRecipient(uploadManifest));
        return brokerServiceInitiation;
    }

    private Manifest getManifest(String enhet, UploadManifest uploadManifest) {
        Manifest manifest = objectFactory.createManifest();
        manifest.setExternalServiceCode(serviceCode.getExternalServiceCode());
        manifest.setExternalServiceEditionCode(serviceCode.getExternalServiceEditionCode());
        manifest.setReportee(enhet);
        manifest.setSendersReference(uploadManifest.getSendersReference());
        manifest.setFileList(objectFactory.createManifestFileList(getArrayOfFile(uploadManifest)));
        ArrayOfProperty arrayOfProperty = objectFactory.createArrayOfProperty();

        if (uploadManifest.getProperties() != null) {
            for (Map.Entry<String, String> entry : uploadManifest.getProperties().entrySet()) {
                Property property = objectFactory.createProperty();
                property.setPropertyKey(entry.getKey());
                property.setPropertyValue(entry.getValue());
                arrayOfProperty.getProperty().add(property);
            }
            if (arrayOfProperty.getProperty().size() > 0) {
                manifest.setPropertyList(objectFactory.createManifestPropertyList(arrayOfProperty));
            }
        }

        return manifest;
    }

    private ArrayOfFile getArrayOfFile(UploadManifest uploadManifest) {
        ArrayOfFile arrayOfFile = objectFactory.createArrayOfFile();

        for (int i = 0; i < uploadManifest.getFileList().size(); i++) {
            File file = objectFactory.createFile();
            file.setFileName(uploadManifest.getFileList().get(i));
            arrayOfFile.getFile().add(file);
        }
        return arrayOfFile;
    }

    private ArrayOfRecipient getArrayOfRecipient(UploadManifest uploadManifest) {
        ArrayOfRecipient arrayOfRecipient = objectFactory.createArrayOfRecipient();

        for (int i = 0; i < uploadManifest.getRecipientList().size(); i++) {
            Recipient recipient = objectFactory.createRecipient();
            recipient.setPartyNumber(uploadManifest.getRecipientList().get(i));
            arrayOfRecipient.getRecipient().add(recipient);
        }
        return arrayOfRecipient;
    }

    private BrokerServiceSearch getBrokerServiceSearch(String enhet, SearchCriteria criteria) throws DatatypeConfigurationException {
        BrokerServiceSearch brokerServiceSearch = objectFactory.createBrokerServiceSearch();
        brokerServiceSearch.setExternalServiceCode(objectFactory.createBrokerServiceSearchExternalServiceCode(serviceCode.getExternalServiceCode()));
        brokerServiceSearch.setExternalServiceEditionCode(serviceCode.getExternalServiceEditionCode());
        brokerServiceSearch.setReportee(enhet);
        brokerServiceSearch.setFileStatus(criteria.getAvailableFileStatus());
        brokerServiceSearch.setMinSentDateTime(DateConverter.convertToXMLGregorianCalendar(criteria.getMinSentDate()));
        brokerServiceSearch.setMaxSentDateTime(DateConverter.convertToXMLGregorianCalendar(criteria.getMaxSentDate()));
        return brokerServiceSearch;
    }
}
