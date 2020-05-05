package com.dybcatering.lajauja.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dybcatering.lajauja.Interface.ItemOnclickListener;
import com.dybcatering.lajauja.R;

public class FoodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView food_name;
    public ImageView food_image;//, fav_image;
    private ItemOnclickListener itemClickListener;

    public void setItemClickListener(ItemOnclickListener itemClickListener) {
        this.itemClickListener = itemClickListener;


    }

    public FoodViewHolder(@NonNull View itemView) {
        super(itemView);
        food_name = itemView.findViewById(R.id.food_name);
        food_image = itemView.findViewById(R.id.food_image);
        //fav_image = itemView.findViewById(R.id.fav);

        itemView.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition(), false);
    }
}
