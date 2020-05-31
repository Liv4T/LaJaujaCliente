package com.dybcatering.lajauja.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.dybcatering.lajauja.Common.Common;
import com.dybcatering.lajauja.Database.Database;
import com.dybcatering.lajauja.FoodDetail;
import com.dybcatering.lajauja.FoodList;
import com.dybcatering.lajauja.Interface.ItemOnclickListener;
import com.dybcatering.lajauja.Model.Banner;
import com.dybcatering.lajauja.Model.Category;
import com.dybcatering.lajauja.Model.Token;
import com.dybcatering.lajauja.R;
import com.dybcatering.lajauja.SearchActivity;
import com.dybcatering.lajauja.ViewHolder.MenuViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class HomeFragment extends Fragment {

 //   private HomeViewModel homeViewModel;


    FirebaseDatabase database;
    DatabaseReference category;
    RecyclerView recyclerView_menu;
    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<Category, MenuViewHolder> adapter;

    SwipeRefreshLayout swipeRefreshLayout;

    HashMap<String,String> image_list;
    SliderLayout mSlider;

    MaterialSearchBar materialSearchBar;

    View root;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_home, container, false);


        swipeRefreshLayout =root.findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary),
                getResources().getColor(android.R.color.holo_green_dark),
                getResources().getColor(android.R.color.holo_orange_dark),
                getResources().getColor(android.R.color.holo_blue_dark)
                );
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
              //  if (Common.IsConnectedToInternet(getContext())){
                    loadMenu();
             //   }else{
              //      Toast.makeText(getContext(), "Por favor revisa tu conexión a internet", Toast.LENGTH_SHORT).show();

             //   }
            }
        });

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                loadMenu();
            }
        });

        database = FirebaseDatabase.getInstance();
        category = database.getReference("Restaurants").child(Common.restaurantSelected)
        .child("detail").child("Category");

        adapter = new FirebaseRecyclerAdapter<Category, MenuViewHolder>(Category.class, R.layout.menu_item, MenuViewHolder.class, category) {

            @Override
            protected void populateViewHolder(MenuViewHolder menuViewHolder, Category category, final int i) {
                menuViewHolder.txtMenuName.setText(category.getName());
                Picasso.with(getActivity().getBaseContext()).load(category.getImage())
                        .into(menuViewHolder.imageView);
                final Category clickitem= category;

                menuViewHolder.setItemClickListener(new ItemOnclickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //Toast.makeText(getActivity(), ""+clickitem.getName(), Toast.LENGTH_SHORT).show();
                        Intent foodList = new Intent(getActivity(), FoodList.class);
                        foodList.putExtra("CategoryId", adapter.getRef(position).getKey());
                        startActivity(foodList);
                    }
                });
            }
        };


        recyclerView_menu = root.findViewById(R.id.recycler_menu);
        recyclerView_menu.setLayoutManager(new GridLayoutManager(getContext(), 2));
    //    LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(recyclerView_menu.getContext(),
      //          R.anim.layout_fall_down);
     //   recyclerView_menu.setLayoutAnimation(controller);

        setupSlider();

        materialSearchBar = root.findViewById(R.id.searchBar);

        materialSearchBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent search = new Intent(getActivity(), SearchActivity.class);
                startActivity(search);
            }
        });


        updateToken(FirebaseInstanceId.getInstance().getToken());



        return root;
    }

    private void setupSlider() {

        mSlider = root.findViewById(R.id.slider);
        image_list = new HashMap<>();

        final DatabaseReference banners =database.getReference("Restaurants").child(Common.restaurantSelected)
                .child("detail").child("Banner");
        banners.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                    Banner banner = dataSnapshot1.getValue(Banner.class);
                    image_list.put(banner.getName()+"@@@"+banner.getId(),banner.getImage()) ;

                }
                for (String key:image_list.keySet()){

                    String[] keySplit = key.split("@@@");
                    String nameOfFood = keySplit[0];
                    String idOfFood = keySplit[1];

                    final TextSliderView textSliderView = new TextSliderView(getContext().getApplicationContext());
                    textSliderView
                            .description(nameOfFood)
                            .image(image_list.get(key))
                            .setScaleType(BaseSliderView.ScaleType.Fit)
                            .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                                @Override
                                public void onSliderClick(BaseSliderView slider) {
                                    Intent intent = new Intent(getContext(), FoodDetail.class);
                                    intent.putExtras(textSliderView.getBundle());
                                    startActivity(intent);
                                }
                            });
                    textSliderView.bundle(new Bundle());
                    textSliderView.getBundle().putString("FoodId", idOfFood);

                    mSlider.addSlider(textSliderView);

                    banners.removeEventListener(this);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mSlider.setPresetTransformer(SliderLayout.Transformer.Background2Foreground);
        mSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mSlider.setCustomAnimation(new DescriptionAnimation());
        mSlider.setDuration(4000);
    }


    private void updateToken(String token) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference tokens = db.getReference("Tokens");
        Token data = new Token(token, false);
        tokens.child(Common.currentUser.getPhone()).setValue(data);
    }

    private void loadMenu() {

         adapter.notifyDataSetChanged();

        recyclerView_menu.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);

        recyclerView_menu.getAdapter().notifyDataSetChanged();
        recyclerView_menu.scheduleLayoutAnimation();
    }
}
