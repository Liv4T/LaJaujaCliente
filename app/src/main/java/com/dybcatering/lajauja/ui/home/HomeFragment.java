package com.dybcatering.lajauja.ui.home;

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

import com.dybcatering.lajauja.Home;
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
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //homeViewModel =                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);



        database= FirebaseDatabase.getInstance();
        category = database.getReference("Category");

        recyclerView_menu = root.findViewById(R.id.recycler_menu);

        recyclerView_menu.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView_menu.setLayoutManager(layoutManager);
        loadMenu();

        // final TextView textView = root.findViewById(R.id.text_home);
        //homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
         //   @Override
           // public void onChanged(@Nullable String s) {
             //   textView.setText(s);
//            }
  //      });
        return root;
    }


    private void loadMenu() {
        FirebaseRecyclerAdapter<Category, MenuViewHolder> adapter = new FirebaseRecyclerAdapter<Category, MenuViewHolder>(Category.class, R.layout.menu_item, MenuViewHolder.class, category) {

            @Override
            protected void populateViewHolder(MenuViewHolder menuViewHolder, Category category, int i) {
                menuViewHolder.txtMenuName.setText(category.getName());
                Picasso.with(getActivity().getBaseContext()).load(category.getImage())
                        .into(menuViewHolder.imageView);
                final Category clickitem= category;

                menuViewHolder.setItemClickListener(new ItemOnclickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Toast.makeText(getActivity(), ""+clickitem.getName(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };
        recyclerView_menu.setAdapter(adapter);
    }
}
