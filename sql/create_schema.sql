-- [schema-name] is temp
-- CREATE SCHEMA IF NOT EXISTS [schema-name];
-- USE [schema-name];
DROP TABLE IF EXISTS HINTS;
DROP TABLE IF EXISTS YAGO_LITERAL_FACT;
DROP TABLE IF EXISTS YAGO_FACT;
DROP TABLE IF EXISTS PREDICATES;
DROP TABLE IF EXISTS WORDS;
DROP TABLE IF EXISTS ENTITIES_DEFINITIONS;
DROP TABLE IF EXISTS ENTITIES;
DROP TABLE IF EXISTS YAGO_TYPE;
DROP TABLE IF EXISTS DEFINITIONS;
DROP TABLE IF EXISTS CATEGORIES;
-- ------------------------------------------------------------------------------------------------
CREATE TABLE CATEGORIES(
id int NOT NULL AUTO_INCREMENT, 
name varchar(50) NOT NULL,
PRIMARY KEY(id),
CONSTRAINT uc_CategoryNmae UNIQUE (name)
); 

CREATE TABLE DEFINITIONS(
id int NOT NULL AUTO_INCREMENT,
yago_type varchar(250) NOT NULL,
category_id int, 
definition varchar(500) NOT NULL,
CONSTRAINT fk_CategoryId FOREIGN KEY(category_id) REFERENCES CATEGORIES(id),
CONSTRAINT uc_YagoType UNIQUE (yago_type),
PRIMARY KEY(id)
);

CREATE TABLE YAGO_TYPE (
yago_id varchar(50), -- can we remove yagoId?
subject varchar(50), 
predicate varchar(50), 
object varchar(250), 
value float, -- can we remove value? 
CONSTRAINT fk_Object FOREIGN KEY(object) REFERENCES DEFINITIONS(yago_type)
);

