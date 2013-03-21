package de.shop.bestellverwaltung.domain;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import java.util.List;
import java.util.Set;

import javax.persistence.TypedQuery;

import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.util.AbstractDomainTest;

@RunWith(Arquillian.class)

public class BestellungTest extends AbstractDomainTest {



	private static final BestellstatusType STATUS_OFFEN = BestellstatusType.OFFEN;
	private static final BestellstatusType STATUS_STORNIERT = BestellstatusType.STORNIERT;
//	private static final int TAG = 15;
//	private static final int MONAT = Calendar.NOVEMBER;
//	private static final int JAHR = 2012;
//	private static final Date AKTUALISIERT_NEU = new GregorianCalendar(JAHR, MONAT, TAG).getTime();
//	private static final Date ERZEUGT_NEU = new GregorianCalendar(JAHR, MONAT, TAG).getTime();
	private static final String LIEFERVERFOLGUNGSNUMMER = "LVN-Test";


	private static final Long KUNDE_ID_NEU = Long.valueOf(601);	
	
	
	@Test
	public void validate() {
		
		assertThat(true, is(true)); 
				
	}
	
	@Test
	public void findBestellungById() {
		//Given
		Long bestellungId = Long.valueOf(300);
		
		//When
		Bestellung bestellung = getEntityManager().find(Bestellung.class, bestellungId);
		
		//Then
		assertThat(bestellung.getId(), is(bestellungId));
	}

	@Test
	public void findBestellungenByStatus() {
		
		// When
		final TypedQuery<Bestellung> query =
				                        getEntityManager().createNamedQuery(Bestellung.FIND_BESTELLUNGEN_BY_STATUS,
				                                                            Bestellung.class);
		query.setParameter(Bestellung.PARAM_BESTELLUNG_STATUS, STATUS_OFFEN);
		final List<Bestellung> bestellungen = query.getResultList();
		
		// Then
		assertThat(bestellungen.isEmpty(), is(false));
	}
	
	@Test
	public void updateBestellung() {
		//Given
		Long bestellungId = Long.valueOf(300);
				
		//When
		Bestellung bestellung = getEntityManager().find(Bestellung.class, bestellungId);
		
		bestellung.setStatus(STATUS_STORNIERT);
		
		try {
			getEntityManager().merge(bestellung);
		}
		catch (ConstraintViolationException e)	{
			// Es gibt Verletzungen bzgl. Bean Validation: auf der Console ausgeben	
			final Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
			
			for (ConstraintViolation<?> v : violations) {
				System.err.println("!!! FEHLERMELDUNG>>> " + v.getMessage());
				System.err.println("!!! ATTRIBUT>>> " + v.getPropertyPath());
				System.err.println("!!! ATTRIBUTWERT>>> " + v.getInvalidValue());
		}
		
			throw new RuntimeException(e);
		}
		
		
		//Then
		Bestellung bestellungCheck = getEntityManager().find(Bestellung.class, bestellungId);
		assertThat(bestellungCheck.getStatus(), is(STATUS_STORNIERT));
	}
	
	@Test
	public void createBestellung() {
		// Given
		Bestellung bestellung = new Bestellung();
		Kunde k = getEntityManager().find(Kunde.class, KUNDE_ID_NEU);
		bestellung.setKunde(k);
		
		bestellung.setStatus(STATUS_OFFEN);
//		bestellung.setAktualisiert(AKTUALISIERT_NEU);
//		bestellung.setErzeugt(ERZEUGT_NEU);
		bestellung.setLieferverfolgungsnummer(LIEFERVERFOLGUNGSNUMMER);
		
		// When
		try {
			getEntityManager().persist(bestellung);         // abspeichern einschl. Kunde
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
		
		// Then
		
		// Den abgespeicherten Kunden ueber eine Named Query ermitteln
		// Anhand von Erzeugt-Zeitstempel suchen
		final TypedQuery<Bestellung> query =
				                        getEntityManager()
				                        .createNamedQuery(Bestellung.FIND_BESTELLUNGEN_BY_LIEFERVERFOLGUNGSNUMMER,
				                                          Bestellung.class);
		query.setParameter(Bestellung.PARAM_BESTELLUNG_LIEFERVERFOLGUNGSNUMMER, LIEFERVERFOLGUNGSNUMMER);
		final List<Bestellung> bestellungen = query.getResultList();
		
		// Ueberpruefung des ausgelesenen Objekts
		assertThat(bestellungen.size(), is(1));
		bestellung = bestellungen.get(0);
		assertThat(bestellung.getId().longValue() > 0, is(true));
//		assertThat(bestellung.getErzeugt(), is(ERZEUGT_NEU));
		
	}
	
	
		
}
