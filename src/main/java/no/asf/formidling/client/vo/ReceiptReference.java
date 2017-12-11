package no.asf.formidling.client.vo;

import no.altinn.receiptexternalec.ReferenceType;

/**
 * Referanse brukt i Altinn kvittering. Kan brukes ved oppdatering av kvittering.
 *
 * <i>Immutable value object.</i>
 */
public class ReceiptReference {

    private ReferenceType referenceType;

    private String referenceValue;

    /**
     * Constructor.
     *
     * @param referenceType ReceiversReference, mottakers referanse hvis kvitteringen blir oppdatert av mottaker av en forsendelse
     * @param referenceValue Referansens verdi
     */
    public ReceiptReference(ReferenceType referenceType, String referenceValue){
        this.referenceType = referenceType;
        this.referenceValue = referenceValue;
    }

    public ReferenceType getReferenceType() {
        return referenceType;
    }

    public String getReferenceValue() {
        return referenceValue;
    }
}
