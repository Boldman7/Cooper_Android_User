package com.boldman.cooperuser.Fragments;

import android.content.Context;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.boldman.cooperuser.Api.ApiClient;
import com.boldman.cooperuser.Api.ApiInterface;
import com.boldman.cooperuser.Helper.SharedHelper;
import com.boldman.cooperuser.Helper.UrlHelper;
import com.boldman.cooperuser.Model.LocalPlace;
import com.boldman.cooperuser.R;
import com.boldman.cooperuser.Utils.Utils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;


public class ExploreNearByFragment extends Fragment {
    private static final String ARG_TITLE = "title";
    private static final String ARG_TYPE = "type";
    private static final String ARG_LATITUDE = "latitude";
    private static final String ARG_LONGITUDE = "longitude";

    private String title, type, latitude, longitude;
    private RelativeLayout firstResultLayout, secondResultLayout, thirdResultLayout, resultsLayout;
    private ImageView firstResultImage, secondResultImage, thirdResultImage;
    private TextView firstResultTitle, firstResultDescription, secondResultTitle, secondResultDescription, thirdResultTitle, thirdResultDescription, noResultsText;
    private View viewTwo, viewThree;

    String placeIdOne = null, placeIdTwo = null, placeIdThree = null, nameOne = null, nameTwo = null, nameThree = null;
    String imageUrlOne = null, websiteUrlOne = null;
    String imageUrlTwo = null, websiteUrlTwo = null;
    String imageUrlThree = null, websiteUrlThree = null;

    ProgressBar progressBar;
    List<LocalPlace> mListLocalPlaces = new ArrayList<>();

    private OnFragmentInteractionListener mListener;

    public static ExploreNearByFragment newInstance() {
        return new ExploreNearByFragment();
    }

