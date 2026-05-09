#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-EOSQL
    CREATE DATABASE user_service_db_blogging_app;
    CREATE DATABASE post_service_db_blogging_app;
    CREATE DATABASE feed_service_db_blogging_app;
EOSQL
