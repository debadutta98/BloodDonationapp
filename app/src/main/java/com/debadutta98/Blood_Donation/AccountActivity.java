package com.debadutta98.Blood_Donation;
import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentProvider;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.drm.DrmManagerClient;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.storage.StorageManager;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AccountActivity extends AppCompatActivity {
    private EditText name,Bloodtype,Gender,Address,Phonenumber,Password;
    private Uri filePath;
    private final int PICK_CAM=75;
    private final int PICK_IMAGE_REQUEST = 71;
    private String link;
    private String imageurl;
private Button save;
private ImageButton camera;
private ImageView imageView;
private Retrofit retrofit;
  private   FirebaseStorage storage;
   private StorageReference storageReference;
private JsonConverter jsonConverter;
    @Override
    protected void onCreate(@NonNull Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        imageView=(ImageView)findViewById(R.id.icon);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.225.200:3000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        jsonConverter=retrofit.create(JsonConverter.class);
        save=(Button)findViewById(R.id.save);
        name=(EditText)findViewById(R.id.name_user);
        Bloodtype=(EditText)findViewById(R.id.bloodtype);
        Gender=(EditText)findViewById(R.id.gender);
        Address=(EditText)findViewById(R.id.address);
        Phonenumber=(EditText)findViewById(R.id.phone);
        Password=(EditText)findViewById(R.id.password);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AccountActivity.this,"enter",Toast.LENGTH_SHORT).show();
                //Toast.makeText(AccountActivity.this,imageurl,Toast.LENGTH_LONG).show();
//                Log.d("link2",imageurl);
                String n,blood,gender,address,phonenumber,password;
                n=name.getText().toString();
                blood=Bloodtype.getText().toString().toUpperCase().trim();
                gender=Gender.getText().toString().toUpperCase().trim();
                address=Address.getText().toString();
                phonenumber=Phonenumber.getText().toString().trim();
                password=Password.getText().toString().trim();

                if(saveuserdata(n,blood,gender,address,phonenumber,password))
                {
                    Log.d("enter","main");
                    Toast.makeText(getApplicationContext(),"valid",Toast.LENGTH_SHORT);
                    validatePhoneNumber(n,phonenumber,blood,gender,password,address);
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"invalid",Toast.LENGTH_SHORT);
                    startActivity(new Intent(AccountActivity.this,AccountActivity.class));
                }
            }
        });
    }

    private void chooseImage() {

        AlertDialog alertDialog = new AlertDialog.Builder(this)
//set icon
                .setIcon(R.drawable.camera)
//set title
                .setTitle("Choose Picture")
//set message
//set positive button
                .setPositiveButton("Gallery", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //set what would happen when positive button is clicked
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"),PICK_IMAGE_REQUEST);
                    }
                })
                .setNegativeButton("Camera", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(ContextCompat.checkSelfPermission(AccountActivity.this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED)
                        {
                            ActivityCompat.requestPermissions(AccountActivity.this,new String[]{Manifest.permission.CAMERA},101);
                        }
                        else
                        {
                            openCamera();
                        }
                    }
                })
                .show();
    }

    private void openCamera() {
        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,102);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==101)
        {
            if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED)
            {
                openCamera();
            }
            else
            {

            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Toast.makeText(AccountActivity.this, String.valueOf(data.getData()), Toast.LENGTH_LONG).show();
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
                uploadImage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if(requestCode==102 && resultCode == RESULT_OK){
            filePath = data.getData();
            Bitmap bitmap =(Bitmap)data.getExtras().get("data");
            imageView.setImageBitmap(bitmap);
           filePath = getImageUri(getApplicationContext(), bitmap);
            uploadImage();
        }
    }
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
    private void uploadImage() {

        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            link=UUID.randomUUID().toString();
            StorageReference ref = storageReference.child("profileImage/"+ link);
            Toast.makeText(AccountActivity.this, link,Toast.LENGTH_LONG).show();
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();

                            Toast.makeText(AccountActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(AccountActivity.this, "Failed"+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
    }
    private boolean saveuserdata(String n, String blood, String gender, String address, String phonenumber, String password) {
        List<String> list=new ArrayList<>();
        list.add("A+");
        list.add("B+");
        list.add("AB+");
        list.add("O+");
        list.add("AB-");
        list.add("O-");
        list.add("A-");
        list.add("B-");
        if((TextUtils.isEmpty(n)))
        {
            return false;
        }
        else if(TextUtils.isEmpty(blood) || (!(list.contains(blood))))
        {
            return false;
        }
        else if(TextUtils.isEmpty(gender))
        {
            return false;
        }
        else if(TextUtils.isEmpty(address))
        {
            return false;
        }
        else if(TextUtils.isEmpty(phonenumber) || phonenumber.length()!=10)
        {
            return false;
        }
        else if(TextUtils.isEmpty(password))
        {
            return false;
        }
        else
        {
            return true;
        }
    }
    private void validatePhoneNumber(final String n, final String phonenumber, final String blood, final String gender, final String password, final String address)
    {
            setProgressDialog();
            HashMap<String,String> params=new HashMap<>();
            params.put("Id",phonenumber);
            params.put("blood_group",blood);
            params.put("username",n);
            params.put("gender",gender);
            params.put("phone",phonenumber);
            params.put("pass",password);
            params.put("address",address);
            if(link!=null)
            {
                params.put("Url", link);
            }
            else
            {
                if(gender.toLowerCase().equals("male"))
                {
                    params.put("Url","male.png");
                }
                else
                {
                    params.put("Url","female.png");
                }
            }
        Call<Void> call=jsonConverter.registration(params);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.code()==200)
                {
                   startActivity(new Intent(AccountActivity.this,LoginActivity.class));
                    Toast.makeText(AccountActivity.this,"Successful",Toast.LENGTH_SHORT).show();
                }
                else if(response.code()==404)
                {
                    setProgressAlert("phone number already exit");
                    Toast.makeText(AccountActivity.this,"Phone number already exit",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                setProgressAlert("Check your connection");
Toast.makeText(getApplicationContext(),String.valueOf(t),Toast.LENGTH_SHORT).show();

            }
        });
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
                        startActivity(new Intent(AccountActivity.this, LoginActivity.class));
                        finish();
                    }
                })
                .show();
    }

    public void setProgressDialog () {

        int llPadding = 30;
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.setPadding(llPadding, llPadding, llPadding, llPadding);
        ll.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams llParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        ll.setLayoutParams(llParam);

        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setIndeterminate(true);
        progressBar.setPadding(0, 0, llPadding, 0);
        progressBar.setLayoutParams(llParam);

        llParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        TextView tvText = new TextView(this);
        tvText.setText("Loading ...");
        tvText.setTextColor(Color.parseColor("#000000"));
        tvText.setTextSize(20);
        tvText.setLayoutParams(llParam);

        ll.addView(progressBar);
        ll.addView(tvText);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setView(ll);

        AlertDialog dialog = builder.create();
        dialog.show();
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(dialog.getWindow().getAttributes());
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setAttributes(layoutParams);
        }
    }

}