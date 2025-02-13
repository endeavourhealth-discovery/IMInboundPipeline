import { S3Event } from 'aws-lambda';
import { connect } from 'amqplib';

const RABBITMQ_URL = process.env.RABBITMQ_URL ?? `amqp://localhost/`;
const RABBITMQ_EXCHANGE = process.env.RABBITMQ_EXCHANGE ?? `File`;

export const handler = async (event: S3Event): Promise<string | undefined> => {
  // Get the object from the event and show its content type
  try {
    console.log(`Connecting...`);
    const connection = await connect(RABBITMQ_URL);
    console.log(`Creating channel...`);
    const channel = await connection.createChannel();
    console.log(`Processing events...`)
    for (const record of event.Records) {
      const key = decodeURIComponent(record.s3.object.key.replace(/\+/g, ` `));
      const routingKey = record.s3.bucket.name + `.` + key.replace(/[/_]/g, `.`);

      if (channel.publish(RABBITMQ_EXCHANGE, routingKey, Buffer.from(record.s3.bucket.name + "/" + key), { persistent: true }))
        console.log(`Published message to exchange '${RABBITMQ_EXCHANGE}' with key '${routingKey}'`);
      else
        console.error(`Failed to publish message to exchange '${RABBITMQ_EXCHANGE}' with key '${routingKey}'`);
    }

    await channel.close();
    await connection.close();
    console.log(`Done`)
    return `Messages published successfully.`;
  } catch (e) {
    console.error(e);
  }

};