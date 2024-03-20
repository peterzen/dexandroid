package org.decred.dex.dexandroid;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.UUID;

public class DexClient implements Serializable {

    private final String id;
    private final String url;

    public DexClient(String id, String URL) {
        this.id = id;
        this.url = URL;
    }

    public String getUrl() {
        return url;
    }

    public String getId(){
        return this.id;
    }

    public static DexClient newDexClientFromURL(String URL){
        String id = UUID.randomUUID().toString();
        return new DexClient(id, URL);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        DexClient dexClient = (DexClient) obj;
        return url.equals(dexClient.url);
    }

}
