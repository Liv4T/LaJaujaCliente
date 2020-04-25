package com.dybcatering.lajauja.ViewHolder;

import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dybcatering.lajauja.Interface.ItemOnclickListener;
import com.dybcatering.lajauja.R;

public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtOrderId, txtOrderStatus, txtOrderPhone, txtOrderAddress;

    private ItemOnclickListener itemClickListener;

    public OrderViewHolder(@NonNull View itemView) {
        super(itemView);


        txtOrderAddress = itemView.findViewById(R.id.order_address);
        txtOrderId  = itemView.findViewById(R.id.order_id);
        txtOrderStatus = itemView.findViewById(R.id.order_status);
        txtOrderPhone = itemView.findViewById(R.id.order_phone);

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
