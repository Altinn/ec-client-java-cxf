package no.asf.formidling.client.ws.interceptor;

import no.asf.formidling.client.ws.cookie.CookieStore;
import org.apache.cxf.binding.soap.SoapFault;
import org.apache.cxf.binding.soap.interceptor.Soap12FaultInInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.ws.security.SecurityConstants;
import org.apache.cxf.ws.security.tokenstore.TokenStoreUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.namespace.QName;
import java.util.List;

/**
 * Interceptor for å håndtere feil med context token.
 */
public class BadContextTokenInFaultInterceptor extends AbstractPhaseInterceptor {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private static final String ERROR_CODE_BAD_CONTEXT_TOKEN = "BadContextToken";

    public BadContextTokenInFaultInterceptor() {
        super(Phase.UNMARSHAL);
        getAfter().add(Soap12FaultInInterceptor.class.getName());
    }

    @Override
    public void handleMessage(Message message) throws Fault {
        Exception exception = message.getContent(Exception.class);
        if (exception instanceof SoapFault) {
            log.error("Server Gods not happy, sent you a soapFault.. Trying to recover..");
            SoapFault soapFault = (SoapFault) exception;
            List<QName> subCodes = soapFault.getSubCodes();
            for (QName subCode : subCodes) {
                log.error("Found subCode: " + subCode.getLocalPart());
                if (subCode.getLocalPart().equalsIgnoreCase(ERROR_CODE_BAD_CONTEXT_TOKEN)) {
                    String tokenId = (String)message.getContextualProperty(SecurityConstants.TOKEN_ID);
                    removeTokenFromMessageAndTokenStore(message, tokenId);
                    CookieStore.setCookie(null);
                    soapFault.setMessage("Token " + tokenId + " is removed from tokenstore, a new one will be requested on your next call. Message from server: " + soapFault.getMessage());
                    message.setContent(Exception.class, soapFault);
                }
            }
        }
    }

    private void removeTokenFromMessageAndTokenStore(Message message, String tokenId) {
        message.getExchange().getEndpoint().remove(SecurityConstants.TOKEN);
        message.getExchange().getEndpoint().remove(SecurityConstants.TOKEN_ID);
        message.getExchange().remove(SecurityConstants.TOKEN_ID);
        message.getExchange().remove(SecurityConstants.TOKEN);
        TokenStoreUtils.getTokenStore(message).remove(tokenId);
        log.error("Removed token " + tokenId + " from message and tokenStore");
    }

}
