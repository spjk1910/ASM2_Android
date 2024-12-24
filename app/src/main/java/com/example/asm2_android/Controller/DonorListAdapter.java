package com.example.asm2_android.Controller;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.asm2_android.Model.NotificationClass;
import com.example.asm2_android.Model.UserClass;
import com.example.asm2_android.R;
import com.example.asm2_android.View.General.DonorDetailActivity;
import com.example.asm2_android.View.General.SiteDetailActivity;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;

public class DonorListAdapter extends RecyclerView.Adapter<DonorListAdapter.ViewHolder> {
    Context context;
    ArrayList<UserClass> donorList;

    public DonorListAdapter(Context context, ArrayList<UserClass> donorList) {
        this.context = context;
        this.donorList = donorList;
    }

    @NonNull
    @Override
    public DonorListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.donor_detail_sheet, parent, false);
        return new DonorListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DonorListAdapter.ViewHolder holder, int position) {
        UserClass donorDetail = donorList.get(position);

        holder.donor_name.setText(donorDetail.getName());
        holder.donor_username.setText(donorDetail.getUsername());
        holder.donor_birthday.setText(donorDetail.getBirthday());
        Glide.with(context)
                .load(donorDetail.getProfileImage())
                .placeholder(R.drawable.default_avatar)
                .error(R.drawable.default_avatar)
                .circleCrop()
                .into(holder.avatar);

        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return donorList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView  donor_name, donor_username, donor_birthday;
        ShapeableImageView avatar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            donor_name = itemView.findViewById(R.id.name);
            donor_username = itemView.findViewById(R.id.username);
            donor_birthday = itemView.findViewById(R.id.birthday);
            avatar = itemView.findViewById(R.id.avatar);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getBindingAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        UserClass donor = donorList.get(position);
                        Intent intent = new Intent(context, DonorDetailActivity.class);
                        intent.putExtra("USER_ID", donor.getUsername());
                        context.startActivity(intent);
                    }
                }
            });
        }
    }
}
