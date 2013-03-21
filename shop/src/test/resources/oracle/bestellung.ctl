OPTIONS(direct=true)
UNRECOVERABLE LOAD DATA
INTO TABLE bestellung
APPEND
FIELDS TERMINATED BY ';' OPTIONALLY ENCLOSED BY '"' (
  bestell_id,
  status,
  kunde_id,
  lieferverfolgungsnummer,
  erzeugt,
  aktualisiert,
  idx)


