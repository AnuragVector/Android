package com.example.mediaplayx.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mediaplayx.R;
import com.example.mediaplayx.data.model.Albummodel;
import com.example.mediaplayx.data.model.Artistmodel;

import java.util.List;

public class AlbumsAdapter extends RecyclerView.Adapter<AlbumsAdapter.ViewHolder>{
    private List<Albummodel> albummodels;
    private AlbumOnclick listener;


    // Constructor
    public interface AlbumOnclick{
        void onAlbumClick(String artistname);
    }
    public AlbumsAdapter(List<Albummodel> albummodels,AlbumOnclick listener ){
        this.albummodels=albummodels;
        this.listener=listener;


    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;

        public ViewHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.tartist);
        }

        public TextView getTextView() {
            return textView;
        }
    }
    @NonNull
    @Override
    public AlbumsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.artist, parent, false); // U
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumsAdapter.ViewHolder holder, int position) {
        Albummodel s = albummodels.get(position);
        holder.textView.setText(s.getAlbum_name());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onAlbumClick(s.getAlbum_name());
            }
        });
    }

    @Override
    public int getItemCount() {
        return albummodels.size();
    }


    public void updateAlbum(List<Albummodel> a ) {
        this.albummodels.clear();
        if (a != null) {
            this.albummodels.addAll(a);

        }
        notifyDataSetChanged();

    }
}
