# Spring Boot App on Azure Sample

> Simple Spring Boot web application with some Azure specific bindings.

## Building

The application is built with Gradle. At present, it is configured to use the `war` plugin and package as an executable WAR that can also be dropped into a servlet environment.
This is useful keeping local development and execution straightforward, while keeping the deployment options flexible.

Build the application with Gradle:

```bash
./gradlew clean build
```

The WAR is automatically output at `build/libs/ROOT.war`.
This makes it easy to drop the application into, say, Tomcat's `webapps` directory and have it function as the root servlet context.

## Azure

* App Service for Windows
  - Tomcat 9
  - Java 11
  - POST https://web-app-name.scm.azurewebsites.net/api/wardeploy
    Header: Authorization Basic-from-publish-profile
    Body: binary WAR file
  - App Service Logs > Application Logging (file system) - time-bound, only some Spring logs in catalina.**.log
  - Log Stream is worthless
* Application Insights
  - Instrumentation key into [`src/main/resources/application.properties`](/src/main/resources/application.properties) and [`src/main/resources/logback.xml`](/src/main/resources/logback.xml)
  - Logs (analytics) - query language for looking at "events" - pay attention to `traces` (logs), `requests` (HTTP), `performanceCounters` (metrics)
  - Metrics to investigate Micrometer-reported metrics, look under `azure.applicationinsights` namespace

## Simulate Application Load

Use [Taurus](https://gettaurus.org/) to execute the [`load-simulation.yml`](/load-simulation.yml) HTTP traffic test.
Update any URL configurations needed, and then execute Taurus, for instance, with Docker Compose:

```bash
docker-compose run taurus load-simulation.yml
```

When running against a local target, this requires that either the `app` or `tomcat` Docker Compose services are already running.
