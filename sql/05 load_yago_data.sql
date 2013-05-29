LOAD DATA LOCAL INFILE  'c:\\Users\\yonatan\\PuzzleCreator\\sql\\filesToLoad\\yagoTypes.tsv'
INTO TABLE YAGO_TYPE
	fields terminated by '\t'
	lines terminated by '\n'
	(subject, predicate, object, answer, additional_information);

LOAD DATA LOCAL INFILE  'c:\\Users\\yonatan\\PuzzleCreator\\sql\\filesToLoad\\yagoFacts.tsv'
INTO TABLE YAGO_FACT
	fields terminated by '\t'
	lines terminated by '\n'
	(subject, predicate, object, is_subject);

LOAD DATA LOCAL INFILE  'c:\\Users\\yonatan\\PuzzleCreator\\sql\\filesToLoad\\yagoLiteralFacts.tsv'
INTO TABLE YAGO_LITERAL_FACT
	fields terminated by '\t'
	lines terminated by '\n'
	(subject, predicate, object);

select distinct object 
from riddle.yago_type 
where  NOT EXISTS (select yago_type from riddle.definitions where yago_type=object);

select distinct predicate 
from riddle.yago_fact 
where NOT EXISTS (select yago_predicate from riddle.predicates where yago_predicate=predicate);

select distinct predicate 
from riddle.yago_literal_fact 
where NOT EXISTS (select yago_predicate from riddle.predicates where yago_predicate=predicate);
