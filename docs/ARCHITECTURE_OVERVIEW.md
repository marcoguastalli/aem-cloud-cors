# Architecture Overview

## Project Structure

`aem-cloud-cors` is an Adobe Experience Manager as a Cloud Service (AEMaaCS) Maven multi-module project with a focus on CORS (Cross-Origin Resource Sharing) configuration and JSON API endpoints.

### Core Module Structure

The `core` bundle contains the following layers:

```
com.aem.cors.core/
├── models/
│   └── components/
│       ├── link/LinkModel.java              # Link component Sling Model
│       └── htmlcomponent/HtmlComponentModel.java
├── servlets/
│   ├── AbstractSlingServlet.java             # Base servlet with JSON response handling
│   ├── CoreBundleServlet.java                # Health check endpoint
│   └── search/
│       └── SearchPageReferencesServlet.java  # Page reference search API
├── services/
│   ├── EnvironmentInfoService.java           # Interface for environment metadata
│   └── EnvironmentInfoServiceImpl.java        # OSGi service implementation
├── commonbeans/
│   ├── EnvironmentType.java                  # Enum: AUTHOR, PUBLISH, PROD_*, STAGE_*, etc.
│   ├── PathsBean.java                        # Input DTO for path-based operations
│   ├── RestOperationResult.java              # Generic response wrapper
│   └── search/
│       ├── SearchPageReferencesBean.java     # Search result wrapper
│       └── SearchResultPage.java             # Individual page reference
├── exceptions/
│   └── AemRuntimeException.java              # Unchecked exception for runtime errors
├── utils/                                     # Pure-Java utilities (no AEM/Sling/JCR/OSGi imports)
│   ├── HttpUtils.java                        # Request/tracking utilities
│   ├── JsonUtils.java                        # JSON serialization via Jackson
│   ├── LoggerUtils.java                      # Tracking ID logging
│   ├── ... (32 more generic classes: dates, strings, hashing, URLs, XML, etc.)
│   └── json/, page/domain/, pagination/, rest/, xml/   # generic value objects and interfaces
├── aemutils/                                  # AEM/Sling/JCR/OSGi-dependent utilities
│   ├── ResourceUtilsNeo.java, PageUtilsNeo.java, HttpUtilsNeo.java, ...
│   └── page/, pagination/, rest/             # page ops, PaginationServiceImpl, RestUtilsImpl
└── CoreConstants.java                        # Static constants for headers, strings, etc.
```

