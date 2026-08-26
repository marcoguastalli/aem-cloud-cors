# CORS

How Cross-Origin Resource Sharing is implemented in this project, and everything that differs
in the `dispatcher` module compared to the baseline `aem-cloud-vanilla` project.

## Overview

CORS support here is three separate, loosely-coupled layers:

1. **AEM-side policy** — Adobe's built-in Granite CORS filter, OSGi-configured (no custom Java).
2. **Dispatcher-side header caching** — a project-specific farm file that caches the CORS
   response headers alongside the response body.
3. **Demo content** — an HTML component + sample page used to interactively exercise CORS from
   the browser.

There is **no custom CORS Sling filter** in `core`. Two existing docs (root `README.md`'s "CORS
Configuration" section, and `docs/DISPATCHER_CORS_CONFIG.md`'s "Integration with AEM CORS Filter"
section) both state the headers are set by "a Sling filter in
`core/src/main/java/com/aem/cors/core/filters/`". That's incorrect — that package only contains
`LoggingFilter.java`. The actual mechanism is #1 below, entirely OSGi config, no Java code. This
doc corrects that; the other two should be updated to stop repeating it.

## 1. AEM-side: Granite CORS Policy (author only)

**File:** `ui.config/src/main/content/jcr_root/apps/aemcors/osgiconfig/config.author/com.adobe.granite.cors.impl.CORSPolicyImpl~aemcors.cfg.json`

```json
{
  "alloworigin": ["http://localhost:3000"],
  "allowedpaths": ["/(content|conf)/aemcors.*"],
  "supportedheaders": [
    "Authorization", "Origin", "Accept", "X-Requested-With", "Content-Type",
    "Access-Control-Request-Method", "Access-Control-Request-Headers"
  ],
  "supportedmethods": ["GET", "HEAD"],
  "alloworiginregexp": []
}
```

- Scoped to `config.author` only — **no `config.publish` counterpart exists.** CORS is only
  policy-enforced on the author tier today; publish has no equivalent config.
- `alloworigin` is a single hardcoded local dev origin (`localhost:3000`) — matches a local
  frontend dev server, not a deployed one. There's nothing here for STAGE/PROD frontend origins.
- `allowedpaths` restricts the policy to this project's own content/conf trees
  (`/(content|conf)/aemcors.*`).
