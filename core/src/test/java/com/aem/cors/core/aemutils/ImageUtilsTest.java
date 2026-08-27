package com.aem.cors.core.aemutils;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.Rendition;
import org.apache.sling.commons.mime.MimeTypeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static com.aem.cors.core.aemutils.ImageUtils.getImageType;
import static com.aem.cors.core.aemutils.ImageUtils.getSvgAsString;
import static com.aem.cors.core.aemutils.ImageUtils.isPathSvg;
import static com.aem.cors.core.aemutils.ImageUtils.isSvg;
import static com.aem.cors.core.aemutils.ImageUtils.isValidaImageExtension;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.aem.cors.core.exceptions.AemRuntimeException;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ImageUtilsTest {

    @Mock
    Asset asset;
    @Mock
    Rendition rendition;
    @Mock
    MimeTypeService mimeTypeService;

    @Test
    void testIsValidaImageExtensionTrue() {
        assertThat(isValidaImageExtension("/content/dam/image.png"), is(true));
    }

    @Test
    void testIsValidaImageExtensionFalse() {
        assertThat(isValidaImageExtension("/content/dam/document.pdf"), is(false));
    }

    @Test
    void testIsPathSvgTrue() {
        assertThat(isPathSvg("/content/dam/icon.SVG"), is(true));
    }

    @Test
    void testIsPathSvgFalse() {
        assertThat(isPathSvg("/content/dam/icon.png"), is(false));
    }

    @Test
    void testIsSvgTrue() {
        assertThat(isSvg("svg"), is(true));
    }

    @Test
    void testIsSvgFalse() {
        assertThat(isSvg("png"), is(false));
    }

    @Test
    void testGetImageTypeNullExtension() {
        assertThat(getImageType(null, mimeTypeService), is("application/octet-stream"));
    }

    @Test
    void testGetImageTypeTiff() {
        assertThat(getImageType("tiff", mimeTypeService), is("application/octet-stream"));
        assertThat(getImageType("tif", mimeTypeService), is("application/octet-stream"));
    }

    @Test
    void testGetImageTypeDelegatesToMimeTypeService() {
        when(mimeTypeService.getMimeType("png")).thenReturn("image/png");
        assertThat(getImageType("png", mimeTypeService), is("image/png"));
    }

    @Test
    void testGetImageTypeNullMimeService() {
        assertThat(getImageType("png", null), is("application/octet-stream"));
    }

    @Test
    void testGetSvgAsString() throws IOException {
        String svgContent = "<svg></svg>";
        when(asset.getOriginal()).thenReturn(rendition);
        when(rendition.getStream()).thenReturn(new ByteArrayInputStream(svgContent.getBytes(StandardCharsets.UTF_8)));

        assertThat(getSvgAsString(asset), is(svgContent));
    }

    @Test
    void testGetSvgAsStringThrowsOnIOException() throws IOException {
        when(asset.getOriginal()).thenReturn(rendition);
        InputStream brokenStream = mock_throwing_input_stream();
        when(rendition.getStream()).thenReturn(brokenStream);
        when(asset.getPath()).thenReturn("/content/dam/broken.svg");

        assertThrows(AemRuntimeException.class, () -> getSvgAsString(asset));
    }

    private InputStream mock_throwing_input_stream() throws IOException {
        InputStream inputStream = org.mockito.Mockito.mock(InputStream.class);
        when(inputStream.readAllBytes()).thenThrow(new IOException("broken"));
        return inputStream;
    }
}
