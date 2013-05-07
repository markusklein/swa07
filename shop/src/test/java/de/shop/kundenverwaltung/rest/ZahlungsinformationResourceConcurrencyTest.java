package de.shop.kundenverwaltung.rest;

import static com.jayway.restassured.RestAssured.given;
import static de.shop.util.TestConstants.ACCEPT;
import static de.shop.util.TestConstants.ZAHLUNGSINFORMATIONEN_ID_PATH;
import static de.shop.util.TestConstants.ZAHLUNGSINFORMATIONEN_ID_PATH_PARAM;
import static de.shop.util.TestConstants.ZAHLUNGSINFORMATIONEN_PATH;
import static java.net.HttpURLConnection.HTTP_CONFLICT;
import static java.net.HttpURLConnection.HTTP_NO_CONTENT;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.runners.MethodSorters.NAME_ASCENDING;
import java.io.StringReader;
import java.lang.invoke.MethodHandles;

import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.jboss.logging.Logger;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.jayway.restassured.response.Response;
import de.shop.util.AbstractResourceTest;
import de.shop.util.ConcurrentUpdate;


@RunWith(Arquillian.class)
@FixMethodOrder(NAME_ASCENDING)
public class ZahlungsinformationResourceConcurrencyTest extends AbstractResourceTest {

	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass());
	
	private static final Long ZAHLUNGSINFORMATION_ID_UPDATE = Long.valueOf(709);
	private static final String NEUER_KONTOINHABER = "Thomas Baier";
	private static final String NEUER_KONTOINHABER_2 = "Frank Tausch";
	//private static final Long ZAHLUNGSINFORMATION_ID_DELETE1 = Long.valueOf(704);
	//private static final Long ZAHLUNGSINFORMATION_ID_DELETE2 = Long.valueOf(705);
		
	@Test
	public void updateUpdate() throws InterruptedException, ExecutionException {
		LOGGER.debugf("BEGINN");
		
		// Given
		final Long zahlId = ZAHLUNGSINFORMATION_ID_UPDATE;
    	final String neuerKontoinhaber = NEUER_KONTOINHABER;
    	final String neuerKontoinhaber2 = NEUER_KONTOINHABER_2;
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

    	// Konkurrierendes Update
		// Aus den gelesenen JSON-Werten ein neues JSON-Objekt mit neuem Nachnamen bauen
    	final JsonObjectBuilder job2 = getJsonBuilderFactory().createObjectBuilder();
    	Set<String> keys = jsonObject.keySet();
    	for (String k : keys) {
    		if ("kontoinhaber".equals(k)) {
    			job2.add("kontoinhaber", neuerKontoinhaber2);
    		}
    		else {
    			job2.add(k, jsonObject.get(k));
    		}
    	}
    	final JsonObject jsonObject2 = job2.build();
    	final ConcurrentUpdate concurrentUpdate = new ConcurrentUpdate(jsonObject2, ZAHLUNGSINFORMATIONEN_PATH,
    			                                                       username, password);
    	final ExecutorService executorService = Executors.newSingleThreadExecutor();
		final Future<Response> future = executorService.submit(concurrentUpdate);
		response = future.get();   // Warten bis der "parallele" Thread fertig ist
		assertThat(response.getStatusCode(), is(HTTP_NO_CONTENT));
		
    	// Fehlschlagendes Update
		// Aus den gelesenen JSON-Werten ein neues JSON-Objekt mit neuem Nachnamen bauen
		final JsonObjectBuilder job1 = getJsonBuilderFactory().createObjectBuilder();
    	keys = jsonObject.keySet();
    	for (String k : keys) {
    		if ("kontoinhaber".equals(k)) {
    			job1.add("kontoinhaber", neuerKontoinhaber);
    		}
    		else {
    			job1.add(k, jsonObject.get(k));
    		}
    	}
    	jsonObject = job1.build();
		response = given().contentType(APPLICATION_JSON)
				          .body(jsonObject.toString())
		                  .auth()
		                  .basic(username, password)
		                  .put(ZAHLUNGSINFORMATIONEN_PATH);
    	
		// Then
		assertThat(response.getStatusCode(), is(HTTP_CONFLICT));
		
		LOGGER.debugf("ENDE");
	}
	
	
	
}
