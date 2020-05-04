package com.dybcatering.lajauja.ui.gallery;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dybcatering.lajauja.Cart;
import com.dybcatering.lajauja.Common.Common;
import com.dybcatering.lajauja.Database.Database;
import com.dybcatering.lajauja.Model.Order;
import com.dybcatering.lajauja.Model.Request;
import com.dybcatering.lajauja.R;
import com.dybcatering.lajauja.ViewHolder.CartAdapter;
import com.dybcatering.lajauja.ViewHolder.OrderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import info.hoang8f.widget.FButton;

public class GalleryFragment extends Fragment {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference requests;

    TextView txtTotalPrice;
    FButton btnPlace;

    List<Order> cart = new ArrayList<>();

    CartAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
       // galleryViewModel =
        //        ViewModelProviders.of(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
       // button = root.findViewById(R.id.button);
        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Request");

        recyclerView = root.findViewById(R.id.listCart);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        txtTotalPrice = root.findViewById(R.id.total);
        btnPlace = root.findViewById(R.id.btnPlaceOrder);

        btnPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cart.size() > 0)
                    showAlertDialog();
                else
                    Toast.makeText(getContext(), "Tu carrito esta vacio", Toast.LENGTH_SHORT).show();

            }
        });

        loadListFood();
        return root;
    }

    private void showAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setTitle("Un paso más");
        alertDialog.setMessage("Ingresa la Dirección de Entrega");

        final EditText edtAdress = new EditText(getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        edtAdress.setLayoutParams(lp);
        alertDialog.setView(edtAdress);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        alertDialog.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Request request = new Request(
                        Common.currentUser.getPhone(),
                        Common.currentUser.getName(),
                        edtAdress.getText().toString(),
                        txtTotalPrice.getText().toString(),
                        cart
                );

                requests.child(String.valueOf(System.currentTimeMillis()))
                        .setValue(request);

                new Database(getContext().getApplicationContext()).cleanCart();
                Toast.makeText(getContext(), "Gracias, la orden ha sido recibida", Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
        });
        alertDialog.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.show();
    }
    private void loadListFood() {
        cart = new Database(getContext()).getCarts();
        adapter = new CartAdapter(cart, getContext());
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

        int total = 0;
        for (Order order:cart)
            total+=(Integer.parseInt(order.getPrice()))*(Integer.parseInt(order.getQuantity()));
        Locale locale = new Locale("en", "US");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

        txtTotalPrice.setText(fmt.format(total));
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getTitle().equals(Common.DELETE))
            deleteCart(item.getOrder());
        return true;
    }

    private void deleteCart(int order) {
        cart.remove(order);
        new Database(getContext()).cleanCart();

        for (Order item:cart)
            new Database(getContext()).addToCart(item);

        loadListFood();

    }

}
