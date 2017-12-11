package no.asf.formidling.client.ws.interceptor;

import org.apache.cxf.binding.soap.saaj.SAAJOutInterceptor;
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
 * Interceptor for å legge til attributten <i>Connection</i> med verdi <i>Keep-Alive</i>
 * i header på utgående webservice melding.
 */
public class HeaderInterceptor extends AbstractPhaseInterceptor {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    public HeaderInterceptor() {
        super(Phase.PRE_PROTOCOL_ENDING);
        getAfter().add(SAAJOutInterceptor.SAAJOutEndingInterceptor.class.getName());
    }

    @Override
    public void handleMessage(Message message) throws Fault {
        log.info("Adding Keep-Alive header");
        Map<String, List> headers = (Map<String, List>) message.get(Message.PROTOCOL_HEADERS);
        headers.put("Connection", Collections.singletonList("Keep-Alive"));
    }

}
