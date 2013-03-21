package de.shop.kundenverwaltung.rest;

import static java.util.logging.Level.FINER;
import static java.util.logging.Level.FINEST;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import static javax.ws.rs.core.MediaType.TEXT_XML;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.jboss.resteasy.annotations.providers.jaxb.Wrapped;

import de.shop.kundenverwaltung.domain.Zahlungsinformation;
import de.shop.kundenverwaltung.service.ZahlungsinformationService;
import de.shop.util.Log;
import de.shop.util.NotFoundException;

@Path("/zahlungsinformationen")
@Produces({ APPLICATION_XML, TEXT_XML, APPLICATION_JSON })
@Consumes
@RequestScoped
@Log
public class ZahlungsinformationResource {
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

	@Inject
	private ZahlungsinformationService zs;
	
	@Inject
	private UriHelperZahlungsinformation uriHelperZahlungsinformation;

	@PostConstruct
	private void postConstruct() {
		LOGGER.log(FINER, "CDI-faehiges Bean {0} wurde erzeugt", this);
	}
	
	@PreDestroy
	private void preDestroy() {
		LOGGER.log(FINER, "CDI-faehiges Bean {0} wird geloescht", this);
	}
	
	@GET
	@Wrapped(element = "zahlungsinformationen") // RESTEasy, nicht Standard
	public Collection<Zahlungsinformation> findAlleZahlungsinformationen() {
		Collection<Zahlungsinformation> zahlungsinformationen = zs.findAllZahlungsinformation();
		return zahlungsinformationen;
	}
	
	@GET
	@Path("{id:[1-9][0-9]*}")
	public Zahlungsinformation findZahlungsinformationById(@PathParam("id") Long id,
			                           @Context UriInfo uriInfo,
			                           @Context HttpHeaders headers) {
		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
		final Zahlungsinformation zahlungsinformation = zs.findZahlungsinformationById(id, locale);
		if (zahlungsinformation == null) {
			final String msg = "Keine Zahlungsinformation gefunden mit der ID " + id;
			throw new NotFoundException(msg);
		}
		
		return zahlungsinformation;
	}
	
	@POST
	@Consumes({ APPLICATION_XML, TEXT_XML })
	@Produces
	public Response createZahlungsinformation(Zahlungsinformation zahlungsinformation,
			                                  @Context UriInfo uriInfo, @Context HttpHeaders headers) {
		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
		
		zahlungsinformation = zs.createZahlungsinformation(zahlungsinformation, locale);
		LOGGER.log(FINEST, "Zahlungsinformation: {0}", zahlungsinformation);
		
		final URI zahlungsinformationUri = uriHelperZahlungsinformation.
				                           getUriZahlungsinformation(zahlungsinformation, uriInfo);
		
		return Response.created(zahlungsinformationUri).build();
	}
	
	@PUT
	@Consumes({ APPLICATION_XML, TEXT_XML })
	@Produces
	public void updateZahlungsinformation(Zahlungsinformation zahlungsinformation, @Context UriInfo uriInfo,
			                              @Context HttpHeaders headers) {
		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
		Zahlungsinformation originaleZahlungsinformation 
		                    = zs.findZahlungsinformationById(zahlungsinformation.getZahlId(), locale);
		
		if (originaleZahlungsinformation == null) {
			final String msg = "Keine Zahlungsinformation gefunden mit der ID " + zahlungsinformation.getZahlId();
			throw new NotFoundException(msg);
		}
		
		LOGGER.log(FINEST, "Zahlungsinformation vorher: %s", originaleZahlungsinformation);
		
		// Daten des vorhandenen Kunden überschreiben
		originaleZahlungsinformation.setValues(zahlungsinformation);
		LOGGER.log(FINEST, "Zahlungsinformation nachher: %s", originaleZahlungsinformation);
		
		// Update durchführen
		zahlungsinformation = zs.updateZahlungsinformation(originaleZahlungsinformation, locale);
		if (zahlungsinformation == null) {
			final String msg = "Keine Zahlungsinformation gefunden mit der ID "
		                       + originaleZahlungsinformation.getZahlId();
			throw new NotFoundException(msg);
		}
	}
	
	
	
}
