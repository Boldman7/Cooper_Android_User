package com.boldman.cooperuser.Fragments;

import android.Manifest;
import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;

import android.text.format.DateUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.boldman.cooperuser.Activities.Coupon.CouponActivity;
import com.boldman.cooperuser.Activities.Service.ExploreActivity;
import com.boldman.cooperuser.Activities.Service.GooglePlaceSearchActivity;
import com.boldman.cooperuser.Activities.Wallet.WalletActivity;
import com.boldman.cooperuser.Adapters.CarImageViewAdapter;
import com.boldman.cooperuser.Adapters.DriverReviewsAdapter;
import com.boldman.cooperuser.Adapters.ProviderListAdapter;
import com.boldman.cooperuser.Api.ApiClient;
import com.boldman.cooperuser.Api.ApiInterface;
import com.boldman.cooperuser.EventBus.GlobalEvent;
import com.boldman.cooperuser.EventBus.MessageEvent;
import com.boldman.cooperuser.Helper.ConnectionHelper;
import com.boldman.cooperuser.Helper.CustomDialog;
import com.boldman.cooperuser.Helper.DirectionsJSONParser;
import com.boldman.cooperuser.Helper.SharedHelper;
import com.boldman.cooperuser.Model.AcceptedDriverInfo;
import com.boldman.cooperuser.Model.CallBack;
import com.boldman.cooperuser.Model.CancelUserSelect;
import com.boldman.cooperuser.Model.CarImage;
import com.boldman.cooperuser.Model.CardDetails;
import com.boldman.cooperuser.Model.DriverInfo;
import com.boldman.cooperuser.Model.FinishRide;
import com.boldman.cooperuser.Model.PlacePredictions;
import com.boldman.cooperuser.Model.RequestUserPay;
import com.boldman.cooperuser.Model.RequestUserRate;
import com.boldman.cooperuser.Model.ServiceType;
import com.boldman.cooperuser.Adapters.ServiceTypePagerAdapter;
import com.boldman.cooperuser.Model.StartRide;
import com.boldman.cooperuser.Model.UserArrived;
import com.boldman.cooperuser.Model.UserLocationInfo;
import com.boldman.cooperuser.Model.YourTrips;
import com.boldman.cooperuser.QuickBlox.MessageChat.MessagingActivity;
import com.boldman.cooperuser.QuickBlox.MessageChat.QBMgr;
import com.boldman.cooperuser.QuickBlox.VideoChat.activities.CallActivity;
import com.boldman.cooperuser.QuickBlox.VideoChat.utils.PushNotificationSender;
import com.boldman.cooperuser.QuickBlox.VideoChat.utils.WebRtcSessionManager;
import com.boldman.cooperuser.R;

import com.boldman.cooperuser.Utils.GlobalConstants;
import com.boldman.cooperuser.Utils.RecyclerviewDisabler;
import com.boldman.cooperuser.Utils.Utils;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.users.model.QBUser;
import com.quickblox.videochat.webrtc.QBRTCClient;
import com.quickblox.videochat.webrtc.QBRTCSession;
import com.quickblox.videochat.webrtc.QBRTCTypes;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.lang.Math.asin;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;

