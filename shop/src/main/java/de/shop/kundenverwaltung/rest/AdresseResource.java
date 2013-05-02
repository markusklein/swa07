package de.shop.kundenverwaltung.rest;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

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

import org.jboss.logging.Logger;
import org.jboss.resteasy.annotations.providers.jaxb.Wrapped;

import de.shop.kundenverwaltung.domain.Adresse;
import de.shop.kundenverwaltung.service.AdresseService;
import de.shop.util.LocaleHelper;
import de.shop.util.Log;
import de.shop.util.NotFoundException;
import de.shop.util.Transactional;

@Path("/adressen")
@Produces(APPLICATION_JSON)
@Consumes
@RequestScoped
@Transactional
@Log
public class AdresseResource {
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass());

	@Context
	private UriInfo uriInfo;
	
	@Context
    private HttpHeaders headers;
	
	@Inject
	private AdresseService as;
	
	@Inject
	private UriHelperAdresse uriHelperAdresse;
	
	@Inject
	private LocaleHelper localeHelper;

	@PostConstruct
	private void postConstruct() {
		LOGGER.debugf("CDI-faehiges Bean %s wurde erzeugt", this);
	}
	
	@PreDestroy
	private void preDestroy() {
		LOGGER.debugf("CDI-faehiges Bean %s wird geloescht", this);
	}
	
	@GET
	@Wrapped(element = "adressen") // RESTEasy, nicht Standard
	public Collection<Adresse> findAlleAdressen() {
		final Collection<Adresse> adressen = as.findAlleAdressen();
		return adressen;
	}
	
	@GET
	@Path("{id:[1-9][0-9]*}")
	public Adresse findAdresseById(@PathParam("id") Long id) {
		final Locale locale = localeHelper.getLocale(headers);
		final Adresse adresse = as.findAdresseById(id, locale);
		if (adresse == null) {
			// TODO msg passend zu locale
			final String msg = "Keine Adresse gefunden mit der ID " + id;
			throw new NotFoundException(msg);
		}
		
		return adresse;
	}
	
	@POST
	@Consumes(APPLICATION_JSON)
	@Produces
	public Response createAdresse(Adresse adresse, @Context UriInfo uriInfo, @Context HttpHeaders headers) {
		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
		adresse = as.createAdresse(adresse, locale);
		LOGGER.trace(adresse);
		
		final URI adresseUri = uriHelperAdresse.getUriAdresse(adresse, uriInfo);
		return Response.created(adresseUri).build();
	}
	
	@PUT
	@Consumes(APPLICATION_JSON)
	@Produces
	public void updateAdresse(Adresse adresse, @Context UriInfo uriInfo, @Context HttpHeaders headers) {
		// Vorhandene Adressen ermitteln
		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
		final Adresse origAdresse = as.findAdresseById(adresse.getAdresseId(), locale);
		if (origAdresse == null) {
			// TODO msg passend zu locale
			final String msg = "Keine Adresse gefunden mit der ID " + adresse.getAdresseId();
			throw new NotFoundException(msg);
		}
		LOGGER.trace(origAdresse);
	
		// Daten der vorhandenen Adresse ueberschreiben
		origAdresse.setValues(adresse);
		LOGGER.trace(origAdresse);
		
		// Update durchfuehren
		adresse = as.updateAdresse(origAdresse, locale);
		if (adresse == null) {
			// TODO msg passend zu locale
			final String msg = "Keine Adresse gefunden mit der ID " + origAdresse.getAdresseId();
			throw new NotFoundException(msg);
		}
	}
}
