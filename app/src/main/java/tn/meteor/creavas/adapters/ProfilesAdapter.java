package tn.meteor.creavas.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import tn.meteor.creavas.R;
import tn.meteor.creavas.models.User;
import tn.meteor.creavas.utils.Constants;

/**
 * Created by ravi on 16/11/17.
 */

public class ProfilesAdapter extends RecyclerView.Adapter<ProfilesAdapter.MyViewHolder> {
    private Context context;
    private List<User> contactList;
    private List<User> contactListFiltered;
    private ContactsAdapterListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, username;
        public CircleImageView thumbnail;

        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            username = view.findViewById(R.id.phone);
            thumbnail = view.findViewById(R.id.userimage);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onUserSelected(contactList.get(getAdapterPosition()));
                }
            });
        }
    }

    public void addData(List<User> contactList) {
        this.contactList = contactList;
        notifyDataSetChanged();
    }

    public ProfilesAdapter(Context context, List<User> contactList, ContactsAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.contactList = contactList;
        this.contactListFiltered = contactList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final User user = contactListFiltered.get(position);
        holder.name.setText(user.getName());
        holder.username.setText(user.getUsername() + " - " + user.getPosts() + " creava");

        ImageLoader imageLoader = ImageLoader.getInstance();
//        Constants.HTTP.IMAGE_URL+"/users/"+user.getProfile_pic_name()
        String url;
        if (user.getProfile_pic_name().contains("https://") || user.getProfile_pic_name().contains("http://")) {
            //image taken from google+
            url = user.getProfile_pic_name();
        } else {
            url = Constants.HTTP.IMAGE_URL + "users/" + user.getProfile_pic_name();
        }
        imageLoader.displayImage(url, holder.thumbnail, new ImageLoadingListener() {
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

    @Override
    public int getItemCount() {
        return contactListFiltered.size();
    }


    public interface ContactsAdapterListener {
        void onUserSelected(User user);
    }
}
