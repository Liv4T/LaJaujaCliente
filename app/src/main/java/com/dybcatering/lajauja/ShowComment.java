package com.dybcatering.lajauja;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Toast;

import com.dybcatering.lajauja.Common.Common;
import com.dybcatering.lajauja.Database.Database;
import com.dybcatering.lajauja.Interface.ItemOnclickListener;
import com.dybcatering.lajauja.Model.Food;
import com.dybcatering.lajauja.Model.Order;
import com.dybcatering.lajauja.Model.Rating;
import com.dybcatering.lajauja.ViewHolder.FoodViewHolder;
import com.dybcatering.lajauja.ViewHolder.ShowCommentViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ShowComment extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference ratingTbl;


    FirebaseRecyclerAdapter<Rating, ShowCommentViewHolder> adapter;

    String foodId = "";

    List<String> suggestList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_comment);



        recyclerView = findViewById(R.id.recyclerComment);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        database = FirebaseDatabase.getInstance();
        ratingTbl = database.getReference("Rating");
        if (getIntent() != null){
            foodId = getIntent().getStringExtra(Common.INTENT_FOOD_ID);
        }
        if (!foodId.isEmpty() && foodId !=null){
            adapter = new FirebaseRecyclerAdapter<Rating, ShowCommentViewHolder>(
                    Rating.class,
                    R.layout.show_comment_dialog,
                    ShowCommentViewHolder.class,
                    ratingTbl.orderByChild("foodId").equalTo(foodId)
            ) {
                @Override
                protected void populateViewHolder(ShowCommentViewHolder showCommentViewHolder, Rating rating, int i) {
                    int count=0, sum=0;

                    showCommentViewHolder.txtComment.setText(rating.getComment());
                    showCommentViewHolder.txtUserPhone.setText(rating.getUserPhone());
                    sum+=Integer.parseInt(rating.getRateValue());
                    count++;
                    if (count !=0) {
                        float average = sum/count;
//                        showCommentViewHolder.ratingBar.setRating(average);
                    }

                }
            };
            Log.d("TAG", ""+adapter.getItemCount());

            recyclerView.setAdapter(adapter);
        }





    }






}
