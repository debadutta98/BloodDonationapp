package com.debadutta98.Blood_Donation;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.debadutta98.Blood_Donation.Modle.Users;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.Holder> {
    List<Users> users;
Context context;
Drawable b;
float distance1;
String phonenumber;
ImageView profileImage;
JsonConverter jsonConverter;
    public MyListAdapter(List<Users> users, Context context, String phonenumber, Drawable b, JsonConverter jsonConverter,ImageView imageView) {
        this.users = users;
        this.context=context;
        this.phonenumber=phonenumber;
        this.b=b;
        this.jsonConverter=jsonConverter;
        this.profileImage=imageView;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(parent.getContext());
        View view=layoutInflater.inflate(R.layout.mylist,parent,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
            holder.userTitle.setText(users.get(position).getUsername());
            holder.bloodgroup.setText(users.get(position).getBlood_group());
            holder.imageView.setTag(position);
        try {
            getLocation(holder,position);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(users.get(position).getUrl()) && users.get(position).getGender().equals("male")) {
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference stef = storage.getReference().child("profileImage").child("male.png");
                stef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        if (uri != null) {
                            Glide.with(context)
                                    .load(uri.toString()) // image url
                                    .placeholder(R.drawable.placeholder) // any placeholder to load at start
                                    .into(holder.imageView);
                        }
                    }
                });
            } else if (TextUtils.isEmpty(users.get(position).getUrl()) && users.get(position).getGender().equals("female")) {
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference stef = storage.getReference().child("profileImage").child("female.png");
                stef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        if (uri != null) {
                            Glide.with(context)
                                    .load(uri.toString()) // image url
                                    .placeholder(R.drawable.placeholder) // any placeholder to load at start
                                    .into(holder.imageView);
                        }
                    }
                });
            } else {
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference stef = storage.getReference().child("profileImage").child(users.get(position).getUrl());
                stef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        if (uri != null) {
                            Glide.with(context)
                                    .load(uri.toString()) // image url
                                    .placeholder(R.drawable.placeholder) // any placeholder to load at start
                                    .into(holder.imageView);
                        }
                    }
                });
            }
            holder.viewprofile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
context.startActivity(new Intent(context,ProfileActivity.class).putExtra("phone",users.get(position).getPhone()).putExtra("gender",users.get(0).getGender()).putExtra("address",users.get(position).getAddress()).putExtra("blood",users.get(position).getBlood_group()).putExtra("url",users.get(position).getUrl()).putExtra("yourPhone",phonenumber).putExtra("name",users.get(position).getUsername()).putExtra("distance",String.valueOf(distance1)));
                }
            });
            holder.contact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + users.get(position).getPhone()));
                    context.startActivity(callIntent);
                }
            });
        present(users.get(0).getPhone(),holder,position);
    }

    private void getLocation(Holder holder, int position) throws IOException {
        Geocoder geocoder=new Geocoder(context);
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        List<Address> list=geocoder.getFromLocationName(users.get(position).getAddress(),1);
        Log.e("tag",String.valueOf(list));
        LatLng lat=null;
        if(list.size()>0)
        {
                Address addrees=list.get(0);
            lat= new LatLng(addrees.getLatitude(),addrees.getLongitude());
        }
            try {

                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                {
                    return;
                }
                Task task = fusedLocationProviderClient.getLastLocation();
                LatLng finalLat = lat;
                task.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Location location = (Location) task.getResult();
                            if(finalLat !=null)
                            { float[] distance = new float[1];
                                location.distanceBetween( new LatLng(location.getLatitude(), location.getLongitude()).latitude,new LatLng(location.getLatitude(), location.getLongitude()).longitude, finalLat.latitude,finalLat.longitude,distance);
                                DecimalFormat newFormat = new DecimalFormat("##");
                                if(distance[0]>=1000)
                                {
                                    distance[0]=distance[0]/1000;
                                    holder.distance.setText(String.valueOf(newFormat.format(distance[0]))+" km");
                                }
                                else
                                {
                                    holder.distance.setText(String.valueOf(newFormat.format(distance[0]))+" m");
                                }
                                 distance1 = distance[0];
                            }
                            }
                    }
                });

            } catch (SecurityException e) {
                Log.e("error900", String.valueOf(e));
            }

    }



    private void updateViews(String sum,int position) {
        HashMap<String,String> map=new HashMap<>();
        map.put("Id",users.get(position).getPhone());
        map.put("views",sum);
        Call<Void> call = jsonConverter.update(map);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.code()==200)
                {
                    Toast.makeText(context,"updated",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(context,"not updated",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }
    private void present(String phone_number,Holder holder,int position)
    {
        HashMap<String,String> map=new HashMap<>();
        map.put("Id",phone_number);
        Call<Void> call = jsonConverter.present(map);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.code()==200)
                {
                   getUser(holder,position);
                }
                else if(response.code()==404)
                {
                    Toast.makeText(context,"Data not found", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, String.valueOf(t), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getUser(Holder holder,int position) {
        HashMap<String,String> map=new HashMap<>();
        map.put("Id",users.get(position).getPhone());
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
                        if(!TextUtils.isEmpty(res1.get(0).getViews()))
                        {
                            holder.imageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    BigInteger bigIntegerStr = new BigInteger(res1.get(0).getViews());
                                    BigInteger a = new BigInteger("1");
                                    BigInteger sum = bigIntegerStr.add(a);
                                    updateViews(String.valueOf(sum), position);
                                    String blood = users.get(position).getBlood_group();
                                    String phone_number = users.get(position).getPhone();
                                    String phone = phonenumber;
                                    Bitmap bitmap = ((BitmapDrawable) profileImage.getDrawable()).getBitmap();
                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                    byte[] imageInByte = baos.toByteArray();
                                    context.startActivity(new Intent(context, StatusActivity.class).putExtra("send1", blood).putExtra("send2", phone).putExtra("send3", phone_number).putExtra("send4",imageInByte));

                                }
                            });
                        }
                        holder.imageView.setBackgroundResource(R.drawable.circle);
                    }
                    else
                    {
                       // b1[0] =true;
                        holder.imageView.setBackgroundResource(R.drawable.circle_green);
                        delete(users.get(position).getPhone());
                    }
                }
                else if(response.code()==404)
                {
Toast.makeText(context,"not found",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<List<Request>> call, Throwable t) {
                Toast.makeText(context,"check your connection",Toast.LENGTH_SHORT).show();
                Toast.makeText(context, String.valueOf(t), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(context,"deleted",Toast.LENGTH_SHORT).show();
                }
                else if(response.code()==404)
                {
                    Toast.makeText(context,"not deleted",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, String.valueOf(t), Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public int getItemCount() {
        if(users==null)
        return 0;
        else
            return users.size();
    }

    class Holder extends RecyclerView.ViewHolder{
TextView userTitle,distance,bloodgroup;
ImageView imageView;
Button viewprofile,contact;
    public Holder(@NonNull View itemView) {
        super(itemView);
        userTitle=(TextView)itemView.findViewById(R.id.title);
        distance=(TextView)itemView.findViewById(R.id.subtitle);
        bloodgroup=(TextView)itemView.findViewById(R.id.bloodtype);
        imageView=(ImageView)itemView.findViewById(R.id.icon);
        viewprofile=(Button)itemView.findViewById(R.id.view_profile);
        contact=(Button)itemView.findViewById(R.id.contact);
    }
}
}