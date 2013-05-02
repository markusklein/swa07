package de.shop.artikelverwaltung.service;
import javax.ejb.ApplicationException;

import de.shop.util.AbstractShopException;

@ApplicationException(rollback = true)
public class KategorieExistsException extends AbstractShopException { 
	

	private static final long serialVersionUID = 4867667611097919943L;
	private final Long id;
	
	public KategorieExistsException(Long id) {
		super("Die ID " + id + " existiert bereits");
		this.id = id;
	}

	public Long getId() {
		return id;
	}
}

