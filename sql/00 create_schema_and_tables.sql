-- [schema-name] is RIDDLE
CREATE SCHEMA IF NOT EXISTS RIDDLE;
USE RIDDLE;

DROP TABLE IF EXISTS HINTS;
DROP TABLE IF EXISTS ANSWERS;
DROP TABLE IF EXISTS ENTITIES_DEFINITIONS;
DROP TABLE IF EXISTS ENTITIES;
DROP TABLE IF EXISTS YAGO_LITERAL_FACT;
DROP TABLE IF EXISTS YAGO_FACT;
DROP TABLE IF EXISTS YAGO_TYPE;
DROP TABLE IF EXISTS PREDICATES;
DROP TABLE IF EXISTS DEFINITIONS_TOPICS;
DROP TABLE IF EXISTS DEFINITIONS;
DROP TABLE IF EXISTS TOPICS;

CREATE TABLE TOPICS(
id int NOT NULL AUTO_INCREMENT, 
name varchar(50) NOT NULL,
PRIMARY KEY(id),
CONSTRAINT uc_CategoryName UNIQUE (name)
); 

CREATE TABLE DEFINITIONS(
id int NOT NULL AUTO_INCREMENT,
yago_type varchar(250) NOT NULL,
definition varchar(500) NOT NULL,
CONSTRAINT uc_YagoType UNIQUE (yago_type),
PRIMARY KEY(id)
);

CREATE TABLE DEFINITIONS_TOPICS(
definition_id int NOT NULL, 
topic_id int NOT NULL,
PRIMARY KEY(definition_id, topic_id),
CONSTRAINT fk_TopicId FOREIGN KEY(topic_id) REFERENCES TOPICS(id),
CONSTRAINT fk_DefinitionId FOREIGN KEY(definition_id) REFERENCES DEFINITIONS(id)
); 


CREATE TABLE PREDICATES (
id int NOT NULL AUTO_INCREMENT, 
-- predicate
yago_predicate varchar(50) NOT NULL,
subject_str varchar(250),
object_str varchar(250),
PRIMARY KEY(id),
CONSTRAINT uc_YagoPredicate UNIQUE (yago_predicate)
);


CREATE TABLE YAGO_TYPE (
subject varchar(100) NOT NULL, 
predicate varchar(50), 
object varchar(250) NOT NULL, 
answer varchar(50), 
additional_information varchar(25),
CONSTRAINT fk_Object FOREIGN KEY(object) REFERENCES DEFINITIONS(yago_type)
);

CREATE TABLE YAGO_FACT (
subject varchar(100), 
predicate varchar(50), 
object varchar(250), 
is_subject boolean, 
CONSTRAINT fk_PredicateFact FOREIGN KEY(predicate) REFERENCES PREDICATES(yago_predicate)
);

CREATE TABLE YAGO_LITERAL_FACT( 
subject varchar(100), 
predicate varchar(50), 
object varchar(250), 
CONSTRAINT fk_PredicateLiteralFact FOREIGN KEY(predicate) REFERENCES PREDICATES(yago_predicate)
);

CREATE TABLE ENTITIES (
id int NOT NULL AUTO_INCREMENT, 
name varchar(100) NOT NULL, 
PRIMARY KEY(id),
CONSTRAINT uc_EntityName UNIQUE (name)
);

CREATE TABLE ENTITIES_DEFINITIONS (
entity_id int NOT NULL, 
definition_id int NOT NULL, 
CONSTRAINT fk_EntityForEntitiyDefinitionId FOREIGN KEY(entity_id) REFERENCES ENTITIES(id),
CONSTRAINT fk_DefinitionForEntityDefinitionId FOREIGN KEY(definition_id) REFERENCES DEFINITIONS(id),
CONSTRAINT uc_EntityCategory UNIQUE (entity_id, definition_id)
);

CREATE TABLE ANSWERS (
id int NOT NULL AUTO_INCREMENT, 
answer varchar(50) NOT NULL,
length int,
frequency int,
entity_id int, 
additional_information varchar(20),
PRIMARY KEY(id), 
CONSTRAINT fk_EntityForWordId FOREIGN KEY(entity_id) REFERENCES ENTITIES(id)
-- CONSTRAINT uc_Answers UNIQUE (answer)
);

CREATE TABLE HINTS (
id int NOT NULL AUTO_INCREMENT, 
-- predicate
predicate_id int NOT NULL,
-- subject or object
yago_hint varchar(250) NOT NULL,
entity_id int NOT NULL, 
is_entity_subject boolean NOT NULL, 
PRIMARY KEY(id),
CONSTRAINT fk_PredicateHintId FOREIGN KEY(predicate_id) REFERENCES PREDICATES(id),
CONSTRAINT fk_EntityHintId FOREIGN KEY(entity_id) REFERENCES ENTITIES(id)
);


CREATE TABLE BEST_SCORES (
user_name varchar(100) NOT NULL, 
score int NOT NULL, 
date datetime NOT NULL
);

-- ------------------------------------------------------------------------------------------------
