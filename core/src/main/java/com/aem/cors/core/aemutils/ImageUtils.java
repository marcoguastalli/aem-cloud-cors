package com.aem.cors.core.aemutils;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.substringAfterLast;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.sling.commons.mime.MimeTypeService;
import org.jetbrains.annotations.NotNull;

import com.aem.cors.core.exceptions.AemRuntimeException;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.Rendition;

/** Util class for images */
public class ImageUtils {

    private static final String DEFAULT_MIME = "application/octet-stream";
    private static final String DOT_SEPARATOR = ".";
    private static final String GIF_EXTENSION = "gif";
    private static final String GIF_MIME_TYPE = "image/gif";
    private static final String JPEG_EXTENSION = "jpeg";
    private static final String JPG_EXTENSION = "jpg";
    private static final String JPG_MIME_TYPE = "image/jpeg";
    private static final String PNG_EXTENSION = "png";
    private static final String PNG_MIME_TYPE = "image/png";
    private static final String SVG_EXTENSION = "svg";
    private static final String SVG_MIME_TYPE = "image/svg+xml";
    private static final String TIFF_TAG = "tiff";
    private static final String TIF_TAG = "tif";

    final static Set<String> ALLOWED_IMAGES_EXTENSIONS = Collections.unmodifiableSet(new HashSet<>(4) {
        {
            add(JPG_EXTENSION);
            add(JPEG_EXTENSION);
            add(PNG_EXTENSION);
            add(GIF_EXTENSION);
            add(SVG_EXTENSION);
        }
    });
    final public static Set<String> ALLOWED_IMAGES_MIME_TYPES = Collections.unmodifiableSet(new HashSet<>(4) {
        {
            add(JPG_MIME_TYPE);
            add(DEFAULT_MIME);
            add(PNG_MIME_TYPE);
            add(GIF_MIME_TYPE);
            add(SVG_MIME_TYPE);
        }
    });

    private ImageUtils() {
        throw new UnsupportedOperationException("Do not instantiate Util class");
    }

    public static boolean isValidaImageExtension(final String path) {
        final String extension = substringAfterLast(path, DOT_SEPARATOR);
        return ALLOWED_IMAGES_EXTENSIONS.contains(extension);
    }

    public static boolean isPathSvg(final String path) {
        return equalsIgnoreCase(SVG_EXTENSION, substringAfterLast(path, DOT_SEPARATOR));
    }

    public static boolean isSvg(final String extension) {
        return equalsIgnoreCase(SVG_EXTENSION, extension);
    }

    /** As the SVG is an XML this method returns the SVG/XML as String
     *
     * @param asset an Asset
     * @return a String */
    public static String getSvgAsString(@NotNull final Asset asset) {
        try {
            final Rendition originalRendition = asset.getOriginal();
            final InputStream inputStream = originalRendition.getStream();
            return new String(inputStream.readAllBytes(), UTF_8);
        } catch (IOException e) {
            throw new AemRuntimeException(String.format("Error read SVG at path '%s'", asset.getPath()), e);
        }
    }

    public static String getImageType(String ext, MimeTypeService mimeTypeService) {
        if (ext == null) {
            return DEFAULT_MIME;
        }
        if (TIFF_TAG.equalsIgnoreCase(ext) || TIF_TAG.equalsIgnoreCase(ext)) {
            return DEFAULT_MIME;
        }
        return mimeTypeService != null ? mimeTypeService.getMimeType(ext) : DEFAULT_MIME;
    }

}
