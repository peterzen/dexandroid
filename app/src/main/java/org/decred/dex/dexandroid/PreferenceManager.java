package org.decred.dex.dexandroid;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PreferenceManager {
    private static final String PREFERENCES_NAME = "settings";
    private static final String DEX_CLIENT_LIST_KEY = "dex_client_list";

    private SharedPreferences sharedPreferences;
    private Gson gson;

    public PreferenceManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public void saveDexClientList(List<DexClient> dexClientList) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String json = gson.toJson(dexClientList);
        editor.putString(DEX_CLIENT_LIST_KEY, json);
        editor.apply();
    }

    public void addDexClient(DexClient client){
        List<DexClient> clientList = getDexClientList();
        clientList.add(client);
        saveDexClientList(clientList);
    }

    public List<DexClient> getDexClientList() {
        String json = sharedPreferences.getString(DEX_CLIENT_LIST_KEY, null);
        Type type = new TypeToken<ArrayList<DexClient>>() {}.getType();
        return gson.fromJson(json, type);
    }
}