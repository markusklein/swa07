package de.shop.kundenverwaltung.service;

import java.util.Collection;

import javax.ejb.ApplicationException;
import javax.validation.ConstraintViolation;

import de.shop.kundenverwaltung.domain.Zahlungsinformation;
import de.shop.util.AbstractShopException;

@ApplicationException(rollback = true)
public class InvalidZahlungsinformationIdException extends AbstractShopException {
	
	private static final long serialVersionUID = 798326775738471204L;
	private final Long zahlId;
	private final Collection<ConstraintViolation<Zahlungsinformation>> violations;
	
	public InvalidZahlungsinformationIdException(Long zahlId,
												 Collection<ConstraintViolation<Zahlungsinformation>> violations) {
		super("Ungueltige Zahlungsinformation-ID: " + zahlId + ", Violations: " + violations);
		this.zahlId = zahlId;
		this.violations = violations;
	}

	public Long getZahlId() {
		return zahlId;
	}

	public Collection<ConstraintViolation<Zahlungsinformation>> getViolations() {
		return violations;
	}
}
