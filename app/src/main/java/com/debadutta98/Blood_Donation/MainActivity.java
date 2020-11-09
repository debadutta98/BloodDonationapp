package com.debadutta98.Blood_Donation;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.debadutta98.Blood_Donation.Prevalent.Privalent;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;


import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
   private Button continuebtn;
   private Button exit;
    private JsonConverter jsonConverter;
    private final String TAG = "SearchActivity";
    private final int ERROR=9001;
    @Override
    protected void onCreate(@NonNull Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        continuebtn=(Button)findViewById(R.id.continue_app);
        exit=(Button)findViewById(R.id.exit_app);
       if(isServiceOk()) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://192.168.225.200:3000/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            jsonConverter = retrofit.create(JsonConverter.class);
            Paper.init(this);
            gotoLogin();
continuebtn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        gotoLogin();
    }
});
exit.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        finish();
    }
});
        }
    }
public boolean isServiceOk()
{
    int avialable= GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);
    if(avialable== ConnectionResult.SUCCESS)
    {
        return true;
    }
    else if(GoogleApiAvailability.getInstance().isUserResolvableError(avialable))
    {
        Dialog dialog=GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this,avialable,ERROR);
        dialog.show();
    }
    else
    {
        Toast.makeText(this,"youcan't make",Toast.LENGTH_SHORT).show();
    }
    return false;
}

    private void gotoLogin() {
        String phone=Paper.book().read(Privalent.userPhoneKey);
        String pass=Paper.book().read(Privalent.userPasswordKey);
        Toast.makeText(MainActivity.this,pass+" "+phone,Toast.LENGTH_LONG);
        if(!(TextUtils.isEmpty(pass)) && !(TextUtils.isEmpty(phone)))
        {
            HashMap<String,String> params=new HashMap<>();
            params.put("phone",phone);
            params.put("pass",pass);
            Call<Void> call=jsonConverter.loginResult(params);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if(response.code()==200)
                    {
                        startActivity(new Intent(MainActivity.this,DonorActivity.class).putExtra("phonenumber",phone));
                        Toast.makeText(MainActivity.this,"Successful",Toast.LENGTH_SHORT).show();
                    }
                    else if(response.code()==404)
                    {
                        setProgressAlert("phone number not exit");
                        startActivity(new Intent(MainActivity.this,LoginActivity.class));
                       // Paper.book().destroy();
                    }
                }
                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    setProgressAlert("Check your connection");
                    Toast.makeText(getApplicationContext(),String.valueOf(t),Toast.LENGTH_SHORT).show();
                   // Paper.book().destroy();
                }
            });
        }
        else
        {
        startActivity(new Intent(this,LoginActivity.class).putExtra("login",1));}
    }
    public void setProgressAlert (String s)
    {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
//set icon
                .setIcon(android.R.drawable.ic_dialog_alert)
//set title
                .setTitle("Invalid SignUp")
//set message
                .setMessage(s)
//set positive button
                .setPositiveButton("TryAgain", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //set what would happen when positive button is clicked
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        finish();
                    }
                })
                .show();
    }


}