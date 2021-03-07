# Club-Management-Spring-Server
Server Side for Full Stack Club Management System

## Initail Guide for Development
- Backend tab
- BE Endpoints Details tab
https://docs.google.com/spreadsheets/d/1xfVHDfJts9OrrrFj1tEIZq4TAzy58dAowS_K-u8CZW8/edit#gid=0

This application was generated using JHipster 6.1.2, you can find documentation and help at [https://www.jhipster.tech/documentation-archive/v6.1.2](https://www.jhipster.tech/documentation-archive/v6.1.2).

## Development

To start your application in the dev profile, simply run:

    ./mvnw

For further instructions on how to develop with JHipster, have a look at [Using JHipster in development][].

### Setup Firebase Credentials
if you just want to run the test, you do not need to follow the instructions in this section. However, if you want to run the application, please follow the instructions below.
1. Get the private key JSON file from Ka Chun
2. Store the private key is some place and get its path. e.g. `D:\code\credentials\ccclubmanagement-firebase-adminsdk-7lbr4-924a830b45.json`
3. Configure Environment variable. Add a new System Variable. 
```
variable name: GOOGLE_APPLICATION_CREDENTIALS // must be the same!
variable value: D:\code\credentials\ccclubmanagement-firebase-adminsdk-7lbr4-924a830b45.json // the path
```
4. restart your IDE, or better yet, restart your machine

resources: https://firebase.google.com/docs/admin/setup#windows

## Building for production

### Packaging as jar

To build the final jar and optimize the clubmanagement application for production, run:

    ./mvnw -Pprod clean verify

To ensure everything worked, run:

    java -jar target/*.jar

Refer to [Using JHipster in production][] for more details.

### Packaging as war

To package your application as a war in order to deploy it to an application server, run:

    ./mvnw -Pprod,war clean verify

## Testing

To launch your application's tests, run:

    ./mvnw verify

For more information, refer to the [Running tests page][].

### Code quality

Sonar is used to analyse code quality. You can start a local Sonar server (accessible on http://localhost:9001) with:

```
docker-compose -f src/main/docker/sonar.yml up -d
```

You can run a Sonar analysis with using the [sonar-scanner](https://docs.sonarqube.org/display/SCAN/Analyzing+with+SonarQube+Scanner) or by using the maven plugin.

Then, run a Sonar analysis:

```
./mvnw -Pprod clean verify sonar:sonar
```

If you need to re-run the Sonar phase, please be sure to specify at least the `initialize` phase since Sonar properties are loaded from the sonar-project.properties file.

```
./mvnw initialize sonar:sonar
```

or

For more information, refer to the [Code quality page][].

## Using Docker to simplify development (optional)

You can use Docker to improve your JHipster development experience. A number of docker-compose configuration are available in the [src/main/docker](src/main/docker) folder to launch required third party services.

For example, to start a mysql database in a docker container, run:

    docker-compose -f src/main/docker/mysql.yml up -d

To stop it and remove the container, run:

    docker-compose -f src/main/docker/mysql.yml down

You can also fully dockerize your application and all the services that it depends on.
To achieve this, first build a docker image of your app by running:

    ./mvnw -Pprod verify jib:dockerBuild

Then run:

    docker-compose -f src/main/docker/app.yml up -d

For more information refer to [Using Docker and Docker-Compose][], this page also contains information on the docker-compose sub-generator (`jhipster docker-compose`), which is able to generate docker configurations for one or several JHipster applications.

## Continuous Integration (optional)

To configure CI for your project, run the ci-cd sub-generator (`jhipster ci-cd`), this will let you generate configuration files for a number of Continuous Integration systems. Consult the [Setting up Continuous Integration][] page for more information.

[jhipster homepage and latest documentation]: https://www.jhipster.tech
[jhipster 6.1.2 archive]: https://www.jhipster.tech/documentation-archive/v6.1.2
[using jhipster in development]: https://www.jhipster.tech/documentation-archive/v6.1.2/development/
[service discovery and configuration with the jhipster-registry]: https://www.jhipster.tech/documentation-archive/v6.1.2/microservices-architecture/#jhipster-registry
[using docker and docker-compose]: https://www.jhipster.tech/documentation-archive/v6.1.2/docker-compose
[using jhipster in production]: https://www.jhipster.tech/documentation-archive/v6.1.2/production/
[running tests page]: https://www.jhipster.tech/documentation-archive/v6.1.2/running-tests/
[code quality page]: https://www.jhipster.tech/documentation-archive/v6.1.2/code-quality/
[setting up continuous integration]: https://www.jhipster.tech/documentation-archive/v6.1.2/setting-up-ci/
