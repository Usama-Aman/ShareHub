package utility;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.mobiletouch.sharehub.R;

public class Constants {

    private static Constants ourInstance = new Constants();

	public static Constants getInstance() {
		return ourInstance;
	}

	/**************************************************
	 *		 SOCKET BROADCAST RECEIVERS KEYS
	 **************************************************/
	public static final String ACTION_REFRESH_DRIVERS       = "refreshDrivers";
	public static final String ACTION_CANCEL_BOOKING        = "cancelBooking";
	public static final String ACTION_DRIVER_ACCEPT_BOOKING   = "driverAcceptBooking";
	public static final String ACTION_ACCEPTED_BOOKING      = "acceptedBooking";
	public static final String ACTION_END_BOOKING           = "endBooking";
	public static final String ACTION_CANCEL_BOOKED_BOOKING = "cancelBookedBooking";
	public static final String ACTION_SOCKET_DISCONNECT     = "socketDisconnect";
	/**************************************************
	 *		 REVERSE GEO CODING KEYS
	 **************************************************/
	public static final int USE_ADDRESS_NAME = 1;
	public static final int USE_ADDRESS_LOCATION = 2;
	public static final String PACKAGE_NAME = "com.sample.foo.simplelocationapp";
	public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
	public static final String RESULT_DATA_KEY = PACKAGE_NAME + ".RESULT_DATA_KEY";
	public static final String RESULT_ADDRESS = PACKAGE_NAME + ".RESULT_ADDRESS";
	public static final String LOCATION_LATITUDE_DATA_EXTRA = PACKAGE_NAME + ".LOCATION_LATITUDE_DATA_EXTRA";
	public static final String LOCATION_LONGITUDE_DATA_EXTRA = PACKAGE_NAME + ".LOCATION_LONGITUDE_DATA_EXTRA";
	public static final String LOCATION_NAME_DATA_EXTRA = PACKAGE_NAME + ".LOCATION_NAME_DATA_EXTRA";
	public static final String FETCH_TYPE_EXTRA = PACKAGE_NAME + ".FETCH_TYPE_EXTRA";
	/**************************************************
	 *		 RESPONSE KEYS
	 **************************************************/
	public static final String RESPONSE_KEY_ERROR       = "error";
	public static final String RESPONSE_KEY_MESSAGE     = "message";
	public static final String RESPONSE_KEY_BOOKING     = "booking";
	//public static final String RESPONSE_KEY_DRIVER_BOOKED     = "bookedDriver";
	public static final String RESPONSE_KEY_USER        = "user";
	public static final String RESPONSE_KEY_INVALID_KEY = "invalidKey";
	public static final String RESPONSE_KEY_BOOKINGS     = "bookings";
	public static final String USER_PHONE_VERIFY = "user_phone_verify";
	public static final String DEVICE_TYPE = "android";
	public static final String DEVICE_REGISTER = "Register";
	public static final String DEVICE_TOKEN_ID = "device_token_id";
	public static final String DIRECTION_URL = "https://maps.googleapis.com/maps/api/directions/json?origin=%f,%f&destination=%f,%f&units=metric";

