package fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;


import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import androidx.appcompat.app.AlertDialog;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.gson.JsonObject;
import com.mobiletouch.sharehub.ContentPageActivity;
import com.mobiletouch.sharehub.MainActivity;
import com.mobiletouch.sharehub.R;
import com.mobiletouch.sharehub.SplashActivity;
import com.mobiletouch.sharehub.UserProfileActivity;
import com.squareup.picasso.Picasso;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import adapters.SettingBoubleCategoryAdapter;
import adapters.SettingsItemCheckboxBaseAdapter;
import application.LocaleHelper;
import models.GeneralResponse;
import models.LanguageSaveResponse;
import models.SettingsCheckedCategoryDataModel;
import models.SettingsResponseData;
import models.SettingsSaveResponse;
import network.ApiClient;
import notifications.NotificationConfig;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utility.AppUtils;
import utility.Constants;
import utility.DialogFactory;
import utility.SharedPreference;
import utility.SpinnerSpacesDecoration;
import utility.URLs;

public class SettingsFragment extends Fragment implements View.OnClickListener {

    private View v;
    private CoordinatorLayout rl_main_profile;
    private AppCompatActivity mContext;
    private String language;
    private ImageView btnToolbarRight, btnToolbarBack;
    private TextView tvToolbarTitle;
    private ImageView btnToolbarDrawer;
    private Toolbar toolBar;
    private TextView tvToolbarClose;
    private AutoCompleteTextView etToolbarSearch;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private LinearLayout llLogOut;
    RelativeLayout llProfile;
    private LinearLayout llLanguage;
    private LinearLayout llAbout;
    private LinearLayout llContactUs;
    private LinearLayout llListView;
    private String languageCode;
    private TextView tvLanguage, tvName, tvPhone;
    private RelativeLayout btClickArrow;
    private RelativeLayout spEventCat;
    private RecyclerView rvSelectedCat;
    private ArrayList<SettingsResponseData.userCategories> UserCategoryArray = new ArrayList<SettingsResponseData.userCategories>();
    private ArrayList<SettingsResponseData.Category> AllCategoryData = new ArrayList<SettingsResponseData.Category>();
    private SettingBoubleCategoryAdapter mBoubleViewAdapter;
    private SettingsItemCheckboxBaseAdapter mSpinnerDataAdapter;
    private ArrayList<SettingsCheckedCategoryDataModel> CheckedCategoryArray = new ArrayList<SettingsCheckedCategoryDataModel>();
    private ArrayList<String> SelectedCategoryArray = new ArrayList<String>();
    private ArrayList<String> CategoriesArrayListAppend;
    private ListView lvCategory;
    private int lvVisibility;
    private int mUserVisibility;
    private int mShowEvent;
    private int mUserNotificationMute;
    private String mUserIntrestArea;
    private SwitchCompat tbtVisibility;
    private SwitchCompat tbtNotification;
    private SwitchCompat tbtShowEvents;
    private TextView mtxtArea;
    private DiscreteSeekBar seekInterestedArea;
    private FrameLayout progressBar;
    private Button mbtSave;
    ImageView ivProfile;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_settings, container, false);
        mContext = (AppCompatActivity) getActivity();
        MainActivity.tabHome.setEnabled(true);

        getActivityData();
        viewInitialize(v);

        return v;
    }

    private void viewInitialize(View v) {
        llProfile = v.findViewById(R.id.llProfile);
        ivProfile = v.findViewById(R.id.ivProfile);
        tvName = v.findViewById(R.id.tvName);
        tvPhone = v.findViewById(R.id.tvPhone);
        btClickArrow = v.findViewById(R.id.btClickArrow);
        llLogOut = v.findViewById(R.id.llLogOut);
        llLanguage = v.findViewById(R.id.ll_language);
        llAbout = v.findViewById(R.id.llAbout);
        llContactUs = v.findViewById(R.id.llContactUs);
        llListView = v.findViewById(R.id.llListView);
        tvLanguage = v.findViewById(R.id.tvLanguage);
        spEventCat = v.findViewById(R.id.spEventCat);
        rvSelectedCat = v.findViewById(R.id.rvSelectedCat);
        lvCategory = v.findViewById(R.id.lvCategory);
        tbtShowEvents = v.findViewById(R.id.tbtShowEvents);
        tbtNotification = v.findViewById(R.id.tbtNotification);
        tbtVisibility = v.findViewById(R.id.tbtVisibility);
        mtxtArea = v.findViewById(R.id.txtArea);
        progressBar = v.findViewById(R.id.progressBar);
        seekInterestedArea = v.findViewById(R.id.seekArea);
        mbtSave = v.findViewById(R.id.btSave);
        if (SharedPreference.getSharedPrefValue(mContext, "name") != null) {
            Constants.Name = SharedPreference.getSharedPrefValue(mContext, "name");
            tvName.setText(Constants.Name);
        }
        if (SharedPreference.getSharedPrefValue(mContext, "phone") != null) {

            Constants.Phone = SharedPreference.getSharedPrefValue(mContext, "phone");

            tvPhone.setText(Constants.Phone);
        }
        if (SharedPreference.getSharedPrefValue(mContext, "image") != null) {

            Constants.Image = SharedPreference.getSharedPrefValue(mContext, "image");

            Picasso.with(mContext)
                    .load(URLs.BaseUrlUserImageUsersThumbs + Constants.Image)   //
                    .placeholder(R.drawable.user_placeholder) // optional
                    .error(R.drawable.user_placeholder)         // optional
                    .into(ivProfile);

        }

        rvSelectedCat.addItemDecoration(new SpinnerSpacesDecoration(1));
        rvSelectedCat.setHasFixedSize(true);
        int numberOfColumns = 2;
        rvSelectedCat.setLayoutManager(new GridLayoutManager(getActivity(), numberOfColumns));
        rvSelectedCat.setItemAnimator(new DefaultItemAnimator());
        languageCode = SharedPreference.unScrambleText(SharedPreference.getSharedPrefValue(mContext, Constants.Pref_Language));
        if (AppUtils.isSet(languageCode)) {
            if (languageCode.equals("en"))
                tvLanguage.setText(mContext.getString(R.string.tv_english));
            else
                tvLanguage.setText(mContext.getString(R.string.tv_arabic));
        }

        llLogOut.setOnClickListener(this);
        llProfile.setOnClickListener(this);
        llLanguage.setOnClickListener(this);
        llAbout.setOnClickListener(this);
        llContactUs.setOnClickListener(this);
        btClickArrow.setOnClickListener(this);
        mbtSave.setOnClickListener(this);


        tbtVisibility.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mUserVisibility = 1;
                } else {
                    mUserVisibility = 0;
                }
            }
        });


        tbtShowEvents.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mShowEvent = 1;
                } else {
                    mShowEvent = 0;
                }
            }
        });


        tbtNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mUserNotificationMute = 1;
                } else {
                    mUserNotificationMute = 0;
                }
            }
        });


        mSpinnerDataAdapter = new SettingsItemCheckboxBaseAdapter(mContext, CheckedCategoryArray);
        lvCategory.setAdapter(mSpinnerDataAdapter);
        lvCategory.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        lvCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                lvVisibility = 0;
                Object itemObject = parent.getAdapter().getItem(position);
                SettingsCheckedCategoryDataModel itemDto = (SettingsCheckedCategoryDataModel) itemObject;
                SettingsResponseData.userCategories response = new SettingsResponseData.userCategories();
                if (itemDto.isChecked()) {
                    if (AppUtils.isSet(languageCode)) {
                        if (languageCode.equals("en")) {
                            if (SelectedCategoryArray.contains(itemDto.getItemText()) || response.getEcatName().equalsIgnoreCase(itemDto.getItemText())) {
                                SelectedCategoryArray.remove(itemDto.getItemText());
                                for (int j = 0; j < UserCategoryArray.size(); j++) {

                                    if (UserCategoryArray.get(j).getEcatName().equals(itemDto.getItemText())) {
                                        UserCategoryArray.remove(j);
                                        mBoubleViewAdapter.notifyDataSetChanged();
                                    }

                                }
                            }
                        } else {
                            if (SelectedCategoryArray.contains(itemDto.getItemText()) || response.getEcat_name_ar().equalsIgnoreCase(itemDto.getItemText())) {
                                SelectedCategoryArray.remove(itemDto.getItemText());
                                for (int j = 0; j < UserCategoryArray.size(); j++) {

                                    if (UserCategoryArray.get(j).getEcat_name_ar().equals(itemDto.getItemText())) {
                                        UserCategoryArray.remove(j);
                                        mBoubleViewAdapter.notifyDataSetChanged();
                                    }

                                }
                            }
                        }
                    }
                    CheckBox itemCheckbox = (CheckBox) view.findViewById(R.id.ckcategory);

                    // Reverse the checkbox and clicked item check state.
                    if (itemDto.isChecked()) {
                        itemCheckbox.setChecked(false);
                        itemDto.setChecked(false);
                    } else {
                        itemCheckbox.setChecked(true);
                        itemDto.setChecked(true);
                    }

                } else {


                    if (AppUtils.isSet(languageCode)) {
                        if (languageCode.equals("en")) {
                            response.setEcatName(itemDto.getItemText());

                            if (!SelectedCategoryArray.contains(response.getEcatName())) {
                                for (int j = 0; j < AllCategoryData.size(); j++) {

                                    if (AllCategoryData.get(j).getEcatName().equals(itemDto.getItemText())) {
                                        response.setEcatId(AllCategoryData.get(j).getEcatId());
                                    }

                                }

                                UserCategoryArray.add(response);
                                for (int j = 0; j < UserCategoryArray.size(); j++) {
                                    if (!SelectedCategoryArray.contains(UserCategoryArray.get(j).getEcatName())) {
                                        SelectedCategoryArray.add(UserCategoryArray.get(j).getEcatName());
                                    }
                                }

                            }
                        } else {
                            response.setEcat_name_ar(itemDto.getItemText());

                            if (!SelectedCategoryArray.contains(response.getEcat_name_ar())) {
                                for (int j = 0; j < AllCategoryData.size(); j++) {

                                    if (AllCategoryData.get(j).getEcat_name_ar().equals(itemDto.getItemText())) {
                                        response.setEcatId(AllCategoryData.get(j).getEcatId());
                                    }

                                }

                                UserCategoryArray.add(response);

                                for (int j = 0; j < UserCategoryArray.size(); j++) {
                                    if (!SelectedCategoryArray.contains(UserCategoryArray.get(j).getEcat_name_ar())) {
                                        SelectedCategoryArray.add(UserCategoryArray.get(j).getEcat_name_ar());
                                    }
                                }
                            }
                        }
                    }


                    // Translate the selected item to DTO object.


                    // Get the checkbox.
                    CheckBox itemCheckbox = (CheckBox) view.findViewById(R.id.ckcategory);

                    // Reverse the checkbox and clicked item check state.
                    if (itemDto.isChecked()) {
                        itemCheckbox.setChecked(false);
                        itemDto.setChecked(false);
                    } else {
                        itemCheckbox.setChecked(true);
                        itemDto.setChecked(true);
                    }

                    rvSelectedCat.setVisibility(View.VISIBLE);
                    llListView.setVisibility(View.GONE);
                    mBoubleViewAdapter = new SettingBoubleCategoryAdapter(getActivity(), UserCategoryArray, SelectedCategoryArray, CheckedCategoryArray);
                    rvSelectedCat.setAdapter(mBoubleViewAdapter);

                    mSpinnerDataAdapter = new SettingsItemCheckboxBaseAdapter(mContext, CheckedCategoryArray);
                    lvCategory.setAdapter(mSpinnerDataAdapter);
                    mSpinnerDataAdapter.notifyDataSetChanged();
                }
            }
        });


        seekInterestedArea.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                mtxtArea.setVisibility(View.VISIBLE);
                mtxtArea.setText(value + "Km");
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

            }
        });


        CallGetSettings();

    }

    public void initToolBar(View v) {
        toolBar = mContext.findViewById(R.id.toolbar);
        ((TextView) toolBar.findViewById(R.id.tv_toolbar_title)).setText(mContext.getString(R.string.tv_settings));
        btnToolbarBack = toolBar.findViewById(R.id.btn_toolbar_back);
        btnToolbarRight = toolBar.findViewById(R.id.btn_toolbar_right);
        btnToolbarRight.setVisibility(View.GONE);

        btnToolbarBack.setOnClickListener(this);
    }

    private void getActivityData() {
        Bundle bundle = mContext.getIntent().getExtras();
        if (bundle != null) {

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llProfile:
                Intent profile = new Intent(mContext, UserProfileActivity.class);
                startActivity(profile);
                break;

            case R.id.llAbout:
                Intent about = new Intent(mContext, ContentPageActivity.class);
                about.putExtra("pageName", "about-us");
                about.putExtra("pageTitle", mContext.getResources().getString(R.string.tv_aboutUS));
                startActivity(about);
                break;

            case R.id.llContactUs:
                Intent contactUs = new Intent(mContext, ContentPageActivity.class);
                contactUs.putExtra("pageName", "contact-us");
                contactUs.putExtra("pageTitle", mContext.getResources().getString(R.string.tv_contactUS));
                startActivity(contactUs);
                break;

            case R.id.llLogOut:
                logoutBottomSheet();
                break;

            case R.id.ll_language:
                showDialogWithLanguageList();
                break;

            case R.id.btClickArrow:

                lvVisibility = 1;
                categorySelectionDialog();
                break;

            case R.id.btSave:
                SaveSettings();
                break;

        }
    }

    private void categorySelectionDialog() {
        final Dialog sheetDialog = new Dialog(mContext);
        View sheetView = mContext.getLayoutInflater().inflate(R.layout.dialog_category_views, null);
        sheetDialog.setContentView(sheetView);
        sheetDialog.setCancelable(true);
        sheetDialog.setCanceledOnTouchOutside(true);
        ((View) sheetView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));
        final ListView lvCategory = sheetView.findViewById(R.id.lvCategory);
        mSpinnerDataAdapter = new SettingsItemCheckboxBaseAdapter(mContext, CheckedCategoryArray);
        lvCategory.setAdapter(mSpinnerDataAdapter);
        lvCategory.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        lvCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                lvVisibility = 0;
                Object itemObject = parent.getAdapter().getItem(position);
                SettingsCheckedCategoryDataModel itemDto = (SettingsCheckedCategoryDataModel) itemObject;
                SettingsResponseData.userCategories response = new SettingsResponseData.userCategories();
                if (itemDto.isChecked()) {
                    if (AppUtils.isSet(languageCode)) {
                        if (languageCode.equals("en")) {
                            if (SelectedCategoryArray.contains(itemDto.getItemText()) || response.getEcatName().equalsIgnoreCase(itemDto.getItemText())) {
                                SelectedCategoryArray.remove(itemDto.getItemText());
                                for (int j = 0; j < UserCategoryArray.size(); j++) {

                                    if (UserCategoryArray.get(j).getEcatName().equals(itemDto.getItemText())) {
                                        UserCategoryArray.remove(j);
                                        mBoubleViewAdapter.notifyDataSetChanged();
                                    }

                                }
                            }
                        } else {
                            if (SelectedCategoryArray.contains(itemDto.getItemText()) || response.getEcat_name_ar().equalsIgnoreCase(itemDto.getItemText())) {
                                SelectedCategoryArray.remove(itemDto.getItemText());
                                for (int j = 0; j < UserCategoryArray.size(); j++) {

                                    if (UserCategoryArray.get(j).getEcat_name_ar().equals(itemDto.getItemText())) {
                                        UserCategoryArray.remove(j);
                                        mBoubleViewAdapter.notifyDataSetChanged();
                                    }

                                }
                            }
                        }
                    }
                    CheckBox itemCheckbox = (CheckBox) view.findViewById(R.id.ckcategory);

                    // Reverse the checkbox and clicked item check state.
                    if (itemDto.isChecked()) {
                        itemCheckbox.setChecked(false);
                        itemDto.setChecked(false);
                    } else {
                        itemCheckbox.setChecked(true);
                        itemDto.setChecked(true);
                    }

                } else {


                    if (AppUtils.isSet(languageCode)) {
                        if (languageCode.equals("en")) {
                            response.setEcatName(itemDto.getItemText());

                            if (!SelectedCategoryArray.contains(response.getEcatName())) {
                                for (int j = 0; j < AllCategoryData.size(); j++) {

                                    if (AllCategoryData.get(j).getEcatName().equals(itemDto.getItemText())) {
                                        response.setEcatId(AllCategoryData.get(j).getEcatId());
                                    }

                                }

                                UserCategoryArray.add(response);
                                for (int j = 0; j < UserCategoryArray.size(); j++) {
                                    if (!SelectedCategoryArray.contains(UserCategoryArray.get(j).getEcatName())) {
                                        SelectedCategoryArray.add(UserCategoryArray.get(j).getEcatName());
                                    }
                                }

                            }
                        } else {
                            response.setEcat_name_ar(itemDto.getItemText());

                            if (!SelectedCategoryArray.contains(response.getEcat_name_ar())) {
                                for (int j = 0; j < AllCategoryData.size(); j++) {

                                    if (AllCategoryData.get(j).getEcat_name_ar().equals(itemDto.getItemText())) {
                                        response.setEcatId(AllCategoryData.get(j).getEcatId());
                                    }

                                }

                                UserCategoryArray.add(response);

                                for (int j = 0; j < UserCategoryArray.size(); j++) {
                                    if (!SelectedCategoryArray.contains(UserCategoryArray.get(j).getEcat_name_ar())) {
                                        SelectedCategoryArray.add(UserCategoryArray.get(j).getEcat_name_ar());
                                    }
                                }
                            }
                        }
                    }


                    // Translate the selected item to DTO object.


                    // Get the checkbox.
                    CheckBox itemCheckbox = (CheckBox) view.findViewById(R.id.ckcategory);

                    // Reverse the checkbox and clicked item check state.
                    if (itemDto.isChecked()) {
                        itemCheckbox.setChecked(false);
                        itemDto.setChecked(false);
                    } else {
                        itemCheckbox.setChecked(true);
                        itemDto.setChecked(true);
                    }

                    rvSelectedCat.setVisibility(View.VISIBLE);
                    llListView.setVisibility(View.GONE);
                    mBoubleViewAdapter = new SettingBoubleCategoryAdapter(getActivity(), UserCategoryArray, SelectedCategoryArray, CheckedCategoryArray);
                    rvSelectedCat.setAdapter(mBoubleViewAdapter);

                    mSpinnerDataAdapter = new SettingsItemCheckboxBaseAdapter(mContext, CheckedCategoryArray);
                    lvCategory.setAdapter(mSpinnerDataAdapter);
                    mSpinnerDataAdapter.notifyDataSetChanged();
                }
            }
        });


        sheetDialog.show();
    }

    private void logoutBottomSheet() {
        final BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(mContext);
        View sheetView = mContext.getLayoutInflater().inflate(R.layout.logout_bottomsheet_layout, null);
        mBottomSheetDialog.setContentView(sheetView);
        mBottomSheetDialog.setCancelable(true);
        mBottomSheetDialog.setCanceledOnTouchOutside(false);
        ((View) sheetView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));
        TextView tvLogOut = sheetView.findViewById(R.id.tvLogOut);
        Button btnCancel = sheetView.findViewById(R.id.btnCancel);
        tvLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Edit code here;
                mBottomSheetDialog.dismiss();

                showProgressBar(true);
                JsonObject paramObject = new JsonObject();
                paramObject.addProperty("device_token",
                        SharedPreference.unScrambleText(SharedPreference.getSharedPrefValue(mContext, NotificationConfig.FCM_ID)));
                removeFCMTokenOnLogout(paramObject);

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Delete code here;
                mBottomSheetDialog.dismiss();
            }
        });
        mBottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                // Do something
            }
        });
        mBottomSheetDialog.show();
    }

    private void showDialogWithLanguageList() {
        List<String> mLanguageList = new ArrayList<String>();
        mLanguageList.add(mContext.getString(R.string.tv_english));
        mLanguageList.add(mContext.getString(R.string.tv_arabic));

        final CharSequence[] Animals = mLanguageList.toArray(new String[mLanguageList.size()]);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_alertdialog_title_layout, null);
        TextView textView = view.findViewById(R.id.alertDialogTitle);
        textView.setText(R.string.tv_language);
        dialogBuilder.setCustomTitle(view);
        dialogBuilder.setItems(Animals, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                String selectedText = Animals[item].toString();
                if (AppUtils.isSet(selectedText) && selectedText.equals(mContext.getString(R.string.tv_english))) {
                    tvLanguage.setText("" + selectedText);
                    SharedPreference.saveSharedPrefValue(mContext, Constants.Pref_Language, "en");
                } else if (AppUtils.isSet(selectedText) && selectedText.equals(mContext.getString(R.string.tv_arabic))) {
                    tvLanguage.setText("" + selectedText);
                    SharedPreference.saveSharedPrefValue(mContext, Constants.Pref_Language, "ar");
                }

                language = SharedPreference.unScrambleText(SharedPreference.getSharedPrefValue(mContext, Constants.Pref_Language));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Context context = LocaleHelper.setLocale(mContext, language);
                    SaveLanguage(language);
                } else {
                    SaveLanguage(language);
                }

            }
        });
        AlertDialog alertDialogObject = dialogBuilder.create();
        alertDialogObject.show();
    }

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    private void CallGetSettings() {
        UserCategoryArray.clear();
        AllCategoryData.clear();
        CheckedCategoryArray.clear();
        SelectedCategoryArray.clear();
        if (!AppUtils.isOnline(getActivity())) {
            DialogFactory.showDropDownNotification(getActivity(),
                    this.getString(R.string.alert_information),
                    this.getString(R.string.alert_internet_connection));
            return;
        }
        try {
            showProgressBar(true);
            GetSettings();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //Network Calling to get Saved Settings

    private void GetSettings() {
        ApiClient apiClient = ApiClient.getInstance();
        apiClient.getSettingsData(new Callback<SettingsResponseData>() {
            @Override
            public void onResponse(Call<SettingsResponseData> call, Response<SettingsResponseData> response) {
                try {
                    showProgressBar(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getStatus()) {

                        mUserIntrestArea = response.body().getData().getUser().getUserIntrestArea();

                        if (AppUtils.isSet(mUserIntrestArea)) {
                            mtxtArea.setVisibility(View.VISIBLE);
                            mtxtArea.setText(mUserIntrestArea + "Km");
                            int interestedArea = Integer.parseInt(mUserIntrestArea);
                            seekInterestedArea.setProgress(interestedArea);
                        }
                        mShowEvent = response.body().getData().getUser().getUserShowEvents();
                        if (mShowEvent == 0) {
                            tbtShowEvents.setChecked(false);
                            mShowEvent = 0;
                        } else {
                            tbtShowEvents.setChecked(true);
                            mShowEvent = 1;
                        }

                        mUserVisibility = response.body().getData().getUser().getUserVisibility();
                        if (mUserVisibility == 0) {
                            tbtVisibility.setChecked(false);
                            mUserVisibility = 0;
                        } else {
                            tbtVisibility.setChecked(true);
                            mUserVisibility = 1;
                        }

                        mUserNotificationMute = response.body().getData().getUser().getUserNotificationMute();
                        if (mUserNotificationMute == 0) {
                            tbtNotification.setChecked(false);
                            mUserNotificationMute = 0;

                        } else {
                            tbtNotification.setChecked(true);
                            mUserNotificationMute = 1;

                        }


                        UserCategoryArray.addAll(response.body().getData().getUser().getUserCategories());
                        AllCategoryData.addAll(response.body().getData().getCategories());
                        mSpinnerDataAdapter.notifyDataSetChanged();
                        if (AppUtils.isSet(languageCode)) {
                            if (languageCode.equals("en")) {
                                if (UserCategoryArray.size() > 0) {
                                    for (int j = 0; j < UserCategoryArray.size(); j++) {
                                        if (!SelectedCategoryArray.contains(UserCategoryArray.get(j).getEcatName())) {
                                            SelectedCategoryArray.add(UserCategoryArray.get(j).getEcatName());
                                        }
                                        CheckedCategoryArray.clear();
                                        for (int i = 0; i < response.body().getData().getCategories().size(); i++) {
                                            SettingsCheckedCategoryDataModel dataModel = new SettingsCheckedCategoryDataModel();
                                            String CompareValue = response.body().getData().getCategories().get(i).getEcatName();
                                            if (SelectedCategoryArray.contains(CompareValue)) {
                                                dataModel.setChecked(true);
                                            } else {
                                                dataModel.setChecked(false);
                                            }
                                            dataModel.setItemText(response.body().getData().getCategories().get(i).getEcatName());
                                            CheckedCategoryArray.add(dataModel);
                                        }

                                    }
                                } else {
                                    CheckedCategoryArray.clear();
                                    for (int i = 0; i < response.body().getData().getCategories().size(); i++) {
                                        SettingsCheckedCategoryDataModel dataModel = new SettingsCheckedCategoryDataModel();
                                        String CompareValue = response.body().getData().getCategories().get(i).getEcatName();
                                        if (SelectedCategoryArray.size() > 0) {
                                            if (SelectedCategoryArray.contains(CompareValue)) {
                                                dataModel.setChecked(true);
                                            } else {
                                                dataModel.setChecked(false);
                                            }
                                        }
                                        dataModel.setItemText(response.body().getData().getCategories().get(i).getEcatName());
                                        CheckedCategoryArray.add(dataModel);
                                    }
                                }
                            } else {
                                if (UserCategoryArray.size() > 0) {
                                    for (int j = 0; j < UserCategoryArray.size(); j++) {
                                        if (!SelectedCategoryArray.contains(UserCategoryArray.get(j).getEcat_name_ar())) {
                                            SelectedCategoryArray.add(UserCategoryArray.get(j).getEcat_name_ar());
                                        }
                                        CheckedCategoryArray.clear();
                                        for (int i = 0; i < response.body().getData().getCategories().size(); i++) {
                                            SettingsCheckedCategoryDataModel dataModel = new SettingsCheckedCategoryDataModel();
                                            String CompareValue = response.body().getData().getCategories().get(i).getEcat_name_ar();
                                            if (SelectedCategoryArray.contains(CompareValue)) {
                                                dataModel.setChecked(true);
                                            } else {
                                                dataModel.setChecked(false);
                                            }
                                            dataModel.setItemText(response.body().getData().getCategories().get(i).getEcat_name_ar());
                                            CheckedCategoryArray.add(dataModel);
                                        }

                                    }
                                } else {
                                    CheckedCategoryArray.clear();
                                    for (int i = 0; i < response.body().getData().getCategories().size(); i++) {
                                        SettingsCheckedCategoryDataModel dataModel = new SettingsCheckedCategoryDataModel();
                                        String CompareValue = response.body().getData().getCategories().get(i).getEcat_name_ar();
                                        if (SelectedCategoryArray.size() > 0) {
                                            if (SelectedCategoryArray.contains(CompareValue)) {
                                                dataModel.setChecked(true);
                                            } else {
                                                dataModel.setChecked(false);
                                            }
                                        }
                                        dataModel.setItemText(response.body().getData().getCategories().get(i).getEcat_name_ar());
                                        CheckedCategoryArray.add(dataModel);
                                    }
                                }

                            }


                        }

                        rvSelectedCat.setVisibility(View.VISIBLE);
                        mBoubleViewAdapter = new SettingBoubleCategoryAdapter(getActivity(), UserCategoryArray, SelectedCategoryArray, CheckedCategoryArray);
                        rvSelectedCat.setAdapter(mBoubleViewAdapter);


                    } else {
                        if (response.body().getMessage() != null && !response.body().getMessage().isEmpty()) {
                            DialogFactory.showDropDownNotification(
                                    mContext,
                                    mContext.getString(R.string.tv_error),
                                    response.body().getMessage());
                        }
                    }
                } else {

                    if (response.code() == 404)
                        DialogFactory.showDropDownNotification(
                                mContext,
                                getString(R.string.alert_information),
                                getString(R.string.alert_api_not_found));
                    if (response.code() == 500)
                        DialogFactory.showDropDownNotification(
                                mContext,
                                getString(R.string.alert_information),
                                getString(R.string.alert_internal_server_error));

                }

            }


            @Override
            public void onFailure(Call<SettingsResponseData> call, Throwable t) {
                try {
                    showProgressBar(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (t instanceof IOException)
                    DialogFactory.showDropDownNotification(
                            mContext,
                            getString(R.string.alert_information),
                            getString(R.string.alert_file_not_found));
                else DialogFactory.showDropDownNotification(
                        mContext,
                        getString(R.string.alert_information),
                        t.getLocalizedMessage());

            }
        });
    }

    private void showProgressBar(final boolean progressVisible) {
        if (getActivity() != null)
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(progressVisible ? View.VISIBLE : View.GONE);
                }
            });
    }
    //Network Calling to Apply Settings

    private void SaveSettings() {
        //if (seekInterestedArea.getProgress() > 0) {
        mUserIntrestArea = "" + seekInterestedArea.getProgress();
        //}
        CategoriesArrayListAppend = new ArrayList<>();
        for (int j = 0; j < UserCategoryArray.size(); j++) {
            CategoriesArrayListAppend.add(UserCategoryArray.get(j).getEcatId().toString());
        }

        if (!CategoriesArrayListAppend.isEmpty()) {
            String categoryIds = android.text.TextUtils.join(",", CategoriesArrayListAppend);
            JsonObject paramObject = new JsonObject();
            //Log.e("categoryIds", "...." + categoryIds);
            paramObject.addProperty("user_visibility", mUserVisibility);
            paramObject.addProperty("user_show_events", mShowEvent);
            paramObject.addProperty("user_notification_mute", mUserNotificationMute);
            paramObject.addProperty("user_intrest_area", mUserIntrestArea);
            paramObject.addProperty("categories", "[" + categoryIds + "]");
            showProgressBar(true);
            callSaveSettings(paramObject);
        } else {
            JsonObject paramObject = new JsonObject();
            paramObject.addProperty("user_visibility", mUserVisibility);
            paramObject.addProperty("user_show_events", mShowEvent);
            paramObject.addProperty("user_notification_mute", mUserNotificationMute);
            paramObject.addProperty("user_intrest_area", mUserIntrestArea);
            paramObject.addProperty("categories", "[]");
            showProgressBar(true);
            callSaveSettings(paramObject);
        }
    }

    private void callSaveSettings(JsonObject params) {
        ApiClient apiClient = ApiClient.getInstance();
        apiClient.saveSettings(params, new Callback<SettingsSaveResponse>() {
            @Override
            public void onResponse(Call<SettingsSaveResponse> call, final Response<SettingsSaveResponse> response) {
                try {
                    showProgressBar(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getStatus()) {
                        CallGetSettings();
                    } else {
                        if (response.body().getMessage() != null && !response.body().getMessage().isEmpty()) {
                            DialogFactory.showDropDownNotification(
                                    mContext,
                                    mContext.getString(R.string.tv_error),
                                    response.body().getMessage());
                        }
                    }
                } else {
                    if (response.code() == 404)
                        DialogFactory.showDropDownNotification(
                                mContext,
                                getString(R.string.alert_information),
                                getString(R.string.alert_api_not_found));
                    if (response.code() == 500)
                        DialogFactory.showDropDownNotification(
                                mContext,
                                getString(R.string.alert_information),
                                getString(R.string.alert_internal_server_error));
                }
            }

            @Override
            public void onFailure(Call<SettingsSaveResponse> call, Throwable t) {
                try {
                    showProgressBar(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (t instanceof IOException)
                    DialogFactory.showDropDownNotification(
                            mContext,
                            getString(R.string.alert_information),
                            getString(R.string.alert_file_not_found));
                else DialogFactory.showDropDownNotification(
                        mContext,
                        getString(R.string.alert_information),
                        t.getLocalizedMessage());
            }
        });
    }
    //Network Calling to Switch/Save Language

    private void SaveLanguage(String lng) {
        JsonObject paramObject = new JsonObject();
        paramObject.addProperty("language", lng);

        showProgressBar(true);
        callSaveLanguage(paramObject);
    }

    private void callSaveLanguage(JsonObject params) {
        ApiClient apiClient = ApiClient.getInstance();
        apiClient.saveLanguage(params, new Callback<LanguageSaveResponse>() {
            @Override
            public void onResponse(Call<LanguageSaveResponse> call, final Response<LanguageSaveResponse> response) {
                try {
                    showProgressBar(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getStatus()) {
                        PublicEventFragment.v = null;
                        PrivateEventFragment.v = null;

                        Intent goDashboard = new Intent(mContext, SplashActivity.class);
                        goDashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(goDashboard);
                        mContext.finishAffinity();
                    } else {
                        if (response.body().getMessage() != null && !response.body().getMessage().isEmpty()) {
                            DialogFactory.showDropDownNotification(
                                    mContext,
                                    mContext.getString(R.string.tv_error),
                                    response.body().getMessage());
                        }
                    }
                } else {
                    if (response.code() == 404)
                        DialogFactory.showDropDownNotification(
                                mContext,
                                getString(R.string.alert_information),
                                getString(R.string.alert_api_not_found));
                    if (response.code() == 500)
                        DialogFactory.showDropDownNotification(
                                mContext,
                                getString(R.string.alert_information),
                                getString(R.string.alert_internal_server_error));
                }
            }

            @Override
            public void onFailure(Call<LanguageSaveResponse> call, Throwable t) {
                try {
                    showProgressBar(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (t instanceof IOException)
                    DialogFactory.showDropDownNotification(
                            mContext,
                            getString(R.string.alert_information),
                            getString(R.string.alert_file_not_found));
                else DialogFactory.showDropDownNotification(
                        mContext,
                        getString(R.string.alert_information),
                        t.getLocalizedMessage());
            }
        });
    }

    // Call to remove FCM tokent on logout
    private void removeFCMTokenOnLogout(JsonObject paramObject) {
        ApiClient apiClient = ApiClient.getInstance();
        apiClient.removeTokenOnLogout(paramObject, new Callback<GeneralResponse>() {
            @Override
            public void onResponse(Call<GeneralResponse> call, final Response<GeneralResponse> response) {
                try {
                    showProgressBar(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getStatus()) {

                        if (Constants.FB_IS_LOGIN) {
                            LoginManager.getInstance().logOut();
                            Constants.FB_IS_LOGIN = false;
                        }
                        SharedPreference.saveBoolSharedPrefValue(mContext, Constants.DEVICE_REGISTER, false);
                        FragmentManager fm = mContext.getSupportFragmentManager();
                        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                            mContext.getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        }
                        SharedPreference.logoutDefaults(mContext);
                        Intent signIn = new Intent(mContext, SplashActivity.class);
                        signIn.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(signIn);
                        mContext.finishAffinity();

                    } else {
                        if (response.body().getMessage() != null && !response.body().getMessage().isEmpty()) {
                            DialogFactory.showDropDownNotification(
                                    mContext,
                                    mContext.getString(R.string.tv_error),
                                    response.body().getMessage());
                        }
                    }
                } else {
                    if (response.code() == 404)
                        DialogFactory.showDropDownNotification(
                                mContext,
                                mContext.getString(R.string.alert_information),
                                mContext.getString(R.string.alert_api_not_found));
                    if (response.code() == 500)
                        DialogFactory.showDropDownNotification(
                                mContext,
                                mContext.getString(R.string.alert_information),
                                mContext.getString(R.string.alert_internal_server_error));
                }
            }

            @Override
            public void onFailure(Call<GeneralResponse> call, Throwable t) {
                try {
                    showProgressBar(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (t instanceof IOException)
                    DialogFactory.showDropDownNotification(
                            mContext,
                            mContext.getString(R.string.alert_information),
                            mContext.getString(R.string.alert_file_not_found));
                else if (t instanceof SocketTimeoutException)
                    DialogFactory.showDropDownNotification(
                            mContext,
                            mContext.getString(R.string.alert_information),
                            mContext.getString(R.string.alert_request_timeout));
                else
                    DialogFactory.showDropDownNotification(
                            mContext,
                            mContext.getString(R.string.alert_information),
                            t.getLocalizedMessage());
            }
        });
    }

    @Override
    public void onResume() {
        if (SharedPreference.getSharedPrefValue(mContext, "name") != null) {
            Constants.Name = SharedPreference.getSharedPrefValue(mContext, "name");
            tvName.setText(Constants.Name);
        }
        if (SharedPreference.getSharedPrefValue(mContext, "phone") != null) {

            Constants.Phone = SharedPreference.getSharedPrefValue(mContext, "phone");

            tvPhone.setText(Constants.Phone);
        }
        if (SharedPreference.getSharedPrefValue(mContext, "image") != null) {

            Constants.Image = SharedPreference.getSharedPrefValue(mContext, "image");

            Picasso.with(mContext)
                    .load(URLs.BaseUrlUserImageUsersThumbs + Constants.Image)   //
                    .placeholder(R.drawable.user_placeholder) // optional
                    .error(R.drawable.user_placeholder)         // optional
                    .into(ivProfile);

        }
        super.onResume();
    }
}