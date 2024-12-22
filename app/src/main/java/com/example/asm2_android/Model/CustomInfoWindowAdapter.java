package com.example.asm2_android.Model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.asm2_android.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private final Context context;

    public CustomInfoWindowAdapter(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public View getInfoContents(@NonNull Marker marker) {
        return null;
    }

    @Nullable
    @Override
    public View getInfoWindow(@NonNull Marker marker) {
        View infoWindowView = LayoutInflater.from(context).inflate(R.layout.marker_info, null);

        TextView eventNameTextView = infoWindowView.findViewById(R.id.event_name);
        TextView bloodTypeTextView = infoWindowView.findViewById(R.id.blood_type);
        TextView startDateTextView = infoWindowView.findViewById(R.id.start_date);
        ImageView eventImageView = infoWindowView.findViewById(R.id.marker_image);

        CustomInfoMarkerClass data = (CustomInfoMarkerClass) marker.getTag();
        eventNameTextView.setText(data.getEventName());
        bloodTypeTextView.setText(data.getBloodType());
        startDateTextView.setText(data.getStartDate());
        eventImageView.setImageResource(data.getImage());

        return infoWindowView;
    }
}
