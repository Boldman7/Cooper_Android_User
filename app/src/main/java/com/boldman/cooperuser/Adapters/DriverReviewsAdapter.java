package com.boldman.cooperuser.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.boldman.cooperuser.Model.YourTrips;
import com.boldman.cooperuser.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

public class DriverReviewsAdapter extends RecyclerView.Adapter<DriverReviewsAdapter.MyViewHolder> {

    private List<YourTrips> finishedRideList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView name,comment;
        public RatingBar userRating;
        public ImageView image;

        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.tv_user_name);
            comment = view.findViewById(R.id.tv_user_comment);
            image = view.findViewById(R.id.img_user);
            userRating = view.findViewById(R.id.rb_user_rating);
        }
    }

    public DriverReviewsAdapter(List<YourTrips> finishedRideList) {
        this.finishedRideList = finishedRideList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_review_history, parent, false);

        context = parent.getContext();

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final YourTrips finishRide = finishedRideList.get(position);

        holder.name.setText(finishRide.getUsername());
        holder.comment.setText(finishRide.getUser_rated());
        holder.userRating.setRating(Float.valueOf(finishRide.getUser_rating()));
        holder.userRating.setIsIndicator(true);

        if(finishRide.getAvatar() != null){

           Picasso.with(context).load(finishRide.getAvatar()).placeholder(context.getResources().getDrawable(R.drawable.loading)).into(holder.image, new Callback() {

               @Override
               public void onSuccess() {
               }

               @Override
               public void onError() {
                   holder.image.setImageDrawable(context.getDrawable(R.drawable.people));
               }
           });
        }else {
           holder.image.setImageDrawable(context.getDrawable(R.drawable.people));
        }
    }

    @Override
    public int getItemCount() {
        return finishedRideList.size();
    }

}

