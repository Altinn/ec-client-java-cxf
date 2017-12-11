package no.asf.formidling.client.vo;

import no.altinn.receiptexternalec.ReceiptStatusEnum;

import java.util.List;

/**
 * Brukes ved oppdatering av underkvitteringer (subreceipt).
 *
 * <i>Immutable value object.</i>
 */
public class SubReceipt {

    private String archiveReference;

    private Integer receiptId;

    private ReceiptStatusEnum receiptStatus;

    private String receiptText;

    private List<ReceiptReference> references;

    private List<SubReceipt> subReceipts;

    /**
     * Constructor.
     *
     * @param archiveReference Hvis det finnes flere kvitteringer med samme arkivreferanse så vil den nyeste bli valgt
     * @param receiptId Unik identifikator for kvittering
     * @param receiptStatus Status for forsendelse som kvitteringen gjelder: OK, UnExpectedError, ValidationFailed, Rejected
     * @param receiptText Oppdateringstekst for kvittering
     * @param references Liste med referanser man ønsker legge til, bør begrenses til ReceiversReference
     * @param subReceipts Liste med barnekvitteringer som også ønskes oppdatert i tillegg til hovedkvitteringen.
     *                    Barnekvitteringer MÅ identifiseres med ReceiptId. Dette kan ikke benyttes til å lage nye barnekvitteringer.
     */
    public SubReceipt(String archiveReference, Integer receiptId, ReceiptStatusEnum receiptStatus, String receiptText, List<ReceiptReference> references, List<SubReceipt> subReceipts){
        this.archiveReference = archiveReference;
        this.receiptId = receiptId;
        this.receiptStatus = receiptStatus;
        this.receiptText = receiptText;
        this.references = references;
        this.subReceipts = subReceipts;
    }

    public String getArchiveReference() {
        return archiveReference;
    }

    public Integer getReceiptId() {
        return receiptId;
    }

    public ReceiptStatusEnum getReceiptStatus() {
        return receiptStatus;
    }

    public String getReceiptText() {
        return receiptText;
    }

    public List<ReceiptReference> getReferences() {
        return references;
    }

    public List<SubReceipt> getSubReceipts() {
        return subReceipts;
    }
}
