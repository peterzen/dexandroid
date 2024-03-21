
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

import androidx.annotation.RequiresApi;

import org.mozilla.geckoview.GeckoRuntime;
import org.mozilla.geckoview.GeckoSession;
import org.mozilla.geckoview.GeckoView;
import org.torproject.jni.TorService;


public class DexClientViewActivity extends Activity {
    private static final String TAG = "DCRDEX";

    private String dexURI = null;

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dexclientview);

        GeckoView geckoView = findViewById(R.id.geckoview);
        geckoView.coverUntilFirstPaint(R.color.backgroundBlue);

        ProgressBar progressBar = findViewById(R.id.progressBar);

        GeckoSession session = new GeckoSession();

        // Workaround for Bug 1758212
        session.setContentDelegate(new GeckoSession.ContentDelegate() {
        });

        DexCompanionApp application = (DexCompanionApp) getApplicationContext();
        GeckoViewHelper gvHelper = application.getGeckoViewHelper();
        GeckoRuntime sRuntime = gvHelper.getGeckoRuntime(this);

        session.open(sRuntime);
        geckoView.setSession(session);

        DexClient dexHost = getIntent().getSerializableExtra("dexHost", DexClient.class);
        if (dexHost == null) {
            Log.e(TAG, "Invalid DEX host: (null)");
            finish();
            return;
        }

        dexURI = gvHelper.getDexURI(dexHost);

        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String status = intent.getStringExtra(TorService.EXTRA_STATUS);

                progressBar.incrementProgressBy(10);

                // onReceive fires twice.  Tor is ready when status is "ON"
                if (status == null || !status.equals("ON")) {
                    return;
                }

                Toast.makeText(context, status, Toast.LENGTH_SHORT).show();
                gvHelper.setProgressBar(session, progressBar);
                session.loadUri(dexURI);
            }
        }, new IntentFilter(TorService.ACTION_STATUS), Context.RECEIVER_NOT_EXPORTED);

        bindService(new Intent(this, TorService.class), new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                TorService torService = ((TorService.LocalBinder) service).getService();
                while (torService.getTorControlConnection() == null) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        Log.e(TAG, "InterruptedException: " + e);
                        Toast.makeText(DexClientViewActivity.this, "Tor control connection failed", Toast.LENGTH_LONG).show();
                    }
                }
                progressBar.incrementProgressBy(10);
                Toast.makeText(DexClientViewActivity.this, "Got Tor control connection", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.i(TAG, "onServiceDisconnected: " + name.toString());
            }

        }, BIND_AUTO_CREATE);
    }
}