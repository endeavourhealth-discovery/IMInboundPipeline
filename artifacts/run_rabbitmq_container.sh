#!/bin/bash

# Step 1: Run RabbitMQ container in the background
docker run -d --name rabbitmq \
  -p 5672:5672 -p 15672:15672 \
  rabbitmq:management

# Wait for RabbitMQ to start (may need to adjust depending on your machine)
echo "Waiting for RabbitMQ to start..."
sleep 20

# Step 2: Create virtual hosts and set permissions
echo "Creating virtual hosts and setting permissions..."

docker exec rabbitmq rabbitmqctl add_vhost Inbound
docker exec rabbitmq rabbitmqctl set_permissions -p Inbound guest ".*" ".*" ".*"

docker exec rabbitmq rabbitmqctl add_vhost Outbound
docker exec rabbitmq rabbitmqctl set_permissions -p Outbound guest ".*" ".*" ".*"

# Step 3: Declare exchanges
echo "Declaring exchanges..."

docker exec rabbitmq rabbitmqadmin -V Inbound declare exchange name=File type=topic
docker exec rabbitmq rabbitmqadmin -V Inbound declare exchange name=Data type=topic

# Step 4: Declare queues
echo "Declaring queues..."

docker exec rabbitmq rabbitmqadmin -V Inbound declare queue name=File-EMIS
docker exec rabbitmq rabbitmqadmin -V Inbound declare queue name=File-TPP
docker exec rabbitmq rabbitmqadmin -V Inbound declare queue name=Data-EMIS
docker exec rabbitmq rabbitmqadmin -V Inbound declare queue name=Data-TPP

# Step 5: Declare bindings
echo "Declaring bindings..."

docker exec rabbitmq rabbitmqadmin -V Inbound declare binding source=File destination=File-EMIS routing_key="endeavour-inbound.EMIS.#"
docker exec rabbitmq rabbitmqadmin -V Inbound declare binding source=File destination=File-TPP routing_key="endeavour-inbound.TPP.#"
docker exec rabbitmq rabbitmqadmin -V Inbound declare binding source=Data destination=Data-EMIS routing_key="endeavour-inbound.EMIS.#"
docker exec rabbitmq rabbitmqadmin -V Inbound declare binding source=Data destination=Data-TPP routing_key="endeavour-inbound.TPP.#"

#Step 6: Declare dead-letter exchange & queue
echo "Declaring File-Undelivered exchange & queue..."
docker exec rabbitmq rabbitmqadmin -V Inbound declare exchange name=File-Undeliverable type=fanout
docker exec rabbitmq rabbitmqadmin -V Inbound declare queue name=File-Undelivered
docker exec rabbitmq rabbitmqadmin -V Inbound declare binding source=File-Undeliverable destination=File-Undelivered
docker exec rabbitmq rabbitmqadmin -V Inbound set_policy "Undeliverable Files" "^File$" '{"alternate-exchange":"File-Undeliverable"}' --apply-to exchanges

docker exec rabbitmq rabbitmqadmin -V Inbound declare exchange name=Data-Undeliverable type=fanout
docker exec rabbitmq rabbitmqadmin -V Inbound declare queue name=Data-Undelivered
docker exec rabbitmq rabbitmqadmin -V Inbound declare binding source=Data-Undeliverable destination=Data-Undelivered
docker exec rabbitmq rabbitmqadmin -V Inbound set_policy "Undeliverable Data" "^Data$" '{"alternate-exchange":"Data-Undeliverable"}' --apply-to exchanges

# Done
echo "RabbitMQ setup complete!"
