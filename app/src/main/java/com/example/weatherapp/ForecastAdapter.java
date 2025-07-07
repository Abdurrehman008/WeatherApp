package com.example.weatherapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ViewHolder> {
    private List<ForecastItem> forecastList;

    public ForecastAdapter(List<ForecastItem> forecastList) {
        this.forecastList = forecastList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView date, temp, condition;
        public ViewHolder(View view) {
            super(view);
            date = view.findViewById(R.id.tvDate);
            temp = view.findViewById(R.id.tvTemp);
            condition = view.findViewById(R.id.tvCondition);
        }
    }

    @NonNull
    @Override
    public ForecastAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_forecast, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ForecastAdapter.ViewHolder holder, int position) {
        ForecastItem item = forecastList.get(position);
        holder.date.setText(item.date);
        holder.temp.setText(item.temp);
        holder.condition.setText(item.condition);
    }

    @Override
    public int getItemCount() {
        return forecastList.size();
    }
}