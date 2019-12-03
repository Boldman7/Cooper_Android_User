package com.boldman.cooperuser.Activities.Service;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.boldman.cooperuser.Adapters.AutoCompleteAdapter;
import com.boldman.cooperuser.Api.ApiClient;
import com.boldman.cooperuser.Api.ApiInterface;
import com.boldman.cooperuser.CooperUserApplication;
import com.boldman.cooperuser.Helper.SharedHelper;
import com.boldman.cooperuser.Model.AccessDetails;
import com.boldman.cooperuser.Model.LocalPlace;
import com.boldman.cooperuser.Model.PlaceAutoComplete;
import com.boldman.cooperuser.Model.PlacePredictions;
import com.boldman.cooperuser.Model.RecentAddressData;
import com.boldman.cooperuser.R;
import com.boldman.cooperuser.Utils.Utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;


/**
 * Created by Tranxit Technologies Pvt Ltd, Chennai
 */

public class GooglePlaceSearchActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final int MY_PERMISSIONS_REQUEST_LOC = 30;
    double latitude;
    double longitude;
    TextView txtPickLocation;
    ImageView backArrow, imgDestClose, imgSourceClose;
    LinearLayout lnrFavorite;
    Activity thisActivity;
    String strSource = "";
    String strSelected = "";
    Bundle extras;
    TextView txtHomeLocation, txtWorkLocation,txtStoreLoaction;
    LinearLayout lnrHome, lnrWork,lnrStore;
    ArrayList<RecentAddressData> lstRecentList = new ArrayList<RecentAddressData>();
    RelativeLayout rytAddressSource;
    RecyclerView rvRecentResults;
    String formatted_address = "";
    private ListView mAutoCompleteList;
    private EditText txtDestination, txtaddressSource;
    private String GETPLACESHIT = "places_hit";
    private PlacePredictions predictions = new PlacePredictions();
    private AutoCompleteAdapter mAutoCompleteAdapter;
    private Location mLastLocation;
    private Handler handler;
    private GoogleApiClient mGoogleApiClient;
    private ApiInterface mApiInterface;
    private PlacePredictions placePredictions = new PlacePredictions();
    private int UPDATE_HOME_WORK = 1;
    Activity mActivity = this;
    Context mContext = this;

    List<LocalPlace> mListLocalPlaces = new ArrayList<>();
    ArrayList<PlaceAutoComplete> totalPlaces = new ArrayList<>();
    int cntGooglePlaces = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_soruce_and_destination);
        thisActivity = this;
        txtDestination = findViewById(R.id.txtDestination);
        txtaddressSource = findViewById(R.id.txtaddressSource);

        backArrow =  findViewById(R.id.backArrow);
        imgDestClose = findViewById(R.id.imgDestClose);
        imgSourceClose = findViewById(R.id.imgSourceClose);

        txtPickLocation = findViewById(R.id.txtPickLocation);
        txtWorkLocation = findViewById(R.id.txtWorkLocation);
        txtHomeLocation = findViewById(R.id.txtHomeLocation);
        txtStoreLoaction =  findViewById(R.id.txtStoreLocation);

        lnrFavorite =findViewById(R.id.lnrFavorite);
        lnrHome = findViewById(R.id.lnrHome);
        lnrWork = findViewById(R.id.lnrWork);
        lnrStore = findViewById(R.id.lnrStore);

        rytAddressSource = findViewById(R.id.rytAddressSource);

        rvRecentResults = findViewById(R.id.rvRecentResults);
        mAutoCompleteList = findViewById(R.id.searchResultLV);

        String cursor = getIntent().getExtras().getString("cursor");
        String s_address = getIntent().getExtras().getString("s_address");
        String d_address = getIntent().getExtras().getString("d_address");

        Log.e("CustomGoogleSearch", "Boldman------>onCreate: source " + s_address);
        Log.e("CustomGoogleSearch", "Boldman------>onCreate: destination" + d_address);
        txtaddressSource.setText(s_address);

        if (d_address != null && !d_address.equalsIgnoreCase("")) {
            txtDestination.setText(d_address);
        }

        if (cursor.equalsIgnoreCase("source")) {
            strSelected = "source";
            txtaddressSource.requestFocus();
            imgSourceClose.setVisibility(View.VISIBLE);
            imgDestClose.setVisibility(View.GONE);
        } else {
            txtDestination.requestFocus();
            strSelected = "destination";
            imgDestClose.setVisibility(View.VISIBLE);
            imgSourceClose.setVisibility(View.GONE);
        }

        String strStatus = SharedHelper.getKey(thisActivity, "req_status");

        if (strStatus.equalsIgnoreCase("PICKEDUP")) {
            if (SharedHelper.getKey(thisActivity, "track_status").equalsIgnoreCase("YES")) {
                rytAddressSource.setVisibility(View.GONE);
            } else {
                rytAddressSource.setVisibility(View.VISIBLE);
            }
        }

        getFavoriteLocations();

        txtaddressSource.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    strSelected = "source";
                    imgSourceClose.setVisibility(View.VISIBLE);
                } else {
                    imgSourceClose.setVisibility(View.GONE);
                }
            }
        });

        txtDestination.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    strSelected = "destination";
                    imgDestClose.setVisibility(View.VISIBLE);
                } else {
                    imgDestClose.setVisibility(View.GONE);
                }
            }
        });

        imgDestClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtDestination.setText("");
                mAutoCompleteList.setVisibility(View.GONE);
                imgDestClose.setVisibility(View.GONE);
                txtDestination.requestFocus();
            }
        });

        imgSourceClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtaddressSource.setText("");
                mAutoCompleteList.setVisibility(View.GONE);
                imgSourceClose.setVisibility(View.GONE);
                txtaddressSource.requestFocus();
            }
        });

        txtPickLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.hideKeyboard(thisActivity);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent();
                        intent.putExtra("pick_location", "yes");
                        intent.putExtra("type", strSelected);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }, 500);
            }
        });

        lnrHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SharedHelper.getKey(GooglePlaceSearchActivity.this, "home").equalsIgnoreCase("")) {
                    gotoHomeWork("home");
                } else {
                    if (strSelected.equalsIgnoreCase("destination")) {
                        placePredictions.strDestAddress = SharedHelper.getKey(GooglePlaceSearchActivity.this, "home");
                        placePredictions.strDestLatitude = SharedHelper.getKey(GooglePlaceSearchActivity.this, "home_lat");
                        placePredictions.strDestLongitude = SharedHelper.getKey(GooglePlaceSearchActivity.this, "home_lng");
                        LatLng latlng = new LatLng(Double.parseDouble(placePredictions.strDestLatitude), Double.parseDouble(placePredictions.strDestLatitude));
                        placePredictions.strDestLatLng = "" + latlng;
                        if (!txtaddressSource.getText().toString().equalsIgnoreCase(SharedHelper.getKey(GooglePlaceSearchActivity.this, "home"))) {
                            txtDestination.setText(SharedHelper.getKey(GooglePlaceSearchActivity.this, "home"));
                            txtDestination.setSelection(0);
                        } else {
                            Toast.makeText(thisActivity, getResources().getString(R.string.source_dest_not_same), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        placePredictions.strSourceAddress = SharedHelper.getKey(GooglePlaceSearchActivity.this, "home");
                        placePredictions.strSourceLatitude = SharedHelper.getKey(GooglePlaceSearchActivity.this, "home_lat");
                        placePredictions.strSourceLongitude = SharedHelper.getKey(GooglePlaceSearchActivity.this, "home_lng");
                        LatLng latlng = new LatLng(Double.parseDouble(placePredictions.strSourceLatitude), Double.parseDouble(placePredictions.strSourceLongitude));
                        placePredictions.strSourceLatLng = "" + latlng;
                        txtaddressSource.setText(placePredictions.strSourceAddress);
                        txtaddressSource.setSelection(0);
                        txtDestination.requestFocus();
                        mAutoCompleteAdapter = null;
                    }

                    if (!txtDestination.getText().toString().equalsIgnoreCase("")) {
                        setAddress();
                    }
                }
            }
        });

        lnrWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!txtaddressSource.getText().toString().equalsIgnoreCase(txtDestination.getText().toString())) {
                    if (SharedHelper.getKey(GooglePlaceSearchActivity.this, "work").equalsIgnoreCase("")) {
                        gotoHomeWork("work");
                    } else {
                        if (strSelected.equalsIgnoreCase("destination")) {
                            placePredictions.strDestAddress = SharedHelper.getKey(GooglePlaceSearchActivity.this, "work");
                            placePredictions.strDestLatitude = SharedHelper.getKey(GooglePlaceSearchActivity.this, "work_lat");
                            placePredictions.strDestLongitude = SharedHelper.getKey(GooglePlaceSearchActivity.this, "work_lng");
                            LatLng latlng = new LatLng(Double.parseDouble(placePredictions.strDestLatitude), Double.parseDouble(placePredictions.strDestLatitude));
                            placePredictions.strDestLatLng = "" + latlng;
                            if (!txtaddressSource.getText().toString().equalsIgnoreCase(SharedHelper.getKey(GooglePlaceSearchActivity.this, "work"))) {
                                txtDestination.setText(SharedHelper.getKey(GooglePlaceSearchActivity.this, "work"));
                                txtDestination.setSelection(0);
                            }else {
                                Toast.makeText(thisActivity, getResources().getString(R.string.source_dest_not_same), Toast.LENGTH_SHORT).show();
                            }
                            txtDestination.setSelection(0);
                        } else {
                            placePredictions.strSourceAddress = SharedHelper.getKey(GooglePlaceSearchActivity.this, "work");
                            placePredictions.strSourceLatitude = SharedHelper.getKey(GooglePlaceSearchActivity.this, "work_lat");
                            placePredictions.strSourceLongitude = SharedHelper.getKey(GooglePlaceSearchActivity.this, "work_lng");
                            LatLng latlng = new LatLng(Double.parseDouble(placePredictions.strSourceLatitude), Double.parseDouble(placePredictions.strSourceLongitude));
                            placePredictions.strSourceLatLng = "" + latlng;
                            txtaddressSource.setText(placePredictions.strSourceAddress);
                            txtaddressSource.setSelection(0);
                            txtDestination.requestFocus();
                            mAutoCompleteAdapter = null;
                        }

                        if (!txtDestination.getText().toString().equalsIgnoreCase("")) {
                            setAddress();
                        }
                    }
                }
            }
        });

        lnrStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!txtaddressSource.getText().toString().equalsIgnoreCase(txtDestination.getText().toString())) {
                    if (SharedHelper.getKey(GooglePlaceSearchActivity.this, "store").equalsIgnoreCase("")) {
                        gotoHomeWork("store");
                    } else {
                        if (strSelected.equalsIgnoreCase("destination")) {
                            placePredictions.strDestAddress = SharedHelper.getKey(GooglePlaceSearchActivity.this, "store");
                            placePredictions.strDestLatitude = SharedHelper.getKey(GooglePlaceSearchActivity.this, "store_lat");
                            placePredictions.strDestLongitude = SharedHelper.getKey(GooglePlaceSearchActivity.this, "store_lng");
                            LatLng latlng = new LatLng(Double.parseDouble(placePredictions.strDestLatitude), Double.parseDouble(placePredictions.strDestLatitude));
                            placePredictions.strDestLatLng = "" + latlng;
                            if (!txtaddressSource.getText().toString().equalsIgnoreCase(SharedHelper.getKey(GooglePlaceSearchActivity.this, "store"))) {
                                txtDestination.setText(SharedHelper.getKey(GooglePlaceSearchActivity.this, "store"));
                                txtDestination.setSelection(0);
                            }else {
                                Toast.makeText(thisActivity, getResources().getString(R.string.source_dest_not_same), Toast.LENGTH_SHORT).show();
                            }
                            txtDestination.setSelection(0);
                        } else {
                            placePredictions.strSourceAddress = SharedHelper.getKey(GooglePlaceSearchActivity.this, "store");
                            placePredictions.strSourceLatitude = SharedHelper.getKey(GooglePlaceSearchActivity.this, "store_lat");
                            placePredictions.strSourceLongitude = SharedHelper.getKey(GooglePlaceSearchActivity.this, "store_lng");
                            LatLng latlng = new LatLng(Double.parseDouble(placePredictions.strSourceLatitude), Double.parseDouble(placePredictions.strSourceLongitude));
                            placePredictions.strSourceLatLng = "" + latlng;
                            txtaddressSource.setText(placePredictions.strSourceAddress);
                            txtaddressSource.setSelection(0);
                            txtDestination.requestFocus();
                            mAutoCompleteAdapter = null;
                        }

                        if (!txtDestination.getText().toString().equalsIgnoreCase("")) {
                            setAddress();
                        }
                    }
                }
            }
        });

        //get permission for Android M
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            fetchLocation();
        } else {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOC);
            } else {
                fetchLocation();
            }
        }

        //Add a text change listener to implement autocomplete functionality
        txtDestination.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                imgDestClose.setVisibility(View.VISIBLE);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // optimised way is to start searching for laction after user has typed minimum 3 chars
                imgDestClose.setVisibility(View.VISIBLE);
                strSelected = "destination";
                if (txtDestination.getText().length() > 0) {
                    lnrFavorite.setVisibility(View.GONE);
                    txtPickLocation.setVisibility(View.VISIBLE);
                    imgDestClose.setVisibility(View.VISIBLE);
                    txtPickLocation.setText(getString(R.string.pin_location));
                    Runnable run = new Runnable() {
                        @Override
                        public void run() {
                            // cancel all the previous requests in the queue to optimise your network calls during autocomplete search
                            CooperUserApplication.getInstance().cancelRequestInQueue(GETPLACESHIT);

                            JSONObject object = new JSONObject();
                            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, getPlaceAutoCompleteUrl(txtDestination.getText().toString()), object, new Response.Listener<JSONObject>() {

                                @Override
                                public void onResponse(JSONObject response) {
                                    Log.v("PayNowRequestResponse", response.toString());
                                    Log.v("PayNowRequestResponse", response.toString());
                                    Gson gson = new Gson();
                                    predictions = gson.fromJson(response.toString(), PlacePredictions.class);

                                    totalPlaces = predictions.getPlaces();
                                    cntGooglePlaces = totalPlaces.size();

                                    getLocalPlaces(txtDestination.getText().toString());
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.v("PayNowRequestResponse", error.toString());
                                }
                            });

                            CooperUserApplication.getInstance().addToRequestQueue(jsonObjectRequest);
                        }

                    };

                    // only canceling the network calls will not help, you need to remove all callbacks as well
                    // otherwise the pending callbacks and messages will again invoke the handler and will send the request
                    if (handler != null) {
                        handler.removeCallbacksAndMessages(null);
                    } else {
                        handler = new Handler();
                    }
                    handler.postDelayed(run, 1000);

                } else {
                    lnrFavorite.setVisibility(View.VISIBLE);
                    mAutoCompleteList.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                imgDestClose.setVisibility(View.VISIBLE);
            }

        });

        //Add a text change listener to implement autocomplete functionality
        txtaddressSource.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                imgSourceClose.setVisibility(View.VISIBLE);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // optimised way is to start searching for laction after user has typed minimum 3 chars
                strSelected = "source";
                if (txtaddressSource.getText().length() > 0) {
                    lnrFavorite.setVisibility(View.GONE);
                    txtPickLocation.setVisibility(View.VISIBLE);
                    imgSourceClose.setVisibility(View.VISIBLE);
                    txtPickLocation.setText(getString(R.string.pin_location));
                    if (mAutoCompleteAdapter == null)
                        mAutoCompleteList.setVisibility(View.VISIBLE);
                    Runnable run = new Runnable() {

                        @Override
                        public void run() {
                            // cancel all the previous requests in the queue to optimise your network calls during autocomplete search
                            CooperUserApplication.getInstance().cancelRequestInQueue(GETPLACESHIT);

                            JSONObject object = new JSONObject();
                            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, getPlaceAutoCompleteUrl(txtaddressSource.getText().toString()),
                                    object, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Log.v("PayNowRequestResponse", response.toString());
                                    Log.v("PayNowRequestResponse", response.toString());
                                    Gson gson = new Gson();
                                    predictions = gson.fromJson(response.toString(), PlacePredictions.class);
                                    totalPlaces = predictions.getPlaces();
                                    cntGooglePlaces = totalPlaces.size();

                                    getLocalPlaces(txtDestination.getText().toString());
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.v("PayNowRequestResponse", error.toString());
                                }
                            });
                            CooperUserApplication.getInstance().addToRequestQueue(jsonObjectRequest);
                        }

                    };
                    // only canceling the network calls will not help, you need to remove all callbacks as well
                    // otherwise the pending callbacks and messages will again invoke the handler and will send the request
                    if (handler != null) {
                        handler.removeCallbacksAndMessages(null);
                    } else {
                        handler = new Handler();
                    }
                    handler.postDelayed(run, 1000);

                } else {
                    lnrFavorite.setVisibility(View.VISIBLE);
                    mAutoCompleteList.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                imgSourceClose.setVisibility(View.VISIBLE);
            }

        });

        //txtDestination.setText("");
        txtDestination.setSelection(txtDestination.getText().length());

        mAutoCompleteList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (txtaddressSource.getText().toString().equalsIgnoreCase("")) {
                    try {
                        AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity);
                        LayoutInflater inflater = (LayoutInflater) thisActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        builder.setMessage("Please choose pickup location")
                                .setTitle(AccessDetails.siteTitle)
                                .setCancelable(true)
                                .setIcon(AccessDetails.site_icon)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        txtaddressSource.requestFocus();
                                        txtDestination.setText("");
                                        imgDestClose.setVisibility(View.GONE);
                                        mAutoCompleteList.setVisibility(View.GONE);
                                        dialog.dismiss();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    setGoogleAddress(position);
                }
            }
        });
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                setAddress();
                finish();
            }
        });

    }

    private void getLocalPlaces(String strSearch) {

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        JsonObject gsonObject = new JsonObject();
        try {
            JSONObject paramObject = new JSONObject();

            paramObject.put("api_token", SharedHelper.getKey(GooglePlaceSearchActivity.this, "api_token"));
            paramObject.put("search_key", strSearch);

            JsonParser jsonParser = new JsonParser();
            gsonObject = (JsonObject) jsonParser.parse(paramObject.toString());

            apiInterface.doSearchLocalPlace(gsonObject).enqueue(new Callback<ResponseBody>() {

                @Override
                public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());

                        if (object.getString("status").equals("1")){

                            JSONObject data = object.getJSONObject("data");
                            JSONArray places = data.getJSONArray("places");

                            mListLocalPlaces = new Gson().fromJson(places.toString(), new TypeToken<List<LocalPlace>>() {
                            }.getType());

                            ArrayList<PlaceAutoComplete> localPlace = new ArrayList<>();

                            for (int i = 0; i < mListLocalPlaces.size(); i ++){

                                PlaceAutoComplete placeAutoComplete = new PlaceAutoComplete();
                                placeAutoComplete.setPlaceDesc(mListLocalPlaces.get(i).getAddress());
                                placeAutoComplete.setPlaceID("");

                                totalPlaces.add(placeAutoComplete);

                            }

                            if (mAutoCompleteAdapter == null) {
                                mAutoCompleteList.setVisibility(View.VISIBLE);

                                mAutoCompleteAdapter = new AutoCompleteAdapter(GooglePlaceSearchActivity.this, totalPlaces, GooglePlaceSearchActivity.this);
                                mAutoCompleteList.setAdapter(mAutoCompleteAdapter);
                            } else {
                                mAutoCompleteList.setVisibility(View.VISIBLE);
                                mAutoCompleteAdapter.clear();
                                mAutoCompleteAdapter.addAll(totalPlaces);
                                mAutoCompleteAdapter.notifyDataSetChanged();
                                mAutoCompleteList.invalidate();
                            }

                        } else{
                            JSONObject data = object.getJSONObject("data");
                            Utils.displayMessage(mActivity, data.getString("message"));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Utils.displayMessage(mActivity, getString(R.string.server_connect_error));
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                    t.printStackTrace();
                    Utils.displayMessage(mActivity, getString(R.string.server_connect_error));
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getFavoriteLocations() {

        Toast.makeText(this, "This is getFavouriteLocations()...", Toast.LENGTH_SHORT).show();

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        JsonObject gsonObject = new JsonObject();
        try {
            JSONObject paramObject = new JSONObject();

            paramObject.put("api_token", SharedHelper.getKey(GooglePlaceSearchActivity.this, "api_token"));

            JsonParser jsonParser = new JsonParser();
            gsonObject = (JsonObject) jsonParser.parse(paramObject.toString());

            apiInterface.doGetFavouriteLocation(gsonObject).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {

                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        JSONObject data = object.getJSONObject("data");

                        if (object.getString("status").equals("1")) {

                            JSONArray homeArray = data.optJSONArray("home");
                            JSONArray workArray = data.optJSONArray("work");
                            JSONArray othersArray = data.optJSONArray("others");
                            JSONArray recentArray = data.optJSONArray("recent");

                            if (homeArray.length() > 0) {
                                Log.v("Home Address", "" + homeArray);
                                txtHomeLocation.setText(homeArray.optJSONObject(0).optString("address"));
                                SharedHelper.putKey(GooglePlaceSearchActivity.this, "home", homeArray.optJSONObject(0).optString("address"));
                                SharedHelper.putKey(GooglePlaceSearchActivity.this, "home_lat", homeArray.optJSONObject(0).optString("latitude"));
                                SharedHelper.putKey(GooglePlaceSearchActivity.this, "home_lng", homeArray.optJSONObject(0).optString("longitude"));
                                SharedHelper.putKey(GooglePlaceSearchActivity.this, "home_id", homeArray.optJSONObject(0).optString("id"));
                            } else {
                                txtHomeLocation.setText(getResources().getString(R.string.add_home_location));
                            }
                            if (workArray.length() > 0) {
                                Log.v("Work Address", "" + workArray);
                                txtWorkLocation.setText(workArray.optJSONObject(0).optString("address"));
                                SharedHelper.putKey(GooglePlaceSearchActivity.this, "work", workArray.optJSONObject(0).optString("address"));
                                SharedHelper.putKey(GooglePlaceSearchActivity.this, "work_lat", workArray.optJSONObject(0).optString("latitude"));
                                SharedHelper.putKey(GooglePlaceSearchActivity.this, "work_lng", workArray.optJSONObject(0).optString("longitude"));
                                SharedHelper.putKey(GooglePlaceSearchActivity.this, "work_id", workArray.optJSONObject(0).optString("id"));
                            } else {
                                txtWorkLocation.setText(getResources().getString(R.string.add_work_location));
                            }
                            if (othersArray.length() > 0) {
                                Log.v("Others Address", "" + othersArray);
                            }
                            if (recentArray.length() > 0) {
                                for (int i = 0; i < recentArray.length(); i++) {
                                    RecentAddressData recentAddressData = new RecentAddressData();
                                    JSONObject jsonObject = recentArray.optJSONObject(i);
                                    recentAddressData.id = jsonObject.optInt("id");
                                    recentAddressData.userId = jsonObject.optInt("user_id");
                                    recentAddressData.address = jsonObject.optString("address");
                                    recentAddressData.type = jsonObject.optString("type");
                                    recentAddressData.latitude = jsonObject.optDouble("latitude");
                                    recentAddressData.longitude = jsonObject.optDouble("longitude");
                                    if (recentAddressData.address != null && !recentAddressData.address.equalsIgnoreCase("")) {
                                        lstRecentList.add(recentAddressData);
                                    }
                                }

                                Log.v("Recent Address", "" + recentArray);
                                rvRecentResults.setVisibility(View.VISIBLE);
                                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                                rvRecentResults.setLayoutManager(mLayoutManager);
                                rvRecentResults.setItemAnimator(new DefaultItemAnimator());
                                RecentPlacesAdapter recentPlacesAdapter = new RecentPlacesAdapter(recentArray, lstRecentList);
                                rvRecentResults.setAdapter(recentPlacesAdapter);
                            }
                        } else {
                        }
                    } catch(Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e("onFailure", "onFailure" + call.request().url());
                }
            });
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setGoogleAddress(int position) {
        if (mGoogleApiClient != null) {

            if (totalPlaces.get(position).getPlaceID() != "") {

                Log.i("setGoogleAddress", "Place ID == >" + predictions.getPlaces().get(position).getPlaceID());
                Places.GeoDataApi.getPlaceById(mGoogleApiClient, predictions.getPlaces().get(position).getPlaceID())
                        .setResultCallback(new ResultCallback<PlaceBuffer>() {
                            @Override
                            public void onResult(PlaceBuffer places) {
                                if (places.getStatus().isSuccess()) {
                                    Place myPlace = places.get(0);
                                    LatLng queriedLocation = myPlace.getLatLng();
                                    Log.v("Latitude is", "" + queriedLocation.latitude);
                                    Log.v("Longitude is", "" + queriedLocation.longitude);
                                    if (strSelected.equalsIgnoreCase("destination")) {
                                        placePredictions.strDestAddress = myPlace.getAddress().toString();
                                        placePredictions.strDestLatLng = myPlace.getLatLng().toString();
                                        placePredictions.strDestLatitude = myPlace.getLatLng().latitude + "";
                                        placePredictions.strDestLongitude = myPlace.getLatLng().longitude + "";
                                        txtDestination.setText(placePredictions.strDestAddress);
                                        txtDestination.setSelection(0);
                                    } else {
                                        placePredictions.strSourceAddress = myPlace.getAddress().toString();
                                        placePredictions.strSourceLatLng = myPlace.getLatLng().toString();
                                        placePredictions.strSourceLatitude = myPlace.getLatLng().latitude + "";
                                        placePredictions.strSourceLongitude = myPlace.getLatLng().longitude + "";
                                        txtaddressSource.setText(placePredictions.strSourceAddress);
                                        txtaddressSource.setSelection(0);
                                        txtDestination.requestFocus();
                                        mAutoCompleteAdapter = null;
                                    }
                                }
                                mAutoCompleteList.setVisibility(View.GONE);

                                if (txtDestination.getText().toString().length() > 0) {
                                    places.release();
                                    if (strSelected.equalsIgnoreCase("destination")) {
                                        if (!placePredictions.strDestAddress.equalsIgnoreCase(placePredictions.strSourceAddress)) {
                                            setAddress();
                                        } else {
                                            Utils.showAlert(thisActivity, thisActivity.getResources().getString(R.string.source_dest_not_same));
                                        }
                                    }
                                } else {
                                    txtDestination.requestFocus();
                                    txtDestination.setText("");
                                    imgDestClose.setVisibility(View.GONE);
                                    mAutoCompleteList.setVisibility(View.GONE);
                                }
                            }
                        });
            } else{

                if (strSelected.equalsIgnoreCase("destination")) {
                    placePredictions.strDestAddress = mListLocalPlaces.get(position - cntGooglePlaces).getAddress();
                    placePredictions.strDestLatLng = "";
                    placePredictions.strDestLatitude = mListLocalPlaces.get(position - cntGooglePlaces).getLatitude() + "";
                    placePredictions.strDestLongitude = mListLocalPlaces.get(position - cntGooglePlaces).getLongitude() + "";
                    txtDestination.setText(placePredictions.strDestAddress);
                    txtDestination.setSelection(0);
                } else {
                    placePredictions.strSourceAddress = mListLocalPlaces.get(position - cntGooglePlaces).getAddress();
                    placePredictions.strSourceLatLng = "";
                    placePredictions.strSourceLatitude = mListLocalPlaces.get(position - cntGooglePlaces).getLatitude() + "";
                    placePredictions.strSourceLongitude = mListLocalPlaces.get(position - cntGooglePlaces).getLongitude() + "";
                    txtaddressSource.setText(placePredictions.strSourceAddress);
                    txtaddressSource.setSelection(0);
                    txtDestination.requestFocus();
                    mAutoCompleteAdapter = null;
                }

                mAutoCompleteList.setVisibility(View.GONE);

                if (txtDestination.getText().toString().length() > 0) {

                    if (strSelected.equalsIgnoreCase("destination")) {
                        if (!placePredictions.strDestAddress.equalsIgnoreCase(placePredictions.strSourceAddress)) {
                            setAddress();
                        } else {
                            Utils.showAlert(thisActivity, thisActivity.getResources().getString(R.string.source_dest_not_same));
                        }
                    }
                } else {
                    txtDestination.requestFocus();
                    txtDestination.setText("");
                    imgDestClose.setVisibility(View.GONE);
                    mAutoCompleteList.setVisibility(View.GONE);
                }
            }
        }
    }

    public String getPlaceAutoCompleteUrl(String input) {
        StringBuilder urlString = new StringBuilder();
        urlString.append("https://maps.googleapis.com/maps/api/place/autocomplete/json");
        urlString.append("?input=");
        try {
            urlString.append(URLEncoder.encode(input, "utf8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        urlString.append("&location=");
        urlString.append(latitude + "," + longitude); // append lat long of current location to show nearby results.
        urlString.append("&radius=500&language=en");
        urlString.append("&key=" + getResources().getString(R.string.google_map_api));

        Log.d("FINAL URL:::   ", urlString.toString());
        return urlString.toString();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        try {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);

            if (mLastLocation != null) {
                latitude = mLastLocation.getLatitude();
                longitude = mLastLocation.getLongitude();
            }

        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    public void fetchLocation() {
        //Build google API client to use fused location
        buildGoogleApiClient();

        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOC: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted!
                    fetchLocation();
                } else {
                    // permission denied!
                    Toast.makeText(this, "Please grant permission for using this app!", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    @Override
    public void onBackPressed() {
        setAddress();
        super.onBackPressed();
    }

    void setAddress() {
        Utils.hideKeyboard(thisActivity);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent();
                if (placePredictions != null) {
                    intent.putExtra("Location Address", placePredictions);
                    intent.putExtra("pick_location", "no");
                    setResult(RESULT_OK, intent);
                } else {
                    setResult(RESULT_CANCELED, intent);
                }
                finish();
            }
        }, 500);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onStop() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    private void gotoHomeWork(String strTag) {

        Toast.makeText(thisActivity, "This is gotoHomeWork()...", Toast.LENGTH_SHORT).show();

//        Intent intentHomeWork = new Intent(GooglePlaceSearchActivity.this, AddHomeWorkActivity.class);
//        intentHomeWork.putExtra("tag", strTag);
//        startActivityForResult(intentHomeWork, UPDATE_HOME_WORK);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UPDATE_HOME_WORK) {
            if (resultCode == Activity.RESULT_OK) {
                getFavoriteLocations();
            }
        }
    }

    private class RecentPlacesAdapter extends RecyclerView.Adapter<RecentPlacesAdapter.MyViewHolder> {
        JSONArray jsonArray;
        ArrayList<RecentAddressData> lstRecentList;

        public RecentPlacesAdapter(JSONArray array, ArrayList<RecentAddressData> lstRecentList) {
            this.jsonArray = array;
            this.lstRecentList = lstRecentList;
        }

        @Override
        public RecentPlacesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.autocomplete_row, parent, false);
            return new RecentPlacesAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(RecentPlacesAdapter.MyViewHolder holder, int position) {
            String[] name = lstRecentList.get(position).address.split(",");
            if (name.length > 0) {
                holder.name.setText(name[0]);
            } else {
                holder.name.setText(lstRecentList.get(position).address);
            }
            holder.location.setText(lstRecentList.get(position).address);

            holder.imgRecent.setImageResource(R.drawable.recent_search);

            holder.lnrLocation.setTag(position);

            holder.lnrLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = Integer.parseInt(view.getTag().toString());
                    if (strSelected.equalsIgnoreCase("destination")) {
                        placePredictions.strDestAddress = lstRecentList.get(position).address;
                        placePredictions.strDestLatitude = lstRecentList.get(position).latitude + "";
                        placePredictions.strDestLongitude = lstRecentList.get(position).longitude + "";
                        LatLng latlng = new LatLng(Double.parseDouble(placePredictions.strDestLatitude), Double.parseDouble(placePredictions.strDestLatitude));
                        placePredictions.strDestLatLng = "" + latlng;
                        txtDestination.setText(lstRecentList.get(position).address);
                        txtDestination.setSelection(0);
                    } else {
                        placePredictions.strSourceAddress = lstRecentList.get(position).address;
                        placePredictions.strSourceLatitude = lstRecentList.get(position).latitude + "";
                        placePredictions.strSourceLongitude = lstRecentList.get(position).longitude + "";
                        LatLng latlng = new LatLng(Double.parseDouble(placePredictions.strSourceLatitude), Double.parseDouble(placePredictions.strSourceLongitude));
                        placePredictions.strSourceLatLng = "" + latlng;
                        txtaddressSource.setText(placePredictions.strSourceAddress);
                        txtaddressSource.setSelection(0);
                        txtDestination.requestFocus();
                        mAutoCompleteAdapter = null;
                    }
                    setAddress();
                }
            });
        }

        @Override
        public int getItemCount() {
            return lstRecentList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            TextView name, location;

            LinearLayout lnrLocation;

            ImageView imgRecent;

            public MyViewHolder(View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.place_name);
                location = itemView.findViewById(R.id.place_detail);
                lnrLocation = itemView.findViewById(R.id.lnrLocation);
                imgRecent =  itemView.findViewById(R.id.imgRecent);
            }
        }
    }

}
