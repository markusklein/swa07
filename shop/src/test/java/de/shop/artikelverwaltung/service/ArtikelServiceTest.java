package de.shop.artikelverwaltung.service;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.inject.Inject;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.artikelverwaltung.domain.Kategorie;
import de.shop.util.AbstractTest;

@RunWith(Arquillian.class)
public class ArtikelServiceTest extends AbstractTest {
private static final long ARTIKEL_ID_VORHANDEN = 200;
private static final String NAME_VORH = "hosen";
private static final float PREIS_NEU = 2000;
private static final String NAME_NEU = "LALALA";
private static final String BESCHREIBUNG_NEU = "Test beschreibung";
private Kategorie  neue_Kategorie = new Kategorie();
private static final long b = 525;
private static final int TAG_NEU = 31;
private static final int MONAT_NEU = Calendar.JANUARY;
private static final int JAHR_NEU = 2011;
private static final Date DATUM_NEU = new GregorianCalendar(JAHR_NEU, MONAT_NEU, TAG_NEU).getTime();


	@Inject
	private ArtikelService as;
	@Inject 
	private KategorieService ks;


	@Test
	public void Test() {
		assertThat(true, is(true));
	}
	@Test
	public void findArtikelById() {
		final long id = ARTIKEL_ID_VORHANDEN;
		
		final Artikel artikel = as.findArtikelById(id, LOCALE);
		
		assertThat(artikel.getArtikelId(), is(id));
	}
	
	@Test
	public void findArtikelByNamen() {
		final String name = NAME_VORH;
		
		final List<Artikel> artikel = as.findArtikelByNamen(name,LOCALE);
		
		for (Artikel a: artikel) {
		assertThat(a.getName(), is(name));
		}
	}
	
	@Test
	public void updateArtikel() {
		final long id = ARTIKEL_ID_VORHANDEN;
		
		Artikel vorhanden = as.findArtikelById(id, LOCALE);
		
		vorhanden.setPreis(PREIS_NEU);
		assertThat(vorhanden.getPreis(), is(PREIS_NEU));
	}
	
	@Test
	public void createArtikel() {
		Artikel neu = new Artikel();
		
		neue_Kategorie = ks.findKategorieById(b, LOCALE);

		
		neu.setName(NAME_NEU);
		neu.setBeschreibung(BESCHREIBUNG_NEU);
		neu.setPreis(PREIS_NEU);
		neu.setKategorie(neue_Kategorie);
		neu.setAktualisiert(DATUM_NEU);
		neu.setErzeugt(DATUM_NEU);
		neu = as.createArtikel(neu, LOCALE);
		assertThat(neu.getName(), is(NAME_NEU));
	}
	
	
}

