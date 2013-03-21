package de.shop.kundenverwaltung.service;


import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;


import javax.inject.Inject;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;


import de.shop.kundenverwaltung.domain.Adresse;
import de.shop.kundenverwaltung.domain.AdresseLandType;
import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.kundenverwaltung.domain.KundeGeschlechtType;
import de.shop.kundenverwaltung.domain.Zahlungsinformation;
import de.shop.kundenverwaltung.service.KundeService.FetchType;
import de.shop.util.AbstractTest;

@RunWith(Arquillian.class)
public class KundeServiceTest extends AbstractTest {
	
	private static final Long KUNDE_ID_VORHANDEN = Long.valueOf(600);
	private static final String KUNDE_NACHNAME_VORHANDEN = "Krause";
	private static final String KUNDE_EMAIL_VORHANDEN = "JanaKrause@dodgit.com";
	
	//KUNDE
	private static final String VORNAME_NEU = "Thomas";
	private static final String NACHNAME_NEU = "Gottschalk";
	private static final String EMAIL_NEU = "tgottschalk@gmx.de";
	private static final KundeGeschlechtType GESCHLECHT_NEU = KundeGeschlechtType.M;
	private static final String PASSWORT_NEU = "loasfasf";
	private static final String TELEFONNUMMER_NEU = "07251202291";
	private static final Timestamp ERZEUGT_NEU =  new Timestamp(new Date().getTime());
	private static final Timestamp AKTUALISIERT_NEU =  new Timestamp(new Date().getTime());
	// GEBDATUM
	private static final int TAG_GEB = 19;
	private static final int MONAT_GEB = Calendar.JUNE;
	private static final int JAHR_GEB = 1989;
	private static final Date GEBURTSDATUM_NEU = new GregorianCalendar(JAHR_GEB, MONAT_GEB, TAG_GEB).getTime();
	
	//ADRESSE
	
	private static final String STRASSE_NEU = "Baumweg 100";
	private static final String PLZ_NEU = "76133";
	private static final String ORT_NEU = "Karlsruhe";
	private static final AdresseLandType LAND_NEU = AdresseLandType.DE;
	
	// ZAHLUNGSINFORMATION
	
	private static final String KONTOINHABER_NEU = "Thomas Gottschalk";
	private static final Long KONTONUMMER_NEU = Long.valueOf(5112369);
	private static final Long BLZ_NEU = Long.valueOf(6691600);
	private static final String KREDITINSTITUT_NEU = "Volksbank Karlsruhe";
	private static final String SWIFT_NEU = "GENODSKA1";
	private static final String IBAN_NEU = "DE66916033086123626597";
  
		
	@Inject
	private KundeService ks;
		
	
	@Test
	public void initialTest() {

		assertThat(true, is(true));
	}
		
		
	@Test
	public void createKundeTest() throws RollbackException, HeuristicMixedException, HeuristicRollbackException,
    SystemException, NotSupportedException {
		
		final Collection<Kunde> kundenVorher = ks.findAllKunden(FetchType.NUR_KUNDE, null);
		
		final UserTransaction trans = getUserTransaction();
		
		trans.commit();
		
		Kunde kunde = new Kunde();
		
		kunde.setVorname(VORNAME_NEU);
		kunde.setNachname(NACHNAME_NEU);
		kunde.setEmail(EMAIL_NEU);
		kunde.setGeschlecht(GESCHLECHT_NEU);
		kunde.setPasswort(PASSWORT_NEU);
		kunde.setTelefonnummer(TELEFONNUMMER_NEU);
		kunde.setGeburtsdatum(GEBURTSDATUM_NEU);
		
		// Adressen
		
		final Adresse lieferadresse = new Adresse();
		
		lieferadresse.setStrasse(STRASSE_NEU);
		lieferadresse.setPlz(PLZ_NEU);
		lieferadresse.setOrt(ORT_NEU);
		lieferadresse.setLand(LAND_NEU);
		lieferadresse.setAktualisiert(AKTUALISIERT_NEU);
		lieferadresse.setErzeugt(ERZEUGT_NEU);
		
		final Adresse rechnungsadresse = new Adresse();
		
		rechnungsadresse.setStrasse(STRASSE_NEU);
		rechnungsadresse.setPlz(PLZ_NEU);
		rechnungsadresse.setOrt(ORT_NEU);
		rechnungsadresse.setLand(LAND_NEU);
		rechnungsadresse.setAktualisiert(AKTUALISIERT_NEU);
		rechnungsadresse.setErzeugt(ERZEUGT_NEU);
		
		kunde.setLieferadresse(lieferadresse);
		kunde.setRechnungsadresse(rechnungsadresse);
		
		// Zahlungsinformation
		
		final Zahlungsinformation zahlungsinformation = new Zahlungsinformation();
		
		zahlungsinformation.setBlz(BLZ_NEU);
		zahlungsinformation.setIban(IBAN_NEU);
		zahlungsinformation.setKontoinhaber(KONTOINHABER_NEU);
		zahlungsinformation.setKreditinstitut(KREDITINSTITUT_NEU);
		zahlungsinformation.setKontonummer(KONTONUMMER_NEU);
		zahlungsinformation.setSwift(SWIFT_NEU);
		zahlungsinformation.setAktualisiert(AKTUALISIERT_NEU);
		zahlungsinformation.setErzeugt(ERZEUGT_NEU);

		final Date datumVorher = new Date();	
		
		kunde.setZahlungsinformation(zahlungsinformation);
		
		trans.begin();
		kunde = ks.createKunde(kunde, zahlungsinformation, lieferadresse, rechnungsadresse, LOCALE);
		trans.commit();
		
		//THEN
		assertThat(datumVorher.getTime() <= kunde.getErzeugt().getTime(), is(true));
		
					
		for (Kunde k : kundenVorher) {
			assertTrue(k.getKundeId() < kunde.getKundeId());
			assertTrue(k.getErzeugt().getTime() < kunde.getErzeugt().getTime());
		}
			
		trans.begin();	
		kunde = ks.findKundeById(kunde.getKundeId(), FetchType.NUR_KUNDE, LOCALE);
		trans.commit();
		
		assertThat(kunde.getNachname(), is(NACHNAME_NEU));
		assertThat(kunde.getEmail(), is(EMAIL_NEU));
	}
	
