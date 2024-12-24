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
import com.example.asm2_android.Model.NotificationClass;
import com.example.asm2_android.R;
import com.example.asm2_android.View.General.MapActivity;
import com.example.asm2_android.View.General.SiteDetailActivity;
import com.example.asm2_android.View.SiteManager.SiteManagerSiteDetailActivity;

import java.util.ArrayList;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    Context context;
    ArrayList<NotificationClass> notificationList;

    public NotificationAdapter(Context context, ArrayList<NotificationClass> notificationList) {
        this.context = context;
        this.notificationList = notificationList;
    }

    @NonNull
    @Override
    public NotificationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notification_item, parent, false);
        return new NotificationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.ViewHolder holder, int position) {
        NotificationClass notification = notificationList.get(position);

        holder.notification_time.setText(notification.getTime());
        holder.notification_content.setText(notification.getContent());
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView  notification_content, notification_time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            notification_content = itemView.findViewById(R.id.notification_content);
            notification_time = itemView.findViewById(R.id.notification_time);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getBindingAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        NotificationClass event = notificationList.get(position);
                        Intent intent = new Intent(context, SiteDetailActivity.class);
                        intent.putExtra("EVENT_ID", event.getEventID());
                        context.startActivity(intent);
                    }
                }
            });
        }
    }
}
