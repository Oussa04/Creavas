package tn.meteor.creavas.kitchen.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import tn.meteor.creavas.R;


/**
 * selects sticker
 * result - Integer, resource id of the sticker, bundled at key EXTRA_STICKER_ID
 * <p>
 * Stickers borrowed from : http://www.flaticon.com/packs/pokemon-go
 */

public class StickerSelectActivity extends AppCompatActivity {

    public static final String EXTRA_STICKER_ID = "extra_sticker_id";

    private final int[] stickerIds = {
            R.drawable.map,
            R.drawable.abra,
            R.drawable.bellsprout,
            R.drawable.bracelet,
            R.drawable.bullbasaur,
            R.drawable.camera,
            R.drawable.candy,
            R.drawable.caterpie,
            R.drawable.charmander,

            R.drawable.mankey,

    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_sticker_activity);


        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.stickers_recycler_view);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(3, LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);

        recyclerView.setNestedScrollingEnabled(false);

        List<Integer> stickers = new ArrayList<>(stickerIds.length);
        for (Integer id : stickerIds) {
            stickers.add(id);
        }

        recyclerView.setAdapter(new StickersAdapter(stickers, this));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onStickerSelected(int stickerId) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_STICKER_ID, stickerId);
        setResult(RESULT_OK, intent);
        finish();
    }

    class StickersAdapter extends RecyclerView.Adapter<StickersAdapter.StickerViewHolder> {

        private final List<Integer> stickerIds;
        private final Context context;
        private final LayoutInflater layoutInflater;

        StickersAdapter(@NonNull List<Integer> stickerIds, @NonNull Context context) {
            this.stickerIds = stickerIds;
            this.context = context;
            this.layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public StickerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new StickerViewHolder(layoutInflater.inflate(R.layout.sticker_item, parent, false));
        }

        @Override
        public void onBindViewHolder(StickerViewHolder holder, int position) {
            holder.image.setImageDrawable(ContextCompat.getDrawable(context, getItem(position)));
        }

        @Override
        public int getItemCount() {
            return stickerIds.size();
        }

        private int getItem(int position) {
            return stickerIds.get(position);
        }

        class StickerViewHolder extends RecyclerView.ViewHolder {

            ImageView image;

            StickerViewHolder(View itemView) {
                super(itemView);
                image = (ImageView) itemView.findViewById(R.id.sticker_image);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int pos = getAdapterPosition();
                        if (pos >= 0) {
                            onStickerSelected(getItem(pos));
                        }
                    }
                });
            }
        }
    }
}
