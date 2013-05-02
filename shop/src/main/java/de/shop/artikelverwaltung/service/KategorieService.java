package de.shop.artikelverwaltung.service;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.groups.Default;
import de.shop.artikelverwaltung.domain.Kategorie;
import de.shop.util.IdGroup;
import de.shop.util.Log;
import de.shop.util.ValidatorProvider;
import org.jboss.logging.Logger;




@Log
public class KategorieService implements Serializable {

	
	private static final long serialVersionUID = -5650547909234333262L;

	@PersistenceContext
	private transient EntityManager em;
	
	@Inject
	private ValidatorProvider validationProvider;
	
	@Inject
	private transient Logger logger; 
	
	@PostConstruct
	private void postConstruct() {
		logger.debugf("CDI-faehiges Bean {0} wurde erzeugt", this);
	}
	
	@PreDestroy
	private void preDestroy() {
		logger.debugf("CDI-faehiges Bean {0} wird geloescht", this);
	}
	
	public List<Kategorie> findAllKategorie() {
		List<Kategorie> kategorie;
		
		kategorie = em.createNamedQuery(Kategorie.FIND_KATEGORIE, Kategorie.class)
					.getResultList();
		
		return kategorie;
	}
	
	public Kategorie findKategorieById(long id, Locale locale) {
		validateKategorieId(id, locale);
		final Kategorie kategorie = em.find(Kategorie.class, id);
		return kategorie;
	}
	
	public List<Kategorie> findKategorieByBezeichnung(String bezeichnung, Locale locale) {
		//TODO validateKategoriebezeichnung(bezeichnung, locale);
		if (bezeichnung == null || bezeichnung.isEmpty()) {
			
			return null;
		}
		
		final List<Kategorie> kategorie = em.createNamedQuery(Kategorie.KATEGORIE_BY_BEZEICHNUNG, Kategorie.class)
				                        .setParameter(Kategorie.PARAMETER_BEZEICHNUNG, "%" + bezeichnung + "%")
				                        .getResultList();
		return kategorie;
	}
	public Kategorie updateKategorie(Kategorie kategorie, Locale locale) {
		if (kategorie == null)
			return null;
		
		validateKategorie(kategorie, locale, Default.class);
		
		try {
			final Kategorie vorhandeneKategorie = em.find(Kategorie.class, kategorie.getKategorieId());
			
			if (vorhandeneKategorie.getKategorieId().longValue() != kategorie.getKategorieId().longValue())
				throw new KategorieExistsException(kategorie.getKategorieId());
		}
		catch (NoResultException e) {
			logger.debugf("Neue Kategorie");
		}
		em.detach(kategorie);
		em.merge(kategorie);
		return kategorie;
	}
	public Kategorie createKategorie(Kategorie kategorie, Locale locale) {
		if (kategorie == null)
			return kategorie;
		
		validateKategorie(kategorie, locale, Default.class);
		
		kategorie.setKategorieId(null);
		em.persist(kategorie);
		return kategorie;
	}
	private void validateKategorie(Kategorie kategorie, Locale locale, Class<?>... groups) {
		// Werden alle Constraints beim Einfuegen gewahrt?
		final Validator validator = validationProvider.getValidator(locale);
		
		final Set<ConstraintViolation<Kategorie>> violations = validator.validate(kategorie, groups);
		if (!violations.isEmpty()) {
			throw new KategorieValidationException(kategorie, violations);
		}
	}
	
	private void validateKategorieId(Long kategorieId, Locale locale) {
		final Validator validator = validationProvider.getValidator(locale);
		final Set<ConstraintViolation<Kategorie>> violations = validator.validateValue(Kategorie.class,
				                                                                           "kategorieId",
				                                                                           kategorieId,
				                                                                           IdGroup.class);
		if (!violations.isEmpty())
			throw new InvalidKategorieIdException(kategorieId, violations);
	}
}
	
	
	
		


