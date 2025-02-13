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

docker exec rabbitmq rabbitmqadmin declare exchange name=File type=topic
docker exec rabbitmq rabbitmqadmin declare exchange name=Data type=topic

# Step 4: Declare queues
echo "Declaring queues..."

docker exec rabbitmq rabbitmqadmin declare queue name=File-EMIS
docker exec rabbitmq rabbitmqadmin declare queue name=File-TPP
docker exec rabbitmq rabbitmqadmin declare queue name=Data-EMIS
docker exec rabbitmq rabbitmqadmin declare queue name=Data-TPP

# Step 5: Declare bindings
echo "Declaring bindings..."

docker exec rabbitmq rabbitmqadmin declare binding source=File destination=File-EMIS routing_key="EMIS.*"
docker exec rabbitmq rabbitmqadmin declare binding source=File destination=File-TPP routing_key="TPP.*"
docker exec rabbitmq rabbitmqadmin declare binding source=Data destination=Data-EMIS routing_key="EMIS.*"
docker exec rabbitmq rabbitmqadmin declare binding source=Data destination=Data-TPP routing_key="TPP.*"

# Done
echo "RabbitMQ setup complete!"
