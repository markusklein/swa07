package de.shop.artikelverwaltung.service;


import java.util.Collection;

import javax.ejb.ApplicationException;
import javax.validation.ConstraintViolation;

import de.shop.artikelverwaltung.domain.Kategorie;
import de.shop.util.AbstractShopException;

@ApplicationException(rollback = true)
public class InvalidKategorieIdException extends AbstractShopException {
	private static final long serialVersionUID = -5500490609340459670L;
	private final Long kategorieId;
	private final Collection<ConstraintViolation<Kategorie>> violations;
	
	public InvalidKategorieIdException(Long kategorieId, Collection<ConstraintViolation<Kategorie>> violations) {
		super("Ungueltige Kategorie-ID: " + kategorieId + ", Violations: " + violations);
		this.kategorieId = kategorieId;
		this.violations = violations;
	}

	public Long getKategorieId() {
		return kategorieId;
	}

	public Collection<ConstraintViolation<Kategorie>> getViolations() {
		return violations;
	}
}
