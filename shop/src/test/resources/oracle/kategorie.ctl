OPTIONS(direct=true)
UNRECOVERABLE LOAD DATA
INTO TABLE kategorie
APPEND
FIELDS TERMINATED BY ';' OPTIONALLY ENCLOSED BY '"' (
	kategorie_id,
	bezeichnung,
	erzeugt,
	aktualisiert)