public class MapFragment extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnMarkerDragListener,
        GoogleMap.OnCameraMoveListener,
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnPoiClickListener ,
        QBMgr.QBChatDialogMessageListenerEx {

    private GoogleMap mMap;
    protected LocationManager mLocationManger;

    UserLocationInfo currentUserInfo = new UserLocationInfo(), lastUserInfo = new UserLocationInfo();

    LatLng mSrcLatLng, mSrc1LatLng, mDstLatLng;
    Marker mSrcMarker, mSrc1Marker, mDstMarker;
    Marker[] mListDriverMarker;
    ArrayList<Map<String, Object>> mListNearDriver;
    String dest_address, dest_lat, dest_lng;
    String current_address = "", source_lat = "", source_lng = "", source_address = "",
            extend_dest_lat = "", extend_dest_lng = "", extend_dest_address = "";

    String formatted_address = "";
    int firstEnter = 0;

    Activity mActivity;
    Context mContext;
    int isToCameraMove = 0;
    private Socket mSocket;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    PointOfInterest mClickedPOI;

    Map<Integer, Bitmap> mListCarIcon;

    /*  Socket Receive Models*/
    AcceptedDriverInfo mAcceptedDriverInfo;
    UserArrived mUserArrived;
    StartRide mStartRide;
    RequestUserPay mRequestUserPay;
    RequestUserRate mRequestUserRate;
    FinishRide mFinishRide;
    CancelUserSelect mCancelUserSelect;

    String mDriverEmail;
    int mServiceType = 0;
    RelativeLayout lytFrmDestination;
    private final static int TIMEOUT = 10;
    private final static int DELAY = 20;
    boolean isSocketConnected = false;
    List<ServiceType> mListServiceType;
    ServiceType selectedServiceType;

    int flowValue;
    String notificationTxt;
    View rootView;
    ProgressDialog mProgressDlg;

    //  0: not, 1: yes
    int mStateDraggable = 1;
    String reqStatus = "";
    LinearLayout lnrSetDest;
    TextView tVQuestion;
    Button btnSetDestYes, btnSetDestNo;
    String mDistanceKm;
    ViewPager mViewPagerServiceType;
    int mCntServiceType, mPosServiceType;
    CallBack mCallBack;
    float mWalletRemain;
    CustomDialog customDialog;
    ApiInterface mApiInterface;
    boolean scheduleTrip = false;
    private static final int EXPLORE_REQUEST_CODE = 196;
    private static final int PAYMENT_SELECT_REQUEST_CODE = 5555;
    private static final int WALLET_TOPUP_REQUEST_CODE = 231;
    List<CardDetails> mListCardDetails;
    int mDistanceMeter = 0;
    int mTimeSeconds = 0;
    String mStartAddress, mEndAddress;

    private static final int COUPON_CODE_SELECT_REQEUST_CODE = 188;
    List<DriverInfo> mListDriverInfo;
    ProviderListAdapter mProviderListAdapter;

    String mRideKey = "";
    int mRideId = 0;
    String mBookingKey = "";
    int mBookingId = 0;

    ///////////////////////// Old Cooper Variables ////////////////////////

    ConnectionHelper helper;
    LinearLayout mapLayout;

    RelativeLayout explore;

    // Source and Destination Layout
    LinearLayout sourceAndDestinationLayout;
    // FrameLayout frmDestination;
    RelativeLayout frmDestination;
    TextView destination;
    ImageView mapfocus, imgBack, shadowBack;
    Button menuButton;
    View tripLine;
    LinearLayout errorLayout;
    ImageView destinationBorderImg;
    TextView frmSource, frmDest, txtChange;
    CardView srcDestLayout;
    RelativeLayout menuAndExploreLayout;


    // Request to providers
    LinearLayout lnrSelectServiceType;
    RecyclerView rcvServiceTypes;
    ImageView leftArrow, rightArrow;
    ImageView imgPaymentType;
    TextView textPayment;
    ImageView imgSos;
    ImageView imgShareRide;
    RelativeLayout lytSelectPayment;
    LinearLayout lytTopupWallet, lytSwitchPaymentMode;
    RelativeLayout lytRouteFound;
    String strPaymentModeDlg;
    TextView tvWalletBalanceDlg;

    // TextView lblPaymentType,lblPaymentChange;
    //   TextView lblPaymentChange;
    TextView booking_id;
    Button btnRequestRides;
    String scheduledDate = "";
    String scheduledTime = "";
    LinearLayoutManager servicesLayoutManager;
    TextView distanceText, capacityText;

    // Driver Details
    LinearLayout lnrHidePopup, lnrProviderPopup, lnrPriceBase, lnrPricemin, lnrPricekm;
    FrameLayout lnrSearchAnimation;

    ImageView imgProviderPopup;
    TextView lblPriceMin, lblBasePricePopup, lblCapacity, lblServiceName, lblPriceKm, lblCalculationType, lblProviderDesc;
    Button btnDonePopup;

    // Approximate Rate
    LinearLayout lnrApproximate;
    LinearLayout lnrAvailableBalance;
    Button btnRequestRideConfirm;
    Button imgSchedule;
    CheckBox chkWallet;
    TextView lblEta;
    TextView lblType;
    TextView lblApproxAmount, surgeDiscount, surgeTxt;
    TextView lblTotalDistance, lblWalletBalance;
    TextView tvDistanceToPickup, tvDistanceToDest, tvDistanceTotal;
    TextView tvEtaToPickup, tvEtaToDest, tvEtaTotal;
    View lineView;
    ImageView btnDistanceDetail, btnETADetail;

    LinearLayout ScheduleLayout;
    TextView scheduleDate;
    TextView scheduleTime;
    Button scheduleBtn;
    DatePickerDialog datePickerDialog;


    // Choose Providers
    LinearLayout lnrChooseProviders;
    ListView lvProvider;
    Button btnRequestNearest, btnBack;
    android.app.AlertDialog dlgDetail;
    DriverReviewsAdapter mDriverReviewsAdapter;
    CarImageViewAdapter mCarImageViewAdapter;

    // Waiting For Providers
    RelativeLayout lnrWaitingForProviders;
    TextView lblNoMatch, tvWaitingTime;
    ImageView imgCenter;
    Button btnCancelRide;
    private boolean mIsShowing;
    private boolean mIsHiding;
    private static final Interpolator INTERPOLATOR = new FastOutSlowInInterpolator();
    Handler mHandlerWaiting;
    Runnable mRunnableWaiting;
    int mTimeWaiting = 60;

    // Driver Accepted
    LinearLayout lnrProviderAccepted, lnrAfterAcceptedStatus, AfterAcceptButtonLayout;
    ImageView imgProvider, imgServiceRequested;
    TextView lblProvider, lblStatus, lblServiceRequested, lblModelNumber, lblSurgePrice;
    RatingBar ratingProvider;
    Button btnCancelTrip;
    ImageView btnCall, btnChat;
    TextView tvUnreadMsgCnt;

    QBUser qbUserAcceptedDriver;

    // Invoice Layout
    LinearLayout lnrInvoice;
    TextView lblBasePrice, lblDistanceCovered, lblExtraPrice, lblTimeTaken, lblDistancePrice, lblCommision, lblTaxPrice, lblTotalPrice, lblPaymentTypeInvoice, lblPaymentChangeInvoice, lblDiscountPrice, lblWalletPrice;
    ImageView imgPaymentTypeInvoice;
    Button btnPayNow;
    Button btnPaymentDoneBtn;
    LinearLayout discountDetectionLayout, subtotal2Layout, walletDetectionLayout;
    LinearLayout bookingIDLayout;
    CheckBox chkCoupon;

    String mCouponCode = "";
    String mCouponDiscount = "";
    String mCouponDiscountType = "";

    double mEstimatedFair = 0;
    double mTaxPrice = 0;
    double mBasePrice = 0;
    double mSurgeValue = 0;
    int mSurgeTrigger = 0;
    double mSubTotal = 0;
    double mSubTotal2 = 0;
    double mTotalAmountToPay = 0.0d;

    // Rate provider Layout
    LinearLayout lnrRateProvider;
    TextView lblProviderNameRate;
    ImageView imgProviderRate;
    RatingBar ratingProviderRate;
    EditText txtCommentsRate;
    Button btnSubmitReview;
    Float pro_rating;

    //  HomeFragmentListener listener
    double mWalletBalance = 0;
    private Random mRandom = new Random(1984);
    LayoutInflater inflater;
    android.app.AlertDialog reasonDialog;
    android.app.AlertDialog cancelRideDialog;
    String is_track = "";
    String strTimeTaken = "";
    Button btnHome, btnWork;
    TextView lblOTP;
    LinearLayout lnrOTP;

    LinearLayout lnrHomeWork, lnrHome, lnrWork;


    RelativeLayout rtlStaticMarker;
    ImageView imgDestination;
    Button btnDone;
    CameraPosition cmPosition;

    //Animation
    Animation slide_down, slide_up, slide_up_top, slide_up_down;

    boolean mApplyTilt = false;
    Double current_latitude, current_longitude;

    String isPaid = "", paymentMode = "";
    int totalRideAmount = 0, walletAmountDetected = 0, couponAmountDetected = 0;

    //   DrawerLayout drawer;
    int NAV_DRAWER = 0;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE_DEST = 18945;
    float feedBackRating;


    public String CurrentStatus = "";
    String strPickLocation = "", strTag = "", strPickType = "";
    boolean once = true;
    int click = 1;
    boolean afterToday = false;
    boolean pick_first = true;

    private OnFragmentInteractionListener mListener;

    public MapFragment() {
        // Required empty public constructor
    }

    public static MapFragment newInstance() {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();

        QBMgr.setExternalDlgMsgListenerForMainActivity(this);

        if (bundle != null) {
            notificationTxt = bundle.getString("Notification");
            Log.e("MapFragment", "onCreate : Notification" + notificationTxt);
        }

        mListCarIcon = new HashMap<>();

        getAndShowServiceList();
    }

    @SuppressLint("MissingPermission")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_map, container, false);
        }
        this.inflater = inflater;

        //  Check permission to access location
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                    (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                     ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            // Android M Permission check (API 23)
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        } else {

            /*  Init UI*/
            initView(rootView);
            initVariables();
            defineClickListeners();

            /*  Init Maps*/
            initMap();
            MapsInitializer.initialize(getActivity());

            /**/
            mListNearDriver = new ArrayList<Map<String, Object>>();
            mListDriverInfo = new ArrayList<>();

            /*  Init Sockets (Create, Define emitter listeners)*/
            createSocket();
            connectToSocket();
            defineSocketListeners();
//            sendUserInfo();

            /*  Location Manager*/
            mLocationManger = (LocationManager) mActivity.getSystemService(Context.LOCATION_SERVICE);

//            mLocationManger = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//            if (mLocationManger != null) {
//                List<String> providers = mLocationManger.getAllProviders();
//                for (String provider : providers) {
            mLocationManger.requestLocationUpdates(LocationManager.GPS_PROVIDER, 200 , 0.1f, this);
//                }
//            }

            setCurrentRide();

//            mLocationManger.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0.01f, MapFragment.this);
        }

        return rootView;
    }

    private void createSocket() {

        try {
            IO.Options opts = new IO.Options();
            mSocket = IO.socket(GlobalConstants.SERVER_SOCKET_URL, opts);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setCurrentRide() {

        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        JsonObject gsonObject = new JsonObject();
        try {
            JSONObject paramObject = new JSONObject();

            paramObject.put("api_token", SharedHelper.getKey(mContext, "api_token"));

            JsonParser jsonParser = new JsonParser();
            gsonObject = (JsonObject) jsonParser.parse(paramObject.toString());

            apiInterface.doGetCurrentRide(gsonObject).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    progressDialog.dismiss();

                    try {
                        JSONObject object = new JSONObject(response.body().string());

                        if (object.getString("status").equals("1")){

                            JSONObject data = object.getJSONObject("data");
                            JSONObject rides = null;

                            if (data != null) {
                                rides = data.getJSONObject("rides");
                            }

                            if (rides != null){

                                int ride_status = rides.getInt("status");

                                if (ride_status == GlobalConstants.RIDE_STATUS_REQUESTED){

                                    displayScreenFindingRide();

                                } else if (ride_status == GlobalConstants.RIDE_STATUS_CANCELED){

                                } else if (ride_status == GlobalConstants.RIDE_STATUS_ACCEPTED){

                                    mAcceptedDriverInfo = new AcceptedDriverInfo();

                                    mAcceptedDriverInfo.setEmail(rides.getString("driver_email"));
                                    mAcceptedDriverInfo.setGender(rides.getInt("driver_gender"));
                                    mAcceptedDriverInfo.setFirstName(rides.getString("driver_firstname"));
                                    mAcceptedDriverInfo.setLastName(rides.getString("driver_lastname"));
                                    mAcceptedDriverInfo.setPhoneNumber(rides.getString("driver_phonenumber"));
                                    mAcceptedDriverInfo.setCarNumber(rides.getString("driver_carnumber"));
                                    mAcceptedDriverInfo.setCarModel(rides.getString("driver_carmodel"));
                                    mAcceptedDriverInfo.setAvatar(rides.getString("driver_avatar"));
                                    mAcceptedDriverInfo.setDriverAvgRating(rides.getString("driver_averagerate"));
                                    mAcceptedDriverInfo.setRideCode(rides.getString("ride_code"));

                                    mServiceType = rides.getInt("driver_servicetype");
                                    mRideId = rides.getInt("ride_id");
                                    paymentMode = rides.getString("payment_method");

                                    try {
                                        mEstimatedFair = Double.valueOf(rides.getString("pay_amount"));
                                    } catch (Exception e){
                                        mEstimatedFair = 0;
                                    }
                                    try {
                                        mBasePrice = Double.valueOf(rides.getString("base_fee"));
                                    } catch (Exception e){
                                        mBasePrice = 0;
                                    }

                                    SharedHelper.putKey(mContext, "payment_mode", paymentMode);

                                    qbUserAcceptedDriver = new QBUser();

                                    qbUserAcceptedDriver.setId(rides.getInt("quickblox_id"));
                                    qbUserAcceptedDriver.setLogin(rides.getString("quickblox_username"));
                                    qbUserAcceptedDriver.setPassword(rides.getString("quickblox_password"));

                                    displayScreenAcceptedDriver();

                                } else if (ride_status == GlobalConstants.RIDE_STATUS_ARRIVED){

                                    mAcceptedDriverInfo = new AcceptedDriverInfo();

                                    mAcceptedDriverInfo.setEmail(rides.getString("driver_email"));
                                    mAcceptedDriverInfo.setGender(rides.getInt("driver_gender"));
                                    mAcceptedDriverInfo.setFirstName(rides.getString("driver_firstname"));
                                    mAcceptedDriverInfo.setLastName(rides.getString("driver_lastname"));
                                    mAcceptedDriverInfo.setPhoneNumber(rides.getString("driver_phonenumber"));
                                    mAcceptedDriverInfo.setCarNumber(rides.getString("driver_carnumber"));
                                    mAcceptedDriverInfo.setCarModel(rides.getString("driver_carmodel"));
                                    mAcceptedDriverInfo.setAvatar(rides.getString("driver_avatar"));
                                    mAcceptedDriverInfo.setDriverAvgRating(rides.getString("driver_averagerate"));
                                    mAcceptedDriverInfo.setRideCode(rides.getString("ride_code"));

                                    mServiceType = rides.getInt("driver_servicetype");
                                    mRideId = rides.getInt("ride_id");
                                    paymentMode = rides.getString("payment_method");

                                    try {
                                        mEstimatedFair = Double.valueOf(rides.getString("pay_amount"));
                                    } catch (Exception e){
                                        mEstimatedFair = 0;
                                    }
                                    try {
                                        mBasePrice = Double.valueOf(rides.getString("base_fee"));
                                    } catch (Exception e){
                                        mBasePrice = 0;
                                    }

                                    SharedHelper.putKey(mContext, "payment_mode", paymentMode);

                                    qbUserAcceptedDriver = new QBUser();

                                    qbUserAcceptedDriver.setId(rides.getInt("quickblox_id"));
                                    qbUserAcceptedDriver.setLogin(rides.getString("quickblox_username"));
                                    qbUserAcceptedDriver.setPassword(rides.getString("quickblox_password"));

                                    displayScreenArrivedDriver();

                                } else if (ride_status == GlobalConstants.RIDE_STATUS_STARTED){

                                    mAcceptedDriverInfo = new AcceptedDriverInfo();

                                    mAcceptedDriverInfo.setEmail(rides.getString("driver_email"));
                                    mAcceptedDriverInfo.setGender(rides.getInt("driver_gender"));
                                    mAcceptedDriverInfo.setFirstName(rides.getString("driver_firstname"));
                                    mAcceptedDriverInfo.setLastName(rides.getString("driver_lastname"));
                                    mAcceptedDriverInfo.setPhoneNumber(rides.getString("driver_phonenumber"));
                                    mAcceptedDriverInfo.setCarNumber(rides.getString("driver_carnumber"));
                                    mAcceptedDriverInfo.setCarModel(rides.getString("driver_carmodel"));
                                    mAcceptedDriverInfo.setAvatar(rides.getString("driver_avatar"));
                                    mAcceptedDriverInfo.setDriverAvgRating(rides.getString("driver_averagerate"));
                                    mAcceptedDriverInfo.setRideCode(rides.getString("ride_code"));

                                    mServiceType = rides.getInt("driver_servicetype");
                                    mRideId = rides.getInt("ride_id");
                                    paymentMode = rides.getString("payment_method");

                                    try {
                                        mEstimatedFair = Double.valueOf(rides.getString("pay_amount"));
                                    } catch (Exception e){
                                        mEstimatedFair = 0;
                                    }
                                    try {
                                        mBasePrice = Double.valueOf(rides.getString("base_fee"));
                                    } catch (Exception e){
                                        mBasePrice = 0;
                                    }

                                    SharedHelper.putKey(mContext, "payment_mode", paymentMode);

                                    qbUserAcceptedDriver = new QBUser();

                                    qbUserAcceptedDriver.setId(rides.getInt("quickblox_id"));
                                    qbUserAcceptedDriver.setLogin(rides.getString("quickblox_username"));
                                    qbUserAcceptedDriver.setPassword(rides.getString("quickblox_password"));

                                    displayScreenStartRide();

                                } else if (ride_status == GlobalConstants.RIDE_STATUS_USERENDED){

                                } else if (ride_status == GlobalConstants.RIDE_STATUS_DRIVERENDED){

                                    mAcceptedDriverInfo = new AcceptedDriverInfo();

                                    mAcceptedDriverInfo.setEmail(rides.getString("driver_email"));
                                    mAcceptedDriverInfo.setGender(rides.getInt("driver_gender"));
                                    mAcceptedDriverInfo.setFirstName(rides.getString("driver_firstname"));
                                    mAcceptedDriverInfo.setLastName(rides.getString("driver_lastname"));
                                    mAcceptedDriverInfo.setPhoneNumber(rides.getString("driver_phonenumber"));
                                    mAcceptedDriverInfo.setCarNumber(rides.getString("driver_carnumber"));
                                    mAcceptedDriverInfo.setCarModel(rides.getString("driver_carmodel"));
                                    mAcceptedDriverInfo.setAvatar(rides.getString("driver_avatar"));
                                    mAcceptedDriverInfo.setDriverAvgRating(rides.getString("driver_averagerate"));
                                    mAcceptedDriverInfo.setRideCode(rides.getString("ride_code"));

                                    mServiceType = rides.getInt("driver_servicetype");
                                    mRideId = rides.getInt("ride_id");
                                    Log.i("AAAAA", mRideId + "");
                                    paymentMode = rides.getString("payment_method");

                                    SharedHelper.putKey(mContext, "payment_mode", paymentMode);

                                    qbUserAcceptedDriver = new QBUser();

                                    qbUserAcceptedDriver.setId(rides.getInt("quickblox_id"));
                                    qbUserAcceptedDriver.setLogin(rides.getString("quickblox_username"));
                                    qbUserAcceptedDriver.setPassword(rides.getString("quickblox_password"));

                                    mDistanceKm = rides.getString("distance");
                                    strTimeTaken = rides.getString("duration");

                                    try {
                                        mEstimatedFair = Double.valueOf(rides.getString("pay_amount"));
                                    } catch (Exception e){
                                        mEstimatedFair = 0;
                                    }
                                    try {
                                        mBasePrice = Double.valueOf(rides.getString("base_fee"));
                                    } catch (Exception e){
                                        mBasePrice = 0;
                                    }

                                    displayScreenToPay();

                                } else if (ride_status == GlobalConstants.RIDE_STATUS_PAY){

                                } else if (ride_status == GlobalConstants.RIDE_STATUS_PAID){

                                    displayScreenToRate();

                                } else if (ride_status == GlobalConstants.RIDE_STATUS_USERRATED){

                                    mAcceptedDriverInfo = new AcceptedDriverInfo();

                                    mAcceptedDriverInfo.setEmail(rides.getString("driver_email"));
                                    mAcceptedDriverInfo.setGender(rides.getInt("driver_gender"));
                                    mAcceptedDriverInfo.setFirstName(rides.getString("driver_firstname"));
                                    mAcceptedDriverInfo.setLastName(rides.getString("driver_lastname"));
                                    mAcceptedDriverInfo.setPhoneNumber(rides.getString("driver_phonenumber"));
                                    mAcceptedDriverInfo.setCarNumber(rides.getString("driver_carnumber"));
                                    mAcceptedDriverInfo.setCarModel(rides.getString("driver_carmodel"));
                                    mAcceptedDriverInfo.setAvatar(rides.getString("driver_avatar"));
                                    mAcceptedDriverInfo.setDriverAvgRating(rides.getString("driver_averagerate"));
                                    mAcceptedDriverInfo.setRideCode(rides.getString("ride_code"));

                                } else if (ride_status == GlobalConstants.RIDE_STATUS_FINISHED){

                                    mAcceptedDriverInfo = new AcceptedDriverInfo();

                                    mAcceptedDriverInfo.setEmail(rides.getString("driver_email"));
                                    mAcceptedDriverInfo.setGender(rides.getInt("driver_gender"));
                                    mAcceptedDriverInfo.setFirstName(rides.getString("driver_firstname"));
                                    mAcceptedDriverInfo.setLastName(rides.getString("driver_lastname"));
                                    mAcceptedDriverInfo.setPhoneNumber(rides.getString("driver_phonenumber"));
                                    mAcceptedDriverInfo.setCarNumber(rides.getString("driver_carnumber"));
                                    mAcceptedDriverInfo.setCarModel(rides.getString("driver_carmodel"));
                                    mAcceptedDriverInfo.setAvatar(rides.getString("driver_avatar"));
                                    mAcceptedDriverInfo.setDriverAvgRating(rides.getString("driver_averagerate"));
                                    mAcceptedDriverInfo.setRideCode(rides.getString("ride_code"));

                                    displayScreenToRate();
                                }

                                int cntUnreadMsg = 0;

                                if (SharedHelper.getKey(mContext, "cntUnreadMsg") != null)
                                    if (!SharedHelper.getKey(mContext, "cntUnreadMsg").equalsIgnoreCase(""))
                                        cntUnreadMsg = Integer.valueOf(SharedHelper.getKey(mContext, "cntUnreadMsg"));

                                EventBus.getDefault().post(new MessageEvent.ReceivedMessage(cntUnreadMsg));
                            }

                        } else{
                            JSONObject data = object.getJSONObject("data");
                            Utils.displayMessage(mActivity, Utils.parseErrorMessage(data));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        //Utils.displayMessage(mActivity, getString(R.string.server_connect_error));
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                    progressDialog.dismiss();

                    t.printStackTrace();
                    Utils.displayMessage(mActivity, getString(R.string.server_connect_error));
                }
            });

        } catch (JSONException e) {

            progressDialog.dismiss();
            e.printStackTrace();
        }

    }

    /*  Set Ids*/
    private void initView(View rootView) {

        helper = new ConnectionHelper(getActivity());

        //  Set Destination

        lnrSetDest = rootView.findViewById(R.id.lnrSetDest);
        tVQuestion = rootView.findViewById(R.id.tVQuestion);
        btnSetDestYes = rootView.findViewById(R.id.btnYes);
        btnSetDestNo = rootView.findViewById(R.id.btnNo);

//        <!-- Map frame -->
        mapLayout = rootView.findViewById(R.id.mapLayout);

//        <!-- Source and Destination Layout-->
        sourceAndDestinationLayout = rootView.findViewById(R.id.sourceAndDestinationLayout);
        srcDestLayout = rootView.findViewById(R.id.sourceDestLayout);
        frmSource = rootView.findViewById(R.id.frmSource);
        lblOTP = rootView.findViewById(R.id.lblOTP);
        lnrOTP = rootView.findViewById(R.id.lnrOTP);
        frmDest = rootView.findViewById(R.id.frmDest);
        txtChange = rootView.findViewById(R.id.txtChange);
        frmDestination = rootView.findViewById(R.id.frmDestination);
        destination = rootView.findViewById(R.id.destination);
        menuButton = rootView.findViewById(R.id.menu_button);
        imgSos = rootView.findViewById(R.id.imgSos);
        imgShareRide = rootView.findViewById(R.id.imgShareRide);
        mapfocus = rootView.findViewById(R.id.mapfocus);
        imgBack = rootView.findViewById(R.id.imgBack);
        shadowBack = rootView.findViewById(R.id.shadowBack);
        tripLine = rootView.findViewById(R.id.trip_line);
        destinationBorderImg = rootView.findViewById(R.id.dest_border_img);
        explore = rootView.findViewById(R.id.explore_home_fragment);
        menuAndExploreLayout = rootView.findViewById(R.id.menu_and_explore_layout);
//        <!-- Request to providers-->

        lnrSelectServiceType = rootView.findViewById(R.id.lnrSelectServiceType);
        rcvServiceTypes = rootView.findViewById(R.id.rcvServiceTypes);
        imgPaymentType = rootView.findViewById(R.id.imgPaymentType);
        textPayment = rootView.findViewById(R.id.textPayment);
        // lblPaymentType = rootView.findViewById(R.id.lblPaymentType);
        //  lblPaymentChange =  rootView.findViewById(R.id.lblPaymentChange);
        booking_id = rootView.findViewById(R.id.booking_id);
        btnRequestRides = rootView.findViewById(R.id.btnRequestRides);
        leftArrow = rootView.findViewById(R.id.left_arrow_cooper_image);
        rightArrow = rootView.findViewById(R.id.right_arrow_cooper_image);
//        distanceText = rootView.findViewById(R.id.location_distance_text);
//        capacityText = rootView.findViewById(R.id.capacity_text);
        lytSelectPayment = rootView.findViewById(R.id.lytSelectPayment);
        lytTopupWallet = rootView.findViewById(R.id.lytTopupWallet);
        lytSwitchPaymentMode = rootView.findViewById(R.id.lytSwitchPaymentMode);
        lytRouteFound = rootView.findViewById(R.id.lytRouteFound);



//        <!--  Driver and service type Details-->

        lnrSearchAnimation = rootView.findViewById(R.id.lnrSearch);
        lnrProviderPopup = rootView.findViewById(R.id.lnrProviderPopup);
        lnrPriceBase = rootView.findViewById(R.id.lnrPriceBase);
        lnrPricekm = rootView.findViewById(R.id.lnrPricekm);
        lnrPricemin = rootView.findViewById(R.id.lnrPricemin);
        lnrHidePopup = rootView.findViewById(R.id.lnrHidePopup);
        imgProviderPopup = rootView.findViewById(R.id.imgProviderPopup);

        lblServiceName = rootView.findViewById(R.id.lblServiceName);
        lblCapacity = rootView.findViewById(R.id.lblCapacity);
        lblPriceKm = rootView.findViewById(R.id.lblPriceKm);
        lblPriceMin = rootView.findViewById(R.id.lblPriceMin);
        lblCalculationType = rootView.findViewById(R.id.lblCalculationType);
        lblBasePricePopup = rootView.findViewById(R.id.lblBasePricePopup);
        lblDistanceCovered = rootView.findViewById(R.id.lblDistanceCovered);
        lblProviderDesc = rootView.findViewById(R.id.lblProviderDesc);

        btnDonePopup = rootView.findViewById(R.id.btnDonePopup);


//         <!--2. Approximate Rate ...-->

        lnrApproximate = rootView.findViewById(R.id.lnrApproximate);
        lnrAvailableBalance = rootView.findViewById(R.id.lnrAvailableBalance);
        lblTotalDistance = rootView.findViewById(R.id.lblTotalDistance);
        lblWalletBalance = rootView.findViewById(R.id.lblWalletBalance);
        imgSchedule = rootView.findViewById(R.id.imgSchedule);
        chkWallet = rootView.findViewById(R.id.chkWallet);
        lblEta = rootView.findViewById(R.id.lblEta);
        lblType = rootView.findViewById(R.id.lblType);
        lblApproxAmount = rootView.findViewById(R.id.lblApproxAmount);
        surgeDiscount = rootView.findViewById(R.id.surgeDiscount);
        surgeTxt = rootView.findViewById(R.id.surge_txt);
        btnRequestRideConfirm = rootView.findViewById(R.id.btnRequestRideConfirm);
        lineView = rootView.findViewById(R.id.lineView);
        btnDistanceDetail = rootView.findViewById(R.id.img_distance_detail);
        btnETADetail = rootView.findViewById(R.id.img_eta_detail);

        // Detail distance and eta
        tvDistanceToPickup = rootView.findViewById(R.id.tv_distance_to_pickup);
        tvDistanceToDest = rootView.findViewById(R.id.tv_distance_to_destination);
        tvDistanceTotal = rootView.findViewById(R.id.tv_distance_total);
        tvEtaToPickup = rootView.findViewById(R.id.tv_eta_to_pickup);
        tvEtaToDest = rootView.findViewById(R.id.tv_eta_to_destination);
        tvEtaTotal = rootView.findViewById(R.id.tv_eta_total);

        //Schedule Layout
        ScheduleLayout = rootView.findViewById(R.id.ScheduleLayout);
        scheduleDate = rootView.findViewById(R.id.scheduleDate);
        scheduleTime = rootView.findViewById(R.id.scheduleTime);
        scheduleBtn = rootView.findViewById(R.id.scheduleBtn);

        /*  Choose Driver*/
        lnrChooseProviders = rootView.findViewById(R.id.lnrChooseProviders);
        lvProvider = rootView.findViewById(R.id.lvProviders);
        btnRequestNearest = rootView.findViewById(R.id.btnNearestRequest);
        btnBack = rootView.findViewById(R.id.btnBack);

//         <!--3. Waiting For Providers ...-->

        lnrWaitingForProviders = rootView.findViewById(R.id.lnrWaitingForProviders);
        tvWaitingTime = rootView.findViewById(R.id.tv_waiting_time);
        lblNoMatch = rootView.findViewById(R.id.lblNoMatch);
        //imgCenter =  rootView.findViewById(R.id.imgCenter);
        btnCancelRide = rootView.findViewById(R.id.btnCancelRide);

//          <!--4. Driver Accepted ...-->

        lnrProviderAccepted = rootView.findViewById(R.id.lnrProviderAccepted);
        lnrAfterAcceptedStatus = rootView.findViewById(R.id.lnrAfterAcceptedStatus);
        AfterAcceptButtonLayout = rootView.findViewById(R.id.AfterAcceptButtonLayout);
        imgProvider = rootView.findViewById(R.id.imgProvider);
        imgServiceRequested = rootView.findViewById(R.id.imgServiceRequested);
        lblProvider = rootView.findViewById(R.id.lblProvider);
        lblStatus = rootView.findViewById(R.id.lblStatus);
        lblSurgePrice = rootView.findViewById(R.id.lblSurgePrice);
        lblServiceRequested = rootView.findViewById(R.id.lblServiceRequested);
        lblModelNumber = rootView.findViewById(R.id.lblModelNumber);
        ratingProvider = rootView.findViewById(R.id.ratingProvider);
        btnCall = rootView.findViewById(R.id.btnCall);
        btnChat = rootView.findViewById(R.id.btnChat);
        tvUnreadMsgCnt = rootView.findViewById(R.id.tvUnreadMsgCnt);
        btnCancelTrip = rootView.findViewById(R.id.btnCancelTrip);


//           <!--5. Invoice Layout ...-->

        lnrInvoice = rootView.findViewById(R.id.lnrInvoice);
        lblBasePrice = rootView.findViewById(R.id.lblBasePrice);
        lblExtraPrice = rootView.findViewById(R.id.lblExtraPrice);
        lblDistancePrice = rootView.findViewById(R.id.lblDistancePrice);
        lblTimeTaken = rootView.findViewById(R.id.lblTimeTaken);
        //lblCommision =rootView.findViewById(R.id.lblCommision);
        lblTaxPrice = rootView.findViewById(R.id.lblTaxPrice);
        lblTotalPrice = rootView.findViewById(R.id.lblTotalPrice);
        lblPaymentTypeInvoice = rootView.findViewById(R.id.lblPaymentTypeInvoice);
        imgPaymentTypeInvoice = rootView.findViewById(R.id.imgPaymentTypeInvoice);
        btnPayNow = rootView.findViewById(R.id.btnPayNow);
        btnPaymentDoneBtn = rootView.findViewById(R.id.btnPaymentDoneBtn);
        bookingIDLayout = rootView.findViewById(R.id.bookingIDLayout);
        walletDetectionLayout = rootView.findViewById(R.id.walletDetectionLayout);
        discountDetectionLayout = rootView.findViewById(R.id.discountDetectionLayout);
        subtotal2Layout = rootView.findViewById(R.id.subtotal2Layout);
        lblWalletPrice = rootView.findViewById(R.id.lblWalletPrice);
        lblDiscountPrice = rootView.findViewById(R.id.lblDiscountPrice);
        chkCoupon = rootView.findViewById(R.id.chkCoupon);

//          <!--6. Rate provider Layout ...-->

        lnrHomeWork = rootView.findViewById(R.id.lnrHomeWork);
        lnrHome = rootView.findViewById(R.id.lnrHome);
        lnrWork = rootView.findViewById(R.id.lnrWork);
        lnrRateProvider = rootView.findViewById(R.id.lnrRateProvider);
        lblProviderNameRate = rootView.findViewById(R.id.lblProviderName);
        imgProviderRate = rootView.findViewById(R.id.imgProviderRate);
        txtCommentsRate = rootView.findViewById(R.id.txtComments);
        ratingProviderRate = rootView.findViewById(R.id.ratingProviderRate);
        btnSubmitReview = (Button) rootView.findViewById(R.id.btnSubmitReview);


//            <!--Static marker-->

        rtlStaticMarker = rootView.findViewById(R.id.rtlStaticMarker);
        imgDestination = rootView.findViewById(R.id.imgDestination);
        btnDone = rootView.findViewById(R.id.btnDone);
        btnHome = rootView.findViewById(R.id.btnHome);
        btnWork = rootView.findViewById(R.id.btnWork);


//        btnRequestRides.setOnClickListener(new OnClick());
//        btnDonePopup.setOnClickListener(new OnClick());
//        lnrHidePopup.setOnClickListener(new OnClick());
//        btnRequestRideConfirm.setOnClickListener(new OnClick());
//        btnCancelRide.setOnClickListener(new OnClick());
//        btnCancelTrip.setOnClickListener(new OnClick());
//        btnCall.setOnClickListener(new OnClick());
//        btnPayNow.setOnClickListener(new OnClick());
//        btnPaymentDoneBtn.setOnClickListener(new OnClick());
//        btnSubmitReview.setOnClickListener(new OnClick());
//        btnHome.setOnClickListener(new OnClick());
//        btnWork.setOnClickListener(new OnClick());
//        btnDone.setOnClickListener(new OnClick());
//        frmDestination.setOnClickListener(new OnClick());
//        frmDest.setOnClickListener(new OnClick());
//        //    lblPaymentChange.setOnClickListener(new OnClick());
//        textPayment.setOnClickListener(new OnClick());
//        frmSource.setOnClickListener(new OnClick());
//        txtChange.setOnClickListener(new OnClick());

//        mapfocus.setOnClickListener(new OnClick());
//        imgSchedule.setOnClickListener(new OnClick());
//        imgBack.setOnClickListener(new OnClick());
//        scheduleBtn.setOnClickListener(new OnClick());
//        scheduleDate.setOnClickListener(new OnClick());
//        scheduleTime.setOnClickListener(new OnClick());
//        imgProvider.setOnClickListener(new OnClick());
//        imgProviderRate.setOnClickListener(new OnClick());
//        imgSos.setOnClickListener(new OnClick());
//        imgShareRide.setOnClickListener(new OnClick());
//
//        lnrProviderPopup.setOnClickListener(new OnClick());
//        ScheduleLayout.setOnClickListener(new OnClick());
//        lnrApproximate.setOnClickListener(new OnClick());
//        lnrProviderAccepted.setOnClickListener(new OnClick());
//        lnrInvoice.setOnClickListener(new OnClick());
//        lnrRateProvider.setOnClickListener(new OnClick());
//        lnrWaitingForProviders.setOnClickListener(new OnClick());

        flowValue = 0;
        layoutChanges();

    }

    private void initVariables(){

        mListServiceType = new ArrayList<>();

        //DISABLE RIDE SERVICE RECYCLERVIEW SCROLLING
        RecyclerView.OnItemTouchListener disabler = (RecyclerView.OnItemTouchListener) new RecyclerviewDisabler();
        rcvServiceTypes.addOnItemTouchListener(disabler);

        //Load animation
        slide_down = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_down);
        slide_up = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up);
        slide_up_top = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up_top);
        slide_up_down = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up_down);

        rootView.setFocusableInTouchMode(true);
        rootView.requestFocus();
        rootView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() != KeyEvent.ACTION_DOWN)
                    return true;

                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (!reqStatus.equalsIgnoreCase("SEARCHING")) {

                        if (lnrSelectServiceType.getVisibility() == View.VISIBLE) {
                            displayFirstScreen();
                        } else if (lnrApproximate.getVisibility() == View.VISIBLE) {
                            flowValue = 1;
                            layoutChanges();
                        } else if (lnrWaitingForProviders.getVisibility() == View.VISIBLE) {
                            flowValue = 2;
                            layoutChanges();
                        } else if (ScheduleLayout.getVisibility() == View.VISIBLE) {
                            flowValue = 2;
                            layoutChanges();
                        } else if (lnrSetDest.getVisibility() == View.VISIBLE) {
                            displayFirstScreen();
                        } else if (lnrChooseProviders.getVisibility() == View.VISIBLE) {
                            displayScreenRequestRide();
                        } else {
                            getActivity().finish();
                        }
                        //////////////////////////layoutChanges();
                        return true;
                    }
                }
                return false;
            }
        });
    }

    /*  Define click listeners*/
    private void defineClickListeners() {

        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if((current_latitude != null) && (current_longitude != null)) {
                    onMenuPressed(current_latitude, current_longitude);
                }
                else{
                    onMenuPressed(0,0);
                }
            }
        });

        explore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(getContext(), ExploreActivity.class);
                intent1.putExtra("latitude", current_latitude);
                intent1.putExtra("longitude", current_longitude);
                startActivityForResult(intent1, EXPLORE_REQUEST_CODE);
            }
        });

        frmDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CurrentStatus.equalsIgnoreCase("")) {

                    goToScreenSelectAddress(mContext, source_lat, source_lng);
                }
            }
        });

        lytSelectPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Intent intent = new Intent(getActivity(), PaymentActivity.class);
