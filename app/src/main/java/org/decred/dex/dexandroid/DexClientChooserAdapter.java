package org.decred.dex.dexandroid;

import static androidx.core.content.ContextCompat.startActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import android.content.Context;
import android.widget.Toast;

public class DexClientChooserAdapter extends RecyclerView.Adapter<DexClientChooserAdapter.MyViewHolder> {
    private List<DexClient> list;
    private Context context;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public MyViewHolder(View v) {
            super(v);
            textView = v.findViewById(R.id.text_view);
        }
    }

    public DexClientChooserAdapter(Context context, List<DexClient> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public DexClientChooserAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        DexClient item = list.get(position);
        holder.textView.setText(item.getClientName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Clicked on item: " + item.getClientName(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, DexClientViewActivity.class);
                intent.putExtra("dexClient", item);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}