package de.shop.artikelverwaltung.rest;

import org.jboss.logging.Logger;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import static javax.ws.rs.core.MediaType.TEXT_XML;

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

import org.jboss.resteasy.annotations.providers.jaxb.Wrapped;

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.artikelverwaltung.service.ArtikelService;
import de.shop.util.LocaleHelper;
import de.shop.util.Log;
import de.shop.util.NotFoundException;


@Path("/artikel")
@Produces(APPLICATION_JSON)
@Consumes
@RequestScoped
@Log
public class ArtikelResource {
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

	@Inject
	private ArtikelService as;
	
	@Inject
	private UriHelperArtikel uriHelperArtikel;
	
	@Inject
	private LocaleHelper localeHelper;

	@PostConstruct
	private void postConstruct() {
		LOGGER.debugf("CDI-faehiges Bean {0} wurde erzeugt", this);
	}
	
	@PreDestroy
	private void preDestroy() {
		LOGGER.debugf("CDI-faehiges Bean {0} wird geloescht", this);
	}
	
	@GET
	@Wrapped(element = "artikel") // RESTEasy, nicht Standard
	public Collection<Artikel> findAlleArtikel() {
		Collection<Artikel> artikel = as.findAllArtikel();
		return artikel;
	}
	
	@GET
	@Path("{id:[1-9][0-9]*}")
	public Artikel findArtikelById(@PathParam("id") Long id,
			                           @Context UriInfo uriInfo,
			                           @Context HttpHeaders headers) {
		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
		// TODO locale anpassen
		final Artikel artikel = as.findArtikelById(id, locale);
		if (artikel == null) {
			// TODO msg passend zu locale
			final String msg = "Kein Artikel gefunden mit der ID " + id;
			throw new NotFoundException(msg);
		}
		
		return artikel;
	}
	@POST
	@Consumes(APPLICATION_JSON)
	@Produces(APPLICATION_JSON)
	public Response createArtikel(Artikel artikel, @Context UriInfo uriInfo, @Context HttpHeaders headers) {
		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
		artikel = as.createArtikel(artikel, locale);
		LOGGER.debugf("Artikel: {0}", artikel);
		
		final URI ArtikelUri = uriHelperArtikel.getUriArtikel(artikel, uriInfo);
		return Response.created(ArtikelUri).build();
	}
	
	@PUT
	@Consumes(APPLICATION_JSON)
	@Produces(APPLICATION_JSON)
	public void updateArtikel(Artikel artikel, @Context UriInfo uriInfo, @Context HttpHeaders headers) {
		// Vorhandenen Kunden ermitteln
		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
		Artikel origArtikel = as.findArtikelById(artikel.getArtikelId(), locale);
		if (origArtikel == null) {
			// TODO msg passend zu locale
			final String msg = "Keine Artikel gefunden mit der ID " + artikel.getArtikelId();
			throw new NotFoundException(msg);
		}
		LOGGER.debugf("Artikel vorher: %s", origArtikel);
	
		// Daten des vorhandenen Artikel ueberschreiben
		origArtikel.setValues(artikel);
		LOGGER.debugf("Artikel nachher: %s", origArtikel);
		
		// Update durchfuehren
		artikel = as.updateArtikel(origArtikel, locale);
		if (artikel == null) {
			// TODO msg passend zu locale
			final String msg = "Kein Artikel gefunden mit der ID " + origArtikel.getArtikelId();
			throw new NotFoundException(msg);
		}
	}
}
