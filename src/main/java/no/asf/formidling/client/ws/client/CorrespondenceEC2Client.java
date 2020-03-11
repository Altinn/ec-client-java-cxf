package no.asf.formidling.client.ws.client;

import no.altinn.correspondenceexternalec.*;
import no.asf.formidling.client.config.EC2ClientConfig;
import no.asf.formidling.client.vo.SecurityCredentials;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.ws.BindingProvider;
import java.util.Properties;

public class CorrespondenceEC2Client {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private ObjectFactory objectFactory;

    private ICorrespondenceExternalEC2 correspondenceExternalEC2;

    private SecurityCredentials credentials;

    /**
     * Construnctor.
     *
     * @param credentials Sikkerhetsinformasjon for tilgang til formidlingstjenesten for en virksomhetsbruker.
     */
    public CorrespondenceEC2Client(SecurityCredentials credentials) {
        this.credentials = credentials;
        this.objectFactory = new ObjectFactory();
        this.correspondenceExternalEC2 = getCorrespondenceClient(credentials.getKeyStoreProperties());
    }

    protected CorrespondenceForEndUserSystemV2 getCorrespondenceForEndUserSystemsEC(int reporteeElementID, int languageID) throws ICorrespondenceExternalEC2GetCorrespondenceForEndUserSystemsECAltinnFaultFaultFaultMessage {

        return correspondenceExternalEC2.getCorrespondenceForEndUserSystemsEC(credentials.getVirksomhetsbruker(), credentials.getVirksomhetsbrukerPassord(), reporteeElementID, languageID);

    }

    private ICorrespondenceExternalEC2 getCorrespondenceClient(Properties signatureProperties) {
        CorrespondenceExternalEC2SF service = new CorrespondenceExternalEC2SF();
        ICorrespondenceExternalEC2 port = service.getCustomBindingICorrespondenceExternalEC2();
        BindingProvider bindingProvider = (BindingProvider) port;
        bindingProvider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, EC2ClientConfig.CORRESPONDENCE_SERVICE_EXTERNAL_EC2);
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
