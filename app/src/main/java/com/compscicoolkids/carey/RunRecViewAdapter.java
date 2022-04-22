package com.compscicoolkids.carey;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class RunRecViewAdapter extends RecyclerView.Adapter<RunRecViewAdapter.ViewHolder>{
    private static final String TAG = "RunRecViewAdapter";

    private ArrayList<Run> runs = new ArrayList<>();

    public RunRecViewAdapter(Context mContext){
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_run, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        //set run data to run card
        holder.txtDistance.setText(runs.get(position).getLength());
        holder.txtTime.setText(String.format("%dmins", runs.get(position).getMinutes()));

        holder.txtDate.setText(runs.get(position).getDate());
    }

    @Override
    public int getItemCount() {
        return runs.size();
    }

    public void setRuns(ArrayList<Run> runs) {
        this.runs = runs;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView txtDate;
        private final TextView txtDistance;
        private final TextView txtTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            CardView parent = itemView.findViewById(R.id.parent);
            txtDate = itemView.findViewById(R.id.date);
            txtDistance = itemView.findViewById(R.id.distance);
            txtTime = itemView.findViewById(R.id.time);
        }
    }
}
