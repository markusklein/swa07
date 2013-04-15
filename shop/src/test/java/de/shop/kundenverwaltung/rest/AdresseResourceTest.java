package de.shop.kundenverwaltung.rest;

import java.io.StringReader;
import java.lang.invoke.MethodHandles;

import javax.json.JsonObject;
import javax.json.JsonReader;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.jayway.restassured.response.Response;

import static com.jayway.restassured.RestAssured.given;
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
										 .pathParam("adresseId", adresseId)
										 .get("/adressen/{adresseId}");
		
		//Then
		assertThat(response.getStatusCode(), is(HTTP_OK));
		
		try (final JsonReader jsonReader =
	              getJsonReaderFactory().createReader(new StringReader(response.asString()))) {
			final JsonObject jsonObject = jsonReader.readObject();
			assertThat(jsonObject.getJsonNumber("adresseId").longValue(), is(adresseId.longValue()));
		}

		LOGGER.debugf("ENDE findAdresseById");
	}
}
