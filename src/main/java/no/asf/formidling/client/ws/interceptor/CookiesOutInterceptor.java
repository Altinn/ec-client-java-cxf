package no.asf.formidling.client.ws.interceptor;


import no.asf.formidling.client.ws.cookie.CookieStore;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Interceptor for å hente <i>Cookie</i> fra {@link CookieStore} og legge til i header i utgående webservice melding.
 */
public class CookiesOutInterceptor extends AbstractPhaseInterceptor {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    public CookiesOutInterceptor() {
        super(Phase.PRE_PROTOCOL);
    }

    @Override
    public void handleMessage(Message message) throws Fault {
        Map<String, List> headers = (Map<String, List>) message.get(Message.PROTOCOL_HEADERS);
        if (CookieStore.getCookie() != null) {
            log.info("CookiesOUTInterceptor -- cookie to attach to header: " + CookieStore.getCookie());
            headers.put("Cookie", Collections.singletonList(CookieStore.getCookie()));
        }
    }

}
