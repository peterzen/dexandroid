
package org.decred.dex.dexandroid;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.mozilla.geckoview.GeckoResult;
import org.mozilla.geckoview.GeckoRuntime;
import org.mozilla.geckoview.GeckoSession;
import org.mozilla.geckoview.GeckoView;
import org.mozilla.geckoview.WebRequestError;
import org.torproject.jni.TorService;


public class DexClientViewActivity extends Activity {

    private ServiceConnection conn;

    private BroadcastReceiver broadcastReceiver;

    private SwipeRefreshLayout swipeRefreshLayout;


    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dexclientview);

        GeckoView geckoView = findViewById(R.id.geckoview);
        geckoView.coverUntilFirstPaint(R.color.backgroundBlue);

        ProgressBar progressBar = findViewById(R.id.progressBar);

        GeckoSession session = new GeckoSession();

        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            session.reload();
            swipeRefreshLayout.setRefreshing(false);
        });

        // Workaround for Bug 1758212
        session.setContentDelegate(new GeckoSession.ContentDelegate() {
        });

        DexCompanionApp application = (DexCompanionApp) getApplicationContext();
        GeckoViewHelper gvHelper = application.getGeckoViewHelper();
        GeckoRuntime sRuntime = gvHelper.getGeckoRuntime(this);

        session.open(sRuntime);

        session.setNavigationDelegate(new GeckoSession.NavigationDelegate() {
            @Override
            public GeckoResult<String> onLoadError(@NonNull GeckoSession session, String uri, @NonNull WebRequestError error) {
                // Error occurred while trying to load a URI
                unbindService(conn);
                unregisterReceiver(broadcastReceiver);
                errorActivity("Unable to connect to DEX client");
                return null;
            }
        });

        DexClient dexHost = getIntent().getSerializableExtra("dexHost", DexClient.class);
        if (dexHost == null) {
            Log.e(DexCompanionApp.LOG_TAG, "Invalid DEX host: (null)");
            finish();
            return;
        }
        geckoView.setSession(session);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String status = intent.getStringExtra(TorService.EXTRA_STATUS);

                progressBar.incrementProgressBy(10);

                // onReceive fires twice.  Tor is ready when status is ON
                if (status == null || !status.equals(TorService.STATUS_ON)) {
                    return;
                }

                Toast.makeText(context, "Connected to Tor network", Toast.LENGTH_SHORT).show();
                gvHelper.setProgressBar(session, progressBar);
                session.loadUri(dexHost.url());
            }
        };

        registerReceiver(broadcastReceiver, new IntentFilter(TorService.ACTION_STATUS), Context.RECEIVER_NOT_EXPORTED);

        conn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                TorService torService = ((TorService.LocalBinder) service).getService();
                while (torService.getTorControlConnection() == null) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        Log.e(DexCompanionApp.LOG_TAG, "InterruptedException: " + e);
                        errorActivity("Tor control connection failed");
                    }
                }
                progressBar.incrementProgressBy(10);
                Toast.makeText(DexClientViewActivity.this, "Got Tor control connection", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.i(DexCompanionApp.LOG_TAG, "onServiceDisconnected: " + name.toString());
                errorActivity("Tor service disconnected");
            }
        };

        bindService(new Intent(this, TorService.class), conn, Context.BIND_AUTO_CREATE);
    }

    // errorActivity navigates back to MainActivity and sends a displayable error message
    private void errorActivity(String errorStr) {
        Intent intent = new Intent(DexClientViewActivity.this, MainActivity.class);
        intent.putExtra("error", errorStr);
        startActivity(intent);
    }
}