package de.shop.artikelverwaltung.service;



import java.io.Serializable;
import java.lang.invoke.MethodHandles;
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

import org.jboss.logging.Logger;

import com.google.common.base.Strings;

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.artikelverwaltung.domain.Kategorie;
import de.shop.util.IdGroup;
import de.shop.util.Log;
import de.shop.util.ValidatorProvider;

@Log
public class ArtikelService implements Serializable {
	private static final long serialVersionUID = 3076865030092242363L;
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	
	@PersistenceContext
	private transient EntityManager em;
	
	@Inject
	private KategorieService ks;
	
	@Inject
	private ValidatorProvider validationProvider;
	
	@PostConstruct
	private void postConstruct() {
		LOGGER.debugf("CDI-faehiges Bean {0} wurde erzeugt", this);
	}
	
	@PreDestroy
	private void preDestroy() {
		LOGGER.debugf("CDI-faehiges Bean {0} wird geloescht", this);
	}
	
	public List<Artikel> findAllArtikel() {
		List<Artikel> artikel;
		
		artikel = em.createNamedQuery(Artikel.FIND_ARTIKEL, Artikel.class)
					.getResultList();
		
		return artikel;
	}
	
	public Artikel findArtikelById(Long id, Locale locale) {
		validateArtikelId(id, locale);
		final Artikel artikel = em.find(Artikel.class, id);
		return artikel;
	}
	
	public List<Artikel> findArtikelByKategorie(String id, Locale locale) {
		
		if (id == null || id.length() == 0)
			return null;
		
		final List<Artikel> artikel = em.createNamedQuery(Artikel.ARTIKEL_BY_KATEGORIE, Artikel.class)
									.setParameter(Artikel.PARAMETER_KATEGORIE_ID, Long.valueOf(id))
									.getResultList();
		
		return artikel;
	}
	
	public List<Artikel> findArtikelByNamen(String name, Locale locale) {
		
		if (name == null)
			return null;
		
		final List<Artikel> artikel = em.createNamedQuery(Artikel.ARTIKEL_BY_NAME, Artikel.class)
									.setParameter(Artikel.PARAMETER_NAME, "%" + name + "%")
									.getResultList();
		
		return artikel;
	}
	

	public List<Artikel> findArtikelByBeschreibung(String beschreibung, Locale locale) {
		//TODO validateArtikelBeschreibung(beschreibung, locale);
		if (Strings.isNullOrEmpty(beschreibung)) {
			
			return null;
		}
		
		final List<Artikel> artikel = em.createNamedQuery(Artikel.ARTIKEL_BY_BESCHREIBUNG, Artikel.class)
				                        .setParameter(Artikel.PARAMETER_BESCHREIBUNG, "%" + beschreibung + "%")
				                        .getResultList();
		return artikel;
	}
	
	public Artikel updateArtikel(Artikel artikel, Locale locale) {
		if (artikel == null)
			return null;
		
		validateArtikel(artikel, locale, Default.class);
		
		try {
			final Artikel vorhandeneArtikel = em.find(Artikel.class, artikel.getArtikelId());
			
			if (vorhandeneArtikel.getArtikelId().longValue() != artikel.getArtikelId().longValue())
				throw new ArtikelExistsException(artikel.getArtikelId());
		}
		catch (NoResultException e) {
			LOGGER.debugf("Neuer Artikel");
		}
		em.detach(artikel);
		em.merge(artikel);
		return artikel;
	}
	public Artikel createArtikel(Artikel artikel, Locale locale) {
		
		if (artikel == null)
			return artikel;
		
		final Kategorie kategorie = ks.findKategorieById(artikel.getKategorie().getKategorieId(), locale);
		
		artikel.setArtikelId(null);
		artikel.setKategorie(kategorie);
		
		List<Artikel> a = findAllArtikel();
		 for (Artikel item: a){
			 if(item.equals(artikel)){
				return null;
			
			}
			
		 }
		 validateArtikel(artikel, locale, Default.class);
			

			em.persist(artikel);
			return artikel;
		
		
	}

	private void validateArtikel(Artikel artikel, Locale locale, Class<?>... groups) {
		// Werden alle Constraints beim Einfuegen gewahrt?
		final Validator validator = validationProvider.getValidator(locale);
		
		final Set<ConstraintViolation<Artikel>> violations = validator.validate(artikel, groups);
		if (!violations.isEmpty()) {
			throw new ArtikelValidationException(artikel, violations);
		}
	}
	
	private void validateArtikelId(Long artikelId, Locale locale) {
		final Validator validator = validationProvider.getValidator(locale);
		final Set<ConstraintViolation<Artikel>> violations = validator.validateValue(Artikel.class,
				                                                                           "artikelId",
				                                                                           artikelId,
				                                                                           IdGroup.class);
		if (!violations.isEmpty())
			throw new InvalidArtikelIdException(artikelId, violations);
	}
	
	
	
}	
