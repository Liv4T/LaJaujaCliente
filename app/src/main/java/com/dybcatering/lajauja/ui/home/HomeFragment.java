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

import com.dybcatering.lajauja.Common.Common;
import com.dybcatering.lajauja.FoodList;
import com.dybcatering.lajauja.Interface.ItemOnclickListener;
import com.dybcatering.lajauja.Model.Category;
import com.dybcatering.lajauja.R;
import com.dybcatering.lajauja.ViewHolder.MenuViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class HomeFragment extends Fragment {

 //   private HomeViewModel homeViewModel;


    FirebaseDatabase database;
    DatabaseReference category;
    RecyclerView recyclerView_menu;
    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<Category, MenuViewHolder> adapter;

    SwipeRefreshLayout swipeRefreshLayout;

   // public HomeFragment(FirebaseRecyclerAdapter<Category, MenuViewHolder> adapter) {
     //   this.adapter = adapter;
   // }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //homeViewModel =                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

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
        category = database.getReference("Category");

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
        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(recyclerView_menu.getContext(),
                R.anim.layout_fall_down);
        recyclerView_menu.setLayoutAnimation(controller);


        return root;
    }


    private void loadMenu() {

         adapter.notifyDataSetChanged();

        recyclerView_menu.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);

        recyclerView_menu.getAdapter().notifyDataSetChanged();
        recyclerView_menu.scheduleLayoutAnimation();
    }
}
