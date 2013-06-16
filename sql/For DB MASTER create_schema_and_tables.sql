-- [schema-name] is DbMysql02
CREATE SCHEMA IF NOT EXISTS DbMysql02;
USE DbMysql02;

DROP TABLE IF EXISTS best_scores;
DROP TABLE IF EXISTS hints;
DROP TABLE IF EXISTS answers;
DROP TABLE IF EXISTS entities_definitions;
DROP TABLE IF EXISTS entities;
DROP TABLE IF EXISTS yago_literal_fact;
DROP TABLE IF EXISTS yago_fact;
DROP TABLE IF EXISTS yago_type;
DROP TABLE IF EXISTS predicates;
DROP TABLE IF EXISTS definitions_topics;
DROP TABLE IF EXISTS definitions;
DROP TABLE IF EXISTS topics;
DROP TABLE IF EXISTS temp_answers;

CREATE TABLE topics(
id int NOT NULL AUTO_INCREMENT, 
name varchar(50) NOT NULL,
PRIMARY KEY(id),
CONSTRAINT uc_CategoryName UNIQUE (name)
); 

CREATE TABLE definitions(
id int NOT NULL AUTO_INCREMENT,
yago_type varchar(250) NOT NULL,
definition varchar(500) NOT NULL,
PRIMARY KEY(id),
CONSTRAINT uc_YagoTypeDefinition UNIQUE (yago_type)
);

CREATE TABLE definitions_topics(
definition_id int NOT NULL, 
topic_id int NOT NULL,
PRIMARY KEY(definition_id, topic_id),
CONSTRAINT fk_TopicId FOREIGN KEY(topic_id) REFERENCES topics(id),
CONSTRAINT fk_DefinitionId FOREIGN KEY(definition_id) REFERENCES definitions(id)
); 


CREATE TABLE predicates(
id int NOT NULL AUTO_INCREMENT, 
-- predicate
yago_predicate varchar(50) NOT NULL,
subject_str varchar(250),
object_str varchar(250),
PRIMARY KEY(id),
CONSTRAINT uc_YagoPredicate UNIQUE (yago_predicate)
);


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

