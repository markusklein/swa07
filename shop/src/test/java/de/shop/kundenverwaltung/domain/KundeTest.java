package de.shop.kundenverwaltung.domain;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

import org.jboss.arquillian.junit.Arquillian;
//import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import de.shop.util.AbstractDomainTest;

import javax.persistence.NoResultException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

@RunWith(Arquillian.class)

public class KundeTest extends AbstractDomainTest {
	
	
	private static final String NACHNAME_VORHANDEN = "Krause";
	private static final String NACHNAME_NICHT_VORHANDEN = "Nicht";
	
	private static final String EMAIL_VORHANDEN = "ThorstenEggers@pookmail.com";
	private static final String EMAIL_NICHT_VORHANDEN = "test@hs-ka.de";
	
	//Kunde
	
	private static final String NACHNAME_NEU = "Knauf";
	private static final String VORNAME_NEU = "Theo";
	private static final String EMAIL_NEU = "theo@test.de";
	private static final KundeGeschlechtType GESCHLECHT_NEU = KundeGeschlechtType.M;
	private static final String PASSWORT_NEU = "asdfgasdfhgewr";
	private static final String TELEFONNUMMER_NEU = "07254123456";
	//FÜR GEB. DATUM
	private static final int TAG_GEB = 20;
	private static final int MONAT_GEB = Calendar.APRIL;
	private static final int JAHR_GEB = 1988;
	
	private static final Date GEBURTSDATUM_NEU = new GregorianCalendar(JAHR_GEB, MONAT_GEB, TAG_GEB).getTime();
	private static final Timestamp ERZEUGT_NEU =  new Timestamp(new Date().getTime());
	private static final Timestamp AKTUALISIERT_NEU =  new Timestamp(new Date().getTime());
	
	//Adresse
	
	
	private static final String STRASSE_NEU = "Musterstrasse 6";
	private static final String PLZ_NEU = "76133";
	private static final String ORT_NEU = "Karlsruhe";
	private static final AdresseLandType LAND_NEU = AdresseLandType.DE;
	
