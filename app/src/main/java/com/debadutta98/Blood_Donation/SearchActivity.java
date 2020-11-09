package com.debadutta98.Blood_Donation;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;


import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
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

public class SearchActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, OnMapReadyCallback {
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private final String TAG = "SearchActivity";
    private final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private boolean permission1 = false;
    private final int CODE = 1;
    private final float DEFAULT_ZERO = 15f;
    //
    private Spinner spinner;
    private Spinner spinner2;
    private String type;
    private String bloodtype;
    private String phone;
    private ImageButton imageButton;
    private Button nearby;
    private ImageView imageView;
    private ImageView imageView1;
   private String dataset1[] = {"Donate", "Require"};
   private String dataset2[] = {"A+", "B+", "O+", "AB+", "A-", "B-", "O-", "AB-"};
    private JsonConverter jsonConverter;
private boolean check=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        spinner = (Spinner) findViewById(R.id.spinner);
        spinner2 = (Spinner) findViewById(R.id.spinner2);
        imageButton = (ImageButton) findViewById(R.id.signout_search);
        nearby = (Button) findViewById(R.id.nearby);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Paper.init(SearchActivity.this);
                Paper.book().destroy();
                check=false;
                startActivity(new Intent(SearchActivity.this, LoginActivity.class));
            }
        });
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.225.200:3000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Bundle bundle = getIntent().getExtras();
        imageView = (ImageView) findViewById(R.id.icon1);
        byte b[] = bundle.getByteArray("send");
        Bitmap bmp = BitmapFactory.decodeByteArray(b, 0, b.length);
        imageView.setImageBitmap(bmp);
        phone = bundle.getString("send1");
        jsonConverter = retrofit.create(JsonConverter.class);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
                R.array.type, R.layout.spinner_layout);
        adapter1.setDropDownViewResource(R.layout.spinner);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.blood, R.layout.spinner_layout);
        adapter2.setDropDownViewResource(R.layout.spinner);
        spinner.setAdapter(adapter1);
        spinner2.setAdapter(adapter2);
        spinner.setOnItemSelectedListener(this);
        spinner2.setOnItemSelectedListener(this);
        imageView1 = (ImageView) findViewById(R.id.search_con);
        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type.equals("Donate")) {
                    findLocationDonor("SELECT blood_group,address FROM user WHERE blood_group=" + "'" + bloodtype + "'");
                } else {
                    findLocationRequire(bloodtype);
                }
            }
        });
        initMap();
        getProfileImage();
        present();
    }

    private void findLocationRequire(String bloodtype) {
        HashMap<String, String> map = new HashMap<>();
        map.put("blood_group", bloodtype);
        Call<List<LocationOfUser>> call = jsonConverter.findRequire(map);
        call.enqueue(new Callback<List<LocationOfUser>>() {
            @Override
            public void onResponse(Call<List<LocationOfUser>> call, Response<List<LocationOfUser>> response) {
                List<LocationOfUser> res2 = response.body();
                //Log.e("this",res2.get(0).getPhone());
                if (response.code() == 200) {
                    getAdress(res2);
                } else if (response.code() == 404) {
                    Toast.makeText(SearchActivity.this, String.valueOf(1), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<LocationOfUser>> call, Throwable t) {
                Toast.makeText(SearchActivity.this, "Check your connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getAdress(List<LocationOfUser> res2) {
        String s = "SELECT blood_group,address FROM user WHERE ";
        String s1 = "phone=";
        String result = "";
        for (int i = 0; i < res2.size(); i++) {
            if (i == 0) {
                result = s + s1 + "'" + res2.get(i).getPhone() + "'";
            } else if (i <= res2.size() - 1) {
                result = result + " " + "OR" + " " + s1 + "'" + res2.get(i).getPhone() + "'";
            }
        }
        if(!result.equals(""))
        findLocationDonor(result);
    }

    private void findLocationDonor(String p) {
        HashMap<String, String> map = new HashMap<>();
        map.put("blood_group", p);
        Call<List<LocationOfUser>> call = jsonConverter.findDonor(map);
        call.enqueue(new Callback<List<LocationOfUser>>() {
            @Override
            public void onResponse(Call<List<LocationOfUser>> call, Response<List<LocationOfUser>> response) {
                List<LocationOfUser> res1 = response.body();
                if (response.code() == 200) {
                    try {
                        Log.e("yuyuyuyuy", String.valueOf(res1));
                        if (res1 != null)
                            init(res1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (response.code() == 404) {

                }
            }

            @Override
            public void onFailure(Call<List<LocationOfUser>> call, Throwable t) {
                Toast.makeText(SearchActivity.this, "Check your connection", Toast.LENGTH_SHORT).show();
            }
        });

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent == spinner) {
            switch (position) {
                case 0:
                    type = String.valueOf(parent.getItemAtPosition(0));
                    break;
                case 1:
                    type = String.valueOf(parent.getItemAtPosition(1));
                    break;
            }
        } else {

            switch (position) {
                case 0:
                    bloodtype = String.valueOf(parent.getItemAtPosition(0));
                    break;
                case 1:
                    bloodtype = String.valueOf(parent.getItemAtPosition(1));
                    break;
                case 2:
                    bloodtype = String.valueOf(parent.getItemAtPosition(2));
                    break;
                case 3:
                    bloodtype = String.valueOf(parent.getItemAtPosition(3));
                    break;
                case 4:
                    bloodtype = String.valueOf(parent.getItemAtPosition(4));
                    break;
                case 5:
                    bloodtype = String.valueOf(parent.getItemAtPosition(5));
                    break;
                case 6:
                    bloodtype = String.valueOf(parent.getItemAtPosition(6));
                    break;
                case 7:
                    bloodtype = String.valueOf(parent.getItemAtPosition(7));
                    break;
            }
            Toast.makeText(SearchActivity.this, bloodtype, Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        type = "Donate";
        bloodtype = "A+";
    }

    private void getProfileImage() {
        HashMap<String, String> params = new HashMap<>();
        //Log.e("tag1",message);
        params.put("phone", phone);
        Call<List<Post>> call = jsonConverter.findUserProfile(params);
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(SearchActivity.this, "usernotexit", Toast.LENGTH_SHORT).show();
                } else {
                    List<Post> res = response.body();
                    Log.e("tag1", res.get(0).getUrl());
                    if (!TextUtils.isEmpty(res.get(0).getUrl())) {
                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        StorageReference stef = storage.getReference().child("profileImage").child(res.get(0).getUrl());
                        stef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                if (uri != null) {
                                    Glide.with(SearchActivity.this)
                                            .load(uri.toString()) // image url
                                            .placeholder(R.drawable.placeholder) // any placeholder to load at start
                                            .into(imageView);
                                }
                            }
                        });
                    } else {
                        if (res.get(0).getGender().equals("male")) {
                            FirebaseStorage storage = FirebaseStorage.getInstance();
                            StorageReference stef = storage.getReference().child("profileImage").child("male.png");
                            stef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    if (uri != null) {
                                        Glide.with(SearchActivity.this)
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
                                        Glide.with(SearchActivity.this)
                                                .load(uri.toString()) // image url
                                                .placeholder(R.drawable.placeholder) // any placeholder to load at start
                                                .into(imageView);
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
                Log.e("tag2", String.valueOf(t));
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(check){
        Intent intent = new Intent(SearchActivity.this, DonorActivity.class);
        intent.putExtra("phonenumber", phone);
        startActivity(intent);}
    }

    private void init(List<LocationOfUser> list1) throws IOException {
        Geocoder geocoder = new Geocoder(SearchActivity.this);
        List<List<Address>> list = new ArrayList<>();
        for (int i = 0; i < list1.size(); i++)
            list.add(geocoder.getFromLocationName(list1.get(i).getAddress(), 1));
        Log.e("tag", String.valueOf(list));
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                Address addrees = list.get(i).get(0);
                moveCamera(new LatLng(addrees.getLatitude(), addrees.getLongitude()), 5f, addrees.getAddressLine(0));
            }

        }

    }

    private void getDevice() {
        Toast.makeText(this, "location", Toast.LENGTH_SHORT).show();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "not allow", Toast.LENGTH_SHORT).show();
                return;
            }
            Task task = fusedLocationProviderClient.getLastLocation();
            task.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Location location = (Location) task.getResult();
                        moveCamera(new LatLng(location.getLatitude(), location.getLongitude()), DEFAULT_ZERO, "Current location");
                    }
                }
            });

        } catch (SecurityException e) {
            Log.e("error900", String.valueOf(e));
        }
    }

    private void moveCamera(LatLng lat, float zoom, String title) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lat, zoom));
        MarkerOptions markerOptions = new MarkerOptions().position(lat).title(title);
        mMap.addMarker(markerOptions);


    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        if (true) {
            getDevice();
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        nearby.setOnClickListener(new View.OnClickListener() {
            String bloodbank = "BloodBank";

            @Override
            public void onClick(View v) {
                Log.d("onClick", "Button is Clicked");
                mMap.clear();
                fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(SearchActivity.this);
                if (ActivityCompat.checkSelfPermission(SearchActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(SearchActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                Task task = fusedLocationProviderClient.getLastLocation();
               // final Location[] location = new Location[1];
                task.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Location location = (Location) task.getResult();
                            String url = getUrl(location.getLatitude(), location.getLongitude(),"Hospital");
                            Object[] DataTransfer = new Object[2];
                            DataTransfer[0] = mMap;
                            DataTransfer[1] = url;
                            Log.d("onClick", url);
                            GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
                            getNearbyPlacesData.execute(DataTransfer);
                            Toast.makeText(SearchActivity.this,"Nearby Bank", Toast.LENGTH_LONG).show();
                        }
                    }
                });


            }
        });
        mMap.setMyLocationEnabled(true);
    }

    private String getUrl(double latitude, double longitude, String nearbyPlace) {

        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + latitude + "," + longitude);
        googlePlacesUrl.append("&radius=" + 500);
        googlePlacesUrl.append("&type=" + nearbyPlace);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=" + "AIzaSyATuUiZUkEc_UgHuqsBJa1oqaODI-3mLs0");
        Log.d("getUrl", googlePlacesUrl.toString());
        return (googlePlacesUrl.toString());
    }

    private void present()
    {
        HashMap<String,String> map=new HashMap<>();
        map.put("Id",phone);
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
                    Toast.makeText(SearchActivity.this,"Data not found", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(SearchActivity.this, String.valueOf(t), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getUser() {
        HashMap<String,String> map=new HashMap<>();
        map.put("Id",phone);
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
                        delete(phone);
                    }
                }
                else if(response.code()==404)
                {

                }
            }
            @Override
            public void onFailure(Call<List<Request>> call, Throwable t) {
                Toast.makeText(SearchActivity.this,"check your connection",Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(SearchActivity.this,"deleted",Toast.LENGTH_SHORT).show();
                }
                else if(response.code()==404)
                {
                    Toast.makeText(SearchActivity.this,"not deleted",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(SearchActivity.this, String.valueOf(t), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
