package de.shop.util;

public final class TestConstants {
	public static final String WEB_PROJEKT = "shop2";
	
	// HTTP-Header
	public static final String ACCEPT = "Accept";
	public static final String LOCATION = "Location";
	
	// URLs und Pfade
	public static final String BASEURI;
	public static final int PORT;
	public static final String BASEPATH;
	
	static {
		BASEURI = System.getProperty("baseuri", "http://localhost");
		PORT = Integer.parseInt(System.getProperty("port", "8080"));
		BASEPATH = System.getProperty("basepath", "/shop/rest");
	}
	
	public static final String KUNDEN_PATH = "/kunden";
	public static final String KUNDEN_URI = BASEURI + ":" + PORT + BASEPATH + KUNDEN_PATH;
	public static final String KUNDEN_ID_PATH_PARAM = "kundeId";
	public static final String KUNDEN_ID_PATH = KUNDEN_PATH + "/{" + KUNDEN_ID_PATH_PARAM + "}";
	public static final String KUNDEN_NACHNAME_QUERY_PARAM = "nachname";
	public static final String KUNDEN_ID_FILE_PATH = KUNDEN_ID_PATH + "/file";
	
	public static final String ADRESSEN_PATH = "/adressen";
	public static final String ADRESSEN_ID_PATH_PARAM = "adresseId";
	public static final String ADRESSEN_ID_PATH = ADRESSEN_PATH + "/{" + ADRESSEN_ID_PATH_PARAM + "}";
	
	public static final String BESTELLUNGEN_PATH = "/bestellungen";
	public static final String BESTELLUNGEN_ID_PATH_PARAM = "bestellungId";
	public static final String BESTELLUNGEN_ID_PATH = BESTELLUNGEN_PATH + "/{" + BESTELLUNGEN_ID_PATH_PARAM + "}";
	public static final String BESTELLUNGEN_ID_KUNDE_PATH = BESTELLUNGEN_ID_PATH + "/kunde";
	
	public static final String ARTIKEL_PATH = "/artikel";
	public static final String ARTIKEL_URI = BASEURI + ":" + PORT + BASEPATH + ARTIKEL_PATH;
	public static final String ARTIKEL_ID_PATH_PARAM = "artikelId";
	public static final String ARTIKEL_ID_PATH = ARTIKEL_PATH + "/{" + ARTIKEL_ID_PATH_PARAM + "}";
	public static final String ARTIKEL_NAME_QUERY_PARAM = "name";
	public static final String ARTIKEL_ID_FILE_PATH = ARTIKEL_ID_PATH + "/file";
	
	public static final String KATEGORIE_PATH = "/kategorien";
	public static final String KATEGORIE_URI = BASEURI + ":" + PORT + BASEPATH + KATEGORIE_PATH;
	public static final String KATEGORIE_ID_PATH_PARAM = "kategorieId";
	public static final String KATEGORIE_ID_PATH = KATEGORIE_PATH + "/{" + KATEGORIE_ID_PATH_PARAM + "}";
	public static final String KATEGORIE_NAME_QUERY_PARAM = "name";
	public static final String KATEGORIE_ID_FILE_PATH = KATEGORIE_ID_PATH + "/file";
	
	public static final String ZAHLUNGSINFORMATIONEN_PATH = "/zahlungsinformationen";
	public static final String ZAHLUNGSINFORMATIONEN_URI = BASEURI + ":" + PORT + BASEPATH + ZAHLUNGSINFORMATIONEN_PATH;
	public static final String ZAHLUNGSINFORMATIONEN_ID_PATH_PARAM = "zahlId";
	public static final String ZAHLUNGSINFORMATIONEN_ID_PATH = ZAHLUNGSINFORMATIONEN_PATH + "/{" + ZAHLUNGSINFORMATIONEN_ID_PATH_PARAM + "}";
	
	// Testklassen fuer Service- und Domain-Tests
	public static final Class<?>[] TEST_CLASSES = { };
	
	private TestConstants() {
	}
}
