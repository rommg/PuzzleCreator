LOAD DATA LOCAL INFILE  '???yagoTypes.tsv'
INTO TABLE yago_type
	fields terminated by '\t'
	lines terminated by '\n'
	(subject, predicate, object, answer, additional_information);

LOAD DATA LOCAL INFILE  '???yagoFacts.tsv'
INTO TABLE yago_fact
	fields terminated by '\t'
	lines terminated by '\n'
	(subject, predicate, object, is_subject);

LOAD DATA LOCAL INFILE  '???yagoLiteralFacts.tsv'
INTO TABLE yago_literal_fact
	fields terminated by '\t'
	lines terminated by '\n'
	(subject, predicate, object);