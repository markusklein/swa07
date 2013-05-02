package de.shop.artikelverwaltung.rest;

import static com.jayway.restassured.RestAssured.given;
import static de.shop.util.TestConstants.ACCEPT;
import static de.shop.util.TestConstants.KATEGORIE_ID_PATH;
import static de.shop.util.TestConstants.KATEGORIE_ID_PATH_PARAM;
import static de.shop.util.TestConstants.KATEGORIE_PATH;
import static de.shop.util.TestConstants.LOCATION;
import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_FORBIDDEN;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_NO_CONTENT;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.runners.MethodSorters.NAME_ASCENDING;

import java.io.StringReader;
import java.lang.invoke.MethodHandles;
import java.util.Set;

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.jayway.restassured.response.Response;

import de.shop.util.AbstractResourceTest;


@RunWith(Arquillian.class)
@FixMethodOrder(NAME_ASCENDING)
public class KategorieResourceTest extends AbstractResourceTest {
	
	private static final Long KATEGORIE_ID_VORHANDEN = Long.valueOf(500);
	private static final Long KATEGORIE_ID_NICHT_VORHANDEN = Long.valueOf(1000);
	private static final String NEUE_BEZEICHNUNG = "Tiere";
	private static final String  NEUE_BEZEICHNUNG_UPDATE = "Babies";

	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	
		
		@Test
		public void validate() {
			assertThat(true, is(true));
		}
		//TODO Auth
		@Test
		public void findKategorieById() {
			LOGGER.debugf("BEGINN findKategorieById");
			
			// Given
			final Long kategorieId = KATEGORIE_ID_VORHANDEN;
			
			// When
			final Response response = given().header(ACCEPT, APPLICATION_JSON)
					                         .pathParameter(KATEGORIE_ID_PATH_PARAM, kategorieId)
	                                         .get(KATEGORIE_ID_PATH);

			// Then
			assertThat(response.getStatusCode(), is(HTTP_OK));
			
			try (final JsonReader jsonReader =
					              getJsonReaderFactory().createReader(new StringReader(response.asString()))) {
				final JsonObject jsonObject = jsonReader.readObject();
				assertThat(jsonObject.getJsonNumber("kategorieId").longValue(), is(kategorieId.longValue()));
			}
			
			LOGGER.debugf("ENDE findKategorieById");
		}
		
		@Test
		public void findKategorieByIdNichtVorhanden() {
			LOGGER.debugf("BEGINN findKategorieByIdNichtVorhanden");
			
			// Given
			final Long kategorieId = KATEGORIE_ID_NICHT_VORHANDEN;
			
			// When
			final Response response = given().header(ACCEPT, APPLICATION_JSON)
					                         .pathParameter(KATEGORIE_ID_PATH_PARAM, kategorieId)
	                                         .get(KATEGORIE_ID_PATH);

	    	// Then
	    	assertThat(response.getStatusCode(), is(HTTP_NOT_FOUND));
			LOGGER.debugf("ENDE findKategorieByIdNichtVorhanden");
		}
		
		@Test
		public void createKategorie() {
			LOGGER.debugf("BEGINN createKategorie");
			
			// Given
			final String bezeichnung = NEUE_BEZEICHNUNG;
			final String username = USERNAME_ADMIN;
			final String password = PASSWORD_ADMIN;
			
			
			final JsonObject jsonObject = getJsonBuilderFactory().createObjectBuilder()
			             		          .add("bezeichnung", bezeichnung)
			             		          .build();

			// When
			final Response response = given().contentType(APPLICATION_JSON)
					                         .body(jsonObject.toString())
					                         .auth()
					                         .basic(username, password)
	                                         .post(KATEGORIE_PATH);
			
			// Then
			assertThat(response.getStatusCode(), is(HTTP_CREATED));
			final String location = response.getHeader(LOCATION);
			final int startPos = location.lastIndexOf('/');
			final String idStr = location.substring(startPos + 1);
			final Long id = Long.valueOf(idStr);
			assertThat(id.longValue() > 0, is(true));

			LOGGER.debugf("ENDE createKategorie");
		}
		
