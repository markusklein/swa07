package de.shop.kundenverwaltung.rest;

import java.io.StringReader;
import java.lang.invoke.MethodHandles;
import java.util.Set;

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;

import static de.shop.util.TestConstants.ADRESSEN_ID_PATH;
import static de.shop.util.TestConstants.ADRESSEN_ID_PATH_PARAM;
import static de.shop.util.TestConstants.ADRESSEN_PATH;
import static de.shop.util.TestConstants.LOCATION;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.jayway.restassured.response.Response;

import static com.jayway.restassured.RestAssured.given;
import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_NO_CONTENT;
import static java.net.HttpURLConnection.HTTP_OK;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.runners.MethodSorters.NAME_ASCENDING;
import static de.shop.util.TestConstants.ACCEPT;
import de.shop.util.AbstractResourceTest;

@RunWith(Arquillian.class)
@FixMethodOrder(NAME_ASCENDING)
public class AdresseResourceTest extends AbstractResourceTest {
	
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass());
	
	private static final Long ADRESSE_ID_VORHANDEN = Long.valueOf(103);
	private static final Long ADRESSE_ID_NICHT_VORHANDEN = Long.valueOf(453);
	
	private static final String STRASSE_NEU = "Karlsstraße 30";
	private static final String PLZ_NEU = "76133";
	private static final String ORT_NEU = "Karlsruhe";
	private static final String LAND_NEU = "DE";
	
	private static final String STRASSE_UPDATE = "Moltkestraße 333";

	@Test
	public void validate() {
		assertThat(true, is(true));
	}
	
	@Test
	public void findAdresseById() {
		LOGGER.debugf("Beginn findAdresseById");
		
		//Given
		final Long adresseId = ADRESSE_ID_VORHANDEN;
		
		//When
		final Response response = given().header(ACCEPT, APPLICATION_JSON)
										 .pathParam(ADRESSEN_ID_PATH_PARAM, adresseId)
										 .get(ADRESSEN_ID_PATH);
		
		//Then
		assertThat(response.getStatusCode(), is(HTTP_OK));
		
		try (final JsonReader jsonReader =
	              getJsonReaderFactory().createReader(new StringReader(response.asString()))) {
			final JsonObject jsonObject = jsonReader.readObject();
			assertThat(jsonObject.getJsonNumber("adresseId").longValue(), is(adresseId.longValue()));
		}

		LOGGER.debugf("ENDE findAdresseById");
	}
	
	@Test
	public void findAdresseByIdNichtVorhanden() {
		LOGGER.debugf("BEGINN findAdresseByIdNichtVorhanden");
		
		// Given
		final Long adresseId = ADRESSE_ID_NICHT_VORHANDEN;
		
		// When
		final Response response = given().header(ACCEPT, APPLICATION_JSON)
				                         .pathParameter(ADRESSEN_ID_PATH_PARAM, adresseId)
                                         .get(ADRESSEN_ID_PATH);

    	// Then
    	assertThat(response.getStatusCode(), is(HTTP_NOT_FOUND));
		LOGGER.debugf("ENDE findAdresseByIdNichtVorhanden");
	}
	
	@Test
	public void createAdresse() {
		LOGGER.debugf("BEGINN createAdresse");
		
		//Given
		final String strasse = STRASSE_NEU;
		final String plz = PLZ_NEU;
		final String ort = ORT_NEU;
		final String land = LAND_NEU;
		
		final String username = USERNAME_ADMIN;
		final String passwort = PASSWORD_ADMIN;
		
		final JsonObject jsonObject =  getJsonBuilderFactory().createObjectBuilder()
		          .add("strasse", strasse)
		          .add("plz", plz)
		          .add("ort", ort)
		          .add("land", land)
		          .build();
		
		// When
		final Response response = given().contentType(APPLICATION_JSON)
						                 .body(jsonObject.toString())
		                                 .auth()
		                                 .basic(username, passwort)
		                                 .post(ADRESSEN_PATH);
		
		// Then
		assertThat(response.getStatusCode(), is(HTTP_CREATED));
		final String location = response.getHeader(LOCATION);
		final int startPos = location.lastIndexOf('/');
		final String idStr = location.substring(startPos + 1);
		final Long id = Long.valueOf(idStr);
		assertThat(id.longValue() > 0, is(true));

		LOGGER.debugf("ENDE createAdresse");
	}
	
	@Test
	public void updateAdresse() {
		final Long adresseId = ADRESSE_ID_VORHANDEN;
		final String strasseUpdate = STRASSE_UPDATE;
		
		final String username = USERNAME_ADMIN;
		final String passwort = PASSWORD_ADMIN;
		
		//When
		Response response = given().header(ACCEPT, APPLICATION_JSON)
										 .pathParam(ADRESSEN_ID_PATH_PARAM, adresseId)
										 .get(ADRESSEN_ID_PATH);
		
		JsonObject jsonObject;
		try (final JsonReader jsonReader =
				 getJsonReaderFactory().createReader(new StringReader(response.asString()))) {
			jsonObject = jsonReader.readObject();
		}
    	assertThat(jsonObject.getJsonNumber("adresseId").longValue(), is(adresseId.longValue()));
		
    	// Aus den gelesenen JSON-Werten ein neues JSON-Objekt mit neuem Nachnamen bauen
    	final JsonObjectBuilder job = getJsonBuilderFactory().createObjectBuilder();
    	final Set<String> keys = jsonObject.keySet();
    	for (String k : keys) {
    		if ("strasse".equals(k)) {
    			job.add("strasse", strasseUpdate);
    		}
    		else {
    			job.add(k, jsonObject.get(k));
    		}
    	}
    	jsonObject = job.build();
    	
		response = given().contentType(APPLICATION_JSON)
				          .body(jsonObject.toString())
                          .auth()
                          .basic(username, passwort)
                          .put(ADRESSEN_PATH);
		
		// Then
		assertThat(response.getStatusCode(), is(HTTP_NO_CONTENT));
	}
}
