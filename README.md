[![Build Status](https://dev.azure.com/marcdenning/spring-app-sample/_apis/build/status/kenobi883.spring-app-sample-az?branchName=main)](https://dev.azure.com/marcdenning/spring-app-sample/_build/latest?definitionId=1&branchName=main)

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

### Gitpod

You may use [Gitpod](https://www.gitpod.io/) to quickly create a dev environment appropriate for this app.
If you already have a Gitpod account, simply navigate to [https://gitpod.io#https://github.com/marcdenning/spring-app-sample-az](https://gitpod.io#https://github.com/marcdenning/spring-app-sample-az) to open a new workspace based on this repo.

## Azure

* App Service for Windows
  - Tomcat 10
  - Java 17
  - ```
    POST https://web-app-name.scm.azurewebsites.net/api/wardeploy
    Header: Authorization Basic-from-publish-profile
    Body: binary WAR file
    ```
  - App Service Logs > Application Logging (file system) - time-bound, only some Spring logs in catalina.**.log
  - Log Stream is worthless
* Application Insights - use the [OpenTelemetry implementation](https://learn.microsoft.com/en-us/azure/azure-monitor/app/opentelemetry-enable?tabs=java) to instrument the app with a Java agent.
  This requires no code changes, and the instrumentation key can be set via an environment variable.
  - Logs (analytics) - query language for looking at "events" - pay attention to `traces` (logs), `requests` (HTTP), `performanceCounters` (metrics)
  - Metrics to investigate Micrometer-reported metrics, look under `azure.applicationinsights` namespace

### Azure AD Authentication for Resource Server

https://learn.microsoft.com/en-us/azure/developer/java/spring-framework/spring-boot-starter-for-azure-active-directory-developer-guide?tabs=SpringCloudAzure5x

TODO: How to set up the app registration

TODO: How to request a token and which to use in Postman

Set these environment variables from your Azure AD app registration:

* `AZURE_AD_TENANTID`
* `AZURE_AD_CLIENTID`
* `AZURE_AD_CLIENTSECRET`

## Simulate Application Load

Use [Taurus](https://gettaurus.org/) to execute the [`load-simulation.yml`](/load-simulation.yml) HTTP traffic test.
Update any URL configurations needed, and then execute Taurus, for instance, with Docker Compose:

```bash
docker-compose run taurus load-simulation.yml
```

When running against a local target, this requires that either the `app` or `tomcat` Docker Compose services are already running.
