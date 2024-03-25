package org.decred.dex.dexandroid;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import org.mozilla.geckoview.AllowOrDeny;
import org.mozilla.geckoview.GeckoResult;
import org.mozilla.geckoview.GeckoSession;
import org.mozilla.geckoview.WebRequest;

import java.util.List;

public class CachingNavigationDelegate implements GeckoSession.NavigationDelegate {
    private Cache cache; // Your cache implementation

    private String TAG = "DCRDEX";

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    public GeckoResult<AllowOrDeny> onLoadRequest(GeckoSession session, LoadRequest request) {
        String url = request.uri;

        // Check if the URL is in the cache
        if (cache.contains(url)) {
            // If the cached resource has not expired, serve it
//                String fileUri = cache.getLocalURL(url);
//                session.load(new GeckoSession.Loader().uri(fileUri));
//                return null;
        }

        // If the URL is not in the cache or the cached resource has expired, make the network request
        session.loadUri(url);
        return null;
    }


//    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
//    @Override
//    public GeckoResult<WebRequest> interceptRequest(@NonNull WebRequest request) {
//        // When you receive the response, save it to your cache
//        byte[] data = request.body.array();
//        cache.put(request.uri, data);
//        return GeckoResult.fromValue(request);
//    }

    // Other methods...
}