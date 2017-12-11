package no.asf.formidling.client.ws.client;

import no.altinn.brokerserviceexternalecstreamed.*;
import no.asf.formidling.client.config.ECClientConfig;
import no.asf.formidling.client.vo.SecurityCredentials;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.headers.Header;
import org.apache.cxf.jaxb.JAXBDataBinding;

import javax.activation.DataHandler;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.soap.MTOMFeature;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Grensesnitt mot webservice BrokerServiceExternalECStreamed. Brukes ikke direkte, men via BrokerECClient.
 *
 */
public class BrokerServiceECStreamedClient {

    IBrokerServiceExternalECStreamed brokerServiceExternalECStreamed;

    private SecurityCredentials credentials;

    private ObjectFactory objectFactory;


    protected BrokerServiceECStreamedClient(SecurityCredentials credentials) {
        this.credentials = credentials;
        this.objectFactory = new ObjectFactory();
        this.brokerServiceExternalECStreamed = getStreamedClient(credentials.getKeyStoreProperties());
    }

    private IBrokerServiceExternalECStreamed getStreamedClient(Properties signatureProperties) {
        BrokerServiceExternalECStreamedSF service = new BrokerServiceExternalECStreamedSF();
        IBrokerServiceExternalECStreamed port = service.getCustomBindingIBrokerServiceExternalECStreamed(new MTOMFeature());
        BindingProvider bindingProvider = (BindingProvider) port;
        bindingProvider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, ECClientConfig.BROKER_SERVICE_EXTERNAL_EC_STREAMED);
        Client client = ClientProxy.getClient(port);
        client.getRequestContext().put("security.signature.properties", signatureProperties);
        client.getRequestContext().put("security.must-understand", true);
        client.getRequestContext().put("org.apache.cxf.message.Message.MAINTAIN_SESSION", true);
        client.getRequestContext().put("javax.xml.ws.session.maintain", true);
        client.getRequestContext().put("security.cache.issued.token.in.endpoint", true); //default: true
        client.getRequestContext().put("security.issue.after.failed.renew", true); //This must be set to true (default: true)
        return port;
    }

    protected ReceiptExternalStreamedBE uploadFile(String fileReference,
                                                String file,
                                                DataHandler dataHandler) throws IBrokerServiceExternalECStreamedUploadFileStreamedECAltinnFaultFaultFaultMessage {
        List<Header> headerList = new ArrayList<Header>();
        Header userName = null;
        Header reportee = null;
        Header reference = null;
        Header password = null;
        Header filename = null;
        try {
            userName = new Header(new QName("http://www.altinn.no/services/ServiceEngine/Broker/2015/06", "UserName"), credentials.getVirksomhetsbruker(), new JAXBDataBinding(String.class));
            reportee = new Header(new QName("http://www.altinn.no/services/ServiceEngine/Broker/2015/06", "Reportee"), credentials.getEnhet(), new JAXBDataBinding(String.class));
            reference = new Header(new QName("http://www.altinn.no/services/ServiceEngine/Broker/2015/06", "Reference"), fileReference, new JAXBDataBinding(String.class));
            password = new Header(new QName("http://www.altinn.no/services/ServiceEngine/Broker/2015/06", "Password"), credentials.getVirksomhetsbrukerPassord(), new JAXBDataBinding(String.class));
            filename = new Header(new QName("http://www.altinn.no/services/ServiceEngine/Broker/2015/06", "FileName"), file, new JAXBDataBinding(String.class));
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        headerList.add(userName);
        headerList.add(reportee);
        headerList.add(reference);
        headerList.add(password);
        headerList.add(filename);

        ObjectFactory objectFactory = new ObjectFactory();
        ((BindingProvider) brokerServiceExternalECStreamed).getRequestContext().put(Header.HEADER_LIST, headerList);
        StreamedPayloadECBE streamedPayloadECBE = objectFactory.createStreamedPayloadECBE();
        streamedPayloadECBE.setDataStream(dataHandler);
        return brokerServiceExternalECStreamed.uploadFileStreamedEC(streamedPayloadECBE);
    }

    protected DataHandler downloadFile(String reference) throws IBrokerServiceExternalECStreamedDownloadFileStreamedECAltinnFaultFaultFaultMessage {
        return brokerServiceExternalECStreamed.downloadFileStreamedEC(credentials.getVirksomhetsbruker(), credentials.getVirksomhetsbrukerPassord(), reference, credentials.getEnhet());
    }

}
