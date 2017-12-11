package no.asf.formidling.client.vo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Holder data om filer som skal til formidlingstjenesten.
 *
 * <i>Immutable value object.</i>
 */
public class UploadManifest {

    private List<String> recipientList;

    private List<String> fileList;

    private String zipFileName;

    private String sendersReference;

    private Map<String, String> properties;

    /**
     * Constructor.
     *
     * @param sendersReference Referanse som formidlingstjenesten skal knyttes til. Bør være unik. Feltet er påkrevd.
     * @param fileList Liste av objekter av typen File. Ment for å kunne utveksle informasjon om
     *                 innholdet i formidlingstjenesten etter avtale mellom avsender og mottaker.
     *                 Innhold valideres eller endres ikke av Altinn. Feltet er ikke påkrevd.
     * @param recipientList Liste av organisasjonsnummer eller fødselsnummer for mottakere av av formidlingstjenesten.
     * @param zipFileName Filnavn for komprimert "payload" fil.
     * @param properties Liste av objekter av typen Property. Ment for å kunne utveksle ytterlige egenskaper
     *                   om formidlingstjenesten med nøkkel og verdi etter avtale mellom avsender og mottaker.
     *                   Innhold valideres eller endres ikke av Altinn. Feltet er ikke påkrevd.
     */
    public UploadManifest(String sendersReference, List<String> fileList, List<String> recipientList, String zipFileName, Map<String, String> properties) {
        this.sendersReference = sendersReference;
        this.fileList = fileList;
        this.recipientList = recipientList;
        this.zipFileName = zipFileName;
        this.properties = properties;
    }


    public List<String> getRecipientList() {
        return recipientList;
    }

    public List<String> getFileList() {
        return fileList;
    }

    public String getZipFileName() {
        return zipFileName;
    }

    public String getSendersReference() {
        return sendersReference;
    }

    public Map<String, String> getProperties() {
        return properties;
    }
}