	public static final String SERVER_URL=""+ URLs.BaseUrl;
	public static final String ADDRESS_SERVER_URL="http://maps.googleapis.com/maps/api/geocode/json";
	public static final String USER_PERSISTENT_DATA="userinfo";
	public static final String CUSTOM_FONT_URL_A="MavenPro-Regular_1.ttf";
	public static final String CUSTOM_FONT_URL_B="MavenPro-Medium_1.ttf";
	public static final String CUSTOM_FONT_URL_C="MavenPro-Bold_1.ttf";
	public static final String CUSTOM_FONT_URL_Arabic="Arial-0015-R.ttf";
	/********************************
     * Shared Preferences Attributes
     *******************************/
	public static String INTERNET_ALERT = "Connection Error...!";
	public static String GetLatiTude = "";
	public static String GetLongiTude = "";
	public static String Latitude = "latitude";
	public static String Longitude = "longitude";
	public static String Location = "location";
	public static String CardInfo = "CardInfo";
	public static String CardHolderName = "CardHolderName";
	public static String SettingFlag = "loginflag";
	public static String FbIsLogin = "fblogin";
	public static String GetLatiTudeValidator = "";
	public static String GetLongiTudeValidator = "";
	public static final String PREF_KEY_TOTAL_BOOKINGS = "totalBooking";
	/*********************************************
     * Notify On Off Shared Preferences Attributes
     ********************************************/
	public static String NOTIFICATION_STATE = "NotifyState";
	public static String priceNotifyOnOff = "priceNotifyOnOff";
	public static String notifyRecallTime = "notifyRecallTime";
	public static String saveStateNotify = "saveStateNotify";
	/*********************************************
     * Coiffeur Data Shared Preferences Attributes
     ********************************************/
	public static String CATEGORY_NAME = "categoryName";
	public static String SUB_CATEGORIES = "subCategories";
	public static String ADDRESS = "address";
	public static String DATE = "date";
	public static String TIME = "time";

	public static String Name = "";
	public static String Image = "";
	public static String Phone = "";
	/*******************************
     * Shared Preferences Attributes
     ******************************/
	public static String pref_name = "Recovery";
	public static String PREF_KEY_DEVICE_TOKEN = "devicetoken";
	public static String TAG_PREF_REG_ID = "pref_device_token";
	public static String Pref_SignUp_DeviceId = "device_id";
	/***************************
	 * SignUp Attributes
	 ***************************/
	public static String pref_notification_counter = "notify_counter";
	public static String Pref_SignUp_phone = "phone";
	public static String Pref_SignUp_Address = "address";
	public static String Pref_SignUp_UserImg = "image";
	public static String Pref_SignUp_firstname = "firstname";
	public static String Pref_SignUp_name = "name";
	public static String Pref_SignUp_lastname = "lastname";
	public static String Pref_SignUp_Email = "email";
	public static String Pref_SignUp_Password = "password";
	public static String Pref_SignUp_auth_token = "auth_token";
	public static String Pref_SignUp_Id = "u_id";
	public static String Pref_SignUp_CountryName = "country";
	public static String Pref_SignUp_lat = "lat";
	public static String Pref_SignUp_lon = "lon";
	public static String Pref_SignUp_CityName = "city";
	public static String Pref_SignUp_CardNumber = "card";
	public static String Pref_SignUp_CardDate = "date";
	public static String Pref_SignUp_SecurityCode = "security";
	public static String Pref_Accept_Booking = "acceptedBooking";
	public static String Pref_Driver_Name = "driverName";
	public static String Pref_Driver_phone = "driverPhone";
	public static String Pref_Driver_Image = "driverImage";
	public static String Pref_Adv_Images = "advImages";
	/***************************
	 * First Build Identify Attributes
	 ***************************/
	public static String Pref_Language = "language";
	public static String App_First_Install = "firstInstall";
	public static boolean APP_FIRST_INSTALL = false;

	public static final int SUCCESS_RESULT = 0;
	public static final int FAILURE_RESULT = 1;
	public static final String LOCATION_DATA_EXTRA = "LOCATION_DATA_EXTRA";
	public static final String LOCATION_DATA_AREA = "LOCATION_DATA_AREA";
	public static final String LOCATION_DATA_CITY = "LOCATION_DATA_CITY";
	public static final String LOCATION_DATA_STREET = "LOCATION_DATA_STREET";

