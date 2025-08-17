-- migration: create datacenters table

CREATE TABLE IF NOT EXISTS datacenters (
  id SERIAL PRIMARY KEY,
  name TEXT NOT NULL,
  short_name TEXT NOT NULL
);
