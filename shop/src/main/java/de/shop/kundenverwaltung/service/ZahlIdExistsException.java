package de.shop.kundenverwaltung.service;

import javax.ejb.ApplicationException;

import de.shop.util.AbstractShopException;

@ApplicationException(rollback = true)
public class ZahlIdExistsException extends AbstractShopException {

	private static final long serialVersionUID = -1981592500591612454L;
	private final long zahlId;
	

	public ZahlIdExistsException(long zahlId) {
		super("Die ZahlId " + zahlId + "existiert bereits");
		this.zahlId = zahlId;
	}
	
	public long getZahlId() {
		
		return zahlId;
	}
	

}