	private static ProgressDialog dialog;
	private static ProgressDialog myDialog;
	public static boolean FB_IS_LOGIN = false;
	public static int recoveryId = 2;
	public static final String INTENT_KEY_PIC = "key_pic";
	public static final String INTENT_KEY_CROPPED_IMAGE = "key_cropped_image";
	public static final String DEFAULT_IMAGE_DIRECTORY = "/CropMe/images/";
	public static final String DEFAULT_IMAGE_NAME = "CROP_ME_IMG.jpg";
	public static String bookingType = "DRIVER_RECOVERY_SERVICE";
	public static final String[] PERMISSIONS = new String[] {"publish_stream","email","publish_actions"};
	public static final String TWITTER_CONSUMER_KEY		= "ctMoqMx1uSIb65I1QOcdWOS1a";//"Xulr2nQqF615gLXWQia5YIuSC";							// "4YGQIZmnT9Urb2WiDFhg0gsQu";
	public static final String TWITTER_CONSUMER_SECRET		= "xaJmbgzAb3z7eLd00f5YioxWBzxG0vNvPTgEkQrOlprCsVi0qG";
	//public static Twitter twitter;
	public static final String TWITTER_CALLBACK_URL		= "oauth://trendscale";
	//public static RequestToken requestToken;
	public static String URL_TWITTER_OAUTH_VERIFIER	= "oauth_verifier";
	public static String APP_ID = "391248557585658";
	public static final String Lat="31.521191";
	public static final String Long="74.356531";
	public static final String points = "31.555097, 74.356230";
	public static String Country[]	= { "Andorra", "United Arab Emirates", "Afghanistan", "Antigua and Barbuda",
			"Anguilla", "Albania", "Armenia", "Angola", "Antarctica", "Argentina", "American Samoa", "Austria", "Australia",
			"Aruba", "�land", "Azerbaijan", "Bosnia and Herzegovina", "Barbados", "Bangladesh", "Belgium", "21Burkina Faso",
			"Bulgaria", "Bahrain", "Burundi", "Benin", "Saint Barth�lemy", "Bermuda", "Brunei", "Bolivia", "Bonaire", "Brazil",
			"Bahamas", "Bhutan", "Bouvet Island", "Botswana", "Belarus", "Belize", "Canada", "Cocos [Keeling] Islands",
			"Democratic Republic of the Congo", "Central African Republic", "Republic of the Congo", "Switzerland", "Ivory Coast",
			"Cook Islands", "Chile", "Cameroon", "China", "Colombia", "Costa Rica", "Cuba", "Cape Verde", "Curacao", "Christmas Island",
			"Cyprus", "Czech Republic", "Germany", "Djibouti", "Denmark", "Dominica", "Dominican Republic", "Algeria", "Ecuador",
			"Estonia", "Egypt", "Western Sahara", "Eritrea", "Spain", "Ethiopia", "7Finland", "Fiji", "Falkland Islands", "Micronesia",
			"Faroe Islands", "France", "Gabon ", "United Kingdom", "Grenada", "Georgia", "French Guiana", "Guernsey", "Ghana",
			"Gibraltar", "ibraltar", "Greenland", "Gambia", "Guinea", "Guadeloupe", "Equatorial Guinea", "Greece", "South Georgia and the South Sandwich Islands",
			"Guatemala", "Guam", "Guinea-Bissau", "Guyana ", "Hong Kong ", "Heard Island and McDonald Islands", "Honduras", "Croatia",
			"Haiti", "Hungary", "Indonesia", "Ireland", "Israel", "Isle of Man", "India", "British Indian Ocean Territory", "Iraq",
			"Iran", "Iceland", "Italy", "Jersey", "Jamaica", "Jordan", "Japan", "Kenya", "Kyrgyzstan", "Cambodia", "Kiribati",
			"Comoros", "Saint Kitts and Nevis", "North Korea", "South Korea", "Kuwait", "Cayman Islands", "Kazakhstan", "Laos",
			"Lebanon", "Saint Lucia", "Liechtenstein", "Sri Lanka", "Liberia", "Lesotho", "Lithuania", "Luxembourg", "Latvia",
			"Libya", "Morocco", "Monaco", "Moldova", "Montenegro", "Saint Martin", "Madagascar", "Marshall Islands", "Macedonia",
			"Mali", "Myanmar [Burma]", "Mongolia", "Macao", "Northern Mariana Islands", "Martinique", "Mauritania", "Montserrat",
			"Malta", "Mauritius", "Maldives", "Malawi", "Mexico", "Malaysia", "Mozambique", "Namibia", "New Caledonia", "Niger",
			"Norfolk Island", "Nigeria", "Nicaragua", "Netherlands", "Norway", "Nepal", "Nauru", "Niue", "New Zealand", "Oman",
			"Panama", "Peru", "French Polynesia", "Papua New Guinea", "Philippines", "Pakistan", "Poland", "Saint Pierre and Miquelon",
			"Puerto Rico", "Palestine", "Portugal", "Palau", "Paraguay", "Qatar", "R�union", "Romania", "Serbia", "Russia", "Rwanda",
			"Saudi Arabia", "Solomon Islands", "Seychelles", "Sudan", "Sweden", "Singapore", "Saint Helena", "Slovenia",
			"Svalbard and Jan Mayen", "Slovakia", "Sierra Leone", "San Marino", "Senegal", "Somalia", "Suriname", "South Sudan",
			"S�o Tom� and Pr�ncipe", "El Salvador", "Sint Maarten", "Syria", "Swaziland", "Turks and Caicos Islands", "Chad",
			"French Southern Territories", "Togo", "Thailand", "Tajikistan", "Tokelau", "East Timor", "Turkmenistan", "Tunisia",
			"Tonga", "Turkey", "Trinidad and Tobago", "Tuvalu", "Taiwan", "Tanzania", "Ukraine", "Uganda", "U.S. Minor Outlying Islands",
			"United States", "Uruguay", "Uzbekistan", "Vatican City", "Saint Vincent and the Grenadines", "Venezuela",
			"British Virgin Islands", "U.S. Virgin Islands", "Vietnam", "Vanuatu", "Wallis and Futuna", "Samoa", "Kosovo", "Yemen",
			"Mayotte", "South Africa", "Zambia", "Zimbabwe" };
	public static String USERDEFAULT_ISLOGGEDIN = "isUserLoggedIn";
	public static String USERDEFAULT_USER_DATA = "userData";
	public static String USERDEFAULT_SORTING_DATA = "sortingData";
	public static String USERDEFAULT_GCM_STRING;

