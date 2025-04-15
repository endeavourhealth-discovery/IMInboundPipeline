# IMInboundPipeline

![Version](https://s3.eu-west-2.amazonaws.com/endeavour-codebuild-output/badges/IMInboundPipeline/version.svg)
![Build Status](https://s3.eu-west-2.amazonaws.com/endeavour-codebuild-output/badges/IMInboundPipeline/build.svg)
![Unit Tests](https://s3.eu-west-2.amazonaws.com/endeavour-codebuild-output/badges/IMInboundPipeline/unit-test.svg)

# Inbound pipeline

## E2E example for EMIS data

### FileReader Config

#### Program arguments:

- --rabbitmq.targetBaseRoutingKey=EMIS
- --rabbitmq.targetExchange=Data
- --rabbitmq.sourceQueue=File-EMIS
- --rabbitmq.filingOutcomeQueue=FilingOutcome-EMIS

#### Environment variables:

- AWS_ACCESS_KEY_ID
- AWS_SECRET_ACCESS_KEY
- BUCKET_NAME
- REGION
- SPRING_RABBITMQ_HOST
- SPRING_RABBITMQ_PASSWORD
- SPRING_RABBITMQ_PORT
- SPRING_RABBITMQ_USERNAME
- SPRING_RABBITMQ_VIRTUALHOST

### DataReader Config

#### Program arguments:

- --rabbitmq.sourceQueue=Data-EMIS
- --rabbitmq.targetBaseRoutingKey=EMIS
- --rabbitmq.targetExchange=FilingOutcome

#### Environment variables:

- SPRING_EVENT_DATASOURCE_PASSWORD
- SPRING_EVENT_DATASOURCE_URL
- SPRING_EVENT_DATASOURCE_USER
- SPRING_INSTANCE_DATASOURCE_PASSWORD
- SPRING_INSTANCE_DATASOURCE_URL
- SPRING_INSTANCE_DATASOURCE_USER
- SPRING_RABBITMQ_HOST
- SPRING_RABBITMQ_PASSWORD
- SPRING_RABBITMQ_PORT
- SPRING_RABBITMQ_USERNAME
- SPRING_RABBITMQ_VIRTUALHOST

### Steps

Start RabbitMQ (either manual install or docker)
Run script for setting up queues

Start Postgres (either manual install or docker)
Run script for setting up tables

Ensure files are in S3 in EMIS

Check there are no messages in queues

(Optional) Drop all rows from all tables from database

Run FileReader

Mock the event received by FileReader for change in files (with curl or postman)

Run DataReader

See files being moved to FILING status/folder in S3