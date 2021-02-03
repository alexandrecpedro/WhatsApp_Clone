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
import com.murilofb.wppclone.models.MessageModel;

import java.text.SimpleDateFormat;
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
        View view;
        if (messagesList.get(viewType).isSent()) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_sent_layout, parent, false);
            return new ChatViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recylcer_recieved_layout, parent, false);
            return new ChatViewHolder(view);
        }

    }

    long onBindViewCount = 0;

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Log.i("ChatActivity", "BindView: " + onBindViewCount);
        onBindViewCount++;
        MessageModel message = messagesList.get(position);
        boolean nullMsg = (message.getMessage() == null);

        if (!nullMsg) {
            holder.txtMessage.setText(message.getMessage());
            holder.txtMessage.setVisibility(View.VISIBLE);
        } else {
            //Log.i("ChatActivity", "LoadedInPosition: " + position);
            holder.imgVPhoto.setVisibility(View.VISIBLE);
            Glide.with(holder.itemView.getContext())
                    .load(Uri.parse(message.getPhotoUrl()))
                    .into(holder.imgVPhoto);
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
        ImageView imgVPhoto;
        TextView txtHourSent;
        TextView txtDateSent;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMessage = itemView.findViewById(R.id.txtMessage);
            imgVPhoto = itemView.findViewById(R.id.imgVPhoto);
            txtHourSent = itemView.findViewById(R.id.txtHour);
            txtDateSent = itemView.findViewById(R.id.txtDate);
        }
    }

}
