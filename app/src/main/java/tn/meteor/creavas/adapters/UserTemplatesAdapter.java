package tn.meteor.creavas.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.joanzapata.iconify.widget.IconTextView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import tn.meteor.creavas.R;
import tn.meteor.creavas.models.Template;
import tn.meteor.creavas.utils.CacheStore;
import tn.meteor.creavas.utils.SquareImageView;


/**
 * Created by User on 6/4/2017.
 */

public class UserTemplatesAdapter extends ArrayAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private int layoutResource;
    private String mAppend;
    private List<Template> templates;

    public UserTemplatesAdapter(Context context, int layoutResource, String append, List<Template> templates) {
        super(context, layoutResource, templates);

        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;
        this.layoutResource = layoutResource;
        mAppend = append;
        this.templates = templates;
    }

    private static class ViewHolder {
        SquareImageView image;
        ProgressBar mProgressBar;
        IconTextView descrption;
        ImageView gradient;
        // ImageButton likeButton;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        /*
        Viewholder build pattern (Similar to recyclerview)
         */
        final ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(layoutResource, parent, false);
            holder = new ViewHolder();
            holder.mProgressBar = (ProgressBar) convertView.findViewById(R.id.mProgressBar);
            holder.image = (SquareImageView) convertView.findViewById(R.id.thumbnail);
            holder.descrption = (IconTextView) convertView.findViewById(R.id.descrption);
            // holder.gradient = (ImageView) convertView.findViewById(R.id.gradient);
            //  holder.likeButton = (ImageButton) convertView.findViewById(R.id.like);


            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Template template = (Template) getItem(position);
        ImageLoader imageLoader = ImageLoader.getInstance();

        holder.descrption.setText("");
        CacheStore cacheStore = CacheStore.getInstance();
        holder.image.setImageBitmap(cacheStore.getCacheFile(template.getId() + ""));
//        holder.likeButton.setOnClickListener(new View.OnClickListener()        {
//            @Override
//            public void onClick(View view) {
//                Intent i = new Intent(mContext, CreavasActivity.class);
//
//                i.putExtra("template", template);
//
//                view.getContext().startActivity(i);
//
//                Toast.makeText(mContext, template.getId() + "", Toast.LENGTH_LONG).show();
//            }
//        });

//
//        imageLoader.displayImage(cacheStore.getCacheFile(template.getId()+"").get, holder.image, new ImageLoadingListener() {
//            @Override
//            public void onLoadingStarted(String imageUri, View view) {
//                if (holder.mProgressBar != null) {
//                    holder.mProgressBar.setVisibility(View.VISIBLE);
//                }
//            }
//
//            @Override
//            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
//                if (holder.mProgressBar != null) {
//                    holder.mProgressBar.setVisibility(View.GONE);
//                }
//            }
//
//            @Override
//            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                if (holder.mProgressBar != null) {
//                    holder.mProgressBar.setVisibility(View.GONE);
//
//                }
//            }
//
//            @Override
//            public void onLoadingCancelled(String imageUri, View view) {
//                if (holder.mProgressBar != null) {
//                    holder.mProgressBar.setVisibility(View.GONE);
//                }
//            }
//        });
//
        return convertView;
    }
}



















