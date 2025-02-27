docker run -d --name postgresDB \
    -e POSTGRES_USER=postgres \
    -e POSTGRES_PASSWORD=password \
    -e POSTGRES_DB=discovery \
    -p 5432:5432 \
    postgres
sleep 5
docker cp ../database/HealthDB-Postgres.sql postgresDB:/database.sql
docker exec -i postgresDB psql -U postgres -f database.sql