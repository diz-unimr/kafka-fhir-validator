# kafka-fhir-validator
Kafka processor validates FHIR resources with HAPI FHIR Validator

## Configuration

| key                              | description                                                                         |
|----------------------------------|-------------------------------------------------------------------------------------|
| FHIR_VALIDATION_INPUT1           | kafka topic name to be consumed                                                     |
| FHIR_VALIDATION_OUTPUT1          | kafka topic name to be used for validation output                                   |
| FHIR_VALIDATION_PROFILE_LOCATION | local folder containing your profile json files. eg. dowloaded from simplifier.net  |
| SPRING_KAFKA_BOOTSTRAPSERVERS    | comma seperated list of your kafka brokers compined with external port              |

## Deployment

In development environment you could use FHIR profiles downloaded via `npm install`. Default configuration will use thous located in folder 'node_modules'.

For production use you should manually deploy wanted FHIR *json* files.

To get started check out *deploy* folder and the provided *docker-compose.yml* files.

