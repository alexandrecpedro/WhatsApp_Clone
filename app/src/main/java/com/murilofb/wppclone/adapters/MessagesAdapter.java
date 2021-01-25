package com.murilofb.wppclone.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.murilofb.wppclone.R;
import com.murilofb.wppclone.models.UserModel;


import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessagesViewHolder> {
    private List<UserModel> friendsList;

    public MessagesAdapter(List<UserModel> friendsList) {
        this.friendsList = friendsList;
    }

    @NonNull
    @Override
    public MessagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_messages, parent, false);
        return new MessagesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessagesViewHolder holder, int position) {
        UserModel friend = friendsList.get(position);
        holder.txtName.setText(friend.getName());
        holder.txtUserName.setText(friend.getUserName());
    }

    @Override
    public int getItemCount() {
        return friendsList.size();
    }

    public class MessagesViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profilePhoto;
        TextView txtName;
        TextView txtUserName;

        public MessagesViewHolder(@NonNull View itemView) {
            super(itemView);
            profilePhoto = itemView.findViewById(R.id.friendProfilePhoto);
            txtName = itemView.findViewById(R.id.friendName);
            txtUserName = itemView.findViewById(R.id.friendUserName);
        }
    }
}
