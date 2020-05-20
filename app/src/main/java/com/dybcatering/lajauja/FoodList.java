package com.dybcatering.lajauja;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.dybcatering.lajauja.Common.Common;
import com.dybcatering.lajauja.Database.Database;
import com.dybcatering.lajauja.Interface.ItemOnclickListener;
import com.dybcatering.lajauja.Model.Category;
import com.dybcatering.lajauja.Model.Food;
import com.dybcatering.lajauja.Model.Order;
import com.dybcatering.lajauja.ViewHolder.FoodViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class FoodList extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference foodList;

    String CategoryId = "";

    FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;


    FirebaseRecyclerAdapter<Food, FoodViewHolder> SearchAdapter;

    List<String> suggestList = new ArrayList<>();
    MaterialSearchBar materialSearchBar;

    Database localDB;

    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);


        database = FirebaseDatabase.getInstance();

        foodList =database.getReference("Restaurants").child(Common.restaurantSelected)
                .child("detail").child("Foods");

        localDB = new Database(this);
        recyclerView = findViewById(R.id.recycler_food);
        recyclerView.setHasFixedSize(true);

        //layoutManager = new LinearLayoutManager(this);
        //recyclerView.setLayoutManager(layoutManager);
        swipeRefreshLayout = findViewById(R.id.swipe_layout);

        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary),
                getResources().getColor(android.R.color.holo_green_dark),
                getResources().getColor(android.R.color.holo_orange_dark),
                getResources().getColor(android.R.color.holo_blue_dark)
        );
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (getIntent() != null)
                    CategoryId = getIntent().getStringExtra("CategoryId");
                if (!CategoryId.isEmpty() && CategoryId != null){
                    if (Common.IsConnectedToInternet(getApplicationContext())){
                        loadListFood(CategoryId);
                    }else{
                        Toast.makeText(FoodList.this, "Por favor revisa tu conexión a internet", Toast.LENGTH_SHORT).show();

                    }

                }
            }
        });
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if (getIntent() != null)
                    CategoryId = getIntent().getStringExtra("CategoryId");
                if (!CategoryId.isEmpty() && CategoryId != null){
                    if (Common.IsConnectedToInternet(getApplicationContext())){
                        loadListFood(CategoryId);
                    }else{
                        Toast.makeText(FoodList.this, "Por favor revisa tu conexión a internet", Toast.LENGTH_SHORT).show();
                    }
                }

                materialSearchBar = findViewById(R.id.searchBar);

                materialSearchBar.setHint("Busca tu comida");
                //materialSearchBar.setSpeechMode(false);
                loadSuggest();
                materialSearchBar.setCardViewElevation(10);
                materialSearchBar.addTextChangeListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        List<String> suggest = new ArrayList<String>();
                        for (String search: suggestList){
                            if (search.toLowerCase().contains(materialSearchBar.getText().toLowerCase()))
                                suggest.add(search);
                        }
                        materialSearchBar.setLastSuggestions(suggest);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
                    @Override
                    public void onSearchStateChanged(boolean enabled) {
                        if (!enabled)
                            recyclerView.setAdapter(adapter);
                    }

                    @Override
                    public void onSearchConfirmed(CharSequence text) {
                        startSearch(text);
                    }

                    @Override
                    public void onButtonClicked(int buttonCode) {

                    }
                });
            }
        });
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));




    }

    private void startSearch(CharSequence text) {
        SearchAdapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(
                Food.class,
                R.layout.food_item,
                FoodViewHolder.class,
                foodList.orderByChild("food").equalTo(text.toString())
        ) {
            @Override
            protected void populateViewHolder(final FoodViewHolder foodViewHolder, final Food food, final int i) {
                foodViewHolder.food_name.setText(food.getFood());
                Picasso.with(getBaseContext()).load(food.getImage())
                        .into(foodViewHolder.food_image);

                final Food local  = food;
                foodViewHolder.setItemClickListener(new ItemOnclickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        // Toast.makeText(FoodList.this, ""+local.getFood(), Toast.LENGTH_SHORT).show();
                        Intent foodDetail = new Intent(FoodList.this, FoodDetail.class);
                        foodDetail.putExtra("FoodId", SearchAdapter.getRef(position).getKey());
                        startActivity(foodDetail);
                    }
                });
            }
        };
        recyclerView.setAdapter(SearchAdapter);

    }

    private void loadSuggest() {
        foodList.orderByChild("menuId").equalTo(CategoryId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                            Food item = postSnapshot.getValue(Food.class);
                            suggestList.add(item.getFood());
                        }

                        materialSearchBar.setLastSuggestions(suggestList);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void loadListFood(String categoryId) {
    adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(Food.class, R.layout.food_item, FoodViewHolder.class, foodList.orderByChild("menuId").equalTo(categoryId)) {
        @Override
        protected void populateViewHolder(final FoodViewHolder foodViewHolder, final Food food, final int i) {
            foodViewHolder.food_name.setText(food.getFood());
            float precio = Float.parseFloat(food.getPrice());
            foodViewHolder.food_price.setAmount(precio, "$");
            Picasso.with(getBaseContext()).load(food.getImage())
                    .into(foodViewHolder.food_image);

            foodViewHolder.quick_cart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new Database(getBaseContext()).addToCart(new Order(
                            adapter.getRef(i).getKey(),
                            food.getFood(),
                            "1",
                            food.getPrice(),
                            food.getDiscount(),
                            food.getImage(),
                            "vacio"
                    ));
                    Toast.makeText(FoodList.this, "Agregado Al Carrito de Compras ", Toast.LENGTH_SHORT).show();
                }
            });
  /*
            if (localDB.isFavorite(adapter.getRef(i).getKey()))
                foodViewHolder.fav_image.setImageResource(R.drawable.ic_favorite_black_24dp);

            foodViewHolder.fav_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!localDB.isFavorite(adapter.getRef(i).getKey())){
                        localDB.addToFavorites(adapter.getRef(i).getKey());
                        foodViewHolder.fav_image.setImageResource(R.drawable.ic_favorite_black_24dp);
                        Toast.makeText(FoodList.this, ""+food.getFood()+" fue añadido a favoritos", Toast.LENGTH_SHORT).show();
                    }else{
                        localDB.removeFromFavorites(adapter.getRef(i).getKey());
                        foodViewHolder.fav_image.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                        Toast.makeText(FoodList.this, ""+food.getFood()+" fue eliminado de los favoritos", Toast.LENGTH_SHORT).show();
                    }
                }
            });
*/

            final Food local  = food;
            foodViewHolder.setItemClickListener(new ItemOnclickListener() {
                @Override
                public void onClick(View view, int position, boolean isLongClick) {
                   // Toast.makeText(FoodList.this, ""+local.getFood(), Toast.LENGTH_SHORT).show();
                    Intent foodDetail = new Intent(FoodList.this, FoodDetail.class);
                    foodDetail.putExtra("FoodId", adapter.getRef(position).getKey());
                    startActivity(foodDetail);
                }
            });

        }
    };
        Log.d("TAG", ""+adapter.getItemCount());
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter !=null)
            adapter.notifyDataSetChanged();
    }
}
