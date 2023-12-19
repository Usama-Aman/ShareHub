package com.mobiletouch.sharehub;

import android.os.Bundle;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import adapters.GroupsListAdapter;
import adapters.UsersListAdapter;
import models.GroupsModel;
import models.UserDetail;
import utility.AppUtils;
import utility.Constants;
import utility.SharedPreference;
import utility.SimpleDividerItemDecoration;

public class AddUsersAndGroupsActivity extends AppCompatActivity implements View.OnClickListener {

    private AppCompatActivity mContext;
    private CoordinatorLayout clMainContent;
    private Toolbar toolBar;
    private ImageView btnToolbarBack;
    private ImageView btnToolbarRight;
    private TextView tvToolbarTitle;
    private RecyclerView rvUsersList,rvGroupsList;
    private UsersListAdapter mAdapter;
    private GroupsListAdapter mGAdapter;
    private ArrayList<UserDetail> usersListData = new ArrayList<UserDetail>();
    private ArrayList<GroupsModel> groupsListData = new ArrayList<GroupsModel>();
    private EditText etSearch;
    private Button btnAdd;
    String language = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_users);

        mContext = (AppCompatActivity) this;
        language = SharedPreference.unScrambleText(SharedPreference.getSharedPrefValue(mContext, Constants.Pref_Language));
        initSharedPref();
        getActivityData();
        initToolBar();
        viewInitialize();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }



    @Override
    public void onClick(View v) {
        switch(v.getId()){

            case R.id.btnAdd:

                AppUtils.hideKeyboard(this);
                finish();
                break;

            case R.id.btn_toolbar_back:
                AppUtils.hideKeyboard(this);
                finish();
                break;

            case R.id.clMainContent:
                AppUtils.hideKeyboard(this);
                break;
        }
    }

    private void initSharedPref() {
        /*UserModel userModel = SharedPreference.getUserDetails(mContext);
        if (userModel != null && userModel.getAuthToken() != null) {
            auth_token = userModel.getAuthToken();
        }*/
    }

    private void getActivityData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            usersListData = bundle.getParcelableArrayList("users");
            groupsListData = bundle.getParcelableArrayList("groups");
        }
    }

    private void viewInitialize() {
        clMainContent = findViewById(R.id.clMainContent);
        etSearch = findViewById(R.id.etSearch);
        btnAdd = findViewById(R.id.btnAdd);
        rvUsersList = findViewById(R.id.rvUsersList);
        rvGroupsList = findViewById(R.id.rvGroupsList);

        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    AppUtils.hideKeyboard(mContext);
                    return true;
                }
                return false;
            }
        });

        mAdapter = new UsersListAdapter(this, usersListData, new UsersListAdapter.UsersAdapterListener() {
            @Override
            public void onUserSelected(UserDetail contact) {
                //Toast.makeText(getApplicationContext(), "Selected: " + contact.getUserFullname() + ", " + contact.getUserMobileNumber(), Toast.LENGTH_LONG).show();
            }
        });

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        rvUsersList.setLayoutManager(mLayoutManager);
        rvUsersList.setItemAnimator(new DefaultItemAnimator());
        rvUsersList.addItemDecoration(new SimpleDividerItemDecoration(mContext));
        rvUsersList.setNestedScrollingEnabled(false);
        rvUsersList.setAdapter(mAdapter);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mAdapter.getFilter().filter(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mGAdapter = new GroupsListAdapter(this, groupsListData, new GroupsListAdapter.GroupsAdapterListener() {
            @Override
            public void onGroupSelected(GroupsModel contact) {

            }
        });

        RecyclerView.LayoutManager mGLayoutManager = new LinearLayoutManager(mContext);
        rvGroupsList.setLayoutManager(mGLayoutManager);
        rvGroupsList.setItemAnimator(new DefaultItemAnimator());
        rvGroupsList.addItemDecoration(new SimpleDividerItemDecoration(mContext));
        rvGroupsList.setNestedScrollingEnabled(false);
        rvGroupsList.setAdapter(mGAdapter);

        btnAdd.setOnClickListener(this);
        clMainContent.setOnClickListener(this);
    }

    public void initToolBar() {
        toolBar = (Toolbar) findViewById(R.id.toolbar);
        btnToolbarBack = (ImageView) toolBar.findViewById(R.id.btn_toolbar_back);

        btnToolbarRight = (ImageView) toolBar.findViewById(R.id.btn_toolbar_right);
        btnToolbarRight.setVisibility(View.GONE);

        tvToolbarTitle = (TextView) toolBar.findViewById(R.id.tv_toolbar_title);
        tvToolbarTitle.setVisibility(View.VISIBLE);
        tvToolbarTitle.setText(mContext.getString(R.string.tv_user_list));

        if (language.equalsIgnoreCase("en"))
            btnToolbarBack.setImageResource(R.drawable.ic_white_arrow);
        else
            btnToolbarBack.setImageResource(R.drawable.ic_white_arrow_right);

        btnToolbarBack.setOnClickListener(this);
    }

}
