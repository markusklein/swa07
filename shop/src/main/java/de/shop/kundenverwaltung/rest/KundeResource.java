package de.shop.kundenverwaltung.rest;

import static java.util.logging.Level.FINER;
import static java.util.logging.Level.FINEST;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import de.shop.bestellverwaltung.domain.Bestellung;
//import de.shop.bestellverwaltung.rest.UriHelperBestellung;
import de.shop.bestellverwaltung.service.BestellungService;
import de.shop.kundenverwaltung.domain.Adresse;
import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.kundenverwaltung.domain.Zahlungsinformation;
import de.shop.kundenverwaltung.service.KundeService;
import de.shop.kundenverwaltung.service.KundeService.FetchType;
import de.shop.util.Log;
import de.shop.util.NotFoundException;
import de.shop.util.Transactional;

@Path("/kunden")
@Produces(APPLICATION_JSON)
@Consumes
@RequestScoped
@Transactional
@Log
public class KundeResource {
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	private static final String VERSION = "1.0";
	
	@Inject
	private KundeService ks;
	
	@Inject
	private BestellungService bs;
	
	@Inject
	private UriHelperKunde uriHelperKunde;

	
	@PostConstruct
	private void postConstruct() {
		LOGGER.log(FINER, "CDI-faehiges Bean {0} wurde erzeugt", this);
	}
	
	@PreDestroy
	private void preDestroy() {
		LOGGER.log(FINER, "CDI-faehiges Bean {0} wird geloescht", this);
	}
	
	@GET
	@Produces(APPLICATION_JSON)
	@Path("version")
	public String getVersion() {
		return VERSION;
	}
	
	/**
	 * Mit der URL /kunden/{id} einen Kunden ermitteln
	 * @param id ID des Kunden
	 * @return Objekt mit Kundendaten, falls die ID vorhanden ist
	 */
	@GET
	@Path("{id:[1-9][0-9]*}")
//	@Formatted    // XML formatieren, d.h. Einruecken und Zeilenumbruch
	public Kunde findKundeById(@PathParam("id") Long id,
			                           @Context UriInfo uriInfo,
			                           @Context HttpHeaders headers) {
		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
		final Kunde kunde = ks.findKundeById(id, FetchType.NUR_KUNDE, locale);
		if (kunde == null) {
			// TODO msg passend zu locale
			final String msg = "Kein Kunde gefunden mit der ID " + id;
			throw new NotFoundException(msg);
		}
	
		// URLs innerhalb des gefundenen Kunden anpassen
		uriHelperKunde.updateUriKunde(kunde, uriInfo);
		
		return kunde;
	}
	

	/**
	 * Mit der URL /kunden werden alle Kunden ermittelt oder
	 * mit kundenverwaltung/kunden?nachname=... diejenigen mit einem bestimmten Nachnamen.
	 * @return Collection mit den gefundenen Kundendaten
	 */
	@GET
	public Collection<Kunde> findKundenByNachname(@QueryParam("nachname") @DefaultValue("") String nachname,
			                                              @Context UriInfo uriInfo,
			                                              @Context HttpHeaders headers) {
		Collection<Kunde> kunden = null;
		if ("".equals(nachname)) {
			kunden = ks.findAllKunden(FetchType.NUR_KUNDE, null);
			if (kunden.isEmpty()) {
				final String msg = "Keine Kunden vorhanden";
				throw new NotFoundException(msg);
			}
		}
		else {
			final List<Locale> locales = headers.getAcceptableLanguages();
			final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
			kunden = ks.findKundenByNachname(nachname, FetchType.NUR_KUNDE, locale);
			if (kunden.isEmpty()) {
				final String msg = "Kein Kunde gefunden mit Nachname " + nachname;
				throw new NotFoundException(msg);
			}
		}
		
		// URLs innerhalb der gefundenen Kunden anpassen
		for (Kunde kunde : kunden) {
			uriHelperKunde.updateUriKunde(kunde, uriInfo);
		}
		
		// Konvertierung in eigene Collection-Klasse wg. Wurzelelement
		//final KundeCollection kundeColl = new KundeCollection(kunden);
		
		return kunden;
	}
	
	
	@GET
	@Path("/prefix/id/{id:[1-9][0-9]*}")
	public Collection<Long> findIdsByPrefix(@PathParam("id") String idPrefix, @Context UriInfo uriInfo) {
		final Collection<Long> ids = ks.findIdsByPrefix(idPrefix);
//		final Collection<RestLongWrapper> result = new ArrayList<>(ids.size());
//		for (Long id : ids) {
//			result.add(new RestLongWrapper(id));
//		}
		return ids;
	}
	
