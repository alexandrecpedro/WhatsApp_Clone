package com.murilofb.wppclone.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.murilofb.wppclone.R;
import com.murilofb.wppclone.models.UserModel;


import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactsViewHolder> {
    private List<UserModel> friendsList;
    private onRecyclerClick recyclerClick;
    private boolean isMessageFrag = false;

    public ContactsAdapter(List<UserModel> friendsList, onRecyclerClick recyclerClick, boolean isMessageFrag) {
        this.friendsList = friendsList;
        this.recyclerClick = recyclerClick;
        this.isMessageFrag = isMessageFrag;
    }

    private boolean verticalVersion = false;

    public ContactsAdapter(List<UserModel> friendsList, onRecyclerClick recyclerClick) {
        this.friendsList = friendsList;
        this.recyclerClick = recyclerClick;
        verticalVersion = true;
    }

    @NonNull
    @Override
    public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_contacts, parent, false);
        return new ContactsViewHolder(view, recyclerClick);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactsViewHolder holder, int position) {
        UserModel friend = friendsList.get(position);
        boolean groupItem = friend.getEmail().equals("");
        holder.txtName.setText(friend.getName());
        if (!groupItem && !friend.getUserName().equals("")) {
            holder.txtUserName.setCompoundDrawables(null, null, null, null);
            holder.txtUserName.setText(friend.getUserName());
        }
        if (groupItem) {
            holder.txtUserName.setVisibility(View.GONE);
            Glide.with(holder.itemView)
                    .load(R.drawable.new_group_green)
                    .into(holder.profilePhoto);
        } else {
            Glide.with(holder.itemView)
                    .load(friend.getProfileImgLink())
                    .placeholder(R.drawable.default_user_but_round)
                    .into(holder.profilePhoto);
        }
        if (isMessageFrag) {
            int lightBlue = ContextCompat.getColor(holder.itemView.getContext(), android.R.color.holo_blue_light);
            holder.txtUserName.setTextColor(lightBlue);
        }

    }

    @Override
    public int getItemCount() {
        if (friendsList == null) {
            return 0;
        }
        return friendsList.size();
    }

    public class ContactsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CircleImageView profilePhoto;
        TextView txtName;
        TextView txtUserName;
        onRecyclerClick recyclerClick;

        public ContactsViewHolder(@NonNull View itemView, onRecyclerClick recyclerClick) {
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
