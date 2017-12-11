package no.asf.formidling.client.vo;

/**
 * Holder identifikator for formidlingstjenesten.
 *
 * <i>Immutable value object.</i>
 */
public class ServiceCode {

    private String externalServiceCode;

    private int externalServiceEditionCode;

    /**
     * Construnctor.
     *
     * @param serviceCode Den unike kode for en formidlingstjeneste
     * @param serviceEditionCode Versjonsnummer av formidlingstjenesten.
     */
    public ServiceCode(String serviceCode, int serviceEditionCode){
        this.externalServiceCode = serviceCode;
        this.externalServiceEditionCode = serviceEditionCode;
    }

    public String getExternalServiceCode() {
        return externalServiceCode;
    }

    public int getExternalServiceEditionCode() {
        return externalServiceEditionCode;
    }

}
