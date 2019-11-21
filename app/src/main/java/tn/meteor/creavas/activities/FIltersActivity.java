package tn.meteor.creavas.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import net.alhazmy13.imagefilter.ImageFilter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import tn.meteor.creavas.R;
import tn.meteor.creavas.adapters.FilterAdapter;
import tn.meteor.creavas.models.Filters;
import tn.meteor.creavas.utils.CacheStore;
import tn.meteor.creavas.utils.Constants;

public class FIltersActivity extends AppCompatActivity implements FilterAdapter.FilterAdapterListener {


    @BindView(R.id.filteredImage)
    ImageView filteredImage;

    @BindView(R.id.filters)
    RecyclerView filters;

    @BindView(R.id.done)
    Button done;

    String imageuri;
    private ArrayList<Filters> filtersList;
    private Bitmap image;
    private Bitmap original;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_filters);

        ButterKnife.bind(this);

        CacheStore cacheStore = CacheStore.getInstance();
        Intent intent = getIntent();
        filtersList = new ArrayList<Filters>();

        imageuri = intent.getStringExtra("imageuri");
        original = cacheStore.getCacheFile(imageuri + "");

        filteredImage.setImageBitmap(original);
        filteredBitmap=original;

        Bitmap imagetocompress = original;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        imagetocompress.compress(Bitmap.CompressFormat.JPEG, 100, out);

        image = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));

        Filters filter = new Filters();
        filter.setName("Original");
        filter.setPreview(imagetocompress);
        filter.setType(ImageFilter.Filter.SHARPEN);
        filtersList.add(filter);

        Filters filter1 = new Filters();
        filter1.setName("B&W");
        filter1.setPreview(image);
        filter1.setType(ImageFilter.Filter.GRAY);
        filtersList.add(filter1);

        Filters filter2 = new Filters();
        filter2.setName("Sketch");
        filter2.setPreview(image);
        filter2.setType(ImageFilter.Filter.SKETCH);
        filtersList.add(filter2);

        Filters filter3 = new Filters();
        filter3.setName("Gotham");
        filter3.setPreview(image);
        filter3.setType(ImageFilter.Filter.GOTHAM);
        filtersList.add(filter3);

        Filters filter5 = new Filters();
        filter5.setName("Block");
        filter5.setPreview(image);
        filter5.setType(ImageFilter.Filter.BLOCK);
        filtersList.add(filter5);

        Filters filter6 = new Filters();
        filter6.setName("Old");
        filter6.setPreview(image);
        filter6.setType(ImageFilter.Filter.OLD);
        filtersList.add(filter6);

        Filters filter7 = new Filters();
        filter7.setName("Light");
        filter7.setPreview(image);
        filter7.setType(ImageFilter.Filter.LIGHT);
        filtersList.add(filter7);

        Filters filter8 = new Filters();
        filter8.setName("Neon");
        filter8.setPreview(image);
        filter8.setType(ImageFilter.Filter.NEON);
        filtersList.add(filter8);

        FilterAdapter itemListDataAdapter = new FilterAdapter(this, filtersList, this);

        filters.setHasFixedSize(true);
        filters.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        filters.setAdapter(itemListDataAdapter);


        filters.setNestedScrollingEnabled(false);

        done = (Button) findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(FIltersActivity.this, SaveCreavasActivity.class);

                intent2.putExtra("imageuri", imageuri);
                intent2.putExtra("imageUriFiltered",imageuri+"-filtered");
                cacheStore.saveCacheFile(imageuri+"-filtered", filteredBitmap, Constants.IMAGE.HIGH_QUALITY);

                startActivity(intent2);
            }
        });
    }

    private Bitmap filteredBitmap;
    @Override
    public void onFilterSilected (Filters filter){

        filteredBitmap = ImageFilter.applyFilter(original, filter.getType());
        filteredImage.setImageBitmap(filteredBitmap);


    }
}