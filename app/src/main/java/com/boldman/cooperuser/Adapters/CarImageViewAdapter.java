package com.boldman.cooperuser.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.boldman.cooperuser.Model.CarImage;
import com.boldman.cooperuser.Model.YourTrips;
import com.boldman.cooperuser.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CarImageViewAdapter extends RecyclerView.Adapter<CarImageViewAdapter.MyViewHolder> {

    private List<CarImage> carImageList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView image;

        public MyViewHolder(View view) {
            super(view);
            image = view.findViewById(R.id.img_car);
        }
    }

    public CarImageViewAdapter(List<CarImage> carImageList) {
        this.carImageList = carImageList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_car_image, parent, false);

        context = parent.getContext();

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final CarImage carImage = carImageList.get(position);

        if(carImage.getCarImageUrl() != null){

           Picasso.with(context).load(carImage.getCarImageUrl()).placeholder(context.getResources().getDrawable(R.drawable.loading)).into(holder.image, new Callback() {

               @Override
               public void onSuccess() {
               }

               @Override
               public void onError() {
               }
           });
        }else {
        }
    }

    @Override
    public int getItemCount() {
        return carImageList.size();
    }

}

