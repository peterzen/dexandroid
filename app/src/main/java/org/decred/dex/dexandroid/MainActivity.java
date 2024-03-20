package org.decred.dex.dexandroid;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

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

        ActivityResultLauncher<Void> launcher = registerForActivityResult(new QRCodeScannerContract(), new ActivityResultCallback<String>() {
            @Override
            public void onActivityResult(String newClientURL) {
                if (!newClientURL.isEmpty()) {
                    mAdapter.addItem(newClientURL);
                }
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Pair new DEX", Toast.LENGTH_SHORT).show();
                launcher.launch(null);
            }
        });
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.delete) {
            RecyclerView.ViewHolder viewHolder = ((DexClientChooserAdapter) recyclerView.getAdapter()).getViewHolder();
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