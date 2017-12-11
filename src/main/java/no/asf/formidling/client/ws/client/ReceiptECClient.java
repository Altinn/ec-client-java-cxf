package no.asf.formidling.client.ws.client;

import no.altinn.receiptexternalec.*;
import no.asf.formidling.client.config.ECClientConfig;
import no.asf.formidling.client.util.DateConverter;
import no.asf.formidling.client.vo.ReceiptReference;
import no.asf.formidling.client.vo.SecurityCredentials;
import no.asf.formidling.client.vo.SubReceipt;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.ws.BindingProvider;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * Grensesnitt mot Altinn webservice for kvitteringer.
 */
public class ReceiptECClient {

    private ObjectFactory objectFactory;

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private IReceiptExternalEC receiptExternalEC;

    private SecurityCredentials credentials;

    private IReceiptExternalEC getReceiptClient(Properties signatureProperties) {
        ReceiptExternalEC service = new ReceiptExternalEC();
        IReceiptExternalEC port = service.getCustomBindingIReceiptExternalEC();
        BindingProvider bindingProvider = (BindingProvider) port;
        bindingProvider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, ECClientConfig.RECEIPT_SERVICE_EXTERNAL_EC);
        Client client = ClientProxy.getClient(port);
        client.getRequestContext().put("security.must-understand", true);
        client.getRequestContext().put("org.apache.cxf.message.Message.MAINTAIN_SESSION", true);
        client.getRequestContext().put("javax.xml.ws.session.maintain", true);
        client.getRequestContext().put("security.cache.issued.token.in.endpoint", true); //default: true
        client.getRequestContext().put("security.issue.after.failed.renew", true); //This must be set to true (default: true)
        client.getRequestContext().put("security.signature.properties", signatureProperties);
        return port;
    }

    /**
     * Construnctor.
     *
     * @param credentials Sikkerhetsinformasjon for tilgang til formidlingstjenesten for en virksomhetsbruker.
     */
    public ReceiptECClient(SecurityCredentials credentials) {
        this.credentials = credentials;
        this.objectFactory = new ObjectFactory();
        this.receiptExternalEC = getReceiptClient(credentials.getKeyStoreProperties());
    }

    /**
     * Finn kvittering fra formidlingstjenesten med angitt id.
     *
     * @param receiptId Unik identifikator kvitteringen i Altinn.
     * @param references Liste med referansen som skal brukes i søket.
     *                   I praksis er det kun en referanse som benyttes i søket. Følgende referansetyper kan benyttes i søk
     *                   - ArchiveReference
     *                   - OutboundShipmentReference
     *                   - BatchReference
     *                   - EndUserSystemReference
     *                   - ExternalShipmentReference
     *                   - SendersReference
     * @return Receipt
     * @throws IReceiptExternalECGetReceiptECV2AltinnFaultFaultFaultMessage Exception fra ReceiptExternalEC
     */
    public Receipt getReceipt(Integer receiptId, List<ReceiptReference> references) throws IReceiptExternalECGetReceiptECV2AltinnFaultFaultFaultMessage {
        log.info("Getting receipt " + receiptId);

        ReceiptSearch search = getReceiptSearch(receiptId, references);
        return receiptExternalEC.getReceiptECV2(credentials.getVirksomhetsbruker(), credentials.getVirksomhetsbrukerPassord(), search);
    }

    /**
     * Finn kvitteringer fra formidlingstjenesten innenfor gitt tidsrom.
     *
     * @param dateFrom Fra dato i søket
     * @param dateTo Til dato i søket
     * @return ReceiptList
     * @throws DatatypeConfigurationException Exception ved konvertering av input dato
     * @throws IReceiptExternalECGetReceiptListECV2AltinnFaultFaultFaultMessage Exception fra ReceiptExternalEC
     */
    public ReceiptList getReceiptList(Date dateFrom, Date dateTo) throws DatatypeConfigurationException, IReceiptExternalECGetReceiptListECV2AltinnFaultFaultFaultMessage {
        log.info("Getting receipt list between " + dateFrom + " to " + dateTo);

        return receiptExternalEC.getReceiptListECV2(credentials.getVirksomhetsbruker(), credentials.getVirksomhetsbrukerPassord(),
                ReceiptType.BROKER_SERVICE, DateConverter.convertToXMLGregorianCalendar(dateFrom), DateConverter.convertToXMLGregorianCalendar(dateTo));
    }

    /**
     * Oppdater kvittering for formidlingstjenesten med en melding.
     * Kan benyttes av sluttbrukersystemer for å oppdatere en kvittering ved mottak av data.
     *
     * @param receiptId Unik identifikator for kvittering i Altinn. Kan benyttes for å spesifisere hvilken kvittering som skal oppdateres.
     * @param receiptStatus Status for forsendelse som kvitteringen gjelder. Mulige verdier er:
     *               - OK
     *               - UnExpectedError
     *               - ValidationFailed
     *               - Rejected
     *               Påkrevd felt.
     * @param arkivReferanse Arkivreferanse i Altinn. Kan benyttes for å spesifisere hvilken kvittering som skal oppdateres.
     *                       Hvis det finnes flere kvitteringer som har samme arkivreferanse, så vil den nyeste
     *                       (den med høyest ReceiptId) bli valgt.
     * @param receiptText Oppdateringstekst for kvittering. Påkrevd felt.
     * @param references Liste med referanser man eventuelt ønsker å legge til på kvitteringen.
     *                   Alle referansetyper utenom NotSet og OwnerPartyReference er gyldige,
     *                   men det bør begrenses til følgende (Unntak kan gjøres etter avtale med forvaltning.):
     *                   - ReceiversReference Mottakers referanse hvis kvitteringen blir oppdatert av mottaker av en forsendelse.
     * @param subReceipts Liste med barnekvitteringer som også ønskes oppdatert i tillegg til hovedkvitteringen.
     *                    Barnekvitteringer MÅ identifiseres med ReceiptId. Dette kan ikke benyttes til å lage nye barnekvitteringer.
     * @return Kvitteringen slik den frermstår etter oppdateringen.
     * @throws IReceiptExternalECUpdateReceiptECAltinnFaultFaultFaultMessage Exception fra ReceiptExternalEC
     */
    public Receipt updateReceipt(Integer receiptId, ReceiptStatusEnum receiptStatus, String arkivReferanse,
                                 List<ReceiptReference> references, String receiptText, List<SubReceipt> subReceipts) throws IReceiptExternalECUpdateReceiptECAltinnFaultFaultFaultMessage {
        log.info("Updating receipt");

        ReceiptSave receiptSave = createReceiptSave(receiptId, receiptStatus, arkivReferanse, references, receiptText, subReceipts);
        return receiptExternalEC.updateReceiptEC(credentials.getVirksomhetsbruker(), credentials.getVirksomhetsbrukerPassord(), receiptSave);
    }

    /**
     * Oppretter en ReceiptSave for oppdatering av kvittering. Tar i mot en liste av subreceipt hvor hver subreceipt igjen kan inneholde liste av subreceipt.
     * Funksjonen vil i så fall kalle seg selv rekursivt.
     *
     */
    private ReceiptSave createReceiptSave(Integer receiptId, ReceiptStatusEnum status, String arkivReferanse,
                                          List<ReceiptReference> references, String receiptText, List<SubReceipt> subReceipts){
        ReceiptSave receiptSave = objectFactory.createReceiptSave();
        receiptSave.setReceiptId(receiptId);
        if (arkivReferanse != null) {
            receiptSave.setArchiveReference(objectFactory.createReceiptSaveExternalArchiveReference(arkivReferanse));
        }
        receiptSave.setReceiptStatus(status);
        receiptSave.setReceiptText(receiptText);
        if (references != null) {
            ReferenceList2 referenceList2 = objectFactory.createReferenceList2();
            for (ReceiptReference ref : references){
                Reference2 ref2 = objectFactory.createReference2();
                ref2.setReferenceType(ref.getReferenceType());
                ref2.setReferenceValue(ref.getReferenceValue());
                referenceList2.getReference().add(ref2);
            }
            receiptSave.setReferences(objectFactory.createReceiptSaveReferences(referenceList2));
        }
        if (subReceipts != null) {
            ReceiptSaveList saveList = objectFactory.createReceiptSaveList();
            for (SubReceipt subReceipt : subReceipts){
                ReceiptSave subReceiptSave = createReceiptSave(subReceipt.getReceiptId(), subReceipt.getReceiptStatus(),
                        subReceipt.getArchiveReference(), subReceipt.getReferences(), subReceipt.getReceiptText(), subReceipt.getSubReceipts());
                saveList.getReceiptSave().add(subReceiptSave);
            }
            receiptSave.setSubReceipts(objectFactory.createReceiptSaveList(saveList));
        }
        return receiptSave;
    }


    /**
     * Oppretter en ReceiptSearch.
     *
     * @param receiptId
     * @param references
     * @return
     */
    private ReceiptSearch getReceiptSearch(Integer receiptId, List<ReceiptReference> references) {
        ReceiptSearch search = objectFactory.createReceiptSearch();
        search.setReceiptId(receiptId);
        if (references != null) {
            ReferenceList2 referenceList2 = objectFactory.createReferenceList2();
            for (ReceiptReference ref : references){
                Reference2 ref2 = objectFactory.createReference2();
                ref2.setReferenceType(ref.getReferenceType());
                ref2.setReferenceValue(ref.getReferenceValue());
                referenceList2.getReference().add(ref2);
            }
            search.setReferences(objectFactory.createReceiptSaveReferences(referenceList2));
        }
        return search;
    }
}
