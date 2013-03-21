package de.shop.kundenverwaltung.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.transaction.RollbackException;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.shop.util.AbstractTest;
import de.shop.kundenverwaltung.domain.Adresse;
import de.shop.kundenverwaltung.domain.AdresseLandType;

@RunWith(Arquillian.class)
public class AdresseServiceTest extends AbstractTest {
	
	private static final Long VORHANDENE_ID = Long.valueOf(104);
	private static final Long VORHANDENE_ID_FUER_UPDATE = Long.valueOf(103);
	private static final String STRASSE_NEU = "Humboldstraﬂe 32";
	private static final String STRASSE_UPDATE = "Uhlandstraﬂe 120";
	private static final String PLZ_NEU = "76133";
	private static final String ORT_NEU = "Karlsruhe";
	
	@Inject
	private AdresseService as;
	
	@Test
	public void validate() {
		assertThat(true, is(true));
	}
	
	@Test
	public void zaehleAlleAdressen() {
		final List<Adresse> adressen = as.findAlleAdressen();
		final Integer anzahl = 19;
		
		assertThat(anzahl, is(adressen.size()));
	}
	
	@Test
	public void findAdresseById() {
		Long id = Long.valueOf(VORHANDENE_ID);
		
		Adresse adresse = as.findAdresseById(id, LOCALE);
		
		assertThat(id, is(adresse.getAdresseId()));
	}
	
	@Test
	public void createAdresse() throws RollbackException, HeuristicMixedException, HeuristicRollbackException,
    SystemException, NotSupportedException {
		List<Adresse> adressenVorher = as.findAlleAdressen();
		final UserTransaction trans = getUserTransaction();
		trans.commit();
		
		Adresse adresse = new Adresse();
		adresse.setStrasse(STRASSE_NEU);
		adresse.setPlz(PLZ_NEU);
		adresse.setOrt(ORT_NEU);
		adresse.setLand(AdresseLandType.DE);
		adresse.setErzeugt(new Timestamp(new Date().getTime()));
		adresse.setAktualisiert(new Timestamp(new Date().getTime()));
		
		final Date datumVorher = new Date();
		
		trans.begin();
		Adresse neueAdresse = as.createAdresse(adresse, LOCALE);
		trans.commit();
		
		assertThat(datumVorher.getTime() <= neueAdresse.getErzeugt().getTime(), is(true));
		
		trans.begin();
		List<Adresse> adressenNachher = as.findAlleAdressen();
		trans.commit();
		
		assertThat(neueAdresse, is(notNullValue()));
		assertThat(adressenVorher.size() + 1, is(adressenNachher.size()));
		assertThat(neueAdresse.getStrasse(), is(STRASSE_NEU));
	}
	
	@Test
	public void updateAdresse() throws RollbackException, HeuristicMixedException, HeuristicRollbackException,
    SystemException, NotSupportedException {
		final Long adresseId = Long.valueOf(VORHANDENE_ID_FUER_UPDATE);
		Adresse adresse = as.findAdresseById(adresseId, LOCALE);
		final UserTransaction trans = getUserTransaction();
		trans.commit();
		
		final String neuestrasse = STRASSE_UPDATE;
		adresse.setStrasse(neuestrasse);
		
		trans.begin();
		adresse = as.updateAdresse(adresse, LOCALE);
		trans.commit();
		
		assertThat(adresse.getStrasse(), is(neuestrasse));
		trans.begin();
		adresse = as.findAdresseById(adresseId, LOCALE);
		trans.commit();
		assertThat(adresse.getStrasse(), is(neuestrasse));
	}
}
