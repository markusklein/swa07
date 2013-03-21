package de.shop.kundenverwaltung.rest;

import java.net.URI;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import de.shop.kundenverwaltung.domain.Zahlungsinformation;
import de.shop.util.Log;

@ApplicationScoped
@Log
public class UriHelperZahlungsinformation {
	public URI getUriZahlungsinformation(Zahlungsinformation zahlungsinformation, UriInfo uriInfo) {
		final UriBuilder ub = uriInfo.getBaseUriBuilder()
		                             .path(ZahlungsinformationResource.class)
		                             .path(ZahlungsinformationResource.class, "findZahlungsinformationById");
		final URI zahlungsinformationUri = ub.build(zahlungsinformation.getZahlId());
		return zahlungsinformationUri;
	}
}
