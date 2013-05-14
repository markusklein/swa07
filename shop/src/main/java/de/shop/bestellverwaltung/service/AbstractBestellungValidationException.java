package de.shop.bestellverwaltung.service;
import java.util.Collection;

import javax.validation.ConstraintViolation;

import de.shop.bestellverwaltung.domain.Bestellung;

public class AbstractBestellungValidationException extends AbstractBestellungServiceException {
	private static final long serialVersionUID = 4510476102703346108L;
	private final Collection<ConstraintViolation<Bestellung>> violations;
	
	public AbstractBestellungValidationException(Collection<ConstraintViolation<Bestellung>> violations) {
		super("Violations: " + violations);
		this.violations = violations;
	}
	
	public Collection<ConstraintViolation<Bestellung>> getViolations() {
		return violations;
	}
}
