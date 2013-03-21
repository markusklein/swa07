OPTIONS(direct=true)
UNRECOVERABLE LOAD DATA
INTO TABLE bestellposition
APPEND
FIELDS TERMINATED BY ';' OPTIONALLY ENCLOSED BY '"' (
  bestellpos_id,
  bestell_id,
  bestellmenge,
  status,
  artikel_id,
  erzeugt,
  aktualisiert,
  idx)