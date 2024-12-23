package com.example.asm2_android.Controller;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.asm2_android.Model.EventHistoryClass;
import com.example.asm2_android.R;
import com.example.asm2_android.View.General.FinishedEventActivity;
import com.example.asm2_android.View.General.SiteDetailActivity;

import java.util.ArrayList;

public class EventHistoryFinishedAdapter extends RecyclerView.Adapter<EventHistoryFinishedAdapter.ViewHolder> {
    Context context;
    ArrayList<EventHistoryClass> eventList;

    public EventHistoryFinishedAdapter(Context context, ArrayList<EventHistoryClass> eventList) {
        this.context = context;
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.event_history_item_finished, parent, false);
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            bloodType = itemView.findViewById(R.id.bloodType);
            bloodVolume = itemView.findViewById(R.id.volume);
            genderAge = itemView.findViewById(R.id.gender_age);
            name = itemView.findViewById(R.id.name);
            address = itemView.findViewById(R.id.address);
            locationName = itemView.findViewById(R.id.location);
            eventDate = itemView.findViewById(R.id.event_date);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (int) v.getTag();
                    if (position != RecyclerView.NO_POSITION) {
                        EventHistoryClass event = eventList.get(position);
                        Intent intent = new Intent(context, FinishedEventActivity.class);
                        intent.putExtra("EVENT_ID", event.getEventID());
                        intent.putExtra("DONOR_NAME", event.getName());
                        intent.putExtra("BLOOD_TYPE", event.getBloodType());
                        intent.putExtra("BLOOD_VOLUME", event.getGetBloodVolumeDonate());
                        context.startActivity(intent);
                    }
                }
            });
        }
    }
}
