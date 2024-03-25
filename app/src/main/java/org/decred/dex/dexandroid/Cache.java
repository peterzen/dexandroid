package org.decred.dex.dexandroid;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Cache {

    private final String TAG = "DCRDEX";
    private final Path cacheRootDir;

    public Cache(Path cacheRootDir) {
        this.cacheRootDir = cacheRootDir;
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public boolean contains(String url) {
        Path path = Paths.get(getCachedFilePath(url));
        return Files.exists(path);
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public byte[] get(String url) {
        Path path = Paths.get(getCachedFilePath(url));
        byte[] fileBytes;

        try {
            fileBytes = Files.readAllBytes(path);
        } catch (IOException e) {
            Log.e(TAG, "An error occurred while reading the file: " + e);
            return null;
        }
        return fileBytes;
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public void put(String url, byte[] data) {
        String filePath = getCachedFilePath(url);
        Path path = Paths.get(filePath);

        Path parentDir = path.getParent();

        if (!Files.exists(parentDir)) {
            try {
                Files.createDirectories(Paths.get(parentDir.toString()));
            } catch (IOException e) {
                Log.e(TAG, "Unable to create cache directory: " + e);
                return;
            }
        }

        try {
            Files.write(path, data);
        } catch (IOException e) {
            Log.e(TAG, "Unable to write to cache: " + e);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public String getCachedFilePath(String url) {
        String f = this.cacheRootDir + "/" + transformUrlToFile(url);
        System.err.println(f);
        return f;
    }

    private String transformUrlToFile(String url) {
        String tr = url.replaceAll("(https?)://(.*)$", "$1--$2");
        System.err.println(tr);
        return tr;
    }

}
