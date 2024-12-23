package com.example.asm2_android.Controller;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.asm2_android.Model.EventHistoryClass;
import com.example.asm2_android.R;
import com.example.asm2_android.View.General.MapActivity;
import com.example.asm2_android.View.General.SiteDetailActivity;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class EventHistoryAdapter extends RecyclerView.Adapter<EventHistoryAdapter.ViewHolder> {
    Context context;
    ArrayList<EventHistoryClass> eventList;

    public EventHistoryAdapter(Context context, ArrayList<EventHistoryClass> eventList) {
        this.context = context;
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.event_history_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EventHistoryClass event = eventList.get(position);

        holder.name.setText(event.getName());
        holder.address.setText(event.getAddress());
        holder.genderAge.setText(event.getGenderAge());
        holder.locationName.setText(event.getLocation());
        holder.eventDate.setText(event.getEventDate());
        holder.bloodType.setText(event.getBloodType());
        holder.bloodVolume.setText(event.getGetBloodVolumeDonate());
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView bloodType, bloodVolume, genderAge, name, address, locationName, eventDate;
        LinearLayout gpsButton, cancelButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            bloodType = itemView.findViewById(R.id.bloodType);
            bloodVolume = itemView.findViewById(R.id.volume);
            genderAge = itemView.findViewById(R.id.gender_age);
            name = itemView.findViewById(R.id.name);
            address = itemView.findViewById(R.id.address);
            locationName = itemView.findViewById(R.id.location);
            eventDate = itemView.findViewById(R.id.event_date);
            gpsButton = itemView.findViewById(R.id.gps_button);
            cancelButton = itemView.findViewById(R.id.cancel_button);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (int) v.getTag();
                    if (position != RecyclerView.NO_POSITION) {
                        EventHistoryClass event = eventList.get(position);
                        Intent intent = new Intent(context, SiteDetailActivity.class);
                        intent.putExtra("EVENT_ID", event.getEventID());
                        context.startActivity(intent);
                    }
                }
            });

            gpsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getBindingAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        EventHistoryClass event = eventList.get(position);
                        Intent intent = new Intent(context, MapActivity.class);
                        intent.putExtra("location", event.getLocation());
                        intent.putExtra("latitude", event.getLatitude());
                        intent.putExtra("longitude", event.getLongitude());
                        context.startActivity(intent);
                    }
                }
            });

            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getBindingAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        EventHistoryClass event = eventList.get(position);
                        String id = event.getEventID();

                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        db.collection("registers")
                                .whereEqualTo("eventID", id)
                                .get()
                                .addOnSuccessListener(queryDocumentSnapshots -> {
                                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                                        document.getReference().delete()
                                                .addOnSuccessListener(aVoid -> {
                                                    eventList.remove(position);
                                                    notifyItemRemoved(position);
                                                })
                                                .addOnFailureListener(e -> {
                                                    Log.e("Firestore", "Failed to delete document", e);
                                                });
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("Firestore", "Failed to query document", e);
                                });
                    }
                }
            });

        }
    }
}
