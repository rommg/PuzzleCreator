DROP TABLE IF EXISTS DEFINITION;
CREATE TABLE DEFINITION ( 
yago_type varchar(100), 
definition varchar(250),
category_name varchar(50), 
id int NOT NULL AUTO_INCREMENT, 
PRIMARY KEY(id)
);

LOAD DATA LOCAL INFILE 'c:\\users\\yonatan\\yago_types_categories.csv' 
INTO TABLE DEFINITION
fields terminated by ','
lines terminated by '\n'
(yago_type,definition,category_name);


DROP TABLE IF EXISTS PREDICATE;
CREATE TABLE PREDICATE ( 
yago_predicate varchar(50), 
subject_str varchar(250), 
object_str varchar(250), 
id int NOT NULL AUTO_INCREMENT, 
PRIMARY KEY(id)
);

LOAD DATA LOCAL INFILE 'c:\\users\\yonatan\\yago_facts_predicates.csv' 
INTO TABLE PREDICATE
fields terminated by ','
lines terminated by '\n'
(yago_predicate, subject_str,object_str);

DROP TABLE IF EXISTS LITERAL;
CREATE TABLE LITERAL ( 
yago_literal_fact varchar(50), 
subject_str varchar(250), 
id int NOT NULL AUTO_INCREMENT, 
PRIMARY KEY(id)
);

LOAD DATA LOCAL INFILE 'c:\\users\\yonatan\\yago_facts_literals_predicates.csv' 
INTO TABLE LITERAL
fields terminated by ','
lines terminated by '\n'
(yago_literal_fact,subject_str, @ignore);
