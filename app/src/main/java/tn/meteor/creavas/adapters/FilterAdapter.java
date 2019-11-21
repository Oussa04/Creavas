package tn.meteor.creavas.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.alhazmy13.imagefilter.ImageFilter;

import java.util.ArrayList;

import tn.meteor.creavas.R;
import tn.meteor.creavas.models.Filters;

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.SingleItemRowHolder> {

    private ArrayList<Filters> itemsList;
    private Context mContext;
private FilterAdapterListener listener;
    public FilterAdapter(Context context, ArrayList<Filters> itemsList,FilterAdapterListener listener) {
        this.itemsList = itemsList;
        this.mContext = context;
        this.listener = listener;

    }

    @Override
    public SingleItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_single_filter, null);
        SingleItemRowHolder mh = new SingleItemRowHolder(v);
        return mh;
    }

    @Override
    public void onBindViewHolder(SingleItemRowHolder holder, int i) {

        Filters singleItem = itemsList.get(i);

        holder.tvTitle.setText(singleItem.getName());

        holder.itemImage.setImageBitmap(ImageFilter.applyFilter(singleItem.getPreview(), singleItem.getType()));


    }

    @Override
    public int getItemCount() {
        return (null != itemsList ? itemsList.size() : 0);
    }

    public interface FilterAdapterListener {
        void onFilterSilected(Filters filter);
    }

    public class SingleItemRowHolder extends RecyclerView.ViewHolder {

        protected TextView tvTitle;

        protected ImageView itemImage;


        public SingleItemRowHolder(View view) {
            super(view);

            this.tvTitle = (TextView) view.findViewById(R.id.tvTitle);
            this.itemImage = (ImageView) view.findViewById(R.id.itemImage);


            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    listener.onFilterSilected(itemsList.get(getAdapterPosition()));


                }
            });


        }

    }

}