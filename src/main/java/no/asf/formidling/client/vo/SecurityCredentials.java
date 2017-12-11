package no.asf.formidling.client.vo;

import org.springframework.stereotype.Component;

import java.util.Properties;

/**
 * Holder sikkerhetsinformasjon for tilgang til formidlingstjenesten
 * for en virksomhetsbruker.
 *
 * <i>Immutable value object.</i>
 */
@Component
public class SecurityCredentials {

    private Properties keyStoreProperties;

    private String virksomhetsbruker;

    private String virksomhetsbrukerPassord;

    private String enhet;

    /**
     * Constructor.
     *
     * @param virksomhetsbruker Brukernavn for virksomhetsbruker
     * @param virksomhetsbrukerPassord Passord for virksomhetsbruker
     * @param enhet Virksomhetens organisasjonsnummer
     * @param securityFile Virksomhetens sertifikat (formast PKCS12)
     * @param securityPassword Passord tilhørende sertifikat
     * @param securityAlias Alias tilhørende sertifikat (aka. friendly name)
     */
    public SecurityCredentials(String virksomhetsbruker,
                               String virksomhetsbrukerPassord,
                               String enhet,
                               String securityFile,
                               String securityPassword,
                               String securityAlias){
        this.virksomhetsbruker = virksomhetsbruker;
        this.virksomhetsbrukerPassord = virksomhetsbrukerPassord;
        this.enhet = enhet;
        keyStoreProperties = new Properties();
        keyStoreProperties.setProperty("org.apache.ws.security.crypto.provider","org.apache.ws.security.components.crypto.Merlin");
        keyStoreProperties.setProperty("org.apache.ws.security.crypto.merlin.keystore.file", securityFile);
        keyStoreProperties.setProperty("org.apache.ws.security.crypto.merlin.keystore.password", securityPassword);
        keyStoreProperties.setProperty("org.apache.ws.security.crypto.merlin.keystore.type","pkcs12");
        keyStoreProperties.setProperty("org.apache.ws.security.crypto.merlin.keystore.private.password", securityPassword);
        keyStoreProperties.setProperty("org.apache.ws.security.crypto.merlin.keystore.alias", securityAlias);
    }

    public Properties getKeyStoreProperties(){
        return keyStoreProperties;
    }

    public String getVirksomhetsbruker() {
        return virksomhetsbruker;
    }

    public String getVirksomhetsbrukerPassord() {
        return virksomhetsbrukerPassord;
    }

    public String getEnhet() {
        return enhet;
    }
}
