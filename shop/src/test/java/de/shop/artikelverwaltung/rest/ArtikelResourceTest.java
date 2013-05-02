package de.shop.artikelverwaltung.rest;

import static com.jayway.restassured.RestAssured.given;
import static de.shop.util.TestConstants.ACCEPT;
import static de.shop.util.TestConstants.ARTIKEL_ID_PATH;
import static de.shop.util.TestConstants.ARTIKEL_ID_PATH_PARAM;
import static java.net.HttpURLConnection.HTTP_OK;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.runners.MethodSorters.NAME_ASCENDING;

import java.io.StringReader;

import javax.json.JsonObject;
import javax.json.JsonReader;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.jayway.restassured.response.Response;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.runners.MethodSorters.NAME_ASCENDING;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.jboss.logging.Logger;

import static com.jayway.restassured.RestAssured.given;
import static de.shop.util.TestConstants.ACCEPT;
import static de.shop.util.TestConstants.BASEPATH;
import static de.shop.util.TestConstants.BASEURI;
import static de.shop.util.TestConstants.ARTIKEL_ID_FILE_PATH;
import static de.shop.util.TestConstants.ARTIKEL_ID_PATH_PARAM;
import static de.shop.util.TestConstants.ARTIKEL_ID_PATH;
import static de.shop.util.TestConstants.ARTIKEL_NAME_QUERY_PARAM;
import static de.shop.util.TestConstants.ARTIKEL_PATH;
import static de.shop.util.TestConstants.KATEGORIE_ID_PATH;
import static de.shop.util.TestConstants.KATEGORIE_ID_PATH_PARAM;
import static de.shop.util.TestConstants.KATEGORIE_PATH;
import static de.shop.util.TestConstants.LOCATION;
import static de.shop.util.TestConstants.PORT;
import static java.net.HttpURLConnection.HTTP_CONFLICT;
import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_FORBIDDEN;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_NO_CONTENT;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.junit.runners.MethodSorters.NAME_ASCENDING;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;






import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.xml.bind.DatatypeConverter;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.jayway.restassured.response.Response;






import de.shop.util.AbstractResourceTest;
import de.shop.artikelverwaltung.domain.Kategorie;
import de.shop.artikelverwaltung.rest.KategorieResource;
import de.shop.artikelverwaltung.rest.ArtikelResource;


@RunWith(Arquillian.class)
@FixMethodOrder(NAME_ASCENDING)
public class ArtikelResourceTest extends AbstractResourceTest {
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	
	private static final Long ARTIKEL_ID_VORHANDEN = Long.valueOf(200);
	private static final Long ARTIKEL_ID_UPDATE = Long.valueOf(210);
	private static final Long ARTIKEL_ID_NICHTVORHANDEN = Long.valueOf(20000);
	private static final String NEUE_BEZEICHNUNG = "wundervolle graue Damenhose gangnamstyle";
	private static final String UPDATE_BEZEICHNUNG = "wundervoller schwarzer Langmantel";
	private static final String NEUER_NAME = "HOSE";
	private static final Long NEUE_KATEGORIE_ID = Long.valueOf(500);
	private static final Float NEUER_PREIS = Float.valueOf((float) 1000.00);
	
	
	@Test
	public void validate() {

		assertThat(true, is(true));
	}
	
	@Test
	public void findArtikelById() {
		LOGGER.debugf("BEGINN ARTIKELSUCHE 1");
		
		// Given
		final Long artikelId = ARTIKEL_ID_VORHANDEN;
		
		// When
		final Response response = given().header(ACCEPT, APPLICATION_JSON)
				                         .pathParameter(ARTIKEL_ID_PATH_PARAM, artikelId)
                                         .get(ARTIKEL_ID_PATH);

		// Then
		assertThat(response.getStatusCode(), is(HTTP_OK));
		
		try (final JsonReader jsonReader =
				              getJsonReaderFactory().createReader(new StringReader(response.asString()))) {
			final JsonObject jsonObject = jsonReader.readObject();
			assertThat(jsonObject.getJsonNumber("artikelId").longValue(), is(artikelId.longValue()));
		}
		
		LOGGER.debugf("ENDE ARTIKELSUCHE 1");
	}
	
	@Test
	public void findArtikelByIdNichtVorhanden() {
		LOGGER.debugf("BEGINN ARTIKELSUCHE 2");
		
		// Given
		final Long artikelId = ARTIKEL_ID_NICHTVORHANDEN;
		
		// When
		final Response response = given().header(ACCEPT, APPLICATION_JSON)
				                         .pathParameter(ARTIKEL_ID_PATH_PARAM, artikelId)
                                         .get(ARTIKEL_ID_PATH);

    	// Then
    	assertThat(response.getStatusCode(), is(HTTP_NOT_FOUND));
		LOGGER.debugf("ENDE ARTIKELSUCHE2");
	}
	@Test
	public void createArtikel() {
		LOGGER.debugf("BEGINN ARTIKEL CREATE");
		
		// Given
		final String bezeichnung = NEUE_BEZEICHNUNG;
		final String name = NEUER_NAME;
		final Float preis = NEUER_PREIS;
		final Long kategorie = NEUE_KATEGORIE_ID;
		final String username = USERNAME_ADMIN;
		final String password = PASSWORD_ADMIN;
		
		
		
		final JsonObject jsonObject = getJsonBuilderFactory().createObjectBuilder()
		             		          .add("beschreibung", bezeichnung)
		             		          .add("name", name)
		             		          .add("preis", preis)
		             		          .add("kategorie",getJsonBuilderFactory().createObjectBuilder().add("kategorieId",kategorie))
		             		          .build();

		// When
		final Response response = given().contentType(APPLICATION_JSON)
				                         .body(jsonObject.toString())
				                         .auth()
				                         .basic(username, password)
                                         .post(ARTIKEL_PATH);
		
		// Then
		assertThat(response.getStatusCode(), is(HTTP_CREATED));
		final String location = response.getHeader(LOCATION);
		final int startPos = location.lastIndexOf('/');
		final String idStr = location.substring(startPos + 1);
		final Long id = Long.valueOf(idStr);
		assertThat(id.longValue() > 0, is(true));

		LOGGER.debugf("ENDE ARTIKEL CREATE");
	}
	
