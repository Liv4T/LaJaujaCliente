package com.dybcatering.lajauja;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.dybcatering.lajauja.Common.Common;
import com.dybcatering.lajauja.Database.Database;
import com.dybcatering.lajauja.Model.Request;
import com.dybcatering.lajauja.Model.Token;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.rengwuxian.materialedittext.MaterialEditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import io.paperdb.Paper;

public class Home extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    FirebaseDatabase database;
    DatabaseReference category;

    TextView txtFull;
    CounterFab fab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

       // database= FirebaseDatabase.getInstance();
       // category = database.getReference("Category");


        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
          //      Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
            //            .setAction("Action", null).show();
                Intent cartIntent = new Intent(Home.this, Cart.class);
                startActivity(cartIntent);

            }
        });

        fab.setCount(new Database(this).getCountCart());
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow, R.id.sign_out)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        View headerView = navigationView.getHeaderView(0);
        txtFull = headerView.findViewById(R.id.txtFull);
        Paper.init(Home.this);
        String user = Paper.book().read(Common.USER_KEY);
        txtFull.setText(user);


     //   recyclerView_menu = findViewById(R.id.recycler_menu);

       // recyclerView_menu.setHasFixedSize(true);
       // layoutManager = new LinearLayoutManager(this);
       // recyclerView_menu.setLayoutManager(layoutManager);

   //     updateToken(FirebaseInstanceId.getInstance().getToken());
        
        showAlertPopUp();
    }

    private void showAlertPopUp() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Home.this);
        alertDialog.setTitle("Información");

        LayoutInflater inflater = this.getLayoutInflater();
        View order_address_comment = inflater.inflate(R.layout.pop_up_alert, null);

        alertDialog.setView(order_address_comment);
        alertDialog.setIcon(R.drawable.ic_star_full_48dp);


        alertDialog.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });


        alertDialog.show();


    }

    @Override
    protected void onResume() {
        super.onResume();
        fab.setCount(new Database(this).getCountCart());
    }

    private void updateToken(String token) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference tokens = db.getReference("Tokens");
        Token data = new Token(token, false);
        tokens.child(Common.currentUser.getPhone()).setValue(data);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);

        return true;
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {


        switch (item.getItemId()){
            case R.id.close:

                      Paper.init(Home.this);
                    Paper.book().destroy();
                    Intent signIn = new Intent(Home.this, SignIn.class);
                      signIn.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(signIn);

                return true;
            case R.id.settings:
                showSettingDialog();
                return true;

            case R.id.buscar:
                Intent search = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(search);

                return true;

            default:
                return super.onOptionsItemSelected(item);

        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void showSettingDialog(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Home.this);
        alertDialog.setTitle("Cambiar configuración");

        LayoutInflater inflater = LayoutInflater.from(this);
        View layout_home = inflater.inflate(R.layout.setting_layout, null);

        final CheckBox ckb = layout_home.findViewById(R.id.ckb_sub_news);

        Paper.init(this);
        String isSuscribe = Paper.book().read("sub_new");

        if (isSuscribe == null || TextUtils.isEmpty(isSuscribe) || isSuscribe.equals("false")){
            ckb.setChecked(false);
        }else{
            ckb.setChecked(true);
        }

        alertDialog.setView(layout_home);

        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                if (ckb.isChecked()){
                    FirebaseMessaging.getInstance().subscribeToTopic(Common.topicName);
                    Paper.book().write("sub_new", "true");
                }else{
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(Common.topicName);
                    Paper.book().write("sub_new", "false");
                }

            }
        });
        alertDialog.show();

    }

}
