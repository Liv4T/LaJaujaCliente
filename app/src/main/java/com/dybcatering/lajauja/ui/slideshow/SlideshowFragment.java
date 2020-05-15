package com.dybcatering.lajauja.ui.slideshow;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dybcatering.lajauja.Common.Common;
import com.dybcatering.lajauja.Interface.ItemOnclickListener;
import com.dybcatering.lajauja.Model.Request;
import com.dybcatering.lajauja.OrderStatus;
import com.dybcatering.lajauja.R;
import com.dybcatering.lajauja.TrackingOrder;
import com.dybcatering.lajauja.ViewHolder.OrderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SlideshowFragment extends Fragment {

    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference requests;

    FirebaseRecyclerAdapter<Request, OrderViewHolder> adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_slideshow, container, false);

        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Request");

        recyclerView = root.findViewById(R.id.listOrders);
        recyclerView.setHasFixedSize(true);
        layoutManager =  new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        if (getActivity().getIntent().getExtras() == null)
            loadOrders(Common.currentUser.getPhone());
        else {
//            loadOrders(getIntent().getStringExtra("userPhone"));
            if (getActivity().getIntent().getStringExtra("userPhone") == null)
                loadOrders(Common.currentUser.getPhone());
            else
                loadOrders(getActivity().getIntent().getStringExtra("userPhone"));
        }



        return root;
    }

    private void loadOrders(String phone) {
        adapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(
                Request.class,
                R.layout.order_layout,
                OrderViewHolder.class,
                requests.orderByChild("phone")
                        .equalTo(phone)
        ) {
            @Override
            protected void populateViewHolder(OrderViewHolder orderViewHolder, Request request, int i) {
                orderViewHolder.txtOrderId.setText(adapter.getRef(i).getKey());
                orderViewHolder.txtOrderStatus.setText(Common.convertCodeToStatus(request.getStatus()));
                orderViewHolder.txtOrderAddress.setText(request.getAddress());
                orderViewHolder.txtOrderPhone.setText(request.getPhone());
                orderViewHolder.txtOrderDate.setText(Common.getDate(Long.parseLong(adapter.getRef(i).getKey())));

                orderViewHolder.setItemClickListener(new ItemOnclickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Common.currentKey = adapter.getRef(position).getKey();
                        startActivity(new Intent(getContext(), TrackingOrder.class));
                    }
                });


            }
        };
        recyclerView.setAdapter(adapter);
    }




}
