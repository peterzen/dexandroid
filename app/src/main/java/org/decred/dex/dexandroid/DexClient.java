package org.decred.dex.dexandroid;

import java.io.Serializable;
import java.util.UUID;

// DexClient is a model representing a DEX host the application can connect to.
public class DexClient implements Serializable {

    private final String id;
    private final String url;

    public DexClient(String id, String url) {
        this.id = id;
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public String getId(){
        return this.id;
    }

    public static DexClient newDexClientFromURL(String url){
        String id = UUID.randomUUID().toString();
        return new DexClient(id, url);
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
