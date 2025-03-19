#!/bin/sh
cd /home/ubuntu
mkdir ./archives
INSTANCE_ID=$(ec2metadata --instance-id)

################
## CodeDeploy ##
################
sudo apt update
sudo apt install ruby-full -y
sudo apt install wget -y
wget https://aws-codedeploy-eu-west-2.s3.eu-west-2.amazonaws.com/latest/install
chmod +x ./install
sudo ./install auto

################
## Coretto 17 ##
################
wget -O - https://apt.corretto.aws/corretto.key | sudo gpg --dearmor -o /usr/share/keyrings/corretto-keyring.gpg
echo "deb [signed-by=/usr/share/keyrings/corretto-keyring.gpg] https://apt.corretto.aws stable main" | sudo tee /etc/apt/sources.list.d/corretto.list
sudo apt-get update
sudo apt-get install -y java-17-amazon-corretto-jdk

###############
## Tomcat 10 ##
###############
aws s3 cp 's3://endeavour-deployment/InformationModel(v2)/tomcat.tar.gz' './archives'
aws s3 cp 's3://endeavour-deployment/InformationModel(v2)/setenv.sh' './archives'
aws s3 cp 's3://endeavour-deployment/InformationModel(v2)/tomcat.service' './archives'
useradd -m -d /opt/tomcat -U -s /bin/false tomcat
tar xzf ./archives/tomcat.tar.gz -C /opt/tomcat --strip-components=1
chown -R tomcat:tomcat /opt/tomcat/
chmod -R u+x /opt/tomcat/bin
rm -r /opt/tomcat/webapps/*
mv ./archives/setenv.sh /opt/tomcat/bin
chmod +x /opt/tomcat/bin/setenv.sh
mv ./archives/tomcat.service /etc/systemd/system/
systemctl enable tomcat.service

################
## PostgreSQL ##
################
sudo sh -c 'echo "deb https://apt.PostgreSQL.org/pub/repos/apt $(lsb_release -cs)-pgdg main" > /etc/apt/sources.list.d/pgdg.list'
curl -fsSL https://www.postgresql.org/media/keys/ACCC4CF8.asc | sudo gpg --dearmor -o /etc/apt/trusted.gpg.d/postgresql.gpg
sudo apt update
sudo apt install PostgreSQL-17 -y
sudo -u postgres psql -c "ALTER USER postgres PASSWORD '$INSTANCE_ID';"

#################################
## RabbitMQ (& Erlang) install ##
#################################
curl -1sLf "https://keys.openpgp.org/vks/v1/by-fingerprint/0A9AF2115F4687BD29803A206B73A36E6026DFCA" | sudo gpg --dearmor > /usr/share/keyrings/com.rabbitmq.team.gpg
curl -1sLf "https://keyserver.ubuntu.com/pks/lookup?op=get&search=0xf77f1eda57ebb1cc" | sudo gpg --dearmor > /usr/share/keyrings/net.launchpad.ppa.rabbitmq.erlang.gpg
curl -1sLf "https://packagecloud.io/rabbitmq/rabbitmq-server/gpgkey" | sudo gpg --dearmor > /usr/share/keyrings/io.packagecloud.rabbitmq.gpg
rm -rf /etc/apt/sources.list.d/rabbitmq.list
rm -rf /etc/apt/sources.list.d/erlang.list
echo "deb [signed-by=/usr/share/keyrings/net.launchpad.ppa.rabbitmq.erlang.gpg] http://ppa.launchpad.net/rabbitmq/rabbitmq-erlang/ubuntu $(lsb_release -sc) main" >> /etc/apt/sources.list.d/rabbitmq.list
echo "deb-src [signed-by=/usr/share/keyrings/net.launchpad.ppa.rabbitmq.erlang.gpg] http://ppa.launchpad.net/rabbitmq/rabbitmq-erlang/ubuntu $(lsb_release -sc) main" >> /etc/apt/sources.list.d/rabbitmq.list
echo "deb [signed-by=/usr/share/keyrings/io.packagecloud.rabbitmq.gpg] https://packagecloud.io/rabbitmq/rabbitmq-server/ubuntu/ $(lsb_release -sc) main" >> /etc/apt/sources.list.d/rabbitmq.list
echo "deb-src [signed-by=/usr/share/keyrings/io.packagecloud.rabbitmq.gpg] https://packagecloud.io/rabbitmq/rabbitmq-server/ubuntu/ $(lsb_release -sc) main" >> /etc/apt/sources.list.d/rabbitmq.list
apt-get update
apt-get -f install # required for collectd
apt --fix-broken install -y # required for collectd
apt-get -y install erlang
apt-get -y install rabbitmq-server

############################
## RabbitMQ Configuration ##
############################
mkdir /rabbitmq

INSTANCE_TYPE=$(ec2metadata --instance-type)

chown rabbitmq:rabbitmq /rabbitmq/

rabbitmq-plugins enable rabbitmq_management
rabbitmq-plugins enable rabbitmq_prometheus

## Set up proper RabbitMQ cluster cookie
RABBIT_ERLANG_COOKIE="IM-INBOUND-PIPELINE"
service rabbitmq-server stop || true
echo $RABBIT_ERLANG_COOKIE > /var/lib/rabbitmq/.erlang.cookie

mkdir /etc/systemd/system/rabbitmq-server.service.d
echo "[Service]" >> /etc/systemd/system/rabbitmq-server.service.d/limits.conf
echo "LimitNOFILE=900000" >> /etc/systemd/system/rabbitmq-server.service.d/limits.conf

aws s3 cp "s3://endeavour-deployment/InformationModel(v2)/Rabbit/rabbitmq.config" /etc/rabbitmq/rabbitmq.config

echo "# I am a complete rabbitmq-env.conf file." >> /etc/rabbitmq/rabbitmq-env.conf
echo "# Comment lines start with a hash character." >> /etc/rabbitmq/rabbitmq-env.conf
echo "# This is a /bin/sh script file - use ordinary envt var syntax" >> /etc/rabbitmq/rabbitmq-env.conf
echo "RABBITMQ_MNESIA_BASE=/rabbitmq/mnesia" >> /etc/rabbitmq/rabbitmq-env.conf

mkdir /etc/rabbitmq/certs
aws s3 cp "s3://endeavour-deployment/InformationModel(v2)/Rabbit/rabbitmq.pem" /etc/rabbitmq/certs
aws s3 cp "s3://endeavour-deployment/InformationModel(v2)/Rabbit/rabbitmqkey.pem" /etc/rabbitmq/certs
aws s3 cp "s3://endeavour-deployment/InformationModel(v2)/Rabbit/ca.pem" /etc/rabbitmq/certs
aws s3 cp "s3://endeavour-deployment/InformationModel(v2)/Rabbit/rabbitmq-admin.pem" /etc/rabbitmq/certs
aws s3 cp "s3://endeavour-deployment/InformationModel(v2)/Rabbit/rabbitmq-adminkey.pem" /etc/rabbitmq/certs
chown -R rabbitmq /etc/rabbitmq/certs

service rabbitmq-server start
sleep 5

rabbitmqctl add_user admin $INSTANCE_ID
rabbitmqctl set_user_tags admin administrator
rabbitmqctl set_permissions -p / admin ".*" ".*" ".*"

rabbitmqctl add_user im_pipeline $INSTANCE_ID
rabbitmqctl set_permissions -p / im_pipeline ".*" ".*" ".*"

rabbitmqctl add_user monitoring monitoring
rabbitmqctl set_user_tags monitoring monitoring
rabbitmqctl set_permissions -p / monitoring "" "" ""

wget -O /home/ubuntu/rabbitmqadmin --no-check-certificate https://localhost:15671/cli/rabbitmqadmin
chmod +x /home/ubuntu/rabbitmqadmin

rabbitmqctl add_vhost Inbound
rabbitmqctl set_permissions -p Inbound admin ".*" ".*" ".*"

# Inbound File
/home/ubuntu/rabbitmqadmin -u admin -p $INSTANCE_ID -V Inbound declare exchange name=File type=topic

/home/ubuntu/rabbitmqadmin -u admin -p $INSTANCE_ID -V Inbound declare queue name=File-EMIS
/home/ubuntu/rabbitmqadmin -u admin -p $INSTANCE_ID -V Inbound declare binding source=File destination=File-EMIS routing_key="endeavour-inbound.EMIS.#"

/home/ubuntu/rabbitmqadmin -u admin -p $INSTANCE_ID -V Inbound declare queue name=File-TPP
/home/ubuntu/rabbitmqadmin -u admin -p $INSTANCE_ID -V Inbound declare binding source=File destination=File-TPP routing_key="endeavour-inbound.TPP.#"

/home/ubuntu/rabbitmqadmin -u admin -p $INSTANCE_ID -V Inbound declare exchange name=File-Undeliverable type=fanout
/home/ubuntu/rabbitmqadmin -u admin -p $INSTANCE_ID -V Inbound declare queue name=File-Undelivered
/home/ubuntu/rabbitmqadmin -u admin -p $INSTANCE_ID -V Inbound declare binding source=File-Undeliverable destination=File-Undelivered
rabbitmqctl -p Inbound set_policy "Undeliverable Files" "^File$" '{"alternate-exchange":"File-Undeliverable"}' --apply-to exchanges

# Inbound Data
/home/ubuntu/rabbitmqadmin -u admin -p $INSTANCE_ID -V Inbound declare exchange name=Data type=topic

/home/ubuntu/rabbitmqadmin -u admin -p $INSTANCE_ID -V Inbound declare queue name=Data-EMIS
/home/ubuntu/rabbitmqadmin -u admin -p $INSTANCE_ID -V Inbound declare binding source=Data destination=Data-EMIS routing_key="endeavour-inbound.EMIS.#"

/home/ubuntu/rabbitmqadmin -u admin -p $INSTANCE_ID -V Inbound declare queue name=Data-TPP
/home/ubuntu/rabbitmqadmin -u admin -p $INSTANCE_ID -V Inbound declare binding source=Data destination=Data-TPP routing_key="endeavour-inbound.TPP.#"

/home/ubuntu/rabbitmqadmin -u admin -p $INSTANCE_ID -V Inbound declare exchange name=Data-Undeliverable type=fanout
/home/ubuntu/rabbitmqadmin -u admin -p $INSTANCE_ID -V Inbound declare queue name=Data-Undelivered
/home/ubuntu/rabbitmqadmin -u admin -p $INSTANCE_ID -V Inbound declare binding source=Data-Undeliverable destination=Data-Undelivered
rabbitmqctl -p Inbound set_policy "Undeliverable Data" "^Data$" '{"alternate-exchange":"Data-Undeliverable"}' --apply-to exchanges

# Inbound FilingOutcome
/home/ubuntu/rabbitmqadmin -u admin -p $INSTANCE_ID -V Inbound declare exchange name=FilingOutcome type=topic

/home/ubuntu/rabbitmqadmin -u admin -p $INSTANCE_ID -V Inbound declare queue name=FilingOutcome-EMIS
/home/ubuntu/rabbitmqadmin -u admin -p $INSTANCE_ID -V Inbound declare binding source=FilingOutcome destination=FilingOutcome-EMIS routing_key="endeavour-inbound.EMIS.#"

/home/ubuntu/rabbitmqadmin -u admin -p $INSTANCE_ID -V Inbound declare queue name=FilingOutcome-TPP
/home/ubuntu/rabbitmqadmin -u admin -p $INSTANCE_ID -V Inbound declare binding source=FilingOutcome destination=FilingOutcome-TPP routing_key="endeavour-inbound.TPP.#"

/home/ubuntu/rabbitmqadmin -u admin -p $INSTANCE_ID -V Inbound declare exchange name=FilingOutcome-Undeliverable type=fanout
/home/ubuntu/rabbitmqadmin -u admin -p $INSTANCE_ID -V Inbound declare queue name=FilingOutcome-Undelivered
/home/ubuntu/rabbitmqadmin -u admin -p $INSTANCE_ID -V Inbound declare binding source=FilingOutcome-Undeliverable destination=FilingOutcome-Undelivered
rabbitmqctl -p Inbound set_policy "Undeliverable FilingOutcome" "^FilingOutcome" '{"alternate-exchange":"FilingOutcome-Undeliverable"}' --apply-to exchanges
