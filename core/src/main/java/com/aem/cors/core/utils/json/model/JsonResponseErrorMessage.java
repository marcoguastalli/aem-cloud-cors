package com.aem.cors.core.utils.json.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Representation of a JSON error result object
 */
public class JsonResponseErrorMessage implements Serializable {

    private static final long serialVersionUID = 57645175697001382L;
    private boolean translated;
    private boolean fullKey;
    private String title;
    private String message;

    public JsonResponseErrorMessage(final boolean translated, final boolean fullKey, final String title, final String message) {
        this.translated = translated;
        this.fullKey = fullKey;
        this.title = title;
        this.message = message;
    }

    public boolean isTranslated() {
        return translated;
    }

    public void setTranslated(boolean translated) {
        this.translated = translated;
    }

    public boolean isFullKey() {
        return fullKey;
    }

    public void setFullKey(boolean fullKey) {
        this.fullKey = fullKey;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof JsonResponseErrorMessage)) {
            return false;
        }
        JsonResponseErrorMessage that = (JsonResponseErrorMessage) o;
        return translated == that.translated &&
            fullKey == that.fullKey &&
            Objects.equals(title, that.title) &&
            Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(translated, fullKey, title, message);
    }

    @Override
    public String toString() {
        return "{translated=" + translated +
            ", fullKey=" + fullKey +
            ", title='" + title + '\'' +
            ", message='" + message + '\'' +
            '}';
    }
}
