package com.debadutta98.Blood_Donation;

import android.content.Intent;
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

public class ProfileActivity extends AppCompatActivity {
private ImageView imageView;
private TextView name,bloodtype,gender1,distance1,phone1,address1;
private String phonenumber,url;
private Button btn;
    private JsonConverter jsonConverter;
private ImageButton imageButton;
private ImageView myprofile;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        imageButton=(ImageButton)findViewById(R.id.signout);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Paper.init(ProfileActivity.this);
                Paper.book().destroy();
                startActivity(new Intent(ProfileActivity.this,LoginActivity.class));
            }
        });
        Bundle bundle=getIntent().getExtras();
        String phone=bundle.getString("phone");
        String blood=bundle.getString("blood");
        String gender=bundle.getString("gender");
        url=bundle.getString("url");
        imageView=(ImageView) findViewById(R.id.icon_profile);
        phonenumber=bundle.getString("yourPhone");
        String  address=bundle.getString("address");
        address1=(TextView)findViewById(R.id.address1);
        address1.setText(address);
        String name_user=bundle.getString("name");
        String distance=bundle.getString("distance");
        name=(TextView)findViewById(R.id.name_user);
        distance1=(TextView)findViewById(R.id.distance2);
        phone1=(TextView)findViewById(R.id.phone1);
        phone1.setText(phone);
        bloodtype=(TextView)findViewById(R.id.blood_group);
        gender1=(TextView)findViewById(R.id.gender1);
        name.setText(name_user);
        bloodtype.setText(blood);
        distance1.setText(distance+"km");
        gender1.setText(gender);
        btn=(Button)findViewById(R.id.callnow);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callNow(phone);
            }
        });
        myprofile=(ImageView)findViewById(R.id.myprofile);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.225.200:3000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        jsonConverter=retrofit.create(JsonConverter.class);
        getProfileUser(gender);
        getProfileImage();
    }

    private void getProfileUser(String gender)
    {
        if (!TextUtils.isEmpty(url)) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference stef = storage.getReference().child("profileImage").child(url);
            stef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    if (uri != null) {
                        Glide.with(ProfileActivity.this)
                                .load(uri.toString()) // image url
                                .placeholder(R.drawable.placeholder) // any placeholder to load at start
                                .into(imageView);
                    }
                }
            });
        } else {
            if (gender.equals("male")) {
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference stef = storage.getReference().child("profileImage").child("male.png");
                stef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        if (uri != null) {
                            Glide.with(ProfileActivity.this)
                                    .load(uri.toString()) // image url
                                    .placeholder(R.drawable.placeholder) // any placeholder to load at start
                                    .into(imageView);
                        }
                    }
                });
            } else {
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference stef = storage.getReference().child("profileImage").child("female.png");
                stef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        if (uri != null) {
                            Glide.with(ProfileActivity.this)
                                    .load(uri.toString()) // image url
                                    .placeholder(R.drawable.placeholder) // any placeholder to load at start
                                    .into(imageView);
                        }
                    }
                });
            }
        }
    }

    private void callNow(String p) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + p));
        startActivity(callIntent);
    }
    private void getProfileImage() {
        HashMap<String,String> params=new HashMap<>();
        //Log.e("tag1",message);
        params.put("phone",phonenumber);
        Call<List<Post>> call=jsonConverter.findUserProfile(params);
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if(!response.isSuccessful())
                {
                    Toast.makeText(ProfileActivity.this,"usernotexit",Toast.LENGTH_SHORT).show();
                }
                else {
                    List<Post> res = response.body();
                    Log.e("tag1",res.get(0).getUrl());
                    if(!TextUtils.isEmpty(res.get(0).getUrl()))
                    {
                        FirebaseStorage storage= FirebaseStorage.getInstance();
                        StorageReference stef=storage.getReference().child("profileImage").child(res.get(0).getUrl());
                        stef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                if(uri!=null) {
                                    Glide.with(ProfileActivity.this)
                                            .load(uri.toString()) // image url
                                            .placeholder(R.drawable.placeholder) // any placeholder to load at start
                                            .into(myprofile);
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
                                        Glide.with(ProfileActivity.this)
                                                .load(uri.toString()) // image url
                                                .placeholder(R.drawable.placeholder) // any placeholder to load at start
                                                .into(myprofile);
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
                                        Glide.with(ProfileActivity.this)
                                                .load(uri.toString()) // image url
                                                .placeholder(R.drawable.placeholder) // any placeholder to load at start
                                                .into(myprofile);
                                    }
                                }
                            });
                        }
                    }
                }
            }
            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                // Toast.makeText(StatusActivity.this,String.valueOf(t),Toast.LENGTH_SHORT).show();
                Log.e("tag2",String.valueOf(t));
            }
        });
    }
    private void present()
    {
        HashMap<String,String> map=new HashMap<>();
        map.put("Id",phonenumber);
        Call<Void> call = jsonConverter.present(map);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.code()==200)
                {
                    getUser();
                }
                else if(response.code()==404)
                {
                    Toast.makeText(ProfileActivity.this,"Data not found", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, String.valueOf(t), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getUser() {
        HashMap<String,String> map=new HashMap<>();
        map.put("Id",phonenumber);
        Call<List<Request>> call = jsonConverter.checkUser(map);
        call.enqueue(new Callback<List<Request>>() {
            @Override
            public void onResponse(Call<List<Request>> call, Response<List<Request>> response) {
                if(response.code()==200)
                {
                    List<Request> res1 = response.body();
                    Log.d("try1",String.valueOf(res1.get(0).getEnd_at().substring(0,10)+" "+res1.get(0).getEnd_at().substring(11,19)));
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date1 = new Date();
                    Log.d("date1",String.valueOf(date1));
                    DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                    Date date2 = null;//2020-11-02T18:23:44.000Z
                    try {
                        date2 = format.parse(res1.get(0).getEnd_at().substring(0,10)+" "+res1.get(0).getEnd_at().substring(11,19));
                        Log.d("try",String.valueOf(date2));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Log.d("date",String.valueOf(date2));

                    if(date1.compareTo(date2)<=0)
                    {
                        imageView.setBackgroundResource(R.drawable.circle);
                    }
                    else
                    {
                        imageView.setBackgroundResource(R.drawable.circle_green);
                        delete(phonenumber);
                    }
                }
                else if(response.code()==404)
                {

                }
            }
            @Override
            public void onFailure(Call<List<Request>> call, Throwable t) {
                Toast.makeText(ProfileActivity.this,"check your connection",Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void delete(String message) {
        HashMap<String,String> hashMap=new HashMap<>();
        hashMap.put("Id",message);
        Call<Void> call = jsonConverter.deleteUser(hashMap);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.code()==200)
                {
                    Toast.makeText(ProfileActivity.this,"deleted",Toast.LENGTH_SHORT).show();
                }
                else if(response.code()==404)
                {
                    Toast.makeText(ProfileActivity.this,"not deleted",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, String.valueOf(t), Toast.LENGTH_SHORT).show();
            }
        });
    }
}