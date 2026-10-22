package com.sahak7an.chatt.adapters;

import static com.sahak7an.chatt.utilities.Constants.PROFILE_IMAGE_SIZE;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sahak7an.chatt.R;
import com.sahak7an.chatt.databinding.ItemContainerRecentConversionBinding;
import com.sahak7an.chatt.listeners.ConversationListener;
import com.sahak7an.chatt.models.ChatMessage;
import com.sahak7an.chatt.models.User;
import com.sahak7an.chatt.utilities.ImageUtils;

import java.util.List;

public class RecentConversationsAdapter extends RecyclerView.Adapter<RecentConversationsAdapter.ConversionViewHolder> {

    private final List<ChatMessage> chatMessages;
    private final ConversationListener conversationListener;

    public RecentConversationsAdapter(List<ChatMessage> chatMessages, ConversationListener conversationListener) {
        this.chatMessages = chatMessages;
        this.conversationListener = conversationListener;
    }

    class ConversionViewHolder extends RecyclerView.ViewHolder {

        ItemContainerRecentConversionBinding recentConversionBinding;

        ConversionViewHolder(ItemContainerRecentConversionBinding recentConversionBinding) {

            super(recentConversionBinding.getRoot());

            this.recentConversionBinding = recentConversionBinding;

        }

        void setData(ChatMessage chatMessage) {

            if (Boolean.TRUE.equals(chatMessage.isImage)) {
                recentConversionBinding.textRecentMessage.setText(R.string.file);
            } else {
                recentConversionBinding.textRecentMessage.setText(chatMessage.message);
            }

            recentConversionBinding.imageProfile.setImageBitmap(
                    ImageUtils.decodeScaled(chatMessage.conversionImage, PROFILE_IMAGE_SIZE)
            );

            recentConversionBinding.textUserName.setText(chatMessage.conversionName);
            recentConversionBinding.getRoot().setOnClickListener(v -> {

                User user = new User();
                user.id = chatMessage.conversionId;
                user.userName = chatMessage.conversionName;
                user.image = chatMessage.conversionImage;
                conversationListener.onConversationClicked(user);

            });

        }

    }

    @NonNull
    @Override
    public ConversionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ConversionViewHolder(

                ItemContainerRecentConversionBinding.inflate(

                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false

                )

        );

    }

    @Override
    public void onBindViewHolder(@NonNull ConversionViewHolder holder, int position) {

        holder.setData(chatMessages.get(position));

    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

}
