package de.shop.bestellverwaltung.service;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.is;

import java.util.List;

import javax.inject.Inject;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;


import org.jboss.arquillian.junit.Arquillian;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.artikelverwaltung.service.ArtikelService;
import de.shop.bestellverwaltung.domain.Bestellposition;
import de.shop.bestellverwaltung.domain.BestellpositionstatusType;
import de.shop.bestellverwaltung.domain.BestellstatusType;
import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.bestellverwaltung.service.BestellungService.BestellungFetchType;
import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.kundenverwaltung.service.KundeService;
import de.shop.kundenverwaltung.service.KundeService.FetchType;
import de.shop.util.AbstractTest;

@RunWith(Arquillian.class)
public class BestellungServiceTest extends AbstractTest {
	
	private static final Long KUNDE_ID = Long.valueOf(600);
	private static final Long ARTIKEL_1_ID = Long.valueOf(210);
	private static final int ARTIKEL_1_BESTELLMENGE = 4;
	private static final Long ARTIKEL_2_ID = Long.valueOf(220);
	private static final int ARTIKEL_2_BESTELLMENGE = 3;
	private static final String LVNNR = "LVNNR-Test";
	private static final Long BESTELLUNG_ID = Long.valueOf(300);
	
	@Inject
	private BestellungService bs;
	
	@Inject
	private KundeService ks;
	
	@Inject
	private ArtikelService as;
	
	@Test
	public void validate() {
		assertThat(true, is(true));
	}
	
	@Test
	public void getAllBestellungen() throws RollbackException, HeuristicMixedException, HeuristicRollbackException,
    									  SystemException, NotSupportedException {
		final UserTransaction trans = getUserTransaction();
		trans.commit();
		

		trans.begin();
		List<Bestellung> bestellungen = bs.findAllBestellungen(BestellungFetchType.NUR_BESTELLUNG, LOCALE);
		trans.commit();
		
		assertThat(bestellungen.size(), is(10));		
	}	
	
	@Test
	public void createBestellung() throws RollbackException, HeuristicMixedException, HeuristicRollbackException,
	                                      SystemException, NotSupportedException {

		Artikel artikel1 = as.findArtikelById(ARTIKEL_1_ID, LOCALE);
		final UserTransaction trans = getUserTransaction();
		trans.commit();
		
		Bestellung bestellung = new Bestellung();
		bestellung.setLieferverfolgungsnummer(LVNNR);
		bestellung.setStatus(BestellstatusType.OFFEN);
		Bestellposition bpos = new Bestellposition(artikel1, ARTIKEL_1_BESTELLMENGE, BestellpositionstatusType.OFFEN);
		bestellung.addBestellposition(bpos);
		
		assertThat(bestellung.getBestellpositionen().get(0).getBestellmenge(), is(ARTIKEL_1_BESTELLMENGE));
		
		trans.begin();
		Artikel artikel2 = as.findArtikelById(ARTIKEL_2_ID, LOCALE);
		trans.commit();
	
		Bestellposition bpos2 = new Bestellposition(artikel2, ARTIKEL_2_BESTELLMENGE, BestellpositionstatusType.OFFEN); 
		bestellung.addBestellposition(bpos2);

		trans.begin();
		Kunde kunde = ks.findKundeById(KUNDE_ID, FetchType.NUR_KUNDE, LOCALE);
		trans.commit();

		assertThat(kunde.getKundeId(), is(KUNDE_ID));		
		
		trans.begin();
		bestellung = bs.createBestellung(bestellung, kunde, LOCALE);
		trans.commit();
		
		// Then
		assertThat(bestellung.getBestellpositionen().size(), is(2));
		for (Bestellposition bp : bestellung.getBestellpositionen()) {
			assertThat(bp.getArtikel().getArtikelId(), anyOf(is(ARTIKEL_1_ID), is(ARTIKEL_2_ID)));
		}
			 
		kunde = bestellung.getKunde();
		assertThat(kunde.getKundeId(), is(KUNDE_ID));
	}
	

	@Test
	public void updateBestellung() throws RollbackException, HeuristicMixedException, HeuristicRollbackException,
    									  SystemException, NotSupportedException {
		
		final UserTransaction trans = getUserTransaction();
		trans.commit();
		

		trans.begin();
		Bestellung bestellung = bs.findBestellungById(BESTELLUNG_ID, BestellungFetchType.MIT_BESTELLPOSITIONEN, LOCALE);
		trans.commit();
		
		//Artikel artikel1 = as.findArtikelById(ARTIKEL_1_ID);
		//Bestellposition bpos = new Bestellposition(artikel1, ARTIKEL_1_BESTELLMENGE, BestellpositionstatusType.OFFEN);
		//bestellung.addBestellposition(bpos);		
		
//		bestellung.getBestellpositionen().get(0).setBestellmenge(99);
		bestellung.setLieferverfolgungsnummer("Test123");
		
//		assertThat(bestellung.getBestellpositionen().size(),is(3));
//		assertThat(bestellung.getBestellpositionen().get(0).getBestellmenge(), is(99));
		
		
		trans.begin();
		bestellung = bs.updateBestellung(bestellung, LOCALE);
		trans.commit();
		
		
		trans.begin();
		bestellung = bs.findBestellungById(BESTELLUNG_ID, BestellungFetchType.MIT_BESTELLPOSITIONEN, LOCALE);
		trans.commit();
//		assertThat(bestellung.getBestellpositionen().size(),is(3));
		assertThat(bestellung.getLieferverfolgungsnummer(), is("Test123"));
//		assertThat(bestellung.getBestellpositionen().get(0).getBestellmenge(), is(99));
		
	}	
	
	@Test
	public void getBestellungById() throws RollbackException, HeuristicMixedException, HeuristicRollbackException,
    									  SystemException, NotSupportedException {
		
		final UserTransaction trans = getUserTransaction();
		trans.commit();
		

		trans.begin();
		Bestellung bestellung = bs.findBestellungById(BESTELLUNG_ID, 
													  BestellungFetchType.NUR_BESTELLUNG, 
													  LOCALE);
		trans.commit();
		
		assertThat(bestellung.getId(), is(BESTELLUNG_ID));		
	}	

}

