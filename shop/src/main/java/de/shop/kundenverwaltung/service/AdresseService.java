package de.shop.kundenverwaltung.service;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Locale;
import java.util.Set;
//import java.util.Locale;
import java.util.logging.Logger;
import static java.util.logging.Level.FINER;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.groups.Default;

import de.shop.util.IdGroup;
import de.shop.util.ValidatorProvider;

import de.shop.kundenverwaltung.domain.Adresse;

public class AdresseService implements Serializable {
	private static final long serialVersionUID = 1650148163922645962L;
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	
	@PersistenceContext
	private transient EntityManager em;
	
	@Inject
	private ValidatorProvider validationProvider;
	
	@PostConstruct
	private void postConstruct() {
		LOGGER.log(FINER, "CDI-faehiges Bean {0} wurde erzeugt", this);
	}
	
	@PreDestroy
	private void preDestroy() {
		LOGGER.log(FINER, "CDI-faehiges Bean {0} wird geloescht", this);
	}
	
	public List<Adresse> findAlleAdressen() {
		List<Adresse> adressen;
		
		adressen = em.createNamedQuery(Adresse.FIND_ADRESSEN, Adresse.class)
					.getResultList();
		
		return adressen;
	}
	
	public Adresse findAdresseById(Long id, Locale locale) {
		validateAdresseId(id, locale);
		Adresse adresse = null;
		
		adresse = em.find(Adresse.class, id);
		
		return adresse;
	}
	
	public Adresse createAdresse(Adresse adresse, Locale locale) {
		if (adresse == null)
			return adresse;
		
		validateAdresse(adresse, locale, Default.class);
		
		adresse.setAdresseId(null);
		em.persist(adresse);
		return adresse;
	}
	
	public Adresse updateAdresse(Adresse adresse, Locale locale) {
		if (adresse == null)
			return null;
		
		validateAdresse(adresse, locale, Default.class);
		
		try {
			final Adresse vorhandeneAdresse = em.find(Adresse.class, adresse.getAdresseId());
			
			if (vorhandeneAdresse.getAdresseId().longValue() != adresse.getAdresseId().longValue())
				throw new AdresseExistsException(adresse.getAdresseId());
		}
		catch (NoResultException e) {
			LOGGER.finest("Neue Adresse");
		}
		em.merge(adresse);
		return adresse;
	}
	
	private void validateAdresse(Adresse adresse, Locale locale, Class<?>... groups) {
		// Werden alle Constraints beim Einfuegen gewahrt?
		final Validator validator = validationProvider.getValidator(locale);
		
		final Set<ConstraintViolation<Adresse>> violations = validator.validate(adresse, groups);
		if (!violations.isEmpty()) {
			throw new AdresseValidationException(adresse, violations);
		}
	}
	
	private void validateAdresseId(Long adresseId, Locale locale) {
		final Validator validator = validationProvider.getValidator(locale);
		final Set<ConstraintViolation<Adresse>> violations = validator.validateValue(Adresse.class,
				                                                                           "adresseId",
				                                                                           adresseId,
				                                                                           IdGroup.class);
		if (!violations.isEmpty())
			throw new InvalidAdresseIdException(adresseId, violations);
	}

}
