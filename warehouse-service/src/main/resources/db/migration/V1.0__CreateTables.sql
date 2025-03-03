-- v1.0 Create items table
CREATE TABLE items
(
    id              SERIAL PRIMARY KEY,
    uid             UUID      NOT NULL,
    available_count INTEGER   NOT NULL,
    name            VARCHAR   NOT NULL,
    description     VARCHAR,
    manufacturer    VARCHAR,
    image_url       VARCHAR,
    created_date    TIMESTAMP NOT NULL,
    modified_date   TIMESTAMP NOT NULL,
    modified_user   VARCHAR   NOT NULL
);

CREATE UNIQUE INDEX ux_items_uid ON items (uid);
