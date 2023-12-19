package utility;

import android.content.Context;
import android.content.SharedPreferences;

public class FragmentStackPref {

    public FragmentStackPref yourPreference;
    private SharedPreferences sharedPreferences;

    String fragment = "key";

    public FragmentStackPref getInstance(Context context) {
        if (yourPreference == null) {
            yourPreference = new FragmentStackPref(context);
        }
        return yourPreference;
    }

    public FragmentStackPref(Context context) {
        sharedPreferences = context.getSharedPreferences("YourCustomNamedPreference", Context.MODE_PRIVATE);
    }

    public void storeSessionFragment(String str_Name) {
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor.putString(fragment, str_Name);
        prefsEditor.commit();
    }

    public String getFragmentStack() {
        return sharedPreferences.getString(fragment, "");
    }


}
