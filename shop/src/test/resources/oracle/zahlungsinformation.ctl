OPTIONS(direct=true)
UNRECOVERABLE LOAD DATA
INTO TABLE zahlungsinformation
APPEND
FIELDS TERMINATED BY ';' OPTIONALLY ENCLOSED BY '"' (
zahl_id,kontoinhaber,kontonummer,blz,kreditinstitut,swift,iban,erzeugt,aktualisiert)