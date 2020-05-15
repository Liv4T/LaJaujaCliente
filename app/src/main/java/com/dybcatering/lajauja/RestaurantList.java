package com.dybcatering.lajauja;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.dybcatering.lajauja.Common.Common;
import com.dybcatering.lajauja.Interface.ItemOnclickListener;
import com.dybcatering.lajauja.Model.Category;
import com.dybcatering.lajauja.Model.Restaurant;
import com.dybcatering.lajauja.ViewHolder.MenuViewHolder;
import com.dybcatering.lajauja.ViewHolder.RestaurantViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class RestaurantList extends AppCompatActivity {

    AlertDialog waitingDialog;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;

    FirebaseDatabase database;
    Restaurant restaurants;
    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<Restaurant, RestaurantViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_list);

        swipeRefreshLayout =findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary),
                getResources().getColor(android.R.color.holo_green_dark),
                getResources().getColor(android.R.color.holo_orange_dark),
                getResources().getColor(android.R.color.holo_blue_dark)
        );
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                  if (Common.IsConnectedToInternet(getBaseContext())){
                loadRestaurant();
                   }else{
                      Toast.makeText(RestaurantList.this, "Por favor revisa tu conexión a internet", Toast.LENGTH_SHORT).show();

                   }
            }
        });
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                loadRestaurant();
            }
        });

        recyclerView = findViewById(R.id.recycler_restaurant);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        database = FirebaseDatabase.getInstance();


        adapter = new FirebaseRecyclerAdapter<Restaurant, RestaurantViewHolder>(Restaurant.class, R.layout.restaurant_item, RestaurantViewHolder.class, database.getReference().child("Restaurants")) {

            @Override
            protected void populateViewHolder(RestaurantViewHolder restaurantViewHolder, Restaurant restaurant, int i) {
//                restaurantViewHolder.txtRestaurantName.setText(restaurant.getName());
                Picasso.with(getBaseContext()).load(restaurant.getImage())
                        .fit()
                        .centerCrop()
                        .into(restaurantViewHolder.imageView);
                final Restaurant clickitem= restaurants;

                restaurantViewHolder.setItemClickListener(new ItemOnclickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //Toast.makeText(getActivity(), ""+clickitem.getName(), Toast.LENGTH_SHORT).show();
                        Intent foodList = new Intent(RestaurantList.this, Home.class);
                        Common.restaurantSelected= adapter.getRef(position).getKey();
                        startActivity(foodList);
                    }
                });
            }

        };


    }

    private void loadRestaurant() {
        adapter.notifyDataSetChanged();

        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);

        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }
}

