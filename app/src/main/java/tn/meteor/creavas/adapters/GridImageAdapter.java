package tn.meteor.creavas.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.joanzapata.iconify.widget.IconTextView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import tn.meteor.creavas.R;
import tn.meteor.creavas.models.Post;
import tn.meteor.creavas.utils.Constants;
import tn.meteor.creavas.utils.SquareImageView;


public class GridImageAdapter extends RecyclerView.Adapter<GridImageAdapter.PostHolder> {

    private Context mContext;
    private List<Post> postList;
    public GridAdapterListener listener;

    public class PostHolder extends RecyclerView.ViewHolder {
        public TextView title, count, comments;
        public ImageView overflow;
        public ProgressBar mProgressBar;
        public CircleImageView userpic;
        public IconTextView description;
        public SquareImageView thumbnail;


        public PostHolder(View view) {
            super(view);
            thumbnail = (SquareImageView) view.findViewById(R.id.thumbnail);
            mProgressBar = (ProgressBar) view.findViewById(R.id.mProgressBar);
            description = (IconTextView) view.findViewById(R.id.descrption);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onPostSelected(postList.get(getAdapterPosition()));
                }
            });


            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    listener.onPostLongClickSelected(postList.get(getAdapterPosition()));
                    return false;
                }
            });
        }
    }

    public GridImageAdapter(Context mContext, GridAdapterListener listener, List<Post> postList) {
        this.mContext = mContext;
        this.listener = listener;
        this.postList = postList;
    }

    @Override
    public PostHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_grid_imageview, parent, false);

        return new PostHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final PostHolder holder, int position) {
        Post post = postList.get(position);
        ImageLoader imageLoader = ImageLoader.getInstance();
        holder.description.setText(post.getLikes() + " {md-favorite}" + "   " + post.getComments() + " {md-comment}");
        imageLoader.displayImage(Constants.HTTP.IMAGE_URL + "templates/" + post.getMedia(), holder.thumbnail, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                if (holder.mProgressBar != null) {
                    holder.mProgressBar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                if (holder.mProgressBar != null) {
                    holder.mProgressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if (holder.mProgressBar != null) {
                    holder.mProgressBar.setVisibility(View.GONE);

                }
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                if (holder.mProgressBar != null) {
                    holder.mProgressBar.setVisibility(View.GONE);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return postList.size();
    }


    public interface GridAdapterListener {
        void onPostSelected(Post post);
        void onPostLongClickSelected(Post post);
    }

}