    public static ExploreNearByFragment newInstance(String title, String type, String latitude, String longitude) {
        ExploreNearByFragment fragment = new ExploreNearByFragment();
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
        View view = inflater.inflate(R.layout.fragment_explore_nearby, container, false);

        TextView titlePage = view.findViewById(R.id.title_nearby);
        TextView viewAll = view.findViewById(R.id.view_all_nearby);

        resultsLayout = view.findViewById(R.id.explore_nearby_results_layout);
        progressBar = view.findViewById(R.id.progress_bar_explore_nearby);
        noResultsText = view.findViewById(R.id.no_results_text);
        firstResultLayout = view.findViewById(R.id.explore_places_layout_one);
        firstResultImage = view.findViewById(R.id.explore_places_image_one);
        firstResultTitle = view.findViewById(R.id.explore_places_place_name_one);
        firstResultDescription = view.findViewById(R.id.explore_places_description_one);

        secondResultLayout = view.findViewById(R.id.explore_places_layout_two);
        secondResultImage = view.findViewById(R.id.explore_places_image_two);
        secondResultTitle = view.findViewById(R.id.explore_places_place_name_two);
        secondResultDescription = view.findViewById(R.id.explore_places_description_two);
        viewTwo = view.findViewById(R.id.view_three);

        thirdResultLayout = view.findViewById(R.id.explore_places_layout_three);
        thirdResultImage = view.findViewById(R.id.explore_places_image_three);
        thirdResultTitle = view.findViewById(R.id.explore_places_place_name_three);
        thirdResultDescription = view.findViewById(R.id.explore_places_description_three);
        viewThree = view.findViewById(R.id.view_four);
        titlePage.setText(title);

        viewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  Toast.makeText(getContext(),"View All "+type, Toast.LENGTH_LONG).show();
                viewAllPressed(title, type, latitude, longitude);
            }
        });

        firstResultLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nameOne != null) {
                    placePressed(0);
                }
            }
        });

        secondResultLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nameTwo!= null) {
                    placePressed(1);
                }
            }
        });

        thirdResultLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nameThree != null) {
                    placePressed(2);
                }
            }
        });

        getSearchResults(type);

        return view;
    }

    private void viewAllPressed(String title, String type, String latitude, String longitude) {
        if (mListener != null) {
            mListener.viewAllPressed(title, type, latitude, longitude);
        }
    }

    public void getSearchResults(String keywords) {
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

                                resultsLayout.setVisibility(View.VISIBLE);
                                firstResultLayout.setVisibility(View.VISIBLE);
                                nameOne = mListLocalPlaces.get(0).getName();
                                firstResultTitle.setText(nameOne);
                                firstResultDescription.setText(mListLocalPlaces.get(0).getAddress());
                                imageUrlOne = mListLocalPlaces.get(0).getImage();
                                websiteUrlOne = mListLocalPlaces.get(0).getWebsite();
//                                placeIdOne = mListLocalPlaces.get(0).getString("place_id");

                                //getPhoto(GlobalConstants. mListLocalPlaces.get(0).getImage(), "first");

                                if (mListLocalPlaces.size() > 1) {

                                    secondResultLayout.setVisibility(View.VISIBLE);
                                    viewTwo.setVisibility(View.VISIBLE);

                                    nameTwo = mListLocalPlaces.get(1).getName();
                                    secondResultDescription.setText(mListLocalPlaces.get(1).getAddress());
                                    imageUrlTwo = mListLocalPlaces.get(1).getImage();
                                    websiteUrlTwo = mListLocalPlaces.get(1).getWebsite();

                                    secondResultTitle.setText(nameTwo);
                                    //getPhoto(jsonArray.getJSONObject(1).getJSONArray("photos").getJSONObject(0).getString("photo_reference"), "second");
                                    //placeIdTwo = jsonArray.getJSONObject(1).getString("place_id");
                                }

                                if (mListLocalPlaces.size() > 2) {
                                    thirdResultLayout.setVisibility(View.VISIBLE);
                                    viewThree.setVisibility(View.VISIBLE);
                                    nameThree = mListLocalPlaces.get(1).getName();
                                    thirdResultTitle.setText(nameThree);
                                    thirdResultDescription.setText(mListLocalPlaces.get(2).getAddress());
                                    imageUrlThree = mListLocalPlaces.get(2).getImage();
                                    websiteUrlThree = mListLocalPlaces.get(2).getWebsite();
                                    //getPhoto(jsonArray.getJSONObject(2).getJSONArray("photos").getJSONObject(0).getString("photo_reference"), "third");
                                    //placeIdThree = jsonArray.getJSONObject(2).getString("place_id");
                                }
                            } else{
                                resultsLayout.setVisibility(View.GONE);
                                noResultsText.setVisibility(View.VISIBLE);
                            }

                        } else{
                            JSONObject data = object.getJSONObject("data");
                            Utils.displayMessage(getActivity(), Utils.parseErrorMessage(data));
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

    public void getPhoto(final String photoReference, final String requestNumber) {
        try {
            final String photoUrl = UrlHelper.GOOGLE_GET_PLACE_PHOTO(400, photoReference, getString(R.string.google_map_api));
            if (requestNumber.equalsIgnoreCase("first")) {
                Picasso.with(getContext()).load(photoUrl).placeholder(R.drawable.loading).into(firstResultImage);
            } else if (requestNumber.equalsIgnoreCase("second")) {
                Picasso.with(getContext()).load(photoUrl).placeholder(R.drawable.loading).into(secondResultImage);
            } else if (requestNumber.equalsIgnoreCase("third")) {
                Picasso.with(getContext()).load(photoUrl).placeholder(R.drawable.loading).into(thirdResultImage);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void placePressed(int id) {
        if (mListener != null) {
            mListener.placeFromExplorePressed(mListLocalPlaces.get(id));
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
        void viewAllPressed(String title, String type, String latitude, String longitude);

        void placeFromExplorePressed(LocalPlace mListLocalPlace);
    }
}
