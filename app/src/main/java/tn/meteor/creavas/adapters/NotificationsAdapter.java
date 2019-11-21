package tn.meteor.creavas.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import tn.meteor.creavas.R;
import tn.meteor.creavas.models.Notification;
import tn.meteor.creavas.utils.Constants;


public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.MyViewHolder> {
    private Context context;
    private List<Notification> notificationList;
    private NotificationsAdapterListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView notificationdesc, time;
        public CircleImageView userimage;
        public ImageView postImage;

        public MyViewHolder(View view) {
            super(view);
            notificationdesc = view.findViewById(R.id.notificationdesc);
            time = view.findViewById(R.id.time);
            userimage = view.findViewById(R.id.userimage);
            postImage = view.findViewById(R.id.postImage);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onNotificationSelected(notificationList.get(getAdapterPosition()));
                }
            });

            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    listener.onNotificationLongClick(notificationList.get(getAdapterPosition()));
                    return false;
                }
            });
        }
    }

    public void addData(List<Notification> notificationList) {
        this.notificationList = notificationList;
        notifyDataSetChanged();
    }

    public NotificationsAdapter(Context context, List<Notification> notificationList, NotificationsAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.notificationList = notificationList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Notification notification = notificationList.get(position);
        if (notification.getNotification_type().equals("like")) {

            holder.notificationdesc.setText("@" + notification.getUsername() + " liked your post");

        } else if (notification.getNotification_type().equals("comment")) {
            holder.notificationdesc.setText("@" + notification.getUsername() + " commented your post");

        } else if (notification.getNotification_type().equals("follow")) {
            holder.notificationdesc.setText("@" + notification.getUsername() + " started following you");

            holder.postImage.setVisibility(View.INVISIBLE);


        }
        holder.time.setText(notification.getDate_notification());
        ImageLoader imageLoader = ImageLoader.getInstance();
//        Constants.HTTP.IMAGE_URL+"/users/"+user.getProfile_pic_name()
        String url;
        if (notification.getUser_image().contains("https://") || notification.getUser_image().contains("http://")) {
            //image taken from google+
            url = notification.getUser_image();
        } else {
            url = Constants.HTTP.IMAGE_URL + "users/" + notification.getUser_image();
        }
        imageLoader.displayImage(url, holder.userimage, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
//                if (holder.mProgressBar != null) {
//                    holder.mProgressBar.setVisibility(View.VISIBLE);
//                }
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
//                if (holder.mProgressBar != null) {
//                    holder.mProgressBar.setVisibility(View.GONE);
//                }
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                if (holder.mProgressBar != null) {
//                    holder.mProgressBar.setVisibility(View.GONE);
//
//                }
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
//                if (holder.mProgressBar != null) {
//                    holder.mProgressBar.setVisibility(View.GONE);
//                }
            }
        });

        if (!notification.getNotification_type().equals("follow")) {

            imageLoader.displayImage(Constants.HTTP.IMAGE_URL + "templates/" + notification.getPost_thumbnail(), holder.postImage, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
//                if (holder.mProgressBar != null) {
//                    holder.mProgressBar.setVisibility(View.VISIBLE);
//                }
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
//                if (holder.mProgressBar != null) {
//                    holder.mProgressBar.setVisibility(View.GONE);
//                }
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                if (holder.mProgressBar != null) {
//                    holder.mProgressBar.setVisibility(View.GONE);
//
//                }
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
//                if (holder.mProgressBar != null) {
//                    holder.mProgressBar.setVisibility(View.GONE);
//                }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

//


    public interface NotificationsAdapterListener {
        void onNotificationSelected(Notification notification);
        void onNotificationLongClick(Notification notification);
    }

}
