package com.boldman.cooperuser.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.boldman.cooperuser.Adapters.ExploreViewAllAdapter;
import com.boldman.cooperuser.Api.ApiClient;
import com.boldman.cooperuser.Api.ApiInterface;
import com.boldman.cooperuser.Helper.SharedHelper;
import com.boldman.cooperuser.Model.LocalPlace;
import com.boldman.cooperuser.R;
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

public class ViewAllFragment extends Fragment implements ExploreViewAllAdapter.ItemPressed{
    private static final String ARG_TITLE = "title";
    private static final String ARG_TYPE = "type";
    private static final String ARG_LATITUDE = "latitude";
    private static final String ARG_LONGITUDE = "longitude";

    private String title, type, latitude, longitude;
    ProgressBar progressBar;
    TextView noResultsText;
    RecyclerView viewAllRecyclerview;

    ExploreViewAllAdapter exploreViewAllAdapter;
    List<LocalPlace> mListLocalPlaces;

    private OnFragmentInteractionListener mListener;
    public static ViewAllFragment newInstance() {
        return new ViewAllFragment();
    }

    public static ViewAllFragment newInstance(String title, String type, String latitude, String longitude) {
        ViewAllFragment fragment = new ViewAllFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_TYPE, type);
        args.putString(ARG_LATITUDE, latitude);
        args.putString(ARG_LONGITUDE, longitude);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString(ARG_TITLE);
            type = getArguments().getString(ARG_TYPE);
            latitude = getArguments().getString(ARG_LATITUDE);
            longitude = getArguments().getString(ARG_LONGITUDE);
        }
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_all, container, false);

        TextView titlePage = view.findViewById(R.id.title_view_all);
        ImageView back = view.findViewById(R.id.back_view_all);
        viewAllRecyclerview = view.findViewById(R.id.view_all_recycler_view);
        progressBar = view.findViewById(R.id.progress_bar_view_all);
        noResultsText = view.findViewById(R.id.no_results_txt_view_all);

        titlePage.setText(title);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStackImmediate();
            }
        });

        getSearchResults(type);

        return view;

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        mListLocalPlaces = new ArrayList<>();

        exploreViewAllAdapter = new ExploreViewAllAdapter(mListLocalPlaces);
        exploreViewAllAdapter.localPlacePressed = this;
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        viewAllRecyclerview.setLayoutManager(mLayoutManager);
        viewAllRecyclerview.setItemAnimator(new DefaultItemAnimator());
        viewAllRecyclerview.setAdapter(exploreViewAllAdapter);

        //getSearchResults(type);
        super.onViewCreated(view, savedInstanceState);
    }

    public void getSearchResults(String keywords){

        progressBar.setVisibility(View.VISIBLE);

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        JsonObject gsonObject = new JsonObject();
        try {
            JSONObject paramObject = new JSONObject();

            paramObject.put("api_token", SharedHelper.getKey(getActivity(), "api_token"));
            paramObject.put("search_key", keywords);

            JsonParser jsonParser = new JsonParser();
            gsonObject = (JsonObject) jsonParser.parse(paramObject.toString());

            apiInterface.doSearchLocalPlace(gsonObject).enqueue(new Callback<ResponseBody>() {

                @Override
                public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                    try {

                        progressBar.setVisibility(View.GONE);
                        JSONObject object = new JSONObject(response.body().string());

                        if (object.getString("status").equals("1")){

                            JSONObject data = object.getJSONObject("data");
                            JSONArray places = data.getJSONArray("places");

                            mListLocalPlaces = new Gson().fromJson(places.toString(), new TypeToken<List<LocalPlace>>() {
                            }.getType());

                            if (mListLocalPlaces.size() > 0){
                                noResultsText.setVisibility(View.GONE);
                                exploreViewAllAdapter.updateList(mListLocalPlaces);
                            } else{
                                noResultsText.setVisibility(View.VISIBLE);
                            }

                        } else{
                            JSONObject data = object.getJSONObject("data");
                            Utils.displayMessage(getActivity(), data.getString("message"));
                        }
                    } catch (Exception e) {

                        progressBar.setVisibility(View.GONE);
                        noResultsText.setVisibility(View.VISIBLE);
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                    progressBar.setVisibility(View.GONE);
                    t.printStackTrace();
                }
            });

        } catch (JSONException e) {
            progressBar.setVisibility(View.GONE);
            e.printStackTrace();
        }

    }

    @Override
    public void localPlacePressed(LocalPlace localPlace) {
        if(mListener != null){
            mListener.localPlacePressed(localPlace);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        void localPlacePressed(LocalPlace localPlace);
    }
}
