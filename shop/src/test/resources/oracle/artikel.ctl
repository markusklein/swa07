OPTIONS(direct=true)
UNRECOVERABLE LOAD DATA
INTO TABLE artikel
APPEND
FIELDS TERMINATED BY ';' OPTIONALLY ENCLOSED BY '"' (
	artikel_id,
	name,
	preis,
	beschreibung,
	kategorie_id,
	erzeugt,
	aktualisiert)
