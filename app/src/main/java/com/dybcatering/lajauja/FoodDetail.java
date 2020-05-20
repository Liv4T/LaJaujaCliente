package com.dybcatering.lajauja;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.dybcatering.lajauja.Common.Common;
import com.dybcatering.lajauja.Database.Database;
import com.dybcatering.lajauja.Model.Food;
import com.dybcatering.lajauja.Model.Order;
import com.dybcatering.lajauja.Model.Rating;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import info.hoang8f.widget.FButton;

public class FoodDetail extends AppCompatActivity implements RatingDialogListener {

    TextView food_name, food_price, food_description;
    ImageView food_image;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton  btnRating;
    CounterFab btnCart;
    ElegantNumberButton numberButton;
    RatingBar ratingBar;


    String foodId= "";

    FirebaseDatabase database;
    DatabaseReference foods;
    DatabaseReference ratingTbl;
    FButton showComment;

    Food currentFood;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        database = FirebaseDatabase.getInstance();
        foods =database.getReference("Restaurants").child(Common.restaurantSelected)
                .child("detail").child("Foods");
        ratingTbl = database.getReference("Rating");
        numberButton = findViewById(R.id.number_button);
        btnCart = findViewById(R.id.btnCart);
        btnRating = findViewById(R.id.btnRating);
        ratingBar = findViewById(R.id.ratingBar);
        showComment = findViewById(R.id.btnShowComments);

        btnRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRatingDialog();
            }
        });

        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //llamar al alertdialog

                switch (currentFood.getMenuId()){

                    case "02":
                    case "-M6aTPZIb53x8i2iet1M":
                    case "-M6aF2b92oplFbm9CP6K":
                    case "-M6aTJuBmOi17CV9HazM":
                    case "-M6aTTHKqecVewwNmwSK":
                        showAlertSopas();
                        break;
                    case "03":
                        showAlertArroces();
                        break;
                    case "04":
                    case "05":
                        showAlertPastas();
                        break;
                    case "06":
                    case "-M6aTY_nXWgK574gCgCt":

                        //cocina de pipe ->
                        agregarCarrito();
                        break;


                }