	//Zahlungsinformation
	
	
	private static final String KONTOINHABER_NEU = "Theo Knauf";
	private static final Long KONTONUMMER_NEU = Long.valueOf(8612362);
	private static final Long BLZ_NEU = Long.valueOf(6691600);
	private static final String KREDITINSTITUT_NEU = "Volksbank Karlsruhe";
	private static final String SWIFT_NEU = "GENODE61KA1";
	private static final String IBAN_NEU = "DE66916000086123626597";
	
	
	

@Test
public void initialTest() {
	
	assertThat(true, is(true));
}

@Test
public void createKunde() {
	// Given
	Kunde kunde = new Kunde();
	
	kunde.setVorname(VORNAME_NEU);
	kunde.setNachname(NACHNAME_NEU);
	kunde.setEmail(EMAIL_NEU);
	kunde.setGeschlecht(GESCHLECHT_NEU);
	kunde.setPasswort(PASSWORT_NEU);
	kunde.setTelefonnummer(TELEFONNUMMER_NEU);
	kunde.setGeburtsdatum(GEBURTSDATUM_NEU);
	
		//Adresse bauen
	
		final Adresse adresse = new Adresse();
		
		adresse.setStrasse(STRASSE_NEU);
		adresse.setPlz(PLZ_NEU);
		adresse.setOrt(ORT_NEU);
		adresse.setLand(LAND_NEU);
		adresse.setAktualisiert(AKTUALISIERT_NEU);
		adresse.setErzeugt(ERZEUGT_NEU);
		
		//Adresse bauen
		
		final Adresse adresse2 = new Adresse();
		
		adresse2.setStrasse(STRASSE_NEU);
		adresse2.setPlz(PLZ_NEU);
		adresse2.setOrt(ORT_NEU);
		adresse2.setLand(LAND_NEU);
		adresse2.setAktualisiert(AKTUALISIERT_NEU);
		adresse2.setErzeugt(ERZEUGT_NEU);

	
	kunde.setLieferadresse(adresse);
	kunde.setRechnungsadresse(adresse2);
	
		//Zahlungsinformation bauen
		final Zahlungsinformation zahlungsinformation = new Zahlungsinformation();
		
		zahlungsinformation.setBlz(BLZ_NEU);
		zahlungsinformation.setIban(IBAN_NEU);
		zahlungsinformation.setKontoinhaber(KONTOINHABER_NEU);
		zahlungsinformation.setKreditinstitut(KREDITINSTITUT_NEU);
		zahlungsinformation.setKontonummer(KONTONUMMER_NEU);
		zahlungsinformation.setSwift(SWIFT_NEU);
		zahlungsinformation.setAktualisiert(AKTUALISIERT_NEU);
		zahlungsinformation.setErzeugt(ERZEUGT_NEU);

		
	kunde.setZahlungsinformation(zahlungsinformation);


	
	// When
	try {
		getEntityManager().persist(kunde);         // abspeichern einschl. Adresse und Zahlungsinformation
	}
	catch (ConstraintViolationException e) {
		// Es gibt Verletzungen bzgl. Bean Validation: auf der Console ausgeben
		final Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
		for (ConstraintViolation<?> v : violations) {
			System.err.println("!!! FEHLERMELDUNG>>> " + v.getMessage());
			System.err.println("!!! ATTRIBUT>>> " + v.getPropertyPath());
			System.err.println("!!! ATTRIBUTWERT>>> " + v.getInvalidValue());
		}
		
		throw new RuntimeException(e);
	}
	
	 List<Kunde> kunden = getEntityManager().createNamedQuery(Kunde.FIND_KUNDEN_BY_NACHNAME,
                                                                           Kunde.class)
                                                         .setParameter(Kunde.PARAM_NACHNAME,
                                                        		       NACHNAME_NEU)
			                                             .getResultList();
	
	//Ueberpruefung des ausgelesenen Objekts
	assertThat(kunden.size(), is(1));
	assertThat(kunde.getKundeId().longValue() > 0, is(true));
	assertThat(kunde.getNachname(), is(NACHNAME_NEU));
}


@Test
public void findKundeByNachnameVorhanden() {
	
	// Given
	final String nachname = NACHNAME_VORHANDEN;
	
	// When
	final Kunde kunde = getEntityManager().createNamedQuery(Kunde.FIND_KUNDEN_BY_NACHNAME, Kunde.class)
                                                  .setParameter(Kunde.PARAM_NACHNAME, nachname)
			                                      .getSingleResult();
	
	// Then
	assertThat(kunde.getNachname() , is(nachname));
}
@Test
public void findKundeByNachnameNichtVorhanden() {
	
	// Given
	final String nachname = NACHNAME_NICHT_VORHANDEN;
	
	// When
	thrown.expect(NoResultException.class);
	getEntityManager().createNamedQuery(Kunde.FIND_KUNDEN_BY_NACHNAME, Kunde.class)
                                                  .setParameter(Kunde.PARAM_NACHNAME, nachname)
			                                      .getSingleResult();

}

@Test
public void findKundeByEmailVorhanden() {
	
	// Given
	final String email = EMAIL_VORHANDEN;
	
	// When
	final Kunde kunde = getEntityManager().createNamedQuery(Kunde.FIND_KUNDE_BY_EMAIL, Kunde.class)
                                                  .setParameter(Kunde.PARAM_EMAIL, email)
			                                      .getSingleResult();
	
	// Then
	assertThat(kunde.getEmail() , is(email));
}
@Test
public void findKundeByEmailNichtVorhanden() {
	
	// Given
	final String email = EMAIL_NICHT_VORHANDEN;
	
	// When
	thrown.expect(NoResultException.class);
	getEntityManager().createNamedQuery(Kunde.FIND_KUNDE_BY_EMAIL, Kunde.class)
                                                  .setParameter(Kunde.PARAM_EMAIL, email)
			                                      .getSingleResult();

}


}