//                startActivityForResult(intent, PAYMENT_SELECT_REQUEST_CODE);
            }
        });

        lytSwitchPaymentMode.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (SharedHelper.getKey(mContext, "payment_mode").equalsIgnoreCase("WALLET")) {

                    SharedHelper.putKey(mContext, "payment_mode", "CASH");
                    imgPaymentType.setImageResource(R.drawable.money_icon);
                    textPayment.setText("CASH");

                    strPaymentModeDlg = "CASH";
                    lytTopupWallet.setVisibility(View.GONE);
                } else {

                    SharedHelper.putKey(mContext, "payment_mode", "WALLET");
                    imgPaymentType.setImageResource(R.drawable.wallet);
                    textPayment.setText("$" + SharedHelper.getKey(mContext, "wallet_balance"));

                    strPaymentModeDlg = "WALLET";
                    lytTopupWallet.setVisibility(View.VISIBLE);
                }

            }
        });

        lytTopupWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), WalletActivity.class);
                startActivityForResult(intent, WALLET_TOPUP_REQUEST_CODE);
            }
        });

        chkCoupon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked){

                    Intent intentDest = new Intent(getActivity(), CouponActivity.class);
                    intentDest.putExtra("invoice", true);
                    startActivityForResult(intentDest, COUPON_CODE_SELECT_REQEUST_CODE);
                } else{

                    cancelCouponCodeSelect();
                }
            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (is_track.equalsIgnoreCase("YES") &&
                        (CurrentStatus.equalsIgnoreCase("STARTED") || CurrentStatus.equalsIgnoreCase("PICKEDUP")
                                || CurrentStatus.equalsIgnoreCase("ARRIVED"))) {
//                    extend_dest_lat = "" + cmPosition.target.latitude;
//                    extend_dest_lng = "" + cmPosition.target.longitude;
//                    showTripExtendAlert(extend_dest_lat, extend_dest_lng);

                } else {
                    pick_first = true;
                    try {

//                        Log.i("centerLat", cmPosition.target.latitude + " Boldman");
//                        Log.i("centerLong", cmPosition.target.longitude + " Boldman");

                        Geocoder geocoder = null;
                        List<Address> addresses;
                        geocoder = new Geocoder(getActivity(), Locale.getDefault());

                        String city = "", state = "", address = "";

                        try {
                            addresses = geocoder.getFromLocation(cmPosition.target.latitude, cmPosition.target.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                            address = addresses.get(0).getAddressLine(0);
                            city = addresses.get(0).getLocality();
                            state = addresses.get(0).getAdminArea();
                            ; // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (strPickType.equalsIgnoreCase("source")) {
                            source_address = getAddressUsingLatLng("source", frmSource, mContext, "" + cmPosition.target.latitude, "" + cmPosition.target.longitude);
                            source_lat = "" + cmPosition.target.latitude;
                            source_lng = "" + cmPosition.target.longitude;
                            if (dest_lat.equalsIgnoreCase("")) {
                                Toast.makeText(mContext, "Select destination", Toast.LENGTH_SHORT).show();
                                Intent intentDest = new Intent(getActivity(), GooglePlaceSearchActivity.class);
                                intentDest.putExtra("cursor", "destination");
                                intentDest.putExtra("s_address", source_address);
                                startActivityForResult(intentDest, PLACE_AUTOCOMPLETE_REQUEST_CODE_DEST);
                            } else {

                                source_lat = "" + cmPosition.target.latitude;
                                source_lng = "" + cmPosition.target.longitude;

                                mSrcLatLng = cmPosition.target;
                                mDstLatLng = new LatLng(Double.parseDouble(dest_lat), Double.parseDouble(dest_lng));

                                //  Place user(source) location marker
                                addSrcMarker(mSrcLatLng);

                                if (mDstMarker != null) {
                                    mDstMarker.remove();
                                }

                                //  Place destination location marker
                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(mDstLatLng);
                                markerOptions.title("Destination");
                                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                mDstMarker = mMap.addMarker(markerOptions);

                                if (currentUserInfo.getLatitude() == 0) {
                                    Utils.displayMessage(getActivity(), "There's a problem with your location. Please check it!");
                                } else {
                                    if (mSocket.connected()) {

                                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                        builder.include(mSrcMarker.getPosition());
                                        builder.include(mDstMarker.getPosition());
                                        LatLngBounds bounds = builder.build();
                                        int padding = 300; // offset from edges of the map in pixels
                                        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                                        mMap.moveCamera(cu);

                                        tVQuestion.setText("Would you like to go to " + address + "?");
                                        flowValue = 11;
                                        layoutChanges();
                                    } else {
                                        Utils.displayMessage(getActivity(), "There's a problem with connection.");
                                    }
                                }

                                CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(cmPosition.target.latitude,
                                        cmPosition.target.longitude));
                                CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);
                                mMap.moveCamera(center);
                                mMap.moveCamera(zoom);

                            }
                        } else {

                            dest_lat = "" + cmPosition.target.latitude;
                            if (dest_lat.equalsIgnoreCase(source_lat)) {
                                Toast.makeText(mContext, getString(R.string.source_dest_not_same), Toast.LENGTH_SHORT).show();
                                Intent intentDest = new Intent(getActivity(), GooglePlaceSearchActivity.class);
                                intentDest.putExtra("cursor", "destination");
                                intentDest.putExtra("s_address", frmSource.getText().toString());
                                startActivityForResult(intentDest, PLACE_AUTOCOMPLETE_REQUEST_CODE_DEST);
                            } else {
                                dest_address = getAddressUsingLatLng("destination", frmDest, mContext, "" + cmPosition.target.latitude, "" + cmPosition.target.longitude);
                                dest_lat = "" + cmPosition.target.latitude;
                                dest_lng = "" + cmPosition.target.longitude;

                                mDstLatLng = cmPosition.target;

                                if (mDstMarker != null) {
                                    mDstMarker.remove();
                                }

                                //  Place destination location marker
                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(mDstLatLng);
                                markerOptions.title("Destination");
                                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                mDstMarker = mMap.addMarker(markerOptions);

                                //  Place user(source) location marker
                                mSrcLatLng = new LatLng(Double.parseDouble(source_lat), Double.parseDouble(source_lng));
                                addSrcMarker(mSrcLatLng);

                                if (currentUserInfo.getLatitude() == 0) {
                                    Utils.displayMessage(getActivity(), "There's a problem with your location. Please check it!");
                                } else {
                                    if (mSocket.connected()) {

                                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                        builder.include(mSrcMarker.getPosition());
                                        builder.include(mDstMarker.getPosition());
                                        LatLngBounds bounds = builder.build();
                                        int padding = 300; // offset from edges of the map in pixels
                                        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                                        mMap.moveCamera(cu);

                                        tVQuestion.setText("Would you like to go to " + address + "?");
                                        flowValue = 11;
                                        layoutChanges();
                                    } else {
                                        Utils.displayMessage(getActivity(), "There's a problem with connection.");
                                    }
                                }

                                strPickLocation = "";
                                strPickType = "";

                                CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(cmPosition.target.latitude,
                                        cmPosition.target.longitude));
                                CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);
                                mMap.moveCamera(center);
                                mMap.moveCamera(zoom);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(mContext, "Can't able to get the address!.Please try again", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        btnSetDestYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(
//                        new CameraPosition.Builder()
//                                .target(new LatLng(currentUserInfo.getLatitude(), currentUserInfo.getLongitude()))
//                                .tilt(90)
//                                .build()));

                displayScreenSelectService();
            }
        });

        btnSetDestNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                displayFirstScreen();
            }
        });

        btnDistanceDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (rootView.findViewById(R.id.lyt_distance_detail).getVisibility() == View.VISIBLE){
                    rootView.findViewById(R.id.lyt_distance_detail).setVisibility(View.GONE);
                    btnDistanceDetail.setImageResource(R.drawable.drop_down);
                } else {
                    rootView.findViewById(R.id.lyt_distance_detail).setVisibility(View.VISIBLE);
                    btnDistanceDetail.setImageResource(R.drawable.drop_up);
                }
            }
        });

        btnETADetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rootView.findViewById(R.id.lyt_eta_detail).getVisibility() == View.VISIBLE){
                    rootView.findViewById(R.id.lyt_eta_detail).setVisibility(View.GONE);
                    btnETADetail.setImageResource(R.drawable.drop_down);
                } else {
                    rootView.findViewById(R.id.lyt_eta_detail).setVisibility(View.VISIBLE);
                    btnETADetail.setImageResource(R.drawable.drop_up);
                }
            }
        });

        lnrSelectServiceType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnRequestRides.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mListDriverInfo.size() > 0){

                    final Dialog dialog = new Dialog(mContext);

                    dialog.setContentView(R.layout.dialog_confirm_payment_mode);
                    dialog.show();

                    if (strPaymentModeDlg.equalsIgnoreCase("CASH"))
                        ((TextView) dialog.findViewById(R.id.tvCurrentSelectedMode)).setText(getString(R.string.cash_mode_is_selected));
                    else
                        ((TextView) dialog.findViewById(R.id.tvCurrentSelectedMode)).setText(getString(R.string.app_wallet_mode_is_selected));

                    dialog.findViewById(R.id.closeIcon).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Close dialog
                            dialog.dismiss();
                        }
                    });

                    dialog.findViewById(R.id.confirmIcon).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            SharedHelper.putKey(mContext, "payment_mode", strPaymentModeDlg);
                            dialog.dismiss();

                            sendRequestEstimateFare();
                        }
                    });

                    dialog.findViewById(R.id.imgWallet).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            ((TextView) dialog.findViewById(R.id.tvCurrentSelectedMode)).setText(getString(R.string.app_wallet_mode_is_selected));

                            dialog.findViewById(R.id.lytSelectPaymentDlg).setVisibility(View.GONE);
                            dialog.findViewById(R.id.lytWalletDlg).setVisibility(View.VISIBLE);
                            ((TextView) dialog.findViewById(R.id.tvDescPaymentModeDlg)).setText(getString(R.string.wallet) + " " + getString(R.string.balance) + "$" +
                                    SharedHelper.getKey(mContext, "wallet_balance"));

                            strPaymentModeDlg = "WALLET";
                        }
                    });

                    dialog.findViewById(R.id.imgCash).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ((TextView) dialog.findViewById(R.id.tvCurrentSelectedMode)).setText(getString(R.string.cash_mode_is_selected));

                            strPaymentModeDlg = "CASH";
                        }
                    });

                    dialog.findViewById(R.id.imgTopUpWalletDlg).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            tvWalletBalanceDlg = dialog.findViewById(R.id.tvDescPaymentModeDlg);

                            Intent intent = new Intent(getActivity(), WalletActivity.class);
                            startActivityForResult(intent, WALLET_TOPUP_REQUEST_CODE);

                        }
                    });

                    dialog.findViewById(R.id.imgSwitchPaymentDlg).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            strPaymentModeDlg = "CASH";
                            ((TextView) dialog.findViewById(R.id.tvCurrentSelectedMode)).setText(getString(R.string.cash_mode_is_selected));
                        }
                    });

                } else{
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mActivity);
                    LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    builder.setTitle(mContext.getResources().getString(R.string.app_name))
                            .setIcon(getResources().getDrawable(R.drawable.cooper_logo))
                            .setMessage(mContext.getResources().getString(R.string.not_exist_driver_of_service));
                    builder.setCancelable(false);
                    builder.setPositiveButton(mContext.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();
                }

            }
        });

        btnRequestRideConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (SharedHelper.getKey(mContext, "payment_mode").equalsIgnoreCase("WALLET") && mWalletBalance <= 0){

                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mActivity);
                    LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    builder.setTitle(mContext.getResources().getString(R.string.app_name))
                            .setIcon(getResources().getDrawable(R.drawable.cooper_logo))
                            .setMessage(mContext.getResources().getString(R.string.wallet_smaller_than_zero));
                    builder.setCancelable(false);
                    builder.setPositiveButton(mContext.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            SharedHelper.putKey(mContext, "payment_mode", "CASH");
                            displayScreenChooseDriver();
                        }
                    }).show();

                } else {
                    displayScreenChooseDriver();
                }

            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                flowValue = 2;
                layoutChanges();
            }
        });

        btnRequestNearest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequestRide("");
                displayScreenFindingRide();
            }
        });

        imgSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                flowValue = 7;
                layoutChanges();

            }
        });

        scheduleDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calender class's instance and get current date , month and year from calender
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                datePickerDialog = new DatePickerDialog(mActivity,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                // set day of month , month and year value in the edit text
                                String choosedMonth = "";
                                String choosedDate = "";
                                String choosedDateFormat = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                                scheduledDate = choosedDateFormat;
                                try {
                                    choosedMonth = Utils.getMonth(choosedDateFormat);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                if (dayOfMonth < 10) {
                                    choosedDate = "0" + dayOfMonth;
                                } else {
                                    choosedDate = "" + dayOfMonth;
                                }
                                afterToday = Utils.isAfterToday(year, monthOfYear, dayOfMonth);
                                scheduleDate.setText(choosedDate + " " + choosedMonth + " " + year);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.getDatePicker().setMaxDate((System.currentTimeMillis() - 1000) + (1000 * 60 * 60 * 24 * 7));
                datePickerDialog.show();
            }
        });

        scheduleTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(mActivity, new TimePickerDialog.OnTimeSetListener() {
                    int callCount = 0;   //To track number of calls to onTimeSet()

                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                        if (callCount == 0) {
                            String choosedHour = "";
                            String choosedMinute = "";
                            String choosedTimeZone = "";
                            String choosedTime = "";

                            scheduledTime = selectedHour + ":" + selectedMinute;

                            if (selectedHour > 12) {
                                choosedTimeZone = "PM";
                                selectedHour = selectedHour - 12;
                                if (selectedHour < 10) {
                                    choosedHour = "0" + selectedHour;
                                } else {
                                    choosedHour = "" + selectedHour;
                                }
                            } else {
                                choosedTimeZone = "AM";
                                if (selectedHour < 10) {
                                    choosedHour = "0" + selectedHour;
                                } else {
                                    choosedHour = "" + selectedHour;
                                }
                            }

                            if (selectedMinute < 10) {
                                choosedMinute = "0" + selectedMinute;
                            } else {
                                choosedMinute = "" + selectedMinute;
                            }
                            choosedTime = choosedHour + ":" + choosedMinute + " " + choosedTimeZone;

                            if (scheduledDate != "" && scheduledTime != "") {
                                Date date = null;
                                try {
                                    date = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).parse(scheduledDate);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                long milliseconds = date.getTime();
                                if (!DateUtils.isToday(milliseconds)) {
                                    scheduleTime.setText(choosedTime);
                                } else {
                                    if (Utils.checkTime(scheduledTime)) {
                                        scheduleTime.setText(choosedTime);
                                    } else {
                                        Utils.displayMessage(mActivity, getString(R.string.different_time));
                                    }
                                }
                            } else {
                                Utils.displayMessage(mActivity, getString(R.string.choose_date_time));
                            }
                        }
                        callCount++;
                    }
                }, hour, minute, false);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        scheduleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedHelper.putKey(mContext, "name", "");
                if (scheduledDate != "" && scheduledTime != "") {
                    Date date = null;
                    try {
                        date = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).parse(scheduledDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    long milliseconds = date.getTime();
                    if (!DateUtils.isToday(milliseconds)) {

                        Utils.displayMessage(mActivity, "Sending request...");

                        //sendRequest();
                        displayScreenChooseDriver();
                    } else {
                        if (Utils.checkTime(scheduledTime)) {
                            Utils.displayMessage(mActivity, "Sending request...");
                            //sendRequest();
                            displayScreenChooseDriver();
                        } else {
                            Utils.displayMessage(mActivity, getString(R.string.different_time));
                        }
                    }
                } else {
                    Utils.displayMessage(mActivity, getString(R.string.choose_date_time));
                }
            }
        });

        leftArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mPosServiceType == 1)
                    leftArrow.setVisibility(View.INVISIBLE);

                mPosServiceType --;

                mViewPagerServiceType.setCurrentItem(mPosServiceType);
            }
        });

        //  In Selecting service type, next and prev button's click listeners.
        rightArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if ((mPosServiceType + 2) == mCntServiceType)
                    rightArrow.setVisibility(View.INVISIBLE);

                mPosServiceType ++;

                mViewPagerServiceType.setCurrentItem(mPosServiceType);
            }
        });

        btnCancelRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                displayCancelRideDialog();
            }
        });

        btnCancelTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                displayCancelRideDialog();
            }
        });

        imgSos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(mContext);

                dialog.setContentView(R.layout.dialog_select_sos);
                dialog.show();

                dialog.findViewById(R.id.closeIcon).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Close dialog
                        dialog.dismiss();
                    }
                });

                dialog.findViewById(R.id.imgSOSCall).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intentCall = new Intent(Intent.ACTION_CALL);
                        intentCall.setData(Uri.parse("tel:" + GlobalConstants.g_sos_phone));
                        startActivity(intentCall);

                        dialog.dismiss();
                    }
                });

                dialog.findViewById(R.id.imgSosEmail).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        final Dialog emailDialog = new Dialog(mContext);

                        emailDialog.setContentView(R.layout.dialog_sos_email);
                        emailDialog.show();

                        emailDialog.findViewById(R.id.closeIcon).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Close dialog
                                emailDialog.dismiss();
                            }
                        });

                        emailDialog.findViewById(R.id.sendEmail).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                String strEmailContent = "content: " + ((EditText) emailDialog.findViewById(R.id.etEmailContent)).getText().toString();
                                strEmailContent += ", current_latitude: " + currentUserInfo.getLatitude();
                                strEmailContent += ", current_longitude: " + currentUserInfo.getLongitude();

                                final ProgressDialog progressDialog = new ProgressDialog(mContext);
                                progressDialog.setMessage("Please wait...");
                                progressDialog.setCancelable(false);
                                progressDialog.show();

                                ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

                                JsonObject gsonObject = new JsonObject();
                                try {
                                    JSONObject paramObject = new JSONObject();

                                    paramObject.put("api_token", SharedHelper.getKey(mContext, "api_token"));
                                    paramObject.put("msg_text", strEmailContent);

                                    JsonParser jsonParser = new JsonParser();
                                    gsonObject = (JsonObject) jsonParser.parse(paramObject.toString());

                                    apiInterface.doSendSOSEmail(gsonObject).enqueue(new Callback<ResponseBody>() {
                                        @Override
                                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                                            progressDialog.dismiss();
                                            emailDialog.dismiss();
                                        }

                                        @Override
                                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                                            progressDialog.dismiss();
                                            t.printStackTrace();
                                            Utils.displayMessage(mActivity, getString(R.string.server_connect_error));
                                        }
                                    });

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                });
            }
        });

        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1);
                } else {

                    final Dialog dialog = new Dialog(mContext);

                    dialog.setContentView(R.layout.dialog_select_call);
                    dialog.setTitle("Select Call");
                    dialog.show();

                    dialog.findViewById(R.id.closeIcon).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Close dialog
                            dialog.dismiss();
                        }
                    });

                    dialog.findViewById(R.id.voiceCall).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (mAcceptedDriverInfo != null) {
                                Intent intentCall = new Intent(Intent.ACTION_CALL);
                                intentCall.setData(Uri.parse("tel:" + mAcceptedDriverInfo.getPhoneNumber()));
                                startActivity(intentCall);
                            } else{
                                Toast.makeText(mContext, "The phone number of this driver doesn't exist", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    dialog.findViewById(R.id.videoCall).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//
//                            try {
//                                Intent newIntent = new Intent(mActivity, OpponentsActivity.class);
//                                newIntent.putExtra("qb_user", QBMgr.getUser());
//                                newIntent.putExtra("qb_driver", qbUserAcceptedDriver);
//                                startActivity(newIntent);
//                            } catch (Exception e){
//                                e.printStackTrace();
//                            }
//                            //

                            try {
                                ArrayList<Integer> opponentsList = new ArrayList<>();
                                opponentsList.add(qbUserAcceptedDriver.getId());
//                            opponentsList.add(101006687);

                                QBRTCTypes.QBConferenceType conferenceType = true
                                        ? QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_VIDEO
                                        : QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_AUDIO;
                                Log.d("video_call", "conferenceType = " + conferenceType);

                                QBRTCClient qbrtcClient = QBRTCClient.getInstance(mContext);
                                QBRTCSession newQbRtcSession = qbrtcClient.createNewSessionWithOpponents(opponentsList, conferenceType);
                                WebRtcSessionManager.getInstance(mContext).setCurrentSession(newQbRtcSession);
                                PushNotificationSender.sendPushMessage(opponentsList, "111");
                                CallActivity.start(mContext, false);
                            } catch (Exception e){
                                e.printStackTrace();
                                Toast.makeText(mContext, "There's a connection problem with quickblox.", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }
            }
        });

        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    Intent newIntent = new Intent(mContext, MessagingActivity.class);
                    newIntent.putExtra("OfferId", QBMgr.getUser().getId());
                    newIntent.putExtra("OfferName", SharedHelper.getKey(mContext, "first_name") + " " + SharedHelper.getKey(mContext, "last_name"));
                    newIntent.putExtra("OfferPartnerID", qbUserAcceptedDriver.getId());
                    newIntent.putExtra("OfferPartnerName", mAcceptedDriverInfo.getFirstName() + " " + mAcceptedDriverInfo.getLastName());

                    startActivity(newIntent);
                } catch (Exception e){
                    Utils.displayMessage(mActivity, "There's a problem with quickblox account.");
                }
            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (lnrSetDest.getVisibility() == View.VISIBLE){

                    displayFirstScreen();
//                    flowValue = 0;
//                    layoutChanges();
//                    addSrcMarker(new LatLng(mUserLocation.getLatitude(), mUserLocation.getLongitude()), 0);
//                    LatLng myLocation = new LatLng(mUserLocation.getLatitude(), mUserLocation.getLongitude());
//                    CameraPosition cameraPosition = new CameraPosition.Builder().target(myLocation).zoom(16).build();
//                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                } else if (lnrSelectServiceType.getVisibility() == View.VISIBLE) {
//                    flowValue = 0;
//                    layoutChanges();
//                    addSrcMarker(new LatLng(mUserLocation.getLatitude(), mUserLocation.getLongitude()), 0);

                    displayFirstScreen();

                    srcDestLayout.setVisibility(View.GONE);
//                    frmSource.setOnClickListener(new OnClick());
//                    frmDest.setOnClickListener(new OnClick());
                    srcDestLayout.setOnClickListener(null);

//                    LatLng myLocation = new LatLng(mUserLocation.getLatitude(), mUserLocation.getLongitude());
//                    CameraPosition cameraPosition = new CameraPosition.Builder().target(myLocation).zoom(16).build();
//                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                } else if (lnrApproximate.getVisibility() == View.VISIBLE) {
//                    frmSource.setOnClickListener(new OnClick());
//                    frmDest.setOnClickListener(new OnClick());
                    srcDestLayout.setOnClickListener(null);
                    flowValue = 1;
                    layoutChanges();
                } else if (lnrWaitingForProviders.getVisibility() == View.VISIBLE) {
                    flowValue = 2;
                    layoutChanges();
                } else if (ScheduleLayout.getVisibility() == View.VISIBLE) {
                    flowValue = 2;
                    layoutChanges();
                }

            }
        });

        btnPayNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendPayNow();
            }
        });

        btnPaymentDoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                btnPayNow.setVisibility(View.GONE);
