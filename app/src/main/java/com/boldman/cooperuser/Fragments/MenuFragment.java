package com.boldman.cooperuser.Fragments;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.boldman.cooperuser.Helper.SharedHelper;
import com.boldman.cooperuser.R;
import com.boldman.cooperuser.Utils.CircleTransformImage;
import com.boldman.cooperuser.Utils.GlobalConstants;
import com.squareup.picasso.Picasso;

/*
 *   MenuFragment
 *   Created by Boldman. 2019.05.18
 * */

public class MenuFragment extends Fragment {
    private static final String ARG_LATITUDE = "latitude";
    private static final String ARG_LONGITUDE = "longitude";

    Double latitude,longitude;
    private static final String TAG = "MenuFragment";
    private ImageView userImage,cancelImage;
    private TextView userName,payment,yourTrips,explore,freeRides,wallet,passbook,help,settings,logout,versionName;

    private OnFragmentInteractionListener mListener;
    public static MenuFragment newInstance() {
        return new MenuFragment();
    }

    public static Fragment newInstance(double latitude, double longitude) {
        MenuFragment fragment = new MenuFragment();
        Bundle args = new Bundle();
        args.putDouble(ARG_LATITUDE, latitude);
        args.putDouble(ARG_LONGITUDE, longitude);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            latitude = getArguments().getDouble(ARG_LATITUDE);
            longitude =  getArguments().getDouble(ARG_LONGITUDE);
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        userImage = view.findViewById(R.id.user_image_menu);
        cancelImage = view.findViewById(R.id.cancel_image_menu);
        userName = view.findViewById(R.id.user_name_menu);
        payment = view.findViewById(R.id.payment_menu);
        yourTrips = view.findViewById(R.id.your_trips_menu);
        explore = view.findViewById(R.id.explore_menu);
        freeRides = view.findViewById(R.id.free_rides_menu);
        wallet = view.findViewById(R.id.wallet_menu);
        passbook = view.findViewById(R.id.passbook_menu);
        help = view.findViewById(R.id.help_menu);
        settings = view.findViewById(R.id.settings_menu);
        logout = view.findViewById(R.id.logout_menu);
        versionName = view.findViewById(R.id.version_menu);

        userName.setText(SharedHelper.getKey(getContext(), "access_email"));
        try {
            PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            String version = pInfo.versionName;
            versionName.setText("V"+version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        // Loading profile image
         if (!SharedHelper.getKey(getContext(),"avatar").equalsIgnoreCase("")
                && !SharedHelper.getKey(getContext(),"avatar").equalsIgnoreCase(null) && SharedHelper.getKey(getContext(),"avatar") != null) {
            userImage.setBackground(null);
            if (SharedHelper.getKey(getContext(), "avatar").startsWith("http"))
                Picasso.with(getContext()).load(SharedHelper.getKey(getContext(), "avatar"))
                        .placeholder(R.drawable.user_default_sidebar)
                        .transform(new CircleTransformImage())
                        .error(R.drawable.user_default_sidebar)
                        .into(userImage);
            else
                Picasso.with(getContext()).load(GlobalConstants.SERVER_HTTP_URL + "/storage/user/avatar/" + SharedHelper.getKey(getContext(), "avatar"))
                        .placeholder(R.drawable.user_default_sidebar)
                        .transform(new CircleTransformImage())
                        .error(R.drawable.user_default_sidebar)
                        .into(userImage);
        }else {
            Picasso.with(getContext()).load(R.drawable.user_default_sidebar)
                    .placeholder(R.drawable.user_default_sidebar)
                    .error(R.drawable.user_default_sidebar)
                    .transform(new CircleTransformImage())
                    .into(userImage);
        }

        cancelImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStackImmediate();
            }
        });

        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mListener != null){
                    mListener.menuImagePressed();
                }
            }
        });

        payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mListener != null){
                    mListener.paymentMenuPressed();
                }
            }
        });

        passbook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mListener != null){
                    mListener.passbookMenuPressed();
                }
            }
        });
        yourTrips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mListener != null){
                    mListener.yourTripsMenuPressed();
                }
            }
        });

        explore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mListener != null){
                    mListener.exploreMenuPressed(latitude,longitude);
                }
            }
        });

        freeRides.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mListener != null){
                    mListener.freeRidesMenuPressed();
                }
            }
        });

        wallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mListener != null){
                    mListener.walletMenuPressed();
                }
            }
        });

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mListener != null){
                    mListener.helpMenuPressed();
                }
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mListener != null){
                    mListener.settingsMenuPressed();
                }
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mListener != null){
                    mListener.logoutMenuPressed();
                }
            }
        });


        return view;
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
        void paymentMenuPressed();
        void yourTripsMenuPressed();
        void exploreMenuPressed(double latitude, double longitude);
        void freeRidesMenuPressed();
        void helpMenuPressed();
        void settingsMenuPressed();
        void logoutMenuPressed();
        void menuImagePressed();
        void walletMenuPressed();
        void passbookMenuPressed();
    }

}
