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


public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.PostHolder> {

    private Context mContext;
    private List<Post> albumList;
    public HomeAdapterListener listener;

    public class PostHolder extends RecyclerView.ViewHolder {
        public TextView title, count, description;
        public ImageView thumbnail, overflow;
        public ProgressBar mProgressBar;
        public CircleImageView userpic;


        public PostHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            mProgressBar = (ProgressBar) view.findViewById(R.id.mProgressBar);
            userpic = (CircleImageView) view.findViewById(R.id.userpic);
            description = (IconTextView) view.findViewById(R.id.descrption);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected contact in callback
                    listener.onPostSelected(albumList.get(getAdapterPosition()));
                }
            });


        }
    }

    public HomeAdapter(Context mContext, HomeAdapterListener listener, List<Post> postList) {
        this.mContext = mContext;
        this.listener = listener;
        this.albumList = postList;
    }

    @Override
    public PostHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_post, parent, false);

        return new PostHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final PostHolder holder, int position) {
        Post post = albumList.get(position);
        holder.title.setText(post.getUsername());
        holder.description.setText(post.getLikes() + " {md-favorite}" + "   " + post.getComments() + " {md-comment}");
        ImageLoader imageLoader = ImageLoader.getInstance();

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
        if (post.getUserImage().contains("https://") || post.getUserImage().contains("http://")) {
            imageLoader.displayImage(post.getUserImage(), holder.userpic, new ImageLoadingListener() {
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

        } else {

            imageLoader.displayImage(Constants.HTTP.IMAGE_URL + "users/" + post.getUserImage(), holder.userpic, new ImageLoadingListener() {
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

    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }


    public interface HomeAdapterListener {
        void onPostSelected(Post post);
    }

}
