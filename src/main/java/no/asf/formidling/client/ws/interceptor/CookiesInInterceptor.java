package no.asf.formidling.client.ws.interceptor;

import no.asf.formidling.client.ws.cookie.CookieStore;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.transport.http.Cookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Interceptor for å plukke opp <i>Cookie</i> fra header i innkommende webservice melding.
 * Den vil da finnes som en attributt <i>Set-Cookie</i>. Hvis funnet så lagres den i {@link CookieStore}.
 */
public class CookiesInInterceptor extends AbstractPhaseInterceptor {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    public CookiesInInterceptor() {
        super(Phase.PRE_PROTOCOL);
    }

    @Override
    public void handleMessage(Message message) throws Fault {
        Map<String, List> headers = (Map<String, List>) message.get(Message.PROTOCOL_HEADERS);
        List<Cookie> cookies= headers.get("Set-Cookie");
        if(cookies != null) {
            log.info("CookiesInInterceptor -- cookie to be stored in cookiestore: " + cookies.get(0));
            CookieStore.setCookie(cookies.get(0));
        }
    }

}
