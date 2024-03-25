package org.decred.dex.dexandroid;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private static final String TAG = "DCRDEX";

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // DEX client chooser list view
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        // floating action button for pairing a new DEX client
        FloatingActionButton fab = findViewById(R.id.fab);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        PreferenceManager preferenceManager = new PreferenceManager(this);
        DexClientChooserAdapter mAdapter = new DexClientChooserAdapter(this, preferenceManager);
        recyclerView.setAdapter(mAdapter);

        // If we were sent back to the main activity due to an error loading a page, display error message
        String errorStr = getIntent().getStringExtra("error");
        if (errorStr != null) {
            Log.e(TAG, "LoadError: " + errorStr);
            Toast.makeText(MainActivity.this, errorStr, Toast.LENGTH_SHORT).show();
        }

        ActivityResultLauncher<Void> launcher = registerForActivityResult(new QRCodeScannerContract(), newClientURL -> {
            if (!newClientURL.isEmpty()) {
                mAdapter.addItem(newClientURL);
            }
        });

        fab.setOnClickListener(view -> {
            Toast.makeText(MainActivity.this, "Pair new DEX", Toast.LENGTH_SHORT).show();
            launcher.launch(null);
        });
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.delete) {
            DexClientChooserAdapter adapter = ((DexClientChooserAdapter) recyclerView.getAdapter());
            if(adapter == null){
                return false;
            }
            RecyclerView.ViewHolder viewHolder = adapter.getViewHolder();
            int position = viewHolder.getAbsoluteAdapterPosition();
            if (position < 0) {
                // FIXME this shouldn't occur but it can be -1 when deleting the last item in the list.
                return false;
            }
            ((DexClientChooserAdapter) recyclerView.getAdapter()).removeItem(position);
            return true;
        }
        return super.onContextItemSelected(item);
    }
}