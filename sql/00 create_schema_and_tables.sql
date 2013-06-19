-- [schema-name] is DbMysql02
CREATE SCHEMA IF NOT EXISTS DbMysql02;
USE DbMysql02;

-- DROP TABLE IF EXISTS BEST_SCORES;
DROP TABLE IF EXISTS hints;
DROP TABLE IF EXISTS answers;
DROP TABLE IF EXISTS entities_definitions;
DROP TABLE IF EXISTS entities;
DROP TABLE IF EXISTS yago_literal_fact;
DROP TABLE IF EXISTS yago_fact;
DROP TABLE IF EXISTS yago_type;
-- DROP TABLE IF EXISTS predicates;
-- DROP TABLE IF EXISTS definitions_topics;
-- DROP TABLE IF EXISTS definitions;
-- DROP TABLE IF EXISTS topics;

-- CREATE TABLE topics(
-- id int NOT NULL AUTO_INCREMENT, 
-- name varchar(50) NOT NULL,
-- PRIMARY KEY(id),
-- CONSTRAINT uc_CategoryName UNIQUE (name)
-- ); 

-- CREATE TABLE definitions(
-- id int NOT NULL AUTO_INCREMENT,
-- yago_type varchar(250) NOT NULL,
-- definition varchar(500) NOT NULL,
-- PRIMARY KEY(id),
-- CONSTRAINT uc_YagoTypeDefinition UNIQUE (yago_type)
-- );

-- CREATE TABLE definitions_topics(
-- definition_id int NOT NULL, 
-- topic_id int NOT NULL,
-- PRIMARY KEY(definition_id, topic_id),
-- CONSTRAINT fk_TopicId FOREIGN KEY(topic_id) REFERENCES topics(id),
-- CONSTRAINT fk_DefinitionId FOREIGN KEY(definition_id) REFERENCES definitions(id)
-- ); 


-- CREATE TABLE predicates (
-- id int NOT NULL AUTO_INCREMENT, 
-- yago_predicate varchar(50) NOT NULL,
-- subject_str varchar(250),
-- object_str varchar(250),
-- PRIMARY KEY(id),
-- CONSTRAINT uc_YagoPredicate UNIQUE (yago_predicate)
-- );


CREATE TABLE yago_type (
subject varchar(100) NOT NULL, 
predicate varchar(50), 
object varchar(250) NOT NULL, 
answer varchar(50), 
additional_information varchar(25),
CONSTRAINT fk_Object FOREIGN KEY(object) REFERENCES definitions(yago_type)
);

CREATE TABLE yago_fact (
subject varchar(100), 
predicate varchar(50), 
object varchar(250), 
is_subject boolean, 
CONSTRAINT fk_PredicateFact FOREIGN KEY(predicate) REFERENCES predicates(yago_predicate)
);

CREATE TABLE yago_literal_fact( 
subject varchar(100), 
predicate varchar(50), 
object varchar(250), 
CONSTRAINT fk_PredicateLiteralFact FOREIGN KEY(predicate) REFERENCES predicates(yago_predicate)
);

CREATE TABLE entities (
id int NOT NULL AUTO_INCREMENT, 
name varchar(100) NOT NULL, 
PRIMARY KEY(id),
CONSTRAINT uc_EntityName UNIQUE (name)
);

CREATE TABLE entities_definitions (
entity_id int NOT NULL, 
definition_id int NOT NULL, 
CONSTRAINT fk_EntityForEntitiyDefinitionId FOREIGN KEY(entity_id) REFERENCES entities(id),
CONSTRAINT fk_DefinitionForEntityDefinitionId FOREIGN KEY(definition_id) REFERENCES definitions(id),
CONSTRAINT uc_EntityCategory UNIQUE (entity_id, definition_id)
);

CREATE TABLE answers (
id int NOT NULL AUTO_INCREMENT, 
answer varchar(50) NOT NULL,
length int,
entity_id int, 
additional_information varchar(25),
PRIMARY KEY(id), 
CONSTRAINT fk_EntityForWordId FOREIGN KEY(entity_id) REFERENCES entities(id)
-- CONSTRAINT uc_Answers UNIQUE (answer)
);

CREATE TABLE hints (
id int NOT NULL AUTO_INCREMENT, 
-- predicate
predicate_id int NOT NULL,
-- subject or object
yago_hint varchar(250) DEFAULT '<User_Hint>',
entity_id int NOT NULL, 
is_entity_subject boolean NOT NULL, 
PRIMARY KEY(id),
CONSTRAINT fk_PredicateHintId FOREIGN KEY(predicate_id) REFERENCES predicates(id),
CONSTRAINT fk_EntityHintId FOREIGN KEY(entity_id) REFERENCES entities(id)
);

-- CREATE TABLE IF NOT EXISTS BEST_SCORES (
-- user_name varchar(100) NOT NULL, 
-- score int NOT NULL, 
-- date date NOT NULL
-- );

-- ------------------------------------------------------------------------------------------------
