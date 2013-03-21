CREATE SEQUENCE hibernate_sequence START WITH 5000;

CREATE TABLE kategorie (
  kategorie_id INTEGER NOT NULL PRIMARY KEY ,
  bezeichnung VARCHAR2(20) NOT NULL ,
  erzeugt TIMESTAMP NOT NULL ,
  aktualisiert TIMESTAMP NOT NULL
);

CREATE TABLE artikel (
  artikel_id INTEGER NOT NULL PRIMARY KEY ,
  name VARCHAR2(45) NOT NULL UNIQUE ,
  preis DECIMAL NOT NULL ,
  beschreibung VARCHAR2(45) NOT NULL ,
  kategorie_id INTEGER NOT NULL REFERENCES kategorie(kategorie_id),
  erzeugt TIMESTAMP NOT NULL ,
  aktualisiert TIMESTAMP NOT NULL
);

CREATE INDEX fk_artikel_kategorie_idx ON artikel(kategorie_id) ;

CREATE TABLE adresse (
  adresse_id INTEGER NOT NULL PRIMARY KEY,
  strasse VARCHAR2(60) NOT NULL,
  plz VARCHAR2(25) NOT NULL,
  ort VARCHAR2(45) NOT NULL,
  land VARCHAR2(2) NOT NULL,
  erzeugt TIMESTAMP NOT NULL,
  aktualisiert TIMESTAMP NOT NULL,
  CONSTRAINT cons_adresse_land CHECK (land IN ('DE', 'CH', 'A'))
);

CREATE TABLE zahlungsinformation (
  zahl_id INTEGER NOT NULL PRIMARY KEY ,
  kontoinhaber VARCHAR2(45) NOT NULL ,
  kontonummer INTEGER NOT NULL ,
  blz INTEGER NOT NULL ,
  kreditinstitut VARCHAR2(60) NOT NULL ,
  swift VARCHAR2(45) NOT NULL ,
  iban VARCHAR2(45) NOT NULL ,
  erzeugt TIMESTAMP NOT NULL ,
  aktualisiert TIMESTAMP NOT NULL
);

CREATE TABLE kunde (
  kunde_id INTEGER NOT NULL PRIMARY KEY,
  vorname VARCHAR2(30) NOT NULL,
  nachname VARCHAR2(30) NOT NULL,
  email VARCHAR2(45) NOT NULL UNIQUE,
  geschlecht CHAR(1) NOT NULL,
  passwort VARCHAR2(45) NOT NULL,
  telefonnummer VARCHAR2(45) NOT NULL ,
  lieferadresse INTEGER NOT NULL REFERENCES adresse(adresse_id),
  rechnungsadresse INTEGER NOT NULL REFERENCES adresse(adresse_id),
  zahlungsinformation_id INTEGER NOT NULL REFERENCES zahlungsinformation(zahl_id),
  geburtsdatum DATE NOT NULL,
  erzeugt TIMESTAMP NOT NULL,
  aktualisiert TIMESTAMP NOT NULL,
  CONSTRAINT cons_kunde_geschlecht CHECK (geschlecht IN ('M', 'W'))
);

CREATE INDEX fk_kunde_lieferadresse_idx ON kunde(lieferadresse) ;
CREATE INDEX fk_kunde_rechnungsadresse_idx ON kunde(rechnungsadresse) ;
CREATE INDEX fk_kunde_zinf_idx ON kunde(zahlungsinformation_id) ;

CREATE TABLE bestellung (
  bestell_id INTEGER NOT NULL PRIMARY KEY ,
  status VARCHAR2(11) NOT NULL ,
  kunde_id INTEGER NOT NULL REFERENCES kunde(kunde_id) ,
  lieferverfolgungsnummer VARCHAR2(45) NULL UNIQUE,
  erzeugt TIMESTAMP NOT NULL ,
  aktualisiert TIMESTAMP NOT NULL ,
  idx SMALLINT,
  CONSTRAINT cons_bestellung_status CHECK (status IN ('OFFEN', 'FREIGEGEBEN', 'STORNIERT', 'VERSENDET'))
);

CREATE INDEX fk_bestellung_kunde_idx ON bestellung(kunde_id) ;

CREATE TABLE bestellposition (
  bestellpos_id INTEGER NOT NULL PRIMARY KEY ,
  bestell_id INTEGER NOT NULL REFERENCES bestellung(bestell_id) ,
  bestellmenge INTEGER NOT NULL ,
  status VARCHAR2(11) NOT NULL ,
  artikel_id INTEGER NOT NULL REFERENCES artikel(artikel_id) ,
  erzeugt TIMESTAMP NOT NULL ,
  aktualisiert TIMESTAMP NOT NULL ,
  idx SMALLINT,
  CONSTRAINT cons_bpos_status CHECK (status IN ('OFFEN', 'BESTAETIGT', 'STORNIERT'))
);
	
CREATE INDEX fk_bpos_bestellung_idx ON bestellposition(bestell_id) ;
CREATE INDEX fk_bpos_artikel_idx ON bestellposition(artikel_id) ;