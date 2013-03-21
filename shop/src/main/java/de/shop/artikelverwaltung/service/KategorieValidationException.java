package de.shop.artikelverwaltung.service;

import java.util.Collection;
import javax.ejb.ApplicationException;
import javax.validation.ConstraintViolation;
import de.shop.artikelverwaltung.domain.Kategorie;
import de.shop.util.AbstractShopException;


	@ApplicationException(rollback = true)
	public class KategorieValidationException extends AbstractShopException {
		private static final long serialVersionUID = -9044101843087812932L;
		private final Kategorie kategorie;
		private final Collection<ConstraintViolation<Kategorie>> violations;
		
//		@Resource(lookup = "java:jboss/UserTransaction")
//		private UserTransaction trans;

		public KategorieValidationException(Kategorie kategorie,
				                        Collection<ConstraintViolation<Kategorie>> violations) {
			super("Ungueltige Kategorie: " + kategorie + ", Violations: " + violations);
			this.kategorie = kategorie;
			this.violations = violations;
		}
		
//		@PostConstruct
//		private void setRollbackOnly() {
//			try {
//				if (trans.getStatus() == STATUS_ACTIVE) {
//					trans.setRollbackOnly();
//				}
//			}
//			catch (IllegalStateException | SystemException e) {
//				throw new InternalError(e);
//			}
//		}

		
		public Kategorie getKategorie() {
			return kategorie;
		}

		public Collection<ConstraintViolation<Kategorie>> getViolations() {
			return violations;
		}
}

