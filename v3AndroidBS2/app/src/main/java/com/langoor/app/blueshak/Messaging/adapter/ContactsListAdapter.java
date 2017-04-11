package com.langoor.app.blueshak.Messaging.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.langoor.blueshak.R;
import com.langoor.app.blueshak.Messaging.activity.ChatActivity;
import com.langoor.app.blueshak.Messaging.data.User;
import com.langoor.app.blueshak.global.GlobalVariables;
import com.langoor.app.blueshak.helper.RoundedImageView;
import com.langoor.app.blueshak.services.model.ContactModel;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.squareup.picasso.Picasso;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class ContactsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "ContactsListAdapter";
    private Context context;
    private List<ContactModel> albumList;
    public String active_tab;
    private static String today;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        protected TextView lastTextMessage,/*TimeStamp,unreadCount,*/name,last_text_message_not_read;
        protected RoundedImageView profilePic;
        protected ImageView item_image,/*product_image,*/image_type;
        public CheckBox chkSelected;
        public RadioButton is_read;

        LinearLayout item_view;
        protected  View view;
        public MyViewHolder(View convertView) {
            super(convertView);
            view=convertView;
            profilePic = (RoundedImageView) convertView.findViewById(R.id.profile_pic);
            is_read=(RadioButton)convertView.findViewById(R.id.is_read);
        /*    product_image = (ImageView) convertView.findViewById(R.id.product_image);*/
            lastTextMessage=(TextView)convertView.findViewById(R.id.last_text_message);
            last_text_message_not_read=(TextView)convertView.findViewById(R.id.last_text_message_not_read);
         /*   TimeStamp=(TextView)convertView.findViewById(R.id.message_timestamp);*/
            /*unreadCount=(TextView)convertView.findViewById(R.id.unread_count);*/
            name = (TextView) convertView.findViewById(R.id.contact_name);
            image_type = (ImageView) convertView.findViewById(R.id.image_type);
        }
    }

    public ContactsListAdapter(Context mContext, List<ContactModel> albumList, String active_tab) {
        this.context = mContext;
        this.albumList = albumList;
        this.active_tab=active_tab;
        Calendar calendar = Calendar.getInstance();
        today = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_chat_list_listview_item, parent, false);
        return new MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder view_holder, int position) {
        try{
            final int pos = position;
            if(view_holder instanceof MyViewHolder){
                MyViewHolder myViewHolder=(MyViewHolder)view_holder;
                final ContactModel contactModel=albumList.get(pos);
              /*  myViewHolder.TimeStamp.setText(getTimeStamp(contactModel.getCreated_at()));*/
                if(contactModel.getMessage_type().equalsIgnoreCase(GlobalVariables.TYPE_IMAGE)){
                   /* myViewHolder.lastTextMessage.setText("Photo");*/
                    myViewHolder.image_type.setVisibility(View.VISIBLE);
                    myViewHolder.lastTextMessage.setVisibility(View.GONE);
                    myViewHolder.last_text_message_not_read.setVisibility(View.GONE);

                    if(!contactModel.is_read()){
                        myViewHolder.is_read.setChecked(true);
                    }
                }else{
                    myViewHolder.lastTextMessage.setText(contactModel.getMessage());
                    myViewHolder.last_text_message_not_read.setText(contactModel.getMessage());
                    if(myViewHolder.image_type.getVisibility()==View.VISIBLE)
                        myViewHolder.image_type.setVisibility(View.GONE);
                    if(!contactModel.is_read()){
                        myViewHolder.lastTextMessage.setVisibility(View.GONE);
                        myViewHolder.last_text_message_not_read.setVisibility(View.VISIBLE);
                        myViewHolder.is_read.setChecked(true);
                    }else{
                        myViewHolder.lastTextMessage.setVisibility(View.VISIBLE);
                    }
                }
                myViewHolder.name.setText(contactModel.getContact_name());
                String profilePic=contactModel.getContact_image();
             /*   ImageLoader imageLoader = ImageLoader.getInstance();
                DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                        .cacheOnDisc(true).resetViewBeforeLoading(true)
                        .showImageForEmptyUri(R.drawable.squareplaceholder)
                        .showImageOnFail(R.drawable.squareplaceholder)
                        .imageScaleType(ImageScaleType.NONE_SAFE)
                        .showImageOnLoading(R.drawable.squareplaceholder).build();
                //download and display image from url
                imageLoader.displayImage(profilePic,myViewHolder.profilePic, options);*/
                /* String product_url=contactModel.getProduct_image();
                        //download and display image from url
                imageLoader.displayImage(product_url,myViewHolder.product_image, options);*/
                if(!TextUtils.isEmpty(profilePic)){
                    Picasso.with(context)
                            .load(profilePic)
                            .placeholder(R.drawable.squareplaceholder)
                            .fit().centerCrop()
                            .into(myViewHolder.profilePic);
                }else{
                    myViewHolder.profilePic.setImageResource(R.drawable.squareplaceholder);
                }
                ((MyViewHolder) view_holder).view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        User user=new User();
                        user.setName(contactModel.getContact_name());
                        if(active_tab.equalsIgnoreCase(GlobalVariables.TYPE_SELLER_TAB))
                            user.setBs_id(contactModel.getSeller_id());
                        else
                            user.setBs_id(contactModel.getBuyer_id());
                        /*user.setBs_id(contactModel.getSeller_id());*/
                        user.setProfileImageUrl(contactModel.getContact_image());
                        user.setProduct_id(contactModel.getActive_product_id());
                        user.setActive_tab(active_tab);
                        user.setConversation_id(contactModel.getConversation_id());
                        Intent intent= ChatActivity.newInstance(context,user);
                        context.startActivity(intent);
                    }
                });
            }
        }  catch (NullPointerException e){
            e.printStackTrace();
            Crashlytics.log(e.getMessage());
        }catch (NumberFormatException e) {
            e.printStackTrace();
            Crashlytics.log(e.getMessage());
        }catch (ArrayIndexOutOfBoundsException e){
            e.printStackTrace();
            Crashlytics.log(e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            Crashlytics.log(e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }
    // method to access in activity after updating selection
    public List<ContactModel> getProductList() {
        return albumList;
    }

    public static String getTimeStamp(String dateStr) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        /*format.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));*/
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        String timestamp = "";
        today = today.length() < 2 ? "0" + today : today;
        try {
            Date date = format.parse(dateStr);
            SimpleDateFormat todayFormat = new SimpleDateFormat("dd");
            String dateToday = todayFormat.format(date);
            format = dateToday.equals(today) ? new SimpleDateFormat("hh:mm a") : new SimpleDateFormat("dd LLL, hh:mm a");
            String date1 = format.format(date);
            timestamp = date1.toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timestamp;
    }
}
