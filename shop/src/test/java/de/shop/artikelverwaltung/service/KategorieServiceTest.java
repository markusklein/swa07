package de.shop.artikelverwaltung.service;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;
import java.util.Date;

import java.util.List;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import de.shop.artikelverwaltung.domain.Kategorie;
import de.shop.util.AbstractTest;
import javax.inject.Inject;
import java.sql.Timestamp;

@RunWith(Arquillian.class)
public class KategorieServiceTest extends AbstractTest {
	private static final long KATEGORIE_ID_VORHANDEN = 500;
	private static final Timestamp DATUM_NEU = new Timestamp(new Date().getTime());
	private static final String BEZEICHNUNG_NEU = "Tiere";
	private static final String BEZEICHNUNG_NEU2 = "Säuglinge";
	private static final String BEZEICHNUNG_VORH = "Herren";
	@Inject
	private KategorieService ks;
	
	@Test
	public void Test() {
		assertThat(true, is(true));
	}
	@Test
	public void findKategorieById() {
		final long id = KATEGORIE_ID_VORHANDEN;
		
		final Kategorie kategorie = ks.findKategorieById(id, LOCALE);
		
		assertThat(kategorie.getKategorieId(), is(id));
	}
	@Test
	public void findKategorieByBezeichnung() {
		final String bez = BEZEICHNUNG_VORH;
		
		final List<Kategorie> kategorie = ks.findKategorieByBezeichnung(bez, LOCALE);
	
		for (Kategorie k : kategorie) {
		assertThat(k.getBezeichnung(), is(BEZEICHNUNG_VORH));
		}
	}
	
	@Test
	public void createKategorie() {
		
		
		Kategorie neueKategorie = new Kategorie();
		neueKategorie.setBezeichnung(BEZEICHNUNG_NEU);
		neueKategorie.setAktualisiert(DATUM_NEU);
		neueKategorie.setErzeugt(DATUM_NEU);
		
		neueKategorie = ks.createKategorie(neueKategorie, LOCALE);
		assertThat(neueKategorie.getBezeichnung(), is(BEZEICHNUNG_NEU));
		
		
		
	}
	
	@Test
	public void updateKategorieBezeichnung() {
		final long id = KATEGORIE_ID_VORHANDEN;
		
		 Kategorie vorhanden = ks.findKategorieById(id, LOCALE);
		vorhanden.setBezeichnung(BEZEICHNUNG_NEU2);
		
		vorhanden = ks.updateKategorie(vorhanden, LOCALE);
		assertThat(vorhanden.getBezeichnung(), is(BEZEICHNUNG_NEU2));
	}
}
	


