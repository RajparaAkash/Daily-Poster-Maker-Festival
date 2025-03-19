package com.festival.dailypostermaker.Api;

import com.festival.dailypostermaker.Model.AddBusinessDetails.AddBusiness;
import com.festival.dailypostermaker.Model.Bg.Bg;
import com.festival.dailypostermaker.Model.CategoryName.CategoryName;
import com.festival.dailypostermaker.Model.CategoryWiseData.CategoryWiseData;
import com.festival.dailypostermaker.Model.Feedback.Feedback;
import com.festival.dailypostermaker.Model.GetUserData.UserData;
import com.festival.dailypostermaker.Model.PostExport;
import com.festival.dailypostermaker.Model.UpdateBusinessDetails.UpdateBusinessDetails;
import com.festival.dailypostermaker.Model.UpdatePersonalDetails.UpdatePersonalDetails;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiEndpoints {

    @POST("v2getUserPersonalPostData")
    @FormUrlEncoded
    Call<CategoryName> getUserPersonalPostData(
            @Field("m_key") String mKey,
            @Field("version_code") int versionCode,
            @Field("page") int page,
            @Field("post_type") int postType,
            @Field("lan_id") int lanId
    );

    @POST("getCategoryWiseData")
    @FormUrlEncoded
    Call<CategoryWiseData> getCategoryWiseData(
            @Field("category_id") int categoryId,
            @Field("m_key") String mKey,
            @Field("version_code") int versionCode,
            @Field("page") int page
    );


    @POST("v2getCategoryWiseData")
    @FormUrlEncoded
    Call<CategoryWiseData> getUpdatedCategoryWiseData(
            @Field("category_id") int categoryId,
            @Field("m_key") String mKey,
            @Field("version_code") int versionCode,
            @Field("page") int page,
            @Field("post_type") int postType,
            @Field("lan_id") int lanId
    );

    @POST("getAssetsData")
    @FormUrlEncoded
    Call<Bg> getAssetsData(
            @Field("version_code") int versionCode,
            @Field("m_key") String mKey
    );

    @POST("getUserData")
    @FormUrlEncoded
    Call<UserData> getUserData(
            @Field("device_id") String deviceId,
            @Field("fb_token") String fbToken,
            @Field("m_key") String mKey,
            @Field("version_code") int versionCode
    );

    @Multipart
    @POST("createdUserProfile")
    Call<UpdatePersonalDetails> updateUserProfile(
            @Part("version_code") RequestBody versionCode,
            @Part("device_id") RequestBody deviceId,
            @Part MultipartBody.Part profile,
            @Part("name") RequestBody name,
            @Part("mobile") RequestBody mobile,
            @Part("email") RequestBody email,
            @Part("about_us") RequestBody aboutUs,
            @Part("instagram") RequestBody instagram,
            @Part("youtube") RequestBody youtube,
            @Part("facebook") RequestBody facebook,
            @Part("twitter") RequestBody twitter,
            @Part("website") RequestBody website,
            @Part("is_logo_change") RequestBody isLogoChange
    );


    @Multipart
    @POST("addBusinessProfile")
    Call<AddBusiness> addBusinessProfile(
            @Part("version_code") RequestBody versionCode,
            @Part("device_id") RequestBody deviceId,
            @Part MultipartBody.Part profile,
            @Part("name") RequestBody name,
            @Part("email") RequestBody email,
            @Part("designation") RequestBody designation,
            @Part("address") RequestBody address,
            @Part("whatsapp") RequestBody whatsapp,
            @Part("instagram") RequestBody instagram,
            @Part("youtube") RequestBody youtube,
            @Part("facebook") RequestBody facebook,
            @Part("twitter") RequestBody twitter,
            @Part("website") RequestBody website
    );

    @Multipart
    @POST("updateBusinessProfile/{businessId}")
    Call<UpdateBusinessDetails> updateBusinessProfile(
            @Path("businessId") int businessId,
            @Part("version_code") RequestBody versionCode,
            @Part("device_id") RequestBody deviceId,
            @Part MultipartBody.Part profile,
            @Part("name") RequestBody name,
            @Part("email") RequestBody email,
            @Part("designation") RequestBody designation,
            @Part("address") RequestBody address,
            @Part("whatsapp") RequestBody whatsapp,
            @Part("instagram") RequestBody instagram,
            @Part("youtube") RequestBody youtube,
            @Part("facebook") RequestBody facebook,
            @Part("twitter") RequestBody twitter,
            @Part("website") RequestBody website
    );

    @POST("v2getUserFeedBack")
    @FormUrlEncoded
    Call<Feedback> sendUserFeedback(
            @Field("feedback") String feedback,
            @Field("m_key") String mKey,
            @Field("version_code") int versionCode,
            @Field("user_id") String userId
    );

    @POST("V2postExport")
    @FormUrlEncoded
    Call<PostExport> postExportCount(@Field("post_id") int postId, @Field("user_id") String user_id);
}
