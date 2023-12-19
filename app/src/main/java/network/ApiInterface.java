package network;

import com.google.gson.JsonObject;

import models.AddEventParticipantsResponse;
import models.AdsResponseData;
import models.BlockedPeopleResponse;
import models.CategoriesListResponse;
import models.ContentPageResponse;
import models.CreateEventResponse;
import models.CreateGroupResponse;
import models.DeleteBlockedMemberResponse;
import models.EventDetailsResponseData;
import models.EventMediaResponse;
import models.EventPeopleResponseData;
import models.EventsCommentsResponseData;
import models.EventsParticipantsResponse;
import models.EventsResponse;
import models.FollowUserResponseData;
import models.FollowersResponse;
import models.ForgotResponse;
import models.GeneralResponse;
import models.GroupAddResponse;
import models.GroupListResponse;
import models.GroupNotMembersResponse;
import models.GroupUpdateResponse;
import models.InvitationResponse;
import models.LanguageSaveResponse;
import models.MapLocationResponse;
import models.NotificationListResponse;
import models.NotificationReadResponse;
import models.PeopleDataResponse;
import models.PhoneVerifyResponse;
import models.PostCommentResponseData;
import models.ProfileEditResponse;
import models.PushNotifyResponse;
import models.SettingsResponseData;
import models.SettingsSaveResponse;
import models.SignInResponse;
import models.SignUpResponse;
import models.UserListResponse;
import models.UserProfileResponse;
import models.UserSelfProfileResponse;
import models.VerifyCodeResponse;
import models.VideoUploadResponse;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {

    @FormUrlEncoded
    @POST("user/register")
    Call<SignUpResponse> registerUser(@Field("fullname") String fullname, @Field("email") String email,
                                      @Field("password") String password, @Field("password_confirmation") String password_confirmation,
                                      @Field("mobile_number") String mobile_number, @Field("username") String username);

    @FormUrlEncoded
    @POST("user/login")
    Call<SignInResponse> login(@Field("email") String email, @Field("password") String password);

    @FormUrlEncoded
    @POST("user/forgot_password")
    Call<ForgotResponse> forgotPass(@Field("email") String email);

    @FormUrlEncoded
    @POST("user/user_verification")
    Call<PhoneVerifyResponse> phoneVerify(@Field("mobile_number") String mobile_number);

    @FormUrlEncoded
    @POST("user/match_sms_token")
    Call<VerifyCodeResponse> verifyCode(@Field("user_id") String user_id, @Field("sms_token") String sms_token);

    @GET("event/getEventInfo")
    Call<CategoriesListResponse> categoriesList();

    @POST("event/list{path}")
    Call<EventsResponse> getEventsData(@Path("path") String Path, @Query("page") int page, @Body JsonObject params);

    @GET("groups")
    Call<GroupListResponse> getGroupList();

    @POST("groups")
    Call<CreateGroupResponse> createGroup(@Body JsonObject params);

    @POST("update_group")
    Call<GroupUpdateResponse> updateGroup(@Body JsonObject params);

    @POST("delete_group")
    Call<GroupListResponse> deleteGroup(@Body JsonObject params);

    @POST("group_add_users")
    Call<GroupAddResponse> addUserToGroup(@Body JsonObject users);

    @POST("group_user_list")
    Call<UserListResponse> getUserList(@Body JsonObject params);

    @POST("block_group_user")
    Call<GroupListResponse> blockMemberCall(@Body JsonObject params);

    @POST("delete_group_user")
    Call<GroupListResponse> deleteMember(@Body JsonObject params);

    @GET("list_block_group_users")
    Call<BlockedPeopleResponse> getGroupBlockedList();

    @POST("remove_block_group_users")
    Call<DeleteBlockedMemberResponse> deleteBlockedMember(@Body JsonObject params);

    @POST("event/addParticipants")
    Call<EventsParticipantsResponse> getEventsParticipantsData(@Body JsonObject params);

    @Multipart
    @POST("event/create")
    Call<CreateEventResponse> createEvent(@Part("event_title") RequestBody event_title, @Part("event_description") RequestBody event_description,
                                          @Part("event_start_date") RequestBody event_start_date, @Part("event_start_time") RequestBody event_start_time,
                                          @Part("event_location") RequestBody event_location, @Part("event_venue_lat") RequestBody event_venue_lat,
                                          @Part("event_venue_long") RequestBody event_venue_long, @Part("event_details") RequestBody event_detail, @Part("ecat_id") RequestBody ecat_id,
                                          @Part("event_iscomments_allowed") RequestBody event_iscomments_allowed, @Part("event_isprivate") RequestBody event_isprivate,
                                          @Part("event_groups") RequestBody event_groups, @Part("event_users") RequestBody event_users,
                                          @Part("event_pincode") RequestBody event_pincode, @Part("event_isApproval") RequestBody event_isApproval_required,
                                          @Part("event_isPincode") RequestBody event_isPincode_required,
                                          @Part("event_cover_photo\"; filename=\"IMG_Gcam.jpg\" ") RequestBody images);

    @Multipart
    @POST("event/edit")
    Call<CreateEventResponse> editEvent(@Part("event_id") RequestBody event_id, @Part("event_title") RequestBody event_title,
                                        @Part("event_description") RequestBody event_description,
                                        @Part("event_start_date") RequestBody event_start_date, @Part("event_start_time") RequestBody event_start_time,
                                        @Part("event_location") RequestBody event_location, @Part("event_venue_lat") RequestBody event_venue_lat,
                                        @Part("event_venue_long") RequestBody event_venue_long, @Part("event_details") RequestBody event_detail, @Part("ecat_id") RequestBody ecat_id,
                                        @Part("event_iscomments_allowed") RequestBody event_iscomments_allowed, @Part("event_isprivate") RequestBody event_isprivate,
                                        @Part("event_pincode") RequestBody event_pincode, @Part("event_isApproval") RequestBody event_isApproval_required,
                                        @Part("event_isPincode") RequestBody event_isPincode_required);

    @Multipart
    @POST("event/edit")
    Call<CreateEventResponse> editEvent(@Part("event_id") RequestBody event_id, @Part("event_title") RequestBody event_title,
                                        @Part("event_description") RequestBody event_description,
                                        @Part("event_start_date") RequestBody event_start_date, @Part("event_start_time") RequestBody event_start_time,
                                        @Part("event_location") RequestBody event_location, @Part("event_venue_lat") RequestBody event_venue_lat,
                                        @Part("event_venue_long") RequestBody event_venue_long, @Part("event_details") RequestBody event_detail, @Part("ecat_id") RequestBody ecat_id,
                                        @Part("event_iscomments_allowed") RequestBody event_iscomments_allowed, @Part("event_isprivate") RequestBody event_isprivate,
                                        @Part("event_pincode") RequestBody event_pincode, @Part("event_isApproval") RequestBody event_isApproval_required,
                                        @Part("event_isPincode") RequestBody event_isPincode_required,
                                        @Part("event_cover_photo\"; filename=\"IMG_Gcam.jpg\" ") RequestBody images);

    @GET("profile{path}")
    Call<UserProfileResponse> getUserProfileData(@Path("path") String path, @Query("id") String userId);

    @GET("event/list_participants{path}")
    Call<EventPeopleResponseData> getEventPeopleList(@Path("path") String path, @Query("event_id") String userId, @Query("page") int page);

    @GET("event/event_detail{path}")
    Call<EventDetailsResponseData> getEventDetails(@Path("path") String path, @Query("event_id") String event_id);

    @GET("event/get_invitation")
    Call<PeopleDataResponse> getUserList(@Query("event_id") String event_id);

    @GET("event/list_comment{path}")
    Call<EventsCommentsResponseData> getEventComments(@Path("path") String path, @Query("page") int page, @Query("event_id") String event_id, @Query("media_id") String media_id);

    @GET("event/map_list{path}")
    Call<EventsResponse> getEventsMapPin(@Path("path") String path);

    @POST("event/post_comment")
    Call<PostCommentResponseData> getResponsePostComment(@Body JsonObject params);

    @POST("event/send_invitation")
    Call<InvitationResponse> getResponseSendInvitation(@Body JsonObject params);

    @GET("profile{path}")
    Call<UserSelfProfileResponse> getUserProfileDetail(@Path("path") String path);

    @Multipart
    @POST("upload_profile_image")
    Call<GeneralResponse> uploadImage(@Part("profile_img\"; filename=\"IMG_USER_GCM.jpg\" ") RequestBody images);

    @Multipart
    @POST("edit_profile")
    Call<ProfileEditResponse> editUserProfileData(@Part("fullname") RequestBody userName,
                                                  @Part("email") RequestBody userEmail,
                                                  @Part("mobile_number") RequestBody userPhone,
                                                  @Part("user_address") RequestBody userAddress);

    @POST("event/addParticipants")
    Call<AddEventParticipantsResponse> addParticipants(@Body JsonObject params);

    @GET("event/get_all_media{path}")
    Call<EventMediaResponse> getEventMedia(@Path("path") String path, @Query("event_id") int param1, @Query("page") int param2);

    @Multipart
    @POST("event/post_media")
    Call<GeneralResponse> uploadMediaImage(@Part("event_id") RequestBody event_id, @Part MultipartBody.Part images);

    @Multipart
    @POST("event/post_media")
    Call<VideoUploadResponse> uploadMediaVideo(@Part("event_id") RequestBody event_id, @Part MultipartBody.Part video);

    @GET("event/map_list{path}")
    Call<MapLocationResponse> getMapEvent(@Path("path") String path);

    @POST("event/delete_media")
    Call<GeneralResponse> deleteMedia(@Body JsonObject media);

    @POST("list_not_group_people")
    Call<GroupNotMembersResponse> getNotMembersList(@Body JsonObject params);

    @POST("follow_user")
    Call<FollowUserResponseData> followUser(@Body JsonObject params);

    @GET("settings")
    Call<SettingsResponseData> getSettings();

    @POST("edit_settings")
    Call<SettingsSaveResponse> getResponseSaveSettings(@Body JsonObject params);

    @POST("saveLanguage")
    Call<LanguageSaveResponse> getResponseSaveLanguage(@Body JsonObject params);

    @GET("getNotification")
    Call<NotificationListResponse> getNotificationList();

    @POST("event/accept_joined_request")
    Call<GeneralResponse> acceptOrRejectRequest(@Body JsonObject params);

    @POST("register_device_token")
    Call<PushNotifyResponse> registerNewDevice(@Body JsonObject params);

    @GET("page/content")
    Call<ContentPageResponse> getContentPage(@Query("page_name") String pageName);

    @FormUrlEncoded
    @POST("update_location")
    Call<GeneralResponse> updateLocation(@Field("latitude") String latitude, @Field("longitude") String longitude);

    @POST("remove_device_token")
    Call<GeneralResponse> removeTokenOnLogout(@Body JsonObject params);
    @POST("media/like")
    Call<GeneralResponse> likeMedia(@Body JsonObject params);

    @POST("contactUs")
    Call<GeneralResponse> contactUs(@Body JsonObject params);

    @POST("readNotification")
    Call<NotificationReadResponse> removeNotification(@Body JsonObject params);

    @POST("event/cancel_event")
    Call<GeneralResponse> cancelEventSelf(@Body JsonObject params);

    @GET("getNotification")
    Call<NotificationListResponse> getNotificationList(@Query("page") int param2);

    @GET("readAllNotification")
    Call<GeneralResponse> markAllReadNotifications();

    @POST("all_ads")
    Call<AdsResponseData> showAds(@Body JsonObject params);

    @GET("get_followers")
    Call<FollowersResponse> getFollowers();
}

