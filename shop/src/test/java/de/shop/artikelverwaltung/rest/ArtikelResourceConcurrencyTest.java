package de.shop.artikelverwaltung.rest;

import static com.jayway.restassured.RestAssured.given;
import static de.shop.util.TestConstants.ACCEPT;
import static de.shop.util.TestConstants.ARTIKEL_ID_PATH;
import static de.shop.util.TestConstants.ARTIKEL_ID_PATH_PARAM;
import static de.shop.util.TestConstants.ARTIKEL_PATH;
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
import de.shop.util.ConcurrentUpdate;

@RunWith(Arquillian.class)
@FixMethodOrder(NAME_ASCENDING)
public class ArtikelResourceConcurrencyTest extends AbstractResourceTest {
	
		private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
		private static final Long ARTIKEL_ID_UPDATE = Long.valueOf(200);
		private static final String NEUER_NAME = "Testname";
		private static final String NEUER_NAME_2 = "Neuername";
		

		@Test
		public void updateUpdate() throws InterruptedException, ExecutionException {
			LOGGER.debugf("BEGINN UPDATE_UPDATE ARTIKEL");
			
			// Given
			final Long artikelId = ARTIKEL_ID_UPDATE;
	    	final String neuerName = NEUER_NAME;
	    	final String neuerName2 = NEUER_NAME_2;
	    	final String username = USERNAME_ADMIN;
			final String password = PASSWORD_ADMIN;
			
			// When
			Response response = given().header(ACCEPT, APPLICATION_JSON)
					                   .pathParameter(ARTIKEL_ID_PATH_PARAM, artikelId)
	                                   .get(ARTIKEL_ID_PATH);
			JsonObject jsonObject;
			try (final JsonReader jsonReader =
					              getJsonReaderFactory().createReader(new StringReader(response.asString()))) {
				jsonObject = jsonReader.readObject();
			}

	    	// Konkurrierendes Update
			// Aus den gelesenen JSON-Werten ein neues JSON-Objekt mit neuem Nachnamen bauen
	    	JsonObjectBuilder job = getJsonBuilderFactory().createObjectBuilder();
	    	Set<String> keys = jsonObject.keySet();
	    	for (String k : keys) {
	    		if ("name".equals(k)) {
	    			job.add("name", neuerName2);
	    		}
	    		else {
	    			job.add(k, jsonObject.get(k));
	    		}
	    	}
	    	final JsonObject jsonObject2 = job.build();
	    	final ConcurrentUpdate concurrentUpdate = new ConcurrentUpdate(jsonObject2, ARTIKEL_PATH,
	    			                                                       username, password);
	    	final ExecutorService executorService = Executors.newSingleThreadExecutor();
			final Future<Response> future = executorService.submit(concurrentUpdate);
			response = future.get();   // Warten bis der "parallele" Thread fertig ist
			assertThat(response.getStatusCode(), is(HTTP_NO_CONTENT));
			
	    	// Fehlschlagendes Update
			// Aus den gelesenen JSON-Werten ein neues JSON-Objekt mit neuem Nachnamen bauen
	    	job = getJsonBuilderFactory().createObjectBuilder();
	    	keys = jsonObject.keySet();
	    	for (String k : keys) {
	    		if ("name".equals(k)) {
	    			job.add("name", neuerName);
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
			assertThat(response.getStatusCode(), is(HTTP_CONFLICT));
			
			LOGGER.debugf("ENDE UPDATE_UPDATE ARTIKEL");
		}
}
