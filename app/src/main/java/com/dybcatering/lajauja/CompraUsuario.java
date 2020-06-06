package com.dybcatering.lajauja;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.dybcatering.lajauja.Common.Common;
import com.dybcatering.lajauja.Database.Database;
import com.dybcatering.lajauja.Model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.widget.CheckBox;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CompraUsuario extends AppCompatActivity {

 

    String address, comment;
    MaterialEditText  edtName, edtLastName, edtDocument, edtPassword, edtConfirmPassword, edtDate, edtEmail;
    Button btnSignUp, btnSelectDate;

    private DatePickerDialog.OnDateSetListener mDateSetListener;


    CheckBox ckbRemember;

    TextView txtTerminosCondiciones;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compra_usuario);
        
        String llaveusuario = Common.USER_KEY;

         edtName = findViewById(R.id.edtName);
         edtLastName = findViewById(R.id.edtLastName);

         edtPassword = findViewById(R.id.edtPassword);
         edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
         edtDate = findViewById(R.id.edtDate);
         edtEmail = findViewById(R.id.edtEmail);
         edtDocument = findViewById(R.id.edtDocument);
         btnSignUp = findViewById(R.id.btnSignUp);

        ckbRemember = findViewById(R.id.chkRemember);

        btnSelectDate = findViewById(R.id.btnSelectDate);

        txtTerminosCondiciones = findViewById(R.id.txtTerminosCondiciones);

        txtTerminosCondiciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://lajauja.com/terminosycondiciones/")));

            }
        });

        btnSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        CompraUsuario.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                edtDate.setText(year + "/" + month  + "/" + dayOfMonth);
            }
        };

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("User").child(Common.currentUser.getPhone());


        btnSignUp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (ckbRemember.isChecked()) {

                    final ProgressDialog mDialog = new ProgressDialog(CompraUsuario.this);
                    mDialog.setMessage("Por favor espere un momento");
                    mDialog.show();

                    Map<String, Object> personaMap = new HashMap<>();
                    personaMap.put("name", edtName.getText().toString());
                    personaMap.put("lastName", edtLastName.getText().toString());
                    personaMap.put("date", edtDate.getText().toString());
                    personaMap.put("document", edtDocument.getText().toString());
                    personaMap.put("email", edtEmail.getText().toString());
                    personaMap.put("password", edtPassword.getText().toString());

                    table_user.updateChildren(personaMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(CompraUsuario.this, "Registrado satisfactoriamente", Toast.LENGTH_SHORT).show();
                            Common.currentUser.setName(edtName.getText().toString());
                            finish();
                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(CompraUsuario.this, "algo fallo en el registro", Toast.LENGTH_SHORT).show();
                                }
                            });
                }else{
                    Toast.makeText(CompraUsuario.this, "No olvides aceptar nuestros términos y condiciones", Toast.LENGTH_SHORT).show();
                }

            }
        });


                    /*
                        table_user.addListenerForSingleValueEvent(new ValueEventListener() {


                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if ( edtName.getText().toString().isEmpty() || edtLastName.getText().toString().isEmpty() || edtPassword.getText().toString().isEmpty() || edtDate.getText().toString().isEmpty() || edtDocument.getText().toString().isEmpty() ) {

                                    Toast.makeText(CompraUsuario.this, "Por favor complete todos los Campos de Registro", Toast.LENGTH_SHORT).show();

                                    mDialog.dismiss();

                                }else{
                                    if (dataSnapshot.child(Common.currentUser.getPhone()).exists()) {
                                        mDialog.dismiss();
                                        User user = new User(edtName.getText().toString(), edtLastName.getText().toString(), edtDate.getText().toString(), edtDocument.getText().toString(), edtEmail.getText().toString(),  Common.currentUser.getMyDirection(),edtPassword.getText().toString(), "false", Common.currentUser.getPhone());


                                    }
                                }


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                    else{
                        Toast.makeText(CompraUsuario.this, "Verifica tu conexión a Internet", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            });

        }
                         */



}
}
