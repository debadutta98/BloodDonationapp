package com.debadutta98.Blood_Donation;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class StatusActivity extends AppCompatActivity {
    private String phone,blood,phone_number;
    private TextView bloodgroup,views;
   private ImageView profileImage;
  private   ImageButton signout;
  private   Button cancel,share;
    private JsonConverter jsonConverter;
    private List<Request> res1;
   private List<Post> res;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        signout=(ImageButton)findViewById(R.id.signout_status);
        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Paper.init(StatusActivity.this);
                Paper.book().destroy();

                startActivity(new Intent(StatusActivity.this,LoginActivity.class));
            }
        });
        Bundle bundle = getIntent().getExtras();
        profileImage=(ImageView) findViewById(R.id.you_profile);
      blood = bundle.getString("send1");
       phone=bundle.getString("send2");
       phone_number=bundle.getString("send3");
        byte[] byteArray = bundle.getByteArray("send4");
        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        profileImage.setImageBitmap(bmp);
       bloodgroup=(TextView)findViewById(R.id.bloodgrouptype);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.225.200:3000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        jsonConverter=retrofit.create(JsonConverter.class);
        cancel=(Button)findViewById(R.id.cancel);
        views=(TextView)findViewById(R.id.views);
        share=(Button)findViewById(R.id.share);
      // setbloodgroup();
        getProfileImage();
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StatusActivity.this,DonorActivity.class).putExtra("phonenumber",phone));
            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWhatsApp();
            }
        });
getData();
    }

    private void getData() {
        HashMap<String,String> map=new HashMap<>();
        map.put("Id",phone_number);
        Call<List<Request>> call = jsonConverter.checkUser(map);
        call.enqueue(new Callback<List<Request>>() {
            @Override
            public void onResponse(Call<List<Request>> call, Response<List<Request>> response)
            {
                if(response.code()==200) {
                    res1=response.body();
                    if(!TextUtils.isEmpty(res1.get(0).getViews()))
                    {
                        views.setText(res1.get(0).getViews());
                        bloodgroup.setText(res1.get(0).getBlood_group());
                    }
                }
            }
            @Override
            public void onFailure(Call<List<Request>> call, Throwable t) {

            }
        });
    }

    private void openWhatsApp() {
        String smsNumber = phone_number; //without '+'
        try {
            Intent sendIntent = new Intent("android.intent.action.MAIN");
            //sendIntent.setComponent(new ComponentName("com.whatsapp", "com.whatsapp.Conversation"));
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.setType("text/plain");
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Thank you for receive my massage I urgently require"+blood+"blood Please help me.My contact number is"+phone_number);
            sendIntent.putExtra("jid", smsNumber + "@s.whatsapp.net"); //phone number without "+" prefix
            sendIntent.setPackage("com.whatsapp");
            startActivity(sendIntent);
        } catch(Exception e) {
            otherapp();
        }

    }

    private void otherapp() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Thank you for receive my massage I urgently require"+blood+"blood Please help me.My contact number is"+phone);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    private void getProfileImage() {
        HashMap<String,String> params=new HashMap<>();
        //Log.e("tag1",message);
        params.put("phone",phone);
        Call<List<Post>> call=jsonConverter.findUserProfile(params);
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if(!response.isSuccessful())
                {
                    Toast.makeText(StatusActivity.this,"usernotexit",Toast.LENGTH_SHORT).show();
                }
                else {
                    res=response.body();
                    Log.e("tag1",res.get(0).getUrl());
                    if(!TextUtils.isEmpty(res.get(0).getUrl()))
                    {
                        FirebaseStorage storage= FirebaseStorage.getInstance();
                        StorageReference stef=storage.getReference().child("profileImage").child(res.get(0).getUrl());
                        stef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                if(uri!=null) {
                                    Glide.with(StatusActivity.this)
                                            .load(uri.toString()) // image url
                                            .placeholder(R.drawable.placeholder) // any placeholder to load at start
                                            .into(profileImage);
                                }
                            }
                        });
                    }
                    else
                    {
                        if(res.get(0).getGender().equals("male"))
                        {
                            FirebaseStorage storage= FirebaseStorage.getInstance();
                            StorageReference stef=storage.getReference().child("profileImage").child("male.png");
                            stef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    if(uri!=null) {
                                        Glide.with(StatusActivity.this)
                                                .load(uri.toString()) // image url
                                                .placeholder(R.drawable.placeholder) // any placeholder to load at start
                                                .into(profileImage);
                                    }
                                }
                            });
                        }
                        else
                        {
                            FirebaseStorage storage= FirebaseStorage.getInstance();
                            StorageReference stef=storage.getReference().child("profileImage").child("female.png");
                            stef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    if(uri!=null) {
                                        Glide.with(StatusActivity.this)
                                                .load(uri.toString()) // image url
                                                .placeholder(R.drawable.placeholder) // any placeholder to load at start
                                                .into(profileImage);
                                    }
                                }
                            });
                        }
                    }
                }
            }
            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                Toast.makeText(StatusActivity.this,String.valueOf(t),Toast.LENGTH_SHORT).show();
                Log.e("tag2",String.valueOf(t));
            }
        });
    }

}