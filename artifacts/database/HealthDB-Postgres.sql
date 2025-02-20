DROP SCHEMA IF EXISTS healthDB CASCADE;

CREATE SCHEMA healthDB;

SET search_path TO healthDB;

CREATE OR REPLACE FUNCTION json_date(text)
  RETURNS timestamptz AS
$$SELECT to_timestamp($1, 'YYYY-MM-DDTHH24:MI:SS')$$
  LANGUAGE sql IMMUTABLE;


DROP TABLE IF EXISTS query_queue;

CREATE TABLE query_queue (
    id UUID PRIMARY KEY,        -- Unique run identifier
    iri VARCHAR(255) NOT NULL,  -- Query IRI
    name VARCHAR(255) NOT NULL, -- Query name
    user_id UUID NOT NULL,         -- User UUID
    queued TIMESTAMP NOT NULL,
    started TIMESTAMP,
    pid INT,                    -- Internal (postgres) process ID (for killing)
    finished TIMESTAMP,
    killed TIMESTAMP,
    status TEXT
);


DROP TABLE IF EXISTS query_result;

CREATE TABLE query_result (
    iri VARCHAR(255) NOT NULL,
    id UUID NOT NULL
);

CREATE INDEX idx_query_result_iri ON query_result(iri);

DROP TABLE IF EXISTS set_member;

CREATE TABLE set_member (
    iri VARCHAR(255) NOT NULL,
    member VARCHAR(255) NOT NULL
);

CREATE INDEX idx_set_member_iri ON set_member(iri);

DROP TABLE IF EXISTS tct;

CREATE TABLE tct (
    iri VARCHAR(255) NOT NULL,
    child VARCHAR(255) NOT NULL,
    level INT
);

CREATE INDEX idx_tct_iri ON tct(iri);

DROP TABLE IF EXISTS instance;

CREATE TABLE instance (
    id UUID PRIMARY KEY,
    json JSON NOT NULL,
    type text generated always as (json ->> '@type') stored
);

CREATE INDEX idx_inst_pat_dob ON instance ((json_date(json ->> 'dateOfBirth'))) WHERE type = 'Patient';
CREATE INDEX idx_inst_pat_nhs ON instance ((json ->> 'nhsNumber')) WHERE type = 'Patient';

DROP TABLE IF EXISTS event;

CREATE TABLE event (
    id UUID PRIMARY KEY,
    json JSON NOT NULL,
    type text generated always as (json ->> '@type') stored
);

CREATE INDEX idx_evt_cpt_typ ON event (type, (json ->> 'concept'), (json_date(json -> 'effectiveDate' ->> 'dateTime')));
CREATE INDEX idx_evt_pat_typ_cpt ON event (((json ->> 'patient')::UUID), type, (json ->> 'concept'));
CREATE INDEX idx_evt_eoc_pat_type ON event (((json ->> 'patientType')::varchar)) WHERE type = 'EpisodeOfCare';
