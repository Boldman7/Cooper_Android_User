package com.boldman.cooperuser.Adapters;

/**
 * Created by Tranxit Technologies Pvt Ltd, Chennai
 */

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.boldman.cooperuser.Model.CardDetails;
import com.boldman.cooperuser.R;

import java.util.List;

public class NewPaymentListAdapter extends ArrayAdapter<CardDetails> {

    List<CardDetails> list;

    Context context;

    Activity activity;

    public NewPaymentListAdapter(Context context, List<CardDetails> list, Activity activity){
        super(context, R.layout.payment_list_item, list);
        this.context=context;
        this.list=list;
        this.activity = activity;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(R.layout.payment_list_item, parent, false);

        ImageView paymentTypeImg = itemView.findViewById(R.id.paymentTypeImg);
        ImageView checkMarkImage = itemView.findViewById(R.id.img_tick);
        TextView cardNumber = itemView.findViewById(R.id.cardNumber);

        try {

           if(list.get(position).getBrand().equalsIgnoreCase("MASTER")){
               paymentTypeImg.setImageResource(R.drawable.master_card_image);
           }else if(list.get(position).getBrand().equalsIgnoreCase("MASTRO")){
               paymentTypeImg.setImageResource(R.drawable.visa_payment_image);
           }else if(list.get(position).getBrand().equalsIgnoreCase("Visa")){
               paymentTypeImg.setImageResource(R.drawable.visa_payment_image);
           }

           int cardNoLen = list.get(position).getCardNo().length();

           cardNumber.setText(" "+ list.get(position).getCardNo().substring(cardNoLen - 4));

//           if(list.get(position).getSelected().equalsIgnoreCase("true")){
//               checkMarkImage.setVisibility(View.VISIBLE);
//           }

        } catch (Exception e) {

            e.printStackTrace();

        }
        return itemView;
    }
}
