# Servlets and Services

This document describes all servlets and OSGi services in aem-cloud-cors.

## Servlets

### CoreBundleServlet (`/bin/aemcors/bundle-core`)

**Location:** `core/src/main/java/com/aem/cors/core/servlets/CoreBundleServlet.java`

**Purpose:** Health check servlet that reports the core bundle status and organization ID.

**Endpoint:** `/bin/aemcors/bundle-core.json` (GET or POST)

**Response:**
```json
{
  "result": "The bundle 'aem-cors.core' is up and active in 'DEV_AUTHOR' with ORGANIZATION_ID: 'my-org-id'"
}
```

**Dependencies:**
- `EnvironmentInfoService` — OSGi service injected to retrieve environment metadata
- `AbstractSlingServlet` — base class providing JSON response writing and error handling

**Use Cases:**
- Monitoring bundle health
- Verifying organization configuration
- Testing environment detection

### SearchPageReferencesServlet (`/bin/aemcors/search/pagereferences`)

**Location:** `core/src/main/java/com/aem/cors/core/servlets/search/SearchPageReferencesServlet.java`

**Purpose:** Searches for pages that reference given paths (e.g., pages using a component or asset).

**Endpoint:** `/bin/aemcors/search/pagereferences.json` (POST)

**Request Body:**
```json
{
  "paths": ["/content/core-components-examples", "/content/my-site"]
}
```

**Response:**
```json
{
  "references": {
    "page-key-1": {
      "pagePath": "/content/site/en/products",
      "pageTitle": "Products",
      "properties": ["cq:template", "jcr:created"]
    }
  }
}
```

**Dependencies:**
- `PathsBean` — input DTO parsed from request JSON
- `SearchResultPage` — output DTO for each reference
- `SearchPageReferencesBean` — wrapper for the references map
- `AbstractSlingServlet` — base class for JSON handling
- `HttpUtils.getTrackingId()` — extract request tracking ID from header or session
- `JsonUtils` — JSON serialization/deserialization
- `com.day.cq.wcm.commons.ReferenceSearch` — AEM core API for reference searching

**Use Cases:**
- Finding all pages using a specific component
- Impact analysis before asset/content deletion
- Page audit and reporting

## OSGi Services

### EnvironmentInfoService (Interface)

**Location:** `core/src/main/java/com/aem/cors/core/services/EnvironmentInfoService.java`

**Purpose:** Interface for querying environment metadata (host, environment type, organization ID).

**Methods:**
```java
String getHost();                      // Protocol + hostname (e.g., "https://author.example.com")
String getHostname();                  // Server hostname from HOSTNAME env var
String getEnvironmentShortName();      // ENVIRONMENT_SHORT_NAME env var
String getEnvironmentAndRunMode();     // "DEV_AUTHOR", "PROD_PUBLISH", etc.
String getOrganizationId();            // ORGANIZATION_ID env var
boolean isAuthor();                    // true if current instance is Author
boolean isPublish();                   // true if current instance is Publish
boolean isProdPublish();               // true if PROD_PUBLISH
boolean isProd();                      // true if any PROD_* mode
boolean isStage();                     // true if any STAGE_* mode
boolean isDev();                       // true if any DEV_* mode
boolean isRde();                       // true if any RDE_* mode
String getEnvironmentString();         // "prod", "stage", "dev", "rde", or "localhost"
```

### EnvironmentInfoServiceImpl (Implementation)

**Location:** `core/src/main/java/com/aem/cors/core/services/EnvironmentInfoServiceImpl.java`

**Purpose:** Implements EnvironmentInfoService using OSGi configuration.

**Configuration (OSGi Web Console):**
- Service name: "AEM CORS :: Environment Info Service"
- Configurable fields:
  - **Host** — full URL with protocol (e.g., `https://author-p12345-e99999.adobeaemcloud.com`)
  - **Hostname** — server hostname (from `HOSTNAME` env var on Cloud Manager)
  - **Environment Short Name** — environment identifier (from `ENVIRONMENT_SHORT_NAME` env var)
  - **Environment and Run Mode** — one of: AUTHOR, PUBLISH, PROD_AUTHOR, PROD_PUBLISH, STAGE_AUTHOR, STAGE_PUBLISH, DEV_AUTHOR, DEV_PUBLISH, RDE_AUTHOR, RDE_PUBLISH
  - **Organization Id** — the Cloud Manager organization ID
  - **Organization Secret** — reserved for future use

