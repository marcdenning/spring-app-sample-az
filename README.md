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

### Azure Storage

Azure Storage is frequently used for its Blob storage operations to handle all types of files.
Utilizing a Blob Storage Container is a much more performant and cost-efficient way of storing binary data or files than managing data in a database for instance.

This branch of the Spring Boot sample app shows how to integrate with Azure Blob Storage.
The following concepts are demonstrated:

* Connecting to a Blob Storage Container
* Uploading a file
* Downloading a file

To get started, create an Azure Storage account in your Azure subscription.
You may follow the quickstart [_Upload, download, and list blobs with the Azure portal_](https://learn.microsoft.com/en-us/azure/storage/blobs/storage-quickstart-blobs-portal) for more details about creating a Storage Account for storing blob data.
When creating your Storage Account, allow public access for easy development of a PoC, but consider appropriate networking rules for a real use case.

It is also recommended to use the Azure CLI to help authenticate to the Storage Account during development.
The Azure CLI (`az` command) is included in the Gitpod workspace if you are using Gitpod.
Otherwise, [install the Azure CLI](https://learn.microsoft.com/en-us/cli/azure/install-azure-cli) on your machine.

Log in to your Azure account using `az login` (in Gitpod, it is best to use `az login --use-device-code`).
This allows an Azure credential to be stored in your workspace, and the Spring Cloud Azure dependencies will automatically pick it up to authenticate to Azure services.

In your `application.properties` file, include the following properties with the name of your storage account and the "Blob service" endpoint found in the Azure portal under Settings > Endpoints:

```properties
spring.cloud.azure.storage.blob.account-name=${AZURE_STORAGE_ACCOUNT_NAME}
spring.cloud.azure.storage.blob.endpoint=${AZURE_STORAGE_ACCOUNT_ENDPOINT}
```

Update the values to your storage account or rely on the environment variables listed to populate these configuration properties.
These properties are used by the Spring Cloud Azure library to create your `BlobServiceClient` automatically.

## Simulate Application Load

Use [Taurus](https://gettaurus.org/) to execute the [`load-simulation.yml`](/load-simulation.yml) HTTP traffic test.
Update any URL configurations needed, and then execute Taurus, for instance, with Docker Compose:

```bash
docker-compose run taurus load-simulation.yml
```

When running against a local target, this requires that either the `app` or `tomcat` Docker Compose services are already running.
