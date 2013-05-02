package de.shop.artikelverwaltung.rest;


import org.jboss.logging.Logger;

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

import org.jboss.resteasy.annotations.providers.jaxb.Wrapped;

import de.shop.artikelverwaltung.domain.Kategorie;
import de.shop.artikelverwaltung.service.KategorieService;


import de.shop.util.LocaleHelper;
import de.shop.util.Log;
import de.shop.util.NotFoundException;
import de.shop.util.Transactional;

@Path("/kategorien")
@Produces(APPLICATION_JSON)
@Consumes
@RequestScoped
@Transactional
@Log
public class KategorieResource {
	
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

	@Inject
	private KategorieService ks;
	
	@Inject
	private UriHelperKategorie uriHelperKategorie;
	
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
	@Wrapped(element = "kategorien") // RESTEasy, nicht Standard
	public Collection<Kategorie> findAlleKategorien() {
		final Collection<Kategorie> kategorien = ks.findAllKategorie();
		return kategorien;
	}
	
	@GET
	@Path("{id:[1-9][0-9]*}")
	public Kategorie findKategorieById(@PathParam("id") Long id,
			                           @Context UriInfo uriInfo,
			                           @Context HttpHeaders headers) {
		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
		// TODO locale anpassen
		final Kategorie kategorie = ks.findKategorieById(id, locale);
		if (kategorie == null) {
			// TODO msg passend zu locale
			final String msg = "Keine Kategorie gefunden mit der ID " + id;
			throw new NotFoundException(msg);
		}
		
		return kategorie;
	}
	
	@POST
	@Consumes(APPLICATION_JSON)
	@Produces
	public Response createKategorie(Kategorie kategorie, @Context UriInfo uriInfo, @Context HttpHeaders headers) {
		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
		kategorie = ks.createKategorie(kategorie, locale);
		LOGGER.debugf("Kategorie: {0}", kategorie);
		
		final URI KategorieUri = uriHelperKategorie.getUriKategorie(kategorie, uriInfo);
		return Response.created(KategorieUri).build();
	}
	
	@PUT
	@Consumes(APPLICATION_JSON)
	@Produces
	public void updateKategorie(Kategorie kategorie, @Context UriInfo uriInfo, @Context HttpHeaders headers) {
		// Vorhandene Kategorie ermitteln
		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
		final Kategorie origKategorie = ks.findKategorieById(kategorie.getKategorieId(), locale);
		if (origKategorie == null) {
			// TODO msg passend zu locale
			final String msg = "Keine Kategorie gefunden mit der ID " + kategorie.getKategorieId();
			throw new NotFoundException(msg);
		}
		LOGGER.debugf("Kategorie vorher: %s", origKategorie);
	
		// Daten des vorhandene Kategorie ueberschreiben
		origKategorie.setValues(kategorie);
		LOGGER.debugf("Kategorie nachher: %s", origKategorie);
		
		// Update durchfuehren
		kategorie = ks.updateKategorie(origKategorie, locale);
		if (kategorie == null) {
			// TODO msg passend zu locale
			final String msg = "Keine Kategorie gefunden mit der ID " + origKategorie.getKategorieId();
			throw new NotFoundException(msg);
		}
	}

}
