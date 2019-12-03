package com.boldman.cooperuser.Activities.Service;


import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.boldman.cooperuser.Fragments.ExploreNearByFragment;
import com.boldman.cooperuser.Fragments.ExplorePlaceDetailFragment;
import com.boldman.cooperuser.Fragments.ViewAllFragment;
import com.boldman.cooperuser.Model.LocalPlace;
import com.boldman.cooperuser.R;


public class ExploreActivity extends AppCompatActivity implements ExploreNearByFragment.OnFragmentInteractionListener,
        ViewAllFragment.OnFragmentInteractionListener{

    Double latitude,longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_explore);

        Bundle getBundle = this.getIntent().getExtras();
        if (getBundle != null) {
            latitude = getBundle.getDouble("latitude");
            longitude = getBundle.getDouble("longitude");
        }

        ImageView close = findViewById(R.id.closeExplore);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction =
                fragmentManager.beginTransaction();

        ExploreNearByFragment fragment_one =  ExploreNearByFragment.newInstance("Restaurants Near Me","restaurant", String.valueOf(latitude), String.valueOf(longitude));
        ExploreNearByFragment fragment_two = ExploreNearByFragment.newInstance("Attractions Near Me","attractions", String.valueOf(latitude), String.valueOf(longitude));
        ExploreNearByFragment fragment_three = ExploreNearByFragment.newInstance("Shopping Near Me","shopping", String.valueOf(latitude), String.valueOf(longitude));
        ExploreNearByFragment fragment_four = ExploreNearByFragment.newInstance("Health And Medical","health+medical", String.valueOf(latitude), String.valueOf(longitude));
        ExploreNearByFragment fragment_five = ExploreNearByFragment.newInstance("Airports Near Me","airport", String.valueOf(latitude), String.valueOf(longitude));
        ExploreNearByFragment fragment_six = ExploreNearByFragment.newInstance("Beaches Near Me","beach", String.valueOf(latitude), String.valueOf(longitude));
        ExploreNearByFragment fragment_seven = ExploreNearByFragment.newInstance("Hotels Near Me","hotel", String.valueOf(latitude), String.valueOf(longitude));
        ExploreNearByFragment fragment_eight= ExploreNearByFragment.newInstance("Historical Sites Near Me","historical%20sites", String.valueOf(latitude), String.valueOf(longitude));
        ExploreNearByFragment fragment_nine = ExploreNearByFragment.newInstance("Clubs Near Me","club", String.valueOf(latitude), String.valueOf(longitude));
        ExploreNearByFragment fragment_ten = ExploreNearByFragment.newInstance("Government Offices","government%20office", String.valueOf(latitude), String.valueOf(longitude));
        ExploreNearByFragment fragment_eleven = ExploreNearByFragment.newInstance("Kids Activities Near Me","kids%20activities", String.valueOf(latitude), String.valueOf(longitude));
        ExploreNearByFragment fragment_twelve = ExploreNearByFragment.newInstance("Water ACTIVITIES Near Me","water%20activities", String.valueOf(latitude), String.valueOf(longitude));
        ExploreNearByFragment fragment_thirteen = ExploreNearByFragment.newInstance("Music And Entertainment","music%20and%20entertainment", String.valueOf(latitude), String.valueOf(longitude));
        ExploreNearByFragment fragment_fourteen = ExploreNearByFragment.newInstance("Cultural Events Near Me","cultural%20events", String.valueOf(latitude), String.valueOf(longitude));


        fragmentTransaction.replace(R.id.containerView1, fragment_one);
        fragmentTransaction.replace(R.id.containerView2, fragment_two);
        fragmentTransaction.replace(R.id.containerView3, fragment_three);
        fragmentTransaction.replace(R.id.containerView4, fragment_four);
        fragmentTransaction.replace(R.id.containerView5, fragment_five);
        fragmentTransaction.replace(R.id.containerView6, fragment_six);
        fragmentTransaction.replace(R.id.containerView7, fragment_seven);
        fragmentTransaction.replace(R.id.containerView8, fragment_eight);
        fragmentTransaction.replace(R.id.containerView9, fragment_nine);
        fragmentTransaction.replace(R.id.containerView10, fragment_ten);
        fragmentTransaction.replace(R.id.containerView11, fragment_eleven);
        fragmentTransaction.replace(R.id.containerView12, fragment_twelve);
        fragmentTransaction.replace(R.id.containerView13, fragment_thirteen);
        fragmentTransaction.replace(R.id.containerView14, fragment_fourteen);

        fragmentTransaction.commit();
    }

    public void addNewFragment(Fragment fragment, String tag) {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container_explore_activity, fragment, tag)
                .addToBackStack(tag)
                .commit();
    }

    @Override
    public void viewAllPressed(String title, String type, String latitude, String longitude) {
        addNewFragment(ViewAllFragment.newInstance(title,type,latitude,longitude),"VIEW_ALL_FRAGMENT");
    }

    @Override
    public void placeFromExplorePressed(LocalPlace mLocalPlace) {

        addNewFragment(ExplorePlaceDetailFragment.newInstance(mLocalPlace),"EXPLORE_PLACE_DETAIL_FRAGMENT");
    }

    @Override
    public void localPlacePressed(LocalPlace mLocalPlace) {

        addNewFragment(ExplorePlaceDetailFragment.newInstance(mLocalPlace),"EXPLORE_PLACE_DETAIL_FRAGMENT");
    }
}
