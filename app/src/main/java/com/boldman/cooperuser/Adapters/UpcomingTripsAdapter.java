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
import com.boldman.cooperuser.Model.BookingRides;
import com.boldman.cooperuser.Model.YourTrips;
import com.boldman.cooperuser.R;
import com.boldman.cooperuser.Utils.DateUtil;

import java.util.List;

public class UpcomingTripsAdapter extends ArrayAdapter<BookingRides> {

    Context context;
    List<BookingRides> list;
    IUpcomingTripsAEventListener mListener;

    public interface IUpcomingTripsAEventListener {
        void onViewDetail(BookingRides selectedUpcomingTrip);
    }

    public UpcomingTripsAdapter(Context context) {
        super(context, 0);
    }

    public UpcomingTripsAdapter(Context context, List<BookingRides> list, IUpcomingTripsAEventListener listener){

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

        BookingRides bookingRide = getItem(position);

        holder.tripId.setText("coOper-" + bookingRide.getId());
        try {
            holder.tripDateAndTime.setText(DateUtil.convertDateToString24HoursFormat(bookingRide.getBook_at()));
        } catch (Exception e){
            holder.tripDateAndTime.setText("");
            e.printStackTrace();
        }

        if (bookingRide.getStatus() != 1)
            holder.tripCost.setText("$" + bookingRide.getPay_amount());
        else {
            if (bookingRide.getCancel_by().equalsIgnoreCase("user"))
                holder.tripCost.setText("Cancelled by User");
            else
                holder.tripCost.setText("Cancelled by Driver");
        }

        holder.tripView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                UpcomingTripsAdapter.this.mListener.onViewDetail(list.get(position));
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
    public BookingRides getItem(int position) {
        return super.getItem(position);
    }


}
