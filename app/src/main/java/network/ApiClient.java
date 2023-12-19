package network;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import application.AppController;
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
import okhttp3.Interceptor;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import utility.RestrictedSocketFactory;
import utility.SharedPreference;
import utility.URLogs;
import utility.URLs;

public class ApiClient {

    private static Retrofit retrofit = null;
    private ApiInterface apiInterface;
    private static ApiClient apiClient;
    private Gson gson;
    public int DEFAULT_BUFFER_SIZE = 8 * 1024;

    private OkHttpClient getClient() {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request.Builder ongoing = chain.request().newBuilder();
                        ongoing.addHeader("Accept", "application/json");
                        if (SharedPreference.isUserLoggedIn(AppController.getInstance().getApplicationContext())) {
                            ongoing.addHeader("Authorization", "Bearer".concat(" ").concat(SharedPreference.getUserDetails(AppController.getInstance().getApplicationContext()).getToken()));
                        }
                        return chain.proceed(ongoing.build());
                    }
                })
                // .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        HostnameVerifier hv = HttpsURLConnection.getDefaultHostnameVerifier();
                        Boolean result = hv.verify("www.sharehubapp.com", session);
                        URLogs.m("Go the Go=" + session + "..." + result);
                        return result;
                    }
                })
                .connectTimeout(350, TimeUnit.SECONDS)
                .readTimeout(350, TimeUnit.SECONDS)
                .socketFactory(new RestrictedSocketFactory(DEFAULT_BUFFER_SIZE))
                .build();
        return client;
    }

  /*  private static SSLContext getSSLConfig(Context context) throws CertificateException, IOException,
            KeyStoreException, NoSuchAlgorithmException, KeyManagementException {

        // Loading CAs from an InputStream
        CertificateFactory cf = null;
        cf = CertificateFactory.getInstance("X.509");

        Certificate ca;
        // I'm using Java7. If you used Java6 close it manually with finally.
        try (InputStream cert = context.getResources().openRawResource(R.raw.nginxselfsigned)) {
            ca = cf.generateCertificate(cert);
        }

        // Creating a KeyStore containing our trusted CAs
        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(null, null);
        keyStore.setCertificateEntry("ca", ca);

        // Creating a TrustManager that trusts the CAs in our KeyStore.
        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);

        // Creating an SSLSocketFactory that uses our TrustManager
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tmf.getTrustManagers(), null);

        return sslContext;
    }*/

   /* private OkHttpClient getClient() {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .connectTimeout(30, TimeUnit.MINUTES)
                .readTimeout(30, TimeUnit.MINUTES)
                .build();
        return client;
    }*/

    public ApiClient() {
        this.gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
        retrofit = new Retrofit.Builder()
                .baseUrl(URLs.BaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(getClient())
                .build();
        this.apiInterface = retrofit.create(ApiInterface.class);
    }

    public static ApiClient getInstance() {
        if (apiClient == null) {
            setInstance(new ApiClient());
        }
        return apiClient;
    }

    public static void setInstance(ApiClient apiClient) {
        ApiClient.apiClient = apiClient;
    }

    public void registerUser(String fullname, String email, String password, String passConfirm, String mobile, String username, Callback<SignUpResponse> callback) {
        Call<SignUpResponse> call = null;
        call = this.apiInterface.registerUser(fullname, email, password, passConfirm, mobile, username);
        /*if(file == null){
            call = this.apiInterface.registerUser(language, name, username, deviceType, deviceToken, email, password);
        }else{
            call = this.apiInterface.registerUser(language, name, username, deviceType, deviceToken,email, password, file);
        }*/
        call.enqueue(callback);
    }

    public void loginUser(String email, String password, Callback<SignInResponse> callback) {
        Call<SignInResponse> call = this.apiInterface.login(email, password);
        call.enqueue(callback);
    }

    public void phoneVerify(String phone, Callback<PhoneVerifyResponse> callback) {
        Call<PhoneVerifyResponse> call = this.apiInterface.phoneVerify(phone);
        call.enqueue(callback);
    }

    public void forgotPassword(String email, Callback<ForgotResponse> callback) {
        Call<ForgotResponse> call = this.apiInterface.forgotPass(email);
        call.enqueue(callback);
    }

    public void verifyCode(String userId, String code, Callback<VerifyCodeResponse> callback) {
        Call<VerifyCodeResponse> call = this.apiInterface.verifyCode(userId, code);
        call.enqueue(callback);
    }

    public void categoriesList(Callback<CategoriesListResponse> callback) {
        Call<CategoriesListResponse> call = this.apiInterface.categoriesList();
        call.enqueue(callback);
    }


    public void getEventsData(int page, JsonObject params, Callback<EventsResponse> callback) {
        if (SharedPreference.isUserLoggedIn(AppController.getInstance().getApplicationContext())) {
            Call<EventsResponse> call = this.apiInterface.getEventsData("", page, params);
            call.enqueue(callback);

        } else {
            Call<EventsResponse> call = this.apiInterface.getEventsData("_simple", page, params);
            call.enqueue(callback);
        }
    }

    public void getEventCommentsListing(int page, String id, String media, Callback<EventsCommentsResponseData> callback) {
        if (SharedPreference.isUserLoggedIn(AppController.getInstance().getApplicationContext())) {
            Call<EventsCommentsResponseData> call = this.apiInterface.getEventComments("", page, id, media);
            call.enqueue(callback);
        } else {
            Call<EventsCommentsResponseData> call = this.apiInterface.getEventComments("_simple", page, id, media);
            call.enqueue(callback);
        }
    }

    public void getGroupList(Callback<GroupListResponse> callback) {
        Call<GroupListResponse> call = this.apiInterface.getGroupList();
        call.enqueue(callback);
    }

    public void getUserList(@Body JsonObject params, Callback<UserListResponse> callback) {
        Call<UserListResponse> call = this.apiInterface.getUserList(params);
        call.enqueue(callback);
    }

    public void createGroup(@Body JsonObject params, Callback<CreateGroupResponse> callback) {
        Call<CreateGroupResponse> call = this.apiInterface.createGroup(params);
        call.enqueue(callback);
    }

    public void updateGroup(@Body JsonObject params, Callback<GroupUpdateResponse> callback) {
        Call<GroupUpdateResponse> call = this.apiInterface.updateGroup(params);
        call.enqueue(callback);
    }

    public void deleteGroup(@Body JsonObject params, Callback<GroupListResponse> callback) {
        Call<GroupListResponse> call = this.apiInterface.deleteGroup(params);
        call.enqueue(callback);
    }

    public void addUserToGroup(JsonObject users,
                               Callback<GroupAddResponse> callback) {
        Call<GroupAddResponse> call = null;
        call = this.apiInterface.addUserToGroup(users);
        call.enqueue(callback);
    }

    public void blockMemberCall(JsonObject user, Callback<GroupListResponse> callback) {
        Call<GroupListResponse> call = this.apiInterface.blockMemberCall(user);
        call.enqueue(callback);
    }

    public void deleteMember(JsonObject user, Callback<GroupListResponse> callback) {
        Call<GroupListResponse> call = this.apiInterface.deleteMember(user);
        call.enqueue(callback);
    }

    public void getGroupBlockedList(Callback<BlockedPeopleResponse> callback) {
        Call<BlockedPeopleResponse> call = this.apiInterface.getGroupBlockedList();
        call.enqueue(callback);
    }

    public void deleteBlockedMember(JsonObject user, Callback<DeleteBlockedMemberResponse> callback) {
        Call<DeleteBlockedMemberResponse> call = this.apiInterface.deleteBlockedMember(user);
        call.enqueue(callback);
    }

    public void getEventsParticipants(JsonObject params, Callback<EventsParticipantsResponse> callback) {
        Call<EventsParticipantsResponse> call = this.apiInterface.getEventsParticipantsData(params);
        call.enqueue(callback);
    }

    public void createEvent(RequestBody event_title, RequestBody event_description, RequestBody event_start_date,
                            RequestBody event_start_time,
                            RequestBody event_location, RequestBody event_venue_lat, RequestBody event_venue_long,
                            RequestBody event_detail, RequestBody ecat_id, RequestBody event_iscomments_allowed, RequestBody event_isprivate,
                            RequestBody event_groups, RequestBody event_users, RequestBody event_pincode,
                            RequestBody event_isapproval_required, RequestBody event_ispinode_required,
                            RequestBody images, Callback<CreateEventResponse> callback) {
        Call<CreateEventResponse> call = null;
        call = this.apiInterface.createEvent(event_title, event_description, event_start_date,
                event_start_time,
                event_location, event_venue_lat, event_venue_long,
                event_detail, ecat_id, event_iscomments_allowed, event_isprivate,
                event_groups, event_users, event_pincode, event_isapproval_required, event_ispinode_required, images);
        call.enqueue(callback);
    }

    public void editEvent(RequestBody event_id, RequestBody event_title, RequestBody event_description, RequestBody event_start_date,
                          RequestBody event_start_time,
                          RequestBody event_location, RequestBody event_venue_lat, RequestBody event_venue_long,
                          RequestBody event_detail, RequestBody ecat_id, RequestBody event_iscomments_allowed, RequestBody event_isprivate,
                          RequestBody event_pincode, RequestBody event_isapproval_required, RequestBody event_ispinode_required,
                          RequestBody images, Callback<CreateEventResponse> callback) {
        Call<CreateEventResponse> call = null;
        if (images == null) {
            call = this.apiInterface.editEvent(event_id, event_title, event_description, event_start_date,
                    event_start_time,
                    event_location, event_venue_lat, event_venue_long,
                    event_detail, ecat_id, event_iscomments_allowed, event_isprivate,
                    event_pincode, event_isapproval_required, event_ispinode_required);
        } else {
            call = this.apiInterface.editEvent(event_id, event_title, event_description, event_start_date,
                    event_start_time,
                    event_location, event_venue_lat, event_venue_long,
                    ecat_id, event_iscomments_allowed, event_isprivate,
                    event_pincode, event_isapproval_required, event_ispinode_required, images);
        }

        call.enqueue(callback);
    }

    public void getUserProfileData(String userId, Callback<UserProfileResponse> callback) {
        if (SharedPreference.isUserLoggedIn(AppController.getInstance().getApplicationContext())) {
            Call<UserProfileResponse> call = this.apiInterface.getUserProfileData("", userId);
            call.enqueue(callback);
        } else {
            Call<UserProfileResponse> call = this.apiInterface.getUserProfileData("_simple", userId);
            call.enqueue(callback);
        }
    }


    public void getEventsPeople(String id, int page, Callback<EventPeopleResponseData> callback) {
        if (SharedPreference.isUserLoggedIn(AppController.getInstance().getApplicationContext())) {
            Call<EventPeopleResponseData> call = this.apiInterface.getEventPeopleList("", id, page);
            call.enqueue(callback);
        } else {
            Call<EventPeopleResponseData> call = this.apiInterface.getEventPeopleList("_simple", id, page);
            call.enqueue(callback);
        }
    }

    public void getDetailsOfEvent(String id, Callback<EventDetailsResponseData> callback) {
        if (SharedPreference.isUserLoggedIn(AppController.getInstance().getApplicationContext())) {
            Call<EventDetailsResponseData> call = this.apiInterface.getEventDetails("", id);
            call.enqueue(callback);
        } else {
            Call<EventDetailsResponseData> call = this.apiInterface.getEventDetails("_simple", id);
            call.enqueue(callback);
        }
    }


    public void getUserListForInvitaion(String id, Callback<PeopleDataResponse> callback) {
        Call<PeopleDataResponse> call = this.apiInterface.getUserList(id);
        call.enqueue(callback);
    }

    public void getResponseSucessInvitaion(JsonObject params, Callback<InvitationResponse> callback) {
        Call<InvitationResponse> call = this.apiInterface.getResponseSendInvitation(params);
        call.enqueue(callback);
    }

    public void getEventsMap(Callback<EventsResponse> callback) {
        if (SharedPreference.isUserLoggedIn(AppController.getInstance().getApplicationContext())) {
            Call<EventsResponse> call = this.apiInterface.getEventsMapPin("");
            call.enqueue(callback);
        } else {
            Call<EventsResponse> call = this.apiInterface.getEventsMapPin("_simple");
            call.enqueue(callback);
        }
    }

    public void getUserProfileDetail(Callback<UserSelfProfileResponse> callback) {
        if (SharedPreference.isUserLoggedIn(AppController.getInstance().getApplicationContext())) {
            Call<UserSelfProfileResponse> call = this.apiInterface.getUserProfileDetail("");
            call.enqueue(callback);
        } else {
            Call<UserSelfProfileResponse> call = this.apiInterface.getUserProfileDetail("_simple");
            call.enqueue(callback);
        }
    }

    public void uploadImage(RequestBody images, Callback<GeneralResponse> callback) {
        Call<GeneralResponse> call = null;
        call = this.apiInterface.uploadImage(images);
        call.enqueue(callback);
    }

    public void addParticipants(@Body JsonObject params, Callback<AddEventParticipantsResponse> callback) {
        Call<AddEventParticipantsResponse> call = null;
        call = this.apiInterface.addParticipants(params);
        call.enqueue(callback);
    }

    public void editUserProfileData(RequestBody userName, RequestBody userEmail, RequestBody userPhone, RequestBody userAddress, Callback<ProfileEditResponse> callback) {
        Call<ProfileEditResponse> call = null;
        call = this.apiInterface.editUserProfileData(userName, userEmail, userPhone, userAddress);
        call.enqueue(callback);
    }

    public void getEventMedia(int param1, int param2, Callback<EventMediaResponse> callback) {
        if (SharedPreference.isUserLoggedIn(AppController.getInstance().getApplicationContext())) {
            Call<EventMediaResponse> call = this.apiInterface.getEventMedia("", param1, param2);
            call.enqueue(callback);
        } else {
            Call<EventMediaResponse> call = this.apiInterface.getEventMedia("_simple", param1, param2);
            call.enqueue(callback);
        }
    }

    public void uploadMediaImage(RequestBody user_id, MultipartBody.Part images, Callback<GeneralResponse> callback) {
        Call<GeneralResponse> call = null;
        call = this.apiInterface.uploadMediaImage(user_id, images);
        call.enqueue(callback);
    }

    public void uploadMediaVideo(RequestBody event_id, MultipartBody.Part video, Callback<VideoUploadResponse> callback) {
        Call<VideoUploadResponse> call = null;
        call = this.apiInterface.uploadMediaVideo(event_id, video);
        call.enqueue(callback);
    }

    public void getEventsMarkerData(Callback<MapLocationResponse> callback) {
        if (SharedPreference.isUserLoggedIn(AppController.getInstance().getApplicationContext())) {
            Call<MapLocationResponse> call = this.apiInterface.getMapEvent("");
            call.enqueue(callback);
        } else {
            Call<MapLocationResponse> call = this.apiInterface.getMapEvent("_simple");
            call.enqueue(callback);
        }
    }

    public void deleteMedia(JsonObject media,
                            Callback<GeneralResponse> callback) {
        Call<GeneralResponse> call = null;
        call = this.apiInterface.deleteMedia(media);
        call.enqueue(callback);
    }

    public void getNotMembersList(@Body JsonObject params, Callback<GroupNotMembersResponse> callback) {
        Call<GroupNotMembersResponse> call = this.apiInterface.getNotMembersList(params);
        call.enqueue(callback);
    }

    public void followUserProfile(@Body JsonObject params, Callback<FollowUserResponseData> callback) {
        Call<FollowUserResponseData> call = null;
        call = this.apiInterface.followUser(params);
        call.enqueue(callback);
    }

    public void getSettingsData(Callback<SettingsResponseData> callback) {
        Call<SettingsResponseData> call = this.apiInterface.getSettings();
        call.enqueue(callback);
    }

    public void saveSettings(@Body JsonObject params, Callback<SettingsSaveResponse> callback) {
        Call<SettingsSaveResponse> call = null;
        call = this.apiInterface.getResponseSaveSettings(params);
        call.enqueue(callback);
    }

    public void saveLanguage(@Body JsonObject params, Callback<LanguageSaveResponse> callback) {
        Call<LanguageSaveResponse> call = null;
        call = this.apiInterface.getResponseSaveLanguage(params);
        call.enqueue(callback);
    }

    public void getNotificationList(Callback<NotificationListResponse> callback) {
        Call<NotificationListResponse> call = this.apiInterface.getNotificationList();
        call.enqueue(callback);
    }

    public void acceptOrRejectRequest(JsonObject user, Callback<GeneralResponse> callback) {
        Call<GeneralResponse> call = this.apiInterface.acceptOrRejectRequest(user);
        call.enqueue(callback);
    }

    public void registerNewDevice(JsonObject data, Callback<PushNotifyResponse> callback) {
        Call<PushNotifyResponse> call = this.apiInterface.registerNewDevice(data);
        call.enqueue(callback);
    }

    public void getContentPage(String pageName, Callback<ContentPageResponse> callback) {
        Call<ContentPageResponse> call = this.apiInterface.getContentPage(pageName);
        call.enqueue(callback);
    }

    public void updateLocation(String lat, String lon, Callback<GeneralResponse> callback) {
        Call<GeneralResponse> call = this.apiInterface.updateLocation(lat, lon);
        call.enqueue(callback);
    }

    public void removeTokenOnLogout(JsonObject user, Callback<GeneralResponse> callback) {
        Call<GeneralResponse> call = this.apiInterface.removeTokenOnLogout(user);
        call.enqueue(callback);
    }

    public void likeMedia(JsonObject params, Callback<GeneralResponse> callback) {
        Call<GeneralResponse> call = this.apiInterface.likeMedia(params);
        call.enqueue(callback);
    }

    public void contactUs(JsonObject user, Callback<GeneralResponse> callback) {
        Call<GeneralResponse> call = this.apiInterface.contactUs(user);
        call.enqueue(callback);
    }

    public void removeNotification(JsonObject user, Callback<NotificationReadResponse> callback) {
        Call<NotificationReadResponse> call = this.apiInterface.removeNotification(user);
        call.enqueue(callback);
    }

    public void cancelSelfEvent(@Body JsonObject params, Callback<GeneralResponse> callback) {
        Call<GeneralResponse> call = null;
        call = this.apiInterface.cancelEventSelf(params);
        call.enqueue(callback);
    }

    public void getNotificationList(int currentPage, Callback<NotificationListResponse> callback) {
        Call<NotificationListResponse> call = this.apiInterface.getNotificationList(currentPage);
        call.enqueue(callback);
    }

    public void markAllReadNotifications(Callback<GeneralResponse> callback) {
        Call<GeneralResponse> call = this.apiInterface.markAllReadNotifications();
        call.enqueue(callback);
    }

    public void showAds(@Body JsonObject params, Callback<AdsResponseData> callback) {
        Call<AdsResponseData> call = null;
        call = this.apiInterface.showAds(params);
        call.enqueue(callback);
    }


    public void getFollowers(Callback<FollowersResponse> callback) {
        Call<FollowersResponse> call = this.apiInterface.getFollowers();
        call.enqueue(callback);
    }

}