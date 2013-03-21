package de.shop.kundenverwaltung.service;

import java.util.Collection;

import javax.ejb.ApplicationException;
import javax.validation.ConstraintViolation;

import de.shop.kundenverwaltung.domain.Adresse;
import de.shop.util.AbstractShopException;

@ApplicationException(rollback = true)
public class InvalidAdresseIdException extends AbstractShopException {
	private static final long serialVersionUID = -5500490609340459670L;
	private final Long adresseId;
	private final Collection<ConstraintViolation<Adresse>> violations;
	
	public InvalidAdresseIdException(Long adresseId, Collection<ConstraintViolation<Adresse>> violations) {
		super("Ungueltige Adresse-ID: " + adresseId + ", Violations: " + violations);
		this.adresseId = adresseId;
		this.violations = violations;
	}

	public Long getAdresseId() {
		return adresseId;
	}

	public Collection<ConstraintViolation<Adresse>> getViolations() {
		return violations;
	}
}
