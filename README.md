# AEM Cloud Cors

AEM as a Cloud Service project with CORS (Cross-Origin Resource Sharing) enabled for exposing JSON endpoints to external consumers across different origins, environments, and AEM instances.

This project demonstrates working CORS configuration across local development, Cloud Manager testing, and multi-environment deployments (DEV, STAGE, PROD). Built using Maven, tested locally against the AEM Cloud Service SDK, and deployed via Adobe Cloud Manager using Full Stack pipelines.

## Modules

The main parts of the template are:

* [core:](core/README.md) Java OSGi bundle with CORS filter logic, servlets, models, and custom endpoints (`com.aem.cors.core.*`)
* [it.tests:](it.tests/README.md) Java-based integration tests
* [ui.apps:](ui.apps/README.md) `/apps` and `/etc` content package: AEM components, templates (HTL), dialogs, clientlib definitions
* [ui.apps.structure:](ui.apps.structure/README.md) structural package (AEMaaCS immutability requirement)
* [ui.config:](ui.config/README.md) OSGi configurations and context-aware config, split by runmode
* [ui.content:](ui.content/README.md) sample page content and AEM DAM assets
* [ui.frontend:](ui.frontend/README.md) Webpack-based frontend build (TypeScript/JavaScript, Sass/SCSS) compiled to client libraries
* [ui.tests:](ui.tests/README.md) Cypress-based end-to-end UI tests
* all: single content package embedding all compiled modules and vendor dependencies (Core WCM Components, ACS AEM Commons, Access Control Tool, SPA Project Core, io.wcm CAConfig)
* dispatcher: Cloud Manager-optimized Dispatcher configuration with caching and security rules

## How to build

The project requires **Java 21** (enforced by Maven's `maven-enforcer-plugin`).

To build all modules:

    mvn clean install

To build and deploy to a local AEM Cloud Service SDK instance (author on localhost:4502):

    ./deploy.sh author

Or deploy to publish (localhost:5503):

    ./deploy.sh publish

The `deploy.sh` script automatically configures Java 21 and Maven profiles. For manual Maven commands:

    mvn clean install -PautoInstallSinglePackage
    mvn clean install -PautoInstallSinglePackagePublish -Daem.port=5503

To deploy a specific module only, run in its directory:

    mvn clean install -PautoInstallPackage

## Documentation

The build process also generates documentation in the form of README.md files in each module directory for easy reference. Depending on the options you select at build time, the content may be customized to your project.

## Testing

There are three levels of testing contained in the project:

### Unit tests

This show-cases classic unit testing of the code contained in the bundle. To
test, execute:

    mvn clean test

### Integration tests

This allows running integration tests that exercise the capabilities of AEM via
HTTP calls to its API. To run the integration tests, run:

    mvn clean verify -Plocal

Test classes must be saved in the `src/main/java` directory (or any of its
subdirectories), and must be contained in files matching the pattern `*IT.java`.

The configuration provides sensible defaults for a typical local installation of
AEM. If you want to point the integration tests to different AEM author and
publish instances, you can use the following system properties via Maven's `-D`
flag.

| Property              | Description                                         | Default value           |
|-----------------------|-----------------------------------------------------|-------------------------|
| `it.author.url`       | URL of the author instance                          | `http://localhost:4502` |
| `it.author.user`      | Admin user for the author instance                  | `admin`                 |
| `it.author.password`  | Password of the admin user for the author instance  | `admin`                 |
| `it.publish.url`      | URL of the publish instance                         | `http://localhost:5503` |
| `it.publish.user`     | Admin user for the publish instance                 | `admin`                 |
| `it.publish.password` | Password of the admin user for the publish instance | `admin`                 |

The integration tests in this archetype use the [AEM Testing
Clients](https://github.com/adobe/aem-testing-clients) and showcase some
recommended [best
practices](https://github.com/adobe/aem-testing-clients/wiki/Best-practices) to
be put in use when writing integration tests for AEM.

## CORS Configuration

CORS is enabled via Adobe's built-in Granite CORS Policy (OSGi config, no custom Java) and a
Dispatcher farm that caches the CORS response headers alongside the response body. See
[`docs/CORS.md`](docs/CORS.md) for the full breakdown.

**Example CORS request:**

    curl -i -H "Origin: http://localhost:3000" \
      http://localhost:4502/content/aemcors.json

## Static Analysis

The `aemanalyser-maven-plugin` performs static analysis for AEMaaCS compliance. It runs automatically during:

    mvn clean install

This validates package dependencies, OSGi constraints, and Cloud Service compatibility. See the [plugin documentation](https://github.com/adobe/aemanalyser-maven-plugin) for details and advanced configuration.

### UI tests

They will test the UI layer of your AEM application using Cypress framework.

Check README file in `ui.tests` module for more details.

Examples of UI tests in different frameworks can be found here: https://github.com/adobe/aem-test-samples

## ClientLibs

The frontend module is made available using an [AEM ClientLib](https://helpx.adobe.com/experience-manager/6-5/sites/developing/using/clientlibs.html). When executing the NPM build script, the app is built and the [`aem-clientlib-generator`](https://github.com/wcm-io-frontend/aem-clientlib-generator) package takes the resulting build output and transforms it into such a ClientLib.

A ClientLib will consist of the following files and directories:

- `css/`: CSS files which can be requested in the HTML
- `css.txt` (tells AEM the order and names of files in `css/` so they can be merged)
- `js/`: JavaScript files which can be requested in the HTML
- `js.txt` (tells AEM the order and names of files in `js/` so they can be merged
- `resources/`: Source maps, non-entrypoint code chunks (resulting from code splitting), static assets (e.g. icons), etc.

## Maven settings

The project comes with the auto-public repository configured. To setup the repository in your Maven settings, refer to:

    http://helpx.adobe.com/experience-manager/kb/SetUpTheAdobeMavenRepository.html
