package de.shop.mail;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;

import org.jboss.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Stateful;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import de.shop.bestellverwaltung.domain.Bestellposition;
import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.bestellverwaltung.service.NeueBestellung;
import de.shop.util.Config;
import de.shop.util.Log;

@ApplicationScoped
@Stateful
@Log
public class BestellungObserver implements Serializable {
	private static final long serialVersionUID = -1567643645881819340L;
	private static final String NEWLINE = System.getProperty("line.separator");
	
	@Resource(lookup = "java:jboss/mail/Default")
	private transient Session mailSession;
	
	@Inject
	private Logger logger;
	
	
	@Inject
	private Config config;
	
	private String absenderMail;
	private String absenderName;
	private String empfaengerMail;
	private String empfaengerName;
	
	@PostConstruct
	// Attribute mit @Inject sind initialisiert
	private void postConstruct() {
		absenderMail = config.getAbsenderMail();
		absenderName = config.getAbsenderName();
		empfaengerMail = config.getEmpfaengerMail();
		empfaengerName = config.getEmpfaengerName();
		
		if (absenderMail == null || empfaengerMail == null) {
			logger.warn("Absender oder Empfaenger fuer Markteting-Emails sind nicht gesetzt.");
			return;
		}
		logger.infof("Absender fuer Markteting-Emails: %s", absenderMail);
		logger.infof("Empfaenger fuer Markteting-Emails: %s", empfaengerMail);
	}
	
	public void onCreateBestellung(@Observes @NeueBestellung Bestellung bestellung) {
		if (absenderMail == null || empfaengerMail == null) {
			return;
		}

		final MimeMessage message = new MimeMessage(mailSession);

		try {
			// Absender setzen
			final InternetAddress absenderObj = new InternetAddress(absenderMail, absenderName);
			message.setFrom(absenderObj);
			
			// Empfaenger setzen
			final InternetAddress empfaenger = new InternetAddress(empfaengerMail, empfaengerName);
			message.setRecipient(RecipientType.TO, empfaenger);   // RecipientType: TO, CC, BCC

			// Subject setzen
			message.setSubject("Neue Bestellung Nr. " + bestellung.getId());
			
			// Text setzen mit MIME Type "text/plain"
			final StringBuilder sb = new StringBuilder("Neue Bestellung Nr. "
                                                       + bestellung.getId() + NEWLINE);
			for (Bestellposition bp : bestellung.getBestellpositionen()) {
				sb.append(bp.getBestellmenge() + "\t" + bp.getArtikel().getName() + NEWLINE);
			}
			final String text = sb.toString();
			logger.infof(text);
			message.setText(text);

			// Hohe Prioritaet einstellen
			//message.setHeader("Importance", "high");
			//message.setHeader("Priority", "urgent");
			//message.setHeader("X-Priority", "1");

			Transport.send(message);
		}
		catch (MessagingException | UnsupportedEncodingException e) {
			logger.error(e.getMessage());
			return;
		}
	}
}
