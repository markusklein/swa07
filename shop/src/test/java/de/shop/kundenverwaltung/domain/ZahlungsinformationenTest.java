package de.shop.kundenverwaltung.domain;


import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import de.shop.util.AbstractDomainTest;

@RunWith(Arquillian.class)
public class ZahlungsinformationenTest extends AbstractDomainTest {
	
	private static final Long ID_VORHANDEN = Long.valueOf(703);
	private static final Long UPDATE_KONTONUMMER = Long.valueOf(99999999);
	private static final Timestamp AKTUALISIERT_VORHANDEN = new Timestamp(new Date().getTime());
	private static final Long BLZ_VORHANDEN = Long.valueOf(66190000);
	private static final Timestamp ERZEUGT_VORHANDEN = new Timestamp(new Date().getTime());
	private static final String IBAN_VORHANDEN = "DE95661900000005616056";
	private static final String KONTOINHABER_VORHANDEN = "Johannes Schweizer";
	private static final String KREDITINSTITUT_VORHANDEN = "Volksbank Karlsruhe";
	private static final String SWIFT_VORHANDEN = "GENODE61KA1";
	
	private static final Long KONTONUMMER_NEU = Long.valueOf(12345678);
	
	@Test
	public void validate() {
		assertThat(true, is(true));
	}
	
	@Test
	public void findZahlungsinformationByIdVorhanden() {
		
		// Given Ausgangswert "703"
		final long id  = ID_VORHANDEN;
				
		//When
		
		final Zahlungsinformation zahlungsinformation = getEntityManager().find(Zahlungsinformation.class, id);
		
		//Then
		assertThat(zahlungsinformation.getZahlId(), is(id));
	}
	
	
	@Test
	public void createZahlungsinformation() {
		// Given " Ausangsdaten aus originalen Datensatz übernommen "
		
		Zahlungsinformation zahlungsinformation = new Zahlungsinformation();
		zahlungsinformation.setAktualisiert(AKTUALISIERT_VORHANDEN);
		zahlungsinformation.setBlz(BLZ_VORHANDEN);
		zahlungsinformation.setErzeugt(ERZEUGT_VORHANDEN);
		zahlungsinformation.setIban(IBAN_VORHANDEN);
		zahlungsinformation.setKontoinhaber(KONTOINHABER_VORHANDEN);
		zahlungsinformation.setKontonummer(KONTONUMMER_NEU);
		zahlungsinformation.setKreditinstitut(KREDITINSTITUT_VORHANDEN);
		zahlungsinformation.setSwift(SWIFT_VORHANDEN);
		
		try {
			getEntityManager().persist(zahlungsinformation);
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
		
		// Die abgespeicherte Zahlungsinformation ueber eine Named Query ermitteln
		
				final List<Zahlungsinformation> zahlungsinformationen = getEntityManager()
					  .createNamedQuery(Zahlungsinformation.FIND_ZAHLUNGSINFORMATION_BY_KONTONR,
		                                Zahlungsinformation.class)
		              .setParameter(Zahlungsinformation.PARAM_ZAHLUNGSINFORMATION_KONTONR, KONTONUMMER_NEU)
		              .getResultList();
		// Ueberpruefung des ausgelesenen Objekts
		assertThat(zahlungsinformationen.size(), is(1));
		assertThat(zahlungsinformation.getKontonummer(), is(KONTONUMMER_NEU));
		assertThat(zahlungsinformation.getZahlId().longValue() > 0, is(true));
	}
	
	@Test
	public void UpdateZahlungsinformationById() {
		//Given
		Long id = ID_VORHANDEN;
		Long kontonummer = UPDATE_KONTONUMMER;
		
		Zahlungsinformation zahlungsinformation = getEntityManager().find(Zahlungsinformation.class, id);
		
		zahlungsinformation.setKontonummer(kontonummer);
		
		try {
			getEntityManager().merge(zahlungsinformation);
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
		Zahlungsinformation zahlungsinformationCheck = getEntityManager().find(Zahlungsinformation.class, id);
		assertThat(zahlungsinformationCheck.getKontonummer(), is(kontonummer));
	}

}
