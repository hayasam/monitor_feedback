
# Introduction

Is a RESTfull Web API that provides endpoints for storing, receiving and updating the feedback configuration.
For a detailed API documentation, please visit: http://docs.supersedeorchestratorapi.apiary.io/#

# Table of Contents

- [Introduction](#introduction)
- [Table of Content](#table-of-content)
- [Installation](#installation)
- [Deployment](#deployment)
- [Tests](#tests)
- [Directory Structure](#directory-structure)
- [Docker](#docker)
- [License](#license)

# Installation

```bash
# clone the repository

cd orchestrator

# copy the configuration files for your configuration
cp src/main/resources/application.properties-dist src/main/resources/application.properties
cp src/main/resources/application-test.properties-dist src/main/resources/application-test.properties
```

Create a test database and a local database to run the application locally if needed. You can find the newest DB dump in src/main/resources/db. Fill in your DB credentials and all other required values in the newly created application.properties and application-test.properties. Do not add this file to the GIT index.

```bash
cd orchestrator
# install the project's dependencies and generate the war file in build/libs/
gradle build
```

DB dump in src/main/resources/db/migrations/supersede_orchestrator_db_structure.sql 

# Tests

To run the integration tests, execute the following commands:

```bash
gradle test
```

# Docker

Build the JAR and the images and containers for the Java Spring Repository and the Repository DB. 

The jdbs connection string in the application.properties should have the DB container service name as host:

```bash
spring.datasource.url = jdbc:mysql://mysqldbserver:3306/<db_name>?useSSL=false
```

```bash
gradle clean build jar
docker-compose up -d
```

Check if the 2 containers are up and running:
 
```bash
docker ps -a  
```

## Troubleshooting

You might get: 
```bash
ERROR org.springframework.boot.SpringApplication - Application startup failed
...
Caused by: java.lang.IllegalArgumentException: No auto configuration classes found in META-INF/spring.factories. If you are using a custom packaging, make sure that file is correct.
```

Completely delete the container and image: 
```bash
docker rm -v <container_name>
docker rmi <image_name>
```

**Attention:** This would delete all stopped containers and all unused images:
```bash
docker ps -q |xargs docker rm
docker images -q |xargs docker rmi
```

Finally execute:
```bash
gradle build jar -x test
gradle bootRepackage
docker-compose up -d 
```

If you execute tests, and you get 
```bash
Process finished with exit code 1
Class not found: "ch.fhnw.cere.orchestrator.OrchestratorApplicationTests"Empty test suite.
```

In IntelliJ: Right-click on the project and choose "Open Module Settings". Go to modules --> paths and set the output path to out/production and the test path to out/test. 


# License

Apache License 2.0