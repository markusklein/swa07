package de.shop.kundenverwaltung.domain;

import static javax.persistence.EnumType.STRING;

import static de.shop.util.Constants.ERSTE_VERSION;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.jboss.logging.Logger;

import de.shop.util.IdGroup;

import java.sql.Timestamp;
import java.util.Date;


/**
 * The persistent class for the adresse database table.
 * 
 */
@Entity
@Table(name = "adresse")
@NamedQueries({
	@NamedQuery(name = Adresse.FIND_ADRESSEN,
				query = "SELECT a FROM Adresse a"),
	@NamedQuery(name = Adresse.FIND_ADRESSE_BY_STRASSE,
				query = "SELECT a "
						+ "FROM Adresse a "
						+ "WHERE a.strasse = :" + Adresse.PARAM_ADRESSE_STRASSE),
	@NamedQuery(name = Adresse.FIND_ADRESSE_BY_PLZ,
				query = "SELECT a "
						+ "FROM Adresse a "
						+ "WHERE a.plz = :" + Adresse.PARAM_ADRESSE_PLZ),
	@NamedQuery(name = Adresse.FIND_ADRESSEN_BY_LAND,
				query = "SELECT a "
						+ "FROM Adresse a "
						+ "WHERE UPPER(a.land) = UPPER(:" + Adresse.PARAM_ADRESSEN_LAND + ")")
})
public class Adresse implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	
	private static final String PREFIX = "Adresse.";
	
	public static final String FIND_ADRESSEN = PREFIX + "findAdressen";
	public static final String FIND_ADRESSE_BY_STRASSE = PREFIX + "findAdresseByStrasse";
	public static final String FIND_ADRESSE_BY_PLZ = PREFIX + "findAdresseByPlz";
	public static final String FIND_ADRESSEN_BY_LAND = PREFIX + "findAdressenByLand";
	
	public static final String PARAM_ADRESSE_STRASSE = "strasse";
	public static final String PARAM_ADRESSE_PLZ = "plz";
	public static final String PARAM_ADRESSEN_LAND = "land";
	
	@Id
	@GeneratedValue
	@Column(name = "adresse_id", unique = true, nullable = false, updatable = false)
	@Min(value = 1, message = "{kundenverwaltung.adresse.id.min}", groups = IdGroup.class)
	private Long adresseId = null;
	
	@Version
	@Basic(optional = false)
	private int version = ERSTE_VERSION;
	
	@NotNull(message = "{kundenverwaltung.adresse.strasse.notNull}")
	@Size(min = 2, max = 60, message = "{kundenverwaltung.adresse.strasse.length}")
	@Column(length = 60, nullable = false)
	@Pattern(regexp = "[A-ZÄÖÜ][a-zäöüß]+ [0-9]+([a-z])?", message = "{kundenverwaltung.adresse.strasse.pattern")
	private String strasse;

	@NotNull(message = "{kundenverwaltung.adresse.plz.notNull}")
	@Column(length = 25, nullable = false)
	@Pattern(regexp = "[0-9]{4,5}", message = "{kundenverwaltung.adresse.plz.pattern}")
	@Digits(integer = 5, fraction = 0)
	private String plz;

	@NotNull(message = "{kundenverwaltung.adresse.ort.notNull}")
	@Size(min = 2, max = 45, message = "{kundenverwaltung.adresse.ort.length}")
	@Column(length = 45, nullable = false)
	@Pattern(regexp = "[A-ZÄÖÜ][a-zäöüß]+", message = "{kundenverwaltung.adresse.ort.pattern}")
	private String ort;
	
	@NotNull(message = "{kundenverwaltung.adresse.land.notNull}")
	@Enumerated(STRING)
	@Column(length = 2, nullable = false)
	private AdresseLandType land;
	
	@Column(name = "aktualisiert", nullable = false)
	@JsonIgnore
	private Timestamp aktualisiert;

	@Column(name = "erzeugt", nullable = false)
	@JsonIgnore
	private Timestamp erzeugt;

	//TODO Rückbeziehung nachschauen
