package de.shop.bestellverwaltung.controller;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.artikelverwaltung.service.ArtikelService;
import de.shop.bestellverwaltung.domain.Bestellposition;

import org.jboss.logging.Logger;

import de.shop.util.Client;
import de.shop.util.Log;

import static java.util.concurrent.TimeUnit.MINUTES; 


@Named("wk")
@ConversationScoped
@Log
public class Warenkorb implements Serializable {
	private static final long serialVersionUID = 2822410085540035488L;
	
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	
	private static final String JSF_VIEW_WARENKORB = "/bestellverwaltung/viewWarenkorb?init=true";
	private static final int TIMEOUT = 5;
	
	private final List<Bestellposition> positionen = new ArrayList<Bestellposition>();;
	private Long artikelId;
	
	@Inject
	private transient Conversation conversation;
	
	@Inject
	private ArtikelService as;
	
	@Inject
	@Client
	private Locale locale;

	@PostConstruct
	private void postConstruct() {
		LOGGER.debugf("CDI-faehiges Bean %s wurde erzeugt", this);
	}
	
	@PreDestroy
	private void preDestroy() {
		LOGGER.debugf("CDI-faehiges Bean %s wird geloescht", this);
	}
	
	public List<Bestellposition> getPositionen() {
		return positionen;
	}
		
	public void setArtikelId(Long artikelId) {
		this.artikelId = artikelId;
	}

	public Long getArtikelId() {
		return artikelId;
	}

	@Override
	public String toString() {
		return "Warenkorb " + positionen;
	}

	
	public String add(Artikel artikel) {
		beginConversation();
		
		for (Bestellposition bp : positionen) {
			if (bp.getArtikel().equals(artikel)) {
				// bereits im Warenkorb
				final int vorhandeneAnzahl = bp.getBestellmenge();
				bp.setBestellmenge(vorhandeneAnzahl + 1);
				return JSF_VIEW_WARENKORB;
			}
		}
		
		final Bestellposition neu = new Bestellposition(artikel);
		positionen.add(neu);
		return JSF_VIEW_WARENKORB;
	}
	
	public String add() {
		final Artikel artikel = as.findArtikelById(artikelId, locale);
		if (artikel == null) {
			return null;
		}
		
		final String outcome = add(artikel);
		artikelId = null;
		return outcome;
	}
	
	public void beginConversation() {
		if (!conversation.isTransient()) {
			return;
		}
		conversation.begin();
		conversation.setTimeout(MINUTES.toMillis(TIMEOUT));
		LOGGER.trace("Conversation beginnt");
	}
	

	public void endConversation() {
		conversation.end();
		LOGGER.trace("Conversation beendet");
	}
	
	public void remove(Bestellposition bestellposition) {
		positionen.remove(bestellposition);
		if (positionen.isEmpty()) {
			endConversation();
		}
	}
}
