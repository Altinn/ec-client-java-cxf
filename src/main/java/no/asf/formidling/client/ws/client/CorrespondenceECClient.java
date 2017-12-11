package no.asf.formidling.client.ws.client;

import no.altinn.correspondenceexternalec.*;
import no.asf.formidling.client.config.ECClientConfig;
import no.asf.formidling.client.vo.SecurityCredentials;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.ws.BindingProvider;
import java.util.Properties;

public class CorrespondenceECClient {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private ObjectFactory objectFactory;

    private ICorrespondenceExternalEC correspondenceExternalEC;

    private SecurityCredentials credentials;

    /**
     * Construnctor.
     *
     * @param credentials Sikkerhetsinformasjon for tilgang til formidlingstjenesten for en virksomhetsbruker.
     */
    public CorrespondenceECClient(SecurityCredentials credentials) {
        this.credentials = credentials;
        this.objectFactory = new ObjectFactory();
        this.correspondenceExternalEC = getCorrespondenceClient(credentials.getKeyStoreProperties());
    }

    protected CorrespondenceForEndUserSystemV2 getCorrespondenceForEndUserSystemsEC(int reporteeElementID, int languageID) throws ICorrespondenceExternalECGetCorrespondenceForEndUserSystemsECAltinnFaultFaultFaultMessage {

        return correspondenceExternalEC.getCorrespondenceForEndUserSystemsEC(credentials.getVirksomhetsbruker(), credentials.getVirksomhetsbrukerPassord(), reporteeElementID, languageID);

    }

    private ICorrespondenceExternalEC getCorrespondenceClient(Properties signatureProperties) {
        CorrespondenceExternalECSF service = new CorrespondenceExternalECSF();
        ICorrespondenceExternalEC port = service.getCustomBindingICorrespondenceExternalEC();
        BindingProvider bindingProvider = (BindingProvider) port;
        bindingProvider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, ECClientConfig.CORRESPONDENCE_SERVICE_EXTERNAL_EC);
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