- Read-only: `supportedmethods` is `GET, HEAD` only — no POST/PUT allowed cross-origin here (the
  `SearchPageReferencesServlet` POST endpoint used in the README's example curl is same-origin
  only; it isn't covered by this CORS policy).

## 2. Dispatcher: header cache passthrough

**File:** `dispatcher/src/conf.dispatcher.d/available_farms/aemcors.farm`

This is a project-specific copy of the archetype's `default.farm`, with one substantive addition —
six `Access-Control-*` headers added to the cache's `/headers` allow-list (lines 116-121):

```
/headers {
    "Cache-Control"
    "Content-Disposition"
    "Content-Type"
    "Expires"
    "Last-Modified"
    "X-Content-Type-Options"
    "Access-Control-Allow-Origin"
    "Access-Control-Expose-Headers"
    "Access-Control-Max-Age"
    "Access-Control-Allow-Credentials"
    "Access-Control-Allow-Methods"
    "Access-Control-Allow-Headers"
}
```

Without this, the dispatcher would cache a response body but drop the CORS headers on subsequent
cache hits — a browser replaying a cross-origin request against a cached page would get the body
but no `Access-Control-Allow-Origin`, and the request would fail CORS validation client-side even
though the origin is actually allowed.

### Fixed: this file previously wasn't wired in (2026-08-26)

`aemcors.farm` lives in `available_farms/`, but the dispatcher only loads farms symlinked into
`enabled_farms/` (via `enabled_farms/farms.any`'s `$include "./*.farm"`). Until 2026-08-26,
`enabled_farms/` only contained a symlink named `default.farm`, pointing at
`available_farms/default.farm` — the **unmodified, generic archetype farm**, byte-identical to
`aem-cloud-vanilla`'s. There was no symlink to `aemcors.farm` anywhere in `enabled_farms/`, so
**the CORS header-caching config had no effect** — the dispatcher was running the plain vanilla
farm, and `aemcors.farm`'s content was dead configuration. None of the existing docs (`README.md`,
`DISPATCHER_CORS_CONFIG.md`, `ARCHITECTURE_OVERVIEW.md`) had caught this — they all described
`aemcors.farm`'s content as if it were active.

Fixed by retargeting the symlink:

```bash
cd dispatcher/src/conf.dispatcher.d/enabled_farms
rm default.farm
ln -s ../available_farms/aemcors.farm aemcors.farm
```

`enabled_farms/` now contains `aemcors.farm -> ../available_farms/aemcors.farm` instead of
`default.farm -> ../available_farms/default.farm`.

## 3. Demo content

An HTML component + sample "Indonesia" content page exist to interactively exercise CORS from a
browser (a card with a button that POSTs to `restcountries` API). See
[`CONTENT_CORS_COMPONENTS_USED.md`](./CONTENT_CORS_COMPONENTS_USED.md) and
[`COMPONENTS_LINK_HTMLCOMPONENT.md`](./COMPONENTS_LINK_HTMLCOMPONENT.md) for the component
structure, and `content_cors-2026-07-24_11-02.txt`/`.zip` for the exported sample content.

## Dispatcher folder: full diff vs `aem-cloud-vanilla`

Structurally identical to vanilla except for one extra file. Content diffs, file by file:

| File | Changed? | What differs |
|---|---|---|
| `dispatcher/pom.xml` | Yes | Project naming only (`groupId`/`artifactId`/`name`/`description`: `aem-cloud-vanilla*` → `aem-cloud-cors*`). Not CORS-specific. |
| `dispatcher/src/conf.d/variables/custom.vars` | Yes | `CONTENT_FOLDER_NAME`: `aemvanilla` → `aemcors`. Not CORS-specific — standard per-project content-root naming. |
| `dispatcher/src/conf.d/rewrites/rewrite.rules` | Yes | Root rewrite target locale: `/us/en.html` → `/de/de.html`. Not CORS-related; looks like an unrelated content-locale choice made for this project, worth a second look if unintentional. |
| `dispatcher/src/conf.dispatcher.d/available_farms/default.farm` | No | Byte-identical to vanilla's — untouched, and (per the issue above) still the one actually active. |
| `dispatcher/src/conf.dispatcher.d/available_farms/aemcors.farm` | **New file** | The only real CORS-specific dispatcher change — see §2 above. |
| `dispatcher/src/conf.dispatcher.d/enabled_farms/default.farm` → `aemcors.farm` | Yes | The active symlink now points at `../available_farms/aemcors.farm` instead of `../available_farms/default.farm` (fixed 2026-08-26 — see §2). |
| Everything else (`assembly.xml`, `README.md`, `clientheaders/*`, `filters/*`, `virtualhosts/*`, `cache/*.any`, `renders/*`, `update_sdk.sh`, `opt-in/USE_SOURCES_DIRECTLY`) | No | Byte-identical to `aem-cloud-vanilla`. |

## Testing / verification

```bash
# AEM-side policy (author only, GET/HEAD, localhost:3000 origin)
curl -i -H "Origin: http://localhost:3000" \
  http://localhost:4502/content/aemcors.json

# Dispatcher header caching — with the enabled_farms wiring fixed, a repeat request to
# a cached page should now also replay the Access-Control-* headers from the first response
curl -i -H "Origin: http://localhost:3000" \
  http://localhost/content/aemcors.html
```

## References

- [`ARCHITECTURE_OVERVIEW.md`](./ARCHITECTURE_OVERVIEW.md) — full project structure
- [`SERVLETS_AND_SERVICES.md`](./SERVLETS_AND_SERVICES.md) — all servlets/OSGi services, including the endpoints referenced above
- [`DISPATCHER_CORS_CONFIG.md`](./DISPATCHER_CORS_CONFIG.md) — prior, narrower write-up of the header-caching config (contains the "Sling filter" inaccuracy corrected above)
- [`CONTENT_CORS_COMPONENTS_USED.md`](./CONTENT_CORS_COMPONENTS_USED.md) — demo content/components