//                btnPaymentDoneBtn.setVisibility(View.GONE);
//
//                displayScreenToRate();
            }
        });

        btnSubmitReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendReview();
            }
        });

    }

    private void cancelCouponCodeSelect() {

        mCouponCode = "";
        calculatePrices();
    }

    private void sendRequestEstimateFare() {
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        JsonObject gsonObject = new JsonObject();
        try {
            JSONObject paramObject = new JSONObject();

            paramObject.put("api_token", SharedHelper.getKey(mContext, "api_token"));
            paramObject.put("service_type", mServiceType);
            paramObject.put("meter", mDistanceMeter);
            paramObject.put("seconds", mTimeSeconds);

            JsonParser jsonParser = new JsonParser();
            gsonObject = (JsonObject) jsonParser.parse(paramObject.toString());

            apiInterface.doGetEstimatedFair(gsonObject).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    progressDialog.dismiss();

                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        JSONObject data = object.getJSONObject("data");

                        if (object.getString("status").equals("1")){

                            JSONObject result = data.getJSONObject("data");

                            try {
                                mEstimatedFair = result.getDouble("estimated_fare");
                            } catch (Exception e){
                                e.printStackTrace();
                                mEstimatedFair = 0;
                            }
                            try {
                                mSurgeValue = result.getDouble("surge_value");
                            } catch (Exception e){
                                e.printStackTrace();
                                mSurgeValue = 0;
                            }
                            try {
                                mSurgeTrigger = result.getInt("surge_trigger");
                            } catch (Exception e){
                                e.printStackTrace();
                                mSurgeTrigger = 0;
                            }
                            try {
                                mTaxPrice = result.getDouble("tax_price");
                            } catch (Exception e){
                                e.printStackTrace();
                                mTaxPrice = 0;
                            }
                            try {
                                mBasePrice = result.getDouble("base_price");
                            } catch (Exception e){
                                e.printStackTrace();
                                mBasePrice = 0;
                            }
                            try {
                                mWalletBalance = result.getDouble("wallet_balance");
                            } catch (Exception e){
                                e.printStackTrace();
                                mWalletBalance = 0;
                            }

                            SharedHelper.putKey(mContext, "wallet_balance", "" + mWalletBalance);

                            displayScreenRequestRide();

                        } else{
                            Utils.displayMessage(mActivity, Utils.parseErrorMessage(data));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Utils.displayMessage(mActivity, getString(R.string.server_connect_error));
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    progressDialog.dismiss();
                    t.printStackTrace();
                    Utils.displayMessage(mActivity, getString(R.string.server_connect_error));
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*  Show dialog for cancel ride*/
    private void displayCancelRideDialog() {

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mActivity);
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        builder.setTitle(mContext.getResources().getString(R.string.app_name))
                .setIcon(getResources().getDrawable(R.drawable.cooper_logo))
                .setMessage(mContext.getResources().getString(R.string.cancel_ride_alert));
        builder.setCancelable(false);
        builder.setPositiveButton(mContext.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                displayCancelReasonDialog();
            }
        });
        builder.setNegativeButton(mContext.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        cancelRideDialog = builder.create();
        cancelRideDialog.show();
    }

    /*  Show dialog for input reason of cancel*/
    private void displayCancelReasonDialog() {

        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
        View view = LayoutInflater.from(mContext).inflate(R.layout.cancel_dialog, null);
        final EditText reasonEtxt = view.findViewById(R.id.reason_etxt);
        Button submitBtn = view.findViewById(R.id.submit_btn);
        builder.setIcon(getResources().getDrawable(R.drawable.cooper_logo))
                .setTitle(mContext.getResources().getString(R.string.app_name))
                .setView(view)
                .setCancelable(true);
        reasonDialog = builder.create();
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (reasonEtxt.getText().toString().equalsIgnoreCase(""))
                    Toast.makeText(mContext, "Please input the reason.", Toast.LENGTH_SHORT).show();
                else if (!mSocket.connected())
                    Toast.makeText(mContext, "Sorry. There's a issue with connection, so you can't request this.", Toast.LENGTH_SHORT).show();
                else {
                    sendCancelRide(reasonEtxt.getText().toString());
                    reasonDialog.dismiss();
                    displayFirstScreen();
                }
            }
        });
        reasonDialog.show();
    }

    /*  create socket and connect*/
    public void connectToSocket() {

        try {

            mSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

                @Override
                public void call(Object... args) {

                    Log.i("Event_Socket_Connect", "Boldman----->Event_Socket_Connect----->");
                    sendUserInfo();
                }

            });

            mSocket.connect();

            int times = 0;

            while(!mSocket.connected() && times * DELAY <= TIMEOUT * 1000){
                Thread.sleep(DELAY);
                Log.i("connecting...","Boldman---->[Websocket Connecting] Connecting "+(++times*0.5)+" times");
            }

            if(!mSocket.connected()){
                if(mSocket!=null) {
                    mSocket.close();
                    Utils.displayMessage(getActivity(), getString(R.string.server_connect_error));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /*  create socket and define emit listeners*/
    private void defineSocketListeners() {

        isSocketConnected = mSocket.connected();

        Log.i("creatSocket", "Boldman------> Socket connection! " + isSocketConnected);

        mSocket.on(GlobalConstants.URL_CALLBACK, new Emitter.Listener() {
            @Override
            public void call(Object... response) {

                try {
                    JSONArray res = new JSONArray(response[0].toString());

                    JsonParser parser = new JsonParser();

                    JsonArray data = (JsonArray) parser.parse(res.toString());

                    if (res.length() != 0) {

                        mCallBack = new Gson().fromJson(data, new TypeToken<List<Callback>>() {
                        }.getType());

                        ((Activity) getContext()).runOnUiThread(new Runnable() {
                            public void run() {
                                //Code goes here
                            }
                        });

                    } else {

                    }
                    // add function
                    //Log.i("Near Driver", "Boldman Near Driver --- call------->");
                } catch (Exception e) {
                    e.printStackTrace();
//                    Toast.makeText(getActivity(), "Sorry. There's a problem with connection.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        /*  When the location of user or driver is changed, this is called.
        *   This shows the drivers near you.*/
        mSocket.on(GlobalConstants.URL_NEAR_DRIVERS, new Emitter.Listener() {
            @Override
            public void call(final Object... response) {

                try {

                    mListDriverInfo = new ArrayList<>();

                    JSONArray res = new JSONArray(response[0].toString());

                    JsonParser parser = new JsonParser();
                    JsonArray data = (JsonArray) parser.parse(res.toString());

                    if (res.length() != 0) {

                        mListDriverInfo = new Gson().fromJson(data, new TypeToken<List<DriverInfo>>() {
                        }.getType());

                        Log.i("near_drivers", mListDriverInfo + "");

                        for (DriverInfo drvInfo : mListDriverInfo){

                            Object arrCarImage = drvInfo.getCarImage();
                            List<CarImage> listCarImage = new ArrayList<>();

                            JsonParser parser1 = new JsonParser();
                            JsonArray data1 = (JsonArray) parser1.parse(arrCarImage.toString());

                            listCarImage = new Gson().fromJson(data1, new TypeToken<List<CarImage>>() {
                            }.getType());

                            drvInfo.setCarImageList(listCarImage);
                        }

                        setDriverList(mListDriverInfo);

                        ((Activity) getContext()).runOnUiThread(new Runnable() {
                            public void run() {
                                //Code goes here

                                showNearDrivers();

                            }
                        });

                    } else {

                        ((Activity) getContext()).runOnUiThread(new Runnable() {
                            public void run() {
                                //Code goes here

                                cleanAllDriverMarkers();

                            }
                        });
                    }
                    // add function
                    //Log.i("Near Driver", "Boldman Near Driver --- call------->");
                } catch (Exception e) {
                    e.printStackTrace();
//                    Toast.makeText(getActivity(), "Sorry. There's a problem with connection.", Toast.LENGTH_SHORT).show();
                }
            }

        });

        /*  When server responds to the request for ride, this is called.*/
        mSocket.on(GlobalConstants.URL_RESPONSE_RIDE, new Emitter.Listener() {
            @Override
            public void call(Object... response) {
                JSONObject res = null;
                try {
                    res = new JSONObject(response[0].toString());

                    if (res.length() != 0) {

                        Log.i("response_ride", "Boldman------> Response Ride! ------>");

                        if (mRideKey.equals(res.getString("request_key")))
                            mRideId = res.getInt("ride_id");

                        if (mBookingKey.equals(res.getString("request_key")))
                            mBookingId = res.getInt("ride_id");

                        Log.i("ride_id", mRideId + "");

                    } else {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
//                    Toast.makeText(getActivity(), "Sorry. There's a problem with connection.", Toast.LENGTH_SHORT).show();
                }
            }
        });


        /*  When ride is accepted by driver, this is called.*/
        mSocket.on(GlobalConstants.URL_ACCEPTED_BY_DRIVER, new Emitter.Listener() {
            @Override
            public void call(Object... response) {
                JSONObject res = null;
                try {
                    res = new JSONObject(response[0].toString());

                    JsonParser parser = new JsonParser();
                    JsonObject data = (JsonObject) parser.parse(res.toString());

                    if (res.length() != 0) {
                        mAcceptedDriverInfo = new Gson().fromJson(data, new TypeToken<AcceptedDriverInfo>() {
                        }.getType());

                        qbUserAcceptedDriver = new QBUser();
                        qbUserAcceptedDriver.setId(res.getInt("quickblox_id"));
                        qbUserAcceptedDriver.setLogin(res.getString("quickblox_username"));
                        qbUserAcceptedDriver.setPassword(res.getString("quickblox_password"));

                        Log.i("accepted_by_driver", "Boldman------> Accepted by driver! ------>" + data);

                        mHandlerWaiting.removeCallbacks(mRunnableWaiting);

                        ((Activity) getContext()).runOnUiThread(new Runnable() {
                            public void run() {

                                if (mBookingKey.equalsIgnoreCase(""))
                                    displayScreenAcceptedDriver();
                                else
                                    displayFirstScreen();
                            }
                        });

                        //sendAgreeToDriver(mAcceptedDriverInfo.getEmail());

                    } else {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
//                    Toast.makeText(getActivity(), "Sorry. There's a problem with connection.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        /*  When accepted by driver, this is called.
         *   This shows only the accepted driver.*/
        mSocket.on(GlobalConstants.URL_RIDE_DRIVER, new Emitter.Listener() {
            @Override
            public void call(final Object... response) {

                try {

                    Log.i("RIDE_DRIVER1111", response[0].toString());

                    JSONObject res = new JSONObject(response[0].toString());

                    Log.i("RIDE_DRIVER", response[0].toString());

                    if (res.length() != 0) {

                        DriverInfo drvInfo = new DriverInfo();

                        drvInfo.setEmail(res.getString("email"));
                        drvInfo.setGender(res.getInt("gender"));
                        drvInfo.setFirstName(res.getString("firstname"));
                        drvInfo.setLastName(res.getString("lastname"));
                        drvInfo.setPhoneNumber(res.getString("phone_number"));
                        drvInfo.setLatitude((float)res.getDouble("latitude"));
                        drvInfo.setLongitude((float)res.getDouble("longitude"));
                        drvInfo.setAngle((float)res.getDouble("angle"));
                        drvInfo.setAvatar(res.getString("avatar"));
                        drvInfo.setAvgRating((float)res.getDouble("avg_rating"));
                        drvInfo.setCarNumber(res.getString("car_number"));
                        drvInfo.setDriverTime(res.getString("driver_time"));

//                        List<CarImage> carImageList = new ArrayList<>();
//
//                        JSONArray carImageArray = res.getJSONArray("car_image");
//                        for (int i = 0; i < carImageArray.length(); i ++){
//                            CarImage carImage = new CarImage();
//
//                            carImage.setCarImageId(((JSONObject)carImageArray.get(i)).getInt("car_image_id"));
//                            carImage.setCarImageUrl(((JSONObject)carImageArray.get(i)).getString("car_image_url"));
//
//                            carImageList.add(carImage);
//                        }
//
//                        drvInfo.setCarImageList(carImageList);

                        Log.i("TAG", "Boldman--> Ride_driver");



                        ((Activity) getContext()).runOnUiThread(new Runnable() {
                            public void run() {

                                setRideDriver(drvInfo);

                                //Code goes here
                                showNearDrivers();
                            }
                        });

                    } else {

                        ((Activity) getContext()).runOnUiThread(new Runnable() {
                            public void run() {

                                //Code goes here
                                cleanAllDriverMarkers();
                            }
                        });
                    }
                    // add function
                    //Log.i("Near Driver", "Boldman Near Driver --- call------->");
                } catch (Exception e) {
                    e.printStackTrace();
//                    Toast.makeText(getActivity(), "Sorry. There's a problem with connection.", Toast.LENGTH_SHORT).show();
                }
            }

        });

        /*  When driver cancel ride, this is called.*/
        mSocket.on(GlobalConstants.URL_CANCEL_USER_RIDE, new Emitter.Listener() {

            @Override
            public void call(Object... response) {

                JSONObject res = null;
                try {
                    res = new JSONObject(response[0].toString());

                    if (res.length() != 0) {

                        String strReason = res.getString("cancel_reason");

                        Log.i("Cancel_User_Ride", "Boldman--->Canceled by driver!--------->" + strReason);

                        ((Activity) getContext()).runOnUiThread(new Runnable() {
                            public void run() {
                                displayFirstScreen();
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
//                    Toast.makeText(getActivity(), "Sorry. There's a problem with connection.", Toast.LENGTH_SHORT).show();
                }

            }
        });


        /*  When driver arrived, this is called.*/
        mSocket.on(GlobalConstants.URL_REQUEST_USER_ARRIVED, new Emitter.Listener() {

            @Override
            public void call(Object... response) {

                JSONObject res = null;
                try {
                    res = new JSONObject(response[0].toString());

                    JsonParser parser = new JsonParser();
                    JsonObject data = (JsonObject) parser.parse(res.toString());

                    if (res.length() != 0) {
                        mUserArrived = new Gson().fromJson(data, new TypeToken<UserArrived>() {
                        }.getType());

                        Log.i("driver_arrived", "Boldman--->Driver arrived!--------->");

                        ((Activity) getContext()).runOnUiThread(new Runnable() {
                            public void run() {
                                displayScreenArrivedDriver();
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
//                    Toast.makeText(getActivity(), "Sorry. There's a problem with connection.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mSocket.on(GlobalConstants.URL_START_RIDE, new Emitter.Listener() {

            @Override
            public void call(Object... response) {

                JSONObject res = null;
                try {
                    res = new JSONObject(response[0].toString());

                    JsonParser parser = new JsonParser();
                    JsonObject data = (JsonObject) parser.parse(res.toString());

                    if (res.length() != 0) {
                        mStartRide = new Gson().fromJson(data, new TypeToken<StartRide>() {
                        }.getType());

                        Log.i("start_ride", "Boldman--->Start Ride!--------->");

                        ((Activity) getContext()).runOnUiThread(new Runnable() {
                            public void run() {
                                displayScreenStartRide();
                            }
                        });

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
//                    Toast.makeText(getActivity(), "Sorry. There's a problem with connection.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mSocket.on(GlobalConstants.URL_REQUEST_USER_PAY, new Emitter.Listener() {

            @Override
            public void call(Object... response) {

                JSONObject res = null;
                try {
                    res = new JSONObject(response[0].toString());

                    JsonParser parser = new JsonParser();
                    JsonObject data = (JsonObject) parser.parse(res.toString());

                    if (res.length() != 0) {
                        mRequestUserPay = new Gson().fromJson(data, new TypeToken<RequestUserPay>() {
                        }.getType());

                        Log.i("request_user_pay", "Boldman--->Request User Pay!--------->" + res.toString());

                        ((Activity) getContext()).runOnUiThread(new Runnable() {
                            public void run() {
                                displayScreenToPay();
                            }
                        });

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
//                    Toast.makeText(getActivity(), "Sorry. There's a problem with connection.", Toast.LENGTH_SHORT).show();
                }
            }
        });


        mSocket.on(GlobalConstants.URL_REQUEST_USER_RATE, new Emitter.Listener() {

            @Override
            public void call(Object... response) {

                JSONObject res = null;
                try {
                    res = new JSONObject(response[0].toString());

                    JsonParser parser = new JsonParser();
                    JsonObject data = (JsonObject) parser.parse(res.toString());

                    if (res.length() != 0) {
                        mRequestUserRate = new Gson().fromJson(data, new TypeToken<RequestUserRate>() {
                        }.getType());

                        Log.i("request_user_rate", "Boldman--->Request User Rate!--------->");

                        ((Activity) getContext()).runOnUiThread(new Runnable() {
                            public void run() {
                                displayScreenToRate();
                            }
                        });

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
//                    Toast.makeText(getActivity(), "Sorry. There's a problem with connection.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mSocket.on(GlobalConstants.URL_FINISH_RIDE, new Emitter.Listener() {

            @Override
            public void call(Object... response) {

                JSONObject res = null;
                try {
                    res = new JSONObject(response[0].toString());

                    JsonParser parser = new JsonParser();
                    JsonObject data = (JsonObject) parser.parse(res.toString());

                    if (res.length() != 0) {
                        mFinishRide = new Gson().fromJson(data, new TypeToken<FinishRide>() {
                        }.getType());

                        Log.i("finish_ride", "Boldman--->Finish ride!--------->");

                        ((Activity) getContext()).runOnUiThread(new Runnable() {
                            public void run() {
                                displayScreenToRate();
                            }
                        });

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
//                    Toast.makeText(getActivity(), "Sorry. There's a problem with connection.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /*  Clean all marker of near drivers.*/
    private void cleanAllDriverMarkers() {

        int i = 0;
        if (mListNearDriver != null) {
            for (i = 0; i < mListNearDriver.size(); i++) {
                if (((Marker)mListNearDriver.get(i).get("marker")) != null)
                    ((Marker)mListNearDriver.get(i).get("marker")).remove();
            }

            mListNearDriver = new ArrayList<>();
        }

        Log.i("Clean_driverMarker", "Boldman----->Clean_All_Driver_Marker--->");
    }

    /*  Show first screen of map*/
    private void displayFirstScreen(){

        //  onPOIClickListener -> enable.
        mStateDraggable = 1;
        mServiceType = 0;
        mBookingKey = "";

        //  First UI
        flowValue = 0;
        layoutChanges();

        //  Add marker to user's location
//        LatLng latLng = new LatLng(mUserLocation.getLatitude(), mUserLocation.getLongitude());
//        addSrcMarker(latLng, 0);
        if (mSrcMarker != null)
            mSrcMarker.remove();

        if (mDstMarker != null)
            mDstMarker.remove();

        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.cancelAll();
        }

//        mMap.clear();

        //  Set camera position to user's location
//        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(16).build();
//        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    /*  Show screen of selecting service type.*/
    private void displayScreenSelectService() {

        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        JsonObject gsonObject = new JsonObject();
        try {
            JSONObject paramObject = new JSONObject();

            paramObject.put("api_token", SharedHelper.getKey(mActivity, "api_token"));

            JsonParser jsonParser = new JsonParser();
            gsonObject = (JsonObject) jsonParser.parse(paramObject.toString());

            apiInterface.doGetWalletBalance(gsonObject).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    progressDialog.dismiss();

                    try {
                        JSONObject object = new JSONObject(response.body().string());

                        if (object.getString("status").equals("1")){

                            JSONObject data = object.getJSONObject("data");

                            SharedHelper.putKey(mContext, "wallet_balance", data.getString("balance"));
                            SharedHelper.putKey(mContext, "payment_mode", "WALLET");

                            displayScreenSelectService_NoGetBalance();

                        } else{
                            JSONObject data = object.getJSONObject("data");
                            SharedHelper.putKey(mContext, "payment_mode", "CASH");
                            Utils.displayMessage(mActivity, Utils.parseErrorMessage(data));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Utils.displayMessage(mActivity, getString(R.string.server_connect_error));
                        SharedHelper.putKey(mContext, "payment_mode", "CASH");
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    progressDialog.dismiss();
                    t.printStackTrace();
                    Utils.displayMessage(mActivity, getString(R.string.server_connect_error));
                    SharedHelper.putKey(mContext, "payment_mode", "CASH");
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
            SharedHelper.putKey(mContext, "payment_mode", "CASH");
        }
    }

    private void initViewPagerServiceType() {

        mPosServiceType = 0;

        if (mListServiceType.get(0).getName().equalsIgnoreCase("Cooper Cool"))
            btnRequestRides.setText("CONFIRM COOL");
        else
            btnRequestRides.setText("CONFIRM");

//        capacityText.setText("1-" + mListServiceType.get(0).getCapacity());

        leftArrow.setVisibility(View.VISIBLE);
        rightArrow.setVisibility(View.VISIBLE);

        leftArrow.setVisibility(View.INVISIBLE);

        mServiceType = mListServiceType.get(0).getId();
        selectedServiceType = mListServiceType.get(0);

        sendUserUpdateLocation();
    }

    private void displayScreenSelectService_NoGetBalance(){

        if (mListServiceType.size() > 0) {

            mApplyTilt = true;

            //mMap.clear();

            // Getting URL to the Google Directions API
            String url = getDirectionsUrl(mSrcLatLng, mDstLatLng);

            Log.i("DirectionUrl", url);

            DownloadTask downloadTask = new DownloadTask();

            // Start downloading json data from Google Directions API
            downloadTask.execute(url);

            //  Display screen of selecting service type
            flowValue = 1;
            layoutChanges();

            mStateDraggable = 0;

            //getApproximateFare(true);

            mCntServiceType = mListServiceType.size();

            ////////////////////////// ViewPager Adapter /////////////////////////////
            mViewPagerServiceType = rootView.findViewById(R.id.pagerServiceTypes);
            mViewPagerServiceType.setClipChildren(false);
            mViewPagerServiceType.setOffscreenPageLimit(3);
            mViewPagerServiceType.setAdapter(new ServiceTypePagerAdapter(MapFragment.this, mListServiceType, 1));

            mPosServiceType = 0;

            initViewPagerServiceType();

            mViewPagerServiceType.addOnPageChangeListener(new ViewPager.OnPageChangeListener(){

                @Override
                public void onPageScrolled(int i, float v, int i1) {

                }

                @Override
                public void onPageSelected(int i) {

                    mPosServiceType = i;

                    String serviceType = mListServiceType.get(i).getName();

                    if (serviceType.equalsIgnoreCase("Cooper Cool"))
                        btnRequestRides.setText("CONFIRM COOL");
                    else if (serviceType.equalsIgnoreCase("Cooper Elite"))
                        btnRequestRides.setText("CONFIRM ELITE");
                    else if (serviceType.equalsIgnoreCase("Extra Seats"))
                        btnRequestRides.setText("CONFIRM EXTRA");
                    else if (serviceType.equalsIgnoreCase("Taxi"))
                        btnRequestRides.setText("CONFIRM TAXI");
                    else if (serviceType.equalsIgnoreCase("Public Buses"))
                        btnRequestRides.setText("CONFIRM BUS");

//                    capacityText.setText("1-" + mListServiceType.get(i).getCapacity());

                    leftArrow.setVisibility(View.VISIBLE);
                    rightArrow.setVisibility(View.VISIBLE);

                    if (i == 0)
                        leftArrow.setVisibility(View.INVISIBLE);
                    else if ((i + 1) == mCntServiceType)
                        rightArrow.setVisibility(View.INVISIBLE);

                    mServiceType = mListServiceType.get(i).getId();
                    selectedServiceType = mListServiceType.get(i);

                    sendUserUpdateLocation();
                }

                @Override
                public void onPageScrollStateChanged(int i) {

                }
            });

            if (SharedHelper.getKey(mContext, "payment_mode").equalsIgnoreCase("WALLET")) {

                SharedHelper.putKey(mContext, "payment_mode", "CASH");
                imgPaymentType.setImageResource(R.drawable.money_icon);
                textPayment.setText("CASH");

                strPaymentModeDlg = "CASH";
                lytTopupWallet.setVisibility(View.GONE);
            } else {

                SharedHelper.putKey(mContext, "payment_mode", "WALLET");
                imgPaymentType.setImageResource(R.drawable.wallet);
                textPayment.setText("$" + SharedHelper.getKey(mContext, "wallet_balance"));

                strPaymentModeDlg = "WALLET";
                lytTopupWallet.setVisibility(View.VISIBLE);
            }

            //getProvidersList(SharedHelper.getKey(mContext, "service_type"));
        }

    }

    /*  Show Estimated Fair */
    private void displayScreenRequestRide(){

        mStateDraggable = 0;

        Double surge_price = 0.0d;
        boolean surge  = false;

//        if (mListNearDriver.size() <= mSurgeTrigger && mListNearDriver.size() > 0){
//            surge_price = (mSurgeValue / 100) * estimatedFair;
//            estimatedFair += surge_price;
//            surge = true;
//        }

        if (surge) {
            surgeDiscount.setVisibility(View.VISIBLE);
            surgeTxt.setVisibility(View.VISIBLE);
            surgeDiscount.setText("1");
        } else {
            surgeDiscount.setVisibility(View.GONE);
            surgeTxt.setVisibility(View.GONE);
        }

        lblApproxAmount.setText("$" + Utils.checkZeroVal(mEstimatedFair));
        lblTotalDistance.setText(mDistanceKm);
        lblWalletBalance.setText("$" + Utils.checkZeroVal(mWalletBalance));
        lblEta.setText(strTimeTaken);
        lblType.setText(selectedServiceType.getName());

        tvDistanceToPickup.setText("To Pickup Location: " + 0 + "km");
        tvDistanceToDest.setText("To Pickup Location: " + mDistanceKm + "km");
        tvDistanceTotal.setText("Total: " + mDistanceKm + "km");

        tvEtaToPickup.setText("To Pickup Location: " + 0 + "min");
        tvEtaToDest.setText("To Pickup Location: " + strTimeTaken + "min");
        tvEtaTotal.setText("Total: " + strTimeTaken + "min");

        flowValue = 2;
        layoutChanges();
    }


    /*  Show list of near driver of selected service type*/
    private void displayScreenChooseDriver(){

        if (mListDriverInfo != null) {

            mProviderListAdapter = new ProviderListAdapter(mContext, mListDriverInfo,
                    strEmail -> {

                        sendRequestRide(strEmail);
                        displayScreenFindingRide();

                    },
                    position -> {

                        // Get Reviews ???
                        getProviderDetail(mListDriverInfo.get(position).getEmail(), position);

                    });

            lvProvider.setAdapter(mProviderListAdapter);
            lvProvider.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Log.i("When_Clicked", "Enter");
                }
            });

        }

        flowValue = 12;
        layoutChanges();
    }

    private void getProviderDetail(String providerEmail, int position) {

        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        JsonObject gsonObject = new JsonObject();
        try {
            JSONObject paramObject = new JSONObject();

            paramObject.put("email", providerEmail);

            Log.i("provider_email", providerEmail);

            JsonParser jsonParser = new JsonParser();
            gsonObject = (JsonObject) jsonParser.parse(paramObject.toString());

            apiInterface.doFinishedRides2(gsonObject).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        JSONObject data = object.getJSONObject("data");

                        if (object.getString("status").equals("1")) {

                            try {

                                JSONArray rides = data.getJSONArray("rides");

                                List<YourTrips> driverFinishedRides = new ArrayList<>();

                                //  Receive the reponse data from server.
                                driverFinishedRides = new Gson().fromJson(rides.toString(), new TypeToken<List<YourTrips>>() {
                                }.getType());

                                displayScreenProviderDetail(mListDriverInfo.get(position), driverFinishedRides);

                            } catch (Exception e) {
                                Log.e("HERE", e.getMessage());
                            }

                        } else {

                            Utils.displayMessage(getActivity(), Utils.parseErrorMessage(data));
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Utils.displayMessage(getActivity(), getString(R.string.server_connect_error));
                    }

                    progressDialog.dismiss();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    progressDialog.dismiss();
                    t.printStackTrace();
                    Utils.displayMessage(getActivity(), getString(R.string.server_connect_error));
                }
            });
        } catch(Exception e){
            e.printStackTrace();
        }

    }

    private void displayScreenProviderDetail(DriverInfo driverInfo, List<YourTrips> driverFinishedRides){

        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_driver_detail, null);

        TextView tvDriverName = view.findViewById(R.id.tv_driver_name);
        TextView tvGender = view.findViewById(R.id.tv_driver_gender);
        TextView tvCarModel = view.findViewById(R.id.tv_car_model);
        TextView tvCarNumber = view.findViewById(R.id.tv_car_number);
        RatingBar ratingBarDriver = view.findViewById(R.id.rb_driver_rating);
        RecyclerView rcvCarImage = view.findViewById(R.id.recycler_car_image);
        RecyclerView rcvReview = view.findViewById(R.id.recycler_driver_review);

        Button btnClose = view.findViewById(R.id.btn_close);

        tvDriverName.setText(driverInfo.getFirstName() + " " + driverInfo.getLastName());
        tvGender.setText(driverInfo.getGender() == 2 ? "Female" : "Male");
        tvCarModel.setText(driverInfo.getCarModel() == null ? "" : driverInfo.getCarModel());
        tvCarNumber.setText(driverInfo.getCarNumber() == null ? "" : driverInfo.getCarNumber());
        ratingBarDriver.setRating(driverInfo.getAvgRating());

        mDriverReviewsAdapter = new DriverReviewsAdapter(driverFinishedRides);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        rcvReview.setLayoutManager(mLayoutManager);
        rcvReview.setItemAnimator(new DefaultItemAnimator());
        rcvReview.setAdapter(mDriverReviewsAdapter);

        mCarImageViewAdapter = new CarImageViewAdapter(driverInfo.getCarImageList());
        RecyclerView.LayoutManager mLayoutManager1 = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        rcvCarImage.setLayoutManager(mLayoutManager1);
        rcvCarImage.setItemAnimator(new DefaultItemAnimator());
        rcvCarImage.setAdapter(mCarImageViewAdapter);

        builder.setView(view)
                .setCancelable(true);

        dlgDetail = builder.create();
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlgDetail.dismiss();
            }
        });

        dlgDetail.show();
    }

    private void displayScreenFindingRide() {

        flowValue = 3;
        layoutChanges();
        //displayFirstScreen();



    }

    /*  Show screen about driver accepted. After this, driver is coming to your location.*/
    private void displayScreenAcceptedDriver() {

        imgSos.setVisibility(View.VISIBLE);

        lblProvider.setText(mAcceptedDriverInfo.getFirstName() + " " + mAcceptedDriverInfo.getLastName());
//        if (mAcceptedDriverInfo.getAvatar().startsWith("http"))
//            Picasso.with(mContext).load(mAcceptedDriverInfo.getAvatar()).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(imgProvider);
//        else
//            Picasso.with(mContext).load(GlobalConstants.SERVER_HTTP_URL + "/storage/driver/avatar/" + mAcceptedDriverInfo.getAvatar()).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(imgProvider);

        Log.i("acceptedDriver", "Boldman------->Avatar : " + mAcceptedDriverInfo.getAvatar());

        lblServiceRequested.setText("Service Type");
        lblModelNumber.setText(mAcceptedDriverInfo.getCarModel() + "\n" + mAcceptedDriverInfo.getCarNumber());

        Picasso.with(mContext).load("aa")
                .placeholder(R.drawable.car_select).error(R.drawable.car_select)
                .into(imgServiceRequested);
        try {
            ratingProvider.setRating(Float.parseFloat(mAcceptedDriverInfo.getDriverAvgRating()));
        } catch (Exception e){
            ratingProvider.setVisibility(View.INVISIBLE);
        }
        //lnrAfterAcceptedStatus.setVisibility(View.GONE);
        lblStatus.setText(mContext.getResources().getString(R.string.arriving));
        lnrOTP.setVisibility(View.VISIBLE);
        lblOTP.setText(mContext.getResources().getString(R.string.otp) + " " + mAcceptedDriverInfo.getRideCode());
        AfterAcceptButtonLayout.setVisibility(View.VISIBLE);
        btnCancelTrip.setText(mContext.getResources().getString(R.string.cancel_trip));
        show(lnrProviderAccepted);

        flowValue = 4;
        layoutChanges();

        btnCancelTrip.setVisibility(View.VISIBLE);
    }

    /*  Show screen about driver arrived.*/
    private void displayScreenArrivedDriver() {

        lblProvider.setText(mAcceptedDriverInfo.getFirstName() + " " + mAcceptedDriverInfo.getLastName());
//        if (mAcceptedDriverInfo.getAvatar().startsWith("http"))
//            Picasso.with(mContext).load(mAcceptedDriverInfo.getAvatar()).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(imgProvider);
//        else
//            Picasso.with(mContext).load(GlobalConstants.SERVER_HTTP_URL + "/storage/driver/avatar/" + mAcceptedDriverInfo.getAvatar()).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(imgProvider);

        lblServiceRequested.setText("Service Type");
        lblModelNumber.setText(mAcceptedDriverInfo.getCarModel() + "\n" + mAcceptedDriverInfo.getCarNumber());
        Picasso.with(mContext).load("aa")
                .placeholder(R.drawable.car_select).error(R.drawable.car_select)
                .into(imgServiceRequested);
        try {
            ratingProvider.setRating(Float.parseFloat(mAcceptedDriverInfo.getDriverAvgRating()));
        } catch (Exception e){
            ratingProvider.setVisibility(View.INVISIBLE);
        }
        //lnrAfterAcceptedStatus.setVisibility(View.GONE);
        lblStatus.setText(mContext.getResources().getString(R.string.arrived));
        lnrOTP.setVisibility(View.VISIBLE);
        lblOTP.setText(mContext.getResources().getString(R.string.otp) + " " + mAcceptedDriverInfo.getRideCode());
        AfterAcceptButtonLayout.setVisibility(View.VISIBLE);
        btnCancelTrip.setText(mContext.getResources().getString(R.string.cancel_trip));
        show(lnrProviderAccepted);

        flowValue = 4;
        layoutChanges();

        imgSos.setVisibility(View.VISIBLE);

        btnCancelTrip.setVisibility(View.VISIBLE);
    }

    /*  Show screen for riding.(On the way)*/
    private void displayScreenStartRide() {

        lblProvider.setText(mAcceptedDriverInfo.getFirstName() + " " + mAcceptedDriverInfo.getLastName());
//        if (mAcceptedDriverInfo.getAvatar().startsWith("http"))
//            Picasso.with(mContext).load(mAcceptedDriverInfo.getAvatar()).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(imgProvider);
//        else
//            Picasso.with(mContext).load(GlobalConstants.SERVER_HTTP_URL + "/storage/driver/avatar/" + mAcceptedDriverInfo.getAvatar()).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(imgProvider);

        lblServiceRequested.setText("Service Type");
        lblModelNumber.setText(mAcceptedDriverInfo.getCarModel() + "\n" + mAcceptedDriverInfo.getCarNumber());
        Picasso.with(mContext).load("aa")
                .placeholder(R.drawable.car_select).error(R.drawable.car_select)
                .into(imgServiceRequested);

        try {
            ratingProvider.setRating(Float.parseFloat(mAcceptedDriverInfo.getDriverAvgRating()));
        } catch (Exception e){
            ratingProvider.setVisibility(View.INVISIBLE);
        }

        //lnrAfterAcceptedStatus.setVisibility(View.GONE);
        lblStatus.setText(mContext.getResources().getString(R.string.picked_up));
        lnrOTP.setVisibility(View.VISIBLE);
        lblOTP.setText(mContext.getResources().getString(R.string.otp) + " " + mAcceptedDriverInfo.getRideCode());
        AfterAcceptButtonLayout.setVisibility(View.VISIBLE);
        btnCancelTrip.setText(mContext.getResources().getString(R.string.cancel_trip));
        show(lnrProviderAccepted);

        flowValue = 4;
        layoutChanges();

        imgSos.setVisibility(View.VISIBLE);

        btnCancelTrip.setVisibility(View.VISIBLE);
    }

    /*  Show pay screen*/
    private void displayScreenToPay() {

        once = true;
        strTag = "";

        try{

            flowValue = 5;
            layoutChanges();

            booking_id.setText(mRideId + "");

            if (mRequestUserPay != null) {
                lblDistanceCovered.setText(Utils.roundTwoDecimals(mRequestUserPay.getDistance() / 1000) + " Km");
                lblTimeTaken.setText(Utils.roundTwoDecimals(mRequestUserPay.getDuration() / 60) + " Min");
            } else {
                lblDistanceCovered.setText(Utils.roundTwoDecimals(Double.valueOf(mDistanceKm) / 1000) + " Km");
                lblTimeTaken.setText(Utils.roundTwoDecimals( Double.valueOf(strTimeTaken) / 60) + " Min");
            }

            mCouponCode = "";
            calculatePrices();

        } catch (Exception e) {
            e.printStackTrace();
        }

        imgSos.setVisibility(View.VISIBLE);
    }

    /*  Show rate screen for driver*/
    private void displayScreenToRate(){

        flowValue = 6;
        layoutChanges();
    }

    private void calculatePrices(){

        if (mCouponCode.equalsIgnoreCase("")){  // No Coupon

            if (mRequestUserPay != null){
                mBasePrice = Utils.roundTwoDecimals(mRequestUserPay.getBase_fee());
                mEstimatedFair = Utils.roundTwoDecimals(mRequestUserPay.getDistance_fee());
            }

            mSubTotal = mBasePrice + mEstimatedFair;
            mTaxPrice = Utils.roundTwoDecimals(mSubTotal * GlobalConstants.VAT_FEE / 100);
            mTotalAmountToPay = Utils.roundTwoDecimals(mSubTotal + mTaxPrice);

            discountDetectionLayout.setVisibility(View.GONE);
            subtotal2Layout.setVisibility(View.GONE);

            lblBasePrice.setText("$" + mBasePrice);
            lblDistancePrice.setText("$" + mEstimatedFair);
            ((TextView) rootView.findViewById(R.id.lblSubtotal)).setText("$" + mSubTotal);
            ((TextView) rootView.findViewById(R.id.lblTaxTag)).setText("VAT (" + GlobalConstants.VAT_FEE + "%)");
            lblTaxPrice.setText("$" + mTaxPrice);
            lblTotalPrice.setText("$" + mTotalAmountToPay);

        } else {                                            // Coupon Applied

            double discountAmount = 0;

            mSubTotal = mBasePrice + mEstimatedFair;

            if (mCouponDiscountType.equalsIgnoreCase("percent"))
                discountAmount = Utils.roundTwoDecimals(mSubTotal * Double.valueOf(mCouponDiscount) / 100);
            else
                discountAmount = Double.valueOf(mCouponDiscount);

            mSubTotal2 = Utils.roundTwoDecimals(mSubTotal - discountAmount);
            mTaxPrice = Utils.roundTwoDecimals(mSubTotal2 * GlobalConstants.VAT_FEE / 100);
            mTotalAmountToPay = Utils.roundTwoDecimals(mSubTotal2 + mTaxPrice);

            discountDetectionLayout.setVisibility(View.VISIBLE);
            subtotal2Layout.setVisibility(View.VISIBLE);

            lblBasePrice.setText("$" + mBasePrice);
            lblDistancePrice.setText("$" + mEstimatedFair);
            ((TextView) rootView.findViewById(R.id.lblSubtotal)).setText("$" + mSubTotal);
            lblDiscountPrice.setText("$" + discountAmount);
            ((TextView) rootView.findViewById(R.id.lblSubtotal2)).setText("$" + mSubTotal2);
            ((TextView) rootView.findViewById(R.id.lblTaxTag)).setText("VAT (" + GlobalConstants.VAT_FEE + "%)");
            lblTaxPrice.setText("$" + mTaxPrice);
            lblTotalPrice.setText("$" + mTotalAmountToPay);
        }
    }

    /*  Set list of near driver.*/
    private void setRideDriver(DriverInfo drvInfo){

//        int cntOld = 0;
//        int i = 0, j = 0;
//        boolean isExist = false;

//        if (mListNearDriver != null)
//            cntOld = mListNearDriver.size();

//        Log.i("setRideDriver", "Boldman----> ride_drvInfo.email : " + drvInfo.getEmail());
//        Log.i("setRideDriver", "Boldman----> cntOld : " + cntOld);

        String timestamp = String.valueOf(System.currentTimeMillis());

        try {
            for (Map<String, Object> mapObj : mListNearDriver) {
                if (!((DriverInfo) mapObj.get("info")).getEmail().equals(drvInfo.getEmail())) {
                    ((Marker) mapObj.get("marker")).remove();
                    mListNearDriver.remove(mapObj);
                } else {
                    mapObj.put("last_latitude", ((DriverInfo) mapObj.get("info")).getLatitude());
                    mapObj.put("last_longitude", ((DriverInfo) mapObj.get("info")).getLongitude());
                    mapObj.put("last_timestamp", timestamp);
                    mapObj.put("info", drvInfo);
                    mapObj.put("flag", true);
                }
            }

            if (mListNearDriver.size() == 0) {
                Map<String, Object> mapObj = new HashMap<>();
                ;
                mapObj.put("last_latitude", drvInfo.getLatitude());
                mapObj.put("last_longitude", drvInfo.getLongitude());
                mapObj.put("last_timestamp", timestamp);
                mapObj.put("info", drvInfo);
                mapObj.put("flag", true);

                mListNearDriver.add(mapObj);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /*  Set list of near driver.*/
    private void setDriverList(List<DriverInfo> listDriverInfo){

        int cntReceived = listDriverInfo.size();
        int cntOld = 0;
        int i = 0, j = 0;
        boolean isExist = false;

        if (mListNearDriver != null)
            cntOld = mListNearDriver.size();
//        Log.i("setDriverList", "Boldman----> cntReceived : " + cntReceived);
//        Log.i("setDriverList", "Boldman----> cntOld : " + cntOld);

        for (i = 0; i < cntOld; i ++)
            mListNearDriver.get(i).put("flag", false);

        for (i = 0; i < cntReceived; i ++) {

            DriverInfo drvInfo = listDriverInfo.get(i);

            isExist = false;

            for (j = 0; j < cntOld; j++) {

                if (drvInfo.getEmail().equals(((DriverInfo) mListNearDriver.get(j).get("info")).getEmail())) {

                    mListNearDriver.get(j).put("last_latitude", ((DriverInfo) mListNearDriver.get(j).get("info")).getLatitude());
                    mListNearDriver.get(j).put("last_longitude", ((DriverInfo) mListNearDriver.get(j).get("info")).getLongitude());
                    mListNearDriver.get(j).put("info", drvInfo);
                    mListNearDriver.get(j).put("flag", true);

                    isExist = true;
                    break;
                }
            }

            if (!isExist) {
                Map<String, Object> mapObj = new HashMap<String, Object>();

                mapObj.put("info", drvInfo);
                mapObj.put("last_latitude", drvInfo.getLatitude());
                mapObj.put("last_longitude", drvInfo.getLongitude());
                mapObj.put("flag", true);

                mListNearDriver.add(mapObj);
            }
        }

        for (Map<String, Object> mapObj : mListNearDriver){
            if (!((boolean)mapObj.get("flag"))) {
                ((Marker)mapObj.get("marker")).remove();
                mListNearDriver.remove(mapObj);
            }
        }
    }

    /*  This shows drivers near you by markers.*/
    private void showNearDrivers() {

        long currentTimestamp = System.currentTimeMillis();

        int i = 0, j = 0;
        int driverCnt = mListNearDriver.size();

        for (i = 0; i < driverCnt; i++){

            DriverInfo drvInfo = (DriverInfo) mListNearDriver.get(i).get("info");

            if (mListNearDriver.get(i).get("marker") == null){

                Marker marker;

                //Place current location marker
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(new LatLng(drvInfo.getLatitude(), drvInfo.getLongitude()));
                markerOptions.anchor(0.5f, 0.5f);
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.provider_location_icon));

                try {
                    for (ServiceType serviceType : mListServiceType) {
                        if (serviceType.getId() == drvInfo.getServiceType()) {
                            markerOptions.title(serviceType.getName());
                            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(mListCarIcon.get(serviceType.getId())));
                            break;
                        }
                    }
                    marker = mMap.addMarker(markerOptions);
                } catch (Exception e){
                    e.printStackTrace();
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.provider_location_icon));
                    marker = mMap.addMarker(markerOptions);
                }

                mListNearDriver.get(i).put("marker", marker);

            }else{

                Marker driverMarker = ((Marker)mListNearDriver.get(i).get("marker"));
                LatLng startPosition = new LatLng(
                        (double)mListNearDriver.get(i).get("last_latitude"),
                        (double)mListNearDriver.get(i).get("last_longitude"));
                LatLng finalPosition = new LatLng(drvInfo.getLatitude(), drvInfo.getLongitude());
                //float oldAngle = (float)(driverMarker.getRotation());
                float nowAngle = (float) drvInfo.getAngle();

                long lastTimestamp = 0;

                if (mListNearDriver.get(i).get("last_timestamp") != null)
                    lastTimestamp = Long.valueOf((String)mListNearDriver.get(i).get("last_timestamp"));

                mListNearDriver.get(i).put("last_timestamp", String.valueOf(currentTimestamp));

                long time = currentTimestamp - lastTimestamp;

                Log.i("Moving Car Pos", "Boldman---------> time" + time + " angle ---> " + nowAngle);

                moveCar(driverMarker, finalPosition, time, nowAngle);
            }
        }
    }

    /*  Show car moving*/
    private void moveCar(final Marker driverMarker, final LatLng finalPosition, final long time, final float angle) {

        Log.i("MovingCarTime" , String.valueOf(time));

        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final Interpolator interpolator = new AccelerateDecelerateInterpolator();
        final float durationInMs = 1000;
        final LatLng startPosition = driverMarker.getPosition();
        final float startRotation = driverMarker.getRotation();

        handler.post(new Runnable() {
            long elapsed;
            float t;
            float v;
            @Override
            public void run() {
                // Calculate progress using interpolator
                elapsed = SystemClock.uptimeMillis() - start;
                t = elapsed / durationInMs;
                v = interpolator.getInterpolation(t);

                driverMarker.setPosition(new LatLng(startPosition.latitude * (1 - t) + (finalPosition.latitude) * t,
                        startPosition.longitude * (1 - t) + (finalPosition.longitude) * t));

                float rot = v * angle + (1 - v) * startRotation;

                driverMarker.setRotation(rot);

                // Repeat till progress is complete.
                if (t < 1) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                }
            }
        });
    }

    @Override
    public boolean processMessage(QBChatDialog dlg, String dialogId, QBChatMessage message, Integer senderId) {
        return false;
    }

    @Override
    public boolean processError(QBChatDialog dlg, String dialogId, QBChatException exception, QBChatMessage message, Integer senderId) {
        return false;
    }

    public interface LatLngInterpolator {
        LatLng interpolate(float fraction, LatLng a, LatLng b);
        class Spherical implements LatLngInterpolator {
            /* From github.com/googlemaps/android-maps-utils */
            @Override
            public LatLng interpolate(float fraction, LatLng from, LatLng to) {
                // http://en.wikipedia.org/wiki/Slerp
                double fromLat = toRadians(from.latitude);
                double fromLng = toRadians(from.longitude);
                double toLat = toRadians(to.latitude);
                double toLng = toRadians(to.longitude);
                double cosFromLat = cos(fromLat);
                double cosToLat = cos(toLat);
                // Computes Spherical interpolation coefficients.
                double angle = computeAngleBetween(fromLat, fromLng, toLat, toLng);
                double sinAngle = sin(angle);
                if (sinAngle < 1E-6) {
                    return from;
                }
                double a = sin((1 - fraction) * angle) / sinAngle;
                double b = sin(fraction * angle) / sinAngle;
                // Converts from polar to vector and interpolate.
                double x = a * cosFromLat * cos(fromLng) + b * cosToLat * cos(toLng);
                double y = a * cosFromLat * sin(fromLng) + b * cosToLat * sin(toLng);
                double z = a * sin(fromLat) + b * sin(toLat);
                // Converts interpolated vector back to polar.
                double lat = atan2(z, sqrt(x * x + y * y));
                double lng = atan2(y, x);
                return new LatLng(toDegrees(lat), toDegrees(lng));
            }

            private double computeAngleBetween(double fromLat, double fromLng, double toLat, double toLng) {
                // Haversine's formula
                double dLat = fromLat - toLat;
                double dLng = fromLng - toLng;
                return 2 * asin(sqrt(pow(sin(dLat / 2), 2) +
                        cos(fromLat) * cos(toLat) * pow(sin(dLng / 2), 2)));
            }
        }
    }

    public void rotateMarker(final Marker marker, final float toRotation, final float st) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final float startRotation = st;
        final long duration = 1555;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed / duration);

                float rot = t * toRotation + (1 - t) * startRotation;

                marker.setRotation(-rot > 180 ? rot / 2 : rot);
                //start_rotation = -rot > 180 ? rot / 2 : rot;
                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                }
            }
        });
    }

    /*  Add driver marker.*/
    private Marker addDriverMarker(DriverInfo driverInfo) {

        Marker marker;
        String strTime = String.format("%.6f", System.currentTimeMillis() / 1000.0);

        //Place current location marker
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(new LatLng(driverInfo.getLatitude(), driverInfo.getLongitude()));
        markerOptions.title("Driver");
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.provider_location_icon));
        marker = mMap.addMarker(markerOptions);

        Log.i("DriverTimeStamp", "--------> driver: " + driverInfo.getDriver_time());
        Log.i("UserTimeStamp", "--------> user: " + strTime);

        return marker;
    }

    /*  Starting fragment, init the map*/
    private void initMap() {

        if (mMap == null) {
            SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }

        if (mMap != null) {
            setupMap();
        }
    }

    void setupMap() {

//            mMap.setMyLocationEnabled(true);
//            mMap.getUiSettings().setMyLocationButtonEnabled(true);

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            return;
        }

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setCompassEnabled(false);
        //mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setBuildingsEnabled(true);
        mMap.setOnMarkerDragListener(this);
        mMap.setOnCameraMoveListener(this);
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        mMap.getUiSettings().setTiltGesturesEnabled(false);
        mMap.setOnPoiClickListener(this);

        View mMapView = this.getView();

        if (mMapView != null &&
                mMapView.findViewById(Integer.parseInt("1")) != null) {
            // Get the button view
            View locationButton = ((View) mMapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            // and next place it, on bottom right (as Google Maps app)
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                    locationButton.getLayoutParams();
            // position on right bottom
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 250, 20, 0);
        }

        //Marker click
//            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
//                @Override
//                public View getInfoWindow(Marker marker) {
//                    // Getting view from the layout file infowindowlayout.xml
//                    View v = mActivity.getLayoutInflater().inflate(R.layout.info_window, null);
//
//                    TextView lblAddress =  v.findViewById(R.id.lblAddress);
//                    TextView lblTime = v.findViewById(R.id.txtTime);
//
//                    lblAddress.setText(marker.getSnippet());
//
//                    if (strTimeTaken.length() > 0) {
//                        lblTime.setText(strTimeTaken);
//                    }
//
//                    if (marker.getTitle() == null) {
//                        return null;
//                    }
//                    if (marker.getTitle().equalsIgnoreCase("Source") || marker.getTitle().equalsIgnoreCase("Destination")) {
//                        return v;
//                    } else {
//                        return null;
//                    }
//                }
//                @Override
//                public View getInfoContents(Marker marker) {
//                    return null;
//                }
//            });

    }

    /////////////////////////////////////// OnMapReadyCallback ////////////////////////////////

    @Override
    public void onMapReady(GoogleMap googleMap) {

//        buildGoogleApiClient();
//
//        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
//            return;
//        }
//
//        mMap.setMyLocationEnabled(true);
//        mMap.getUiSettings().setMyLocationButtonEnabled(true);
//        //mMap.setTrafficEnabled(true);
//
//        mMap.getUiSettings().setCompassEnabled(false);
//        //mMap.getUiSettings().setMyLocationButtonEnabled(true);
//        mMap.setBuildingsEnabled(true);
//        mMap.setOnMarkerDragListener(this);
//        mMap.setOnCameraMoveListener(this);
//        mMap.getUiSettings().setRotateGesturesEnabled(false);
//        mMap.getUiSettings().setTiltGesturesEnabled(false);
//        mMap.setOnPoiClickListener(this);


        mMap = googleMap;

        setupMap();

        Log.i("onMapReady_", "aaa");

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                return;
            }
        }

        buildGoogleApiClient();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        mGoogleApiClient.connect();
    }

////////////////////////////////////////  Fragment   //////////////////////////////////////////

    @Override
    public void onAttach(Context context) {

        super.onAttach(context);
        this.mContext = context;
        GlobalEvent.getBus().register(this);
        try {
            mListener = (OnFragmentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mActivity = activity;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceivedMessage(MessageEvent.ReceivedMessage event) {

        int cnt = event.state;

        if (cnt > 0){
            tvUnreadMsgCnt.setVisibility(View.VISIBLE);
            tvUnreadMsgCnt.setText(cnt + "");
        } else{
            tvUnreadMsgCnt.setVisibility(View.INVISIBLE);
        }
    }

//////////////////////////////////////// MarkerDragListener  //////////////////////////////////


    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

    }

////////////////////////////////////// CameraMoveListener  ///////////////////////////////////

    public void onCameraMove() {

        cmPosition = mMap.getCameraPosition();
    }

///////////////////////////////////// POI ClickListener /////////////////////////////////////

    @Override
    public void onPoiClick(PointOfInterest poi) {

//        Toast.makeText(getActivity(), "Clicked: " +
//                        poi.name + "\nPlace ID:" + poi.placeId +
//                        "\nLatitude:" + poi.latLng.latitude +
//                        " Longitude:" + poi.latLng.longitude,
//                Toast.LENGTH_SHORT).show();

        if (mStateDraggable == 1) {

//            mMap.clear();
//            stopAnim();

            //  defalut : 1
            mClickedPOI = poi;

            mDstLatLng = new LatLng(poi.latLng.latitude, poi.latLng.longitude);

            if (mDstMarker != null) {
                mDstMarker.remove();
            }

            //  Place destination location marker
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(mDstLatLng);
            markerOptions.title("Destination");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            mDstMarker = mMap.addMarker(markerOptions);

            //  Place user(source) location marker
            mSrcLatLng = new LatLng(currentUserInfo.getLatitude(), currentUserInfo.getLongitude());

            addSrcMarker(mSrcLatLng);

            if (currentUserInfo == null) {
                mDstMarker.remove();
                Utils.displayMessage(getActivity(), "There's a problem with your location. Please check it!");
            } else {

                if (mSocket.connected()) {

                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    builder.include(mSrcMarker.getPosition());
                    builder.include(mDstMarker.getPosition());
                    LatLngBounds bounds = builder.build();
                    int padding = 300; // offset from edges of the map in pixels
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                    mMap.moveCamera(cu);

                    tVQuestion.setText("Would you like to go to " + mClickedPOI.name + "?");
                    flowValue = 11;
                    layoutChanges();
                } else {

                    Utils.displayMessage(getActivity(), "There's a problem with socket connection.");
                }
            }

            current_latitude = mSrcLatLng.latitude;
            current_longitude = mSrcLatLng.longitude;

            source_lat = "" + current_latitude;
            source_lng = "" + current_longitude;

            dest_lat = "" + mDstLatLng.latitude;
            dest_lng = "" + mDstLatLng.longitude;

//            goToScreenSelectAddress(mContext, source_lat, source_lng);
//
//            dest_address = getAddressUsingLatLng_("destination", frmDest, mContext, dest_lat, dest_lng);
        }
    }

    void goToScreenSelectAddress(final Context context, String latitude, String longitude) {

        mApiInterface = ApiClient.getGoogleMapClient().create(ApiInterface.class);

        Call<ResponseBody> call = mApiInterface.getGoogleMapResponse(latitude + "," + longitude,
                context.getResources().getString(R.string.google_map_api));

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.i("getAddressUseLatLng", "Boldman------>SUCESS" + response.body());
                Log.i("getAddressUseLatLng", "Boldman------>URL GET" + call.request().url().toString());
                if (response.body() != null) {
                    try {
                        String bodyString = new String(response.body().bytes());
                        Log.i("getAddressUseLatLng", "Boldman------>bodyString" + bodyString);
                        try {
                            JSONObject jsonObj = new JSONObject(bodyString);
                            JSONArray jsonArray = jsonObj.getJSONArray("results");
                            if (jsonArray.length() > 0) {

                                String currentAddress = jsonArray.getJSONObject(0).getString("formatted_address");
                                source_address = currentAddress;
                                current_address = currentAddress;
                                frmSource.setText("" + current_address);
                                SharedHelper.putKey(context, "source", "" + current_address);

                                Intent intent2 = new Intent(getActivity(), GooglePlaceSearchActivity.class);
                                intent2.putExtra("cursor", "destination");
                                intent2.putExtra("s_address", frmSource.getText().toString());
                                intent2.putExtra("d_address", frmDest.getText().toString());
                                startActivityForResult(intent2, PLACE_AUTOCOMPLETE_REQUEST_CODE_DEST);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Utils.displayMessage(mActivity, getString(R.string.problem_google_map_response));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Utils.displayMessage(mActivity, getString(R.string.problem_google_map_response));
                    }
                } else {
                    Utils.displayMessage(mActivity, getString(R.string.problem_google_map_connection));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("onFailure", "onFailure" + call.request().url());
                Utils.displayMessage(mActivity, getString(R.string.problem_google_map_connection));
            }
        });
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();


            parserTask.execute(result);

        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                routes = parser.parse(jObject);

                strTimeTaken = parser.getEstimatedTime();
                mTimeSeconds = parser.getTimeSeconds();
                mDistanceKm = parser.getDistanceKmText();
                mDistanceMeter = parser.getDistanceMeter();
                mStartAddress = parser.getStartAddress();
                mEndAddress = parser.getEndAddress();

                lblTotalDistance.setText(mDistanceKm);
                lblEta.setText(strTimeTaken);

                tvDistanceToDest.setText("To Pickup Location: " + mDistanceKm + "km");
                tvDistanceTotal.setText("Total: " + mDistanceKm + "km");

                tvEtaToDest.setText("To Pickup Location: " + strTimeTaken + "min");
                tvEtaTotal.setText("Total: " + strTimeTaken + "min");

                SharedHelper.putKey(mContext, "distance", mDistanceKm);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
//            ArrayList points = null;
//            PolylineOptions lineOptions = null;
//            MarkerOptions markerOptions = new MarkerOptions();
//
//            if (result != null) {
//                for (int i = 0; i < result.size(); i++) {
//                    points = new ArrayList();
//                    lineOptions = new PolylineOptions();
//
//                    List<HashMap<String, String>> path = result.get(i);
//
//                    for (int j = 0; j < path.size(); j++) {
//                        HashMap<String, String> point = path.get(j);
//
//                        double lat = Double.parseDouble(point.get("lat"));
//                        double lng = Double.parseDouble(point.get("lng"));
//                        LatLng position = new LatLng(lat, lng);
//
//                        points.add(position);
//                    }
//
//                    lineOptions.addAll(points);
//                    lineOptions.width(5);
//                    lineOptions.color(Color.TRANSPARENT);
//                    lineOptions.geodesic(true);
//
//                }
//
//                // Drawing polyline in the Google Map for the i-th route
//                if (lineOptions != null)
//                    mMap.addPolyline(lineOptions);
//                else
//                    Utils.displayMessage(getActivity(), getString(R.string.problem_google_map_response));
//
//                if (flowValue != 0) {
//                    if (lineOptions != null && points != null) {
//                        //mMap.addPolyline(lineOptions);
//                        startAnim(points);
//                    } else {
//                        Log.d("onPostExecute", "without Polylines drawn");
//                    }
//                }
//
//                distanceText.setText(mDistanceKm);
//            }else{
//                Utils.displayMessage(getActivity(), getString(R.string.problem_google_map_connection));
//            }
        }
    }

    private void startAnim(ArrayList<LatLng> routeList) {
//        if (mMap != null && routeList.size() > 1) {
//            MapAnimator.getInstance().animateRoute(mContext, mMap, routeList);
//        }
    }

    private void stopAnim() {
//        if (mMap != null) {
//            MapAnimator.getInstance().stopAnim();
//        }
    }


    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;

        // Output format
        String output = "json";

        // Building the url to the web service (Replace API key!!!!!!!!!!!!!!!!!!!!!!)
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_map_api);

        return url;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    /* Start count waiting time in the screen of finding ride */
    private void startCountWaitingTime() {

        mTimeWaiting = 60;

        mHandlerWaiting = new Handler();

        mRunnableWaiting = new Runnable() {

            @Override
            public void run() {

                if (mTimeWaiting >= 0){

                    tvWaitingTime.setText(mTimeWaiting + " s");
                    mTimeWaiting --;
                } else{

                    if (mSocket.connected()) {
                        sendCancelRide("");
                        displayFirstScreen();
                    } else{
                        Toast.makeText(mContext, "There's a problem with connection to server.", Toast.LENGTH_SHORT).show();
                    }

                    mHandlerWaiting.removeCallbacks(mRunnableWaiting);
                }

                mHandlerWaiting.postDelayed(mRunnableWaiting, 1000);
            }
        };

        mActivity.runOnUiThread(mRunnableWaiting);
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////

    public void getAndShowServiceList() {

        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        Log.i("getAndShowServiceList", "Boldman------>in getAndShowServiceList()");

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        apiInterface.doGetServiceType().enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {

                    JSONObject object = new JSONObject(response.body().string());
                    JSONObject data = object.getJSONObject("data");

                    if (object.getString("status").equals("1")) {

                        JSONArray service_type = data.getJSONArray("service_type");

                        //  Receive the reponse data from server.
                        mListServiceType = new Gson().fromJson(service_type.toString(), new TypeToken<List<ServiceType>>() {
                        }.getType());


                        for (ServiceType serviceType : mListServiceType) {
                            Thread thread = new Thread(new Runnable(){
                                @Override
                                public void run(){

                                        URL url;
                                        try {
                                            url = new URL(serviceType.getMarker_url());
                                            Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                            mListCarIcon.put(serviceType.getId(), bmp);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                            });
                            thread.start();
                        }

                    } else{

                        Utils.displayMessage(getActivity(), Utils.parseErrorMessage(data));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Utils.displayMessage(getActivity(), getString(R.string.server_connect_error));
                }

                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
                t.printStackTrace();
                Utils.displayMessage(getActivity(), getString(R.string.server_connect_error));
            }
        });

    }

    /*  Add marker in location indicated. state: first->1, not->0*/
    private void addSrcMarker(LatLng latLng) {

        if (mSrcMarker != null)
            mSrcMarker.remove();

        //Place current location marker
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        mSrcMarker = mMap.addMarker(markerOptions);
    }


///////////////////////////////////// gms LocationListener ////////////////////////////////////

    @Override
    public void onLocationChanged(Location location) {

        Log.i("onLocationChanged", currentUserInfo.getLatitude() + "");

        //  Save user's current location
        lastUserInfo = currentUserInfo;

        currentUserInfo.setLatitude(location.getLatitude());
        currentUserInfo.setLongitude(location.getLongitude());
        currentUserInfo.setTimestamp(System.currentTimeMillis());

        current_latitude = currentUserInfo.getLatitude();
        current_longitude = currentUserInfo.getLongitude();

        source_lat = "" + current_latitude;
        source_lng = "" + current_longitude;

        //  Place current location marker
        //addSrcMarker(latLng, 0);

        LatLng currentLatLng = new LatLng(currentUserInfo.getLatitude(), currentUserInfo.getLongitude());

//        if (lastUserInfo != null) {
//            LatLng lastLatLng = new LatLng(lastUserInfo.getLatitude(), lastUserInfo.getLongitude());
//            animateAddMarker(lastLatLng, currentLatLng);
//        }

//        if (lastUserInfo.getLatitude() == 0){
//        }

        if (flowValue == 1 && mApplyTilt == true){

            CameraPosition cameraPosition = new CameraPosition.Builder().
                    target(currentLatLng).
                    tilt(90).
                    zoom(15).
                    bearing(0).
                    build();

            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            mApplyTilt = true;
        } else if (flowValue == 0){
            CameraPosition cameraPosition = new CameraPosition.Builder().
                    target(currentLatLng).
                    tilt(0).
                    zoom(15).
                    bearing(0).
                    build();

            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        } else
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));

        //  Send socket request about updating user's location
        if (mSocket.connected())
            sendUserUpdateLocation();
        else {
//            Toast.makeText(mContext, getString(R.string.server_connect_error), Toast.LENGTH_SHORT).show();
            Log.i("sendUserUpdateLocation", "Boldman------->Connection Problem-------->");

            //connectToSocket();
        }
    }

    private void animateAddMarker(LatLng latLngLast, LatLng latLng) {


    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    //////////////////////////////////// ConnectionCallbacks  ///////////////////////////////

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    /////////////////////////////////// ConnectionFailedListener  ////////////////////////////
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    ///////////////////////////////////// Socket Request  ////////////////////////////////////


    /*  Starting fragment, Send the user info to server*/
    private void sendUserInfo() {

        try {
            JSONObject val = new JSONObject();
            val.put("user_type", 1);
            val.put("api_token", SharedHelper.getKey(mContext, "api_token"));

            emitJsonObject(GlobalConstants.URL_INIT_CONNECT, val);

            Log.i("User Info", "BOLDMAN-----------> ");
        } catch (JSONException e) {
            Log.e("HERE", e.getMessage());
        } catch (Exception e) {
            Log.e("HERE", e.getMessage());
        }
    }

    /*  Send user's locaiton.*/
    private void sendUserUpdateLocation() {

        String strTime = String.format("%.6f", System.currentTimeMillis() / 1000.0);

        try {
            JSONObject val = new JSONObject();
            //val.put("service_type", mServiceType);
            val.put("service_type", mServiceType);
            val.put("api_token", GlobalConstants.g_api_token);
            val.put("latitude", currentUserInfo.getLatitude());
            val.put("longitude", currentUserInfo.getLongitude());
            val.put("timestamp", strTime);
            val.put("ride_id", mRideId);

            emitJsonObject(GlobalConstants.URL_USER_LOCATION_UPDATED, val);

            //Log.i("AAAAAAAA", "BOLDMAN-----------> ");
        } catch (JSONException e) {
            Log.e("HERE", e.getMessage());
        } catch (Exception e) {
            Log.e("HERE", e.getMessage());
        }

    }

    /*  Send ride request.*/
    private void sendRequestRide(String strDriverEmail) {

        long currentTimeMillis = System.currentTimeMillis();
        String strTime = String.format("%.6f", currentTimeMillis / 1000.0);

        try {
            JSONObject val = new JSONObject();

            val.put("service_type", mServiceType);
//            val.put("service_type", mPosServiceType + 1);
            val.put("api_token", GlobalConstants.g_api_token);
            val.put("src_latitude", mSrcLatLng.latitude);
            val.put("src_longitude", mSrcLatLng.longitude);
            val.put("dst_latitude", mDstLatLng.latitude);
            val.put("dst_longitude", mDstLatLng.longitude);
            val.put("timestamp", strTime);
            val.put("s_address", mStartAddress);
            val.put("d_address", mEndAddress);

            String strPayMode = "";
            if (SharedHelper.getKey(mContext, "payment_mode").equalsIgnoreCase("wallet"))
                strPayMode = "wallet";
            else
                strPayMode = "cash";

            val.put("payment_method", strPayMode);
            val.put("distance", (double)mDistanceMeter / 1000);
            val.put("pay_amount", mEstimatedFair);

            /*  If schduledDate and scheduledTime are not empty, this is schedule request.*/
            if (scheduledDate != "" && scheduledTime != "") {
                Date date = null;
                long timeInMilliseconds = 0;
                date = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.ENGLISH).parse(scheduledDate + " " + scheduledTime);
                timeInMilliseconds = date.getTime();
                mBookingKey = String.valueOf(currentTimeMillis);

                val.put("book_at", String.format("%.6f", timeInMilliseconds / 1000.0));
                val.put("request_key", mBookingKey);

            } else{
                mRideKey = String.valueOf(currentTimeMillis);
                val.put("request_key", mRideKey);
            }

            if (strDriverEmail.equals("") || strDriverEmail == null){
                val.put("is_selected", 0);
            }else{
                val.put("is_selected", 1);
                val.put("driver_email", strDriverEmail);
            }

            emitJsonObject(GlobalConstants.URL_REQUEST_RIDE, val);

            Log.i("Request_ride", "BOLDMAN-----------> " + val.toString());
        } catch (JSONException e) {
            Log.e("HERE", e.getMessage());
        } catch (Exception e) {
            Log.e("HERE", e.getMessage());
        }

    }

    public void sendPayNow() {

        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        Log.i("getAndShowServiceList", "Boldman------>in getAndShowServiceList()");

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        JsonObject gsonObject = new JsonObject();
        try {
            JSONObject paramObject = new JSONObject();

            paramObject.put("api_token", SharedHelper.getKey(mContext, "api_token"));
            paramObject.put("email", mAcceptedDriverInfo.getEmail());
            paramObject.put("amount", mTotalAmountToPay);

            String strPayMode = "";
            if (SharedHelper.getKey(mContext, "payment_mode").equalsIgnoreCase("wallet"))
                strPayMode = "wallet";
            else
                strPayMode = "cash";

            paramObject.put("payment_method", strPayMode);
            paramObject.put("ride_id", mRideId);

            if (mCouponCode != null && !mCouponCode.equals(""))
                paramObject.put("coupon_code", mCouponCode);

            JsonParser jsonParser = new JsonParser();
            gsonObject = (JsonObject) jsonParser.parse(paramObject.toString());

            apiInterface.doPay(gsonObject).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        JSONObject data = object.getJSONObject("data");

                        if (object.getString("status").equals("1")) {

                            try {
                                String strTime = String.format("%.6f", System.currentTimeMillis() / 1000.0);

                                JSONObject val = new JSONObject();
                                val.put("api_token", GlobalConstants.g_api_token);
                                val.put("pay_amount", mTotalAmountToPay);
                                val.put("timestamp", strTime);
                                val.put("ride_id", mRideId);
                                val.put("coupon_code", "");
                                val.put("device_type", "android");
                                val.put("device_token", "boldman123");

                                emitJsonObject(GlobalConstants.URL_CONFIRM_USER_PAY, val);

                                Log.i("Confirm_User_Pay", "BOLDMAN-----------> ");
                            } catch (JSONException e) {
                                Log.e("HERE", e.getMessage());
                            } catch (Exception e) {
                                Log.e("HERE", e.getMessage());
                            }

                        } else {

                            Utils.displayMessage(getActivity(), Utils.parseErrorMessage(data));
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Utils.displayMessage(getActivity(), getString(R.string.server_connect_error));
                    }

                    progressDialog.dismiss();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    progressDialog.dismiss();
                    t.printStackTrace();
                    Utils.displayMessage(getActivity(), getString(R.string.server_connect_error));
                }
            });
        } catch(Exception e){
            e.printStackTrace();
        }

    }


    public void sendReview() {

        try {

//            txtCommentsRate.setFocusable(false);
//            ratingProviderRate.setClickable(false);

            String strTime = String.format("%.6f", System.currentTimeMillis() / 1000.0);

            JSONObject val = new JSONObject();
            val.put("api_token", GlobalConstants.g_api_token);
            val.put("memo", txtCommentsRate.getText().toString());
            val.put("point", feedBackRating);
            val.put("timestamp", strTime);
            val.put("ride_id", mRideId);

            emitJsonObject(GlobalConstants.URL_CONFIRM_USER_RATE, val);

            mRideId = 0;
            displayFirstScreen();

            Log.i("Confirm_User_Rate", "BOLDMAN-----------> ");
        } catch (JSONException e) {
            Log.e("HERE", e.getMessage());
        } catch (Exception e) {
            Log.e("HERE", e.getMessage());
        }

    }

    /*  Send agree to driver*/
    private void sendAgreeToDriver(String driverEmail) {

        if (mSocket.connected()) {

            String strTime = String.format("%.6f", System.currentTimeMillis() / 1000.0);

            mDriverEmail = driverEmail;

            try {
                JSONObject val = new JSONObject();
                val.put("api_token", GlobalConstants.g_api_token);
                val.put("driver_email", mDriverEmail);
                val.put("timestamp", strTime);

                emitJsonObject(GlobalConstants.URL_AGREE_TO_DRIVER, val);

                Log.i("agree_to_driver", "Boldman------> Agree to driver! ------>");

            } catch (JSONException e) {
                Log.e("HERE", e.getMessage());
            } catch (Exception e) {
                Log.e("HERE", e.getMessage());
            }
        }
    }

    /*  Send cancel ride*/
    private void sendCancelRide(String strReason) {

        mProgressDlg = new ProgressDialog(mContext);
        mProgressDlg.setTitle("Please wait..");
        mProgressDlg.setCancelable(false);
        if (mProgressDlg != null)
            mProgressDlg.show();

        String strTime = String.format("%.6f", System.currentTimeMillis() / 1000.0);

        try {
            JSONObject val = new JSONObject();
            val.put("api_token", GlobalConstants.g_api_token);
            val.put("timestamp", strTime);
            val.put("ride_id", mRideId);
            val.put("cancel_reason", strReason);

            emitJsonObject(GlobalConstants.URL_CANCEL_RIDE, val);

            Log.i("cancel_ride", "Boldman-------> Cancel ride! ------>");
        } catch (JSONException e) {
            Log.e("HERE", e.getMessage());
        } catch (Exception e) {
            Log.e("HERE", e.getMessage());
        }

        mProgressDlg.dismiss();
    }

    private void emitJsonObject(String url, JSONObject obj) {

        JsonObject gsonObject = new JsonObject();
        JsonParser jsonParser = new JsonParser();

        gsonObject = (JsonObject) jsonParser.parse(obj.toString());
        mSocket.emit(url, gsonObject);
    }

    //////////////////////////////////// Map ClickListener  //////////////////////////////////

//    @Override
//    public void onMapClick(LatLng latLng) {
//
////        Log.i("aaaaa", "lat : " + latLng.latitude + " lng : " + latLng.longitude + "--------");
////
////        if (mDstMarker != null)
////            mDstMarker.remove();
////
////        MarkerOptions destMarker = new MarkerOptions()
////                .position(latLng).title("destination").snippet("Destination")
////                .icon(BitmapDescriptorFactory.fromBitmap(MapUtils.createDestinationMarker(this,"Destination")));
////
////        mDstMarker = mMap.addSrcMarker(destMarker);
//
//
//
//    }

    void layoutChanges() {
        try {
            Utils.hideKeyboard(getActivity());

            if (lnrSetDest.getVisibility() == View.VISIBLE){
                lnrSetDest.startAnimation(slide_down);
            } else if (lnrApproximate.getVisibility() == View.VISIBLE) {
                lnrApproximate.startAnimation(slide_down);
            } else if (ScheduleLayout.getVisibility() == View.VISIBLE) {
                ScheduleLayout.startAnimation(slide_down);
            } else if (lnrSelectServiceType.getVisibility() == View.VISIBLE) {
                lnrSelectServiceType.startAnimation(slide_down);
            } else if (lnrProviderPopup.getVisibility() == View.VISIBLE) {
                lnrProviderPopup.startAnimation(slide_down);
                lnrSearchAnimation.startAnimation(slide_up_down);
                lnrSearchAnimation.setVisibility(View.VISIBLE);
            } else if (lnrChooseProviders.getVisibility() == View.VISIBLE){
                lnrChooseProviders.startAnimation(slide_down);
            } else if (lnrInvoice.getVisibility() == View.VISIBLE) {
                lnrInvoice.startAnimation(slide_down);
            } else if (lnrRateProvider.getVisibility() == View.VISIBLE) {
                lnrRateProvider.startAnimation(slide_down);
            } else if (lnrInvoice.getVisibility() == View.VISIBLE) {
                lnrInvoice.startAnimation(slide_down);
            }

            lnrSetDest.setVisibility(View.GONE);
            lnrSelectServiceType.setVisibility(View.GONE);
            lnrProviderPopup.setVisibility(View.GONE);
            lnrApproximate.setVisibility(View.GONE);
            lnrChooseProviders.setVisibility(View.GONE);
            lnrWaitingForProviders.setVisibility(View.GONE);
            lnrProviderAccepted.setVisibility(View.GONE);
            srcDestLayout.setVisibility(View.GONE);
            lnrInvoice.setVisibility(View.GONE);
            lnrRateProvider.setVisibility(View.GONE);
            lnrHomeWork.setVisibility(View.GONE);
            ScheduleLayout.setVisibility(View.GONE);
            rtlStaticMarker.setVisibility(View.GONE);
            frmDestination.setVisibility(View.GONE);
            imgBack.setVisibility(View.GONE);
            lytRouteFound.setVisibility(View.GONE);

            menuAndExploreLayout.setVisibility(View.GONE);

            imgSos.setVisibility(View.GONE);
            txtCommentsRate.setText("");
            scheduleDate.setText(mContext.getResources().getString(R.string.sample_date));
            scheduleTime.setText(mContext.getResources().getString(R.string.sample_time));

//            txtCommentsRate.setFocusable(true);
//            ratingProviderRate.setClickable(true);

            /*  First Screen*/
            if (flowValue == 0) {

                //imgSos.setVisibility(View.GONE);

                mStateDraggable = 1;
                menuAndExploreLayout.setVisibility(View.VISIBLE);
                explore.setVisibility(View.VISIBLE);
                menuButton.setVisibility(View.VISIBLE);
                srcDestLayout.setVisibility(View.GONE);
                txtChange.setVisibility(View.GONE);
//                frmSource.setOnClickListener(new OnClick());
//                frmDest.setOnClickListener(new OnClick());
                srcDestLayout.setOnClickListener(null);
                if (mMap != null) {
                    stopAnim();
                }

                if (!SharedHelper.getKey(mContext, "home").equalsIgnoreCase("")) {
                    lnrHome.setVisibility(View.VISIBLE);
                } else {
                    lnrHome.setVisibility(View.GONE);
                }
                if (!SharedHelper.getKey(mContext, "work").equalsIgnoreCase("")) {
                    lnrWork.setVisibility(View.VISIBLE);
                } else {
                    lnrWork.setVisibility(View.GONE);
                }
                if (lnrHome.getVisibility() == View.GONE && lnrWork.getVisibility() == View.GONE) {
                    lnrHomeWork.setVisibility(View.GONE);
                } else {
                    lnrHomeWork.setVisibility(View.VISIBLE);
                }

                frmDestination.setAnimation(slide_up);
                frmDestination.setVisibility(View.VISIBLE);

                destination.setText("");
                destination.setHint(mContext.getResources().getString(R.string.where_to_go));
                frmDest.setText("");
                dest_address = "";
                dest_lat = "";
                dest_lng = "";
                source_lat = "" + current_latitude;
                source_lng = "" + current_longitude;
                source_address = "" + current_address;
                sourceAndDestinationLayout.setVisibility(View.VISIBLE);

            }  else if (flowValue == 1){    //  Selecting service type Screen

                lytRouteFound.setVisibility(View.VISIBLE);

                menuButton.setVisibility(View.GONE);
                frmSource.setVisibility(View.VISIBLE);
                destinationBorderImg.setVisibility(View.GONE);
                frmDestination.setVisibility(View.VISIBLE);

                imgBack.setVisibility(View.VISIBLE);
                lnrSelectServiceType.startAnimation(slide_up);
                lnrSelectServiceType.setVisibility(View.VISIBLE);

                if (SharedHelper.getKey(mContext, "payment_mode").equalsIgnoreCase("CASH")) {
                    imgPaymentType.setImageResource(R.drawable.money_icon);
                    textPayment.setText("CASH");
                } else {
                    imgPaymentType.setImageResource(R.drawable.wallet);
                    textPayment.setText("$" + SharedHelper.getKey(mContext, "wallet_balance"));
                }

            } else if( flowValue == 2) {        //  Screen to Confirm ride with checking distance, time...
                imgBack.setVisibility(View.VISIBLE);
                menuButton.setVisibility(View.GONE);
                chkWallet.setVisibility(View.GONE);

                if (SharedHelper.getKey(mContext, "payment_mode").equalsIgnoreCase("WALLET")){
                    lnrAvailableBalance.setVisibility(View.VISIBLE);
                } else {
                    lnrAvailableBalance.setVisibility(View.GONE);
                }

                btnDistanceDetail.setImageResource(R.drawable.drop_down);
                btnETADetail.setImageResource(R.drawable.drop_down);
                rootView.findViewById(R.id.lyt_distance_detail).setVisibility(View.GONE);
                rootView.findViewById(R.id.lyt_eta_detail).setVisibility(View.GONE);

//                if (!Double.isNaN(wallet_balance) && wallet_balance > 0) {
//                    if (lineView != null && chkWallet != null) {
//                        lineView.setVisibility(View.VISIBLE);
//                        chkWallet.setVisibility(View.GONE);
//                        lnrAvailableBalance.setVisibility(View.GONE);
//                    }
//                } else {
//                    if (lineView != null && chkWallet != null) {
//                        lineView.setVisibility(View.GONE);
//
//                        lnrAvailableBalance.setVisibility(View.GONE);
//                    }
//                }

                chkWallet.setChecked(false);
                lnrApproximate.startAnimation(slide_up);
                lnrApproximate.setVisibility(View.VISIBLE);
                if (mSrcMarker != null && mDstMarker != null) {
                    mSrcMarker.setDraggable(false);
                    mDstMarker.setDraggable(false);
                }
            } else if ( flowValue == 12) {
                imgBack.setVisibility(View.VISIBLE);
                menuButton.setVisibility(View.GONE);
                chkWallet.setChecked(false);
                lnrChooseProviders.startAnimation(slide_up);
                lnrChooseProviders.setVisibility(View.VISIBLE);
                if (mSrcMarker != null && mDstMarker != null) {
                    mSrcMarker.setDraggable(false);
                    mDstMarker.setDraggable(false);
                }
            } else if (flowValue == 3) {        //  Finding driver Screen
                imgBack.setVisibility(View.GONE);
                menuButton.setVisibility(View.GONE);
                lnrWaitingForProviders.setVisibility(View.VISIBLE);
                srcDestLayout.setVisibility(View.GONE);
                //menuAndExploreLayout.setVisibility(View.VISIBLE);
                //sourceAndDestinationLayout.setVisibility(View.GONE);
                if (mSrcMarker != null && mDstMarker != null) {
                    mSrcMarker.setDraggable(false);
                    mDstMarker.setDraggable(false);
                }

                startCountWaitingTime();

            } else if (flowValue == 4) {        //  Accepted, Arrived, On Ride Screen
                menuButton.setVisibility(View.VISIBLE);
                lnrProviderAccepted.startAnimation(slide_up);
                lnrProviderAccepted.setVisibility(View.VISIBLE);
                if (mSrcMarker != null && mDstMarker != null) {
                    mSrcMarker.setDraggable(false);
                    mDstMarker.setDraggable(false);
                }
            } else if (flowValue == 5) {        //  Paying Screen
                menuButton.setVisibility(View.VISIBLE);
                lnrInvoice.startAnimation(slide_up);
                lnrInvoice.setVisibility(View.VISIBLE);

                chkCoupon.setChecked(false);

                if (mSrcMarker != null && mDstMarker != null) {
                    mSrcMarker.setDraggable(false);
                    mDstMarker.setDraggable(false);
                }

                if (SharedHelper.getKey(mContext, "payment_mode").equalsIgnoreCase("WALLET")){
                    imgPaymentTypeInvoice.setImageResource(R.drawable.wallet);
                    lblPaymentTypeInvoice.setText("WALLET");
                } else{
                    imgPaymentTypeInvoice.setImageResource(R.drawable.cash_payment_image);
                    lblPaymentTypeInvoice.setText("CASH");
                }

            } else if (flowValue == 6) {        /// Giving feedback Screen
                menuButton.setVisibility(View.VISIBLE);
                lnrRateProvider.startAnimation(slide_up);
                lnrRateProvider.setVisibility(View.VISIBLE);
                LayerDrawable drawable = (LayerDrawable) ratingProviderRate.getProgressDrawable();
                drawable.getDrawable(0).setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
                drawable.getDrawable(1).setColorFilter(Color.parseColor("#FFAB00"), PorterDuff.Mode.SRC_ATOP);
                drawable.getDrawable(2).setColorFilter(Color.parseColor("#FFAB00"), PorterDuff.Mode.SRC_ATOP);
                ratingProviderRate.setRating(5.0f);
                feedBackRating = 1;
                ratingProviderRate.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float rating, boolean b) {
                        if (rating < 1.0f) {
                            ratingProviderRate.setRating(1.0f);
                            feedBackRating = 1;
                        }
                        feedBackRating = rating;
                    }
                });
                if (mSrcMarker != null && mDstMarker != null) {
                    mSrcMarker.setDraggable(false);
                    mDstMarker.setDraggable(false);
                }
            } else if (flowValue == 7){             //  Screen to schedule

                imgBack.setVisibility(View.VISIBLE);
                ScheduleLayout.startAnimation(slide_up);
                ScheduleLayout.setVisibility(View.VISIBLE);
                if (mSrcMarker != null && mDstMarker != null) {
                    mSrcMarker.setDraggable(false);
                    mDstMarker.setDraggable(false);
                }

            }  else if (flowValue == 9) {
                srcDestLayout.setVisibility(View.GONE);
                rtlStaticMarker.setVisibility(View.VISIBLE);
                shadowBack.setVisibility(View.GONE);
                frmDestination.setVisibility(View.GONE);
                if (mSrcMarker != null && mDstMarker != null) {
                    mSrcMarker.setDraggable(false);
                    mDstMarker.setDraggable(false);
                }
            } else if (flowValue == 11){           //   Second Screen to confirm destination
                imgBack.setVisibility(View.VISIBLE);
                menuButton.setVisibility(View.GONE);
                chkWallet.setChecked(false);
                lnrSetDest.startAnimation(slide_up);
                lnrSetDest.setVisibility(View.VISIBLE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    @Override
//    public void onInteractionExploreToMap(String lat, String lng) {
//
//        Utils.displayMessage(getActivity(), "lat: " + lat + " lng: " + lng);
//
//    }

    /*  Send schedule request.*/
    public void sendRequest() {
        customDialog = new CustomDialog(mContext);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();

        String strWallet = "";

        if (chkWallet.isChecked()) {
            strWallet = "1";
        } else {
            strWallet = "0";
        }

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        JsonObject gsonObject = new JsonObject();
        try {
            JSONObject paramObject = new JSONObject();

            Log.wtf("PAYMENT MODE ~~~ ",SharedHelper.getKey(mContext, "payment_mode"));

            paramObject.put("api_token", SharedHelper.getKey(mContext, "api_token"));
            paramObject.put("s_latitude", source_lat);
            paramObject.put("s_longitude", source_lng);
            paramObject.put("d_latitude", dest_lat);
            paramObject.put("d_longitude", dest_lng);
            paramObject.put("s_address", source_address);
            paramObject.put("d_address", dest_address);
            //paramObject.put("service_type", mServiceType);
            paramObject.put("service_type", mServiceType);
            paramObject.put("distance", mDistanceKm);
            paramObject.put("schedule_date", scheduledDate);
            paramObject.put("schedule_time", scheduledTime);
            paramObject.put("payment_mode", SharedHelper.getKey(mContext, "payment_mode"));
            paramObject.put("use_wallet", strWallet);

            if (!SharedHelper.getKey(mContext, "payment_mode").equals("CASH")) {
                paramObject.put("card_id", SharedHelper.getKey(mContext, "card_id"));
            }

            JsonParser jsonParser = new JsonParser();
            gsonObject = (JsonObject) jsonParser.parse(paramObject.toString());

            apiInterface.doRequestScheduleRide(gsonObject).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    if (customDialog != null)
                        customDialog.dismiss();

                    try {
                        JSONObject object = new JSONObject(response.body().string());

                        if (object.getString("status").equals("1")){

                            JSONObject data = object.getJSONObject("data");
                            String request_id = data.getString("request_id");

                            if (request_id.equals("") || (request_id == null)) {
                                Utils.displayMessage(mActivity, Utils.getMessageForKey(data.getString("message")));
                            } else {
                                SharedHelper.putKey(mContext, "current_status", "");
                                SharedHelper.putKey(mContext, "request_id", "" + data.getString("request_id"));
                                if (!scheduledDate.equalsIgnoreCase("") && !scheduledTime.equalsIgnoreCase(""))
                                    scheduleTrip = true;
                                else
                                    scheduleTrip = false;
                                flowValue = 3;
                                layoutChanges();
                            }

                        } else{
                            JSONObject data = object.getJSONObject("data");
                            Utils.displayMessage(mActivity, Utils.parseErrorMessage(data));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Utils.displayMessage(mActivity, getString(R.string.server_connect_error));
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    if (customDialog != null)
                        customDialog.dismiss();
                    t.printStackTrace();
                    Utils.displayMessage(mActivity, getString(R.string.server_connect_error));
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void show(final View view) {
        mIsShowing = true;
        ViewPropertyAnimator animator = view.animate()
                .translationY(0)
                .setInterpolator(INTERPOLATOR)
                .setDuration(500);

        animator.setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                mIsShowing = false;
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                // Canceling a show should hide the view
                mIsShowing = false;
                if (!mIsHiding) {
                    hide(view);
                }
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });

        animator.start();
    }


    private void hide(final View view) {
        mIsHiding = true;
        ViewPropertyAnimator animator = view.animate()
                .translationY(view.getHeight())
                .setInterpolator(INTERPOLATOR)
                .setDuration(200);

        animator.setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                // Prevent drawing the View after it is gone
                mIsHiding = false;
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                // Canceling a hide should show the view
                mIsHiding = false;
                if (!mIsShowing) {
                    show(view);
                }
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });

        animator.start();
    }

    private void showTripExtendAlert(PlacePredictions placePredictions) {

//        dest_lat = placePredictions.strDestLatitude;
//        dest_lng = placePredictions.strDestLongitude;
//        dest_address = placePredictions.strDestAddress;
//
//        if (mDstMarker != null)
//            mDstMarker.remove();
//
//
//        flowValue = 11;
//        layoutChanges();


//        Utilities.getAddressUsingLatLng("destination", frmDest, context, latitude, longitude);
//        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
//        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        builder.setTitle(AccessDetails.siteTitle)
//                .setIcon(AccessDetails.site_icon)
//                .setMessage(getString(R.string.extend_trip_alert));
//        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                Utilities.getAddressUsingLatLng("destination", destination, context, latitude, longitude);
//                extendTripAPI(latitude, longitude);
//            }
//        });
//        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                //Reset to previous seletion menu in navigation
//                dialog.dismiss();
//            }
//        });
//        builder.setCancelable(false);
//        final android.support.v7.app.AlertDialog dialog = builder.create();
//        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
//        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
//            @Override
//            public void onShow(DialogInterface arg) {
//                dialog.getButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
//                dialog.getButton(android.support.v7.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
//            }
//        });
//        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (requestCode == EXPLORE_REQUEST_CODE){

            if (resultCode == Activity.RESULT_OK) {

                dest_lat = data.getStringExtra("dest_lat");
                dest_lng = data.getStringExtra("dest_lng");
                dest_address = data.getStringExtra("dest_address");

                mSrcLatLng = new LatLng(current_latitude, current_longitude);
                mDstLatLng = new LatLng(Double.parseDouble(dest_lat), Double.parseDouble(dest_lng));

                if (mSrcMarker != null)
                    mSrcMarker.remove();

                addSrcMarker(mSrcLatLng);

                if (mDstMarker != null)
                    mDstMarker.remove();

                MarkerOptions destMarkerOptions = new MarkerOptions()
                        .position(mDstLatLng).title("destination").snippet(frmDest.getText().toString())
                        //   .icon(BitmapDescriptorFactory.fromResource(R.drawable.provider_marker));
                        //  .icon(BitmapDescriptorFactory.fromResource(R.drawable.destination_marker));
                        //  .icon(BitmapDescriptorFactory.fromBitmap(createDestinationMarker(frmDest.getText().toString(),SharedHelper.getKey(getContext(),"eta_time"))));
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

                mDstMarker = mMap.addMarker(destMarkerOptions);
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(mSrcMarker.getPosition());
                builder.include(mDstMarker.getPosition());
                LatLngBounds bounds = builder.build();
                int padding = 300; // offset from edges of the map in pixels
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                mMap.moveCamera(cu);

                tVQuestion.setText("Would you like to go to " + dest_address + "?");
                flowValue = 11;
                layoutChanges();

                Log.i("MapFragment", "Boldman-----> onActivityResult");
            }
        }
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE_DEST) {

            if (resultCode == Activity.RESULT_OK) {

                PlacePredictions placePredictions;
                placePredictions = (PlacePredictions) data.getSerializableExtra("Location Address");
                strPickLocation = data.getExtras().getString("pick_location");
                strPickType = data.getExtras().getString("type");
                if (strPickLocation.equalsIgnoreCase("yes")) {
                    pick_first = true;
                    mMap.clear();
                    flowValue = 9;
                    layoutChanges();
                    float zoomLevel = 16.0f; //This goes up to 21
                    stopAnim();
                } else {
                    if (placePredictions != null) {
                        if (is_track.equalsIgnoreCase("YES") && CurrentStatus.equalsIgnoreCase("PICKEDUP")) {
                            showTripExtendAlert(placePredictions);
                        } else {
                            if (!placePredictions.strSourceAddress.equalsIgnoreCase("")) {
                                try {
                                    source_lat = "" + placePredictions.strSourceLatitude;
                                    source_lng = "" + placePredictions.strSourceLongitude;
//                                    source_address =  Utilities.getAddressUsingLatLng("source", frmSource, context, ""+source_lat,
//                                            ""+source_lng);
                                    source_address = "" + placePredictions.strSourceAddress;
                                    frmSource.setText(source_address);
                                    SharedHelper.putKey(mContext, "source", "" + source_address);
                                    if (!placePredictions.strSourceLatitude.equalsIgnoreCase("")
                                            && !placePredictions.strSourceLongitude.equalsIgnoreCase("")) {
                                        double latitude = Double.parseDouble(placePredictions.strSourceLatitude);
                                        double longitude = Double.parseDouble(placePredictions.strSourceLongitude);

                                        mSrcLatLng = new LatLng(latitude, longitude);

                                        if (mSrcMarker != null)
                                            mSrcMarker.remove();

                                        MarkerOptions markerOptions = new MarkerOptions()
                                                .position(mSrcLatLng)
                                                .snippet(frmSource.getText().toString())
                                                .title("source")
                                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

                                        mSrcMarker = mMap.addMarker(markerOptions);

                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            if (!placePredictions.strDestAddress.equalsIgnoreCase("")) {
                                dest_lat = "" + placePredictions.strDestLatitude;
                                dest_lng = "" + placePredictions.strDestLongitude;
//                                dest_address =  Utilities.getAddressUsingLatLng("destination", frmDest, context, ""+dest_lat,
//                                        ""+dest_lng);
                                dest_address = "" + placePredictions.strDestAddress;
                                SharedHelper.putKey(mContext, "destination", "" + dest_address);
                                frmDest.setText(dest_address);
                                SharedHelper.putKey(mContext, "current_status", "2");
                                if (source_lat != null && source_lng != null && !source_lng.equalsIgnoreCase("")
                                        && !source_lat.equalsIgnoreCase("")) {
                                    try {

                                        mSrcLatLng = new LatLng(Double.parseDouble(source_lat), Double.parseDouble(source_lng));

                                        if (mSrcMarker != null)
                                            mSrcMarker.remove();

                                        MarkerOptions markerOptions = new MarkerOptions()
                                                .position(mSrcLatLng)
                                                .snippet(frmSource.getText().toString())
                                                .title("source")
                                                //  .icon(BitmapDescriptorFactory.fromResource(R.drawable.user_marker));
                                                //     .icon(BitmapDescriptorFactory.fromResource(R.drawable.source_marker));
                                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

                                        mSrcMarker = mMap.addMarker(markerOptions);

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                if (!dest_lat.equalsIgnoreCase("") && !dest_lng.equalsIgnoreCase("")) {
                                    try {
                                        mDstLatLng = new LatLng(Double.parseDouble(dest_lat), Double.parseDouble(dest_lng));
                                        if (mDstMarker != null)
                                            mDstMarker.remove();
                                        MarkerOptions destMarkerOptions = new MarkerOptions()
                                                .position(mDstLatLng).title("destination").snippet(frmDest.getText().toString())
                                                //   .icon(BitmapDescriptorFactory.fromResource(R.drawable.provider_marker));
                                                //  .icon(BitmapDescriptorFactory.fromResource(R.drawable.destination_marker));
                                        //  .icon(BitmapDescriptorFactory.fromBitmap(createDestinationMarker(frmDest.getText().toString(),SharedHelper.getKey(getContext(),"eta_time"))));
                                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

                                        mDstMarker = mMap.addMarker(destMarkerOptions);
                                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                        builder.include(mSrcMarker.getPosition());
                                        builder.include(mDstMarker.getPosition());
                                        LatLngBounds bounds = builder.build();
                                        int padding = 300; // offset from edges of the map in pixels
                                        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                                        mMap.moveCamera(cu);

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            Log.i("onActivityResult", "Boldman---------> Before dest_address compare");
                            if (dest_address.equalsIgnoreCase("")) {
//                            frmSource.setText(source_address);

                                Log.i("onActivityResult", "Boldman-------> When dest_address equla ''");

                            } else {

                                Log.i("onActivityResult", "Boldman---------> Before Question");
                                tVQuestion.setText("Would you like to go to " + dest_address + "?");
                                flowValue = 11;
                                layoutChanges();
                            }
                        }
                    }
                }
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getActivity(), data);
                // TODO: Handle the error.
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
        if (requestCode == PAYMENT_SELECT_REQUEST_CODE){
            if (resultCode == Activity.RESULT_OK) {
                String strPaymentType = data.getStringExtra("payment_type");

                if (strPaymentType.equals("CASH")) {
                    SharedHelper.putKey(mContext, "payment_mode", "CASH");
                    imgPaymentType.setImageResource(R.drawable.money_icon);
                    textPayment.setText("CASH");
                    //    lblPaymentType.setText("CASH");
                } else {
//                    SharedHelper.putKey(mContext, "card_id", String.valueOf(cardInfo.getCardId()));
                    SharedHelper.putKey(mContext, "payment_mode", "WALLET");
                    imgPaymentType.setImageResource(R.drawable.wallet);
                    textPayment.setText("$" + SharedHelper.getKey(mContext, "wallet_balance"));
                    //  lblPaymentType.setText("XXXX-XXXX-XXXX-" + cardInfo.getLastFour());
                }

            }
        }
        if (requestCode == WALLET_TOPUP_REQUEST_CODE){

            if (resultCode == Activity.RESULT_OK) {

                textPayment.setText("$" + SharedHelper.getKey(mContext, "wallet_balance"));

                if (tvWalletBalanceDlg != null)
                    tvWalletBalanceDlg.setText(getString(R.string.wallet) + " " + getString(R.string.balance) + "$" +
                            SharedHelper.getKey(mContext, "wallet_balance"));
            }
        }
        if (requestCode == COUPON_CODE_SELECT_REQEUST_CODE){

            if (resultCode == Activity.RESULT_OK) {

                try {
                    mCouponCode = data.getStringExtra("coupon_code");
                    mCouponDiscount = data.getStringExtra("discount");
                    mCouponDiscountType = data.getStringExtra("discount_type");

                    calculatePrices();

                } catch (Exception e){
                    e.printStackTrace();
                }

            } else
                chkCoupon.setChecked(false);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void onMenuPressed(double latitude,double longitude) {
        if (mListener != null) {
            mListener.onMenuPressed(latitude,longitude);
        }
    }

    public interface OnFragmentInteractionListener {
        void onMenuPressed(double latitude,double longitude);
    }

    @Override
    public void onDestroy(){

        mSocket.off();
        mSocket.disconnect();
        GlobalEvent.getBus().unregister(this);

        super.onDestroy();
    }

    public String getAddressUsingLatLng(final String strType, final TextView txtView, final Context context, String latitude, String longitude){

        mApiInterface = ApiClient.getGoogleMapClient().create(ApiInterface.class);

        Call<ResponseBody> call = mApiInterface.getGoogleMapResponse(latitude + "," + longitude,
                context.getResources().getString(R.string.google_map_api));

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.i("getAddressUseLatLng", "Boldman------>SUCESS" + response.body());
                Log.i("getAddressUseLatLng", "Boldman------>URL GET" + call.request().url().toString());
                if (response.body() != null) {
                    try {
                        String bodyString = new String(response.body().bytes());
                        Log.i("getAddressUseLatLng", "Boldman------>bodyString" + bodyString);
                        try {
                            JSONObject jsonObj = new JSONObject(bodyString);
                            JSONArray jsonArray = jsonObj.getJSONArray("results");
                            if (jsonArray.length() > 0) {

                                formatted_address = jsonArray.getJSONObject(0).getString("formatted_address");
                                txtView.setText("" + current_address);

                                if (strType.equalsIgnoreCase("source")){
                                    SharedHelper.putKey(context, "source", "" + formatted_address);
                                }else{
                                    SharedHelper.putKey(context, "destination", "" + formatted_address);
                                }

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("onFailure", "onFailure" + call.request().url());
            }
        });

        return "" + formatted_address;
    }

    // When back button of phone is clicked, this function is called.
    public boolean allowBackPressed(){

        if (flowValue == 11){
            flowValue = 0;
            layoutChanges();
            return false;
        } else
            return true;
    }
}
