package com.dybcatering.lajauja;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.dybcatering.lajauja.Interface.ItemOnclickListener;
import com.dybcatering.lajauja.Model.Category;
import com.dybcatering.lajauja.Model.Food;
import com.dybcatering.lajauja.ViewHolder.FoodViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class FoodList extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference foodList;

    String CategoryId = "";

    FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;

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
