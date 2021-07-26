package com.example.lokerid.Adapter;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lokerid.Model.HistoryModel;
import com.example.lokerid.R;

import java.util.ArrayList;
import java.util.Calendar;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder> {

    Context context;
    ArrayList<HistoryModel> myHistory;

    public HistoryAdapter(Context context, ArrayList<HistoryModel> myHistory) {
        this.context = context;
        this.myHistory = myHistory;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.card_history, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tvNumber.setText(Integer.toString(position + 1));
        holder.tvDate.setText(myHistory.get(position).getTime());
        holder.tvStand.setText(myHistory.get(position).getLoker() + " - " + myHistory.get(position).getStand());
    }

    @Override
    public int getItemCount() {
        return myHistory.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvNumber, tvDate, tvStand;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNumber = itemView.findViewById(R.id.tvNumber);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvStand = itemView.findViewById(R.id.tvStand);
        }
    }
}
