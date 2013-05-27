package de.shop.artikelverwaltung.controller;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.Flash;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpSession;

import org.jboss.logging.Logger;

import de.shop.artikelverwaltung.domain.Kategorie;
import de.shop.artikelverwaltung.service.KategorieService;
import de.shop.util.Log;
import de.shop.util.Transactional;

@Named("kac")
@RequestScoped
@Log
public class KategorieContoller implements Serializable {

	private static final long serialVersionUID = 1564024850446471639L;

	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass());
	
	private static final String JSF_LIST_KATEGORIE = "/artikelverwaltung/listKategorie";
	private static final String FLASH_KATEGORIE = "kategorie";
	//private static final int ANZAHL_LADENHUETER = 5;
	
	private static final String JSF_SELECT_KATEGORIE = "/artikelverwaltung/selectKategorie";
	private static final String SESSION_VERFUEGBARE_KATEGORIE = "verfuegbareKategorie";

	private String bezeichnung;
	private List<Kategorie> kategorien = Collections.emptyList();
	

	@Inject
	private KategorieService ks;
	
	@Inject
	private Flash flash;
	
	@Inject
	private transient HttpSession session;

	
	@PostConstruct
	private void postConstruct() {
		LOGGER.debugf("CDI-faehiges Bean %s wurde erzeugt", this);
	}

	@PreDestroy
	private void preDestroy() {
		LOGGER.debugf("CDI-faehiges Bean %s wird geloescht", this);
	}
	
	@Override
	public String toString() {
		return "KategorieController [bezeichnung=" + bezeichnung + "]";
	}

	public String getBezeichnung() {
		return bezeichnung;
	}

	public void setBezeichnung(String bezeichnung) {
		this.bezeichnung = bezeichnung;
	}

	//TODO locale statt null
	@Transactional
	public String findKategorieByBezeichnung() {
		final List<Kategorie> kategorie = ks.findKategorieByBezeichnung(bezeichnung, null);
		flash.put(FLASH_KATEGORIE, kategorie);

		return JSF_LIST_KATEGORIE;
	}
	
	@Transactional
	public String findAllKategorien() {
		kategorien = ks.findAllKategorie();
		flash.put(FLASH_KATEGORIE, kategorien);

		return JSF_LIST_KATEGORIE;
	}
	
	@Transactional
	public String selectKategorie() {
		if (session.getAttribute(SESSION_VERFUEGBARE_KATEGORIE) != null) {
			return JSF_SELECT_KATEGORIE;
		}
		
		final List<Kategorie> alleKategorie = ks.findAllKategorie();
		session.setAttribute(SESSION_VERFUEGBARE_KATEGORIE, alleKategorie);
		return JSF_SELECT_KATEGORIE;
	}

	public List<Kategorie> getKategorien() {
		kategorien = ks.findAllKategorie();
		flash.put(FLASH_KATEGORIE, kategorien);
		return kategorien;
	}

	public void setKategorien(List<Kategorie> kategorien) {
		this.kategorien = kategorien;
	}
}
