package com.dybcatering.lajauja.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dybcatering.lajauja.Interface.ItemOnclickListener;
import com.dybcatering.lajauja.R;

public class RestaurantViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtRestaurantName;
    public ImageView imageView;

    private ItemOnclickListener itemClickListener;

    public RestaurantViewHolder(@NonNull View itemView) {
        super(itemView);

        //txtRestaurantName = itemView.findViewById(R.id.restaurant_name);
        imageView = itemView.findViewById(R.id.restaurant_image);

        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemOnclickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition(), false);
    }
}

