package utility;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.balysv.materialripple.MaterialRippleLayout;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.mobiletouch.sharehub.R;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AppUtils {

    private static ProgressDialog dialog;
    private static ProgressDialog mProgressDialog;
    private static double latitude;
    private static double longitude;
    private static SharedPreferences pref;
    private static SharedPreferences.Editor editor;
    public static FragmentToActivityMethodAccessListner fragmentCommunicator;
    private static Locale locale;
    private static String languageCode;
    private Context mContext;

    public AppUtils(Context ctx) {
        this.mContext = ctx;
    }

    /**************************************************
     * For MultiLanguage support for whole application
     **************************************************/
    public static void multiLanguageConfiguration(Context mContext) {
        languageCode = SharedPreference.unScrambleText(SharedPreference.getSharedPrefValue(mContext, Constants.Pref_Language));
        if (AppUtils.isSet(languageCode)) {
            locale = new Locale(languageCode);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            mContext.getResources().updateConfiguration(config, mContext.getResources().getDisplayMetrics());
        }
    }

    /********************
     * For Bitmap Streaming
     *******************/
    public static void CopyStream(InputStream is, OutputStream os) {
        final int buffer_size = 1024;
        try {
            byte[] bytes = new byte[buffer_size];
            for (; ; ) {
                int count = is.read(bytes, 0, buffer_size);
                if (count == -1)
                    break;
                os.write(bytes, 0, count);
            }
        } catch (Exception ex) {
        }
    }

    /********************
     * For Bitmap Rotation
     *******************/
    public static Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    /**********************
     * For email validation
     **********************/
    public static boolean isEmailValid(String email) {
        String regExpn = "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";
        CharSequence inputStr = email;
        Pattern pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            return true;
        } else {
            return false;
        }
    }

    /****************************************
     * Get "String" to check is it null or not
     ****************************************/
    public static boolean isSet(String string) {
        if (string != null && string.trim().length() > 0) {
            return true;
        } else {
            return false;
        }
    }

    /****************************************
     * Get "String" to check is it null or not
     ****************************************/
    public String getErrorDefinition(int errorCode) {
        switch (errorCode) {
            case 400:
                return "Missing required parameters or invalid parameters/values (" + errorCode + ")";
            case 401:
                return "Incorrect email or password.(" + errorCode + ")";
            case 403:
                return "Account exists and user provided correct password, but account does not have a valid status.(" + errorCode + ")";
            case 500:
                return "Server Failure. (" + errorCode + ")";
            default:
                return "An error has occurred. (" + errorCode + ")";
        }
    }

    /**********************************
     * Get city and Country name method
     *********************************/
    public static String getcity(Context context, double latitude, double longitude) {
        StringBuilder results = new StringBuilder();
        try {
            Geocoder geocoderr = new Geocoder(context, Locale.getDefault());
            List<Address> addresses_city = geocoderr.getFromLocation(latitude, longitude, 1);
            if (addresses_city.size() > 0) {
                Address address_city = addresses_city.get(0);
                results.append(address_city.getLocality()).append(" ,");
                results.append(address_city.getCountryName());
            }
        } catch (IOException e) {
            //Log.e("tag", e.getMessage());
        }
        return results.toString();
    }

    /***********************************
     * Get city and Country name method
     ***********************************/
    public static String getAddress(Context context, double latitude, double longitude) {
        StringBuilder results = new StringBuilder();
        try {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                results.append(address.getThoroughfare()).append(" ,");
                results.append(address.getLocality()).append(" ,");
                results.append(address.getCountryName());
            }
        } catch (IOException e) {

        }
        return results.toString();
    }

    /***************
     * Alert Dialog
     **************/
    @SuppressWarnings("deprecation")
    public static void AlertEdit(Context context, String string) {
        final AlertDialog alertDialog = new AlertDialog.Builder(context)
                .create();
        alertDialog.setTitle(R.string.app_name);
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.CustomAlertDialogStyle;
        alertDialog.setMessage(string);
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    /*****************************************************
     * Apply Blink Effect On Every TextView And Button etc
     *****************************************************/
    public static void buttonEffect(View button) {
        final int color = Color.parseColor("#00000000");
        try {
            if (button != null) {
                button.setOnTouchListener(new View.OnTouchListener() {

                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN: {
                                v.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
                                v.invalidate();
                                break;
                            }
                            case MotionEvent.ACTION_UP: {
                                v.getBackground().clearColorFilter();
                                v.invalidate();
                                break;
                            }
                        }
                        return false;
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /******************************
     * Apply Effect On Every View(Button, TextView, LinearLayout etc)
     * Please Add In Gradle This Code First (compile 'com.balysv:material-ripple:1.0.2')
     *****************************/
    public static void setRippleEffect(View view) {
        MaterialRippleLayout.on(view)
                .rippleColor(Color.parseColor("#0183b5"))
                //.rippleColor(Color.parseColor("#AAAAAA"))
                .rippleAlpha(0.1f)
                .rippleHover(true)
                .create();
    }

    /******************************
     * Apply Font On Whole Activity
     *****************************/
    public static void applyFont(final Context context, final View root, final String fontPath) {
        try {
            if (root instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) root;
                int childCount = viewGroup.getChildCount();
                for (int i = 0; i < childCount; i++)
                    applyFont(context, viewGroup.getChildAt(i), fontPath);
            } else if (root instanceof TextView)
                ((TextView) root).setTypeface(Typeface.createFromAsset(context.getAssets(), fontPath));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /****************************************
     * Custom Alert Dialog With Custom Layout
     ***************************************/
    public static void CustomAlertDialog(Context context, String msgType, String message) {
        final Dialog customDialog = new Dialog(context);
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customDialog.setCancelable(false);
        //customDialog.getWindow().getAttributes().windowAnimations = R.style.CustomAnimations;
        customDialog.setContentView(R.layout.custom_alert_dailog);

        TextView tv_title = (TextView) customDialog.findViewById(R.id.tv_dialog_title);
        if (msgType != null && !msgType.isEmpty() && msgType.equalsIgnoreCase("Error")) {
            tv_title.setText("Error!");
            tv_title.setTextColor(ContextCompat.getColor(context, R.color.colorRed));
        } else if (msgType != null && !msgType.isEmpty() && msgType.equalsIgnoreCase("Warn")) {
            tv_title.setText("Warn!");
            tv_title.setTextColor(ContextCompat.getColor(context, R.color.colorYellow));
        } else if (msgType != null && !msgType.isEmpty() && msgType.equalsIgnoreCase("Success")) {
            tv_title.setText("Success!");
            tv_title.setTextColor(ContextCompat.getColor(context, R.color.colorGreen));
        } else {
            tv_title.setText(msgType);
        }
        TextView tv_content = (TextView) customDialog.findViewById(R.id.tv_dialog_content);
        tv_content.setText(message);

        Button btn_Ok = (Button) customDialog.findViewById(R.id.btn_dialog_ok);
        btn_Ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog.dismiss();
				/*Intent i = new Intent(getActivity(), MainActivity.class);
				startActivity(i);
				//finish();*/
            }
        });
        customDialog.show();
    }

    /****************************************
     * Set First Word Capitalize Of String
     ***************************************/
    public static String firstWordCapitalize(final String line) {
        if (line != null && line.length() > 0) {
            return Character.toUpperCase(line.charAt(0)) + line.substring(1);
        } else {
            return "";
        }
    }

    /****************************************
     * Convert LatLng To Location variable
     ***************************************/
    public static Location convertLatLngToLocation(LatLng point) {
        Location loc = new Location("");
        loc.setLatitude(point.latitude);
        loc.setLongitude(point.longitude);
        return loc;
    }

    /****************************************
     * Convert Address To Location LatLong variables
     ***************************************/
    public static LatLng getLocationFromAddress(Context context, String strAddress) {
        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;
        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();
            p1 = new LatLng(location.getLatitude(), location.getLongitude());
        } catch (Exception ex) {
            ex.getMessage();
        }
        return p1;
    }

    /*******************************************
     * Decode File For "Out Of Memory" Exception
     *******************************************/
    public static Bitmap decodeFile(File f, int WIDTH, int HIGHT) {
        try {
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);
            // The new size we want to scale to
            final int REQUIRED_WIDTH = WIDTH;
            final int REQUIRED_HIGHT = HIGHT;
            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_WIDTH
                    && o.outHeight / scale / 2 >= REQUIRED_HIGHT)
                scale *= 2;
            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
        }
        return null;
    }

    /*************************
     * To set Image Rotation
     ************************/
    public static int getCameraPhotoOrientation(Context context, Uri imageUri,
                                                String imagePath) {
        int rotate = 0;
        try {
            context.getContentResolver().notifyChange(imageUri, null);
            File imageFile = new File(imagePath);
            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }

    /******************************************
     * To set first letter capitalize of particular string.
     ******************************************/
    public static String capitalize(final String line) {
        return Character.toUpperCase(line.charAt(0)) + line.substring(1);
    }

    public boolean checkDates(Context context, String Todate, String FromDate) {
        //SimpleDateFormat dfDate = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
        boolean b = false;
        try {
            if (dateFormatter.parse(Todate).before(dateFormatter.parse(FromDate))) {
                b = true;
                //AppUtils.ShowToast(context, "Valid Date");//If start date is before end date
            } else if (dateFormatter.parse(Todate).equals(dateFormatter.parse(FromDate))) {
                b = true;
            } else {
                //AppUtils.ShowToast(context, "InValid Date");
                b = false; //If start date is after the end date
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return b;
    }

    /******************************************
     * Get location by using GPSTracker class
     ******************************************/
    public static LatLng getGPSTrackerLocation(Context c) {
        GPSTracker gps = new GPSTracker(c);
        if (gps.canGetLocation()) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
			/*if (latitude !=0.0 && longitude !=0.0) {
				pref = c.getSharedPreferences(Constants.pref_name, c.MODE_PRIVATE);
				editor = pref.edit();
				editor.putString(Constants.Latitude, String.valueOf(latitude));
				editor.putString(Constants.Longitude, String.valueOf(longitude));
				editor.commit();
			}*/
        } else {
            gps.showSettingsAlert();
        }
        return new LatLng(latitude, longitude);
    }

    /******************************************
     * Upload Image With Universal Library
     ******************************************/
    public static void displayUniversalImage(String url, ImageView imageView) {
		/*ImageLoader imageLoader = ImageLoader.getInstance();
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.cacheInMemory(true)
				.cacheOnDisc(true)
				.considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.showImageForEmptyUri(R.drawable.placeholder_b)
				.showImageOnFail(R.drawable.placeholder_b)
				.showImageOnLoading(R.drawable.placeholder_b).build();
		imageLoader.displayImage(url, imageView, options);*/
    }

    /*********************************************************
     * Multiple texts with different color into single TextView
     *********************************************************/
    public static String getColoredSpanned(String text, String color) {
        String input = "<font color=" + color + ">" + text + "</font>";
        return input;
    }

    public static String parseTodaysDate(String time) {
        String inputPattern = "yyyy-MM-dd";
        String outputPattern = "MM-dd-yyyy";

        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);
        Date date = null;
        String str = null;
        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
            //URLogs.i("mini", "Converted Date Today:" + str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String parseTodayCalenderDateOrTime(String time) {
        String inputPattern = "EEE MMM d HH:mm:ss zzz yyyy";
        //String outputPattern = "dd-MM-yyyy";
        String outputPattern = "dd-MM-yyyy";

        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);

            Log.i("mini", "Converted Date Today:" + str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String parseTodaysDateII(String time) {
        String inputPattern = "yyyy-MM-dd HH:mm:ss";
        String outputPattern = "MM-dd-yyyy HH:mm ";

        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);
        Date date = null;
        String str = null;
        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
            //URLogs.i("mini", "Converted Date Today:" + str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String parseTime(String time) {
        //String inputPattern = "HH:mm:ss";
        //String inputPattern = "E yyyy.MM.dd 'at' hh:mm:ss a zzz";
        String inputPattern = "hh:mm a";
        String outputPattern = "HH:mm:ss";

        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);
        Date date = null;
        String str = null;
        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
            //URLogs.i("mini", "Converted Date Today:" + str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String parseTimeReverse(String time) {

        String outputPattern = "hh:mm a";
        String inputPattern = "HH:mm:ss";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern, Locale.US);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern, Locale.US);
        Date date = null;
        String str = null;
        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
            //URLogs.i("mini", "Converted Date Today:" + str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String parseTimeReverseForOpeningHour(String time) {
        //String inputPattern = "HH:mm:ss";
        //String inputPattern = "E yyyy.MM.dd 'at' hh:mm:ss a zzz";
        String outputPattern = "h:mm a";
        String inputPattern = "HH:mm:ss";

        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);
        Date date = null;
        String str = null;
        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
            //URLogs.i("mini", "Converted Date Today:" + str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String parseTimeFromTimePicker(String time) {
        //String inputPattern = "HH:mm:ss";
        //String inputPattern = "E yyyy.MM.dd 'at' hh:mm:ss a zzz";
        String inputPattern = "EEE MMM d HH:mm:ss zzz yyyy";
        //String outputPattern = "HH:mm a";
        String outputPattern = "HH:mm:ss";

        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern, Locale.US);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern, Locale.US);
        Date date = null;
        String str = null;
        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
            //L.i("mini", "Converted Date Today:" + str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String formatTimeFromServer(String dateStr) {
        String inputPattern = "yyyy-MM-dd hh:mm:ss";
        String outputPattern = "hh:mm aa";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        try {
            date = inputFormat.parse(dateStr);
            dateStr = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateStr;
    }

    /**
     * @param s H:m timestamp, i.e. [Hour in day (0-23)]:[Minute in hour (0-59)]
     * @return total minutes after 00:00
     */
    public static int parseHrstoMins(String s) {
        String[] str = s.split(" ");
        String stringHourMins = str[0];
        String[] hourMin = stringHourMins.split(":");
        int hour = Integer.parseInt(hourMin[0]);
        int mins = Integer.parseInt(hourMin[1]);
        int hoursInMins = hour * 60;
        return hoursInMins + mins;
    }

    public static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    /*******************************
     * Method for network is in working state or not.
     ******************************/
    public static boolean isOnline(Context cntext) {
        ConnectivityManager cm = (ConnectivityManager) cntext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()
                && cm.getActiveNetworkInfo().isAvailable()
                && cm.getActiveNetworkInfo().isConnected()) {
            return true;
        }
        return false;
    }

    /*******************************
     * Hide keyboard from edit text
     ******************************/
    public static void hideKeyboard(Activity context) {
        InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = context.getCurrentFocus();
        if (view != null) {
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void hideKeyboardForOverlay(Activity context) {
  /*      InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = context.getCurrentFocus();
        if (view != null) {
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } else {
            try {
                inputManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            } catch (Exception e) {
                e.getMessage();
            }
        }*/
    }

    /*******************************
     * Show keyboard with edit text
     ******************************/
    public static void showKeyboardWithFocus(View v, Activity a) {
        try {
            v.requestFocus();
            InputMethodManager imm = (InputMethodManager) a.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
            a.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*************************************
     * Show toast with your custom messages
     *************************************/
    public static void showToast(Context context, String txt) {
        Toast.makeText(context, txt, Toast.LENGTH_LONG).show();
    }

    /*************************************
     * Show progress bar with callback true or false
     *************************************/
    private static void showProgress(Button btn, ProgressBar progressBar, boolean progressVisible) {
        btn.setEnabled(!progressVisible);
        progressBar.setVisibility(progressVisible ? View.VISIBLE : View.GONE);
    }


    /*************************************
     * Custom progress dialog callback with context
     *************************************/
    public static void showSimpleProgressDialog(Context context, String title,
                                                String msg, boolean isCancelable) {
        try {
            if (mProgressDialog == null) {
                mProgressDialog = ProgressDialog.show(context, title, msg);
                mProgressDialog.setCancelable(isCancelable);
            }

            if (!mProgressDialog.isShowing()) {
                mProgressDialog.show();
            }
        } catch (IllegalArgumentException ie) {
            ie.printStackTrace();
        } catch (RuntimeException re) {
            re.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showSimpleProgressDialog(Context context) {
        showSimpleProgressDialog(context, null, "Loading...", false);
    }

    public static void removeSimpleProgressDialog() {
        try {
            if (mProgressDialog != null) {
                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                    mProgressDialog = null;
                }
            }
        } catch (IllegalArgumentException ie) {
            ie.printStackTrace();
        } catch (RuntimeException re) {
            re.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*************************************
     * Just for checking network is available or not
     *************************************/
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /*************************************
     * Get bitmap from Uri
     *************************************/
    public static Bitmap getBitmapFromUri(Uri uri, Context context)
            throws IOException {
        ParcelFileDescriptor parcelFileDescriptor = context
                .getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor
                .getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    /************************************************
     * Show progress dialog with your custom messages
     ***********************************************/
    public static void showDialog(final String message, final Activity context) {
        context.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                dialog = new ProgressDialog(context);
                dialog.setTitle(context.getString(R.string.app_name));
                dialog.setMessage(message);
                dialog.show();
            }
        });
    }

    /*****************************************************
     * Show error into edit text with your custom messages
     ****************************************************/
    public static void ShowError(EditText et, String error) {
        if (et.length() == 0) {
            et.setError(error);
        }
    }

    /*************************************
     * Get bitmap from Uri
     *************************************/
    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // URLogs exception
            return null;
        }
    }

    public static int resolveTransparentStatusBarFlag(Context ctx) {
        String[] libs = ctx.getPackageManager().getSystemSharedLibraryNames();
        String reflect = null;
        if (libs == null)
            return 0;
        final String SAMSUNG = "touchwiz";
        final String SONY = "com.sonyericsson.navigationbar";
        for (String lib : libs) {
            if (lib.equals(SAMSUNG)) {
                reflect = "SYSTEM_UI_FLAG_TRANSPARENT_BACKGROUND";
            } else if (lib.startsWith(SONY)) {
                reflect = "SYSTEM_UI_FLAG_TRANSPARENT";
            }
        }
        if (reflect == null)
            return 0;
        try {
            Field field = View.class.getField(reflect);
            if (field.getType() == Integer.TYPE) {
                return field.getInt(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void setTranslucentStatus(Window win, boolean on) {
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    /*************************************
     * Get bitmap path with 100% quality
     *************************************/
    public static String getImagePath(Bitmap bitmap, int quality) throws IOException {
        File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + Constants.DEFAULT_IMAGE_DIRECTORY);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        String imagePath = Environment.getExternalStorageDirectory().getAbsolutePath() + Constants.DEFAULT_IMAGE_DIRECTORY + Constants.DEFAULT_IMAGE_NAME;
        File f = new File(imagePath);
        f.createNewFile();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos);
        byte[] bitmapdata = bos.toByteArray();

        //write the bytes in file
        FileOutputStream fos = new FileOutputStream(f);
        fos.write(bitmapdata);
        fos.flush();
        fos.close();
        return imagePath;
    }

    public static String getImagePath(Uri selectedImage, Context ctx) {
        String filePath = null;
        String[] filePathColumn = {MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME};
        Cursor cursor =
                ctx.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;
    }

    /********************************************************
     * Decodes image and scales it to reduce memory consumption
     *******************************************************/
    //
    public static Bitmap decodeFile(File f) {
        try {
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            //Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE = 200;
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            //decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /***********************************************************
     * Below code Working for scale image as aspect ratio:
     Bitmap bitmapImage = BitmapFactory.decodeFile("Your path");
     int nh = (int) ( bitmapImage.getHeight() * (512.0 / bitmapImage.getWidth()) );
     Bitmap scaled = Bitmap.createScaledBitmap(bitmapImage, 512, nh, true);
     your_imageview.setImageBitmap(scaled);
     Compress your image without losing quality like Whatsapp
     **********************************************************/
    public static String compressImage(String filePath) {
        //String filePath = getRealPathFromURI(imageUri);
        Bitmap scaledBitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        //by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
        //you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

        //max Height and width values of the compressed image is taken as 816x612
        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

        //width and height values are set maintaining the aspect ratio of the image
        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;
            }
        }

        //setting inSampleSize value allows to load a scaled down version of the original image
        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);
        //inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;
        //this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];
        try {
            //load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

        //check the rotation of the image and display it properly
        try {
            ExifInterface exif = new ExifInterface(filePath); //imgFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
            } else if (orientation == 3) {
                matrix.postRotate(180);
            } else if (orientation == 8) {
                matrix.postRotate(270);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true); // rotating bitmap
        } catch (Exception e) {
            e.getStackTrace();
        }


        FileOutputStream out = null;
        String filename = getFilename();
        try {
            out = new FileOutputStream(filename);
            //write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return filename;
    }

    public static String getFilename() {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), Constants.DEFAULT_IMAGE_DIRECTORY);
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
        return uriSting;

    }

    private String getRealPathFromURI(String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = mContext.getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    public static String formatDateFromServer(String dateStr) {
        String inputPattern = "yyyy-MM-dd hh:mm:ss";
        String outputPattern = "dd MMM yyyy";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        try {
            date = inputFormat.parse(dateStr);
            dateStr = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateStr;
    }

    public static Boolean validateAdsDate(String startDateTime, String endDateTime) {
        if ((convertStrToDate(getCurrentDate()).after(convertStrToDate(startDateTime)) && convertStrToDate(getCurrentDate()).before(convertStrToDate(endDateTime)))) {
            return true;
        }
        return false;
    }

    public static Date convertStrToDate(String dateTime) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = dateFormat.parse(dateTime);
            return date;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return dateFormat.format(new Date());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static Boolean validateAdsStartDate(String startDateTime) {

        if ((convertStrToDate(getCurrentDate()).after(convertStrToDate(startDateTime)))) {
            return true;
        }
        return false;
    }

    public static Boolean validateAdsEndDate(String endDateTime) {

        if (convertStrToDate(getCurrentDate()).before(convertStrToDate(endDateTime))) {
            return true;
        }
        return false;
    }


    public static Uri getLocalBitmapUri(Context context, Bitmap bmp) {

        // Store image to default external storage directory
        Uri bmpUri = null;
        try {
            File file = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "share_event_qr_image_" + System.currentTimeMillis() + ".png");
            file.getParentFile().mkdirs();
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

    public static void checkGPS(Context ctx) {
        final LocationManager manager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            DialogFactory.showDropDownNotification((Activity) ctx,
                    ctx.getString(R.string.alert_information),
                    ctx.getString(R.string.msg_gps));
        }
    }


    public static void shareLinkToSocial(
            Context ctx,
            String id,
            String title,
            String desc,
            String imagePath,
            FrameLayout progressBar
    ) {

        if (imagePath == null)
            return;
        if (!AppUtils.isSet(imagePath))
            return;
        if (Uri.parse(imagePath) == null)
            return;


        progressBar.setVisibility(View.VISIBLE);
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("sharehub.com/event")
                .appendQueryParameter("event_id", id);

        DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(builder.build())
                .setDomainUriPrefix("https://sharehubapp.page.link")
                .setAndroidParameters(
                        new DynamicLink.AndroidParameters.Builder("com.mobiletouch.sharehub")
                                .setMinimumVersion(1)
                                .setFallbackUrl(Uri.parse("https://play.google.com/store/apps/details?id=com.mobiletouch.sharehub"))
                                .build())
                .setIosParameters(
                        new DynamicLink.IosParameters.Builder("com.mobiletouch.sharehub")
                                .setAppStoreId("1461764253")
                                .build())
                .buildDynamicLink();

        Uri dynamicLinkUri = dynamicLink.getUri();

        FirebaseDynamicLinks.getInstance().createDynamicLink().setLongLink(dynamicLinkUri).buildShortDynamicLink().addOnCompleteListener(((Activity) ctx), new OnCompleteListener<ShortDynamicLink>() {
            @Override
            public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                if (task.isSuccessful()) {
                    if (progressBar != null)
                        progressBar.setVisibility(View.GONE);
                    Uri shortLink = task.getResult().getShortLink();
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_TITLE, title);
                    intent.putExtra(Intent.EXTRA_TEXT, shortLink.toString());
                    intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(imagePath));
                    intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);

                    Intent chooser = Intent.createChooser(intent, ctx.getResources().getString(R.string.tv_select));
                    chooser.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    ((Activity) ctx).startActivityForResult(chooser, 123);
                    //  shareT
                } else {
                    if (progressBar != null)
                        progressBar.setVisibility(View.GONE);
                    String errorMessage = task.getException().getMessage();
                    DialogFactory.showDropDownNotification(
                            ((Activity) ctx),
                            ctx.getString(R.string.tv_error),
                            ctx.getString(R.string.tv_errordynamic_share));
                    Log.d("Dynamic Error", "Error creating Dynamic link " + errorMessage);
                }
            }
        });

        //https://play.google.com/store/apps/details?id=

    }


    public static void inviteToApp(
            Context ctx, String path,
            FrameLayout progressBar
    ) {

        if (path == null)
            return;
        if (!AppUtils.isSet(path))
            return;
        if (Uri.parse(path) == null)
            return;

        try {
            if (progressBar != null)
                progressBar.setVisibility(View.VISIBLE);
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("https")
                    .authority("sharehub.com/group");

            DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                    .setLink(builder.build())
                    .setDomainUriPrefix("https://sharehubapp.page.link")
                    .setAndroidParameters(
                            new DynamicLink.AndroidParameters.Builder("com.mobiletouch.sharehub")
                                    .setMinimumVersion(1)
                                    .setFallbackUrl(Uri.parse("https://play.google.com/store/apps/details?id=com.mobiletouch.sharehub"))
                                    .build())
                    .setIosParameters(
                            new DynamicLink.IosParameters.Builder("com.mobiletouch.sharehub")
                                    .setAppStoreId("1461764253")
                                    .build())
                    .buildDynamicLink();

         Uri dynamicLinkUri = dynamicLink.getUri();

            FirebaseDynamicLinks.getInstance().createDynamicLink().setLongLink(dynamicLinkUri).buildShortDynamicLink().addOnCompleteListener(((Activity) ctx), new OnCompleteListener<ShortDynamicLink>() {
                @Override
                public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                    if (task.isSuccessful()) {
                        if (progressBar != null)
                            progressBar.setVisibility(View.GONE);
                        Uri shortLink = task.getResult().getShortLink();
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("image/*");
                        intent.putExtra(Intent.EXTRA_TITLE, "");
                        intent.putExtra(Intent.EXTRA_TEXT, ctx.getResources().getString(R.string.alert_share_intent_group) + "\n" + shortLink.toString());
                        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(path));
                        intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);

                        Intent chooser = Intent.createChooser(intent, ctx.getResources().getString(R.string.tv_select));
                        chooser.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        ((Activity) ctx).startActivityForResult(chooser, 123);
                        //  shareT
                    } else {
                        if (progressBar != null)
                            progressBar.setVisibility(View.GONE);
                        String errorMessage = task.getException().getMessage();
                        DialogFactory.showDropDownNotification(
                                ((Activity) ctx),
                                ctx.getString(R.string.tv_error),
                                ctx.getString(R.string.tv_errordynamic_share));
                        Log.d("Dynamic Error", "Error creating Dynamic link " + errorMessage);
                    }
                }
            });


        } catch (Exception e) {
            if (progressBar != null)
                progressBar.setVisibility(View.GONE);
            DialogFactory.showDropDownNotification(
                    ((Activity) ctx),
                    ctx.getString(R.string.tv_error),
                    ctx.getString(R.string.tv_errordynamic_share));
        }


        //https://play.google.com/store/apps/details?id=

    }


    public static String getEventTypeByDate(Date startTime, Date nowTime, Date endTime) {
        try {


            if (endTime.after(nowTime)) {
                if (startTime.before(nowTime) && endTime.after(nowTime)) {
                    return "live";
                } else
                    return "future";
            } else if (endTime.before(nowTime)) {
                return "past";
            }
        } catch (Exception e) {
            return "";
        }
        return "";
    }

    public static Date converterStringToDateTime(String strDate) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        try {
            Date date = format.parse(strDate);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }


    public static String getCurrentDateTime() {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        String formattedDate = df.format(c);
        return formattedDate;
    }

    /*************************************
     * Making Custom Marker Bitmap
     *************************************/
    public static BitmapDescriptor bitmapDescriptorFromVector(Context context) {
        Drawable background = ContextCompat.getDrawable(context, R.drawable.ic_map_pin_new);
        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

}
