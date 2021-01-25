package com.murilofb.wppclone.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.murilofb.wppclone.R;
import com.murilofb.wppclone.models.UserModel;


import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.MessagesViewHolder> {
    private List<UserModel> friendsList;
    private Context context;
    private onRecyclerClick recyclerClick;

    public ContactsAdapter(List<UserModel> friendsList, Context context, onRecyclerClick recyclerClick) {
        this.friendsList = friendsList;
        this.context = context;
        this.recyclerClick = recyclerClick;
    }

    @NonNull
    @Override
    public MessagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_messages, parent, false);
        return new MessagesViewHolder(view, recyclerClick);
    }

    @Override
    public void onBindViewHolder(@NonNull MessagesViewHolder holder, int position) {
        UserModel friend = friendsList.get(position);
        holder.txtName.setText(friend.getName());
        holder.txtUserName.setText("@" + friend.getUserName());
        Glide.with(holder.itemView)
                .load(friend.getProfileImgLink())
                .placeholder(R.drawable.default_user_but_round)
                .into(holder.profilePhoto);
    }

    @Override
    public int getItemCount() {
        if (friendsList == null) {
            return 0;
        }
        return friendsList.size();
    }

    public class MessagesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CircleImageView profilePhoto;
        TextView txtName;
        TextView txtUserName;
        onRecyclerClick recyclerClick;

        public MessagesViewHolder(@NonNull View itemView, onRecyclerClick recyclerClick) {
            super(itemView);
            profilePhoto = itemView.findViewById(R.id.contactProfilePhoto);
            txtName = itemView.findViewById(R.id.contactName);
            txtUserName = itemView.findViewById(R.id.contactUserName);
            this.recyclerClick = recyclerClick;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            recyclerClick.onClick(getAdapterPosition());
        }
    }

    public interface onRecyclerClick {
        void onClick(int position);

    }
}
