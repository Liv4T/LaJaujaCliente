package com.dybcatering.lajauja;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.dybcatering.lajauja.Common.Common;
import com.dybcatering.lajauja.Model.Restaurant;
import com.dybcatering.lajauja.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {


    Button btnRegistro, btnIniciar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnIniciar = findViewById(R.id.btnSignIn);
        btnRegistro = findViewById(R.id.btnSignUp);


        Paper.init(MainActivity.this);


        btnIniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signin = new Intent(MainActivity.this, SignIn.class);
                startActivity(signin);
            }
        });

        btnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signup = new Intent(MainActivity.this, SignUp.class);
                startActivity(signup);
            }
        });

        String user = Paper.book().read(Common.USER_KEY);
        String pwd = Paper.book().read(Common.PWD_KEY);
//        if (user != null && pwd != null){
        if (user != null ){
            if (!user.isEmpty()){
                login(user);
            }
        }


    }

//    private void login(final String phone, final String pwd){
    private void login(final String phone){


        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("User");

        if (Common.IsConnectedToInternet(getBaseContext())) {


            final ProgressDialog mDialog = new ProgressDialog(MainActivity.this);
            mDialog.setMessage("Por favor espere un momento");
            mDialog.show();


            table_user.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.child(phone).exists()) {
                        mDialog.dismiss();
                        User user = dataSnapshot.child(phone).getValue(User.class);
                        user.setPhone(phone);
                        if (user.getPhone().equals(phone)) {
                            Intent intent = new Intent(MainActivity.this, RestaurantList.class);
                            Common.currentUser = user;
                            startActivity(intent);
                            finish();

                        } else {
                            Toast.makeText(MainActivity.this, "No se pudo iniciar sesión", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        mDialog.dismiss();
                        Toast.makeText(MainActivity.this, "El usuario no existe en esta base de datos", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }else{
            Toast.makeText(MainActivity.this, "Verifica tu conexión a Internet", Toast.LENGTH_SHORT).show();
            return;
        }

    }
}
