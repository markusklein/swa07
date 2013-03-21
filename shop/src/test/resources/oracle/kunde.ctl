OPTIONS(direct=true)
UNRECOVERABLE LOAD DATA
INTO TABLE kunde
APPEND
FIELDS TERMINATED BY ';' OPTIONALLY ENCLOSED BY '"' (
kunde_id,
vorname,
nachname,
email,
geschlecht,
passwort,
telefonnummer,
lieferadresse,
rechnungsadresse,
zahlungsinformation_id,
geburtsdatum,
erzeugt,
aktualisiert
)