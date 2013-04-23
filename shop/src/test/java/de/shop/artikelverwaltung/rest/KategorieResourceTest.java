package de.shop.artikelverwaltung.rest;

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
import static de.shop.util.TestConstants.KATEGORIE_ID_FILE_PATH;
import static de.shop.util.TestConstants.KATEGORIE_ID_PATH_PARAM;
import static de.shop.util.TestConstants.KATEGORIE_ID_PATH;
import static de.shop.util.TestConstants.KATEGORIE_NAME_QUERY_PARAM;
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
		
		@Test
		public void findKategorieById() {
			LOGGER.debugf("BEGINN");
			
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
			
			LOGGER.debugf("ENDE");
		}
		
		@Test
		public void findKategorieByIdNichtVorhanden() {
			LOGGER.debugf("BEGINN");
			
			// Given
			final Long kategorieId = KATEGORIE_ID_NICHT_VORHANDEN;
			
			// When
			final Response response = given().header(ACCEPT, APPLICATION_JSON)
					                         .pathParameter(KATEGORIE_ID_PATH_PARAM, kategorieId)
	                                         .get(KATEGORIE_ID_PATH);

	    	// Then
	    	assertThat(response.getStatusCode(), is(HTTP_NOT_FOUND));
			LOGGER.debugf("ENDE");
		}
		
		@Test
		public void createKategorie() {
			LOGGER.debugf("BEGINN");
			
			// Given
			final String bezeichnung = NEUE_BEZEICHNUNG;
			
			
			final JsonObject jsonObject = getJsonBuilderFactory().createObjectBuilder()
			             		          .add("bezeichnung", bezeichnung)
			             		          .build();

			// When
			final Response response = given().contentType(APPLICATION_JSON)
					                         .body(jsonObject.toString())
	                                         .post(KATEGORIE_PATH);
			
			// Then
			assertThat(response.getStatusCode(), is(HTTP_CREATED));
			final String location = response.getHeader(LOCATION);
			final int startPos = location.lastIndexOf('/');
			final String idStr = location.substring(startPos + 1);
			final Long id = Long.valueOf(idStr);
			assertThat(id.longValue() > 0, is(true));

			LOGGER.debugf("ENDE");
		}
		
		public void updateKategorie() {
			
			LOGGER.debugf("BEGINN");
			
			//Given
			 
			final Long kategorieId = KATEGORIE_ID_VORHANDEN;
			final String neue_bezeichnung = NEUE_BEZEICHNUNG_UPDATE;
			
			// When
			Response response = given().header(ACCEPT, APPLICATION_JSON)
					                   .pathParameter(KATEGORIE_ID_PATH_PARAM, kategorieId)
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
		}
}
