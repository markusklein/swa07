package de.shop.kundenverwaltung.domain;

import static de.shop.util.Constants.ERSTE_VERSION;
import static javax.persistence.EnumType.STRING;

import java.io.Serializable;
import java.net.URI;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.validator.constraints.Email;

import java.sql.Timestamp;
import java.util.Date;


/**
 * The persistent class for the kunde database table.
 * 
 */

@Entity
@XmlRootElement	
@Table(name = "kunde")
@NamedQueries({
	@NamedQuery(name  = Kunde.FIND_KUNDE_BY_EMAIL, 
				query = "select kunde from Kunde as kunde where Kunde.email = :" + Kunde.PARAM_EMAIL),
	@NamedQuery(name  = Kunde.FIND_KUNDEN_BY_NACHNAME, 
				query = "select kunde from Kunde as kunde where Kunde.nachname = :" + Kunde.PARAM_NACHNAME),
	@NamedQuery(name  = Kunde.FIND_KUNDEN, 
				query = "select kunde from Kunde as kunde"),
	@NamedQuery(name  = Kunde.FIND_KUNDEN_ORDER_BY_ID, 
				query = "select kunde from Kunde as kunde order by Kunde_ID"),
	@NamedQuery(name  = Kunde.FIND_KUNDE_BY_ID_FETCH_ZAHLUNGSINFORMATION, 
				query = "SELECT DISTINCT K FROM Kunde K LEFT JOIN FETCH K.zahlungsinformation WHERE K.kundeid = :" 
						+ Kunde.PARAM_ID),
	@NamedQuery(name  = Kunde.FIND_KUNDE_BY_ID_FETCH_ADRESSE, 
				query = "SELECT DISTINCT K FROM Kunde K LEFT JOIN FETCH K.rechnungsadresse " 
						+ "LEFT JOIN FETCH K.lieferadresse WHERE  K.kundeid = :" + Kunde.PARAM_ID),
	@NamedQuery(name  = Kunde.FIND_KUNDE_BY_ID_FETCH_ADRESSE_UND_ZAHLUNGSINFORMATION, 
				query = "SELECT DISTINCT K FROM Kunde K LEFT JOIN FETCH K.rechnungsadresse "
						+ "LEFT JOIN FETCH K.lieferadresse LEFT JOIN FETCH K.zahlungsinformation WHERE  K.kundeid = :" 
						+ Kunde.PARAM_ID),
	@NamedQuery(name  = Kunde.FIND_KUNDE_BY_EMAIL_FETCH_ZAHLUNGSINFORMATION, 
				query = "SELECT DISTINCT K FROM Kunde K LEFT JOIN FETCH K.zahlungsinformation WHERE K.email = :" 
						+ Kunde.PARAM_EMAIL),
	@NamedQuery(name  = Kunde.FIND_KUNDE_BY_EMAIL_FETCH_ADRESSE, 
				query = "SELECT DISTINCT K FROM Kunde K LEFT JOIN FETCH K.rechnungsadresse " 
						+ "LEFT JOIN FETCH K.lieferadresse WHERE K.email = :" 
						+ Kunde.PARAM_EMAIL),
	@NamedQuery(name  = Kunde.FIND_KUNDE_BY_EMAIL_FETCH_ADRESSE_UND_ZAHLUNGSINFORMATION, 
				query = "SELECT DISTINCT K FROM Kunde K LEFT JOIN FETCH K.rechnungsadresse " 
						+ "LEFT JOIN FETCH K.lieferadresse LEFT JOIN FETCH K.zahlungsinformation WHERE  K.email = :" 
						+ Kunde.PARAM_EMAIL),
	@NamedQuery(name  = Kunde.FIND_KUNDEN_BY_NACHNAME_FETCH_ZAHLUNGSINFORMATION, 
				query = "SELECT DISTINCT K FROM Kunde K " 
						+ "LEFT JOIN FETCH K.zahlungsinformation WHERE UPPER(K.nachname) = UPPER(:" 
						+ Kunde.PARAM_NACHNAME + ")"),
	@NamedQuery(name  = Kunde.FIND_KUNDEN_BY_NACHNAME_FETCH_ADRESSE, 
				query = "SELECT DISTINCT K FROM Kunde K LEFT JOIN FETCH K.rechnungsadresse " 
						+ "LEFT JOIN FETCH K.lieferadresse WHERE UPPER(K.nachname) = UPPER(:" 
						+ Kunde.PARAM_NACHNAME + ")"),
	@NamedQuery(name  = Kunde.FIND_KUNDEN_BY_NACHNAME_FETCH_ADRESSE_UND_ZAHLUNGSINFORMATION, 
				query = "SELECT DISTINCT K FROM Kunde K LEFT JOIN FETCH K.rechnungsadresse " 
						+ "LEFT JOIN FETCH K.lieferadresse " 
						+ "LEFT JOIN FETCH K.zahlungsinformation WHERE  UPPER(K.nachname) = UPPER(:" 
						+ Kunde.PARAM_NACHNAME + ")"),
	@NamedQuery(name  = Kunde.FIND_IDS_BY_PREFIX,
				query = "SELECT   k.kundeid FROM  Kunde k WHERE CONCAT('', k.kundeid) LIKE :" 
						+ Kunde.PARAM_KUNDE_ID_PREFIX + " ORDER BY k.kundeid"),
	@NamedQuery(name  = Kunde.FIND_NACHNAMEN_BY_PREFIX,
   	            query = "SELECT   DISTINCT k.nachname FROM  Kunde k WHERE UPPER(k.nachname) LIKE UPPER(:" 
   	            		+ Kunde.PARAM_KUNDE_NACHNAME_PREFIX + ")")
}
	
	)

