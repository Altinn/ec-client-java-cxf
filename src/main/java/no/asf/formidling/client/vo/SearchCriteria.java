package no.asf.formidling.client.vo;

import no.altinn.brokerserviceexternalec.BrokerServiceAvailableFileStatus;

import java.util.Date;

/**
 * Holder søkekriterier for å finne tilgjengelige formidlinger for en mottaker.
 *
 * <i>Immutable value object.</i>
 */
public class SearchCriteria {

    private BrokerServiceAvailableFileStatus availableFileStatus;

    private Date minSentDate;

    private Date maxSentDate;

    /**
     * Constructor.
     *
     * @param availableFileStatus Status for formidlingstjenesten. Mulige verdier er:
     *                            - Uploaded, betyr at formidlingstjenesten er lastet opp av avsender og tilgjengelig for nedlasting.
     *                            - Downloaded, betyr at formidlingstjenesten allerede er lastet ned fra Altinn, men fremdeles tilgjengelig for nedlasting.
     * @param minSentDate Angir dato for å begrense resultatsett av formidlingstjenester sent inn etter angitt dato
     * @param maxSentDate Angir dato for å begrense resultatsett av formidlingstjenester sent før angitt dato
     */
    public SearchCriteria(BrokerServiceAvailableFileStatus availableFileStatus, Date minSentDate, Date maxSentDate){
        this.availableFileStatus = availableFileStatus;
        this.minSentDate = minSentDate;
        this.maxSentDate = maxSentDate;
    }

    public BrokerServiceAvailableFileStatus getAvailableFileStatus() {
        return availableFileStatus;
    }

    public Date getMinSentDate() {
        return minSentDate;
    }

    public Date getMaxSentDate() {
        return maxSentDate;
    }
}