/*
                new Database(getBaseContext()).addToCart(new Order(
                        foodId,
                        currentFood.getFood(),
                        numberButton.getNumber(),
                        currentFood.getPrice(),
                        currentFood.getDiscount(),
                        currentFood.getImage()
                        //aqui iria el acompañamiento
                ));
                Toast.makeText(FoodDetail.this, "Agregado Al Carrito de Compras ", Toast.LENGTH_SHORT).show();
 */

            }
        });

        btnCart.setCount(new Database(this).getCountCart());

        showComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FoodDetail.this, ShowComment.class);
                intent.putExtra(Common.INTENT_FOOD_ID, foodId);
                startActivity(intent);
            }
        });

        food_description = findViewById(R.id.food_description);
        food_name = findViewById(R.id.food_name);
        food_price = findViewById(R.id.food_price);
        food_image = findViewById(R.id.img_food );

        collapsingToolbarLayout = findViewById(R.id.collapsin);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.CollapsedAppbar);


        if (getIntent() != null)
            foodId = getIntent().getStringExtra("FoodId");
        if (!foodId.isEmpty()){
            getDetailFood(foodId);
            getRatingFood(foodId);
        }
    }

    private void agregarCarrito() {
        new Database(getBaseContext()).addToCart(new Order(
                foodId,
                currentFood.getFood(),
                numberButton.getNumber(),
                currentFood.getPrice(),
                currentFood.getDiscount(),
                currentFood.getImage(),
                "Sin Acompañamiento"

        ));
        Toast.makeText(FoodDetail.this, "Agregado Al Carrito de Compras ", Toast.LENGTH_SHORT).show();

    }

    private void showAlertSopas() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(FoodDetail.this);
        alertDialog.setTitle("Información");

        LayoutInflater inflater = this.getLayoutInflater();
        View order_address_comment = inflater.inflate(R.layout.sopas_item, null);

        final RadioButton radiopapas = order_address_comment.findViewById(R.id.radiopapas);
        final RadioButton radioplatanitos = order_address_comment.findViewById(R.id.radioplatanitos);
        final RadioButton radioarracacha = order_address_comment.findViewById(R.id.radioarracacha);
        final RadioButton radioarrozblanco = order_address_comment.findViewById(R.id.radioarrozblanco);
        final RadioButton radioarrozintegral = order_address_comment.findViewById(R.id.radioarrozintegral);



        alertDialog.setView(order_address_comment);
        alertDialog.setIcon(R.drawable.ic_star_full_48dp);


        alertDialog.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (radiopapas.isChecked() ) {

                    String precioActual = currentFood.getPrice();
                    int foo = Integer.parseInt(precioActual);
                    int total = foo + 3000;
                    String totalString = Integer.toString(total);

                    new Database(getBaseContext()).addToCart(new Order(
                            foodId,
                            currentFood.getFood(),
                            numberButton.getNumber(),
                            totalString,
                            currentFood.getDiscount(),
                            currentFood.getImage(),
                            "Papas Chips"

                    ));
                    Toast.makeText(FoodDetail.this, "Agregado Al Carrito de Compras ", Toast.LENGTH_SHORT).show();

                } else if (radioplatanitos.isChecked()){

                    String precioActual = currentFood.getPrice();
                    int foo = Integer.parseInt(precioActual);
                    int total = foo + 3000;
                    String totalString = Integer.toString(total);

                    new Database(getBaseContext()).addToCart(new Order(
                            foodId,
                            currentFood.getFood(),
                            numberButton.getNumber(),
                            totalString,
                            currentFood.getDiscount(),
                            currentFood.getImage(),
                            "Platanitos Chips"

                    ));
                    Toast.makeText(FoodDetail.this, "Agregado Al Carrito de Compras ", Toast.LENGTH_SHORT).show();


                }else if ( radioarracacha.isChecked()){
                    String precioActual = currentFood.getPrice();
                    int foo = Integer.parseInt(precioActual);
                    int total = foo + 3000;
                    String totalString = Integer.toString(total);

                    new Database(getBaseContext()).addToCart(new Order(
                            foodId,
                            currentFood.getFood(),
                            numberButton.getNumber(),
                            totalString,
                            currentFood.getDiscount(),
                            currentFood.getImage(),
                            "Arracacha Chips"

                    ));
                    Toast.makeText(FoodDetail.this, "Agregado Al Carrito de Compras ", Toast.LENGTH_SHORT).show();


                }else if ( radioarrozintegral.isChecked()){
                    String precioActual = currentFood.getPrice();
                    int foo = Integer.parseInt(precioActual);
                    int total = foo + 3000;
                    String totalString = Integer.toString(total);

                    new Database(getBaseContext()).addToCart(new Order(
                            foodId,
                            currentFood.getFood(),
                            numberButton.getNumber(),
                            totalString,
                            currentFood.getDiscount(),
                            currentFood.getImage(),
                            "Arroz Integral"

                    ));
                    Toast.makeText(FoodDetail.this, "Agregado Al Carrito de Compras ", Toast.LENGTH_SHORT).show();


                } else if (radioarrozblanco.isChecked()){
                   String precioActual = currentFood.getPrice();
                   int foo = Integer.parseInt(precioActual);
                   int total = foo+2500;
                   String totalString = Integer.toString(total);

                   new Database(getBaseContext()).addToCart(new Order(
                           foodId,
                           currentFood.getFood(),
                           numberButton.getNumber(),
                           totalString,
                           currentFood.getDiscount(),
                           currentFood.getImage(),
                           "Arroz Blanco"

                   ));
                   Toast.makeText(FoodDetail.this, "Agregado Al Carrito de Compras ", Toast.LENGTH_SHORT).show();

               }

                dialog.dismiss();
            }
        });


        alertDialog.show();

    }

    private void showAlertArroces() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(FoodDetail.this);
        alertDialog.setTitle("Información");

        LayoutInflater inflater = this.getLayoutInflater();
        View order_address_comment = inflater.inflate(R.layout.sopas_item, null);

        final RadioButton radiopapas = order_address_comment.findViewById(R.id.radiopapas);
        final RadioButton radioplatanitos = order_address_comment.findViewById(R.id.radioplatanitos);
        final RadioButton radioarracacha = order_address_comment.findViewById(R.id.radioarracacha);
        final RadioButton radioarrozblanco = order_address_comment.findViewById(R.id.radioarrozblanco);
        final RadioButton radioarrozintegral = order_address_comment.findViewById(R.id.radioarrozintegral);



        alertDialog.setView(order_address_comment);
        alertDialog.setIcon(R.drawable.ic_star_full_48dp);


        alertDialog.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (radiopapas.isChecked() ) {

                    String precioActual = currentFood.getPrice();
                    int foo = Integer.parseInt(precioActual);
                    int total = foo + 3000;
                    String totalString = Integer.toString(total);

                    new Database(getBaseContext()).addToCart(new Order(
                            foodId,
                            currentFood.getFood(),
                            numberButton.getNumber(),
                            totalString,
                            currentFood.getDiscount(),
                            currentFood.getImage(),
                            "Papas Chips"

                    ));
                    Toast.makeText(FoodDetail.this, "Agregado Al Carrito de Compras ", Toast.LENGTH_SHORT).show();

                } else if (radioplatanitos.isChecked()){

                    String precioActual = currentFood.getPrice();
                    int foo = Integer.parseInt(precioActual);
                    int total = foo + 3000;
                    String totalString = Integer.toString(total);

                    new Database(getBaseContext()).addToCart(new Order(
                            foodId,
                            currentFood.getFood(),
                            numberButton.getNumber(),
                            totalString,
                            currentFood.getDiscount(),
                            currentFood.getImage(),
                            "Platanitos Chips"

                    ));
                    Toast.makeText(FoodDetail.this, "Agregado Al Carrito de Compras ", Toast.LENGTH_SHORT).show();


                }else if ( radioarracacha.isChecked()){
                    String precioActual = currentFood.getPrice();
                    int foo = Integer.parseInt(precioActual);
                    int total = foo + 3000;
                    String totalString = Integer.toString(total);

                    new Database(getBaseContext()).addToCart(new Order(
                            foodId,
                            currentFood.getFood(),
                            numberButton.getNumber(),
                            totalString,
                            currentFood.getDiscount(),
                            currentFood.getImage(),
                            "Arracacha Chips"

                    ));
                    Toast.makeText(FoodDetail.this, "Agregado Al Carrito de Compras ", Toast.LENGTH_SHORT).show();


                }else if ( radioarrozintegral.isChecked()){
                    String precioActual = currentFood.getPrice();
                    int foo = Integer.parseInt(precioActual);
                    int total = foo + 3000;
                    String totalString = Integer.toString(total);

                    new Database(getBaseContext()).addToCart(new Order(
                            foodId,
                            currentFood.getFood(),
                            numberButton.getNumber(),
                            totalString,
                            currentFood.getDiscount(),
                            currentFood.getImage(),
                            "Arroz Integral"

                    ));
                    Toast.makeText(FoodDetail.this, "Agregado Al Carrito de Compras ", Toast.LENGTH_SHORT).show();


                } else if (radioarrozblanco.isChecked()){
                    String precioActual = currentFood.getPrice();
                    int foo = Integer.parseInt(precioActual);
                    int total = foo+2500;
                    String totalString = Integer.toString(total);

                    new Database(getBaseContext()).addToCart(new Order(
                            foodId,
                            currentFood.getFood(),
                            numberButton.getNumber(),
                            totalString,
                            currentFood.getDiscount(),
                            currentFood.getImage(),
                            "Arroz Blanco"

                    ));
                    Toast.makeText(FoodDetail.this, "Agregado Al Carrito de Compras ", Toast.LENGTH_SHORT).show();

                }

                dialog.dismiss();
            }
        });


        alertDialog.show();

    }

    private void showAlertPastas(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(FoodDetail.this);
        alertDialog.setTitle("Información");

        LayoutInflater inflater = this.getLayoutInflater();
        View order_pastas = inflater.inflate(R.layout.pastas_item, null);

        final RadioButton radiobolonesa = order_pastas.findViewById(R.id.radiobolonesa);
        final RadioButton radiocarbonara = order_pastas.findViewById(R.id.radiocarbonara);
        final RadioButton radionapolitana = order_pastas.findViewById(R.id.radionapolitana);
        final RadioButton radioverduras = order_pastas.findViewById(R.id.radioverduras);
        final RadioButton radiopesto = order_pastas.findViewById(R.id.radiopesto);



        alertDialog.setView(order_pastas);
        alertDialog.setIcon(R.drawable.ic_star_full_48dp);

        alertDialog.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (radiobolonesa.isChecked()){
                    new Database(getBaseContext()).addToCart(new Order(
                            foodId,
                            currentFood.getFood(),
                            numberButton.getNumber(),
                            currentFood.getPrice(),
                            currentFood.getDiscount(),
                            currentFood.getImage(),
                            "Boloñesa 250gr"

                    ));
                    Toast.makeText(FoodDetail.this, "Agregado Al Carrito de Compras ", Toast.LENGTH_SHORT).show();

                }else if (radiocarbonara.isChecked()){
                    new Database(getBaseContext()).addToCart(new Order(
                            foodId,
                            currentFood.getFood(),
                            numberButton.getNumber(),
                            currentFood.getPrice(),
                            currentFood.getDiscount(),
                            currentFood.getImage(),
                            "Carbonara 250gr"

                    ));
                    Toast.makeText(FoodDetail.this, "Agregado Al Carrito de Compras ", Toast.LENGTH_SHORT).show();

                }else if (radionapolitana.isChecked()){
                    new Database(getBaseContext()).addToCart(new Order(
                            foodId,
                            currentFood.getFood(),
                            numberButton.getNumber(),
                            currentFood.getPrice(),
                            currentFood.getDiscount(),
                            currentFood.getImage(),
                            "Napolitana 250gr"

                    ));
                    Toast.makeText(FoodDetail.this, "Agregado Al Carrito de Compras ", Toast.LENGTH_SHORT).show();

                }else if (radioverduras.isChecked()){
                    new Database(getBaseContext()).addToCart(new Order(
                            foodId,
                            currentFood.getFood(),
                            numberButton.getNumber(),
                            currentFood.getPrice(),
                            currentFood.getDiscount(),
                            currentFood.getImage(),
                            "Verduras 250gr"

                    ));
                    Toast.makeText(FoodDetail.this, "Agregado Al Carrito de Compras ", Toast.LENGTH_SHORT).show();

                } else if (radiopesto.isChecked()){
                    new Database(getBaseContext()).addToCart(new Order(
                            foodId,
                            currentFood.getFood(),
                            numberButton.getNumber(),
                            currentFood.getPrice(),
                            currentFood.getDiscount(),
                            currentFood.getImage(),
                            "Pesto 250gr"

                    ));
                    Toast.makeText(FoodDetail.this, "Agregado Al Carrito de Compras ", Toast.LENGTH_SHORT).show();

                }
                dialog.dismiss();

            }
        });
        alertDialog.show();
    }



    private void getRatingFood(String foodId) {
        Query foodRating = ratingTbl.orderByChild("foodId").equalTo(foodId);

        foodRating.addValueEventListener(new ValueEventListener() {
            int count=0, sum=0;

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    Rating item = postSnapshot.getValue(Rating.class);
                    sum+=Integer.parseInt(item.getRateValue());
                    count++;
                }
                if (count !=0) {
                float average = sum/count;
                ratingBar.setRating(average);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showRatingDialog() {
        new AppRatingDialog.Builder()
                .setPositiveButtonText("Enviar")
                .setNegativeButtonText("Cancelar")
                .setNoteDescriptions(Arrays.asList("Muy Malo", "No Está Bien", "Aceptable", "Muy Bueno", "Excelente"))
                .setDefaultRating(5)
                .setTitle("Califica esta comida")
                .setDescription("Por favor seleccione la calificación según su retroalimentación")
                .setTitleTextColor(R.color.colorPrimary)
                .setDescriptionTextColor(R.color.colorPrimary)
                .setHint("Por favor escriba su comentario aqui...")
                .setHintTextColor(R.color.colorAccent)
                .setCommentTextColor(android.R.color.white)
                .setCommentBackgroundColor(R.color.colorPrimaryDark)
                .setWindowAnimation(R.style.RatingDialogFadeAnim)
                .create(FoodDetail.this)
                .show();
    }

    private void getDetailFood(String foodId) {
        foods.child(foodId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentFood = dataSnapshot.getValue(Food.class);

                Picasso.with(getBaseContext()).load(currentFood.getImage())
                        .into(food_image);

                collapsingToolbarLayout.setTitle(currentFood.getFood());

                food_price.setText(currentFood.getPrice());

                food_name.setText(currentFood.getFood());

                food_description.setText(currentFood.getDescription());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onNegativeButtonClicked() {

    }

    @Override
    public void onPositiveButtonClicked(int value, @NotNull String comments) {
            final Rating rating = new Rating(Common.currentUser.getPhone(),
                    foodId,
                    String.valueOf(value),
                    comments);

            ratingTbl.push()
                    .setValue(rating)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(FoodDetail.this, "Gracias por enviar su opinion", Toast.LENGTH_SHORT).show();

                        }
                    });

            /*
            ratingTbl.child(Common.currentUser.getPhone()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(Common.currentUser.getPhone()).exists()){
                        ratingTbl.child(Common.currentUser.getPhone()).removeValue();

                        ratingTbl.child(Common.currentUser.getPhone()).setValue(rating);
                    }else{
                        ratingTbl.child(Common.currentUser.getPhone()).setValue(rating);
                    }
                    Toast.makeText(FoodDetail.this, "Gracias por su envio", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
             */

    }
}