		public void updateKategorie() {
			
			LOGGER.debugf("BEGINN updateKategorie");
			
			//Given
			 
			final Long kategorieId = KATEGORIE_ID_VORHANDEN;
			final String neue_bezeichnung = NEUE_BEZEICHNUNG_UPDATE;
			final String username = USERNAME_ADMIN;
			final String password = PASSWORD_ADMIN;
			
			// When
			Response response = given().header(ACCEPT, APPLICATION_JSON)
					                   .pathParameter(KATEGORIE_ID_PATH_PARAM, kategorieId)
					                   .auth()
					                   .basic(username, password)
	                                   .get(KATEGORIE_ID_PATH);
			
			JsonObject jsonObject;
			try (final JsonReader jsonReader =
					              getJsonReaderFactory().createReader(new StringReader(response.asString()))) {
				jsonObject = jsonReader.readObject();
			}
	    	assertThat(jsonObject.getJsonNumber("KategorieId").longValue(), is(kategorieId.longValue()));
	    	
	    	// Aus den gelesenen JSON-Werten ein neues JSON-Objekt mit neuem Nachnamen bauen
	    	final JsonObjectBuilder job = getJsonBuilderFactory().createObjectBuilder();
	    	final Set<String> keys = jsonObject.keySet();
	    	for (String k : keys) {
	    		if ("bezeichnung".equals(k)) {
	    			job.add("bezeichnung", neue_bezeichnung);
	    		}
	    		else {
	    			job.add(k, jsonObject.get(k));
	    		}
	    	}
	    	jsonObject = job.build();
	    	
			response = given().contentType(APPLICATION_JSON)
					          .body(jsonObject.toString())
	                          //.auth()
	                          //.basic(username, password)
	                          .put(KATEGORIE_PATH);
			
			// Then
			assertThat(response.getStatusCode(), is(HTTP_NO_CONTENT));
			LOGGER.debugf("ENDE updateKategorie");
		}
		
		@Test
		public void invalidcreateKategorie() {
			LOGGER.debugf("BEGINN invalidcreateKategorie");
			
			// Given
			final String bezeichnung = NEUE_BEZEICHNUNG;
			final String username = USERNAME_ADMIN;
			final String password = PASSWORD_FALSCH;
			
			
			final JsonObject jsonObject = getJsonBuilderFactory().createObjectBuilder()
			             		          .add("bezeichnung", bezeichnung)
			             		          .build();

			// When
			final Response response = given().contentType(APPLICATION_JSON)
					                         .body(jsonObject.toString())
					                         .auth()
					                         .basic(username, password)
	                                         .post(KATEGORIE_PATH);
			
			// Then
			assertThat(response.getStatusCode(), is(HTTP_UNAUTHORIZED));
		
			LOGGER.debugf("ENDE invalidcreateKategorie");
		}
		
		@Test
		public void invalidrollcreateKategorie() {
			LOGGER.debugf("BEGINN invalidrollcreateKategorie");
			
			// Given
			final String bezeichnung = NEUE_BEZEICHNUNG;
			final String username = USERNAME;
			final String password = PASSWORD;
			
			
			final JsonObject jsonObject = getJsonBuilderFactory().createObjectBuilder()
			             		          .add("bezeichnung", bezeichnung)
			             		          .build();

			// When
			final Response response = given().contentType(APPLICATION_JSON)
					                         .body(jsonObject.toString())
					                         .auth()
					                         .basic(username, password)
	                                         .post(KATEGORIE_PATH);
			
			// Then
			assertThat(response.getStatusCode(), is(HTTP_FORBIDDEN));
		
			LOGGER.debugf("ENDE invalidrollcreateKategorie");
		}
}
