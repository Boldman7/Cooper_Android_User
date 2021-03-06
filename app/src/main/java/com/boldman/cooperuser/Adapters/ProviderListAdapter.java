package com.boldman.cooperuser.Adapters;

/**
 * ProviderList
 * Created by Boldman
 * 2019.06.06
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.boldman.cooperuser.Model.DriverInfo;
import com.boldman.cooperuser.R;
import com.boldman.cooperuser.Utils.Utils;

import java.util.List;

import okhttp3.internal.Util;


public class ProviderListAdapter extends ArrayAdapter<DriverInfo> {

    Context context;
    List<DriverInfo> list;
    IProviderListRequestListener mRequestListener;
    IProviderListViewListener mViewListener;

    public interface IProviderListRequestListener {
        void onProviderRequest(String strProviderEmail);
    }

    public interface IProviderListViewListener {
        void onProviderView(int position);
    }

    public ProviderListAdapter(Context context, List<DriverInfo> list, IProviderListRequestListener requestListener, IProviderListViewListener viewListener){
        super(context, R.layout.provider_list_item, list);
        this.context=context;
        this.list=list;
        this.mRequestListener = requestListener;
        this.mViewListener = viewListener;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(R.layout.provider_list_item, parent, false);

        ImageView imgAvatar = itemView.findViewById(R.id.imgAvatar);
        RatingBar ratingBar = itemView.findViewById(R.id.ratingProvider);
        TextView tvName = itemView.findViewById(R.id.tvName);
        TextView tvDistance = itemView.findViewById(R.id.tvDistance);
        TextView tvEta = itemView.findViewById(R.id.tv_eta);
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
            tvDistance.setText(Utils.roundTwoDecimals(list.get(position).getDistance() / 1000) + " Km");
            tvEta.setText(Utils.roundTwoDecimals(list.get(position).getDistance() / 400) + " min");

            if (list.get(position).getGender() == 2)
                tvGender.setText(context.getString(R.string.female));
            else
                tvGender.setText(context.getString(R.string.male));

            String strCarModel = list.get(position).getCarModel();
            strCarModel = strCarModel == null ? "" : strCarModel;
            String strCarNumber = list.get(position).getCarNumber();
            strCarNumber = strCarNumber == null ? "" : strCarNumber;

            tvCarModelNumber.setText(strCarModel + " - " + strCarNumber);

        } catch (Exception e) {

            e.printStackTrace();

        }

        btnRequest.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                ProviderListAdapter.this.mRequestListener.onProviderRequest(list.get(position).getEmail());
            }});

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ProviderListAdapter.this.mViewListener.onProviderView(position);
            }
        });
        return itemView;
    }
}
