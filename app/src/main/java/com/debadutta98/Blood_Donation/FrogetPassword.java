package com.debadutta98.Blood_Donation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.debadutta98.Blood_Donation.Modle.Users;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FrogetPassword extends AppCompatActivity {
private EditText phone,pass,conpass;
private TextView gotosignup;
private Button submit;
private JsonConverter jsonConverter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_froget_password);
        phone=(EditText)findViewById(R.id.phone_number);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.225.200:3000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Bundle bundle = getIntent().getExtras();
        jsonConverter=retrofit.create(JsonConverter.class);
        pass=(EditText)findViewById(R.id.password_user);
        conpass=(EditText)findViewById(R.id.confirm_password);
        gotosignup=(TextView)findViewById(R.id.goto_sign_up);
        submit=(Button)findViewById(R.id.submit_button);
        gotosignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FrogetPassword.this,AccountActivity.class));
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatepassword();
            }
        });
    }

    private void updatepassword() {
        String p=phone.getText().toString();
        String password=pass.getText().toString();
        String conform=conpass.getText().toString();
        if(check(p,password,conform))
        {
        updateuserpassword(p,password,conform);
        }
    }

    private void updateuserpassword(String p, String password, String conform) {
        HashMap<String,String> params=new HashMap<>();
        params.put("phone",p);
        params.put("pass",conform);
        Call<Void> call=jsonConverter.updateUser(params);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.code()==200)
                {
                    startActivity(new Intent(FrogetPassword.this,LoginActivity.class));
                }
                else if(response.code()==404)
                {
                    Toast.makeText(FrogetPassword.this,"user not found",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(FrogetPassword.this,AccountActivity.class));
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
Toast.makeText(FrogetPassword.this,"check your connection",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean check(String p, String password, String conform) {
        if(TextUtils.isEmpty(p) || p.length()!=10)
        {  return false;}
        else if(TextUtils.isEmpty(password))
        {return false;}
        else if(TextUtils.isEmpty(conform))
        { return false;}
        else
        {
            if(password.equals(conform))
            {
                return true;
            }
            else
            {
                return false;
            }
        }
    }

}