See [Utility Packages](#utility-packages-utils--aemutils) below for the full breakdown of `utils`/`aemutils`.

### Utility Packages (`utils` / `aemutils`)

`core/src/main/java/com/aem/cors/core/{utils,aemutils}` hold 59 utility classes imported from a
personal cross-project reference collection, split by whether they touch AEM/Sling/JCR/OSGi APIs.
All PostFinance/Wasisa client-specific types, constants, and business logic were stripped out
during import — only generic, reusable logic was kept. Every class has a corresponding JUnit 5
test under `core/src/test/java/.../{utils,aemutils}`.

**`com.aem.cors.core.utils`** — pure Java/generic-lib only, no AEM/Sling/JCR/OSGi imports:

| Category | Classes |
|---|---|
| Strings/arrays/numbers | `StringsUtils`, `ArrayComparator`, `NumberUtils`, `HashUtils`, `HtmlUtils`, `LinkUtils`, `MenuUtils` |
| Dates/locale | `CalendarUtils`, `DateFormatterUtils`, `LocalDateTimeUtils`, `LocaleUtils` |
| HTTP/cookies/URLs | `HttpUtils`, `CookieUtils`, `UrlUtilsNeo`, `PathTenantUtils` |
| JSON | `JsonUtils`, `json/GsonUtils`, `json/customserializer/GsonCustomStringSerializer`, `json/model/*` (8 response/error DTOs) |
| REST domain | `rest/RestResponse`, `rest/domain/AbstractRestResponse`, `rest/domain/MapRestResponse`, `rest/exception/RestRequestException` |
| Misc | `LoggerUtils`, `LoggerUtilsNeo`, `MathUtils`, `StreamUtils`, `xml/XmlUtils`, `pagination/PaginationService` (interface), `page/domain/PageInfo` |

**`com.aem.cors.core.aemutils`** — depends on Sling/AEM/JCR/OSGi/Granite APIs:

| Category | Classes |
|---|---|
| Resources | `ResourceUtils`, `ResourceUtilsNeo`, `ResourceMultiValueFieldUtils`, `ResourceSortUtils`, `NodeUtils` |
| Pages | `PageUtilsNeo`, `page/PageManagingUtils`, `page/PageOperationsUtils` |
| HTTP/REST | `HttpRequestUtils`, `HttpUtilsNeo`, `RestUtils` (interface), `rest/RestUtilsImpl`, `QueryManagerUtils` |
| JSON/model export | `JsonJacksonUtils`, `ModelExporterUtils` |
| Dialogs/UI | `DialogValidationUtils`, `LightBoxUtils`, `PropertiesUtils`, `PathComparatorUtils` |
| Misc | `ImageUtils`, `TagsUtils`, `UrlUtils`, `UserUtils`, `pagination/PaginationServiceImpl` (OSGi `@Component`) |

14 files from the original collection were excluded entirely (not partially stripped) because they
were too tightly coupled to proprietary business logic/content models with no generic value left
once the client-specific parts were removed — e.g. `PathUtils`, `LanguageUtils`, `PageUtils`,
`AssetUtils`, `ReferenceComponentUtils`, the `page/service/PageService(Impl)` pair, and `RamoUtils`
(a pure insurance-line-of-business enum).

### Content Package Structure

The `ui.apps` module contains JCR-based component definitions:

```
/apps/aemcors/
├── components/
│   ├── link/                                 # Link component (HTL + dialog + model)
│   ├── htmlcomponent/                        # HTML embedding component
│   ├── title/                                # Title component (v4 versioned proxy)
│   └── helloworld/                           # Archetype sample component
├── clientlibs/
│   ├── site/                                 # Main frontend assets (CSS, JS)
│   └── dependencies/                         # Third-party libraries
```

### Dispatcher Configuration

The `dispatcher` module includes farm configuration specific to CORS:

```
dispatcher/src/conf.dispatcher.d/
└── available_farms/
    └── aemcors.farm                          # CORS-specific farm with header caching
```

## Key Design Patterns

### 1. OSGi Service Injection

Services (like `EnvironmentInfoService`) are registered as OSGi components with the `@Component` annotation and injected via `@Reference`:

```java
@Component(service = EnvironmentInfoService.class, immediate = true)
public class EnvironmentInfoServiceImpl implements EnvironmentInfoService { ... }

// Usage in servlet
@Component(service = Servlet.class, ...)
public class MyServlet {
    @Reference
    private EnvironmentInfoService environmentInfoService;
}
```

### 2. Sling Models for Components

AEM components use Sling Models to adapt resources to business logic:

```java
@Model(
    adaptables = Resource.class,
    adapters = LinkModel.class,
    resourceType = "aemcors/components/link"
)
public class LinkModel { ... }
```

### 3. Servlet-Based REST Endpoints

Custom endpoints are implemented as Sling servlets registered with specific paths and methods:

```java
@Component(service = Servlet.class, property = {
    SLING_SERVLET_PATHS + "=" + "/bin/aemcors/search/pagereferences",
    SLING_SERVLET_METHODS + "=" + METHOD_POST,
    SLING_SERVLET_EXTENSIONS + "=" + JSON_EXTENSION
})
public class SearchPageReferencesServlet extends AbstractSlingServlet { ... }
```

### 4. Common Response Handling

`AbstractSlingServlet` provides a common pattern for JSON responses:

```java
String json = JsonUtils.createJsonStringFromObject(result);
if (json != null) {
    writeJsonObject(response, trackingId, SC_OK, json, "max-age=3600");
} else {
    writeErrorJsonObject(response, trackingId, SC_BAD_REQUEST);
}
```

### 5. Tracking IDs for Observability

All requests flow through tracking ID extraction, enabling request tracing across logs:

```java
String trackingId = HttpUtils.getTrackingId(request);  // From X-UUID header or session
LoggerUtils.logInfoTrackingId(log, trackingId, "Processing request");
```

### 6. Versioned Component Proxies

Components overlaying Core WCM Components use versioned proxy folders:

```
/apps/aemcors/components/title/
├── .content.xml                              # Empty folder marker
├── _cq_editConfig.xml                        # Edit configuration
└── v4/
    └── title/
        └── .content.xml                      # Extends core v4 title
```

## Dependency Flow

```
SearchPageReferencesServlet
│
├─ extends AbstractSlingServlet
│  ├─ uses HttpUtils.getTrackingId()
│  ├─ uses JsonUtils for JSON serialization
│  └─ uses LoggerUtils for tracking ID logging
│
├─ input: PathsBean (from request JSON)
│
├─ processing: com.day.cq.wcm.commons.ReferenceSearch
│
└─ output: SearchPageReferencesBean
   └─ contains: Map<String, SearchResultPage>


CoreBundleServlet
│
├─ extends AbstractSlingServlet
│
├─ @Reference EnvironmentInfoService
│  └─ injected: EnvironmentInfoServiceImpl
│
└─ returns: RestOperationResult


LinkModel / HtmlComponentModel
│
├─ @Model adapts Resource to component logic
│
└─ uses: CoreConstants for DOT, HTML_EXTENSION
```

## Data Flow: Search Example

1. **Request arrives** at `/bin/aemcors/search/pagereferences.json` (POST)

2. **Servlet processing:**
   - Extract tracking ID: `HttpUtils.getTrackingId(request)`
   - Deserialize request JSON to `PathsBean`: `JsonUtils.getObjectFromRequest(..., PathsBean.class)`
   - Log: `LoggerUtils.logInfoTrackingId(log, trackingId, "Searching paths...")`

3. **Business logic:**
   - Use `com.day.cq.wcm.commons.ReferenceSearch` to find page references
   - Build `SearchResultPage` objects with page path/title/properties
   - Collect into `SearchPageReferencesBean`

4. **Response writing:**
   - Serialize to JSON: `JsonUtils.createJsonStringFromObject(bean)`
   - Write response: `AbstractSlingServlet.writeJsonObject(..., json, "no-cache")`
   - Set status 200 OK, content-type application/json, cache headers

5. **Logging:**
   - On error: `LoggerUtils.logErrorTrackingId(log, trackingId, "Search failed", exception)`
   - All logs include `trID: <tracking-id>` prefix for correlation

## Environment Configuration

`EnvironmentInfoService` reads OSGi configuration to determine the deployment environment:

```
Local Development:
  - hostname: "localhost"
  - environmentAndRunMode: "AUTHOR"
  - organizationId: (empty)

Cloud Manager (Dev environment):
  - hostname: "aem-dev-p12345-e99999.adobeaemcloud.com"
  - environmentAndRunMode: "DEV_AUTHOR" or "DEV_PUBLISH"
  - organizationId: "my-org-1234"

Cloud Manager (Prod environment):
  - hostname: "aem-prod-p12345-e99999.adobeaemcloud.com"
  - environmentAndRunMode: "PROD_AUTHOR" or "PROD_PUBLISH"
  - organizationId: "my-org-1234"
```

Servlets use this information for:
- Environment-specific feature flags
- Conditional logging levels
- Organization tracking
- Health check reporting

## Build & Deployment

### Local Build
```bash
mvn clean install -PautoInstallSinglePackage
```

### Cloud Manager Deployment
1. Code is pushed to git
2. Cloud Manager pipeline builds the project
3. `aemanalyser-maven-plugin` validates AEMaaCS compliance
4. All modules are packaged into a single `all.zip`
5. Deployed to target environment (DEV → STAGE → PROD)

### Dispatcher Configuration
The Dispatcher validates and caches requests:
- CORS headers are cached per request path
- Non-GET requests are not cached
- Static resources are cached aggressively
- `/bin` paths use specific cache rules

## Testing Strategy

- **Unit tests** (core module): JUnit 5 + Mockito + io.wcm AEM Mocks
- **Integration tests** (it.tests): Run against deployed AEM instance
- **UI tests** (ui.tests): Cypress E2E tests in Docker
- **Manual API testing**: `curl` against `/bin/aemcors/*` endpoints

## Key Dependencies

- **AEM SDK**: 2026.7.27083.20260713T105930Z-260600
- **Core WCM Components**: 2.32.4
- **Jackson**: 2.18.2 (JSON serialization)
- **Lombok**: 1.18.46 (annotation processing)
- **Jetbrains Annotations**: 24.0.1 (null-analysis)
- **Apache Commons**: Lang3, IO
- **SLF4J + Logback**: logging

See `pom.xml` for full dependency versions.
