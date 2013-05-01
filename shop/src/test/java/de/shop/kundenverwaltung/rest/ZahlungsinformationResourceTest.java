package de.shop.kundenverwaltung.rest;

import static com.jayway.restassured.RestAssured.given;
import static de.shop.util.TestConstants.ACCEPT;
import static de.shop.util.TestConstants.BASEPATH;
import static de.shop.util.TestConstants.BASEURI;
import static de.shop.util.TestConstants.ZAHLUNGSINFORMATIONEN_ID_PATH;
import static de.shop.util.TestConstants.ZAHLUNGSINFORMATIONEN_ID_PATH_PARAM;
import static de.shop.util.TestConstants.ZAHLUNGSINFORMATIONEN_PATH;
import static de.shop.util.TestConstants.ZAHLUNGSINFORMATIONEN_URI;
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
import java.security.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.jboss.logging.Logger;

import javax.inject.Inject;
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

import de.shop.kundenverwaltung.domain.Zahlungsinformation;
import de.shop.util.AbstractResourceTest;



@RunWith(Arquillian.class)
@FixMethodOrder(NAME_ASCENDING)
public class ZahlungsinformationResourceTest extends AbstractResourceTest {
	
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	private static final Long ZAHLUNGSINFORMATION_ID_VORHANDEN = Long.valueOf(700);
	private static final Long ZAHLUNGSINFORMATION_ID_NICHT_VORHANDEN = Long.valueOf(2333);
	
	private static final String KONTOINHABER_NEU = "Morgan Freeman";
	private static final Long KONTONUMMER_NEU = Long.valueOf(9191221);
	private static final Long BLZ_NEU = Long.valueOf(66650081);
	private static final String KREDITINSTITUT_NEU = "Sparkasse Chemnitz";
	private static final String IBAN_NEU ="DE54666500859995054284";
	private static final String SWIFT_NEU ="PZHSDE66YYY";
	
	private static final String KONTOINHABER_UPDATE = "Dennis Santos";
	
	@Test
	public void validate() {
		assertThat(true, is(true));
	}
	
	@Test
	public void findZahlungsinformationById() {
		LOGGER.debugf("Beginn");
		
		// Given
		final Long zahlId = ZAHLUNGSINFORMATION_ID_VORHANDEN;
		
		// When
		final Response response = given().header(ACCEPT, APPLICATION_JSON)
				                         .pathParameter(ZAHLUNGSINFORMATIONEN_ID_PATH_PARAM, zahlId)                       
				                         .get(ZAHLUNGSINFORMATIONEN_ID_PATH);

		// Then
		assertThat(response.getStatusCode(), is(HTTP_OK));
		
		try (final JsonReader jsonReader =
				              getJsonReaderFactory().createReader(new StringReader(response.asString()))) {
			final JsonObject jsonObject = jsonReader.readObject();
			assertThat(jsonObject.getJsonNumber("zahlId").longValue(), is(zahlId.longValue()));
		}
		
		LOGGER.debugf("ENDE");
	}
	
	@Test
	public void findZahlungsinformationByIdNichtVorhanden() {
		LOGGER.debugf("BEGINN");
		
		// Given
		final Long zahlId = ZAHLUNGSINFORMATION_ID_NICHT_VORHANDEN;
		
		// When
		final Response response = given().header(ACCEPT, APPLICATION_JSON)
				                         .pathParameter(ZAHLUNGSINFORMATIONEN_ID_PATH_PARAM, zahlId)
                                         .get(ZAHLUNGSINFORMATIONEN_ID_PATH);

    	// Then
    	assertThat(response.getStatusCode(), is(HTTP_NOT_FOUND));
		LOGGER.debugf("ENDE");
	}
	
	@Test
	public void createZahlungsinformation() {
		LOGGER.debugf("BEGINN");
		
		// Given
		final String kontoinhaber = KONTOINHABER_NEU;
		final Long kontonummer = KONTONUMMER_NEU;
		final Long blz = BLZ_NEU;
		final String kreditinstitut = KREDITINSTITUT_NEU;
		final String iban = IBAN_NEU;
		final String swift = SWIFT_NEU;
		
		
		final String username = USERNAME_ADMIN;
		final String password = PASSWORD_ADMIN;
		
		final JsonObject jsonObject =  getJsonBuilderFactory().createObjectBuilder()
		             		          .add("kontoinhaber", kontoinhaber)
		             		          .add("kontonummer", kontonummer)
		             		          .add("blz", blz)
		             		          .add("kreditinstitut", kreditinstitut)
		             		          .add("iban", iban)
		             		          .add("swift", swift)
		             		          .build();

		// When
		final Response response = given().contentType(APPLICATION_JSON)
				                         .body(jsonObject.toString())
                                         .auth()
                                         .basic(username, password)
                                         .post(ZAHLUNGSINFORMATIONEN_PATH);
		
		// Then
		assertThat(response.getStatusCode(), is(HTTP_CREATED));
		final String location = response.getHeader(LOCATION);
		final int startPos = location.lastIndexOf('/');
		final String idStr = location.substring(startPos + 1);
		final Long id = Long.valueOf(idStr);
		assertThat(id.longValue() > 0, is(true));

		LOGGER.debugf("ENDE");
	}
	
	@Test
	public void updateZahlungsinformation() {
		LOGGER.debugf("BEGINN");
		
		// Given
		final Long zahlId = ZAHLUNGSINFORMATION_ID_VORHANDEN;
		final String neuerKontoinhaber = KONTOINHABER_UPDATE;
		final String username = USERNAME_ADMIN;
		final String password = PASSWORD_ADMIN;
		
		// When
		Response response = given().header(ACCEPT, APPLICATION_JSON)
				                   .pathParameter(ZAHLUNGSINFORMATIONEN_ID_PATH_PARAM, zahlId)
                                   .get(ZAHLUNGSINFORMATIONEN_ID_PATH);
		
		JsonObject jsonObject;
		try (final JsonReader jsonReader =
				              getJsonReaderFactory().createReader(new StringReader(response.asString()))) {
			jsonObject = jsonReader.readObject();
		}
    	assertThat(jsonObject.getJsonNumber("zahlId").longValue(), is(zahlId.longValue()));
    	
    	// Aus den gelesenen JSON-Werten ein neues JSON-Objekt mit neuem Nachnamen bauen
    	final JsonObjectBuilder job = getJsonBuilderFactory().createObjectBuilder();
    	final Set<String> keys = jsonObject.keySet();
    	for (String k : keys) {
    		if ("kontoinhaber".equals(k)) {
    			job.add("kontoinhaber", neuerKontoinhaber);
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
                          .put(ZAHLUNGSINFORMATIONEN_PATH);
		
		// Then
		assertThat(response.getStatusCode(), is(HTTP_NO_CONTENT));
   	}
	
}
