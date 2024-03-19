
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
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import org.mozilla.geckoview.GeckoRuntime;
import org.mozilla.geckoview.GeckoSession;
import org.mozilla.geckoview.GeckoView;
import org.torproject.jni.TorService;


public class DexClientViewActivity extends Activity {
    private static final String TAG = "DCRDEX";

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dexclientview);

        GeckoView view = findViewById(R.id.geckoview);
        GeckoSession session = new GeckoSession();

        // Workaround for Bug 1758212
        session.setContentDelegate(new GeckoSession.ContentDelegate() {
        });

        DexCompanionApp application = (DexCompanionApp) getApplicationContext();
        GeckoViewHelper gvHelper = application.getGeckoViewHelper();
        GeckoRuntime sRuntime = gvHelper.getGeckoRuntime(this);

        session.open(sRuntime);
        view.setSession(session);

        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // tor is initialized, load page
                String status = intent.getStringExtra(TorService.EXTRA_STATUS);
                Toast.makeText(context, status, Toast.LENGTH_SHORT).show();
                session.loadUri("http://kenphf64zothc4vl4wzsgt43jzroyoukd2zh75k5ho3bpydqzxkvpdad.onion/");
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
                        Log.i(TAG, "InterruptedException: " + e);
                    }
                }
                Toast.makeText(DexClientViewActivity.this, "Got Tor control connection", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.i(TAG, "onServiceDisconnected: " + name.toString());
            }

        }, BIND_AUTO_CREATE);
    }


}