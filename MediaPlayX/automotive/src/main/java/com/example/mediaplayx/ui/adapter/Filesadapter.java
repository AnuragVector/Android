package com.example.mediaplayx.ui.adapter;

import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mediaplayx.R;
import com.example.mediaplayx.data.model.Artistmodel;
import com.example.mediaplayx.data.model.Filesmodel;

import java.util.ArrayList;
import java.util.List;

public class Filesadapter extends RecyclerView.Adapter<Filesadapter.ViewHolder> {
    private List<Filesmodel> filesmodels;
    private FilesOnclick listener;


    // Constructor
    public interface FilesOnclick{
        void onFilesClick(String FIlename);
    }
    public Filesadapter(List<Filesmodel> filesmodels, FilesOnclick listener) {
        this.filesmodels = filesmodels != null ? filesmodels : new ArrayList<>();
        this.listener=listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;
        private ImageView songimage;
        private MediaMetadataRetriever retriever;

        public ViewHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.tartist);
            songimage = view.findViewById(R.id.image1);
            retriever = new MediaMetadataRetriever();
        }

        public TextView getTextView() {
            return textView;
        }
    }

    @NonNull
    @Override
    public Filesadapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.artist, parent, false);

        return new Filesadapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Filesadapter.ViewHolder holder, int position) {
        Filesmodel s = filesmodels.get(position);
       holder.textView.setText(s.getFilepath());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onFilesClick(s.getFilepath());

            }
        });

    }


    @Override
    public int getItemCount() {
        return filesmodels.size();
    }
    public void updateFiles(List<Filesmodel> a ) {
        this.filesmodels.clear();
        if (a != null) {
            this.filesmodels.addAll(a);

        }
        notifyDataSetChanged();

    }
}

