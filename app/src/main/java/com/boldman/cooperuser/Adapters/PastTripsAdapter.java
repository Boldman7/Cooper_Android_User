package com.boldman.cooperuser.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.boldman.cooperuser.Helper.SharedHelper;
import com.boldman.cooperuser.Model.YourTrips;
import com.boldman.cooperuser.R;
import com.boldman.cooperuser.Utils.DateUtil;

import java.util.List;

public class PastTripsAdapter extends ArrayAdapter<YourTrips> {

    Context context;
    List<YourTrips> list;
    IPastTripsAEventListener mListener;

    public interface IPastTripsAEventListener {
        void onViewDetail(YourTrips selectedPastTrip);
    }

    public PastTripsAdapter(Context context) {
        super(context, 0);
    }

    public PastTripsAdapter(Context context, List<YourTrips> list, IPastTripsAEventListener listener){

        super(context, R.layout.upcoming_trips_list_item, list);
        this.context=context;
        this.list=list;
        this.mListener = listener;
    }

    @Override
    public View getView(int position, View contentView, ViewGroup parent) {
        ViewHolder holder;

        if (contentView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            contentView = inflater.inflate(R.layout.upcoming_trips_list_item, parent, false);
            holder = new ViewHolder(contentView);
            contentView.setTag(holder);
        } else {
            holder = (ViewHolder) contentView.getTag();
        }

        YourTrips yourTrips = getItem(position);

        holder.tripId.setText("coOper-" + yourTrips.getId());
        holder.tripDateAndTime.setText(!yourTrips.getFinish_at().equalsIgnoreCase("null") ? DateUtil.convertDateToString24HoursFormat(yourTrips.getFinish_at()) : "");
        holder.tripCost.setText("$" + yourTrips.getPay_amount());

        holder.tripView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                PastTripsAdapter.this.mListener.onViewDetail(list.get(position));
            }});

        return contentView;
    }

    private static class ViewHolder {
        TextView tripDateAndTime, tripCost, tripView, tripId;
        RelativeLayout timeAndCostLayout;

        public ViewHolder(View view) {
            tripDateAndTime = view.findViewById(R.id.tv_datetime);
            tripCost = view.findViewById(R.id.tv_price);
            tripView = view.findViewById(R.id.tv_view);
            tripId = view.findViewById(R.id.tv_id);
        }
    }

    @Override
    public int getViewTypeCount() {
        return super.getViewTypeCount();
    }

    @Nullable
    @Override
    public YourTrips getItem(int position) {
        return super.getItem(position);
    }


}