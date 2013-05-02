package de.shop.bestellverwaltung.service;

import java.util.Collection;

import javax.ejb.ApplicationException;
import javax.validation.ConstraintViolation;

import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.util.AbstractShopException;

@ApplicationException(rollback = true)
public class InvalidBestellungIdException extends AbstractShopException {
	private static final long serialVersionUID = -8973151010781329074L;
	
	private final Long bestellungId;
	private final Collection<ConstraintViolation<Bestellung>> violations;
	
	public InvalidBestellungIdException(Long bestellungId, Collection<ConstraintViolation<Bestellung>> violations) {
		super("Ungueltige Bestellung-ID: " + bestellungId + ", Violations: " + violations);
		this.bestellungId = bestellungId;
		this.violations = violations;
	}

	public Long getBestellungId() {
		return bestellungId;
	}

	public Collection<ConstraintViolation<Bestellung>> getViolations() {
		return violations;
	}
}
