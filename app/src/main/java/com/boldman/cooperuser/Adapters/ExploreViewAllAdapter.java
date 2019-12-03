package com.boldman.cooperuser.Adapters;

import android.content.Context;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.boldman.cooperuser.Model.LocalPlace;
import com.boldman.cooperuser.R;
import com.boldman.cooperuser.Utils.GlobalConstants;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ExploreViewAllAdapter extends RecyclerView.Adapter<ExploreViewAllAdapter.localPlaceHolder> {

    private List<LocalPlace> localPlaceList;
    private Context context;

    public ItemPressed localPlacePressed;

    public interface ItemPressed {
        void localPlacePressed(LocalPlace localPlace);
    }

    public class localPlaceHolder extends RecyclerView.ViewHolder {
        public TextView name,address;
        public ImageView image;
        public RelativeLayout listItemLayout;

        public localPlaceHolder(View view) {
            super(view);
            name = view.findViewById(R.id.explore_places_list_item_place_name);
            address = view.findViewById(R.id.explore_places_list_item_place_description);
            image = view.findViewById(R.id.explore_places_list_item_image);
            listItemLayout = view.findViewById(R.id.explore_places_list_item_layout);

        }
    }

    public ExploreViewAllAdapter(List<LocalPlace> localPlaceList) {
        this.localPlaceList = localPlaceList;
    }

    @Override
    public localPlaceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.explore_places_list_item, parent, false);

        context = parent.getContext();

        return new localPlaceHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final localPlaceHolder holder, final int position) {
        final LocalPlace localPlace = localPlaceList.get(position);

        holder.name.setText(localPlace.getName());
        holder.address.setText(localPlace.getAddress());

        if(localPlace.getImage() != null){

           Picasso.with(context).load(GlobalConstants.SERVER_HTTP_URL + localPlace.getImage()).placeholder(context.getResources().getDrawable(R.drawable.loading)).into(holder.image, new Callback() {

               @Override
               public void onSuccess() {
               }

               @Override
               public void onError() {
                   holder.image.setImageDrawable(context.getResources().getDrawable(R.mipmap.ic_launcher));
               }
           });
        }else {
           holder.image.setImageDrawable(context.getResources().getDrawable(R.mipmap.ic_launcher));
        }


        holder.listItemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                localPlacePressed.localPlacePressed(localPlaceList.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        return localPlaceList.size();
    }

    public void updateList(List<LocalPlace> newList){

        localPlaceList.clear();
        localPlaceList.addAll(newList);
        this.notifyDataSetChanged();
    }

}

