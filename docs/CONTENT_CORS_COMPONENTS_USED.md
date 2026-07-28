# Content Cors Example Components

This document references the components used in the exported content snapshot `content_cors-2026-07-24_11-02.zip`.

## Components

The example CORS content uses:

### HTML Component

- **Component Path:** `aemcors/components/htmlcomponent`
- **Purpose:** Embedding CORS-testing scripts and interactive markup
- **Example in content:** The POST button markup shown in `content_cors-2026-07-24_11-02.txt` (Indonesia country card + POST restcountries button) is a candidate for this component
- **Usage:** Allows content authors to embed custom HTML/JavaScript for CORS demonstrations and third-party API integrations

### Link Component

- **Component Path:** `aemcors/components/link`
- **Purpose:** Simple hyperlink/navigation elements
- **Usage:** Referenced in navigation, CTA buttons, or documentation links within the CORS example content

---

## Reference Content

See `content_cors-2026-07-24_11-02.txt` for the HTML example (Indonesia flag, POST button to restcountries API). This snippet represents what might be authored using the **HTML Component** to create interactive CORS-testing blocks.

For detailed component structure and Sling Model backing, see [COMPONENTS_LINK_HTMLCOMPONENT.md](./COMPONENTS_LINK_HTMLCOMPONENT.md).