//	@OneToOne(mappedBy = "lieferadresse")
//	@JsonIgnore
//	private Kunde kundeLieferAdresse;
//	
//	@OneToOne(mappedBy = "rechnungsadresse")
//	@JsonIgnore
//	private Kunde kundeRechnungsAdresse;
	
	@PrePersist
	protected void prePersist() {
		erzeugt = new Timestamp(new Date().getTime());
		aktualisiert = new Timestamp(new Date().getTime());
	}

	@PreUpdate
	private void preUpdate() {
		aktualisiert = new Timestamp(new Date().getTime());
	}
	
	@PostPersist
	private void postPersist() {
		LOGGER.debugf("Neue Adresse mit ID=%s", adresseId);
	}
	
	@PostUpdate
	private void postUpdate() {
		LOGGER.debugf("Adresse mit ID=%s aktualisiert", adresseId);
	}
	
	//Konstruktoren
	public Adresse() {
		
	}
	
	public Adresse(Long adresseId, Timestamp aktualisiert, Timestamp erzeugt,
			AdresseLandType land, String ort, String plz, String strasse) {
		super();
		this.adresseId = adresseId;
		this.aktualisiert = aktualisiert == null ? null : (Timestamp) aktualisiert.clone();
		this.erzeugt = erzeugt == null ? null : (Timestamp) erzeugt.clone();
		this.land = land;
		this.ort = ort;
		this.plz = plz;
		this.strasse = strasse;
	}
		
	//Getter and Setter
	public Long getAdresseId() {
		return this.adresseId;
	}

	public void setAdresseId(Long adresseId) {
		this.adresseId = adresseId;
	}

	public Timestamp getAktualisiert() {
		return aktualisiert == null ? null : (Timestamp) aktualisiert.clone();
	}

	public void setAktualisiert(Timestamp aktualisiert) {
		this.aktualisiert = aktualisiert == null ? null : (Timestamp) aktualisiert.clone();
	}

	public Timestamp getErzeugt() {
		return erzeugt == null ? null : (Timestamp) erzeugt.clone();
	}

	public void setErzeugt(Timestamp erzeugt) {
		this.erzeugt = erzeugt == null ? null : (Timestamp) erzeugt.clone();
	}

	public AdresseLandType getLand() {
		return this.land;
	}

	public void setLand(AdresseLandType land) {
		this.land = land;
	}

	public String getOrt() {
		return this.ort;
	}

	public void setOrt(String ort) {
		this.ort = ort;
	}

	public String getPlz() {
		return this.plz;
	}

	public void setPlz(String plz) {
		this.plz = plz;
	}

	public String getStrasse() {
		return this.strasse;
	}

	public void setStrasse(String strasse) {
		this.strasse = strasse;
	}
	
	public int getVersion() {
		return this.version;
	}
	
	public void setVersion(int version) {
		this.version = version;
	}
	
	public void setValues(Adresse a) {
		this.strasse = a.getStrasse();
		this.plz = a.getPlz();
		this.ort = a.getOrt();
		this.land = a.getLand();
		this.version = a.getVersion();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((land == null) ? 0 : land.hashCode());
		result = prime * result + ((ort == null) ? 0 : ort.hashCode());
		result = prime * result + ((plz == null) ? 0 : plz.hashCode());
		result = prime * result + ((strasse == null) ? 0 : strasse.hashCode());
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
		Adresse other = (Adresse) obj;
		if (land == null) {
			if (other.land != null)
				return false;
		} 
		else if (!land.equals(other.land))
			return false;
		if (ort == null) {
			if (other.ort != null)
				return false;
		} 
		else if (!ort.equals(other.ort))
			return false;
		if (plz == null) {
			if (other.plz != null)
				return false;
		} 
		else if (!plz.equals(other.plz))
			return false;
		if (strasse == null) {
			if (other.strasse != null)
				return false;
		} 
		else if (!strasse.equals(other.strasse))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Adresse [adresseId=" + adresseId + ", aktualisiert="
				+ aktualisiert + ", erzeugt=" + erzeugt + ", land=" + land
				+ ", ort=" + ort + ", plz=" + plz + ", strasse=" + strasse
				+ "]";
	}

}