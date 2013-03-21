package de.shop.kundenverwaltung.service;

import java.util.Collection;

import javax.ejb.ApplicationException;
import javax.validation.ConstraintViolation;

import de.shop.kundenverwaltung.domain.Zahlungsinformation;
import de.shop.util.AbstractShopException;

@ApplicationException(rollback = true)
public class ZahlungsinformationValidationException extends AbstractShopException {

private static final long serialVersionUID = -4361276098804240272L;
private final Zahlungsinformation zahlungsinformation;
private final Collection<ConstraintViolation<Zahlungsinformation>> violations;

//@Resource(lookup = "java:jboss/UserTransaction")
//private UserTransaction trans;

public ZahlungsinformationValidationException(Zahlungsinformation zahlungsinformation
											 , Collection<ConstraintViolation<Zahlungsinformation>> violations) {
	super("Ungueltige Zahlungsinformation: " + zahlungsinformation + ", Violations: " + violations);
	this.zahlungsinformation = zahlungsinformation;
	this.violations = violations;
	
}

//@PostConstruct
//private void setRollbackOnly() {
//	try {
//		if (trans.getStatus() == STATUS_ACTIVE) {
//			trans.setRollbackOnly();
//		}
//	}
//	catch (IllegalStateException | SystemException e) {
//		throw new InternalError(e);
//	}
//}

public Zahlungsinformation getZahlungsinformation() {
	return zahlungsinformation;
}

public Collection<ConstraintViolation<Zahlungsinformation>> getViolations() {
	return violations;
}

	
}
