package no.asf.formidling.client.ws.client;

import no.altinn.brokerserviceexternalec.*;
import no.altinn.brokerserviceexternalecstreamed.IBrokerServiceExternalECStreamedDownloadFileStreamedECAltinnFaultFaultFaultMessage;
import no.altinn.brokerserviceexternalecstreamed.IBrokerServiceExternalECStreamedUploadFileStreamedECAltinnFaultFaultFaultMessage;
import no.asf.formidling.client.vo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.DataHandler;
import javax.xml.datatype.DatatypeConfigurationException;

/**
 * Grensesnitt mot Altinn formidlingstjenesten.
 *
 */
public class BrokerECClient {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    BrokerServiceECClient brokerECClient;

    BrokerServiceECStreamedClient brokerECStreamedClient;

    private SecurityCredentials credentials;

    private ServiceCode serviceCode;

    /**
     * Constructor.
     *
     * @param credentials Sikkerhetsinformasjon for tilgang til formidlingstjenesten for en virksomhetsbruker.
     * @param serviceCode Kode som angir hvilken formidlingstjeneste som skal brukes.
     */
    public BrokerECClient(SecurityCredentials credentials, ServiceCode serviceCode) {
        this.credentials = credentials;
        this.serviceCode = serviceCode;
        this.brokerECClient = new BrokerServiceECClient(credentials, serviceCode);
        this.brokerECStreamedClient = new BrokerServiceECStreamedClient(credentials);
    }


    /**
     * Tjeneste for avgiver til å laste opp til Altinn formidlingstjenesten.
     * Utføres i 2 steg:
     * 1. InitiateService. Starter prosessen for å laste opp en ny formidlingstjeneste.
     *    Laster opp metadata om tjenesten og hvem som skal motta. Når Altinn har verifisert
     *    dette vil mottaker få en referanse som videre benyttes for å laste opp selve payloaden.
     * 2. UploadFile. Strømmer formidlingstjenestens innhold til Altinn basert på referansen
     *    mottatt gjennom kallet til steg 1.
     *
     * @param uploadManifest Data om filer som skal til formidlingstjenesten.
     * @param dataHandler Datastrømmen for fil som lastes opp.
     * @return Referanse og kvittering fra formidlingstjenesten.
     * @throws IBrokerServiceExternalECInitiateBrokerServiceECAltinnFaultFaultFaultMessage Exception fra BrokerServiceEC
     * @throws IBrokerServiceExternalECStreamedUploadFileStreamedECAltinnFaultFaultFaultMessage Exception fra BrokerServiceECStreamed
     */
    public UploadResponse uploadFile(UploadManifest uploadManifest,
                                     DataHandler dataHandler) throws IBrokerServiceExternalECInitiateBrokerServiceECAltinnFaultFaultFaultMessage, IBrokerServiceExternalECStreamedUploadFileStreamedECAltinnFaultFaultFaultMessage {
        UploadResponse response = new UploadResponse();

        log.info("Initializing broker");
        response.setFileReference(brokerECClient.initiateService(uploadManifest));
        log.info("Init ok. Reference = " + response.getFileReference());

        log.info("Uploading file: " + uploadManifest.getZipFileName());
        response.setReceiptStreamed(brokerECStreamedClient.uploadFile(response.getFileReference(), uploadManifest.getZipFileName(), dataHandler));
        log.info("Upload ok. Receipt = " + response.getReceiptStreamed().getReceiptId());

        return response;
    }

    /**
     * Henter ut tilgjengelige formidlingstjenester som en mottaker kan laste ned.
     * Listen med filer vil bli filtrert basert på rettigheter.
     *
     * @param criteria Søkekriterier for å finne filer som er tilgjengelige fra formidlingstjenesten
     * @return liste av tilgjengelige filer
     * @throws IBrokerServiceExternalECGetAvailableFilesECAltinnFaultFaultFaultMessage Exception fra BrokerServiceEC
     * @throws DatatypeConfigurationException Exception ved konvertering av input dato i søkekriterier
     */
    public BrokerServiceAvailableFileList getAvailableFiles(SearchCriteria criteria) throws IBrokerServiceExternalECGetAvailableFilesECAltinnFaultFaultFaultMessage, DatatypeConfigurationException {
        log.info("Fetching available files");

        return brokerECClient.getReferences(criteria);
    }

    /**
     * Strømmer formidlingstjenestens innhold fra Altinn til mottaker basert på en referanse for formidlingstjenesten
     * som kan hentes ved å benytte GetAvailableFiles.
     *
     * @param fileReference Referanse returnert av GetAvailableFiles.
     * @return Handler til datastrømmen for formidlingstjenestefilen.
     * @throws IBrokerServiceExternalECStreamedDownloadFileStreamedECAltinnFaultFaultFaultMessage Exception fra BrokerServiceECStreamed
     */
    public DataHandler downloadFile(String fileReference) throws IBrokerServiceExternalECStreamedDownloadFileStreamedECAltinnFaultFaultFaultMessage {
        log.info("Downloading fileReference: " + fileReference);

        return brokerECStreamedClient.downloadFile(fileReference);
    }


    /**
     * Bekrefter at man har fått lastet ned en formidlingstjeneste fil i sin helhet.
     * Operasjonen kalles etter at det er blitt gjort en nedlasting hvis dette gikk bra.
     * Resultatet er at avsender kan se hvem av filens mottakere som har fått hentet filen.
     *
     * Når alle på mottakerlisten har kalt denne tjenesten blir filen fjernet fra formidlingstjenesten.
     *
     * @param fileReference Angir referansen til filen som skal bekreftes nedlastet.
     * @throws IBrokerServiceExternalECConfirmDownloadedECAltinnFaultFaultFaultMessage Exception fra BrokerServiceEC
     */
    public void confirmDownloaded(String fileReference) throws IBrokerServiceExternalECConfirmDownloadedECAltinnFaultFaultFaultMessage {
        log.info("Confirming download of fileReference: " + fileReference);

        brokerECClient.confirmDownloaded(fileReference);
    }

}
