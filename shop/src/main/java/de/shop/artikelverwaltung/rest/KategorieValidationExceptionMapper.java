package de.shop.artikelverwaltung.rest;

import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static javax.ws.rs.core.Response.Status.CONFLICT;

import java.util.Collection;

import javax.enterprise.context.ApplicationScoped;
import javax.validation.ConstraintViolation;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import de.shop.artikelverwaltung.domain.Kategorie;
import de.shop.artikelverwaltung.service.KategorieValidationException;

@Provider
@ApplicationScoped
public class KategorieValidationExceptionMapper implements ExceptionMapper<KategorieValidationException> { 
	
	@Override
	public Response toResponse(KategorieValidationException e) {
		final Collection<ConstraintViolation<Kategorie>> violations = e.getViolations();
		final StringBuilder sb = new StringBuilder();
		for (ConstraintViolation<Kategorie> v : violations) {
			sb.append(v.getMessage());
			sb.append(" ");
		}
		
		final String responseStr = sb.toString();
		final Response response = Response.status(CONFLICT)
		                                  .type(TEXT_PLAIN)
		                                  .entity(responseStr)
		                                  .build();
		return response;
	}
}
