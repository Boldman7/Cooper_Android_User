package com.boldman.cooperuser.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;

import com.boldman.cooperuser.Model.ServiceType;
import com.boldman.cooperuser.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ServiceTypePagerAdapter extends PagerAdapter {

    // this adapter is used in BrowseSearchFrag
    Fragment fragment;
    List<ServiceType> serviceTypeList;
    int adapterType;
    Context context;

    public ServiceTypePagerAdapter(Fragment fragment, List<ServiceType> list, int adapterType) {

        this.fragment = fragment;
        this.serviceTypeList = list;
        this.adapterType = adapterType;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        ServiceType serviceType = serviceTypeList.get(position);

        final View view = LayoutInflater.from(fragment.getContext()).inflate(R.layout.layout_pager_service_type, null);
        view.setTag(position);
        view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Log.i("PagerAdapter", "Boldman-------->PagerAdapter------------>");
            }
        });

        try {

            String serviceName = serviceType.getName();

//            if (serviceName.equalsIgnoreCase("Cooper Cool"))
//                ((TextView) view.findViewById(R.id.serviceTitleItem)).setText("Economy");
//            else if (serviceName.equalsIgnoreCase("Cooper Elite"))
//                ((TextView) view.findViewById(R.id.serviceTitleItem)).setText("Premium");
//            else
//                ((TextView) view.findViewById(R.id.serviceTitleItem)).setText(serviceName);

            ((TextView) view.findViewById(R.id.serviceTitleItem)).setText(serviceName);

            ((TextView) view.findViewById(R.id.serviceDescriptionItem)).setText(serviceType.getDriverName());
//            Glide.with(fragment).load(serviceType.getImage())
//                    .placeholder(R.drawable.car_select).dontAnimate().error(R.drawable.car_select).into((ImageView)view.findViewById(R.id.serviceImg));

            Picasso.with(fragment.getContext()).load(serviceType.getImage()).placeholder(R.drawable.car_select).error(R.drawable.car_select).into((ImageView)view.findViewById(R.id.serviceImg));

            ((TextView) view.findViewById(R.id.servicePriceItem)).setText("$" + String.valueOf(serviceType.getPrice()));
            ((TextView) view.findViewById(R.id.tv_capacity)).setText("1-" + serviceType.getCapacity());
            container.addView(view);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return serviceTypeList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }

}