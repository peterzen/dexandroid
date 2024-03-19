package org.decred.dex.dexandroid;

import java.io.Serializable;

public class DexClient implements Serializable {

    private String clientName;
    private String clientUrl;
    private String authCookie;

    public DexClient(String clientName, String clientUrl, String authCookie) {
        this.clientName = clientName;
        this.clientUrl = clientUrl;
        this.authCookie = authCookie;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientUrl() {
        return clientUrl;
    }

    public void setClientUrl(String clientUrl) {
        this.clientUrl = clientUrl;
    }

    public String getAuthCookie() {
        return authCookie;
    }

    public void setAuthCookie(String authCookie) {
        this.authCookie = authCookie;
    }
}
