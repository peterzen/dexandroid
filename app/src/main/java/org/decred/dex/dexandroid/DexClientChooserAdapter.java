package org.decred.dex.dexandroid;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import android.content.Context;
import android.widget.Toast;

public class DexClientChooserAdapter extends RecyclerView.Adapter<DexClientChooserAdapter.ItemViewHolder> {
    private final PreferenceManager preferenceManager;
    private final List<DexClient> list;
    private final Context context;

    private ItemViewHolder currentViewHolder;


    public static class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        public TextView textView;

        public ItemViewHolder(View v) {
            super(v);
            textView = v.findViewById(R.id.text_view);
            v.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(Menu.NONE, R.id.delete, Menu.NONE, "Delete");
        }
    }

    public DexClientChooserAdapter(Context context, PreferenceManager preferenceManager) {
        this.context = context;
        this.preferenceManager = preferenceManager;
        this.list = preferenceManager.getDexClientList();
    }

    public void addItem(String URL) {
        if (preferenceManager.containsUrl(URL)) {
            Toast.makeText(context, "DEX client already exists", Toast.LENGTH_SHORT).show();
            return;
        }
        DexClient newItem = preferenceManager.addDexClientFromURL(URL);
        list.add(newItem);
        notifyItemInserted(list.size() - 1);
        Toast.makeText(context, "Paired DEX: " + URL, Toast.LENGTH_LONG).show();
    }

    public void removeItem(int position) {
        DexClient item = list.get(position);
        preferenceManager.removeDexClient(item);
        list.remove(position);
        notifyItemRemoved(position);
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, @SuppressLint("RecyclerView") int position) {
        currentViewHolder = holder;
        DexClient item = list.get(position);
        holder.textView.setText(item.getUrl());
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DexClientViewActivity.class);
            intent.putExtra("dexHost", item);
            context.startActivity(intent);
        });

        holder.itemView.setOnLongClickListener(v -> {
            v.showContextMenu();
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public ItemViewHolder getViewHolder() {
        return currentViewHolder;
    }
}