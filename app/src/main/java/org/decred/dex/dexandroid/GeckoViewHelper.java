package org.decred.dex.dexandroid;

import android.app.Activity;
import android.util.Log;

import org.mozilla.geckoview.BuildConfig;
import org.mozilla.geckoview.GeckoRuntime;
import org.mozilla.geckoview.GeckoRuntimeSettings;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class GeckoViewHelper {

    private static GeckoRuntime sRuntime;
    private static final String TAG = "DCRDEX";

    public GeckoViewHelper() {
    }

    private void writeGeckoRuntimeConfig(File filesDir) {
        File filePath = getGeckoConfigFile(filesDir);
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(filePath);
            String gvRuntimeConfigTemplate = """
prefs:
  network.proxy.socks: "127.0.0.1"
  network.proxy.socks_port: 9050
  network.proxy.socks_remote_dns: true
  network.proxy.socks_version: 5
  network.proxy.type: 1
  network.dns.blockDotOnion: false
                """;
            outputStream.write(gvRuntimeConfigTemplate.getBytes());
        } catch (Exception e) {
            Log.e(TAG, "Unable to create GeckoRuntime config file: " + e);
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    Log.e(TAG, "Error closing GeckoRuntime config file: " + e);
                }
            }
        }
    }

    private File getGeckoConfigFile(File filesDir) {
        return new File(filesDir, "gecko-config.yaml");
    }

    public GeckoRuntime getGeckoRuntime(Activity activity) {
        // GeckoRuntime can only be initialized once per process
        if (sRuntime != null) {
            sRuntime.attachTo(activity);
            return sRuntime;
        }
        File filesDir = activity.getFilesDir();
        // TODO add error handling.  writeGeckoRuntimeConfig() must throw an exception in case of error
        this.writeGeckoRuntimeConfig(filesDir);
        GeckoRuntimeSettings.Builder sb = this.createSettingsBuilder(filesDir);
        GeckoRuntimeSettings settings = sb.build();
        sRuntime = GeckoRuntime.create(activity, settings);
        return sRuntime;
    }

    private GeckoRuntimeSettings.Builder createSettingsBuilder(File filesDir) {
        GeckoRuntimeSettings.Builder sb = new GeckoRuntimeSettings.Builder();
        if (BuildConfig.DEBUG) {
            sb.debugLogging(true);
            sb.remoteDebuggingEnabled(true);
            sb.consoleOutput(true);
            sb.aboutConfigEnabled(true);
        }
        String gvConfigPath = this.getGeckoConfigFile(filesDir).getAbsolutePath();
        Log.i(TAG, "loading geckoView config from " + gvConfigPath);
        sb.configFilePath(gvConfigPath);
        sb.javaScriptEnabled(true);
        return sb;
    }
}
