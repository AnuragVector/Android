package com.example.mediaplayx.ui.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mediaplayx.R;

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.ViewHolder> {

    private String[] localDataSet;
    private int selectedPosition=0,previousPosition=-1;
    private OnItemClickListener listener; // Declare the listener

    // Define the interface for the callback
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public MediaAdapter(String[] dataSet, OnItemClickListener listener) {
        this.localDataSet = dataSet != null ? dataSet : new String[0]; // Handle null case
        this.listener = listener; // Initialize the listener
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;

        public ViewHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.textlistview);


        }

        public TextView getTextView() {
            return textView;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.mediacategory, viewGroup, false); // Use your custom layout here
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        viewHolder.getTextView().setText(localDataSet[position]);
        if(position==selectedPosition){
            viewHolder.itemView.setBackgroundColor(ContextCompat.getColor(viewHolder.itemView.getContext(), R.color.selected_color));
        }
        else
            viewHolder.itemView.setBackgroundColor(ContextCompat.getColor(viewHolder.itemView.getContext(), R.color.default_color));

        viewHolder.itemView.setOnClickListener(v -> {
            Log.d("adapter", "clicked " + localDataSet[position]);
            listener.onItemClick(position);
             previousPosition = selectedPosition;
            selectedPosition = position;

            // Notify the adapter to refresh the previous and current selected items
            if(previousPosition!=selectedPosition) {
                notifyItemChanged(previousPosition);
                notifyItemChanged(selectedPosition);
            }

            // Scale animation
            v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(50).withEndAction(() -> {
                v.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
            });
        });
    }

    @Override
    public int getItemCount() {
        return (int)localDataSet.length;
    }
}