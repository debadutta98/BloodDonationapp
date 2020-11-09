package com.debadutta98.Blood_Donation;

import com.debadutta98.Blood_Donation.Modle.Users;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface JsonConverter {
    @POST("/users")
    Call<Void> registration(@Body HashMap<String,String> post);
    @POST("/login")
    Call<Void> loginResult(@Body HashMap<String,String> post);
    @GET("/data")
    Call<List<Users>> loginResult();
    @POST("/profile")
    Call<List<Post>> findUserProfile(@Body  HashMap<String,String> post);
    @POST("/newrequest")
    Call<Void> insert(@Body HashMap<String,String> post);
    @POST("/checkuser")
    Call<List<Request>> checkUser(@Body HashMap<String,String> post);
    @POST("/deleteuser")
    Call<Void> deleteUser(@Body HashMap<String,String> post);
    @POST("/update")
    Call<Void> update(@Body HashMap<String,String> post);
    @POST("/present")
    Call<Void> present(@Body HashMap<String,String> post);

    @POST("/findDonor")
    Call<List<LocationOfUser>> findDonor(@Body HashMap<String,String> post);
    @POST("/findRequire")
    Call<List<LocationOfUser>> findRequire(@Body HashMap<String,String> post);
    @POST("/forgetpassword")
    Call<Void> updateUser(@Body HashMap<String,String> post);
}