	public static class UserFBLoginData{
		public static String USER_ID="userid";
		public static String USER_NAME="username";
		public static boolean IS_LOGIN=false;
	}

	public static class EntityTypes{
		public static final String REVIEW="review";
		public static final String PROMOTION="promotion";
		public static final String MENU="menu";
		public static final String DAILY_DEAL="dailydeal";

		public static final String ADD_COMMENT="add_comment";
		public static final String TYPE_KEY="review_type";
		public static final String ACTION_KEY="main_action";
	}

	public static class ServerCodes{
		public static final String STATUS_FAIL="fail";
		public static final String STATUS_OK="ok";
	}

	public static class ServerRequestMethod{
		public static final int GET=0;
		public static final int POST=1;
	}

	public static enum ServiceAction{
		recoveryTypesList,registerList,requestPinList,verifyPinData,changePassData,forgotPassData,
		loginData,serviceTypesList,serviceProvidersList,requestServiceData,nearByServiceProvidersData,
		userProfileUpdateData,estimatedPriceData,myOrderHistoryData,myCreditCardData,ratingServiceData
	};

	public static void switchOnOff(ImageButton btn){
		if (!btn.isSelected())
			btn.setSelected(!btn.isSelected());
		else
			btn.setSelected(!btn.isSelected());
	}