CREATE TABLE yago_literal_fact ( 
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
frequency int,
entity_id int, 
additional_information varchar(25),
PRIMARY KEY(id), 
CONSTRAINT fk_EntityForWordId FOREIGN KEY(entity_id) REFERENCES entities(id)
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

CREATE TABLE IF NOT EXISTS best_scores (
user_name varchar(100) NOT NULL, 
score int NOT NULL, 
date datetime NOT NULL
);

-- ------------------------------------------------------------------------------------------------
INSERT INTO topics (id, name) VALUES (1, 'Cinema & TV');
INSERT INTO topics (id, name) VALUES (2, 'General Knowledge');
INSERT INTO topics (id, name) VALUES (3, 'Geography');
INSERT INTO topics (id, name) VALUES (4, 'Israel');
INSERT INTO topics (id, name) VALUES (5, 'Music');
INSERT INTO topics (id, name) VALUES (6, 'Personalities');
INSERT INTO topics (id, name) VALUES (7, 'Sports');
INSERT INTO topics (id, name) VALUES (8, 'User Updates');

INSERT INTO definitions (id, yago_type, definition) VALUES (1, '<wikicategory_1990s_drama_films>', '1990s drama film');
INSERT INTO definitions (id, yago_type, definition) VALUES (2, '<wikicategory_20th-century_actors>', '20th-century actor');
INSERT INTO definitions (id, yago_type, definition) VALUES (3, '<wikicategory_21st-century_actors>', '21st-century actor');
INSERT INTO definitions (id, yago_type, definition) VALUES (4, '<wikicategory_American_criminal_comedy_films>', 'American criminal comedy film');
INSERT INTO definitions (id, yago_type, definition) VALUES (5, '<wikicategory_American_film_directors>', 'American film director');
INSERT INTO definitions (id, yago_type, definition) VALUES (6, '<wikicategory_Australian_soap_opera_actors>', 'Australian soap opera actor');
INSERT INTO definitions (id, yago_type, definition) VALUES (7, '<wikicategory_Canadian_film_actors>', 'Canadian film actor');
INSERT INTO definitions (id, yago_type, definition) VALUES (8, '<wikicategory_Star_Trek:_Enterprise_characters>', 'Character in Star Trek: Enterprise');
INSERT INTO definitions (id, yago_type, definition) VALUES (9, '<wikicategory_2000s_crime_films>', 'Crime film from the 2000s');
INSERT INTO definitions (id, yago_type, definition) VALUES (10, '<wikicategory_2000s_drama_films>', 'Drama film from the 2000s ');
INSERT INTO definitions (id, yago_type, definition) VALUES (11, '<wikicategory_English_film_actors>', 'English film actor');
INSERT INTO definitions (id, yago_type, definition) VALUES (12, '<wikicategory_Films_directed_by_Quentin_Tarantino>', 'Film directed by Quentin Tarantino');
INSERT INTO definitions (id, yago_type, definition) VALUES (13, '<wikicategory_Films_directed_by_Steven_Spielberg>', 'Film directed by Steven Spielberg');
INSERT INTO definitions (id, yago_type, definition) VALUES (14, '<wikicategory_Films_directed_by_the_Coen_brothers>', 'Film directed by the Coen Brothers');
INSERT INTO definitions (id, yago_type, definition) VALUES (15, '<wikicategory_Films_directed_by_Ridley_Scott>', 'Film directed by Ridley Scott');
INSERT INTO definitions (id, yago_type, definition) VALUES (16, '<wikicategory_Films_directed_by_Tim_Burton>', 'Film directed by Tim Burton');
INSERT INTO definitions (id, yago_type, definition) VALUES (17, '<wikicategory_HBO_network_shows>', 'HBO network show');
INSERT INTO definitions (id, yago_type, definition) VALUES (18, '<wikicategory_Lesbian-related_films>', 'Lesbian-related film');
INSERT INTO definitions (id, yago_type, definition) VALUES (19, '<wikicategory_Psychological_thriller_films>', 'Psychological thriller film');
INSERT INTO definitions (id, yago_type, definition) VALUES (20, '<wikicategory_Serial_killer_films>', 'Film about serial killer');
INSERT INTO definitions (id, yago_type, definition) VALUES (21, '<wikicategory_Spaghetti_Western_actors>', 'Spaghetti Western actor');
INSERT INTO definitions (id, yago_type, definition) VALUES (22, '<wikicategory_Space_adventure_films>', 'Film about space adventure');
INSERT INTO definitions (id, yago_type, definition) VALUES (23, '<wikicategory_War_epic_films>', 'War epic film');
INSERT INTO definitions (id, yago_type, definition) VALUES (24, '<wikicategory_Characters_in_The_Lord_of_the_Rings>', 'Character in The Lord of the Rings');
INSERT INTO definitions (id, yago_type, definition) VALUES (25, '<wikicategory_Characters_created_by_Stan_Lee>', 'Character that was created by Stan Lee');
INSERT INTO definitions (id, yago_type, definition) VALUES (26, '<wikicategory_Fictional_characters_who_can_turn_invisible>', 'Fictional character who can turn invisible');
INSERT INTO definitions (id, yago_type, definition) VALUES (27, '<wikicategory_Filename_extensions>', 'Filename extension');
INSERT INTO definitions (id, yago_type, definition) VALUES (28, '<wikicategory_Mythological_places>', 'Mythological place');
INSERT INTO definitions (id, yago_type, definition) VALUES (29, '<wordnet_programming_language_106898352>', 'Programming language');
INSERT INTO definitions (id, yago_type, definition) VALUES (30, '<wikicategory_Port_cities_and_towns_of_the_Persian_Gulf>', 'Town of the Persian Gulf');
INSERT INTO definitions (id, yago_type, definition) VALUES (31, '<wikicategory_African_countries>', 'African country');
INSERT INTO definitions (id, yago_type, definition) VALUES (32, '<wikicategory_Capitals_in_Africa>', 'Capital in Africa');
INSERT INTO definitions (id, yago_type, definition) VALUES (33, '<wikicategory_Capitals_in_Asia>', 'Capital in Asia');
INSERT INTO definitions (id, yago_type, definition) VALUES (34, '<wikicategory_Capitals_in_Europe>', 'Capital in Europe');
INSERT INTO definitions (id, yago_type, definition) VALUES (35, '<wikicategory_Capitals_in_North_America>', 'Capital in North America');
INSERT INTO definitions (id, yago_type, definition) VALUES (36, '<wikicategory_Central_Asian_countries>', 'Central Asian country');
INSERT INTO definitions (id, yago_type, definition) VALUES (37, '<wikicategory_Countries_of_the_Mediterranean_Sea>', 'Country of the Mediterranean Sea');
INSERT INTO definitions (id, yago_type, definition) VALUES (38, '<wikicategory_East_Asian_countries>', 'East Asian country');
INSERT INTO definitions (id, yago_type, definition) VALUES (39, '<wikicategory_European_countries>', 'European country');
INSERT INTO definitions (id, yago_type, definition) VALUES (40, '<wikicategory_European_Union_member_economies>', 'European Union member economy');
INSERT INTO definitions (id, yago_type, definition) VALUES (41, '<wikicategory_Islamic_holy_places>', 'Holy place in Islam');
INSERT INTO definitions (id, yago_type, definition) VALUES (42, '<wikicategory_Holy_cities>', 'Holy city');
INSERT INTO definitions (id, yago_type, definition) VALUES (43, '<wikicategory_Member_states_of_the_United_Nations>', 'Member state of the United Nations');
INSERT INTO definitions (id, yago_type, definition) VALUES (44, '<wikicategory_Near_Eastern_countries>', 'Near Eastern country');
INSERT INTO definitions (id, yago_type, definition) VALUES (45, '<wikicategory_Deserts_of_Africa>', 'Desert in Africa');
INSERT INTO definitions (id, yago_type, definition) VALUES (46, '<wikicategory_Southeast_Asian_countries>', 'Southeast Asian country');
INSERT INTO definitions (id, yago_type, definition) VALUES (47, '<wikicategory_States_of_the_United_States>', 'State of the United States');
INSERT INTO definitions (id, yago_type, definition) VALUES (48, '<wikicategory_Israeli_film_actors>', 'Israeli film actor');
INSERT INTO definitions (id, yago_type, definition) VALUES (49, '<wikicategory_Israeli_television_actors>', 'Israeli television actor');
INSERT INTO definitions (id, yago_type, definition) VALUES (50, '<wikicategory_Israeli_film_directors>', 'Israeli film director');
INSERT INTO definitions (id, yago_type, definition) VALUES (51, '<wikicategory_Hebrew_Bible_cities>', 'City from the Hebrew Bible');
INSERT INTO definitions (id, yago_type, definition) VALUES (52, '<wikicategory_Cities_in_Israel>', 'City in Israel');
INSERT INTO definitions (id, yago_type, definition) VALUES (53, '<wikicategory_Cities_in_the_West_Bank>', 'City in the West Bank');
INSERT INTO definitions (id, yago_type, definition) VALUES (54, '<wikicategory_Research_institutes_in_Israel>', 'Research institution in Israel');
INSERT INTO definitions (id, yago_type, definition) VALUES (55, '<wikicategory_Israeli_composers>', 'Israeli composer');
INSERT INTO definitions (id, yago_type, definition) VALUES (56, '<wikicategory_Israeli_female_singers>', 'Israeli female singer');
INSERT INTO definitions (id, yago_type, definition) VALUES (57, '<wikicategory_Israeli_male_singers>', 'Israeli male singer');
INSERT INTO definitions (id, yago_type, definition) VALUES (58, '<wikicategory_Israeli_musicians>', 'Israeli musician');
INSERT INTO definitions (id, yago_type, definition) VALUES (59, '<wikicategory_Israeli_pop_singers>', 'Israeli pop singer');
INSERT INTO definitions (id, yago_type, definition) VALUES (60, '<wikicategory_Israeli_record_producers>', 'Israeli records producer');
INSERT INTO definitions (id, yago_type, definition) VALUES (61, '<wikicategory_Israeli_rock_guitarists>', 'Israeli rock guitarist');
INSERT INTO definitions (id, yago_type, definition) VALUES (62, '<wikicategory_Israeli_rock_singers>', 'Israeli rock singer');
INSERT INTO definitions (id, yago_type, definition) VALUES (63, '<wikicategory_Assassinated_Israeli_politicians>', 'Assassinated Israeli politician');
INSERT INTO definitions (id, yago_type, definition) VALUES (64, '<wikicategory_Israeli_children\'s_writers>', 'Israeli children\'s writer');
INSERT INTO definitions (id, yago_type, definition) VALUES (65, '<wikicategory_Israeli_female_models>', 'Israeli female model');
INSERT INTO definitions (id, yago_type, definition) VALUES (66, '<wikicategory_Israeli_journalists>', 'Israeli journalist');
INSERT INTO definitions (id, yago_type, definition) VALUES (67, '<wikicategory_Israeli_male_models>', 'Israeli male model');
INSERT INTO definitions (id, yago_type, definition) VALUES (68, '<wikicategory_Israeli_Nobel_laureates>', 'Israeli Nobel laureate');
INSERT INTO definitions (id, yago_type, definition) VALUES (69, '<wikicategory_Israeli_non-fiction_writers>', 'Israeli non-fiction writer');
INSERT INTO definitions (id, yago_type, definition) VALUES (70, '<wikicategory_Israeli_novelists>', 'Israeli novelist');
INSERT INTO definitions (id, yago_type, definition) VALUES (71, '<wikicategory_Israeli_party_leaders>', 'Israeli party leader');
INSERT INTO definitions (id, yago_type, definition) VALUES (72, '<wikicategory_Israeli_people_of_Iraqi_origin>', 'Israeli personality of Iraqi origin');
INSERT INTO definitions (id, yago_type, definition) VALUES (73, '<wikicategory_Israeli_people_of_Polish_origin>', 'Israeli personality of Polish origin');
INSERT INTO definitions (id, yago_type, definition) VALUES (74, '<wikicategory_Israeli_people_of_Yemeni_origin>', 'Israeli personality of Yemeni origin');
INSERT INTO definitions (id, yago_type, definition) VALUES (75, '<wikicategory_Israeli_science_fiction_writers>', 'Israeli science fiction writer');
INSERT INTO definitions (id, yago_type, definition) VALUES (76, '<wikicategory_Israeli_short_story_writers>', 'Israeli short story writer');
INSERT INTO definitions (id, yago_type, definition) VALUES (77, '<wikicategory_Israeli_stage_actors>', 'Israeli stage actor');
INSERT INTO definitions (id, yago_type, definition) VALUES (78, '<wikicategory_Israeli_comedians>', 'Israeli comedian');
INSERT INTO definitions (id, yago_type, definition) VALUES (79, '<wikicategory_LGBT_people_from_Israel>', 'LGBT personality from Israel');
INSERT INTO definitions (id, yago_type, definition) VALUES (80, '<wikicategory_Palmach_fighters>', 'Palmach fighter');
INSERT INTO definitions (id, yago_type, definition) VALUES (81, '<wikicategory_Rokdim_Im_Kokhavim_participants>', 'Participated in Rokdim Im Kokhavim ');
INSERT INTO definitions (id, yago_type, definition) VALUES (82, '<wikicategory_Prime_Ministers_of_Israel>', 'Prime Minister of Israel');
INSERT INTO definitions (id, yago_type, definition) VALUES (83, '<wikicategory_Signatories_of_the_Israeli_Declaration_of_Independence>', 'Signatory of the Israeli Declaration of Independence');
INSERT INTO definitions (id, yago_type, definition) VALUES (84, '<wikicategory_Beitar_Jerusalem_F.C._players>', 'Beitar Jerusalem F.C. player');
INSERT INTO definitions (id, yago_type, definition) VALUES (85, '<wikicategory_Football_clubs_in_Israel>', 'Football Club in Israel');
INSERT INTO definitions (id, yago_type, definition) VALUES (86, '<wikicategory_Israel_international_footballers>', 'Israel international footballer');
INSERT INTO definitions (id, yago_type, definition) VALUES (87, '<wikicategory_Olympic_bronze_medalists_for_Israel>', 'Israeli athlete who won an Olympic bronze medal');
INSERT INTO definitions (id, yago_type, definition) VALUES (88, '<wikicategory_Olympic_swimmers_of_Israel>', 'Israeli olympic swimmer');
INSERT INTO definitions (id, yago_type, definition) VALUES (89, '<wikicategory_Maccabi_Haifa_F.C._players>', 'Maccabi Haifa F.C. player');
INSERT INTO definitions (id, yago_type, definition) VALUES (90, '<wikicategory_Maccabi_Tel_Aviv_F.C._players>', 'Maccabi Tel Aviv F.C. player');
INSERT INTO definitions (id, yago_type, definition) VALUES (91, '<wikicategory_Maccabiah_Games_gold_medalists>', 'Won a gold medal of the Maccabiah Games');
INSERT INTO definitions (id, yago_type, definition) VALUES (92, '<wikicategory_African_American_rappers>', 'African American rapper');
INSERT INTO definitions (id, yago_type, definition) VALUES (93, '<wikicategory_Alternative_rock_groups_from_California>', 'Alternative rock group from California');
INSERT INTO definitions (id, yago_type, definition) VALUES (94, '<wikicategory_Alternative_rock_groups_from_New_York>', 'Alternative rock group from New York');
INSERT INTO definitions (id, yago_type, definition) VALUES (95, '<wikicategory_American_folk_rock_musicians>', 'American folk rock musician');
INSERT INTO definitions (id, yago_type, definition) VALUES (96, '<wikicategory_American_pop_singers>', 'American pop singer');
INSERT INTO definitions (id, yago_type, definition) VALUES (97, '<wikicategory_American_rock_guitarists>', 'American rock guitarist');
INSERT INTO definitions (id, yago_type, definition) VALUES (98, '<wikicategory_American_soul_singers>', 'American soul singer');
INSERT INTO definitions (id, yago_type, definition) VALUES (99, '<wikicategory_British_alternative_rock_groups>', 'British alternative rock group');
INSERT INTO definitions (id, yago_type, definition) VALUES (100, '<wikicategory_English_New_Wave_musicians>', 'English New Wave musician');
INSERT INTO definitions (id, yago_type, definition) VALUES (101, '<wikicategory_English_rock_singers>', 'English rock singer');
INSERT INTO definitions (id, yago_type, definition) VALUES (102, '<wikicategory_Female_rock_singers>', 'Female rock singer');
INSERT INTO definitions (id, yago_type, definition) VALUES (103, '<wikicategory_Hip_hop_singers>', 'Hip hop singer');
INSERT INTO definitions (id, yago_type, definition) VALUES (104, '<wikicategory_Lead_guitarists>', 'Lead guitarist');
INSERT INTO definitions (id, yago_type, definition) VALUES (105, '<wikicategory_Murdered_musicians>', 'Musician who was murdered ');
INSERT INTO definitions (id, yago_type, definition) VALUES (106, '<wikicategory_Synthpop_groups>', 'Synthpop group');
INSERT INTO definitions (id, yago_type, definition) VALUES (107, '<wikicategory_20th-century_mathematicians>', '20th-century mathematician');
INSERT INTO definitions (id, yago_type, definition) VALUES (108, '<wikicategory_Assassinated_United_States_Presidents>', 'Assassinated United States President');
INSERT INTO definitions (id, yago_type, definition) VALUES (109, '<wikicategory_English_novelists>', 'English novelist');
INSERT INTO definitions (id, yago_type, definition) VALUES (110, '<wikicategory_English_science_fiction_writers>', 'English science fiction writer');
INSERT INTO definitions (id, yago_type, definition) VALUES (111, '<wikicategory_German_philosophers>', 'German philosopher');
INSERT INTO definitions (id, yago_type, definition) VALUES (112, '<wikicategory_Jewish_American_scientists>', 'Jewish American scientist');
INSERT INTO definitions (id, yago_type, definition) VALUES (113, '<wikicategory_Jewish_philosophers>', 'Jewish philosopher');
INSERT INTO definitions (id, yago_type, definition) VALUES (114, '<wikicategory_Jewish_poets>', 'Jewish poet');
INSERT INTO definitions (id, yago_type, definition) VALUES (115, '<wikicategory_Jewish_politicians>', 'Jewish politician');
INSERT INTO definitions (id, yago_type, definition) VALUES (116, '<wikicategory_Nobel_Peace_Prize_laureates>', 'Nobel Peace Prize laureate');
INSERT INTO definitions (id, yago_type, definition) VALUES (117, '<wikicategory_Nonviolence_advocates>', 'Advocate of Nonviolence');
INSERT INTO definitions (id, yago_type, definition) VALUES (118, '<wikicategory_The_Voice_judges>', 'One of the Voice judges');
INSERT INTO definitions (id, yago_type, definition) VALUES (119, '<wikicategory_The_X_Factor_judges>', 'One of the X Factor judges');
INSERT INTO definitions (id, yago_type, definition) VALUES (120, '<wikicategory_Philosophers_of_mind>', 'Philosopher of mind');
INSERT INTO definitions (id, yago_type, definition) VALUES (121, '<wikicategory_Presidents_of_the_United_States>', 'President of the United States');
INSERT INTO definitions (id, yago_type, definition) VALUES (122, '<wikicategory_2010_FIFA_World_Cup_players>', '2010 FIFA World Cup player');
INSERT INTO definitions (id, yago_type, definition) VALUES (123, '<wikicategory_Premier_League_clubs>', 'Premier League football club');
INSERT INTO definitions (id, yago_type, definition) VALUES (124, '<wikicategory_A.C._Milan_players>', 'A.C. Milan player');
INSERT INTO definitions (id, yago_type, definition) VALUES (125, '<wikicategory_Basketball_players_from_New_York>', 'Basketball player from New York');
INSERT INTO definitions (id, yago_type, definition) VALUES (126, '<wikicategory_Boston_Celtics_players>', 'Boston Celtics player');
INSERT INTO definitions (id, yago_type, definition) VALUES (127, '<wikicategory_Chelsea_F.C._players>', 'Chelsea F.C. player');
INSERT INTO definitions (id, yago_type, definition) VALUES (128, '<wikicategory_Chicago_Bulls_players>', 'Chicago Bulls player');
INSERT INTO definitions (id, yago_type, definition) VALUES (129, '<wikicategory_FC_Barcelona_footballers>', 'FC Barcelona football player');
INSERT INTO definitions (id, yago_type, definition) VALUES (130, '<wikicategory_Los_Angeles_Lakers_players>', 'Los Angeles Lakers player');
INSERT INTO definitions (id, yago_type, definition) VALUES (131, '<wikicategory_Manchester_United_F.C._players>', 'Manchester United F.C. player');
INSERT INTO definitions (id, yago_type, definition) VALUES (132, '<wikicategory_NBA_Slam_Dunk_Contest_champions>', 'NBA Slam Dunk Contest champion');
INSERT INTO definitions (id, yago_type, definition) VALUES (133, '<wikicategory_UEFA_Euro_2008_players>', 'UEFA Euro 2008 player');
INSERT INTO definitions (id, yago_type, definition) VALUES (134, '<wikicategory_MTV_cartoons>', 'MTV cartoon');
INSERT INTO definitions (id, yago_type, definition) VALUES (135, '<wikicategory_Constellations>', 'Constellation (astronomy)');
INSERT INTO definitions (id, yago_type, definition) VALUES (136, '<wikicategory_Computer_security_software_companies>', 'Computer security software company');
INSERT INTO definitions (id, yago_type, definition) VALUES (137, '<wikicategory_Broadway_musicals>', 'Broadway musical');
INSERT INTO definitions (id, yago_type, definition) VALUES (138, '<wikicategory_Fashion_magazines>', 'Fashion magazine');
INSERT INTO definitions (id, yago_type, definition) VALUES (139, '<wikicategory_Mythological_kings>', 'Mythological king');
INSERT INTO definitions (id, yago_type, definition) VALUES (140, '<wikicategory_Hebrew_Bible_people>', 'Character from the Hebrew Bible');
INSERT INTO definitions (id, yago_type, definition) VALUES (141, '<wikicategory_Jewish_ritual_objects>', 'Jewish ritual object');
INSERT INTO definitions (id, yago_type, definition) VALUES (142, '<wikicategory_University_towns>', 'University town');
INSERT INTO definitions (id, yago_type, definition) VALUES (143, '<wikicategory_Capitals_in_South_America>', 'Capital in South America');
INSERT INTO definitions (id, yago_type, definition) VALUES (144, '<wikicategory_Countries_bordering_the_Atlantic_Ocean>', 'Country on the borders of the Atlantic Ocean');
INSERT INTO definitions (id, yago_type, definition) VALUES (145, '<wikicategory_Archaeological_sites_in_Iraq>', 'An archaeological site in Iraq');
INSERT INTO definitions (id, yago_type, definition) VALUES (146, '<wikicategory_Cities_in_the_Netherlands>', 'City in the Netherlands');
INSERT INTO definitions (id, yago_type, definition) VALUES (147, '<wikicategory_Mediterranean_port_cities_and_towns_in_Italy>', 'Mediterranean port city in Italy');
INSERT INTO definitions (id, yago_type, definition) VALUES (148, '<wikicategory_Neighbourhoods_of_Barcelona>', 'Neighbourhood in Barcelona');
INSERT INTO definitions (id, yago_type, definition) VALUES (149, '<wikicategory_European_seas>', 'European sea');
INSERT INTO definitions (id, yago_type, definition) VALUES (150, '<wikicategory_Arabic-speaking_countries_and_territories>', 'Arabic-speaking territory');
INSERT INTO definitions (id, yago_type, definition) VALUES (151, '<wikicategory_Ethnic_groups_in_Europe>', 'European ethnic group');
INSERT INTO definitions (id, yago_type, definition) VALUES (152, '<wikicategory_Software_companies_of_Israel>', 'Israeli software company');
INSERT INTO definitions (id, yago_type, definition) VALUES (153, '<wikicategory_Neighbourhoods_of_Jerusalem>', 'Neighbourhood in Jerusalem');
INSERT INTO definitions (id, yago_type, definition) VALUES (154, '<wikicategory_Israeli_settlements>', 'Israeli settlement');
INSERT INTO definitions (id, yago_type, definition) VALUES (155, '<wikicategory_Kokhav_Nolad_contestants>', 'Participated in Kokhav Nolad');
INSERT INTO definitions (id, yago_type, definition) VALUES (156, '<wikicategory_Israeli_mathematicians>', 'Israeli mathematician');
INSERT INTO definitions (id, yago_type, definition) VALUES (157, '<wikicategory_Maccabi_Petah_Tikva_F.C._players>', 'Maccabi Petah Tikva F.C. player');
INSERT INTO definitions (id, yago_type, definition) VALUES (158, '<wikicategory_Hapoel_Tel_Aviv_F.C._players>', 'Hapoel Tel Aviv F.C. player');
INSERT INTO definitions (id, yago_type, definition) VALUES (159, '<wikicategory_Bnei_Sakhnin_F.C._players>', 'Bnei Sakhnin F.C. player');
INSERT INTO definitions (id, yago_type, definition) VALUES (160, '<wikicategory_Port_cities_and_towns_of_the_Baltic_Sea>', 'Port city of the Baltic Sea');
INSERT INTO definitions (id, yago_type, definition) VALUES (161, '<wikicategory_Coastal_cities_and_towns_in_Sweden>', 'Coastal city in Sweden');
INSERT INTO definitions (id, yago_type, definition) VALUES (162, '<wikicategory_French-speaking_countries>', 'French-speaking country');
INSERT INTO definitions (id, yago_type, definition) VALUES (163, '<wikicategory_Southern_Mediterranean_countries>', 'Southern Mediterranean country');
INSERT INTO definitions (id, yago_type, definition) VALUES (164, '<wikicategory_Least_developed_countries>', 'Least developed country');
INSERT INTO definitions (id, yago_type, definition) VALUES (165, '<wikicategory_Middle_Eastern_countries>', 'Country in the Middle East');
INSERT INTO definitions (id, yago_type, definition) VALUES (166, '<wikicategory_Member_states_of_NATO>', 'Member state of NATO');
INSERT INTO definitions (id, yago_type, definition) VALUES (167, '<wikicategory_Active_volcanoes_of_Indonesia>', 'Active volcano in Indonesia');
INSERT INTO definitions (id, yago_type, definition) VALUES (168, '<wikicategory_Cities_in_Iran>', 'City in Iran');
INSERT INTO definitions (id, yago_type, definition) VALUES (169, '<wikicategory_Squares_in_Tel_Aviv>', 'A Square in Tel Aviv');
INSERT INTO definitions (id, yago_type, definition) VALUES (170, '<wikicategory_Ancient_cities>', 'An ancient city');
INSERT INTO definitions (id, yago_type, definition) VALUES (171, '<wikicategory_Ancient_Greek_cities>', 'Ancient Greek city');
INSERT INTO definitions (id, yago_type, definition) VALUES (172, '<wikicategory_Roman_sites_in_Egypt>', 'Roman site in Egypt');
INSERT INTO definitions (id, yago_type, definition) VALUES (173, '<wikicategory_Lakes_of_New_York>', 'A lake in New York');
INSERT INTO definitions (id, yago_type, definition) VALUES (174, '<wikicategory_Rivers_of_Romania>', 'A river in Romania');
INSERT INTO definitions (id, yago_type, definition) VALUES (175, '<wikicategory_Coastal_cities_in_Australia>', 'Coastal city in Australia');
INSERT INTO definitions (id, yago_type, definition) VALUES (176, '<wikicategory_Cities_in_Colorado>', 'City in Colorado');
INSERT INTO definitions (id, yago_type, definition) VALUES (177, '<wikicategory_Port_cities_in_Africa>', 'Port city in Africa');
INSERT INTO definitions (id, yago_type, definition) VALUES (178, '<wikicategory_World_Heritage_Sites_in_Italy>', 'World Heritage Site in Italy');
INSERT INTO definitions (id, yago_type, definition) VALUES (179, '<wikicategory_Israeli_generals>', 'Israeli general');
INSERT INTO definitions (id, yago_type, definition) VALUES (180, '<wikicategory_Israeli_activists>', 'Israeli activist');
INSERT INTO definitions (id, yago_type, definition) VALUES (181, '<wikicategory_Israeli_comics_writers>', 'Israeli comics writer');
INSERT INTO definitions (id, yago_type, definition) VALUES (182, '<wikicategory_Israeli_literary_critics>', 'Israeli literary critic');
INSERT INTO definitions (id, yago_type, definition) VALUES (183, '<wikicategory_Israeli_jazz_musicians>', 'Israeli jazz musician');
INSERT INTO definitions (id, yago_type, definition) VALUES (184, '<wikicategory_Israeli_musical_groups>', 'Israeli musical group');
INSERT INTO definitions (id, yago_type, definition) VALUES (185, '<wikicategory_Israeli_women_in_politics>', 'Israeli woman in politics');
INSERT INTO definitions (id, yago_type, definition) VALUES (186, '<wikicategory_Israeli_pacifists>', 'Israeli pacifist');
INSERT INTO definitions (id, yago_type, definition) VALUES (187, '<wikicategory_Israeli_female_tennis_players>', 'Israeli female tennis player');
INSERT INTO definitions (id, yago_type, definition) VALUES (188, '<wikicategory_Mapai_politicians>', 'Mapai politician');
INSERT INTO definitions (id, yago_type, definition) VALUES (189, '<wikicategory_Mayors_of_Haifa>', 'Mayor of Haifa');
INSERT INTO definitions (id, yago_type, definition) VALUES (190, '<wikicategory_Mayors_of_Tel_Aviv-Yafo>', 'Mayor of Tel Aviv-Yafo');
INSERT INTO definitions (id, yago_type, definition) VALUES (191, '<wikicategory_Mayors_of_Jerusalem>', 'Mayor of Jerusalem');
INSERT INTO definitions (id, yago_type, definition) VALUES (192, '<wikicategory_Michael_Jackson_songs>', 'One of Michael Jackson songs');
INSERT INTO definitions (id, yago_type, definition) VALUES (193, '<wikicategory_Israeli_television_presenters>', 'Israeli television presenter');
INSERT INTO definitions (id, yago_type, definition) VALUES (194, '<wikicategory_Israeli_film_producers>', 'Israeli film producer');
INSERT INTO definitions (id, yago_type, definition) VALUES (195, '<wikicategory_Israeli_diplomats>', 'Israeli diplomat');
INSERT INTO definitions (id, yago_type, definition) VALUES (196, '<wikicategory_Israeli_films>', 'Israeli film');

INSERT INTO definitions_topics (definition_id, topic_id) VALUES (1, 1);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (2, 1);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (3, 1);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (4, 1);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (5, 1);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (6, 1);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (7, 1);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (8, 1);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (9, 1);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (10, 1);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (11, 1);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (12, 1);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (13, 1);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (14, 1);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (15, 1);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (16, 1);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (17, 1);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (18, 1);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (19, 1);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (20, 1);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (21, 1);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (22, 1);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (23, 1);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (24, 2);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (25, 2);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (26, 2);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (27, 2);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (28, 2);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (29, 2);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (30, 3);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (31, 3);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (32, 3);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (33, 3);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (34, 3);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (35, 3);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (36, 3);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (37, 3);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (38, 3);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (39, 3);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (40, 3);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (41, 3);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (42, 3);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (43, 3);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (44, 3);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (45, 3);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (46, 3);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (47, 3);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (48, 4);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (48, 1);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (49, 4);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (49, 1);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (50, 4);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (50, 1);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (51, 4);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (51, 3);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (52, 4); 
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (52, 3);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (53, 4); 
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (53, 3);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (54, 4);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (54, 3);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (55, 4); 
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (55, 5);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (56, 4);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (56, 5);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (57, 4);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (57, 5);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (58, 4);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (58, 5);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (59, 4);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (59, 5);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (60, 4); 
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (60, 5);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (61, 4);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (61, 5);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (62, 4);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (62, 5);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (63, 4);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (63, 6);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (64, 4);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (64, 6);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (65, 4);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (65, 6);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (66, 4);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (66, 6);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (67, 4);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (67, 6);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (68, 4);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (68, 6);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (69, 4);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (69, 6);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (70, 4);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (70, 6);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (71, 4);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (71, 6);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (72, 4);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (72, 6);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (73, 4);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (73, 6);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (74, 4);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (74, 6);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (75, 4);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (75, 6);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (76, 4);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (76, 6);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (77, 4);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (77, 6);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (78, 4);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (78, 6);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (79, 4);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (79, 6);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (80, 4);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (80, 6);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (81, 4);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (81, 6);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (82, 4);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (82, 6);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (83, 4);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (83, 6);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (84, 4);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (84, 7);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (85, 4);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (85, 7);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (86, 4);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (86, 7);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (87, 4);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (87, 7);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (88, 4);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (88, 7);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (89, 4);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (89, 7);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (90, 4);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (90, 7);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (91, 4);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (91, 7);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (92, 5);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (93, 5);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (94, 5);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (95, 5);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (96, 5);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (97, 5);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (98, 5);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (99, 5);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (100, 5);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (101, 5);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (102, 5);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (103, 5);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (104, 5);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (105, 5);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (106, 5);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (107, 6);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (108, 6);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (109, 6);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (110, 6);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (111, 6);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (112, 6);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (113, 6);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (114, 6);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (115, 6);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (116, 6);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (117, 6);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (118, 6);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (119, 6);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (120, 6);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (121, 6);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (122, 7);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (123, 7);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (124, 7);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (125, 7);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (126, 7);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (127, 7);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (128, 7);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (129, 7);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (130, 7);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (131, 7);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (132, 7);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (133, 7);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (134, 1);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (135, 2);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (136, 2);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (137, 2);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (138, 2);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (139, 2);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (140, 2);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (141, 2);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (142, 3);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (143, 3);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (144, 3);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (145, 3);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (146, 3);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (147, 3);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (148, 3);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (149, 3);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (150, 3);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (151, 3);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (152, 4);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (152, 2);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (153, 4);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (153, 3);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (154, 4);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (154, 3);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (155, 4);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (155, 6);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (156, 4);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (156, 6);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (157, 4);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (157, 7);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (158, 4);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (158, 7);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (159, 4);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (159, 7);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (160, 3);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (161, 3);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (162, 3);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (163, 3);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (164, 3);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (165, 3);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (166, 3);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (167, 3);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (168, 3);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (169, 3);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (169, 4);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (170, 3);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (171, 3);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (172, 3);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (173, 3);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (174, 3);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (175, 3);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (176, 3);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (177, 3);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (178, 3);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (179, 6);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (179, 4);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (180, 6);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (180, 4);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (181, 6);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (181, 4);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (182, 6);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (182, 4);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (183, 5);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (183, 4);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (184, 5);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (184, 4);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (185, 6);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (185, 4);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (186, 6);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (186, 4);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (187, 7);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (187, 4);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (188, 6);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (188, 4);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (189, 6);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (189, 4);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (190, 6);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (190, 4);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (191, 6);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (191, 4);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (192, 5);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (193, 1);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (193, 4);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (194, 1);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (194, 4);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (195, 6);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (195, 4);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (196, 1);
INSERT INTO definitions_topics (definition_id, topic_id) VALUES (196, 4);

INSERT INTO predicates (yago_predicate, subject_str, object_str) VALUE ('<diedOnDate>', 'That person died on ?', null);
INSERT INTO predicates (yago_predicate, subject_str, object_str) VALUE ('<wasBornOnDate>', 'That person was born on ?', null);
INSERT INTO predicates (yago_predicate, subject_str, object_str) VALUE ('<wasCreatedOnDate>', 'Was created on ?', null);
INSERT INTO predicates (yago_predicate, subject_str, object_str) VALUE ('<hasNumberOfPeople>', 'Populated by ? people', null);
INSERT INTO predicates (yago_predicate, subject_str, object_str) VALUE ('<hasMotto>', 'Its motto is ?', null);
INSERT INTO predicates (yago_predicate, subject_str, object_str) VALUE ('<hasPages>', 'Has ? pages', null);

INSERT INTO predicates (yago_predicate, subject_str, object_str) VALUE ('<actedIn>', 'Participated in ?', 'Production in which ? participated');
INSERT INTO predicates (yago_predicate, subject_str, object_str) VALUE ('<created>', 'Created ?', 'Was created by ?');
INSERT INTO predicates (yago_predicate, subject_str, object_str) VALUE ('<directed>', 'Directed ?', 'Was directed by ?');
INSERT INTO predicates (yago_predicate, subject_str, object_str) VALUE ('<hasCapital>', 'Its capital is ?', 'Captial of ?');
INSERT INTO predicates (yago_predicate, subject_str, object_str) VALUE ('<hasChild>', 'Parent of ?', 'Child of ?');
INSERT INTO predicates (yago_predicate, subject_str, object_str) VALUE ('<hasCurrency>', 'Its currency is ?', 'The currency of ?');
INSERT INTO predicates (yago_predicate, subject_str, object_str) VALUE ('<happenedIn>', 'It happened in ?', '? happened there');
INSERT INTO predicates (yago_predicate, subject_str, object_str) VALUE ('<hasOfficialLanguage>', 'Its official language is ?', 'The official language of ?');
INSERT INTO predicates (yago_predicate, subject_str, object_str) VALUE ('<holdsPoliticalPosition>', '?', 'The political position ? holds');
INSERT INTO predicates (yago_predicate, subject_str, object_str) VALUE ('<isInterestedIn>', 'Person who is interested in ?', '? is interested in it');
INSERT INTO predicates (yago_predicate, subject_str, object_str) VALUE ('<isMarriedTo>', 'Married to ?', 'Married to ?');
-- INSERT INTO predicates (yago_predicate, subject_str, object_str) VALUE ('<isLocatedIn>', 'Located in ?', '? is located there');
-- INSERT INTO predicates (yago_predicate, subject_str, object_str) VALUE ('<livesIn>', 'Person who lives in ?', '? lives there');
INSERT INTO predicates (yago_predicate, subject_str, object_str) VALUE ('<playsFor>', 'Plays for ?', '? plays for it');
INSERT INTO predicates (yago_predicate, subject_str, object_str) VALUE ('<wasBornIn>', 'Was born in ?', '? was born there');
INSERT INTO predicates (yago_predicate, subject_str, object_str) VALUE ('<hasWonPrize>', 'Has won the ?', '? has won it');
INSERT INTO predicates (yago_predicate, subject_str, object_str) VALUE ('<wroteMusicFor>', 'Wrote music for ?', '? wrote its music');
INSERT INTO predicates (yago_predicate, subject_str, object_str) VALUE ('<worksAt>', 'Works at ?', '? works there');