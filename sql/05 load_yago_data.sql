LOAD DATA LOCAL INFILE  '<git dir>\PuzzleCreator\\sql\\filesToLoad\\yagoTypes.tsv'
INTO TABLE YAGO_TYPE
	fields terminated by '\t'
	lines terminated by '\n'
	(subject, predicate, object, answer, additional_information);

LOAD DATA LOCAL INFILE  '<git dir>\PuzzleCreator\\sql\\filesToLoad\\yagoFacts.tsv'
INTO TABLE YAGO_FACT
	fields terminated by '\t'
	lines terminated by '\n'
	(subject, predicate, object, is_subject);

LOAD DATA LOCAL INFILE  '<git dir>\PuzzleCreator\\sql\\filesToLoad\\yagoLiteralFacts.tsv'
INTO TABLE YAGO_LITERAL_FACT
	fields terminated by '\t'
	lines terminated by '\n'
	(subject, predicate, object);

select distinct object 
from temp.yago_type 
where  NOT EXISTS (select yago_type from temp.definitions where yago_type=object);

select distinct predicate 
from temp.yago_fact 
where NOT EXISTS (select yago_predicate from temp.predicates where yago_predicate=predicate);

select distinct predicate 
from temp.yago_literal_fact 
where NOT EXISTS (select yago_predicate from temp.predicates where yago_predicate=predicate);
