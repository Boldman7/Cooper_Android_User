package com.boldman.cooperuser.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.boldman.cooperuser.Model.LocalPlace;
import com.boldman.cooperuser.R;

public class ExplorePlaceDetailFragment extends Fragment {
    private static final String ARG_PLACE_ID = "place_id";
    private static final String ARG_PLACE_NAME = "name";
    private static final String ARG_PLACE_LATITUDE = "latitude";
    private static final String ARG_PLACE_LONGITUDE = "longitude";
    private static final String ARG_PLACE_ADDRESS = "address";
    private static final String ARG_PLACE_WEBSITE = "btnWebsite";

    private String placeId, name, latitude, longitude, address, website_url;
    private ImageView placeImage;
    private TextView tVAddress, tVWebSite;
    private TextView title;
    private Button btnWebsite, btnCooperMeHere;

    public static ExplorePlaceDetailFragment newInstance() {
        return new ExplorePlaceDetailFragment();
    }

    public static ExplorePlaceDetailFragment newInstance(LocalPlace localPlace) {
        ExplorePlaceDetailFragment fragment = new ExplorePlaceDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PLACE_ID, "1");
        args.putString(ARG_PLACE_NAME, localPlace.getName());
        args.putString(ARG_PLACE_LATITUDE, localPlace.getLatitude() + "");
        args.putString(ARG_PLACE_LONGITUDE, localPlace.getLongitude() + "");
        args.putString(ARG_PLACE_ADDRESS, localPlace.getAddress());
        args.putString(ARG_PLACE_WEBSITE, localPlace.getWebsite());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            placeId = getArguments().getString(ARG_PLACE_ID);
            name = getArguments().getString(ARG_PLACE_NAME);
            latitude = getArguments().getString(ARG_PLACE_LATITUDE);
            longitude = getArguments().getString(ARG_PLACE_LONGITUDE);
            address = getArguments().getString(ARG_PLACE_ADDRESS);
            website_url = getArguments().getString(ARG_PLACE_WEBSITE);
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_explore_place_detail, container, false);

        ImageView back = view.findViewById(R.id.back_explore_place_detail);
        title = view.findViewById(R.id.title_explore_place_detail);
        placeImage = view.findViewById(R.id.explore_place_detail_place_image);
        btnCooperMeHere = view.findViewById(R.id.cooper_me_here);
        tVAddress = view.findViewById(R.id.explore_place_detail_place_address);
        tVWebSite = view.findViewById(R.id.explore_place_detail_visit_website_text);
        btnWebsite = view.findViewById(R.id.explore_place_detail_visit_website_url);

        title.setText(name);

        tVAddress.setText(address);

        if(website_url != null){
            btnWebsite.setVisibility(View.VISIBLE);
            btnWebsite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(website_url));
                    startActivity(i);
                }
            });

        }else{
            tVWebSite.setText("Website Not Available.");
            btnWebsite.setVisibility(View.GONE);
        }

//        if(jsonObject.has("photos")){
//            String photoReference = jsonObject.getJSONArray(  "photos").getJSONObject(0).getString("photo_reference");
//            final String photoUrl = UrlHelper.GOOGLE_GET_PLACE_PHOTO(400,photoReference,getContext().getResources().getString(R.string.google_map_api));
//            Picasso.with(getContext()).load(photoUrl).into(placeImage);
//        }else{
//            placeImage.setVisibility(View.GONE);
//        }


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStackImmediate();
            }
        });

        btnCooperMeHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent resultIntent = new Intent();
                resultIntent.putExtra("explore_address", "SUCCESS!");
                resultIntent.putExtra("dest_lat", latitude);
                resultIntent.putExtra("dest_lng", longitude);
                resultIntent.putExtra("dest_address", address);

                getActivity().setResult(Activity.RESULT_OK, resultIntent);
                getActivity().finish();

            }
        });

        return view;
    }

}
