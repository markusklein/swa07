package de.shop.mail;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.jboss.logging.Logger;

import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.kundenverwaltung.service.NeuerKunde;
import de.shop.util.Config;
import de.shop.util.Log;



@ApplicationScoped
@Log
public class KundeObserver implements Serializable {

	private static final long serialVersionUID = -8081997720730732302L;
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
	
	@PostConstruct
	// Attribute mit @Inject sind initialisiert
	private void postConstruct() {
		absenderMail = config.getAbsenderMail();
		absenderName = config.getAbsenderName();
		empfaengerMail = config.getEmpfaengerMail();
		
		if (absenderMail == null || empfaengerMail == null) {
			logger.warn("Absender oder Empfaenger fuer Markteting-Emails sind nicht gesetzt.");
			return;
		}
		logger.infof("Absender fuer Markteting-Emails: %s", absenderMail);
		logger.infof("Empfaenger fuer Markteting-Emails: %s", empfaengerMail);
	}

	
	public void onCreateKunde(@Observes @NeuerKunde Kunde kunde) {
		final String mailEmpfaenger = kunde.getEmail();
		if (absenderMail == null || mailEmpfaenger == null) {
			return;
		}
		final String vorname = kunde.getVorname() == null ? "" : kunde.getVorname();
		final String nameEmpfaenger = vorname + kunde.getNachname();
		
		final MimeMessage message = new MimeMessage(mailSession);

		try {
			// Absender setzen
			final InternetAddress absenderObj = new InternetAddress(absenderMail, absenderName);
			message.setFrom(absenderObj);
			
			// Empfaenger setzen
			final InternetAddress empfaenger = new InternetAddress(mailEmpfaenger, nameEmpfaenger);
			message.setRecipient(RecipientType.TO, empfaenger);   // RecipientType: TO, CC, BCC

			// Subject setzen
			message.setSubject("Herzlich Willkommen in unserem Webshop");
			
			// Text setzen mit MIME Type "text/plain"
			final StringBuilder sb = new StringBuilder("Hallo " + kunde.getVorname() + " " 
										 + kunde.getNachname() + "," + NEWLINE);
			sb.append("willkommen in unserem Online Shop!");
			sb.append(NEWLINE);
			sb.append("Ihre Kundennummer lautet: ");
			sb.append(kunde.getKundeId());
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
