package com.langoor.app.blueshak.Messaging.gcm;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;
import android.widget.Toast;

import com.divrt.co.R;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.langoor.app.blueshak.AppController;
import com.langoor.app.blueshak.Messaging.activity.ChatActivity;
import com.langoor.app.blueshak.Messaging.data.GcmMessage;
import com.langoor.app.blueshak.Messaging.data.Message;
import com.langoor.app.blueshak.Messaging.data.User;
import com.langoor.app.blueshak.Messaging.helper.Constants;
import com.langoor.app.blueshak.Messaging.manager.MessageManager;
import com.langoor.app.blueshak.Messaging.util.CommonUtil;
import com.langoor.app.blueshak.global.GlobalFunctions;
import com.langoor.app.blueshak.global.GlobalVariables;

import com.langoor.app.blueshak.services.model.NotificationModel;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 *
 */
public class GcmIntentService extends IntentService {
    Context context;


    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        context=this;
        if (intent == null) return;
        final Bundle extras = intent.getExtras();
        final GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has SIDE-effect of unparcelling Bundle
            if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                broadcastGcmMessage(extras);
            }
        }

        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }
    private void broadcastGcmMessage(Bundle extras) {
        final String json1 = extras.getString("u");
        if(!TextUtils.isEmpty(json1) || json1!=null){
            try{
                JSONObject obj=new JSONObject(json1);
                if(obj.has("type")){
                    String type=obj.getString("type");
                    if(type.equalsIgnoreCase("Message Response")){
                        System.out.println("######SAvig notitifcaiton##########");
                        String message=obj.getString("message");
                        int notification_unread_count= GlobalFunctions.getSharedPreferenceInt(context, GlobalVariables.NOTIFICATIONS_UNREAD_COUNT);
                        GlobalFunctions.setSharedPreferenceInt(context, GlobalVariables.NOTIFICATIONS_UNREAD_COUNT,notification_unread_count+1);
                        AppController.getInstance().getPrefManager().saveNotifications(new
                                NotificationModel(message,new SimpleDateFormat(CommonUtil.DATE_FORMAT_FOR_MESSAGE).format(new Date())));
                        sendNotification("Message Response","Blueshak",message,type);
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }else{
            System.out.println("######SAvig m3essage##########");
            final String title = extras.getString("title");
            System.out.println("######extras########"+extras.toString());
            System.out.println("######title########"+title);
            Gson gson = new Gson();
            if (title == null) return;
            final JsonElement contentJson = gson.fromJson(title, JsonElement.class);
            if (!contentJson.isJsonObject()) return;
            System.out.println("######contentJson########"+contentJson.toString());
            final JsonObject json = contentJson.getAsJsonObject();
            System.out.println("######json########"+json.toString());
            final JsonObject jsonData = json.getAsJsonObject("data");
            final String itnsStr = jsonData.get("itns").getAsString();
            final GcmMessage.ITNS itnsObject = gson.fromJson(itnsStr, GcmMessage.ITNS.class);
            final GcmMessage gcmMessage = new GcmMessage(itnsObject);
            Gson  sender_gson=new Gson();
            String gcmMessage_res=sender_gson.toJson(gcmMessage);
            System.out.println("######GcmMessage##########"+gcmMessage_res);
            if(gcmMessage != null){
                User sender = new User(gcmMessage.getSenderFirstName(),null,gcmMessage.getSenderNumber(),gcmMessage.getSenderCc_id(), gcmMessage.getSenderImageUrl());
                sender.setConversation_id(gcmMessage.getConversationId());
                String active_tab;
                System.out.println("######getConversationId##########"+gcmMessage.getConversationId());
                if(gcmMessage.getActiveTab().equalsIgnoreCase(GlobalVariables.TYPE_SELLER_TAB))
                    active_tab=GlobalVariables.TYPE_BUYER_TAB;
                else
                    active_tab=GlobalVariables.TYPE_SELLER_TAB;
                sender.setConversation_id(gcmMessage.getConversationId());
                sender.setActive_tab(active_tab);
                Message receivedMessage = new Message(gcmMessage.getMessageString(),sender,new Date(),gcmMessage.getMessageToken(),gcmMessage.getMessageId());
                receivedMessage.setType(gcmMessage.getMessage_type());
                receivedMessage.setMessage_image_url(gcmMessage.getMessage_imageUrl());
                MessageManager.getInstance(context).doMessageAuthentication(gcmMessage, receivedMessage,context,sender);

            }
        }
    }
    /**
     * Send simple notification using the NotificationCompat API.
     */
    public void sendNotification(String user,String Title,String Text,String Type) {

        //UserManager.getInstance(context).isFromNotification(true);
        // Use NotificationCompat.Builder to set up our notification.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        //icon appears in device notification bar and right hand corner of notification
        builder.setSmallIcon(R.drawable.ic_app_icon);
        Intent intent;
        if(Type.equalsIgnoreCase("Message Response")){
            intent = new Intent(this, ChatActivity.class);
            Toast.makeText(context,"Move to Notification",Toast.LENGTH_LONG).show();
        }else{
            intent = new Intent(this, ChatActivity.class);
            intent.putExtra("Notification",user);
        }

        /*intent = new Intent(this, NotificationActivity.class);*/
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT);
        // Set the intent that will fire when the user taps the notification.
        builder.setContentIntent(pendingIntent);

        // Large icon appears on the left of the notification
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_app_icon));

        // Content title, which appears in large type at the top of the notification
        builder.setContentTitle(Title);

        // Content text, which appears in smaller text below the title
        if(Type.equalsIgnoreCase(GlobalVariables.TYPE_IMAGE))
            builder.setContentText("Image");
        else
            builder.setContentText(Text);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification=builder.build();
        notification.defaults = Notification.DEFAULT_SOUND
                | Notification.DEFAULT_VIBRATE;
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        // Will display the notification in the notification bar
        notificationManager.notify(Constants.NOTIFICATION_ID, notification);
    }

}
