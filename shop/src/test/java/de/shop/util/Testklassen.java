package de.shop.util;

import java.util.Arrays;
import java.util.List;

import de.shop.artikelverwaltung.domain.ArtikelTest;
import de.shop.artikelverwaltung.domain.KategorieTest;
import de.shop.artikelverwaltung.service.ArtikelServiceTest;
import de.shop.artikelverwaltung.service.KategorieServiceTest;
import de.shop.bestellverwaltung.domain.BestellungTest;
import de.shop.bestellverwaltung.service.BestellungServiceTest;
import de.shop.kundenverwaltung.domain.AdresseTest;
import de.shop.kundenverwaltung.service.AdresseServiceTest;
import de.shop.kundenverwaltung.service.ZahlungsinformationServiceTest;
import de.shop.kundenverwaltung.domain.KundeTest;
import de.shop.kundenverwaltung.service.KundeServiceTest;
import de.shop.kundenverwaltung.domain.ZahlungsinformationenTest;

public enum Testklassen {
	INSTANCE;
	
	// Testklassen aus *VERSCHIEDENEN* Packages auflisten (durch Komma getrennt):
	// so dass alle darin enthaltenen Klassen ins Web-Archiv mitverpackt werden
	private List<Class<? extends AbstractTest>> classes = Arrays.asList(KundeTest.class, KundeServiceTest.class, BestellungTest.class, BestellungServiceTest.class, AdresseTest.class, AdresseServiceTest.class, KategorieTest.class, KategorieServiceTest.class, ArtikelTest.class, ArtikelServiceTest.class, ZahlungsinformationenTest.class, ZahlungsinformationServiceTest.class);
	
	public static Testklassen getInstance() {
		return INSTANCE;
	}
	
	public List<Class<? extends AbstractTest>> getTestklassen() {
		return classes;
	}
}
