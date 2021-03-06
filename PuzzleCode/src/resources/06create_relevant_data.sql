INSERT INTO entities (name) 
	SELECT DISTINCT subject AS name  
	FROM yago_type; 



-- inserat into answer table 
-- use a temporary table

CREATE TABLE temp_answers (
answer varchar(50) NOT NULL,
entity varchar(100) NOT NULL,
additional_information varchar(25) 
);


LOAD DATA LOCAL INFILE  '???yagoHumanAnswers.tsv'
INTO TABLE temp_answers
	fields terminated by '\t'
	lines terminated by '\n'
	(entity, answer, additional_information);

INSERT INTO answers (answer, length, additional_information, entity_id)
	SELECT DISTINCT yago.answer, LENGTH(yago.answer), yago.additional_information, entities.id
	FROM yago_type AS yago, entities 
	WHERE yago.subject = entities.name; -- AND LENGTH(yago.answer) < 16;

INSERT INTO answers (answer, length, additional_information, entity_id)
	SELECT temp.answer, LENGTH(temp.answer), temp.additional_information, entities.id
	FROM temp_answers AS temp, entities
	WHERE entities.name = temp.entity;

DROP TABLE temp_answers;
-- 

-- inserat into ENTITY_DEFENITION
INSERT INTO entities_definitions (entity_id, definition_id)
	SELECT DISTINCT entities.id, def.id 
	FROM yago_type yago, definitions def, entities 
	WHERE yago.subject = entities.name AND yago.object = def.yago_type;

-- inserat into hints
INSERT INTO hints (predicate_id, yago_hint, entity_id, is_entity_subject)  
	SELECT p.id AS predicate_id, y.object AS yago_hint, e.id AS entity_id, y.is_subject AS is_entity_subject
	FROM predicates p, yago_fact y, entities e 
	WHERE p.yago_predicate=y.predicate AND e.name=y.subject AND y.is_subject;

INSERT INTO hints (predicate_id, yago_hint, entity_id, is_entity_subject)  
	SELECT p.id AS predicate_id, y.subject AS yago_hint, e.id AS entity_id, y.is_subject AS is_entity_subject
	FROM predicates p, yago_fact y, entities e 
	WHERE p.yago_predicate=y.predicate AND e.name=y.object AND not(y.is_subject);

INSERT INTO hints (predicate_id, yago_hint, entity_id, is_entity_subject)  
	SELECT p.id AS predicate_id, y.object AS yago_hint, e.id AS entity_id, true AS is_entity_subject
	FROM predicates p, yago_literal_fact y, entities e 
	WHERE p.yago_predicate=y.predicate AND e.name=y.subject;
