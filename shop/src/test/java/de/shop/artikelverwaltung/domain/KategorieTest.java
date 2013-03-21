package de.shop.artikelverwaltung.domain;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;

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
public class KategorieTest extends AbstractDomainTest {
	
	private final String BEZEICHNUNG_ALT = "Herren";
	private final String BEZEICHNUNG_NEU = "TIER";
	private static final int TAG = 01;
	private static final int MONAT = Calendar.JUNE;
	private static final int JAHR = 2006;
	private static final Date AKTUALISIERT_NEU = new GregorianCalendar(JAHR, MONAT, TAG).getTime();
	private static final Date ERZEUGT_NEU = new GregorianCalendar(JAHR, MONAT, TAG).getTime();
	private static final long a = 525;
	
	
	
	
	@Test
	public void validate() {
		assertThat(true, is(true));
	}
	
	
	@Test
	public void findKategorieById() {
		//Given
		Long kategorie_id = Long.valueOf(a);
		
		//When
		Kategorie kategorie = getEntityManager().find(Kategorie.class, kategorie_id);
		
		//Then
		assertThat(kategorie.getKategorieId(), is(kategorie_id));
		
	}
	
	@Test 
	public void findKategorieByBezeichnung() {
		//Given
		final String para = BEZEICHNUNG_ALT;
		
		//When
		
		final TypedQuery<Kategorie>query = getEntityManager()
				.createNamedQuery(Kategorie.KATEGORIE_BY_BEZEICHNUNG, Kategorie.class);
		
								query.setParameter(Kategorie.PARAMETER_BEZEICHNUNG, para);
		final List<Kategorie>kategorien = query.getResultList();
		//Then
		assertThat(kategorien.isEmpty(), is(false));
	}
	

	@Test 
	public void findKategorieByAktualisiert() {
		//Given
		final Date akt = AKTUALISIERT_NEU;
		
		//When
		
		final TypedQuery<Kategorie>query = getEntityManager()
				.createNamedQuery(Kategorie.KATEGORIE_BY_AKTUALISIERT, Kategorie.class);
		
								query.setParameter(Kategorie.PARAMETER_AKTUALISIERT, akt);
		final List<Kategorie>kategorien = query.getResultList();
		//Then
		assertThat(kategorien.isEmpty(), is(false));
	}
	
	
	@Test 
	public void findKategorieByErzeugt() {
		//Given
		final Date erz = ERZEUGT_NEU;
		
		//When
		
		final TypedQuery<Kategorie>query = getEntityManager()
				.createNamedQuery(Kategorie.KATEGORIE_BY_ERZEUGT, Kategorie.class);
		
								query.setParameter(Kategorie.PARAMETER_ERZEUGT, erz);
		final List<Kategorie>kategorien = query.getResultList();
		//Then
		assertThat(kategorien.isEmpty(), is(false));
	}
	
	@Test
	public void createKategorie() {
		//Given
		Kategorie kategorie = new Kategorie();
		kategorie.setBezeichnung(BEZEICHNUNG_NEU);
		kategorie.setErzeugt(new Timestamp(new Date().getTime()));
		kategorie.setAktualisiert(new Timestamp(new Date().getTime()));
		
		try {
			getEntityManager().persist(kategorie);
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
		
		// THEN
		
		// Die abgespeicherte Kategorie ueber eine Named Query ermitteln
				
		final List<Kategorie> kategorien = getEntityManager()
				.createNamedQuery(Kategorie.KATEGORIE_BY_BEZEICHNUNG, Kategorie.class)
				.setParameter(Kategorie.PARAMETER_BEZEICHNUNG, BEZEICHNUNG_NEU).getResultList();
		
				// Ueberpruefung des ausgelesenen Objekts
				assertThat(kategorien.size(), is(1));
				assertThat(kategorie.getBezeichnung(), is(BEZEICHNUNG_NEU));
				assertThat(kategorie.getKategorieId().longValue() > 0, is(true));
	}
	
	
	
}
