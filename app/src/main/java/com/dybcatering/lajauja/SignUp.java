package com.dybcatering.lajauja;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import com.dybcatering.lajauja.Common.Common;
import com.dybcatering.lajauja.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.Calendar;

public class SignUp extends AppCompatActivity {

    MaterialEditText edtPhone, edtName, edtLastName, edtDocument, edtDirection, edtPassword, edtDate;
    Button btnSignUp, btnSelectDate;

    private DatePickerDialog.OnDateSetListener mDateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        edtName = findViewById(R.id.edtName);
        edtLastName = findViewById(R.id.edtLastName);

        edtPassword = findViewById(R.id.edtPassword);
        edtPhone = findViewById(R.id.edtPhone);
        edtDate = findViewById(R.id.edtDate);
        edtDocument = findViewById(R.id.edtDocument);
        edtDirection = findViewById(R.id.edtDirecion);

        btnSignUp = findViewById(R.id.btnSignUp);

        btnSelectDate = findViewById(R.id.btnSelectDate);

        btnSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        SignUp.this,
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
                edtDate.setText(year+"/"+month+"/"+dayOfMonth);
            }
        };




        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("User");


        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Common.IsConnectedToInternet(getBaseContext())) {
                    final ProgressDialog mDialog = new ProgressDialog(SignUp.this);
                    mDialog.setMessage("Por favor espere un momento");
                    mDialog.show();

                    table_user.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (edtPhone.getText().toString().isEmpty() || edtName.getText().toString().isEmpty() || edtLastName.getText().toString().isEmpty() || edtPassword.getText().toString().isEmpty() || edtDate.getText().toString().isEmpty() || edtDocument.getText().toString().isEmpty() || edtDirection.getText().toString().isEmpty() ) {

                                Toast.makeText(SignUp.this, "Por favor complete todos los Campos de Registro", Toast.LENGTH_SHORT).show();

                                mDialog.dismiss();

                            }else{
                                if (dataSnapshot.child(edtPhone.getText().toString()).exists()) {
                                    mDialog.dismiss();
                                    Toast.makeText(SignUp.this, "Este usuario ya existe", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    mDialog.dismiss();
                                    User user = new User(edtName.getText().toString(), edtLastName.getText().toString(), edtDate.getText().toString(), edtDocument.getText().toString(), edtDirection.getText().toString(), edtPassword.getText().toString());
                                    table_user.child(edtPhone.getText().toString()).setValue(user);
                                    Toast.makeText(SignUp.this, "Registrado satisfactoriamente", Toast.LENGTH_SHORT).show();
                                    finish();
                                }    
                            }
                            

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                else{
                    Toast.makeText(SignUp.this, "Verifica tu conexión a Internet", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

    }
}
