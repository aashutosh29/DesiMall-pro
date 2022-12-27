package com.aashutosh.simplestore.utils;


import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.aashutosh.simplestore.R;
import com.aashutosh.simplestore.ui.HomeActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


@SuppressLint("MissingFirebaseInstanceTokenRefresh")
public class PushNotificationService extends FirebaseMessagingService {
    public static String TAG = "desiMall";
    Bitmap bitmap;
    NotificationManager notificationManager;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
       /* SharedPrefsHelper sharedPrefsHelper = SharedPrefsHelper.getInstance(getApplicationContext());
        sharedPrefsHelper.saveValue(Constant.URL, remoteMessage.getData().get("link"));
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "from : " + remoteMessage.getFrom());
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "message body" + remoteMessage.getNotification().getBody());
        }

        if (remoteMessage.getNotification().getImageUrl() != null) {
            bitmap = getBitmapfromUrl(remoteMessage.getNotification().getImageUrl().toString());
        }

        Log.d(TAG, "testing: " + remoteMessage.getData().get("link"));
        sendNotification(remoteMessage.getNotification().getBody(), remoteMessage.getNotification().getTitle(), bitmap, remoteMessage.getData().get("link"));
*/
        super.onMessageReceived(remoteMessage);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (remoteMessage.getData().size() > 0) {
            String totalPrice = remoteMessage.getData().get("totalPrice");
            String message = remoteMessage.getData().get("message");
            String imageUrl = remoteMessage.getData().get("url");
            String route = remoteMessage.getData().get("route");
            sendNotification(message, totalPrice, getBitmapfromUrl(imageUrl), route);
        }


    }


    private void sendNotification(String body, String totalprice, Bitmap uri, String link) {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(Constant.URL, link);
        Log.d(TAG, "testing: " + link);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
        String channelId = "MyApp";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Your order has been verified")
                .setContentText("Order of " + totalprice + " will be delivered soon")
                .setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(uri))
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Channel human readable title", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);

            notificationManager.notify(0, notificationBuilder.build());
        }

    }

    public Bitmap getBitmapfromUrl(String imageUrl) {
        try {

            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;

        }
    }
}
