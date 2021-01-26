package com.murilofb.wppclone.adapters;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.core.Context;
import com.murilofb.wppclone.R;
import com.murilofb.wppclone.models.MessageModel;

import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessagesViewHolder> {
    private List<MessageModel> messagesList;


    public MessagesAdapter(List<MessageModel> messagesList) {
        this.messagesList = messagesList;
    }

    @NonNull
    @Override
    public MessagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_messages, parent, false);
        return new MessagesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessagesViewHolder holder, int position) {
        MessageModel message = messagesList.get(position);
        Drawable buttonDrawable;
        if (message.isSent()) {
            buttonDrawable = ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.rounded_button_light_green);
        } else {
            buttonDrawable = ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.rounded_button_white);
        }
        holder.txtMessage.setBackground(buttonDrawable);
        holder.txtMessage.setText(message.getMessage());
    }

    @Override
    public int getItemCount() {
        if (messagesList != null) {
            return messagesList.size();
        }
        Log.i("Messages", "null List");
        return 0;
    }

    public class MessagesViewHolder extends RecyclerView.ViewHolder {
        TextView txtMessage;

        public MessagesViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMessage = itemView.findViewById(R.id.txtMessage);
        }
    }
}