	public static void showDialog(final String message, final Activity context){

		context.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				try {
					dialog = new ProgressDialog(context, R.style.CustomProgressDialogStyle);
					dialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large_Inverse);
					dialog.setCancelable(false);
					dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
						@Override
						public void onCancel(DialogInterface dialog) {
							dialog.dismiss();
						}
					});
					dialog.show();
				}catch (Exception e){e.printStackTrace();}
			}
		});
	}

	 public  static void dismissDialog() throws Exception {
		if(dialog != null && dialog.isShowing()){
			dialog.dismiss();
			dialog = null;
		}
	}

	public static void showDefaultDialog(final String message, final Activity context){

		context.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				try {
					myDialog = ProgressDialog.show(context, null, null);
					ProgressBar spinner = new ProgressBar(context, null,android.R.attr.progressBarStyle);
					spinner.getIndeterminateDrawable().setColorFilter(Color.parseColor("#53CBF1"), android.graphics.PorterDuff.Mode.SRC_IN);
					myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
					myDialog.setContentView(spinner);
					myDialog.setMessage(message);
					myDialog.setCancelable(false);
					myDialog.show();

					/*myDialog = new ProgressDialog(context);
					myDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large_Inverse);
					myDialog.setMessage(message);
					myDialog.setCancelable(false);
					myDialog.show();*/
				}catch (Exception e){e.printStackTrace();}
			}
		});
	}

	public  static void dismissDefaultDialog() throws Exception {
		if(myDialog != null && myDialog.isShowing()){
			myDialog.dismiss();
			myDialog = null;
		}
	}

	public static void showUploadingDialog(final String message, final Activity context){

		context.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				try {
					myDialog = new ProgressDialog(context, R.style.CustomProgressDialogStyle);
					myDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large_Inverse);
					myDialog.setCancelable(false);
					myDialog.show();
				}catch (Exception e){e.printStackTrace();}
			}
		});
	}
	 public  static void dismissUploadingDialog() throws Exception {
		if(myDialog!=null && myDialog.isShowing()){
			myDialog.dismiss();
			myDialog=null;
		}
	}

	public static void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			// pre-condition
			return;
		}
		int totalHeight = 0;
		int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
			totalHeight += listItem.getMeasuredHeight();
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
		listView.requestLayout();
	}

	public static void setListViewHeightBasedOnChildren(ListView listView , int hight) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			// pre-condition
			return;
		}
		int totalHeight = 0;
		int oneViewHeight =0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			oneViewHeight = listItem.getMeasuredHeight();
			totalHeight += listItem.getMeasuredHeight();
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		if(totalHeight >hight){
			params.height = hight;
		} else{
			params.height = totalHeight+(oneViewHeight/2);
		}
		listView.setLayoutParams(params);
	}

	public static void setListViewHeightHalf(ListView listView , int hight) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}
		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		@SuppressWarnings("unused")
		int finalHight =totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		params.height = hight;
		listView.setLayoutParams(params);
	}

	public static void updateListViewHeight(ListView myListView) {
		ListAdapter myListAdapter = myListView.getAdapter();
		if (myListAdapter == null) {
			return;
		}
		//get listview height
		int totalHeight = 0;
		int adapterCount = myListAdapter.getCount();
		for (int size = 0; size < adapterCount ; size++) {
			View listItem = myListAdapter.getView(size, null, myListView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}
		//Change Height of ListView
		ViewGroup.LayoutParams params = myListView.getLayoutParams();
		params.height = totalHeight + (myListView.getDividerHeight() * (adapterCount - 1));
		myListView.setLayoutParams(params);
	}

	public static void setListViewNewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}
		int totalHeight = listView.getPaddingTop() + listView.getPaddingBottom();
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			if (listItem instanceof ViewGroup) {
				listItem.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
			}
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
	}

	public static void setListViewHeightBasedOnChildren_forchecking(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			// pre-condition
			return;
		}

		int totalHeight = 0;
		int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
		listView.requestLayout();
	}
}
