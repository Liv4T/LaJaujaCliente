package com.dybcatering.lajauja.CheckOut;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.braintreepayments.cardform.view.CardForm;
import com.dybcatering.lajauja.Cart;
import com.dybcatering.lajauja.Common.Common;
import com.dybcatering.lajauja.Database.Database;
import com.dybcatering.lajauja.Home;
import com.dybcatering.lajauja.Model.DataMessage;
import com.dybcatering.lajauja.Model.MyResponse;
import com.dybcatering.lajauja.Model.Order;
import com.dybcatering.lajauja.Model.Request;
import com.dybcatering.lajauja.Model.Token;
import com.dybcatering.lajauja.Model.User;
import com.dybcatering.lajauja.R;
import com.dybcatering.lajauja.Remote.APIService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import co.epayco.android.Epayco;
import co.epayco.android.models.Authentication;
import co.epayco.android.models.Card;
import co.epayco.android.models.Charge;
import co.epayco.android.models.Client;
import co.epayco.android.util.EpaycoCallback;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckOutCard extends AppCompatActivity {


    CardForm cardForm;
    Button buy;
    AlertDialog.Builder alertBuilder;
    FirebaseDatabase database;
    DatabaseReference requests;

    //llamado de los productos de cart
    List<Order> cart = new ArrayList<>();

    APIService mService;

    ACProgressFlower dialogloading;

    TextView total;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out_card);

        mService = Common.getFCMService();



        cardForm = findViewById(R.id.card_form);
        buy = findViewById(R.id.btnBuy);
        cardForm.cardRequired(true)
                .expirationRequired(true)
                .cvvRequired(true)
                .setup(CheckOutCard.this);
        cardForm.getCvvEditText().setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);

        cart = new Database(this).getCarts();

        total = findViewById(R.id.total);


        Bundle b = new Bundle();
        b = getIntent().getExtras();
        final String payment = b.getString("payment");


        total.setText(payment);


        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cardForm.isValid()) {
                    alertBuilder = new AlertDialog.Builder(CheckOutCard.this);
                    alertBuilder.setTitle("Confirmar Compra");
                    alertBuilder.setMessage("Número de Tarjeta: " + cardForm.getCardNumber() + "\n" +
                            "Fecha de expiración: " + cardForm.getExpirationDateEditText().getText().toString() + "\n" +
                            "CVV: " + cardForm.getCvv() + "\n" +
                            "Total: "+ payment);
                    alertBuilder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
//                            Toast.makeText(CheckOutCard.this, "Thank you for purchase", Toast.LENGTH_LONG).show();

                             dialogloading = new ACProgressFlower.Builder(CheckOutCard.this)
                                    .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                                    .themeColor(Color.WHITE)
                                    .text("Registrando pago")
                                    .fadeColor(Color.DKGRAY).build();

                            dialogloading.show();


                            Authentication auth = new Authentication();

                            auth.setApiKey("09a20cdc0bb914e3fc296b66a9384edb");
                            auth.setPrivateKey("47456095b252b6dd13a8fe07dc5099c1");
                            auth.setLang("ES");
                            auth.setTest(true);

                            final Epayco epayco = new Epayco(auth);

                            Card card = new Card();

                            card.setNumber(cardForm.getCardNumber());
                            card.setMonth(cardForm.getExpirationMonth());
                            card.setYear(cardForm.getExpirationYear());
                            card.setCvc(cardForm.getCvv());

                            epayco.createToken(card, new EpaycoCallback() {
                                @Override
                                public void onSuccess(JSONObject data) throws JSONException {
                             //       Toast.makeText(CheckOutCard.this, "el token es"+data.get("id"), Toast.LENGTH_SHORT).show();




                                    EnviarCliente(data.getString("id"));
                                }

                                @Override
                                public void onError(Exception error) {}
                            });






                        }
                    });
                    alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    AlertDialog alertDialog = alertBuilder.create();
                    alertDialog.show();

                } else {
                    Toast.makeText(CheckOutCard.this, "Por favor complete el formulario", Toast.LENGTH_LONG).show();
                }
            }
        });

    }


    public void EnviarCliente(final String id){

        Authentication auth = new Authentication();

        auth.setApiKey("09a20cdc0bb914e3fc296b66a9384edb");
        auth.setPrivateKey("47456095b252b6dd13a8fe07dc5099c1");
        auth.setLang("ES");
        auth.setTest(true);

        final Epayco epayco = new Epayco(auth);

        Client client = new Client();

        client.setTokenId(id);
        client.setName(Common.currentUser.getName());
        client.setEmail(Common.currentUser.getEmail());
        client.setPhone(Common.currentUser.getPhone());
        client.setDefaultCard(true);

        epayco.createCustomer(client, new EpaycoCallback() {
            @Override
            public void onSuccess(JSONObject data) throws JSONException {
               // Toast.makeText(CheckOutCard.this, "es" + data.get("customerId"), Toast.LENGTH_SHORT).show();

                EnviarTransaccion(id, data.getString("customerId"));

            }

            @Override
            public void onError(Exception error) {}
        });
    }

    public void EnviarTransaccion(String tokencard, String customerid){

        Authentication auth = new Authentication();

        auth.setApiKey("09a20cdc0bb914e3fc296b66a9384edb");
        auth.setPrivateKey("47456095b252b6dd13a8fe07dc5099c1");
        auth.setLang("ES");
        auth.setTest(true);

        final Epayco epayco = new Epayco(auth);

        User user = new User();
        String username = user.getName();
        String lastname = user.getLastName();


        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Request");


        Bundle b = new Bundle();
        b = getIntent().getExtras();
        final String address = b.getString("address");
        final String payment = b.getString("payment");
        final String status = b.getString("status");
        final String comment = b.getString("comment");
        final String paymentState = b.getString("paymentState");
        final String order_number = String.valueOf(System.currentTimeMillis());

        total.setText("$"+ payment);


        Charge charge = new Charge();

//Schema
        charge.setTokenCard(tokencard);
        charge.setCustomerId(customerid);

//Required
        charge.setDocType("CC");
        charge.setDocNumber(Common.currentUser.getDocument());
        charge.setName(Common.currentUser.getName());
        charge.setLastName(Common.currentUser.getLastName());
        charge.setEmail(Common.currentUser.getEmail());
        charge.setInvoice("OR-"+order_number);
        charge.setDescription("Orden" + order_number);
        charge.setValue(payment);
        charge.setTax("0");
        charge.setTaxBase(payment);
        charge.setCurrency("COP");
        charge.setDues("36");
        charge.setAddress(address);
        charge.setIp("190.000.000.000");/*This is the client's IP, it is required*/

//Optional


        epayco.createCharge(charge, new EpaycoCallback() {
            @Override
            public void onSuccess(JSONObject data) throws JSONException {
                Toast.makeText(CheckOutCard.this, ""+data.get("factura"), Toast.LENGTH_SHORT).show();
                Request request = new Request(
                        Common.currentUser.getPhone(),
                        Common.currentUser.getName(),
                        address,
                        payment,
                        "0",
                        comment,
                        "Tarjeta",
                        "8:00 a 14:00",
                        "",
                        //falta agregar lat y long desde la peticion
                        cart
                );

                requests.child(order_number)
                        .setValue(request);

                new Database(getBaseContext()).cleanCart();
                sendNotification(order_number);

                dialogloading.dismiss();
                Toast.makeText(CheckOutCard.this, "Gracias, la orden ha sido recibida", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(CheckOutCard.this, Home.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(Exception error) {}
        });
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
                    dataSend.put("message", "Tienes una nueva orden"+order_number);
                    DataMessage dataMessage = new DataMessage(serverToken.getToken(), dataSend);


                    mService.sendNotification(dataMessage)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.body().success == 1){
                                        Toast.makeText(CheckOutCard.this, "Gracias, la orden ha sido recibida", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }else{
                                        Toast.makeText(CheckOutCard.this, "Lo sentimos, no fue posible entregar la orden", Toast.LENGTH_SHORT).show();
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


}
