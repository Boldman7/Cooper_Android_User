package com.boldman.cooperuser.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.boldman.cooperuser.Activities.Trips.UpcomingTripDetailActivity;
import com.boldman.cooperuser.Adapters.UpcomingTripsAdapter;
import com.boldman.cooperuser.Api.ApiClient;
import com.boldman.cooperuser.Api.ApiInterface;
import com.boldman.cooperuser.Helper.ConnectionHelper;
import com.boldman.cooperuser.Helper.CustomDialog;
import com.boldman.cooperuser.Model.BookingRides;
import com.boldman.cooperuser.Model.YourTrips;
import com.boldman.cooperuser.R;
import com.boldman.cooperuser.Utils.GlobalConstants;
import com.boldman.cooperuser.Utils.Utils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpcomingTrips extends Fragment {

    Boolean isInternet;
    UpcomingTripsAdapter upcomingTripsAdapter;
    private ListView listView;
    RelativeLayout errorLayout;
    ConnectionHelper helper;
    CustomDialog customDialog;
    View rootView;

    List<BookingRides> bookingRideList;

    public UpcomingTrips() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        rootView = inflater.inflate(R.layout.fragment_upcoming_trips, container, false);
        init();

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
    }

    public void getUpcomingRideList() {

        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        JsonObject gsonObject = new JsonObject();
        try {
            JSONObject paramObject = new JSONObject();

            paramObject.put("api_token", GlobalConstants.g_api_token);

            JsonParser jsonParser = new JsonParser();
            gsonObject = (JsonObject) jsonParser.parse(paramObject.toString());

            apiInterface.doBookingRides(gsonObject).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        JSONObject data = object.getJSONObject("data");

                        if (object.getString("status").equals("1")) {

                            JSONArray rides = data.getJSONArray("rides");

                            bookingRideList = new Gson().fromJson(rides.toString(), new TypeToken<List<BookingRides>>() {
                            }.getType());

                            upcomingTripsAdapter = new UpcomingTripsAdapter(getContext(), bookingRideList,
                                    selectedTrip -> {

                                        Intent intent = new Intent(getContext(), UpcomingTripDetailActivity.class);
                                        intent.putExtra("trip", selectedTrip);
                                        startActivity(intent);

                                    });

                            if (bookingRideList.size() > 0) {
                                listView.setVisibility(View.VISIBLE);
                                errorLayout.setVisibility(View.GONE);
                                listView.setAdapter(upcomingTripsAdapter);
                                Log.wtf("TRIP NUMBER ~~~ ", upcomingTripsAdapter.getCount() + "");
                            } else {
                                listView.setVisibility(View.GONE);
                                errorLayout.setVisibility(View.VISIBLE);
                            }

                        } else {

                            errorLayout.setVisibility(View.VISIBLE);
                            listView.setVisibility(View.GONE);
                            Utils.displayMessage(getActivity(), Utils.parseErrorMessage(data));
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "Server connect error", Toast.LENGTH_SHORT).show();
                    }

                    progressDialog.dismiss();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    progressDialog.dismiss();
                    t.printStackTrace();
                    Toast.makeText(getActivity(), "Server connect error", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void init() {
        // recyclerView = rootView.findViewById(R.id.recyclerView);
        listView = rootView.findViewById(R.id.lv_upcoming);
        errorLayout = rootView.findViewById(R.id.errorLayout);
        errorLayout.setVisibility(View.GONE);
        helper = new ConnectionHelper(getActivity());
        isInternet = helper.isConnectingToInternet();

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onResume(){
        super.onResume();

        Log.i("XXXXXXXXXXXXXXXXX", "UpcomingTripsOnResume");

        getUpcomingRideList();

    }

}