**Activation:**
- Marked as `immediate = true`, so it activates as soon as the bundle starts
- Logs all configuration values at INFO level upon activation

**Usage Example:**
```java
@Reference
private EnvironmentInfoService environmentInfoService;

public void myMethod() {
    if (environmentInfoService.isProd()) {
        // Production-specific logic
    }
    String env = environmentInfoService.getEnvironmentString(); // "prod", "stage", "dev", etc.
    String orgId = environmentInfoService.getOrganizationId();   // e.g., "my-org-1234"
}
```

## Support Classes

### Exception Handling

#### AemRuntimeException

**Location:** `core/src/main/java/com/aem/cors/core/exceptions/AemRuntimeException.java`

**Purpose:** Generic unchecked exception for AEM runtime errors.

**Constructors:**
```java
AemRuntimeException(String message)
AemRuntimeException(String message, Throwable cause)
```

**Usage:**
```java
if (jsonAsString == null) {
    throw new AemRuntimeException("Failed to serialize response to JSON");
}
```

### Data Transfer Objects (Beans)

#### PathsBean

**Location:** `core/src/main/java/com/aem/cors/core/commonbeans/PathsBean.java`

**Purpose:** Input DTO for endpoints accepting a list of paths.

**Fields:**
- `List<String> paths` — JSON-mapped paths to process

**Example JSON:**
```json
{"paths": ["/content/site", "/content/dam/assets"]}
```

#### RestOperationResult

**Location:** `core/src/main/java/com/aem/cors/core/commonbeans/RestOperationResult.java`

**Purpose:** Generic wrapper for REST operation responses.

**Fields:**
- `Object result` — transient response payload (can be any serializable object)

**Example JSON:**
```json
{"result": "The bundle is active"}
```

#### SearchResultPage

**Location:** `core/src/main/java/com/aem/cors/core/commonbeans/search/SearchResultPage.java`

**Purpose:** Represents a single page reference in search results.

**Fields:**
- `String pagePath` — JCR path to the page
- `String pageTitle` — page title/display name
- `Set<String> properties` — set of properties referring to the search target

#### SearchPageReferencesBean

**Location:** `core/src/main/java/com/aem/cors/core/commonbeans/search/SearchPageReferencesBean.java`

**Purpose:** Wrapper for all page references returned by SearchPageReferencesServlet.

**Fields:**
- `Map<String, SearchResultPage> references` — keyed by some identifier, value is SearchResultPage

#### EnvironmentType (Enum)

**Location:** `core/src/main/java/com/aem/cors/core/commonbeans/EnvironmentType.java`

**Purpose:** Enumeration of all possible AEM deployment environment types.

**Values:**
- `AUTHOR`, `PUBLISH` — instance types
- `PROD_AUTHOR`, `PROD_PUBLISH` — production
- `STAGE_AUTHOR`, `STAGE_PUBLISH` — staging
- `DEV_AUTHOR`, `DEV_PUBLISH` — development
- `RDE_AUTHOR`, `RDE_PUBLISH` — rapid development environment
- `DISPATCHER` — reserved

### Utilities

#### LoggerUtils

**Location:** `core/src/main/java/com/aem/cors/core/utils/LoggerUtils.java`

**Purpose:** SLF4J logging utilities with tracking ID support.

**Static Methods:**
```java
logDebugTrackingId(Logger log, String trackingId, String message)
logInfoTrackingId(Logger log, String trackingId, String message)
logWarnTrackingId(Logger log, String trackingId, String message)
logErrorTrackingId(Logger log, String trackingId, String message)
logErrorTrackingId(Logger log, String trackingId, String message, Exception e)
logHttpRequestParameters(Logger log, String trackingId, SlingHttpServletRequest request)
```

**Example:**
```java
@Slf4j
public class MyServlet extends AbstractSlingServlet {
    protected void doPost(SlingHttpServletRequest req, SlingHttpServletResponse res) {
        String trackingId = HttpUtils.getTrackingId(req);
        LoggerUtils.logInfoTrackingId(log, trackingId, "Processing POST request");
        // ... process request
        LoggerUtils.logErrorTrackingId(log, trackingId, "Request failed", exception);
    }
}
```

