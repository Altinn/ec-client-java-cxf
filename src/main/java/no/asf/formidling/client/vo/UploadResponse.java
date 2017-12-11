package no.asf.formidling.client.vo;

import no.altinn.brokerserviceexternalecstreamed.ReceiptExternalStreamedBE;

/**
 * Holder filreferanse fra initialisering av broker
 * og kvittering fra opplasting til formidlingstjenesten.
 *
 */
public class UploadResponse {

    private String fileReference;

    private ReceiptExternalStreamedBE receiptStreamed;

    /**
     * @return filreferanse fra initialisering av serviceBroker
     */
    public String getFileReference() {
        return fileReference;
    }

    public void setFileReference(String fileReference) {
        this.fileReference = fileReference;
    }

    /**
     * @return Kvittering fra opplasting til formidlingstjenesten.
     */
    public ReceiptExternalStreamedBE getReceiptStreamed() {
        return receiptStreamed;
    }

    public void setReceiptStreamed(ReceiptExternalStreamedBE receiptStreamed) {
        this.receiptStreamed = receiptStreamed;
    }
}
