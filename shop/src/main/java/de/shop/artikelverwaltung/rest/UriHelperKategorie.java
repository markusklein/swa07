package de.shop.artikelverwaltung.rest;

import java.net.URI;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import de.shop.artikelverwaltung.domain.Kategorie;
import de.shop.util.Log;

@ApplicationScoped
@Log
public class UriHelperKategorie {
	public URI getUriKategorie(Kategorie kategorie, UriInfo uriInfo) {
		final UriBuilder ub = uriInfo.getBaseUriBuilder()
		                             .path(KategorieResource.class)
		                             .path(KategorieResource.class, "findKategorieById");
		final URI kategorieUri = ub.build(kategorie.getKategorieId());
		return kategorieUri;
	}
}