	@Test
	public void updateArtikel() {
		
		LOGGER.debugf("BEGINN UPDATE ARTIKEL");
		
		//Given
		 
		final Long artikelId = ARTIKEL_ID_UPDATE;
		final String neue_bezeichnung = UPDATE_BEZEICHNUNG;
		final String username = USERNAME_ADMIN;
		final String password = PASSWORD_ADMIN;
		
		// When
		Response response = given().header(ACCEPT, APPLICATION_JSON)
				                   .pathParameter(ARTIKEL_ID_PATH_PARAM, artikelId)
                                   .get(ARTIKEL_ID_PATH);
		
		assertThat(response.getStatusCode(), is(HTTP_OK));
		
		JsonObject jsonObject;
		try (final JsonReader jsonReader =
				              getJsonReaderFactory().createReader(new StringReader(response.asString()))) {
			 jsonObject = jsonReader.readObject();
		}
    	assertThat(jsonObject.getJsonNumber("artikelId").longValue(), is(artikelId.longValue()));
    	
    	// Aus den gelesenen JSON-Werten ein neues JSON-Objekt mit neuem Nachnamen bauen
    	final JsonObjectBuilder job = getJsonBuilderFactory().createObjectBuilder();
    	final Set<String> keys = jsonObject.keySet();
    	for (String k : keys) {
    		if ("beschreibung".equals(k)) {
    			job.add("beschreibung", neue_bezeichnung);
    		}
    		else {
    			job.add(k, jsonObject.get(k));
    		}
    	}
    	jsonObject = job.build();
    	
		response = given().contentType(APPLICATION_JSON)
				          .body(jsonObject.toString())
                          .auth()
                          .basic(username, password)
                          .put(ARTIKEL_PATH);
		
		// Then
		assertThat(response.getStatusCode(), is(HTTP_NO_CONTENT));
	}
	
	@Test
	public void invalidcreateArtikel() {
		LOGGER.debugf("BEGINN invalidartikelcreate");
		
		// Given
		final String bezeichnung = NEUE_BEZEICHNUNG;
		final String name = NEUER_NAME;
		final Float preis = NEUER_PREIS;
		final Long kategorie = NEUE_KATEGORIE_ID;
		final String username = USERNAME_ADMIN;
		final String password = PASSWORD_FALSCH;
		
		
		
		final JsonObject jsonObject = getJsonBuilderFactory().createObjectBuilder()
		             		          .add("beschreibung", bezeichnung)
		             		          .add("name", name)
		             		          .add("preis", preis)
		             		          .add("kategorie",getJsonBuilderFactory().createObjectBuilder().add("kategorieId",kategorie))
		             		          .build();

		// When
		final Response response = given().contentType(APPLICATION_JSON)
				                         .body(jsonObject.toString())
				                         .auth()
				                         .basic(username, password)
                                         .post(ARTIKEL_PATH);
		
		// Then
		assertThat(response.getStatusCode(), is(HTTP_UNAUTHORIZED));
		

		LOGGER.debugf("ENDE invalidartikelcreate");
	}
	
	@Test
	public void invalidrollcreateArtikel() {
		LOGGER.debugf("BEGINN invalidrollartikelcreate");
		
		// Given
		final String bezeichnung = NEUE_BEZEICHNUNG;
		final String name = NEUER_NAME;
		final Float preis = NEUER_PREIS;
		final Long kategorie = NEUE_KATEGORIE_ID;
		final String username = USERNAME;
		final String password = PASSWORD;
		
		
		
		final JsonObject jsonObject = getJsonBuilderFactory().createObjectBuilder()
		             		          .add("beschreibung", bezeichnung)
		             		          .add("name", name)
		             		          .add("preis", preis)
		             		          .add("kategorie",getJsonBuilderFactory().createObjectBuilder().add("kategorieId",kategorie))
		             		          .build();

		// When
		final Response response = given().contentType(APPLICATION_JSON)
				                         .body(jsonObject.toString())
				                         .auth()
				                         .basic(username, password)
                                         .post(ARTIKEL_PATH);
		
		// Then
		assertThat(response.getStatusCode(), is(HTTP_FORBIDDEN));
		

		LOGGER.debugf("ENDE invalidrollartikelcreate");
	}
	
	
}
