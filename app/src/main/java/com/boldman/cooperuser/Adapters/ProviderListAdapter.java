package com.boldman.cooperuser.Adapters;

/**
 * ProviderList
 * Created by Boldman
 * 2019.06.06
 */

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.boldman.cooperuser.Model.CardDetails;
import com.boldman.cooperuser.Model.DriverInfo;
import com.boldman.cooperuser.R;
import com.boldman.cooperuser.Utils.GlobalConstants;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ProviderListAdapter extends ArrayAdapter<DriverInfo> {

    Context context;
    List<DriverInfo> list;
    IProviderListAEventListener mListener;

    public interface IProviderListAEventListener {
        void onProviderRequest(String strProviderEmail);
    }

    public ProviderListAdapter(Context context, List<DriverInfo> list){
        super(context, R.layout.provider_list_item, list);
        this.context=context;
        this.list=list;
    }

    public ProviderListAdapter(Context context, List<DriverInfo> list, IProviderListAEventListener listener){
        super(context, R.layout.provider_list_item, list);
        this.context=context;
        this.list=list;
        this.mListener = listener;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(R.layout.provider_list_item, parent, false);

        ImageView imgAvatar = itemView.findViewById(R.id.imgAvatar);
        RatingBar ratingBar = itemView.findViewById(R.id.ratingProvider);
        TextView tvName = itemView.findViewById(R.id.tvName);
        TextView tvDistance = itemView.findViewById(R.id.tvDistance);
        TextView tvCarModelNumber = itemView.findViewById(R.id.tvCarModelNumber);
        TextView tvGender = itemView.findViewById(R.id.tVGender);
        Button btnRequest = itemView.findViewById(R.id.btnSelectRequest);

        try {
//            if (list.get(position).getAvatar().startsWith("http"))
//                Picasso.with(context).load(list.get(position).getAvatar()).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(imgAvatar);
//            else
//                Picasso.with(context).load(GlobalConstants.SERVER_HTTP_URL + "/storage/driver/avatar/" + list.get(position).getAvatar()).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(imgAvatar);

            ratingBar.setRating(list.get(position).getAvgRating());
            tvName.setText(list.get(position).getFirstName() + " " + list.get(position).getLastName());
            tvDistance.setText("1.5 km");
            tvCarModelNumber.setText(list.get(position).getCarModel() + " - " + list.get(position).getCarNumber());

            if (list.get(position).getGender() == 2)
                tvGender.setText(context.getString(R.string.female));
            else
                tvGender.setText(context.getString(R.string.male));

        } catch (Exception e) {

            e.printStackTrace();

        }

        btnRequest.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                ProviderListAdapter.this.mListener.onProviderRequest(list.get(position).getEmail());
            }});
        return itemView;
    }
}
