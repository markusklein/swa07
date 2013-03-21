package de.shop.kundenverwaltung.service;

import java.util.Collection;

import javax.ejb.ApplicationException;
import javax.validation.ConstraintViolation;

import de.shop.kundenverwaltung.domain.Adresse;
import de.shop.util.AbstractShopException;

@ApplicationException(rollback = true)
public class AdresseValidationException extends AbstractShopException {
	private static final long serialVersionUID = -9044101843087812932L;
	private final Adresse adresse;
	private final Collection<ConstraintViolation<Adresse>> violations;
	
//	@Resource(lookup = "java:jboss/UserTransaction")
//	private UserTransaction trans;

	public AdresseValidationException(Adresse adresse,
			                        Collection<ConstraintViolation<Adresse>> violations) {
		super("Ungueltige Adresse: " + adresse + ", Violations: " + violations);
		this.adresse = adresse;
		this.violations = violations;
	}
	
//	@PostConstruct
//	private void setRollbackOnly() {
//		try {
//			if (trans.getStatus() == STATUS_ACTIVE) {
//				trans.setRollbackOnly();
//			}
//		}
//		catch (IllegalStateException | SystemException e) {
//			throw new InternalError(e);
//		}
//	}

	
	public Adresse getAdresse() {
		return adresse;
	}

	public Collection<ConstraintViolation<Adresse>> getViolations() {
		return violations;
	}
}
