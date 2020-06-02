package com.dybcatering.lajauja;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.dybcatering.lajauja.CheckOut.CheckOutCard;
import com.dybcatering.lajauja.Common.Common;
import com.dybcatering.lajauja.Common.Config;
import com.dybcatering.lajauja.Database.Database;
import com.dybcatering.lajauja.Model.DataMessage;
import com.dybcatering.lajauja.Model.MyResponse;
import com.dybcatering.lajauja.Model.Order;

import com.dybcatering.lajauja.Model.Request;
import com.dybcatering.lajauja.Model.Token;
import com.dybcatering.lajauja.Model.User;
import com.dybcatering.lajauja.Remote.APIService;
import com.dybcatering.lajauja.ViewHolder.CartAdapter;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import info.hoang8f.widget.FButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Cart extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference requests;

    public TextView txtTotalPrice, totalenvio, totalmasenvio;
    FButton btnPlace;

    List<Order> cart = new ArrayList<>();

   CartAdapter adapter;

   APIService mService;


   String address, comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);


        mService = Common.getFCMService();

        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Request");

        recyclerView = findViewById(R.id.listCart);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        txtTotalPrice = findViewById(R.id.total);
        totalenvio = findViewById(R.id.totalenvio);
        totalmasenvio = findViewById(R.id.totalmasenvio);


        btnPlace = findViewById(R.id.btnPlaceOrder);

        User user = new User();
       // final String nombreusuario = user.getName();



        btnPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String formatAmmount = txtTotalPrice.getText().toString()
                        .replace("$", "")
                        .replace(",", "")
                        .replace(".00", "");

                final int totalint = Integer.valueOf(formatAmmount);

                int precio = 6000;
                int total = 0;

///tortilla blanda de harina
                total = precio +totalint;
                if (total < 15000){
                    Toast.makeText(Cart.this, "No es posible realizar una compra inferior a $15.000", Toast.LENGTH_SHORT).show();
                }else if(total <60000){

                    String valor = String.valueOf(total);
                    int totalmasenv = total;
                    totalenvio.setText("$6.000");
                    totalmasenvio.setText("$"+totalmasenv);

                    Toast.makeText(Cart.this, "Se agrega un precio adicional de $6.000 por costos de envío cuando el total es inferior a $60.0000", Toast.LENGTH_SHORT).show();
                    if (Common.currentUser.getName().isEmpty() || Common.currentUser.getName().equals("")){
                        iniciarRegistroFinal();
                    }else{
                        showAlertDialog(total);
                    }


                } else if (totalint > 60000) {
                    showAlertDialog(total);
                }  else {
                        Toast.makeText(Cart.this, "Tu carrito esta vacio", Toast.LENGTH_SHORT).show();
                    }

            }
        });

        loadListFood();


    }

    private void iniciarRegistroFinal() {
        Intent comprausuario = new Intent(Cart.this, CompraUsuario.class);
        startActivity(comprausuario);
    }

    private void showAlertDialog(final int total) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Cart.this);
        alertDialog.setTitle("Un paso más");
        alertDialog.setMessage("Ingresa la Dirección de Entrega");

        LayoutInflater inflater = this.getLayoutInflater();
        View order_address_comment = inflater.inflate(R.layout.order_adress_comment, null);

        final MaterialEditText edtAdress = order_address_comment.findViewById(R.id.edtAdress);
        final MaterialEditText edtComment = order_address_comment.findViewById(R.id.edtComment);


       // final RadioButton rdCrediBanco = order_address_comment.findViewById(R.id.rdiPagoCrediBanco);
       // final RadioButton rdCOD = order_address_comment.findViewById(R.id.rdiPagoContraEntrega);

      //  final RadioButton rdHora1 = order_address_comment.findViewById(R.id.rdiHoraEntregaUno);
   //     final RadioButton rdHora2 = order_address_comment.findViewById(R.id.rdiHoraEntregaDos);

        edtAdress.setText(Common.currentUser.getMyDirection());

        alertDialog.setView(order_address_comment);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        alertDialog.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                address = edtAdress.getText().toString();
                comment = edtComment.getText().toString();

                /*
                if (!rdHora1.isChecked() && !rdHora2.isChecked()){
                    Toast.makeText(Cart.this, "Por Favor Seleccione una Hora de Entrega", Toast.LENGTH_SHORT).show();
                    return;
                }else if (rdHora1.isChecked()){

                 */
                if (TextUtils.isEmpty(address) || TextUtils.isEmpty(comment)){
                    Toast.makeText(Cart.this, "No olvides completar todos los campos", Toast.LENGTH_SHORT).show();
                }else{
                    String formatAmmount = txtTotalPrice.getText().toString()
                            .replace("$", "")
                            .replace(",", "")
                            .replace(".00", "");

                    int validacion = Integer.parseInt(formatAmmount);

                    int total = 0;
                    if (validacion <= 60000){
                        total  = validacion + 6000;
                    }else{
                        total = validacion;
                    }

                    String totalconvertido = String.valueOf(total);

                    Intent checkoutcard = new Intent(Cart.this, CheckOutCard.class);
                    checkoutcard.putExtra("address", address);
                    checkoutcard.putExtra("payment", totalconvertido);
                    checkoutcard.putExtra("status", "0");
                    checkoutcard.putExtra("comment", comment);
                    checkoutcard.putExtra("paymentState", "Aceptado");
                    startActivity(checkoutcard);



                    String latlng = "";




                }





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



    private void sendNotification(final String order_number) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        final Query data  = tokens.orderByChild("isServerToken").equalTo(true);
        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapShot: dataSnapshot.getChildren()){
                    Token serverToken = postSnapShot.getValue(Token.class);

//                    Notification notification = new Notification("La Jauja", "Tienes una nueva orden"+order_number);
  //                  Sender content = new Sender(serverToken.getToken(), notification);
                    Map<String, String > dataSend = new HashMap<>();
                    dataSend.put("title", "La Jauja");
                    dataSend.put("message", "Tienes una nueva orden: "+order_number);
                    DataMessage dataMessage = new DataMessage(serverToken.getToken(), dataSend);


                    mService.sendNotification(dataMessage)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.body().success == 1){
                                         Toast.makeText(Cart.this, "Gracias, la orden ha sido recibida", Toast.LENGTH_SHORT).show();
                                         finish();
                                    }else{
                                        Toast.makeText(Cart.this, "Lo sentimos, no fue posible entregar la orden", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {
                                    Log.e("Error", t.getMessage());
                                }
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadListFood() {
        cart = new Database(this).getCarts();
        adapter = new CartAdapter(cart, this);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

        int total = 0;
        for (Order order:cart)
            total+=(Integer.parseInt(order.getPrice()))*(Integer.parseInt(order.getQuantity()));
        Locale locale = new Locale("en", "US");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

        int envio = 0;
        if (total<= 60000){
            envio = 6000;
            totalenvio.setText("$6.000");
        }else{
            envio = 0;
            totalenvio.setText("$0");
        }

        txtTotalPrice.setText(fmt.format(total));
        totalmasenvio.setText(fmt.format(total+envio));
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getTitle().equals(Common.DELETE))
            deleteCart(item.getOrder());
        return true;
    }

    private void deleteCart(int order) {
        cart.remove(order);
        new Database(this).cleanCart();

        for (Order item:cart)
            new Database(this).addToCart(item);

        loadListFood();

    }

}
