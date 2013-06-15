DROP TABLE IF EXISTS YAGO_TYPE;
CREATE TABLE YAGO_TYPE ( 
subject varchar(50), 
predicate varchar(50), 
object varchar(250), 
proper_name VARCHAR(50) NOT NULL, 
add_info VARCHAR(30), 
id int NOT NULL AUTO_INCREMENT,
PRIMARY KEY(id)
);

LOAD DATA LOCAL INFILE 'c:\\users\\yonatan\\temp_yago_files\\filtered_tsv_files\\yagoTypes.tsv' 
INTO TABLE YAGO_TYPE
fields terminated by '\t'
lines terminated by '\n'
(subject,predicate,object,proper_name, add_info);

DROP TABLE IF EXISTS YAGO_FACT;
CREATE TABLE YAGO_FACT (
subject varchar(50), 
predicate varchar(50), 
object varchar(250), 
is_subject_hit TINYINT NOT NULL,
id int NOT NULL AUTO_INCREMENT, 
PRIMARY KEY(id)
);

LOAD DATA LOCAL INFILE 'c:\\users\\yonatan\\temp_yago_files\\filtered_tsv_files\\yagoFacts.tsv' 
INTO TABLE YAGO_FACT
fields terminated by '\t'
lines terminated by '\n'
(subject,predicate,object,is_subject_hit);

DROP TABLE IF EXISTS ENTITY_ANSWER;
CREATE TABLE ENTITY_ANSWER ( 
entity varchar(50) NOT NULL,
answer varchar(50) NOT NULL,
answer_type varchar(50) NOT NULL, 
id int NOT NULL AUTO_INCREMENT, 
PRIMARY KEY(id)
);

LOAD DATA LOCAL INFILE 'c:\\users\\yonatan\\temp_yago_files\\filtered_tsv_files\\yagoHumanAnswers.tsv' 
INTO TABLE ENTITY_ANSWER
fields terminated by '\t'
lines terminated by '\n'
(entity,answer,answer_type);

DROP TABLE IF EXISTS YAGO_LITERAL_FACT;
CREATE TABLE YAGO_LITERAL_FACT ( 
subject varchar(50), 
predicate varchar(50), 
object varchar(250),
id int NOT NULL AUTO_INCREMENT, 
PRIMARY KEY(id)
);

LOAD DATA LOCAL INFILE 'c:\\users\\yonatan\\temp_yago_files\\filtered_tsv_files\\yagoLiteralFacts.tsv' 
INTO TABLE YAGO_LITERAL_FACT
fields terminated by '\t'
lines terminated by '\n'
(subject,predicate,object);


