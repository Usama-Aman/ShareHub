package adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.mobiletouch.sharehub.EventCommentsActivity;
import com.mobiletouch.sharehub.EventVideoCommentsActivity;
import com.mobiletouch.sharehub.MainActivity;
import com.mobiletouch.sharehub.R;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import fragments.EventDetailsFragment;
import models.CheckDataModel;
import models.LoginResult;
import models.NotificationListResponseDatum;
import models.NotificationReadResponse;
import network.ApiClient;
import notifications.NotificationConfig;
import notifications.NotificationUtils;
import retrofit2.Call;
import retrofit2.Callback;
import utility.AppUtils;
import utility.CircleTransform;
import utility.Constants;
import utility.DialogFactory;
import utility.SharedPreference;
import utility.URLogs;
import utility.URLs;

public class NotificationListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM = 0;
    private static final int LOADING = 1;
    private boolean isLoadingAdded = false;
    private boolean retryPageLoad = false;
    private String errorMsg;
    private Context mContext;
    private List<NotificationListResponseDatum> mNotificationList;
    OnItemClickListener listener;

    public interface OnItemClickListener {
        public void onAcceptClick(NotificationListResponseDatum message);

        public void onRejectClick(NotificationListResponseDatum message);
    }

    public NotificationListAdapter(Context context, List<NotificationListResponseDatum> messageList, OnItemClickListener listener) {
        mContext = context;
        mNotificationList = messageList;
        this.listener = listener;
    }

    // Inflates the appropriate layout according to the ViewType.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ITEM:
                viewHolder = getViewHolder(parent, inflater);
                break;
            case LOADING:
                View v2 = inflater.inflate(R.layout.item_progress, parent, false);
                viewHolder = new LoadingVH(v2);
                break;
        }

        return viewHolder;
    }

    @NonNull
    private RecyclerView.ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
        RecyclerView.ViewHolder viewHolder;
        View v1 = inflater.inflate(R.layout.item_notification_attend, parent, false);
        viewHolder = new NotificationHolder(v1);
        return viewHolder;
    }

    // Passes the message object to a ViewHolder so that the contents can be bound to UI.
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        switch (holder.getItemViewType()) {
            case ITEM:
                ((NotificationHolder) holder).bind(mNotificationList.get(position));
                break;
            case LOADING:
                LoadingVH loadingVH = (LoadingVH) holder;
                if (retryPageLoad) {
                    loadingVH.mErrorLayout.setVisibility(View.VISIBLE);
                    loadingVH.mProgressBar.setVisibility(View.GONE);

                    loadingVH.mErrorTxt.setText(
                            errorMsg != null ?
                                    errorMsg :
                                    mContext.getString(R.string.alert_msg_unknown));
                } else {
                    loadingVH.mErrorLayout.setVisibility(View.GONE);
                    loadingVH.mProgressBar.setVisibility(View.VISIBLE);
                }
                break;
        }
    }


    //Received Message View Holder
    public class NotificationHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvTitle, tvDesc, tvUnread;
        Button btnAccept, btnReject;
        CircleImageView ivNotif;

        NotificationHolder(View itemView) {
            super(itemView);
            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvDesc = (TextView) itemView.findViewById(R.id.tvDesc);

            ivNotif = (CircleImageView) itemView.findViewById(R.id.ivNotif);
            tvUnread = (TextView) itemView.findViewById(R.id.tvUnread);

            btnAccept = (Button) itemView.findViewById(R.id.btnAccept);
            btnReject = (Button) itemView.findViewById(R.id.btnReject);
        }

        void bind(final NotificationListResponseDatum message) {
            try {
                btnAccept.setVisibility(View.GONE);
                btnReject.setVisibility(View.GONE);
                itemView.setEnabled(true);

                if (message.getNotificationType().equalsIgnoreCase("requestToAttend")) {
                    // If the current user is the sender of the message
                    btnAccept.setVisibility(View.VISIBLE);
                    btnReject.setVisibility(View.VISIBLE);
                }

                if (message.getNotificationType().equalsIgnoreCase("AdminNotification"))
                    itemView.setEnabled(false);


                tvDate.setText(AppUtils.formatTimeFromServer(message.getCreatedAt()));
                tvTitle.setText(message.getUser().getUserFullname());
                tvDesc.setText(message.getMessage());

                if (AppUtils.isSet(message.getUser().getUserPhoto())) {
                    Picasso.with(mContext)
                            .load(URLs.BaseUrlUserImageUsersThumbs + message.getUser().getUserPhoto())   //
                            .transform(new CircleTransform())
                            .placeholder(R.drawable.user_placeholder) // optional
                            .error(R.drawable.user_placeholder)         // optional
                            .into(ivNotif);
                } else {
                    Picasso.with(mContext)
                            .load("")   //
                            .transform(new CircleTransform())
                            .placeholder(R.drawable.user_placeholder) // optional
                            .error(R.drawable.user_placeholder)         // optional
                            .into(ivNotif);
                }

                tvUnread.setVisibility(View.GONE);
                if (message.getReadStatus() == 0) {
                    tvUnread.setVisibility(View.VISIBLE);
                }

                btnAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onAcceptClick(message);
                    }
                });


                btnReject.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onRejectClick(message);
                    }
                });


                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (message.getNotificationType().equalsIgnoreCase("comment") || message.getNotificationType().equalsIgnoreCase("media_like")) {

                            int user_id = 0;
                            int event_id = 0;
                            int notify_id = 0;
                            int media_id = 0;
                            Boolean islike = false;
                            String media_type = "";
                            String media_url = "";
                            String isSelf = "";
                            notify_id = message.getNotiId();

                            if (message.getReadStatus() == 0) {
                                JsonObject paramObject = new JsonObject();
                                paramObject.addProperty("notification_id", notify_id);
                                removeNotification(paramObject);
                            }
                            try {
                                JSONObject notificationData = new JSONObject(message.getNotificationData());
                                user_id = message.getFromUserId();
                                event_id = notificationData.getInt("event_id");
                                media_id = notificationData.getJSONObject("media").getInt("emedia_id");
//                                islike=notificationData.getJSONObject("media").getBoolean("is_liked");
                                islike = false;
                                media_type = notificationData.getJSONObject("media").getString("emedia_type");
                                if (media_type.equalsIgnoreCase("image")) {
                                    if (SharedPreference.isUserLoggedIn(mContext) && SharedPreference.getUserDetails(mContext) != null) {
                                        LoginResult userModel = SharedPreference.getUserDetails(mContext);
                                        if (userModel.getUserData().getUserId().equals(user_id)) {
                                            CheckDataModel.isSelf = "1";
                                        } else {
                                            CheckDataModel.isSelf = "0";

                                        }
                                    }
                                    media_url = notificationData.getJSONObject("media").getString("fullImage");
                                    Intent intent = new Intent(mContext, EventCommentsActivity.class);
                                    intent.putExtra("url", media_url);
                                    intent.putExtra("event_id", event_id);
                                    intent.putExtra("media_id", media_id);
                                    intent.putExtra("islike", islike);
                                    intent.putExtra("fromNotifications", true);
                                    mContext.startActivity(intent);

                                } else {
                                    if (SharedPreference.isUserLoggedIn(mContext) && SharedPreference.getUserDetails(mContext) != null) {
                                        LoginResult userModel = SharedPreference.getUserDetails(mContext);
                                        if (userModel.getUserData().getUserId().equals(user_id)) {
                                            CheckDataModel.isSelf = "1";
                                        } else {
                                            CheckDataModel.isSelf = "0";

                                        }
                                    }
                                    URLogs.m("fromNotifications: " + true);
                                    media_url = notificationData.getJSONObject("media").getString("fullImage");
                                    Intent intent = new Intent(mContext, EventVideoCommentsActivity.class);
                                    intent.putExtra("url", media_url);
                                    intent.putExtra("event_id", event_id);
                                    intent.putExtra("media_id", media_id);
                                    intent.putExtra("islike", islike);
                                    intent.putExtra("fromNotifications", true);
                                    mContext.startActivity(intent);

                                }
                            } catch (Throwable t) {
                                t.printStackTrace();
                            }


                        } else {

                            int user_id = 0;
                            int event_id = 0;
                            int notify_id = 0;
                            notify_id = message.getNotiId();

                            if (message.getReadStatus() == 0) {
                                JsonObject paramObject = new JsonObject();
                                paramObject.addProperty("notification_id", notify_id);
                                removeNotification(paramObject);
                            }
                            try {
                                JSONObject notificationData = new JSONObject(message.getNotificationData());
                                user_id = notificationData.getInt("user_id");
                                event_id = notificationData.getInt("event_id");
                            } catch (Throwable t) {
                                t.printStackTrace();
                            }
                            EventDetailsFragment optionsFrag = new EventDetailsFragment();
                            Bundle args = new Bundle();
                            args.putString("event_id", Integer.toString(event_id));
                            args.putString("isAttend", "1");
                            if (SharedPreference.isUserLoggedIn(mContext) && SharedPreference.getUserDetails(mContext) != null) {
                                LoginResult userModel = SharedPreference.getUserDetails(mContext);
                                if (userModel.getUserData().getUserId().equals(user_id)) {
                                    args.putString("isself", "1");
                                } else {
                                    args.putString("isself", "0");
                                }
                            }
                            args.putString("isSelfProfile", "notification");
                            optionsFrag.setArguments(args);
                            ((MainActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.container, optionsFrag, "OptionsFragment").addToBackStack(null).commit();
                        }
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


    //Loading view holder
    protected class LoadingVH extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ProgressBar mProgressBar;
        private ImageButton mRetryBtn;
        private TextView mErrorTxt;
        private RelativeLayout mErrorLayout;

        public LoadingVH(View itemView) {
            super(itemView);
            mProgressBar = (ProgressBar) itemView.findViewById(R.id.loadmore_progress);
            mRetryBtn = (ImageButton) itemView.findViewById(R.id.loadmore_retry);
            mErrorTxt = (TextView) itemView.findViewById(R.id.loadmore_errortxt);
            mErrorLayout = (RelativeLayout) itemView.findViewById(R.id.loadmore_errorlayout);
            mProgressBar.setVisibility(View.GONE);
            mErrorLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.loadmore_retry:
                case R.id.loadmore_errorlayout:
                    showRetry(false, null);
                    //mCallback.retryPageLoad();
                    break;
            }
        }
    }


    /********************************************************************************/

    @Override
    public int getItemCount() {
        return mNotificationList == null ? 0 : mNotificationList.size();
    }

    /*
     * Displays Pagination retry footer view along with appropriate errorMsg
     * @param show
     * @param errorMsg to display if page load fails
     */
    public void showRetry(boolean show, @Nullable String errorMsg) {
        retryPageLoad = show;
        notifyItemChanged(mNotificationList.size() - 1);
        if (errorMsg != null) this.errorMsg = errorMsg;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == mNotificationList.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /************************************************************************
     * Helper Function
     ************************************************************************/
    public void add(NotificationListResponseDatum r) {
        try {
            mNotificationList.add(r);
            notifyItemInserted(mNotificationList.size() - 1);
        } catch (IndexOutOfBoundsException e) {
            notifyDataSetChanged();
            e.printStackTrace();
        }
    }

    public void addAll(List<NotificationListResponseDatum> mDataSet) {
        for (NotificationListResponseDatum result : mDataSet) {
            add(result);
        }
    }

    public void remove(NotificationListResponseDatum r) {
        try {
            int position = mNotificationList.indexOf(r);
            if (position > -1) {
                mNotificationList.remove(position);
                notifyItemRemoved(position);
            }
        } catch (IndexOutOfBoundsException e) {
            notifyDataSetChanged();
            e.printStackTrace();
        }
    }

    public void clear() {
        isLoadingAdded = false;
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new NotificationListResponseDatum());
    }

    public void removeLoadingFooter() {
        try {
            isLoadingAdded = false;
            int position = mNotificationList.size() - 1;
            NotificationListResponseDatum result = getItem(position);
            if (result != null) {
                mNotificationList.remove(position);
                notifyItemRemoved(position);
            }
        } catch (IndexOutOfBoundsException e) {
            notifyDataSetChanged();
            e.getStackTrace();
        }
    }

    public NotificationListResponseDatum getItem(int position) {
        return mNotificationList.get(position);
    }

    /************************************************************************/

    private void removeNotification(JsonObject paramObject) {
        ApiClient apiClient = ApiClient.getInstance();
        apiClient.removeNotification(paramObject, new Callback<NotificationReadResponse>() {
            @Override
            public void onResponse(Call<NotificationReadResponse> call, final retrofit2.Response<NotificationReadResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getStatus()) {
                        SharedPreference.saveIntegerSharedPrefValue(mContext, Constants.pref_notification_counter, response.body().getUnreadNotifications());
                        if (!NotificationUtils.isAppIsInBackground(mContext)) {
                            Intent pushNotificationBroadCast = new Intent(NotificationConfig.PUSH_NOTIFICATION);
                            LocalBroadcastManager.getInstance(mContext).sendBroadcast(pushNotificationBroadCast);
                        }
                    } else {
                        if (response.body().getMessage() != null && !response.body().getMessage().isEmpty()) {
                            DialogFactory.showDropDownNotification(
                                    ((MainActivity) mContext),
                                    ((MainActivity) mContext).getString(R.string.tv_error),
                                    response.body().getMessage());
                        }
                    }
                } else {
                    if (response.code() == 404)
                        DialogFactory.showDropDownNotification(
                                ((MainActivity) mContext),
                                ((MainActivity) mContext).getString(R.string.alert_information),
                                ((MainActivity) mContext).getString(R.string.alert_api_not_found));
                    if (response.code() == 500)
                        DialogFactory.showDropDownNotification(
                                ((MainActivity) mContext),
                                ((MainActivity) mContext).getString(R.string.alert_information),
                                ((MainActivity) mContext).getString(R.string.alert_internal_server_error));
                }
            }

            @Override
            public void onFailure(Call<NotificationReadResponse> call, Throwable t) {
                if (t instanceof IOException)
                    DialogFactory.showDropDownNotification(
                            ((MainActivity) mContext),
                            ((MainActivity) mContext).getString(R.string.alert_information),
                            ((MainActivity) mContext).getString(R.string.alert_file_not_found));
                else if (t instanceof SocketTimeoutException)
                    DialogFactory.showDropDownNotification(
                            ((MainActivity) mContext),
                            ((MainActivity) mContext).getString(R.string.alert_information),
                            ((MainActivity) mContext).getString(R.string.alert_request_timeout));
                else
                    DialogFactory.showDropDownNotification(
                            ((MainActivity) mContext),
                            ((MainActivity) mContext).getString(R.string.alert_information),
                            t.getLocalizedMessage());
            }
        });
    }
}