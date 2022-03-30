package com.compscicoolkids.carey;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;


public class RunRecViewAdapter extends RecyclerView.Adapter<RunRecViewAdapter.ViewHolder>{
    private static final String TAG = "RunRecViewAdapter";

    private ArrayList<Run> runs = new ArrayList<>();
    private Context mContext;

    public RunRecViewAdapter(Context mContext){
        this.mContext = mContext;
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
        Log.d(TAG, "onBindViewHolder: called");
        holder.txtDistance.setText(String.format("%.02fkm", runs.get(position).getLength()));
        holder.txtTime.setText(String.format("%dmins", runs.get(position).getMinutes()));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(runs.get(position).getDate());

        holder.txtDate.setText(String.format("%d/%d/%d", calendar.get(Calendar.DAY_OF_MONTH),(calendar.get(Calendar.MONTH)+1), calendar.get(Calendar.YEAR)));

        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, runs.get(position).getId() + " selected.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return runs.size();
    }

    public void setRuns(ArrayList<Run> runs) {
        this.runs = runs;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private CardView parent;
        private TextView txtDate;
        private TextView txtDistance;
        private TextView txtTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            parent = itemView.findViewById(R.id.parent);
            txtDate = itemView.findViewById(R.id.date);
            txtDistance = itemView.findViewById(R.id.distance);
            txtTime = itemView.findViewById(R.id.time);
        }
    }
}
