package no.asf.formidling.client.ws.client;

import no.altinn.brokerserviceexternalec.*;
import no.altinn.brokerserviceexternalecstreamed.IBrokerServiceExternalEC2StreamedDownloadFileStreamedECAltinnFaultFaultFaultMessage;
import no.altinn.brokerserviceexternalecstreamed.IBrokerServiceExternalEC2StreamedUploadFileStreamedECAltinnFaultFaultFaultMessage;
import no.asf.formidling.client.vo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.datatype.DatatypeConfigurationException;

/**
 * Grensesnitt mot Altinn formidlingstjenesten.
 *
 */
public class BrokerEC2Client {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    BrokerServiceEC2Client brokerEC2Client;

    BrokerServiceEC2StreamedClient brokerEC2StreamedClient;

    private SecurityCredentials credentials;

    private ServiceCode serviceCode;

    /**
     * Constructor.
     *
     * @param credentials Sikkerhetsinformasjon for tilgang til formidlingstjenesten for en virksomhetsbruker.
     * @param serviceCode Kode som angir hvilken formidlingstjeneste som skal brukes.
     */
    public BrokerEC2Client(SecurityCredentials credentials, ServiceCode serviceCode) {
        this.credentials = credentials;
        this.serviceCode = serviceCode;
        this.brokerEC2Client = new BrokerServiceEC2Client(credentials, serviceCode);
        this.brokerEC2StreamedClient = new BrokerServiceEC2StreamedClient(credentials);
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
     * @throws IBrokerServiceExternalEC2InitiateBrokerServiceECAltinnFaultFaultFaultMessage Exception fra BrokerServiceEC2
     * @throws IBrokerServiceExternalEC2StreamedUploadFileStreamedECAltinnFaultFaultFaultMessage Exception fra BrokerServiceEC2Streamed
     */
    public UploadResponse uploadFile(UploadManifest uploadManifest,
                                     byte[] dataHandler) throws IBrokerServiceExternalEC2InitiateBrokerServiceECAltinnFaultFaultFaultMessage, IBrokerServiceExternalEC2StreamedUploadFileStreamedECAltinnFaultFaultFaultMessage {
        UploadResponse response = new UploadResponse();

        log.info("Initializing broker");
        response.setFileReference(brokerEC2Client.initiateService(uploadManifest));
        log.info("Init ok. Reference = " + response.getFileReference());

        log.info("Uploading file: " + uploadManifest.getZipFileName());
        response.setReceiptStreamed(brokerEC2StreamedClient.uploadFile(response.getFileReference(), uploadManifest.getZipFileName(), dataHandler));
        log.info("Upload ok. Receipt = " + response.getReceiptStreamed().getReceiptId());

        return response;
    }

    /**
     * Henter ut tilgjengelige formidlingstjenester som en mottaker kan laste ned.
     * Listen med filer vil bli filtrert basert på rettigheter.
     *
     * @param criteria Søkekriterier for å finne filer som er tilgjengelige fra formidlingstjenesten
     * @return liste av tilgjengelige filer
     * @throws IBrokerServiceExternalEC2GetAvailableFilesECAltinnFaultFaultFaultMessage Exception fra BrokerServiceEC
     * @throws DatatypeConfigurationException Exception ved konvertering av input dato i søkekriterier
     */
    public BrokerServiceAvailableFileList getAvailableFiles(SearchCriteria criteria) throws IBrokerServiceExternalEC2GetAvailableFilesECAltinnFaultFaultFaultMessage, DatatypeConfigurationException {
        log.info("Fetching available files");

        return brokerEC2Client.getReferences(criteria);
    }

    /**
     * Strømmer formidlingstjenestens innhold fra Altinn til mottaker basert på en referanse for formidlingstjenesten
     * som kan hentes ved å benytte GetAvailableFiles.
     *
     * @param fileReference Referanse returnert av GetAvailableFiles.
     * @return Handler til datastrømmen for formidlingstjenestefilen.
     * @throws IBrokerServiceExternalEC2StreamedDownloadFileStreamedECAltinnFaultFaultFaultMessage Exception fra BrokerServiceECStreamed
     */
    public byte[] downloadFile(String fileReference) throws IBrokerServiceExternalEC2StreamedDownloadFileStreamedECAltinnFaultFaultFaultMessage {
        log.info("Downloading fileReference: " + fileReference);

        return brokerEC2StreamedClient.downloadFile(fileReference);
    }


    /**
     * Bekrefter at man har fått lastet ned en formidlingstjeneste fil i sin helhet.
     * Operasjonen kalles etter at det er blitt gjort en nedlasting hvis dette gikk bra.
     * Resultatet er at avsender kan se hvem av filens mottakere som har fått hentet filen.
     *
     * Når alle på mottakerlisten har kalt denne tjenesten blir filen fjernet fra formidlingstjenesten.
     *
     * @param fileReference Angir referansen til filen som skal bekreftes nedlastet.
     * @throws IBrokerServiceExternalEC2ConfirmDownloadedECAltinnFaultFaultFaultMessage Exception fra BrokerServiceEC
     */
    public void confirmDownloaded(String fileReference) throws IBrokerServiceExternalEC2ConfirmDownloadedECAltinnFaultFaultFaultMessage {
        log.info("Confirming download of fileReference: " + fileReference);

        brokerEC2Client.confirmDownloaded(fileReference);
    }

}
