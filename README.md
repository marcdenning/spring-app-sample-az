[![build-app](https://github.com/marcdenning/spring-app-sample-az/actions/workflows/build-app.yml/badge.svg)](https://github.com/marcdenning/spring-app-sample-az/actions/workflows/build-app.yml)

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
  - Java 21
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

### Microsoft Entra Authentication for Resource Server

This feature utilizes the [Spring Boot Starter Guide from Microsoft](https://learn.microsoft.com/en-us/azure/developer/java/spring-framework/spring-boot-starter-for-entra-developer-guide?tabs=SpringCloudAzure5x) to set up the application.
Specifically, see the section [Protect a resource server/API](https://learn.microsoft.com/en-us/azure/developer/java/spring-framework/spring-boot-starter-for-entra-developer-guide?tabs=SpringCloudAzure5x#protect-a-resource-serverapi) to require authentication and authorization on the API.

First, you need an App Registration in Entra:

1. Create a new app registration following the [Quickstart: Register an application in Microsoft Entra ID](https://learn.microsoft.com/en-us/entra/identity-platform/quickstart-register-app).
  Note the application ID.
2. Then add a client secret using [Add and manage application credentials in Microsoft Entra ID](https://learn.microsoft.com/en-us/entra/identity-platform/how-to-add-credentials?tabs=client-secret).
  Note the client secret as well.
3. Now, ensure the registration exposes an API scope using [Configure an application to expose a web API](https://learn.microsoft.com/en-us/entra/identity-platform/quickstart-configure-app-expose-web-apis).
4. Configure the identified properties in the application following the notes below and start the API.
  It should now require an `Authorization` header formatted as `Bearer $JWT` where you provide the bearer token value.

TODO: How to request a token and which to use in Postman

Set these environment variables from your Microsoft Entra app registration:

* `AZURE_AD_TENANTID` - GUID identifying your Microsoft Entra
* `AZURE_AD_CLIENTID` - GUID identifying the client from the app registration
* `AZURE_AD_CLIENTSECRET` - secret value assigned to the app registration to authenticate to Microsoft Entra
* `AZURE_AD_APPURI` - the URI to use to compare the `aud` claim of the token to
  * This may be just the client ID, or it may be prefixed with a protocol such as `api://`

## Simulate Application Load

Use [Taurus](https://gettaurus.org/) to execute the [`load-simulation.yml`](/load-simulation.yml) HTTP traffic test.
Update any URL configurations needed, and then execute Taurus, for instance, with Docker Compose:

```bash
docker-compose run taurus load-simulation.yml
```

When running against a local target, this requires that either the `app` or `tomcat` Docker Compose services are already running.
