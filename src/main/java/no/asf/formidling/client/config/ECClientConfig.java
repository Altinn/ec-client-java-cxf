package no.asf.formidling.client.config;

import no.asf.formidling.client.ws.interceptor.BadContextTokenInFaultInterceptor;
import no.asf.formidling.client.ws.interceptor.CookiesInInterceptor;
import no.asf.formidling.client.ws.interceptor.CookiesOutInterceptor;
import no.asf.formidling.client.ws.interceptor.HeaderInterceptor;
import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.feature.LoggingFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.Properties;

/**
 * Kan brukes som spring context konfigurasjon i java utgave.
 *
 */
@Configuration
@ComponentScan(basePackages = "no.asf.formidling.client.ws")
public class ECClientConfig {

    @Autowired
    Bus bus;

    public static String BROKER_SERVICE_EXTERNAL_EC;

    public static String BROKER_SERVICE_EXTERNAL_EC_STREAMED;

    public static String RECEIPT_SERVICE_EXTERNAL_EC;

    public static String CORRESPONDENCE_SERVICE_EXTERNAL_EC;

    public static String REPORTEE_ELEMENT_LIST_EC;

    public static String INTERMEDIARY_INBOUND_EXTERNAL_EC;

    /**
     * Leser inn properties filer fra classpath og laster inn i spring context.
     *
     * @return void
     * @throws IOException Feil ved lesing av properties filer
     */
    @Bean
    @Profile("default")
    public static PropertySourcesPlaceholderConfigurer properties() throws IOException{
        PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer = new PropertySourcesPlaceholderConfigurer();
        Properties properties = new Properties();
        properties.loadFromXML(new ClassPathResource("properties/formidling-uri.xml").getInputStream());
        properties.loadFromXML(new ClassPathResource("properties/formidling-tjeneste.xml").getInputStream());
        properties.loadFromXML(new ClassPathResource("properties/logging-properties.xml").getInputStream());
        propertySourcesPlaceholderConfigurer.setProperties(properties);
        return propertySourcesPlaceholderConfigurer;
    }

    /**
     * Initialiserer CFX Bus med nødvendige interceptors og logging.
     *
     * @return Bus
     */


    @Bean
    public Bus springBus() {
        Bus bus = new SpringBus();
        LoggingFeature loggingFeature = new LoggingFeature();
        loggingFeature.setPrettyLogging(true);
        loggingFeature.initialize(bus);
        bus.getFeatures().add(loggingFeature);
        bus.getInInterceptors().add(new CookiesInInterceptor());
        bus.getOutInterceptors().add(new CookiesOutInterceptor());
        bus.getOutInterceptors().add(new HeaderInterceptor());
        bus.getInFaultInterceptors().add(new BadContextTokenInFaultInterceptor());
        return bus;
    }

    @Value("${broker.service.external.ec}")
    public void setBrokerServiceExternalEc(String brokerServiceExternalEc) {
        BROKER_SERVICE_EXTERNAL_EC = brokerServiceExternalEc;
    }

    @Value("${broker.service.external.ec.streamed}")
    public void setBROKER_SERVICE_EXTERNAL_EC_STREAMED(String BROKER_SERVICE_EXTERNAL_EC_STREAMED) {
        this.BROKER_SERVICE_EXTERNAL_EC_STREAMED = BROKER_SERVICE_EXTERNAL_EC_STREAMED;
    }

    @Value("${broker.service.external.ec.receipt}")
    public void setRECEIPT_SERVICE_EXTERNAL_EC(String RECEIPT_SERVICE_EXTERNAL_EC) {
        this.RECEIPT_SERVICE_EXTERNAL_EC = RECEIPT_SERVICE_EXTERNAL_EC;
    }

    @Value("${correspondence.external.ec}")
    public void setCorrespondenceServiceExternalEc(String correspondenceServiceExternalEc) {
        CORRESPONDENCE_SERVICE_EXTERNAL_EC = correspondenceServiceExternalEc;
    }

    @Value("${reportee.elementlist.ec}")
    public void setReporteeElementListEc(String reporteeElementListEc) {
        REPORTEE_ELEMENT_LIST_EC = reporteeElementListEc;
    }

    @Value("${intermediary.inbound.external.ec}")
    public void setIntermediaryInboundExternalEC(String intermediaryInboundExternalEC) {
        INTERMEDIARY_INBOUND_EXTERNAL_EC = intermediaryInboundExternalEC;
    }
}
