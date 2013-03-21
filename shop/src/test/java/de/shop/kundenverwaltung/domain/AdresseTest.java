package de.shop.kundenverwaltung.domain;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;

import java.util.List;
import java.util.Set;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.Date;
import java.sql.Timestamp;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import de.shop.util.AbstractDomainTest;

@RunWith(Arquillian.class)
public class AdresseTest extends AbstractDomainTest {
	
	private static final Long VORHANDENE_ID = Long.valueOf(100);
	private static final Long VORHANDENE_ID_FUER_UPDATE = Long.valueOf(103);
	private static final String VORHANDENE_STRASSE = "Eisenbahnstraﬂe 57";
	private static final String STRASSE_NEU = "Humboldstraﬂe 32";
	private static final String STRASSE_UPDATE = "Uhlandstraﬂe 120";
	private static final String PLZ_NEU = "76133";
	private static final String PLZ_VORHANDEN = "78224";
	private static final String ORT_NEU = "Karlsruhe";
	private static final String LAND_VORHANDEN = "DE";
	
	@Test
	public void validate() {
		assertThat(true, is(true));
	}
	
	@Test
	public void findAdresseByIdVorhanden() {
		//Given
		Long adresseId = VORHANDENE_ID;
		
		//When
		Adresse adresse = getEntityManager().find(Adresse.class, adresseId);
		
		//Then
		assertThat(adresse.getAdresseId(), is(adresseId));
	}
	
	@Test
	public void findAdresseByStrasseVorhanden() {
		//Given
		final String strasse = VORHANDENE_STRASSE;
		
		//When
		final Adresse adresse = getEntityManager()
						  .createNamedQuery(Adresse.FIND_ADRESSE_BY_STRASSE, Adresse.class)
						  .setParameter(Adresse.PARAM_ADRESSE_STRASSE, strasse)
						  .getSingleResult();
		
		//Then
		assertThat(adresse.getStrasse(), is(strasse));
	}
	
	@Test
	public void findAdresseByPlzVorhanden() {
		//Given
		final String plz = PLZ_VORHANDEN;
		
		//When
		final Adresse adresse = getEntityManager()
						  .createNamedQuery(Adresse.FIND_ADRESSE_BY_PLZ, Adresse.class)
						  .setParameter(Adresse.PARAM_ADRESSE_PLZ, plz)
						  .getSingleResult();
		
		//Then
		assertThat(adresse.getPlz(), is(plz));
	}
	
	@Test
	public void findAdressenByLand() {
		//Given
		final String land = LAND_VORHANDEN;
		
		//When
		final List<Adresse> adressen = getEntityManager()
						  .createNamedQuery(Adresse.FIND_ADRESSEN_BY_LAND, Adresse.class)
						  .setParameter(Adresse.PARAM_ADRESSEN_LAND, land)
						  .getResultList();
		
		//Then
		assertThat(adressen.isEmpty(), is(false));
		
	}
	
	@Test
	public void createAdresse() {
		//Given
		Adresse adresse = new Adresse();
		adresse.setStrasse(STRASSE_NEU);
		adresse.setPlz(PLZ_NEU);
		adresse.setOrt(ORT_NEU);
		adresse.setLand(AdresseLandType.DE);
		adresse.setErzeugt(new Timestamp(new Date().getTime()));
		adresse.setAktualisiert(new Timestamp(new Date().getTime()));
		
		try {
			getEntityManager().persist(adresse);
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
		
		// Die abgespeicherte Adresse ueber eine Named Query ermitteln
				
		final List<Adresse> adressen = getEntityManager()
						.createNamedQuery(Adresse.FIND_ADRESSE_BY_STRASSE, Adresse.class)
				        .setParameter(Adresse.PARAM_ADRESSE_STRASSE, STRASSE_NEU)
				        .getResultList();
		
				// Ueberpruefung des ausgelesenen Objekts
				assertThat(adressen.size(), is(1));
				assertThat(adresse.getStrasse(), is(STRASSE_NEU));
				assertThat(adresse.getAdresseId().longValue() > 0, is(true));
	}
	
	@Test
	public void updateAdresseById() {
		//Given
		Long adresseId = Long.valueOf(VORHANDENE_ID_FUER_UPDATE);
		
		Adresse adresse = getEntityManager().find(Adresse.class, adresseId);
		
		adresse.setStrasse(STRASSE_UPDATE);
		
		try {
			getEntityManager().merge(adresse);
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
		Adresse adresseCheck = getEntityManager().find(Adresse.class, adresseId);
		assertThat(adresseCheck.getStrasse(), is(STRASSE_UPDATE));
	}
}
