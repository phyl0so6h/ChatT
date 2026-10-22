package com.sahak7an.chatt.adapters;

import static com.sahak7an.chatt.utilities.Constants.PROFILE_IMAGE_SIZE;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sahak7an.chatt.databinding.ItemContainerUserBinding;
import com.sahak7an.chatt.listeners.UserListener;
import com.sahak7an.chatt.models.User;
import com.sahak7an.chatt.utilities.ImageUtils;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder>{

    private final List<User> userList;
    private final UserListener userListener;

    public UsersAdapter(List<User> userList, UserListener userListener) {
        this.userList = userList;
        this.userListener = userListener;
    }

    class UserViewHolder extends RecyclerView.ViewHolder {

        ItemContainerUserBinding itemContainerUserBinding;

        UserViewHolder(ItemContainerUserBinding itemContainerUserBinding) {

            super(itemContainerUserBinding.getRoot());
            this.itemContainerUserBinding = itemContainerUserBinding;

        }

        void setUserData(User user) {

            itemContainerUserBinding.textUserName.setText(user.userName);
            itemContainerUserBinding.textEmail.setText(user.email);

            itemContainerUserBinding.imageProfile.setImageBitmap(
                    ImageUtils.decodeScaled(user.image, PROFILE_IMAGE_SIZE)
            );

            itemContainerUserBinding.getRoot().setOnClickListener(v -> userListener.onUserClicked(user));

        }
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ItemContainerUserBinding itemContainerUserBinding = ItemContainerUserBinding.inflate(

                LayoutInflater.from(parent.getContext()),
                parent,
                false

        );

        return new UserViewHolder(itemContainerUserBinding);

    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {

        holder.setUserData(userList.get(position));

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

}
