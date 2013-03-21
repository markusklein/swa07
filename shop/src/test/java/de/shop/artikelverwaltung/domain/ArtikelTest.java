package de.shop.artikelverwaltung.domain;


import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

import javax.persistence.TypedQuery;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import de.shop.util.AbstractDomainTest;

@RunWith(Arquillian.class)
public class ArtikelTest extends AbstractDomainTest {
	
	private static final String VORHANDEN_BESCHREIBUNG = "Damenkleid";
	private static final String VORHANDEN_NAME = "Mantel Grau Groesse M";
	private static final String NV_NAME = "Hemd";
	private static final String NV_BESCHREIBUNG = "herren Hemd";
	private static final float VORHANDEN_PREIS = 100;
	private static final int TAG = 01;
	private static final int MONAT = Calendar.AUGUST;
	private static final int JAHR = 2006;
	private static final Date AKTUALISIERT_NEU = new GregorianCalendar(JAHR, MONAT, TAG).getTime();
	private static final Date ERZEUGT_NEU = new GregorianCalendar(JAHR, MONAT, TAG).getTime();
	private static final long a = 200;

	@Test
	public void validate() {
		assertThat(true, is(true));
	}

	
	@Test
	public void findArtikelById() {
		//Given
		Long artikel_id = a;
		
		//When
		Artikel artikel = getEntityManager().find(Artikel.class, artikel_id);
		
		//Then
		assertThat(artikel.getArtikelId(), is(artikel_id));
	}
	
	@Test 
	public void findArtikelByBeschreibung() {
		//Given
		final String para = VORHANDEN_BESCHREIBUNG;
		
		//When
		
		final TypedQuery<Artikel>query = getEntityManager()
				.createNamedQuery(Artikel.ARTIKEL_BY_BESCHREIBUNG, Artikel.class);
		
								query.setParameter(Artikel.PARAMETER_BESCHREIBUNG, para);
		final List<Artikel>artikel = query.getResultList();
		//Then
		assertThat(artikel.isEmpty(), is(false));
	}
	
	@Test 
	public void findArtikelByName() {
		//Given
		final String para = VORHANDEN_NAME;
		
		//Wh
		
		final TypedQuery<Artikel>query = getEntityManager()
				.createNamedQuery(Artikel.ARTIKEL_BY_NAME, Artikel.class);
		
								query.setParameter(Artikel.PARAMETER_NAME, para);
		final List<Artikel>artikel = query.getResultList();
		//Then
		assertThat(artikel.isEmpty(), is(false));
	}
	
	@Test 
	public void findArtikelByPreis() {
		//Given
		final float para = VORHANDEN_PREIS;
		
		//Wh
		
		final TypedQuery<Artikel>query = getEntityManager()
				.createNamedQuery(Artikel.ARTIKEL_BY_PREIS, Artikel.class);
		
								query.setParameter(Artikel.PARAMETER_PREIS, para);
		final List<Artikel>artikel = query.getResultList();
		//Then
		assertThat(artikel.isEmpty(), is(false));
	}
	
	@Test 
	public void findArtikelByAktualisiert() {
		//Given
		final Date akt = AKTUALISIERT_NEU;
		
		//When
		
		final TypedQuery<Artikel>query = getEntityManager()
				.createNamedQuery(Artikel.ARTIKEL_BY_AKTUALISIERT, Artikel.class);
		
								query.setParameter(Artikel.PARAMETER_AKTUALISIERT, akt);
		final List<Artikel>artikel = query.getResultList();
		//Then
		assertThat(artikel.isEmpty(), is(false));
	}
	
	
	@Test 
	public void findArtikelByErzeugt() {
		//Given
		final Date erz = ERZEUGT_NEU;
		
		//When
		
		final TypedQuery<Artikel>query = getEntityManager()
				.createNamedQuery(Artikel.ARTIKEL_BY_ERZEUGT, Artikel.class);
		
								query.setParameter(Artikel.PARAMETER_ERZEUGT, erz);
		final List<Artikel>artikel = query.getResultList();
		//Then
		assertThat(artikel.isEmpty(), is(false));
	}

	@Test
	public void createArtikel() {
		// Given
		Artikel artikel = new Artikel();
		
		artikel.setName(NV_NAME);
		artikel.setBeschreibung(NV_BESCHREIBUNG);
		artikel.setPreis(a);
		artikel.setErzeugt(ERZEUGT_NEU);
		artikel.setAktualisiert(AKTUALISIERT_NEU);
	
		
			//Kategorie erstellen
			final Kategorie kategorie = new Kategorie();
			kategorie.setBezeichnung("SÄUGLINGE");
			kategorie.setErzeugt(new Timestamp(new Date().getTime()));
			kategorie.setAktualisiert(new Timestamp(new Date().getTime()));
			
			artikel.setKategorie(kategorie);
		
		// When
		try {
			getEntityManager().persist(artikel);         // abspeichern einschl. Adresse und Zahlungsinformation
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
		final List<Artikel> art = getEntityManager().createNamedQuery(Artikel.ARTIKEL_BY_NAME,
	                                                                           Artikel.class)
	                                                         .setParameter(Artikel.PARAMETER_NAME,
	                                                        		       NV_NAME)
				                                             .getResultList();
		
		// Ueberpruefung des ausgelesenen Objekts
		assertThat(art.size(), is(1));
		assertThat(artikel.getArtikelId().longValue() > 0, is(true));
		assertThat(artikel.getName(), is(NV_NAME));
	}

	
	
}

