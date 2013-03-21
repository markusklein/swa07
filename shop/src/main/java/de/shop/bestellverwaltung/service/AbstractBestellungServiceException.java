package de.shop.bestellverwaltung.service;

import de.shop.util.AbstractShopException;

public abstract class AbstractBestellungServiceException extends AbstractShopException {


	/**
	 * 
	 */
	private static final long serialVersionUID = 3946346877973081221L;

	public AbstractBestellungServiceException(String msg) {
		super(msg);
	}
}
