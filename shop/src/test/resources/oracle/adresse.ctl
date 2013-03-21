OPTIONS(direct=true)
UNRECOVERABLE LOAD DATA
INTO TABLE adresse
APPEND
FIELDS TERMINATED BY ';' OPTIONALLY ENCLOSED BY '"' (
	adresse_id,
	strasse,
	plz,
	ort,
	land,
	erzeugt,
	aktualisiert)
