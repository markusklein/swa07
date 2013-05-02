package de.shop.bestellverwaltung.service;

import static de.shop.util.Constants.KEINE_ID;
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

import org.jboss.logging.Logger;
import javax.enterprise.event.Event;


import de.shop.bestellverwaltung.domain.Bestellposition;
import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.kundenverwaltung.service.KundeService;
import de.shop.kundenverwaltung.service.KundeService.FetchType;
import de.shop.util.IdGroup;
import de.shop.util.TechnicalDate;
import de.shop.util.ValidatorProvider;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.groups.Default;

public class BestellungService implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8088898369932459648L;
	
	public enum BestellungFetchType {
		NUR_BESTELLUNG,
		MIT_BESTELLPOSITIONEN
	}

	@PersistenceContext
	private transient EntityManager em;
	
	@Inject
	private KundeService ks;
	
	@Inject
	private ValidatorProvider validationProvider;
	
	@Inject
	private transient Logger logger; 
	
	@Inject
	@NeueBestellung
	private transient Event<Bestellung> event;
	
	@PostConstruct
	private void postConstruct() {
		logger.debugf("CDI-faehiges Bean {0} wurde erzeugt", this);
	}
	
	@PreDestroy
	private void preDestroy() {
		logger.debugf("CDI-faehiges Bean {0} wird geloescht", this);
	}
	
	public Bestellung findBestellungById(Long id, BestellungFetchType fetch, Locale locale) {
		Bestellung bestellung = null;
		try {
			switch (fetch) {
				case NUR_BESTELLUNG:
					bestellung = em.find(Bestellung.class, id);
					break;
				
				case MIT_BESTELLPOSITIONEN:
					bestellung = em.createNamedQuery(
									Bestellung.FIND_BESTELLUNG_BY_ID_FETCH_BESTELLPOSITIONEN,
									Bestellung.class)
							  .setParameter(Bestellung.PARAM_ID, id)
							  .getSingleResult();
					break;
	
				default:
					bestellung = em.find(Bestellung.class, id);
					break;
			}
		}
		catch (NoResultException e) {
			return null;
		}
		return bestellung;
	}
	
	public Kunde findKundeByBestellung(Long id) {
		try {
			final Kunde kunde = em.createNamedQuery(Bestellung.FIND_KUNDE_BY_ID, Kunde.class)
                                          .setParameter(Bestellung.PARAM_ID, id)
					                      .getSingleResult();
			return kunde;
		}
		catch (NoResultException e) {
			return null;
		}
	}
	
	public List<Bestellung> findBestellungenByKunde(Long kundeId) {
		final List<Bestellung> bestellungen = em.createNamedQuery(Bestellung.FIND_BESTELLUNGEN_BY_KUNDE,
															      Bestellung.class)
                                              .setParameter(Bestellung.PARAM_KUNDEID, kundeId)
                                              .getResultList();
		return bestellungen;
	}
	
	public List<Bestellung> findAllBestellungen(BestellungFetchType fetch, Locale locale) {
		List<Bestellung> bestellungen = null;
		try {
			switch (fetch) {
				case NUR_BESTELLUNG:
					bestellungen = em.createNamedQuery(Bestellung.FIND_ALL_BESTELLUNGEN, Bestellung.class)
                                 .getResultList();
					break;
				case MIT_BESTELLPOSITIONEN:
					bestellungen = em.createNamedQuery(
									  Bestellung.FIND_ALL_BESTELLUNGEN_FETCH_BESTELLPOSITIONEN, 
									  Bestellung.class)
							  	  .getResultList();
					break;
	
				default:
					bestellungen = em.createNamedQuery(Bestellung.FIND_ALL_BESTELLUNGEN, Bestellung.class)
                    .getResultList();
					break;
			}
		}
		catch (NoResultException e) {
			return null;
		}
		return bestellungen;
	}
	
	
	public Bestellung createBestellung(Bestellung bestellung,
			                           Kunde kunde,
			                           Locale locale) {
		if (bestellung == null) {
			return null;
		}
		
		for (Bestellposition bp : bestellung.getBestellpositionen()) {
			logger.debugf("Bestellposition: {0}", bp);				
		}

		
		kunde = ks.findKundeById(kunde.getKundeId(), FetchType.NUR_KUNDE, locale);
		bestellung.setKunde(kunde);

		
		bestellung.setId(KEINE_ID);
		for (Bestellposition bp : bestellung.getBestellpositionen()) {
			bp.setId(KEINE_ID);
		}
		
		validateBestellung(bestellung, locale, TechnicalDate.class);
		em.persist(bestellung);
		event.fire(bestellung);

		return bestellung;
	}
	
	public Bestellung updateBestellung(Bestellung bestellung, Locale locale) {		
		if (bestellung == null) {
			return null;
		}
		
		// Werden alle Constraints beim Modifizieren gewahrt?
		validateBestellung(bestellung, locale, Default.class, IdGroup.class);
		
		
		//um Bestellpositionen updaten zu können, bpos in Schleife abarbeiten, passende bpos von db laden und updaten
		em.merge(bestellung);
		return bestellung;
	}
	
	
//	private void validateBestellungId(Long bestellungId, Locale locale) {
//		final Validator validator = validationService.getValidator(locale);
//		final Set<ConstraintViolation<Bestellung>> violations = validator.validateValue(Bestellung.class,
//				                                                                           "id",
//				                                                                           bestellungId,
//				                                                                           IdGroup.class);
//		if (violations != null && !violations.isEmpty())
//			throw new InvalidBestellungIdException(bestellungId, violations);
//	}

	private void validateBestellung(Bestellung bestellung, Locale locale, Class<?>... groups) {
		final Validator validator = validationProvider.getValidator(locale);
		
		final Set<ConstraintViolation<Bestellung>> violations = validator.validate(bestellung);
		if (violations != null && !violations.isEmpty()) {
			throw new BestellungValidationException(bestellung, violations);
		}
	}
	
	
}
