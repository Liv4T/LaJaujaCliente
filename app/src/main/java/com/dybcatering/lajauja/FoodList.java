package com.dybcatering.lajauja;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.dybcatering.lajauja.Interface.ItemOnclickListener;
import com.dybcatering.lajauja.Model.Category;
import com.dybcatering.lajauja.Model.Food;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        database = FirebaseDatabase.getInstance();

        foodList = database.getReference("Foods");
        recyclerView = findViewById(R.id.recycler_food);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        if (getIntent() != null)
            CategoryId = getIntent().getStringExtra("CategoryId");
        if (!CategoryId.isEmpty() && CategoryId != null){
                loadListFood(CategoryId);
        }

        materialSearchBar = findViewById(R.id.searchBar);

        materialSearchBar.setHint("Busca tu comida");
        //materialSearchBar.setSpeechMode(false);
        loadSuggest();
        materialSearchBar.setLastSuggestions(suggestList);
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

    private void startSearch(CharSequence text) {
        SearchAdapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(
                Food.class,
                R.layout.food_item,
                FoodViewHolder.class,
                foodList.orderByChild("Food").equalTo(text.toString())
        ) {
            @Override
            protected void populateViewHolder(FoodViewHolder foodViewHolder, Food food, int i) {
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
        foodList.orderByChild("MenuId").equalTo(CategoryId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                            Food item = postSnapshot.getValue(Food.class);
                            suggestList.add(item.getFood());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void loadListFood(String categoryId) {
    adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(Food.class, R.layout.food_item, FoodViewHolder.class, foodList.orderByChild("MenuId").equalTo(CategoryId)) {
        @Override
        protected void populateViewHolder(FoodViewHolder foodViewHolder, Food food, int i) {
            foodViewHolder.food_name.setText(food.getFood());
            Picasso.with(getBaseContext()).load(food.getImage())
                    .into(foodViewHolder.food_image);

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

    }
}
