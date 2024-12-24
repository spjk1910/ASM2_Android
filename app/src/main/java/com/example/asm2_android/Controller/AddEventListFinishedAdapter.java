package com.example.asm2_android.Controller;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.asm2_android.Model.EventDetailClass;
import com.example.asm2_android.Model.EventHistoryClass;
import com.example.asm2_android.R;
import com.example.asm2_android.View.General.FinishedEventActivity;
import com.example.asm2_android.View.General.MapActivity;
import com.example.asm2_android.View.SiteManager.SiteManagerDetailFinishedActivity;
import com.example.asm2_android.View.SiteManager.SiteManagerSiteDetailActivity;

import java.util.ArrayList;

public class AddEventListFinishedAdapter extends RecyclerView.Adapter<AddEventListFinishedAdapter.ViewHolder> {
    Context context;
    ArrayList<EventDetailClass> eventList;

    public AddEventListFinishedAdapter(Context context, ArrayList<EventDetailClass> eventList) {
        this.context = context;
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public AddEventListFinishedAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.site_manager_event_control_list_finished, parent, false);
        return new AddEventListFinishedAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddEventListFinishedAdapter.ViewHolder holder, int position) {
        EventDetailClass event = eventList.get(position);

        holder.eventName.setText(event.getEventName());
        holder.siteManagerName.setText(event.getSiteManagerName());
        holder.locationName.setText(event.getLocationName());
        holder.eventDate.setText(event.getEventDate());
        holder.bloodVolume.setText(event.getTotalBloodAmount() + " ml");
        holder.locationAddress.setText(event.getLocationAddress());
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView bloodVolume, eventName, siteManagerName, locationName, eventDate,locationAddress;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            bloodVolume = itemView.findViewById(R.id.volume);
            locationName = itemView.findViewById(R.id.locationName);
            eventDate = itemView.findViewById(R.id.event_date);
            eventName = itemView.findViewById(R.id.event_name);
            siteManagerName = itemView.findViewById(R.id.site_manager);
            locationAddress = itemView.findViewById(R.id.locationAddress);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (int) v.getTag();
                    if (position != RecyclerView.NO_POSITION) {
                        EventDetailClass event = eventList.get(position);
                        Intent intent = new Intent(context, SiteManagerDetailFinishedActivity.class);
                        intent.putExtra("EVENT_DETAIL", event);
                        context.startActivity(intent);
                    }
                }
            });

        }
    }
}
