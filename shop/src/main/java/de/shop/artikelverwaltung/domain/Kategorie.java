package de.shop.artikelverwaltung.domain;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Column;


import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.sql.Timestamp;
import java.util.Date;

import static de.shop.util.Constants.MIN_ID;
import static de.shop.util.Constants.BEZEICHNUNG_LENGTH_MAX;
import de.shop.util.IdGroup;
import de.shop.util.XmlDateAdapter;


/**
 * The persistent class for the kategorie database table.
 * 
 */

@Entity
@Table(name = "kategorie")
@NamedQueries({
	@NamedQuery(name = Kategorie.FIND_KATEGORIE,
				query = "SELECT a FROM Kategorie a"),
				
	@NamedQuery (name = Kategorie.KATEGORIE_BY_BEZEICHNUNG,
				query = "SELECT      a"
				+ " FROM Kategorie a"
				+ " WHERE a.bezeichnung LIKE :"	+ Kategorie.PARAMETER_BEZEICHNUNG	
				+ " ORDER BY a.id ASC"),

	@NamedQuery (name = Kategorie.KATEGORIE_BY_AKTUALISIERT,
				query = "SELECT     a"
				+ " FROM Kategorie a"
				+ " WHERE a.aktualisiert = :"	+ Kategorie.PARAMETER_AKTUALISIERT),
				
	@NamedQuery (name = Kategorie.KATEGORIE_BY_ERZEUGT,
				query = "SELECT      a"
				+ " FROM Kategorie a"
				+ " WHERE a.erzeugt = :" + Kategorie.PARAMETER_ERZEUGT), 
				
	
				
})
@XmlRootElement
public class Kategorie implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -292137236243905181L;
	private static final String PREFIX = "Kategorie.";
	public static final String FIND_KATEGORIE = PREFIX + "findAllKategorie";
	public static final  String KATEGORIE_BY_BEZEICHNUNG = PREFIX + "findKategorieByBezeichnung";
	public static final  String KATEGORIE_BY_ID 		 = PREFIX + "findKategorieById";		
	public static final  String KATEGORIE_BY_AKTUALISIERT = PREFIX + "findKategorieByAktualisiert";
	public static final  String KATEGORIE_BY_ERZEUGT = PREFIX + "findKategorieByErzeugt";
	
	public static final String PARAMETER_BEZEICHNUNG = "bezeichnung";
	public static final String PARAMETER_AKTUALISIERT = "aktualisiert";
	public static final String PARAMETER_ERZEUGT = "erzeugt";
	public static final String PARAMETER_ID = "id";
	 
	
	public Kategorie(Long kategorieId, String bezeichnung, Timestamp erzeugt,
			Timestamp aktualisiert) {
		super();
		this.kategorieId = kategorieId;
		this.bezeichnung = bezeichnung;
		this.erzeugt = erzeugt == null ? null : (Timestamp) erzeugt.clone();
		this.aktualisiert = aktualisiert == null ? null : (Timestamp) aktualisiert.clone();
	}

	@Id
	@GeneratedValue
	@Min(value = MIN_ID, message = "{artikelverwaltung.kategorie.id.min}", groups = IdGroup.class)
	@Column(name = "kategorie_id", nullable = false)
	@XmlAttribute
	private Long kategorieId;

	@Column(name = "bezeichnung", nullable = false, length = 20)
	@NotNull(message = "{artikelverwaltung.kategorie.bezeichnung.notNull}")
	@Size(max = BEZEICHNUNG_LENGTH_MAX, message = "{artikelverwaltung.kategorie.bezeichnung.length}")
	@XmlElement(required = true)
	private String bezeichnung;

	@Column(name = "erzeugt", nullable = false)
	@XmlJavaTypeAdapter(XmlDateAdapter.class)
	private Timestamp erzeugt;
	
	@Column(name = "aktualisiert", nullable = false)
	@XmlJavaTypeAdapter(XmlDateAdapter.class)
	private Timestamp aktualisiert;
	
	@PrePersist
	protected void prePersist() {
		erzeugt = new Timestamp(new Date().getTime());
		aktualisiert = new Timestamp(new Date().getTime());
	}
	
	@PreUpdate
	protected void preUpdate() {
		aktualisiert = new Timestamp(new Date().getTime());
	}
	
	public Long getKategorieId() {
		return this.kategorieId;
	}

	public void setKategorieId(Long kategorieId) {
		
		this.kategorieId = kategorieId;
		
	}

	public String getBezeichnung() {
		return this.bezeichnung;
	}

	public void setBezeichnung(String bezeichnung) {
		this.bezeichnung = bezeichnung;
	}
	
	public Timestamp getErzeugt() {
		return erzeugt == null ? null : (Timestamp) erzeugt.clone();
	}

	public void setErzeugt(Timestamp erzeugt) {
		this.erzeugt = erzeugt == null ? null : (Timestamp) erzeugt.clone();
	}

	public Timestamp getAktualisiert() {
		return aktualisiert == null ? null : (Timestamp) aktualisiert.clone();
	}

	public void setAktualisiert(Timestamp aktualisiert) {
		this.aktualisiert = aktualisiert == null ? null : (Timestamp) aktualisiert.clone();
	}

	public Kategorie() {
	}
	
	public void setValues(Kategorie k) {
		bezeichnung = k.bezeichnung;
		
	}
	
	@Override
	public String toString() {
		return "Kategorie [kategorieId=" + kategorieId + ", aktualisiert="
				+ aktualisiert + ", bezeichnung=" + bezeichnung + ", erzeugt="
				+ erzeugt + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((bezeichnung == null) ? 0 : bezeichnung.hashCode());
		result = prime * result
				+ ((kategorieId == null) ? 0 : kategorieId.hashCode());
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
		Kategorie other = (Kategorie) obj;
		if (bezeichnung == null) {
			if (other.bezeichnung != null)
				return false;
		} 
		else if (!bezeichnung.equals(other.bezeichnung))
			return false;
		if (kategorieId == null) {
			if (other.kategorieId != null)
				return false;
		} 
		else if (!kategorieId.equals(other.kategorieId))
			return false;
		return true;
	}

}
	
	
	
	
