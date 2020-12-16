package com.example.barberapps.Rest;

import com.example.barberapps.GetPost.GetBarber;
import com.example.barberapps.GetPost.GetPesanan;
import com.example.barberapps.GetPost.GetUser;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiInterface {

    @FormUrlEncoded
    @POST("wsRegisterCustomer.php")
    Call<GetUser> registerCustomer( @Field("nama") String nama,
                                    @Field("email") String email,
                                    @Field("hp") String hp,
                                    @Field("alamat") String alamat,
                                    @Field("password") String password,
                                    @Field("jenisUser") String jenisUser);

    @FormUrlEncoded
    @POST("wsRegisterKepster.php")
    Call<GetUser> registerKepster( @Field("nama") String nama,
                                    @Field("email") String email,
                                    @Field("hp") String hp,
                                    @Field("alamat") String alamat,
                                    @Field("password") String password,
                                   @Field("jenisUser") String jenisUser,
                                   @Field("barbershop") String barbershop);

    @FormUrlEncoded
    @POST("wsRegisterBarber.php")
    Call<GetUser> registerBarber(@Field("namaBarber") String namaBarber,
                                   @Field("email") String email,
                                   @Field("hp") String hp,
                                   @Field("alamat") String alamat,
                                   @Field("password") String password,
                                   @Field("lat") String lat,
                                   @Field("lng") String lng);

    @FormUrlEncoded
    @POST("wsLogin.php")
    Call<GetUser> postLogin(@Field("email") String email,
                            @Field("password") String password,
                            @Field("jenisUser") String jenisUser);

    @FormUrlEncoded
    @POST("wsGetKepsterBarber.php")
    Call<GetUser> getKepsterBarber(@Field("barbershop") String barbershop);

    @FormUrlEncoded
    @POST("wsGetDataBarbershop.php")
    Call<GetBarber> getDataBarber(@Field("namaBarber") String namaBarber);

    @FormUrlEncoded
    @POST("wsGetBarbershopTerdekat.php")
    Call<GetBarber> getBarberTerdekat(@Field("lat") String lat,
                                    @Field("lng") String lng);

    @FormUrlEncoded
    @POST("wsPostPesanan.php")
    Call<GetPesanan> postPesanan(@Field("customer") String customer,
                                 @Field("barbershop") String barbershop,
                                 @Field("kepster") String kepster,
                                 @Field("tanggal") String tanggal,
                                 @Field("waktu") String waktu,
                                 @Field("jenisPesanan") String jenisPesanan,
                                 @Field("status") String status,
                                 @Field("lat") String lat,
                                 @Field("lng") String lng,
                                 @Field("alamat") String alamat);

    @FormUrlEncoded
    @POST("wsUpdatePesanan.php")
    Call<GetPesanan> updatePesanan(@Field("id") String id,
                                   @Field("status") String status);

    @FormUrlEncoded
    @POST("wsCekPesanan.php")
    Call<GetPesanan> getCekPesanan(@Field("jenisUser") String jenisUser,
                                   @Field("user") String user,
                                   @Field("status") String status);

    @FormUrlEncoded
    @POST("wsGetPesananKepster.php")
    Call<GetPesanan> getWaitingList(@Field("kepster") String kepster,
                                    @Field("status") String status);


}