	@Test
	public void updateKundeTest() throws RollbackException, HeuristicMixedException, HeuristicRollbackException,
        SystemException, NotSupportedException {
		

		// Given
		final Long kundeId = KUNDE_ID_VORHANDEN;

		// When
		Kunde kunde = ks.findKundeById(kundeId, FetchType.NUR_KUNDE, LOCALE);
		final UserTransaction trans = getUserTransaction();
		trans.commit();
		
		final String alterNachname = kunde.getNachname();
		final String neuerNachname = alterNachname + alterNachname.charAt(alterNachname.length() - 1);
		kunde.setNachname(neuerNachname);
	
		trans.begin();
		kunde = ks.updateKunde(kunde, LOCALE);
		trans.commit();
		
		// Then
		assertThat(kunde.getNachname(), is(neuerNachname));
		trans.begin();
		kunde = ks.findKundeById(kundeId, FetchType.NUR_KUNDE, LOCALE);
		trans.commit();
		assertThat(kunde.getNachname(), is(neuerNachname));
	}

	
	@Test
	public void findKundeByNachname() throws RollbackException, HeuristicMixedException, 
		HeuristicRollbackException, SystemException {

		// Wenn
		final Collection<Kunde> kunden = ks.findKundenByNachname(
				KUNDE_NACHNAME_VORHANDEN, FetchType.MIT_ADRESSE_UND_ZAHLUNGSINFORMATION, LOCALE);
		
		// ThenKUNDE_ID_VORHANDEN
		assertThat(kunden, is(notNullValue()));
		assertThat(kunden.isEmpty(), is(false));

		for (Kunde k : kunden) {
			assertThat(k.getNachname(), is(KUNDE_NACHNAME_VORHANDEN));

		}
		
	}

	@Test
	public void findKundeByEmail() throws RollbackException, HeuristicMixedException, 
		HeuristicRollbackException, SystemException {

		// Wenn
		final Kunde kunde = ks.findKundeByEmail(KUNDE_EMAIL_VORHANDEN, FetchType.MIT_ZAHLUNGSINFORMATION, LOCALE);
		
		// Then KUNDE_ID_VORHANDEN
		assertThat(kunde, is(notNullValue()));
		assertThat(kunde.getEmail(), is(KUNDE_EMAIL_VORHANDEN));
		
	}
		

}
