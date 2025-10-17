CREATE TABLE IF NOT EXISTS planet (
  id bigint GENERATED ALWAYS AS IDENTITY,
  name text NOT NULL,
  diameter integer NOT NULL
);
