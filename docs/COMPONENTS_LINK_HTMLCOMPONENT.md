# Link and HTML Component

Two utility components added to support flexible content authoring in AEM Cloud Cors.

## Link Component

A simple anchor/hyperlink component that renders as an HTML `<a>` tag.

**Location:** `ui.apps/src/main/content/jcr_root/apps/aemcors/components/link/`

**Features:**
- Text label (via `linkText` property)
- URL/path field (via `linkURL` property) with browsable path picker rooted at `/content/aemcors`
- Target attribute toggle (`linkTarget`, `_blank` or `_self`)
- Backed by `com.aem.cors.core.models.components.link.LinkModel` Sling Model

**Dialog Fields:**
- **Link Text**: The visible text/label of the link
- **Link URL**: The target path or URL (uses pathfield for AEM content browsing)
- **Open in new window**: Checkbox to toggle between `_blank` and `_self` target

**HTL Template:** `link.html`  
Renders as: `<a href="${model.linkURL}" ...>${model.linkText}</a>`

---

## HTML Component

A content-editable HTML/code block component for embedding raw HTML or snippets.

**Location:** `ui.apps/src/main/content/jcr_root/apps/aemcors/components/htmlcomponent/`

**Features:**
- Textarea dialog for entering HTML code
- Renders as unsafe HTML in publish mode (via `@ context='unsafe'`)
- Edit mode shows component ID; publish mode displays the HTML
- Backed by `com.aem.cors.core.models.components.htmlcomponent.HtmlComponentModel` Sling Model

**Dialog Fields:**
- **HTML Code**: Textarea (10 rows) for raw HTML/JavaScript markup

**HTL Template:** `htmlcomponent.html`  
Renders as: `<div>${model.htmlCode @ context='unsafe'}</div>` (in publish mode)

---

## Use Cases

- **Link Component**: Simple navigation links, CTAs, internal/external references
- **HTML Component**: Embedding CORS-testing scripts, custom markup snippets, third-party integrations (see `content_cors-2026-07-24_11-02.txt` for example CORS test button markup)

---

## Sling Model Implementation

Both components require corresponding Sling Models in the `core` module:

- `com.aem.cors.core.models.components.link.LinkModel`
- `com.aem.cors.core.models.components.htmlcomponent.HtmlComponentModel`

These are adapted from `aem-vanilla` originals but updated to use `com.aem.cors` package path.
