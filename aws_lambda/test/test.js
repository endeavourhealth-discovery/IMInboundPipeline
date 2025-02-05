const { handler } = require("../index");
const event = require("./event.json");

// Invoke the Lambda function
handler(event)
  .then((response) => {
    console.log("Lambda response:", response);
  })
  .catch((error) => {
    console.error("Lambda error:", error);
  });