public class Kunde implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1867578666521060479L;
	
	
	private static final String PREFIX = "KUNDE.";
	public static final String FIND_KUNDE_BY_EMAIL = PREFIX + "findKundeByEmail";
	public static final String FIND_KUNDEN_BY_NACHNAME = PREFIX + "findKundeByNachname";
	public static final String FIND_KUNDEN = PREFIX + "findKunden";
	public static final String FIND_KUNDEN_ORDER_BY_ID = PREFIX + "findKundeOrderById";
	public static final String FIND_KUNDEN_FETCH_BESTELLUNGEN = PREFIX + "findKundeFetchBestellungen";
	
	public static final String FIND_KUNDE_BY_ID_FETCH_ZAHLUNGSINFORMATION = PREFIX 
											+ "findKundeByIdFetchZahlungsinformation";
	
	public static final String FIND_KUNDE_BY_ID_FETCH_ADRESSE = PREFIX + "findKundeByIdFetchAdresse";
	
	public static final String FIND_KUNDE_BY_ID_FETCH_ADRESSE_UND_ZAHLUNGSINFORMATION = PREFIX 
											+ "findKundeByIdFetchAdresseUndZahlungsinformation";
	
	public static final String FIND_KUNDE_BY_EMAIL_FETCH_ZAHLUNGSINFORMATION = PREFIX 
											+ "findKundeByEmailFetchZahlungsinformation";
	
	public static final String FIND_KUNDE_BY_EMAIL_FETCH_ADRESSE = PREFIX + "findKundeByEmailFetchAdresse";
	
	public static final String FIND_KUNDE_BY_EMAIL_FETCH_ADRESSE_UND_ZAHLUNGSINFORMATION = PREFIX 
											+ "findKundeByEmailFetchAdresseUndZahlungsinformation";
	
	public static final String FIND_KUNDEN_BY_NACHNAME_FETCH_ZAHLUNGSINFORMATION = PREFIX 
											+ "findKundeByNachnameFetchZahlungsinformation";
	
	public static final String FIND_KUNDEN_BY_NACHNAME_FETCH_ADRESSE = PREFIX + "findKundeByNachnameFetchAdresse";
	
	public static final String FIND_KUNDEN_BY_NACHNAME_FETCH_ADRESSE_UND_ZAHLUNGSINFORMATION = PREFIX 
											+ "findKundeByNachnameFetchAdresseUndZahlungsinformation";
	
	public static final String FIND_NACHNAMEN_BY_PREFIX = PREFIX + "findNachnameByPrefix";
	public static final String FIND_IDS_BY_PREFIX = PREFIX + "findIdsByPrefix";
	
	
	public static final String PARAM_EMAIL = "email";
	public static final String PARAM_ID = "id";
	public static final String PARAM_NACHNAME = "nachname";
	public static final String PARAM_KUNDE_NACHNAME_PREFIX = "nachnameprefix";
	public static final String PARAM_KUNDE_ID_PREFIX = "idprefix";
	

	
	
	@Id
	@XmlElement
	@NotNull
	@GeneratedValue
	@Column(name = "kunde_id", unique = true, updatable = false, nullable = false)
	private Long kundeid;
	
	@Version
	@Basic(optional = false)
	private int version = ERSTE_VERSION;

	@XmlElement
	@NotNull
	@Size(min = 2, max = 30, message = "{kundenverwaltung.kunde.vorname.length}")
	@Pattern(regexp = "[A-ZÄÖÜ][a-zäöüß]+")
	@Column(length = 30, nullable = false)
	private String vorname;

	@XmlElement
	@NotNull(message = "{kundenverwaltung.kunde.nachname.notNull}")
	@Size(min = 2, max = 30, message = "{kundenverwaltung.kunde.nachname.length}")
	@Pattern(regexp = "[A-ZÄÖÜ][a-zäöüß]+", message = "{kundenverwaltung.kunde.nachname.pattern}")
	@Column(length = 30, nullable = false)
	private String nachname;

	@XmlElement
	@NotNull(message ="{kundenverwaltung.kunde.email.notNull}")
	@Email
	@Size(min = 8, max = 45, message = "{kundenverwaltung.kunde.email.length}")
	@Column(length = 45, nullable = false, updatable = false)
	private String email;
	
	
	@XmlElement
	@Column(length = 1, nullable = false)
	@Enumerated(STRING)
	private KundeGeschlechtType geschlecht;
	
	@XmlElement
	@NotNull
	@Size(min = 6, max = 45)
	@Column(length = 45, nullable = false)
	private String passwort;
	
	@XmlElement
	@NotNull
	@Size(max = 45)
	@Column(length = 45, nullable = false)
	private String telefonnummer;

	@XmlElement
	@NotNull(message = "{kundenverwaltung.kunde.adresse.notNull}")
	@OneToOne(optional = false, cascade = {CascadeType.PERSIST, CascadeType.REMOVE })
	@JoinColumn(name = "lieferadresse")
	private Adresse lieferadresse;

	@XmlElement
	@NotNull
	@OneToOne(optional = false, cascade = {CascadeType.PERSIST, CascadeType.REMOVE })
	@JoinColumn(name = "rechnungsadresse")
	private Adresse rechnungsadresse;

	@XmlElement
	@NotNull
	@OneToOne(optional = false, cascade = {CascadeType.PERSIST, CascadeType.REMOVE })
	@JoinColumn(name = "zahlungsinformation_ID")
	private Zahlungsinformation zahlungsinformation;
	
	@XmlElement
	@NotNull
	@Past
	@Temporal(TemporalType.DATE)
	@Column(nullable = false)
	private Date geburtsdatum;
	
	@XmlElement
	@NotNull
	@Column(nullable = false)
	private Timestamp aktualisiert;

	@XmlElement
	@NotNull
	@Past(message = "{kundenverwaltung.kunde.erzeugt.past}")
	@Column(nullable = false)
	private Timestamp erzeugt;
	
	@Transient
	@XmlElement(name = "bestellungen")
	private URI bestellungenUri;


	@PrePersist
	private void prePersist() {
		erzeugt =  new Timestamp(new Date().getTime());
		aktualisiert =  new Timestamp(new Date().getTime());
		
	}

	@PreUpdate
	private void preUpdate() {
		aktualisiert =  new Timestamp(new Date().getTime());
	}
	
	
	
	
	public Long getKundeId() {
		
		
		return this.kundeid;
	}

	public void setKundeId(Long kundeId) {
		
		
		
		this.kundeid = kundeId;
	}

	public Timestamp getAktualisiert() {
		return aktualisiert == null ? null : (Timestamp) aktualisiert.clone();
	}


	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Timestamp getErzeugt() {
		return erzeugt == null ? null : (Timestamp) erzeugt.clone();
	}

	public Date getGeburtsdatum() {
		return this.geburtsdatum;
	}

	public void setGeburtsdatum(Date geburtsdatum) {
		this.geburtsdatum = geburtsdatum;
	}

	public KundeGeschlechtType getGeschlecht() {
		return this.geschlecht;
	}

	public void setGeschlecht(KundeGeschlechtType geschlecht) {
		this.geschlecht = geschlecht;
	}

	public Adresse getLieferadresse() {
		return this.lieferadresse;
	}

	public void setLieferadresse(Adresse lieferadresse) {
		this.lieferadresse = lieferadresse;
	}

	public String getNachname() {
		return this.nachname;
	}

	public void setNachname(String nachname) {
		this.nachname = nachname;
	}

	public String getPasswort() {
		return this.passwort;
	}

	public void setPasswort(String passwort) {
		this.passwort = passwort;
	}

	public Adresse getRechnungsadresse() {
		return this.rechnungsadresse;
	}

	public void setRechnungsadresse(Adresse rechnungsadresse) {
		this.rechnungsadresse = rechnungsadresse;
	}

	public String getTelefonnummer() {
		return this.telefonnummer;
	}

	public void setTelefonnummer(String telefonnummer) {
		this.telefonnummer = telefonnummer;
	}

	public String getVorname() {
		return this.vorname;
	}

	public void setVorname(String vorname) {
		this.vorname = vorname;
	}

	public Zahlungsinformation getZahlungsinformation() {
		return this.zahlungsinformation;
	}

	public void setZahlungsinformation(Zahlungsinformation zahlungsinformation) {
		this.zahlungsinformation = zahlungsinformation;
	}
	
	public URI getBestellungenUri() {
		return bestellungenUri;
	}



	public void setBestellungenUri(URI bestellungenUri) {
		this.bestellungenUri = bestellungenUri;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Kunde other = (Kunde) obj;
		if (email == null) {
			if (other.email != null)
				return false;
		} 
		else if (!email.equals(other.email))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Kunde [kundeId=" + kundeid + ", aktualisiert=" + aktualisiert
				+ ", email=" + email + ", erzeugt=" + erzeugt
				+ ", geburtsdatum=" + geburtsdatum + ", geschlecht="
				+ geschlecht
				+ ", nachname=" + nachname + ", passwort=" + passwort
				+ ", telefonnummer="
				+ telefonnummer + ", vorname=" + vorname + "]";
	}



	public void setValues(Kunde kunde) {
			this.kundeid = kunde.kundeid;
			this.vorname = kunde.vorname;
			this.nachname = kunde.nachname;
			this.email = kunde.email;
			this.geschlecht = kunde.geschlecht;
			this.passwort = kunde.passwort;
			this.telefonnummer = kunde.telefonnummer;
			this.lieferadresse = kunde.lieferadresse;
			this.rechnungsadresse = kunde.rechnungsadresse;
			this.zahlungsinformation = kunde.zahlungsinformation;
			this.geburtsdatum = kunde.geburtsdatum;
			this.bestellungenUri = kunde.bestellungenUri;		
	}
	 
}