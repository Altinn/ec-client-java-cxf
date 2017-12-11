package no.asf.formidling.client.util;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Utility for å konvertere dato til format brukt i webservice grensesnittet.
 */
public class DateConverter {

    /**
     * Konverter Date til XMLGregorianCalendar.
     *
     * @param inDate Dato som skal konverteres
     * @return XMLGregorianCalendar
     * @throws DatatypeConfigurationException Exception ved konvertering av dato
     */
    static public XMLGregorianCalendar convertToXMLGregorianCalendar(Date inDate) throws DatatypeConfigurationException {
        if (inDate != null) {
            GregorianCalendar gcal = new GregorianCalendar();
            gcal.setTime(inDate);
            XMLGregorianCalendar xgcal = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
            return xgcal;
        } else {
            return null;
        }
    }
}
