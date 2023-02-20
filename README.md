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

### Azure SQL

Azure SQL, a PaaS Microsoft SQL Server database offering, is a common choice for enterprises adopting Azure as a cloud platform.

This branch of this Spring Boot sample app shows how to integrate with Azure SQL.
The following concepts are demonstrated:

* Including the Microsoft SQL Server driver
* Entity mapping with JPA
* Authentication with database credentials
* Authentication with Azure AD credentials

Follow [the quickstart in the Microsoft Docs](https://learn.microsoft.com/en-us/azure/azure-sql/database/single-database-create-quickstart?view=azuresql&tabs=azure-cli) to set up a resource group, SQL server, and SQL database for use with this sample.
Make sure to provide your IP address in the `startIp` and `endIp` variables.
Note the output of the commands to feed into your app configuration.
You can use the `id` property output of the `az sql db create` command to feed into `az sql db show-connection-string --client jdbc --ids $fullResourceId` in order to get a JDBC URL.

After standing up the database successfully, configure the application to authenticate and run the Spring Boot app.
Un-comment the set of properties in `application.properties` for the desired authentication method and update values as appropriate.
When the app starts up, use the `http://localhost:8080/api/products` endpoint to test the connection and Spring Data setup.

#### Authentication with database credentials

Authenticating to Azure SQL with plain old database credentials is the most straightforward method of those available.
When you create an Azure SQL database, you provision an admin account.
It is best practice _not_ to use the admin account for your application, and instead provision an app-specific account with a constrained set of permissions.
For instance:

```sql
USE my_database; 

CREATE ROLE app_role; 
GO 

GRANT SELECT, INSERT, UPDATE, DELETE TO app_role; 
GO 

CREATE USER username WITH PASSWORD = 'password'; 
GO 

ALTER ROLE app_role ADD MEMBER username; 
GO 
```

When you configure your Spring datasource, your properties should look something like:

```yaml
spring:
  datasource:
    url: "jdbc:sqlserver://sqlservername.database.windows.net:1433;database=sqldbname;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30"
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
```

Of course, handle the account credentials appropriately.

#### Authentication with Azure AD credentials

A more centrally manageable method of authenticating to an Azure SQL database is with Azure Active Directory credentials.
This method allows, among other benefits, the use of Managed Identities in Azure.
For instance, if the app is deployed to App Service, the Managed Identity feature allows the app to have an identity automatically provisioned and made available to the application runtime.
This helps protect credentials from being leaked.

One potential pain point with using Managed Identities is the developer experience.
Managed Identities are provided by the Azure runtime, but developers are often coding, compiling, and running their applications on local machines during the development lifecycle.

One way of allowing developers to authenticate from their local environment while still leveraging Managed Identities during deployment, is making use of the Azure Default Credential chain.
In version 12+ of the Microsoft SQL Server JDBC driver, the driver can authenticate based on a chain of identities including an Azure CLI token.
A developer can install the Azure CLI, authenticate to a tenant via `az login`, and the database driver can detect that authentication token as a fallback.

Before configuring the application, the developer's Azure Active Directory account must be given permissions to use the database.
Check [the Microsoft documentation on assigning roles](https://learn.microsoft.com/en-us/azure/developer/java/spring-framework/migrate-sql-database-to-passwordless-connection?tabs=spring%2Capp-service%2Cassign-role-azure-cli#assign-roles-to-the-managed-identity) for up-to-date steps.
At the time of writing, the following SQL commands should be executed on the target database where `$AZ_DATABASE_AD_MI_USERNAME` is the developer's Azure AD username.
These commands also apply to the Managed Identity created by Azure services such as App Service.

```sql
CREATE USER "$AZ_DATABASE_AD_MI_USERNAME" FROM EXTERNAL PROVIDER;
ALTER ROLE db_datareader ADD MEMBER "$AZ_DATABASE_AD_MI_USERNAME";
ALTER ROLE db_datawriter ADD MEMBER "$AZ_DATABASE_AD_MI_USERNAME";
GO
```

Configuring the Spring Boot app requires using the dependency management feature of Maven or the Gradle plugin to pull in the `com.azure.spring:spring-cloud-azure-dependencies` artifact.
Then, the application depends on the `com.azure.spring:spring-cloud-azure-starter` package, and of course the database driver: `com.microsoft.sqlserver:mssql-jdbc:12.2.0.jre11`.
Check the [`build.gradle`](./build.gradle) file for how to declare these dependencies in Gradle.

With the Azure Spring Cloud dependencies in the classpath, ensure that the Spring datasource URL includes `authentication=ActiveDirectoryDefault` for developer machines:

```yaml
spring:
  datasource:
    url: "jdbc:sqlserver://sqlservername.database.windows.net:1433;database=sqldbname;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;authentication=ActiveDirectoryDefault"
```

As long as the developer has a valid authentication token with the Azure CLI, they should be able to start the Spring Boot app and the Microsoft SQL Server driver will find that credential and authenticate to the database.

Spring profiles or environment variables can be used to override the datasource URL once deployed and leverage the Managed Identity of the compute service.

## Simulate Application Load

Use [Taurus](https://gettaurus.org/) to execute the [`load-simulation.yml`](/load-simulation.yml) HTTP traffic test.
Update any URL configurations needed, and then execute Taurus, for instance, with Docker Compose:

```bash
docker-compose run taurus load-simulation.yml
```

When running against a local target, this requires that either the `app` or `tomcat` Docker Compose services are already running.
