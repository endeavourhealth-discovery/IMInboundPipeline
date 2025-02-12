require("dotenv").config();
const amqp = require("amqplib");

exports.handler = async (event) => {
  const connectionUrl = process.env.RABBITMQ_URL || `amqp://localhost`;

  let connection;
  let channel;

  try {
    connection = await amqp.connect(connectionUrl);
    channel = await connection.createChannel();
    console.log("Connected");
    const EXCHANGE_NAME = "File"; // get from sns event
    const ROUTING_KEY = "EMIS.observation"; // get from sns event
    channel.assertExchange(EXCHANGE_NAME, "topic");

    for (const record of event.Records) {
      const snsMessage = record.Sns.Message;
      channel.publish(EXCHANGE_NAME, ROUTING_KEY, Buffer.from(snsMessage));
      console.log(`Published message to RabbitMQ through exchange '${EXCHANGE_NAME}' and topic '${ROUTING_KEY}'`);
    }

    return {
      statusCode: 200,
      body: JSON.stringify("Messages published successfully."),
    };
  } catch (error) {
    console.error("Error processing SNS event:", error);

    if (channel) {
      try {
        await channel.close();
      } catch (closeError) {
        console.error("Error closing channel:", closeError);
      }
    }
    if (connection) {
      try {
        await connection.close();
      } catch (closeError) {
        console.error("Error closing connection:", closeError);
      }
    }
    throw error;
  }
};