#### HttpUtils

**Location:** `core/src/main/java/com/aem/cors/core/utils/HttpUtils.java`

**Purpose:** HTTP request utilities for tracking and parameter validation.

**Static Methods:**
```java
String getTrackingId(HttpServletRequest request)
  // Returns HTTP_HEADER_UUID header if present, otherwise session ID, otherwise empty string

List<String> verifyMandatoryRequestParameter(SlingHttpServletRequest request, String... paramNames)
  // Returns list of error messages for missing/blank parameters
```

**Example:**
```java
String trackingId = HttpUtils.getTrackingId(request);  // for logging

List<String> errors = HttpUtils.verifyMandatoryRequestParameter(request, "name", "email");
if (!errors.isEmpty()) {
    // return 400 Bad Request with error messages
}
```

#### JsonUtils

**Location:** `core/src/main/java/com/aem/cors/core/utils/JsonUtils.java`

**Purpose:** JSON serialization/deserialization using Jackson ObjectMapper.

**Static Methods:**
```java
String createJsonStringFromObject(Serializable object)
  // Serializes object to JSON string (null if error)

<T> T createObjectFromJsonString(String json, Class<T> clazz)
  // Deserializes JSON string to object (null if error)

ObjectNode createJsonObjectNodeFromEntries(Set<Map.Entry<String, String>> entries)
  // Creates a JSON object from Map entries

ArrayNode createJsonArrayNodeFromObjects(List<Serializable> objects)
  // Creates a JSON array from objects

<T> T getObjectFromRequest(SlingHttpServletRequest req, String trackingId, Class<T> clazz)
  // Reads request body as JSON and deserializes to class (null if error)
```

**Example:**
```java
SearchPageReferencesBean result = new SearchPageReferencesBean(references);
String json = JsonUtils.createJsonStringFromObject(result);
if (json != null) {
    response.getWriter().print(json);
}

PathsBean input = JsonUtils.getObjectFromRequest(request, trackingId, PathsBean.class);
if (input != null) {
    // Process paths
}
```

### Base Servlet Class

#### AbstractSlingServlet

**Location:** `core/src/main/java/com/aem/cors/core/servlets/AbstractSlingServlet.java`

**Purpose:** Base class for Sling servlets with JSON response writing and error handling.

**Protected Methods:**
```java
void writeJsonObject(SlingHttpServletResponse response, String trackingId, int statusCode, String jsonAsString, String cacheHeaderValue)
  // Writes JSON response with status, content-type, and cache headers

void writeErrorJsonObject(SlingHttpServletResponse response, String trackingId, int statusCode)
  // Writes default error JSON with no-cache directive

static String getIdFromSession(HttpServletRequest request)
  // Extracts session ID or empty string
```

**Example Extension:**
```java
@Component(service = Servlet.class, property = {...})
public class MyServlet extends AbstractSlingServlet {
    protected void doPost(SlingHttpServletRequest req, SlingHttpServletResponse res) throws IOException {
        String trackingId = HttpUtils.getTrackingId(req);
        String json = JsonUtils.createJsonStringFromObject(result);
        if (json != null) {
            writeJsonObject(res, trackingId, HttpServletResponse.SC_OK, json, "max-age=3600");
        } else {
            writeErrorJsonObject(res, trackingId, HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
```

## Component Interactions

```
CoreBundleServlet
├─ Extends AbstractSlingServlet
├─ @Reference EnvironmentInfoService
└─ Returns RestOperationResult

SearchPageReferencesServlet
├─ Extends AbstractSlingServlet
├─ Input: PathsBean
├─ Processing: com.day.cq.wcm.commons.ReferenceSearch
├─ Output: SearchPageReferencesBean → SearchResultPage[]
└─ Dependencies: HttpUtils, JsonUtils, LoggerUtils

EnvironmentInfoServiceImpl
├─ Implements EnvironmentInfoService
└─ Configuration: uses EnvironmentType enum internally

All Servlets
└─ Use AbstractSlingServlet, HttpUtils, JsonUtils, LoggerUtils for common patterns
```