	@GET
	@Path("/prefix/nachname/{nachname}")
	public Collection<String> findNachnamenByPrefix(@PathParam("nachname") String nachnamePrefix,
			                                                   @Context UriInfo uriInfo) {
		final Collection<String> nachnamen = ks.findNachnamenByPrefix(nachnamePrefix);
//		final Collection<RestStringWrapper> wrapperColl = new ArrayList<>(nachnamen.size());
//		for (String n : nachnamen) {
//			wrapperColl.add(new RestStringWrapper(n));
//		}
		return nachnamen;
	}

	
	/**
	 * Mit der URL /kunden/{id}/bestellungen die Bestellungen zu eine Kunden ermitteln
	 * @param kundeId ID des Kunden
	 * @return Objekt mit Bestellungsdaten, falls die ID vorhanden ist
	 */
	@GET
	@Path("{id:[1-9][0-9]*}/bestellungen")
	public Collection<Bestellung> findBestellungenByKundeId(@PathParam("id") Long kundeId,  @Context UriInfo uriInfo) {
		final Collection<Bestellung> bestellungen = bs.findBestellungenByKunde(kundeId);
		if (bestellungen.isEmpty()) {
			final String msg = "Kein Bestellungen zur " + kundeId + " gefunden.";
			throw new NotFoundException(msg);
		}
		
		
		return bestellungen;
	}

	@GET
	@Path("{id:[1-9][0-9]*}/bestellungenIds")
	public Collection<Long> findBestellungenIdsByKundeId(@PathParam("id") Long kundeId,  @Context UriInfo uriInfo) {
		final Collection<Bestellung> bestellungen = bs.findBestellungenByKunde(kundeId);
		if (bestellungen.isEmpty()) {
			final String msg = "Kein Bestellungen zur " + kundeId + " gefunden.";
			throw new NotFoundException(msg);
		}
		
		final int anzahl = bestellungen.size();
		final Collection<Long> bestellungenIds = new ArrayList<>(anzahl);
		for (Bestellung bestellung : bestellungen) {
			bestellungenIds.add(bestellung.getId());
		}
		
		return bestellungenIds;
	}


	/**
	 * Mit der URL /kunden einen Kunden per POST anlegen.
	 * @param kunde neuer Kunde
	 * @return Response-Objekt mit URL des neuen Privatkunden
	 */
	@POST
	@Consumes(APPLICATION_JSON)
	@Produces
	public Response createKunde(Kunde kunde, @Context UriInfo uriInfo, @Context HttpHeaders headers) {
		final Adresse lieferadresse = kunde.getLieferadresse();
		final Adresse rechnungsadresse = kunde.getRechnungsadresse();
		final Zahlungsinformation zahlungsinf = kunde.getZahlungsinformation();
		
		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
		
		kunde = ks.createKunde(kunde, zahlungsinf, lieferadresse, rechnungsadresse, locale);
		LOGGER.log(FINEST, "Kunde: {0}", kunde);
		
		final URI kundeUri = uriHelperKunde.getUriKunde(kunde, uriInfo);
		return Response.created(kundeUri).build();
	}
	
	
	/**
	 * Mit der URL /kunden einen Kunden per PUT aktualisieren
	 * @param kunde zu aktualisierende Daten des Kunden
	 */
	@PUT
	@Consumes(APPLICATION_JSON)
	@Produces
	public void updateKunde(Kunde kunde, @Context UriInfo uriInfo, @Context HttpHeaders headers) {
		// Vorhandenen Kunden ermitteln
		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
		Kunde origKunde = ks.findKundeById(kunde.getKundeId(), FetchType.MIT_ADRESSE_UND_ZAHLUNGSINFORMATION, locale);
		if (origKunde == null) {
			// TODO msg passend zu locale
			final String msg = "Kunde mit ID " + kunde.getKundeId() + "konnte nicht gefunden werden.";
			throw new NotFoundException(msg);
		}
		LOGGER.log(FINEST, "Kunde vorher: %s", origKunde);
	
		// Daten des vorhandenen Kunden ueberschreiben
		origKunde.setValues(kunde);
		LOGGER.log(FINEST, "Kunde nachher: %s", origKunde);
		
		// Update durchfuehren
		kunde = ks.updateKunde(origKunde, locale);
		if (kunde == null) {
			// TODO msg passend zu locale
			final String msg = "Kein Kunde gefunden mit der ID " + origKunde.getKundeId();
			throw new NotFoundException(msg);
		}
	}
	
}
