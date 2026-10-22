package com.sahak7an.chatt.firebase;

import static com.sahak7an.chatt.utilities.Constants.KEY_FCM_TOKEN;
import static com.sahak7an.chatt.utilities.Constants.KEY_MESSAGE;
import static com.sahak7an.chatt.utilities.Constants.KEY_RECEIVER_ID;
import static com.sahak7an.chatt.utilities.Constants.KEY_RECEIVER_IMAGE;
import static com.sahak7an.chatt.utilities.Constants.KEY_RECEIVER_USER_NAME;
import static com.sahak7an.chatt.utilities.Constants.KEY_USER;
import static com.sahak7an.chatt.utilities.Constants.KEY_USER_ID;
import static com.sahak7an.chatt.utilities.Constants.KEY_USER_NAME;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.graphics.drawable.IconCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.sahak7an.chatt.R;
import com.sahak7an.chatt.activities.ChatActivity;
import com.sahak7an.chatt.models.User;
import com.sahak7an.chatt.utilities.ImageUtils;
import com.sahak7an.chatt.utilities.PreferenceManager;

import java.util.Random;

public class MessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String token) {

        super.onNewToken(token);

    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {

        super.onMessageReceived(message);

        PreferenceManager preferenceManager = new PreferenceManager(this);

        User user = new User();

        user.id = message.getData().get(KEY_USER_ID);
        user.token = message.getData().get(KEY_FCM_TOKEN);
        user.userName = message.getData().get(KEY_USER_NAME);

        preferenceManager.putString(KEY_RECEIVER_ID, user.id);
        preferenceManager.putString(KEY_RECEIVER_USER_NAME, user.userName);

        int notificationId = new Random().nextInt();
        String channelId = "chat_message";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            CharSequence channelName = "Chat'T";
            String channelDescription = "This notification channel is used for chat message notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            channel.setDescription(channelDescription);
            channel.setLockscreenVisibility(Notification.GROUP_ALERT_ALL);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }

        Intent intent = new Intent(this, ChatActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(KEY_USER, user);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, notificationId, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId);

        builder.setSmallIcon(R.drawable.ic_chat);
        builder.setContentTitle(user.userName);
        builder.setContentText(message.getData().get(KEY_MESSAGE));
        builder.setLargeIcon(ImageUtils.decode(message.getData().get(KEY_RECEIVER_IMAGE)));
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {

            return;

        }

        notificationManagerCompat.notify(notificationId, builder.build());

    }

}
