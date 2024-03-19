package org.decred.dex.dexandroid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // DEX client chooser list view
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        // floating action button for pairing a new DEX client
        FloatingActionButton fab = findViewById(R.id.fab);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        PreferenceManager preferenceManager = new PreferenceManager(this);
        List<DexClient> dexClients = preferenceManager.getDexClientList();
        RecyclerView.Adapter<DexClientChooserAdapter.MyViewHolder> mAdapter = new DexClientChooserAdapter(this, dexClients);
        recyclerView.setAdapter(mAdapter);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Pair new DEX", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, QRCodeScannerActivity.class);
                startActivity(intent);

//                List<DexClient> dexClientList = new ArrayList<>();
//                dexClientList.add(new DexClient("dex1", "http://kenphf64zothc4vl4wzsgt43jzroyoukd2zh75k5ho3bpydqzxkvpdad.onion", "cookie_value"));
//                preferenceManager.saveDexClientList(dexClientList);
            }
        });
    }
}