create extension hstore;
create schema hospital;
create table if not exists hospital."Patient" ("patient_id" BIGSERIAL NOT NULL PRIMARY KEY, "first_name" VARCHAR NOT NULL, "last_name" VARCHAR NOT NULL, "age" VARCHAR NOT NULL, "gender" VARCHAR NOT NULL);
create table if not exists hospital."PatientHistory" ("patient_id" INT NOT NULL, "disease" VARCHAR NOT NULL);

create table if not exists hospital."Symptoms" ("disease" VARCHAR , "symptom1" VARCHAR NOT NULL, "symptom2" VARCHAR NOT NULL, "symptom3" VARCHAR NOT NULL, "symptom4" VARCHAR NOT NULL, "symptom5" VARCHAR NOT NULL, "symptom6" VARCHAR NOT NULL, "symptom7" VARCHAR NOT NULL, "symptom8" VARCHAR NOT NULL, "symptom9" VARCHAR NOT NULL, "symptom10" VARCHAR NOT NULL, "symptom11" VARCHAR NOT NULL, "symptom12" VARCHAR NOT NULL, "symptom13" VARCHAR NOT NULL, "symptom14" VARCHAR NOT NULL, "symptom15" VARCHAR NOT NULL, "symptom16" VARCHAR NOT NULL, "symptom17" VARCHAR NOT NULL, "symptom18" VARCHAR NOT NULL);
create table if not exists hospital."DiseaseDescription" ("disease" VARCHAR , "description" VARCHAR );
create table if not exists hospital."Precautions" ("disease" VARCHAR NOT NULL, "precaution1" VARCHAR NOT NULL, "precaution2" VARCHAR NOT NULL, "precaution3" VARCHAR NOT NULL, "precaution4" VARCHAR NOT NULL);

create table if not exists hospital."PositiveNoun" ("noun" VARCHAR NOT NULL, "x" VARCHAR NOT NULL);
create table if not exists hospital."PositiveVerb" ("verb" VARCHAR NOT NULL, "x" VARCHAR NOT NULL);
create table if not exists hospital."PositiveAdjective" ("adjective" VARCHAR NOT NULL, "x" VARCHAR NOT NULL);
create table if not exists hospital."PositiveAdverb" ("adverb" VARCHAR NOT NULL, "x" VARCHAR NOT NULL);
create table if not exists hospital."PositiveWords" ("word" VARCHAR NOT NULL, "x" VARCHAR NOT NULL);

create table if not exists hospital."NegativeNoun" ("noun" VARCHAR NOT NULL, "x" VARCHAR NOT NULL);
create table if not exists hospital."NegativeVerb" ("verb" VARCHAR NOT NULL, "x" VARCHAR NOT NULL);
create table if not exists hospital."NegativeAdjective" ("adjective" VARCHAR NOT NULL, "x" VARCHAR NOT NULL);
create table if not exists hospital."NegativeAdverb" ("adverb" VARCHAR NOT NULL, "x" VARCHAR NOT NULL);
create table if not exists hospital."NegativeWords" ("word" VARCHAR NOT NULL, "x" VARCHAR NOT NULL);

