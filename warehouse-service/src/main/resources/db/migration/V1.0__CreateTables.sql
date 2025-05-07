-- v1.0 Create tables
CREATE TABLE items
(
    id              SERIAL PRIMARY KEY,
    name            VARCHAR   NOT NULL,
    description     VARCHAR,
    available_count INTEGER   NOT NULL,
    manufacturer    VARCHAR,
    image_url       VARCHAR,
    created_date    TIMESTAMP NOT NULL,
    modified_date   TIMESTAMP NOT NULL,
    modified_user   VARCHAR   NOT NULL
);

CREATE UNIQUE INDEX ux_items_name ON items (name);
