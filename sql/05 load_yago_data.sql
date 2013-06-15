LOAD DATA LOCAL INFILE  '???yagoTypes.tsv'
INTO TABLE YAGO_TYPE
	fields terminated by '\t'
	lines terminated by '\n'
	(subject, predicate, object, answer, additional_information);

LOAD DATA LOCAL INFILE  '???yagoFacts.tsv'
INTO TABLE YAGO_FACT
	fields terminated by '\t'
	lines terminated by '\n'
	(subject, predicate, object, is_subject);

LOAD DATA LOCAL INFILE  '???yagoLiteralFacts.tsv'
INTO TABLE YAGO_LITERAL_FACT
	fields terminated by '\t'
	lines terminated by '\n'
	(subject, predicate, object);