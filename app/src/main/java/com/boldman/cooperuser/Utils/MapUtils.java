package com.boldman.cooperuser.Utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.boldman.cooperuser.R;

public class MapUtils {

    public static Bitmap createSourceMarker(Fragment fragment, String addressSource) {

        View markerLayout = fragment.getLayoutInflater().inflate(R.layout.marker_src_map_fragment, null);

        ImageView markerImage =markerLayout.findViewById(R.id.marker_image);
        TextView markerText = markerLayout.findViewById(R.id.marker_text);
        markerImage.setImageResource(R.drawable.source_marker);
        markerText.setText(addressSource);

        markerLayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        markerLayout.layout(0, 0, markerLayout.getMeasuredWidth(), markerLayout.getMeasuredHeight());

        final Bitmap bitmap = Bitmap.createBitmap(markerLayout.getMeasuredWidth(), markerLayout.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        markerLayout.draw(canvas);
        return bitmap;
    }

    public static Bitmap createDestinationMarker(Fragment fragment, String addressSource) {

        View markerLayout = fragment.getLayoutInflater().inflate(R.layout.marker_dest_map_fragment, null);

        ImageView markerImage =markerLayout.findViewById(R.id.destination_marker_image);
        TextView markerText = markerLayout.findViewById(R.id.destination_marker_text);
        TextView timeToReachAtDestination = markerLayout.findViewById(R.id.destination_marker_time);

        markerImage.setImageResource(R.drawable.destination_marker);
        markerText.setText(addressSource);
        markerLayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        markerLayout.layout(0, 0, markerLayout.getMeasuredWidth(), markerLayout.getMeasuredHeight());

        final Bitmap bitmap = Bitmap.createBitmap(markerLayout.getMeasuredWidth(), markerLayout.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        markerLayout.draw(canvas);
        return bitmap;
    }

}
