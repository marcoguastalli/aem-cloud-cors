# Dispatcher CORS Configuration

The Dispatcher configuration for AEM Cloud Cors includes CORS-specific HTTP response header caching to ensure cross-origin requests are properly handled and cached.

## File Location

**Farm:** `dispatcher/src/conf.dispatcher.d/available_farms/aemcors.farm`

## CORS Headers Caching

The critical addition to standard dispatcher caching is the `/headers` section within the cache configuration block (lines 109-122 of `aemcors.farm`):

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

## Purpose

This configuration ensures that **CORS response headers are cached alongside the response body**, so:

1. **CORS headers persist across cache hits** — when a cached response is served, the stored CORS headers are re-sent
2. **Preflight responses are properly cached** — OPTIONS requests that set `Access-Control-*` headers are cached correctly
3. **Cross-origin consumers get consistent headers** — every response from the same cached resource includes the same CORS headers

## Key CORS Headers

| Header | Purpose |
|--------|---------|
| `Access-Control-Allow-Origin` | Specifies which origins can access the resource |
| `Access-Control-Allow-Methods` | Lists HTTP methods allowed (GET, POST, etc.) |
| `Access-Control-Allow-Headers` | Lists headers the browser is allowed to send |
| `Access-Control-Expose-Headers` | Lists headers the browser is allowed to read from the response |
| `Access-Control-Max-Age` | Caches preflight response validity (in seconds) |
| `Access-Control-Allow-Credentials` | Indicates if credentials (cookies, auth) are allowed |

## Integration with AEM CORS Filter

The Sling filter in `core/src/main/java/com/aem/cors/core/filters/` sets these headers on responses. The Dispatcher configuration ensures they are preserved through the caching layer.

## Testing

To verify CORS headers are cached correctly:

```bash
# Make a CORS request to the endpoint
curl -i -H "Origin: http://example.com" \
  http://localhost/bin/aemcors/search/pagereferences.json

# Response should include Access-Control-Allow-Origin and other CORS headers
# On repeat requests to cached resources, headers should persist
```

---

**Reference:** [MDN CORS HTTP Response Headers](https://developer.mozilla.org/en-US/docs/Web/HTTP/CORS#the_http_response_headers)
