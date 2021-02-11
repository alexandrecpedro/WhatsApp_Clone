package com.murilofb.wppclone.adapters;

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.murilofb.wppclone.R;
import com.murilofb.wppclone.models.GroupModel;
import com.murilofb.wppclone.models.MessageModel;
import com.murilofb.wppclone.models.UserModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    private List<MessageModel> messagesList;

    public ChatAdapter(List<MessageModel> messagesList) {
        this.messagesList = messagesList;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MessageModel message = messagesList.get(viewType);
        View view;
        if (message.isGroup()) {
            UserModel currentUser = UserModel.getCurrentUser();
            if (currentUser.getUserId().equals(message.getSentBy())) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_sent_layout, parent, false);
            } else {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recylcer_recieved_layout, parent, false);
            }
        } else {
            if (message.isSent()) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_sent_layout, parent, false);
            } else {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recylcer_recieved_layout, parent, false);
            }
        }
        return new ChatViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        MessageModel message = messagesList.get(position);
        boolean nullMsg = (message.getMessage() == null) || (message.getMessage().equals(""));

        if (!nullMsg) {
            holder.txtMessage.setText(message.getMessage());
            holder.txtMessage.setVisibility(View.VISIBLE);
        } else {
            holder.imgVPhoto.setVisibility(View.VISIBLE);
            Glide.with(holder.itemView.getContext())
                    .load(Uri.parse(message.getPhotoUrl()))
                    .into(holder.imgVPhoto);
        }
        if (message.isGroup()) {
            if (!message.isSent()) {
                GroupModel group = message.getGroupModel();
                List<UserModel> participants = group.getParticipants();
                String sentBy = message.getSentBy();
                for (UserModel item : participants) {
                    if (item.getUserId().equals(sentBy)) {
                        holder.txtSentName.setText(item.getUserName());
                        holder.txtSentName.setVisibility(View.VISIBLE);
                        break;
                    }
                }
            }
        }
        SimpleDateFormat sdFormat = new SimpleDateFormat("HH:mm");
        holder.txtHourSent.setText(sdFormat.format(message.getMessageTime()));
        sdFormat = new SimpleDateFormat("dd/MM");
        holder.txtDateSent.setText(sdFormat.format(message.getMessageTime()));

    }


    @Override
    public int getItemCount() {
        if (messagesList != null) {
            return messagesList.size();
        }
        return 0;
    }


    public class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView txtMessage;
        TextView txtSentName;
        ImageView imgVPhoto;
        TextView txtHourSent;
        TextView txtDateSent;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMessage = itemView.findViewById(R.id.txtMessage);
            txtSentName = itemView.findViewById(R.id.sentName);
            imgVPhoto = itemView.findViewById(R.id.imgVPhoto);
            txtHourSent = itemView.findViewById(R.id.txtHour);
            txtDateSent = itemView.findViewById(R.id.txtDate);
        }
    }

}
