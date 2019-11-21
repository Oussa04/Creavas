package tn.meteor.creavas.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import tn.meteor.creavas.R;
import tn.meteor.creavas.models.Comment;
import tn.meteor.creavas.utils.Constants;


/**
 * Created by Oussaa on 12/12/2017.
 */

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentHolder> {
    private Context context;
    private List<Comment> commentList;
    public CommentDeleteListener listener;

    public class CommentHolder extends RecyclerView.ViewHolder {
        public TextView username, comment;
        public ImageButton delete;
        public CircleImageView thumbnail;

        public CommentHolder(View view) {
            super(view);
            username = view.findViewById(R.id.username);
            comment = view.findViewById(R.id.comment);
            thumbnail = view.findViewById(R.id.userimage);
            delete = view.findViewById(R.id.delete);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected contact in callbackTo
                    // Toast.makeText(view.getContext(), "Hello", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    public CommentAdapter(Context context, CommentDeleteListener listener,List<Comment> commentList) {
        this.context = context;
        this.commentList = commentList;
        this.listener = listener;
    }

    @Override
    public CommentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comment, parent, false);

        return new CommentHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CommentHolder holder, int position) {
        Comment comment = commentList.get(position);
        holder.username.setText(comment.getUsername());
        holder.comment.setText(comment.getText());
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (!auth.getUid().equals(comment.getIdUser())) {
            holder.delete.setVisibility(View.GONE);
        } else {
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onCommentDeleted(commentList.get(position));
                }
            });

        }


        ImageLoader imageLoader = ImageLoader.getInstance();
//        Constants.HTTP.IMAGE_URL+"/users/"+user.getProfile_pic_name()
        String url;
        if (comment.getUserImage().contains("https://") || comment.getUserImage().contains("http://")) {
            //image taken from google+
            url = comment.getUserImage();
        } else {
            url = Constants.HTTP.IMAGE_URL + "users/" + comment.getUserImage();
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
        return commentList.size();
    }

    public interface CommentDeleteListener {
        void onCommentDeleted(Comment comment);
    }
}
