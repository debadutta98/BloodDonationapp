package com.debadutta98.Blood_Donation;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.debadutta98.Blood_Donation.Modle.Users;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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

public class DonorActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
 private ImageView profileImage;
    private List<Users> get;
    private List<Request> res1;
    private ImageButton signout;
    private List<Post> res;
    private ImageView profile;
    private ImageButton search;
    private Button request;
    private Variable v1=new Variable();
    private String message;
    private boolean b;
    private JsonConverter jsonConverter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor);
        signout=(ImageButton)findViewById(R.id.menu_signout);
        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Paper.init(DonorActivity.this);
                Paper.book().destroy();
                startActivity(new Intent(DonorActivity.this,LoginActivity.class));
            }
        });
        profileImage=(ImageView)findViewById(R.id.icon_user);
recyclerView=(RecyclerView)findViewById(R.id.rclview);
recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.225.200:3000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
         Bundle bundle = getIntent().getExtras();
         message = bundle.getString("phonenumber");
        jsonConverter=retrofit.create(JsonConverter.class);
        //profile=(ImageView)findViewById(R.id.icon_user);
        search=findViewById(R.id.search);
        getProfileImage();
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(DonorActivity.this,SearchActivity.class);
                Bitmap bitmap = ((BitmapDrawable) profileImage.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imageInByte = baos.toByteArray();
                intent.putExtra("send",imageInByte);
                intent.putExtra("send1",message);
                startActivity(intent);
            }
        });
        request=(Button)findViewById(R.id.btnRequest);
        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequest();
            }
        });
        //Log.e("p1",String.valueOf(b));
        getUserdata();
        getUser();
    }


    private void sendRequest() {
        String[] listItems = {"A+", "B+", "O+", "AB+", "A-","B-","O-","AB-"};

        AlertDialog.Builder builder = new AlertDialog.Builder(DonorActivity.this);
        builder.setTitle("Choose Blood group");

        int checkedItem = 0; //this will checked the item when user open the dialog
        builder.setSingleChoiceItems(listItems, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                v1.setData(listItems[which]);

            }
        });

        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                present();
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void insertRcord(String listItem) {
        HashMap<String, String> params = new HashMap<String, String>();
       // Toast.makeText(DonorActivity.this,String.valueOf(getUser(message)),Toast.LENGTH_SHORT).show();

            params.put("Id", message);
            params.put("phone", message);
            params.put("blood_group", listItem);
            params.put("views", "0");
            Calendar calendar = Calendar.getInstance();
            params.put("start_at", returnDate(String.valueOf(calendar.getTime())));
            calendar.add(Calendar.HOUR_OF_DAY, +24);
            params.put("end_at", returnDate(String.valueOf(calendar.getTime())));
            Call<Void> call = jsonConverter.insert(params);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.code() == 200) {
                        profileImage.setBackgroundResource(R.drawable.circle);
                        Toast.makeText(DonorActivity.this, "Succssful", Toast.LENGTH_SHORT).show();
                    } else if (response.code() == 404) {
                        Toast.makeText(DonorActivity.this, "error?????????????????", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(DonorActivity.this, String.valueOf(t), Toast.LENGTH_SHORT).show();
                }
            });
    }
    private void present()
    {
        HashMap<String,String> map=new HashMap<>();
        map.put("Id",message);
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
                    insertRcord(v1.getData());
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(DonorActivity.this, String.valueOf(t), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getUser(){
        HashMap<String,String> map=new HashMap<>();
        map.put("Id",message);
        Call<List<Request>> call = jsonConverter.checkUser(map);
        call.enqueue(new Callback<List<Request>>() {
            @Override
            public void onResponse(Call<List<Request>> call, Response<List<Request>> response) {
                if(response.code()==200)
                {
                   // Toast.makeText(DonorActivity.this,"hiiiiiiiiiiiiii",Toast.LENGTH_SHORT).show();
                    res1=response.body();
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
                    profileImage.setBackgroundResource(R.drawable.circle);
                    }
                   else
                    {
                       delete();
                    }
                }
                else if(response.code()==404)
                {

                }
            }
            @Override
            public void onFailure(Call<List<Request>> call, Throwable t) {

                Toast.makeText(DonorActivity.this, String.valueOf(t), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void delete() {
        HashMap<String,String> hashMap=new HashMap<>();
        hashMap.put("Id",message);
        Call<Void> call = jsonConverter.deleteUser(hashMap);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
               if(response.code()==200)
               {
                   Toast.makeText(DonorActivity.this,"deleted",Toast.LENGTH_SHORT);
               }
               else if(response.code()==404)
               {
                   Toast.makeText(DonorActivity.this,"not deleted",Toast.LENGTH_SHORT);
               }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(DonorActivity.this, String.valueOf(t), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private String returnDate(String s) {
        String string[]=s.split(" ");
        String m="";
        switch (string[1]) {
            case "Jan":
                m="1";
                break;
            case "Fab":
                m="2";
                break;
            case "Mar":
                m="3";
                break;
            case "Apr":
                m="4";
                break;
            case "May":
                m="5";
                break;
            case "Jun":
                m="6";
                break;
            case "Jul":
                m="7";
                break;
            case "Aug":
                m="8";
                break;
            case "Sep":
                m="9";
                break;
            case "Oct":
                m="10";
                break;
            case "Nov":
                m="11";
                break;
            case "Dec":
                m="12";
                break;
        }
        String date=string[string.length-1]+"-"+m+"-"+string[2]+" "+string[3];
        return date;
    }

    private void getProfileImage() {
        HashMap<String,String> params=new HashMap<>();
        //Log.e("tag1",message);
        params.put("phone",message);
        Call<List<Post>> call=jsonConverter.findUserProfile(params);
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if(!response.isSuccessful())
                {
                    Toast.makeText(DonorActivity.this,"error!!!!!!!!!!!!!!",Toast.LENGTH_SHORT).show();
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
                                    Glide.with(DonorActivity.this)
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
                                        Glide.with(DonorActivity.this)
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
                                        Glide.with(DonorActivity.this)
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
Toast.makeText(DonorActivity.this,String.valueOf(t),Toast.LENGTH_SHORT).show();
Log.e("tag2",String.valueOf(t));
            }
        });
    }

    private void getUserdata()
    {
        //List<Users> get;
        Call<List<Users>> call=jsonConverter.loginResult();
        call.enqueue(new Callback<List<Users>>() {
            @Override
            public void onResponse(Call<List<Users>> call, Response<List<Users>> response) {
               if(!response.isSuccessful())
               {
                   Toast.makeText(DonorActivity.this,"error",Toast.LENGTH_SHORT).show();
               }
               else
               {
                   List<Users> get1 = response.body();
                   for(int i=0;i<get1.size();i++)
                   {
                       if(get1.get(i).getPhone().equals(message))
                       {
                           get1.remove(i);
                       }
                   }
                   if(get1!=null)
                   recyclerView.setAdapter(new MyListAdapter(get1,DonorActivity.this,message,profileImage.getBackground(),jsonConverter,profileImage));
               }
            }
            @Override
            public void onFailure(Call<List<Users>> call, Throwable t) {

            }
        });
    }

}