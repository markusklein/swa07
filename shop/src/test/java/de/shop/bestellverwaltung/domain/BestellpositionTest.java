package de.shop.bestellverwaltung.domain;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import de.shop.util.AbstractDomainTest;

import java.util.List;
import java.util.Set;

import javax.persistence.TypedQuery;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

@RunWith(Arquillian.class)

public class BestellpositionTest extends AbstractDomainTest {
	private static final Long ARTIKEL_ID = Long.valueOf(200);
	private static final Long BESTELLPOSITION_ID = Long.valueOf(403);
	private static final int BESTELLMENGE = 5;

	@Test
	public void validate() {
		
		assertThat(true, is(true));
	}
	
	@Test
	public void updateBestellpositionById() {
		
		Bestellposition bestellposition = getEntityManager().find(Bestellposition.class, BESTELLPOSITION_ID);
		
		bestellposition.setBestellmenge(BESTELLMENGE);
		
		try {
			getEntityManager().merge(bestellposition);
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
		Bestellposition bestellpositionCheck = getEntityManager().find(Bestellposition.class, BESTELLPOSITION_ID);
		assertThat(bestellpositionCheck.getBestellmenge(), is(5));
	}
	
	@Test
	public void findBestellpositionenByArtikel() {
		
		// When
		final TypedQuery<Bestellposition> query =
				                        getEntityManager().createNamedQuery(
				                        		Bestellposition.FIND_BESTELLPOSITIONEN_BY_ARTIKEL,
				                                Bestellposition.class);
		query.setParameter(Bestellposition.PARAM_BESTELLPOSITION_ARTIKEL, ARTIKEL_ID);
		final List<Bestellposition> bestellpositionen = query.getResultList();
		
		// Then
		assertThat(bestellpositionen.isEmpty(), is(false));
	}
	
}
