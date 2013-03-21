package de.shop.mail;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.invoke.MethodHandles;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.kundenverwaltung.service.NeuerKunde;
import de.shop.util.Log;

@ApplicationScoped
@Log
public class KundeObserver implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8081997720730732302L;
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	private static final String NEWLINE = System.getProperty("line.separator");
	
	@Resource(lookup = "java:jboss/mail/Default")
	private transient Session mailSession;
	
	private String mailAbsender;   // in META-INF\seam-beans.xml setzen
	private String nameAbsender;   // in META-INF\seam-beans.xml setzen

	@PostConstruct
	private void init() {
		if (mailAbsender == null) {
			LOGGER.warning("Der Absender fuer Kunde-Emails ist nicht gesetzt.");
			return;
		}
		LOGGER.info("Absender fuer Kunde-Emails: " + mailAbsender);
	}
	
	public void onCreateKunde(@Observes @NeuerKunde Kunde kunde) {
		final String mailEmpfaenger = kunde.getEmail();
		if (mailAbsender == null || mailEmpfaenger == null) {
			return;
		}
		final String vorname = kunde.getVorname() == null ? "" : kunde.getVorname();
		final String nameEmpfaenger = vorname + kunde.getNachname();
		
		final MimeMessage message = new MimeMessage(mailSession);

		try {
			// Absender setzen
			final InternetAddress absenderObj = new InternetAddress(mailAbsender, nameAbsender);
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
			LOGGER.finest(text);
			message.setText(text);

			// Hohe Prioritaet einstellen
			//message.setHeader("Importance", "high");
			//message.setHeader("Priority", "urgent");
			//message.setHeader("X-Priority", "1");

			Transport.send(message);
		}
		catch (MessagingException | UnsupportedEncodingException e) {
			LOGGER.severe(e.getMessage());
			return;
		}
	}
}
