package com.debadutta98.Blood_Donation;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.debadutta98.Blood_Donation.Modle.Users;
import com.debadutta98.Blood_Donation.Prevalent.Privalent;
import com.esotericsoftware.kryo.NotNull;


import java.util.HashMap;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {
private EditText email,password;
private CheckBox forget;
private Button login,signup;
private TextView forget_password;
    private JsonConverter jsonConverter;
    @Override
    protected void onCreate(@NonNull Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email=(EditText)findViewById(R.id.user_email);
        password=(EditText)findViewById(R.id.user_password);
        login=(Button)findViewById(R.id.login_botton);
        signup=(Button)findViewById(R.id.sign_up);
        forget=(CheckBox)findViewById(R.id.checkbox);
        forget_password=(TextView)findViewById(R.id.forget_password);
         Paper.init(this);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.225.200:3000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        jsonConverter=retrofit.create(JsonConverter.class);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkuser();
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoAcoount();
            }
        });
        forget_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,FrogetPassword.class));
            }
        });
    }

    private void gotoAcoount() {
        Intent intent=new Intent(LoginActivity.this,AccountActivity.class);
        startActivity(intent);
    }

    private void checkuser() {
        String Phone,Password;
        Phone=email.getText().toString();
        Password=password.getText().toString();
            if (TextUtils.isEmpty(Password)) {
                    Toast.makeText(this, "enter valid password", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty( Phone) || Phone.length() != 10) {
                    Toast.makeText(this, "enter valid phonenumber", Toast.LENGTH_SHORT).show();
            } else {

                AllowAccesstoAccount(Phone,Password);
            }
    }



    private void AllowAccesstoAccount(final String phone, final String password) {
        if(forget.isChecked())
        {
            Paper.book().write(Privalent.userPhoneKey,phone);
            Paper.book().write(Privalent.userPasswordKey,password);
            HashMap<String,String> params=new HashMap<>();
            params.put("phone",phone);
            params.put("pass",password);
            Call<Void> call=jsonConverter.loginResult(params);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if(response.code()==200)
                    {
                        startActivity(new Intent(LoginActivity.this,DonorActivity.class).putExtra("phonenumber",phone));
                        Toast.makeText(LoginActivity.this,"Successful",Toast.LENGTH_SHORT).show();
                    }
                    else if(response.code()==404)
                    {
                        setProgressAlert("phone number not exit");
                        startActivity(new Intent(LoginActivity.this,AccountActivity.class));
                        Paper.book().destroy();
                    }
                }
                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    setProgressAlert("Check your connection");
                    Toast.makeText(getApplicationContext(),String.valueOf(t),Toast.LENGTH_SHORT).show();
                    Paper.book().destroy();
                }
            });
        }
        else
        {
            Toast.makeText(LoginActivity.this,"plz click remember me",Toast.LENGTH_SHORT).show();
        }
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
                        startActivity(new Intent(LoginActivity.this, LoginActivity.class));
                        finish();
                    }
                })
                .show();
    }

}