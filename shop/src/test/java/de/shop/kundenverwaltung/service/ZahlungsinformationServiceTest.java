package de.shop.kundenverwaltung.service;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import de.shop.util.AbstractTest;
import static org.hamcrest.CoreMatchers.notNullValue;

import javax.inject.Inject;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import de.shop.kundenverwaltung.domain.Zahlungsinformation;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@RunWith(Arquillian.class)
public class ZahlungsinformationServiceTest extends AbstractTest {
	
	private static final Timestamp AKTUALISIERT_NEU = new Timestamp(new Date().getTime());
	private static final Long BLZ_NEU = Long.valueOf(66199999);
	private static final Timestamp ERZEUGT_NEU = new Timestamp(new Date().getTime());
	private static final String IBAN_NEU = "DE95661900000005616099";
	private static final String KONTOINHABER_NEU = "Thorsten Früh";
	private static final Long KONTONUMMER_NEU = Long.valueOf(72829201);
	private static final String KREDITINSTITUT_NEU = "Volksbank Passau";
	private static final String SWIFT_NEU = "ZENODE61KA9";
	
	
	private static final Long ID_VORHANDEN = Long.valueOf(703);
	
	@Inject
	private ZahlungsinformationService zs;
	
		
		@Test
		public void FindAllZahlungsinformation() {
			
			final List<Zahlungsinformation> zahlungsinformationen = zs.findAllZahlungsinformation();
			final int anz = zahlungsinformationen.size();
			final int a = 10;
			assertThat(anz, is(a));

		}
		
		@Test
		public void findZahlungsinformationByIdVorhanden() {
			
			// Given Ausgangswert "703"
			 long id  = Long.valueOf(ID_VORHANDEN);
			
			//When
			
			Zahlungsinformation zahlungsinformation = zs.findZahlungsinformationById(ID_VORHANDEN, LOCALE);
			
			//Then
			assertThat(zahlungsinformation.getZahlId(), is(id));
		}
		
		@Test
		public void createZahlungsinformation() throws RollbackException,
												HeuristicMixedException, HeuristicRollbackException,
												SystemException, NotSupportedException {
			
			List<Zahlungsinformation> davor = zs.findAllZahlungsinformation();
			final UserTransaction trans = getUserTransaction();
				
			trans.commit();
			int davorgesamt = davor.size();
			
			Zahlungsinformation zahlungsinformation = new Zahlungsinformation();
			zahlungsinformation.setAktualisiert(AKTUALISIERT_NEU);
			zahlungsinformation.setBlz(BLZ_NEU);
			zahlungsinformation.setErzeugt(ERZEUGT_NEU);
			zahlungsinformation.setIban(IBAN_NEU);
			zahlungsinformation.setKontoinhaber(KONTOINHABER_NEU);
			zahlungsinformation.setKontonummer(KONTONUMMER_NEU);
			zahlungsinformation.setKreditinstitut(KREDITINSTITUT_NEU);
			zahlungsinformation.setSwift(SWIFT_NEU);
			
			trans.begin();
			Zahlungsinformation neueZahlungsinformation = zs.createZahlungsinformation(zahlungsinformation, LOCALE);
			trans.commit();
			
			trans.begin();
			List<Zahlungsinformation> danach = zs.findAllZahlungsinformation();
			trans.commit();
			
			int danachgesamt = danach.size();
			
			assertThat(neueZahlungsinformation, is(notNullValue()));
			assertThat(davorgesamt + 1, is(danachgesamt));
			assertThat(neueZahlungsinformation.getKontonummer(), is(KONTONUMMER_NEU));
			assertThat(neueZahlungsinformation.getZahlId().longValue() > 0, is(true));
		}
		
		@Test
		public void updateKontonummer() throws RollbackException, HeuristicMixedException, HeuristicRollbackException,
        SystemException, NotSupportedException {
		
			final long zahlId = Long.valueOf(ID_VORHANDEN);
			
			Zahlungsinformation zahlungsinformation = zs.findZahlungsinformationById(zahlId, LOCALE);
			final UserTransaction trans = getUserTransaction();
			trans.commit();
			
			final long neueKontonummer = Long.valueOf(71717171);
			zahlungsinformation.setKontonummer(neueKontonummer);
			
			trans.begin();
			zahlungsinformation = zs.updateZahlungsinformation(zahlungsinformation, LOCALE);
			trans.commit();
			
			//Asserts
			assertThat(zahlungsinformation.getKontonummer(), is(neueKontonummer));
			trans.begin();
			zahlungsinformation = zs.findZahlungsinformationById(zahlId, LOCALE);
			trans.commit();
			assertThat(zahlungsinformation.getKontonummer(), is(neueKontonummer));
		}
		
		
		
		@Test
		public void Test() {
	
			assertThat(true, is(true));
 		}

}

