package application;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.facebook.FacebookSdk;

import utility.Constants;
import utility.SharedPreference;

public class AppController extends Application implements Application.ActivityLifecycleCallbacks{
    public static AppController mInstance;


    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreference.writeSharedPreference(getApplicationContext(), 0,"cntSP", "cntKey");
        registerActivityLifecycleCallbacks(this);
        FacebookSdk.sdkInitialize(this);

        mInstance = this;
        if (SharedPreference.unScrambleText(SharedPreference.getSharedPrefValue(this, Constants.Pref_Language)) == null) {
            SharedPreference.saveSharedPrefValue(this, Constants.Pref_Language, "en");
        }
    }
    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        Log.d("activity created","true");
    }

    @Override
    public void onActivityStarted(Activity activity) {
        Log.d("activity started","truei");
        //CrickSignUpLoginManager.getCrickManager().RefreshToken();
    }

    @Override
    public void onActivityResumed(Activity activity) {
        Log.d("activity resume","true");
    }

    @Override
    public void onActivityPaused(Activity activity) {
        Log.d("activty pause","trueeeeeee");
    }

    @Override
    public void onActivityStopped(Activity activity) {
        Log.d("activity stopped","true");
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        Log.d("activity savedIns","true");
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
       /* AlarmBroadCast alarm = new AlarmBroadCast();
        alarm.cancelAlarm(getApplicationContext());*/
        Log.d("activity destroy","true");
    }

    @Override
    protected void attachBaseContext(Context base) {
        //super.attachBaseContext(base);
        super.attachBaseContext(LocaleHelper.onAttach(base));
        //MultiDex.install(this);
        Log.d("activity baseContext","true");
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    /* For Saving secret token and refresh token according to mechanism
    * @param token
    * @ refreshtoken*/

}