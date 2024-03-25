package org.decred.dex.dexandroid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class CacheTest {
    private Cache cache;

    @Before
    public void setUp() {

        Path tempDirectory = null;
        try {
            tempDirectory = Files.createTempDirectory("CacheTest");
            System.out.println("Temporary directory path: " + tempDirectory.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        cache = new Cache(tempDirectory);
    }

    @Test
    public void testContains() {
        assertFalse(cache.contains("http://example.com/notcontains"));
    }

    @Test
    public void testPut() {
        String data = "This is a test";
        String url = "http://example.com/testput";
        assertFalse(cache.contains(url));
        cache.put(url, data.getBytes());
        assertTrue(cache.contains(url));
        assertNotNull(cache.get(url));
        byte[] dataRead = cache.get(url);
        assertEquals(data, new String(dataRead));
    }

    @Test
    public void testPutAssetVersion() {
        String data = "This is a test";
        String url = "http://example.com/testput-version";
        String versionSuffix = "?v=abcde";
        assertFalse(cache.contains(url + versionSuffix));
        cache.put(url + versionSuffix, data.getBytes());
        assertTrue(cache.contains(url + versionSuffix));
        assertFalse(cache.contains(url));
        assertFalse(cache.contains(url + "?v=anotherversion&anothervar=blah"));
        assertNotNull(cache.get(url + versionSuffix));
        byte[] dataRead = cache.get(url + versionSuffix);
        assertEquals(data, new String(dataRead));
    }

}