CREATE TABLE ENTITIES (
id int NOT NULL AUTO_INCREMENT, 
name varchar(50) NOT NULL, 
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

CREATE TABLE WORDS (
id int NOT NULL AUTO_INCREMENT, 
word varchar(50) NOT NULL,
length int,
frequency int,
entity_id int, 
PRIMARY KEY(id), 
CONSTRAINT fk_EntityForWordId FOREIGN KEY(entity_id) REFERENCES ENTITIES(id),
CONSTRAINT uc_Words UNIQUE (word)
);

CREATE TABLE PREDICATES (
id int NOT NULL AUTO_INCREMENT, 
yago_predicate varchar(50) NOT NULL, -- predicate
subject_str varchar(250) NOT NULL,
object_str varchar(250) NOT NULL,
PRIMARY KEY(id),
CONSTRAINT uc_YagoPredicate UNIQUE (yago_predicate)
);

CREATE TABLE YAGO_FACT (
yago_id varchar(50), -- can we remove yago_id?
subject varchar(50), 
predicate varchar(50), 
object varchar(250), 
value float, -- can we remove value?
entity_id int, 
CONSTRAINT fk_PredicateFact FOREIGN KEY(predicate) REFERENCES PREDICATES(yago_predicate)
);

CREATE TABLE YAGO_LITERAL_FACT(
yago_id varchar(50), 
subject varchar(50), 
predicate varchar(50), 
object varchar(250), 
value float, 
entity_id int, 
CONSTRAINT fk_PredicateLiteralFact FOREIGN KEY(predicate) REFERENCES PREDICATES(yago_predicate)
);

CREATE TABLE HINTS (
id int NOT NULL AUTO_INCREMENT, 
predicate_id int NOT NULL, -- predicate
yago_hint varchar(50) NOT NULL, -- subject or object
entity_id int NOT NULL, 
is_entity_subject boolean NOT NULL, 
PRIMARY KEY(id),
CONSTRAINT fk_PredicateHintId FOREIGN KEY(predicate_id) REFERENCES PREDICATES(id),
CONSTRAINT fk_EntityHintId FOREIGN KEY(entity_id) REFERENCES ENTITIES(id)
);

-- ------------------------------------------------------------------------------------------------
INSERT INTO CATEGORIES (name) VALUES ('Geography');
INSERT INTO CATEGORIES (name) VALUES ('Music');
INSERT INTO CATEGORIES (name) VALUES ('Movies');
INSERT INTO CATEGORIES (name) VALUES ('Israel');

INSERT INTO DEFINITIONS (yago_type, category_id, definition) VALUES ('<wikicategory_Capitals_in_Europe>', 1, 'Capital in Europe');
INSERT INTO DEFINITIONS (yago_type, category_id, definition) VALUES ('<wikicategory_States_of_the_United_States>', 1, 'States of the United States');
INSERT INTO DEFINITIONS (yago_type, category_id, definition) VALUES ('<wikicategory_Former_United_States_state_capitals>', 1, 'United States state capital');
INSERT INTO DEFINITIONS (yago_type, category_id, definition) VALUES ('<wikicategory_English-language_singers>', 2, 'English-language singer');
INSERT INTO DEFINITIONS (yago_type, category_id, definition) VALUES ('<wikicategory_Jewish_American_musicians>', 2, 'Jewish American musician');
INSERT INTO DEFINITIONS (yago_type, category_id, definition) VALUES ('<wikicategory_Jewish_poets>', 2, 'Jewish poet');
INSERT INTO DEFINITIONS (yago_type, category_id, definition) VALUES ('<wikicategory_2000s_comedy_films>', 3, '2000s comedy film');
INSERT INTO DEFINITIONS (yago_type, category_id, definition) VALUES ('<wordnet_movie_106613686>', 3, 'wordnet_movie XXXXXXX need to choose better types');
INSERT INTO DEFINITIONS (yago_type, category_id, definition) VALUES ('<wikicategory_2013_films>', 3, '2013 film');
INSERT INTO DEFINITIONS (yago_type, category_id, definition) VALUES ('<wikicategory_Israeli_rock_singers>', 4, 'Israeli rock singer');
INSERT INTO DEFINITIONS (yago_type, category_id, definition) VALUES ('<wikicategory_Israeli_artists>', 4, 'Israeli artist');
INSERT INTO DEFINITIONS (yago_type, category_id, definition) VALUES ('<wikicategory_Israeli_children\'s_writers>', 4, 'Israeli children\'s writer');
INSERT INTO DEFINITIONS (yago_type, category_id, definition) VALUES ('<wikicategory_Israeli_basketball_players>', 4, 'Israeli basketball players');

LOAD DATA LOCAL INFILE 'c:\\Users\\kleins\\tau\\yago\\yagoTypesFromYonatan.tsv' 
INTO TABLE YAGO_TYPE
	fields terminated by '\t'
	lines terminated by '\n'
	(yago_id,subject,predicate,object,value);

INSERT INTO ENTITIES (name) 
	SELECT DISTINCT subject AS name  
	FROM yago_type; 

INSERT INTO ENTITIES_DEFINITIONS (entity_id, definition_id)
	SELECT DISTINCT e.id AS entity_id, d.id AS definition_id
	FROM yago_type t, definitions d, entities e 
	WHERE d.yago_type=t.object AND t.subject=e.name; 

INSERT INTO WORDS (word, length, entity_id)
	SELECT TRIM(TRAILING '>' FROM TRIM(LEADING '<' FROM name)) AS name, char_length(name)-2 AS length, id 
	FROM entities
	WHERE name not like '%\_%' AND char_length(name) <=12 and TRIM(TRAILING '>' FROM TRIM(LEADING '<' FROM name)) REGEXP  '^[A-Za-z0-9]+$';
-- INSERT INTO WORDS (word, length, entity_id)
-- SELECT REPLACE(entity_name, '_', '') AS name , LENGTH(REPLACE(entity_name, '_', '')) AS length, id 
-- 	FROM (
-- 		SELECT TRIM(TRAILING '>' FROM TRIM(LEADING '<' FROM name)) AS entity_name, char_length(name)-2 AS length, id 
-- 		FROM entities
-- 		WHERE name  like '%\_%') AS more_than_one_word_entity
-- 	WHERE LENGTH(REPLACE(entity_name, '_', '')) <= 12 AND REPLACE(entity_name, '_', '') REGEXP  '^[A-Za-z0-9]+$';

INSERT INTO PREDICATES (yago_predicate, subject_str, object_str) VALUES ('<actedIn>' , '? was acted in it', 'acted in ?');
INSERT INTO PREDICATES (yago_predicate, subject_str, object_str) VALUES ('<wasBornOnDate>', 'was born on ? date', 'BUG_IF_EXIST');

LOAD DATA LOCAL INFILE 'c:\\Users\\kleins\\tau\\yago\\yagoFactsFromYonatan.tsv' 
INTO TABLE YAGO_FACT
	fields terminated by '\t'
	lines terminated by '\n'
	(yago_id,subject,predicate,object,value, entity_id);

LOAD DATA LOCAL INFILE 'c:\\Users\\kleins\\tau\\yago\\yagoLiteralFactsFromYonatan.tsv' 
INTO TABLE YAGO_LITERAL_FACT
	fields terminated by '\t'
	lines terminated by '\n'
	(yago_id,subject,predicate,object,value);

	
-- ------------------------------------------------------------------------------------------------
-- We should think about this part (index) 	
CREATE INDEX yagoFactIndexSubject ON YAGO_FACT(subject);
CREATE INDEX yagoFactIndexPredicate ON YAGO_FACT(predicate);
CREATE INDEX yagoFactIndexSubjectPredicate ON YAGO_FACT(subject,predicate);
CREATE INDEX yagoFactIndexObject ON YAGO_FACT(object);

CREATE INDEX yagoTypeIndexSubject ON YAGO_TYPE(subject);
CREATE INDEX yagoTypeIndexPredicate ON YAGO_TYPE(predicate);
CREATE INDEX yagoTypeIndexSubjectPredicate ON YAGO_TYPE(subject,predicate);
CREATE INDEX yagoTypeIndexObject ON YAGO_TYPE(object);

CREATE INDEX yagoLiteralIndexSubject ON YAGO_LITERAL_FACT(subject);
CREATE INDEX yagoLiteralIndexPredicate ON YAGO_LITERAL_FACT(predicate);
CREATE INDEX yagoLiteralIndexSubjectPredicate ON YAGO_LITERAL_FACT(subject,predicate);
CREATE INDEX yagdefinitionsoLiteralIndexObject ON YAGO_LITERAL_FACT(object);