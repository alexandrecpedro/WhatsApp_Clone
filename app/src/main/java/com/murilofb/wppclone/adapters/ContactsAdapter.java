package com.murilofb.wppclone.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    private boolean verticalVersion = false;

    //Esse construtor será somente chamado na activity de gerar grupo
    public ContactsAdapter(List<UserModel> friendsList, @Nullable onRecyclerClick recyclerClick) {
        this.friendsList = friendsList;
        if (recyclerClick != null) {
            this.recyclerClick = recyclerClick;
        }
        verticalVersion = true;
    }

    @NonNull
    @Override
    public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (verticalVersion) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_contacts_vertical, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_contacts, parent, false);
        }
        if (recyclerClick == null) {
            return new ContactsViewHolder(view, null);
        }
        return new ContactsViewHolder(view, recyclerClick);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactsViewHolder holder, int position) {
        UserModel friend = friendsList.get(position);
        boolean groupItem = friend.getEmail() != null && friend.getEmail().equals("");
        if (verticalVersion) {
            String[] splittedName = friend.getName().split(" ");
            holder.txtContactTitle.setText(splittedName[0]);
            if (recyclerClick == null) {
                holder.imgBtnRemoveMember.setVisibility(View.GONE);
            }
        } else {
            holder.txtContactTitle.setText(friend.getUserName());
        }


        if (!groupItem && !friend.getUserName().equals("") && !verticalVersion) {
            holder.txtContactDesc.setCompoundDrawables(null, null, null, null);
            holder.txtContactDesc.setText(friend.getName());
        }
        if (groupItem) {
            holder.txtContactDesc.setVisibility(View.GONE);
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
            holder.txtContactDesc.setTextColor(lightBlue);
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
        TextView txtContactTitle;
        TextView txtContactDesc;
        onRecyclerClick recyclerClick;
        ImageButton imgBtnRemoveMember;

        public ContactsViewHolder(@NonNull View itemView, @Nullable onRecyclerClick recyclerClick) {
            super(itemView);
            profilePhoto = itemView.findViewById(R.id.contactProfilePhoto);
            txtContactTitle = itemView.findViewById(R.id.contactTitle);
            txtContactDesc = itemView.findViewById(R.id.contactDesc);
            imgBtnRemoveMember = itemView.findViewById(R.id.imgBtnRemoveMember);

            if (recyclerClick != null) {
                this.recyclerClick = recyclerClick;
                itemView.setOnClickListener(this);
            }

        }

        @Override
        public void onClick(View v) {
            if (recyclerClick != null) {
                recyclerClick.onClick(getAdapterPosition());
            }
        }
    }

    public interface onRecyclerClick {
        void onClick(int position);
    }
}
