package tn.meteor.creavas.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.JsonObject;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.volokh.danylo.hashtaghelper.HashTagHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Random;

import at.markushi.ui.CircleButton;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tn.meteor.creavas.R;
import tn.meteor.creavas.kitchen.widget.MotionView;
import tn.meteor.creavas.models.LayerDB;
import tn.meteor.creavas.models.LayerDB_Table;
import tn.meteor.creavas.models.Template;
import tn.meteor.creavas.models.Template_Table;
import tn.meteor.creavas.services.PostsClient;
import tn.meteor.creavas.services.ServiceFactory;
import tn.meteor.creavas.services.UploadClient;
import tn.meteor.creavas.services.response.Result;
import tn.meteor.creavas.services.response.ResultPost;
import tn.meteor.creavas.utils.AnimatedGIFWriter;
import tn.meteor.creavas.utils.CacheStore;
import tn.meteor.creavas.utils.ImageUtils;

public class SaveCreavasActivity extends AppCompatActivity {
    @BindView(R.id.thumbnail)
    ImageView thumbnail;

    @BindView(R.id.image)
    CircleButton image;
    @BindView(R.id.gif)
    CircleButton gif;


    @BindView(R.id.share)
    CircleButton share;

    private Switch shareTemplate;
    String imageUriFiltered;

    @BindView(R.id.post)
    CircleButton post;
    private FirebaseAuth mAuth;
    String imageuri;
    Intent intent;

    Bitmap imgeToShare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_creavas);
        CacheStore cacheStore = CacheStore.getInstance();
        intent = getIntent();
        mAuth = FirebaseAuth.getInstance();
        ButterKnife.bind(this);
//        getSupportActionBar().hide();
        imageuri= intent.getStringExtra("imageuri");
        imageUriFiltered = intent.getStringExtra("imageUriFiltered");

        imgeToShare = cacheStore.getCacheFile(imageUriFiltered + "");

        thumbnail.setImageBitmap(imgeToShare);
        image.setOnClickListener(view -> ImageUtils.saveImage(imgeToShare));

        post.setOnClickListener(view -> showPostDialog());


        gif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    generateGif();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


    }


    TextView description;


    MaterialSpinner theme;
    MaterialDialog dialog;

    private HashTagHelper mTextHashTagHelper;
    private View positiveAction;

    private void showPostDialog() {


        dialog =
                new MaterialDialog.Builder(this)
                        .title("Share a post")
                        .customView(R.layout.activity_share_post, true)
                        .positiveText("Post")
                        .negativeText(android.R.string.cancel)
                        .onPositive(
                                (dialog1, which) -> Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show())
                        .build();

        positiveAction = dialog.getActionButton(DialogAction.POSITIVE);

        //noinspection ConstantConditions
        description = dialog.getCustomView().findViewById(R.id.description);
        theme = dialog.getCustomView().findViewById(R.id.theme);
        shareTemplate = (Switch) dialog.getCustomView().findViewById(R.id.shareTemplate);

        ArrayAdapter<CharSequence> staticAdapter = ArrayAdapter
                .createFromResource(this, R.array.theme,
                        android.R.layout.simple_spinner_item);
        staticAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        theme.setAdapter(staticAdapter);

        theme.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                selectedTheme = item;
            }
        });
        mTextHashTagHelper = HashTagHelper.Creator.create(getResources().getColor(R.color.colorAccent), null);
        mTextHashTagHelper.handle(description);
        dialog.show();
        // disabled by default
        positiveAction.setEnabled(true);
        positiveAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addPostToDB();


            }
        });
    }

    String selectedTheme = "";

    private void addPostToDB() {
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading ...");
        progressDialog.show();
        JsonObject parameters = new JsonObject();
        Template template = SQLite.select(Template_Table.ALL_COLUMN_PROPERTIES).from(Template.class).where(Template_Table.id.is(Integer.parseInt(imageuri))).querySingle();


        if (shareTemplate.isChecked()) {
            parameters.addProperty("public", "true");
            parameters.addProperty("idUser", mAuth.getUid());
            parameters.addProperty("description", description.getText().toString());
            parameters.addProperty("theme", selectedTheme);
            parameters.addProperty("type", "image");
            parameters.addProperty("backgroundColor", template.getBackgroundColor());
            parameters.addProperty("aspectRatio", template.getAspectRatio());

        } else {
            parameters.addProperty("public", "false");
            parameters.addProperty("idUser", mAuth.getUid());
            parameters.addProperty("description", description.getText().toString());
            parameters.addProperty("theme", selectedTheme);
            parameters.addProperty("type", "image");
        }

        int n = 1000000;
        Random rand = new Random();
        String name = mAuth.getUid() + rand.nextInt(n) + ".png";
        parameters.addProperty("media", name);


        CacheStore cacheStore = CacheStore.getInstance();

        try {
            uploadMedia(cacheStore.getFileUri(imageUriFiltered + ""), name);
        } catch (IOException e) {
            e.printStackTrace();
        }
        PostsClient client = ServiceFactory.getApiClient().create(PostsClient.class);
        Call<ResultPost> call = client.addPost(parameters);
        call.enqueue(new Callback<ResultPost>() {
            @Override
            public void onResponse(Call<ResultPost> call, Response<ResultPost> response) {
                Log.e("--------------------->", response.body().toString());
                int x = response.body().getIdTemplate();
                if (response.body().getIdTemplate() > 0) {
                    List<LayerDB> layerList = SQLite.select().
                            from(LayerDB.class).where(LayerDB_Table.idTemplate.is(template.getId())).queryList();
                    int listSize = layerList.size();
                    for (int i = 0; i < listSize; i++) {
                        layerList.get(i).setIdTemplate(x);
                        if (layerList.get(i).getType().equals("image")) {
                            try {
                                layerList.get(i).setImageLink("template-" + layerList.get(i).getIdTemplate() + "-layer-" + layerList.get(i).getLayerOrder() + ".png");
                                uploadMedia(cacheStore.getFileUri(imageuri + "-layer-" + i), "template-" + layerList.get(i).getIdTemplate() + "-layer-" + layerList.get(i).getLayerOrder() + ".png");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                    Call<Result> call2 = client.addLayers(layerList);
                    call2.enqueue(new Callback<Result>() {
                        @Override
                        public void onResponse(Call<Result> call, Response<Result> response) {

                            if (response.body().getResult().equals("success")) {

                                progressDialog.dismiss();
                                dialog.dismiss();

                            }
                        }

                        @Override
                        public void onFailure(Call<Result> call, Throwable t) {

                        }
                    });


                } else if (response.body().getIdTemplate() == 0) {

                    progressDialog.dismiss();


                }


            }

            @Override
            public void onFailure(Call<ResultPost> call, Throwable t) {

            }
        });

    }

    private void uploadMedia(File cacheFile, String name) throws IOException {

        CacheStore cacheStore = CacheStore.getInstance();
        UploadClient client = ServiceFactory.getApiClient().create(UploadClient.class);
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), cacheFile);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", name, requestFile);
        Call<Result> resultCall = client.uploadMedia(body);
        resultCall.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {


                // Response Success or Fail
                if (response.isSuccessful()) {
                    if (response.body().getResult().equals("success"))
                        Toast.makeText(SaveCreavasActivity.this, "SUCCESS", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(SaveCreavasActivity.this, "FAIL / SUCCESS", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(SaveCreavasActivity.this, "FAIL KHLAS", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {

            }
        });


    }

    private MaterialDialog gifDialog;
    int noof = 1;

    private void generateGif() throws Exception {


        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();


        AnimatedGIFWriter writer = new AnimatedGIFWriter(true);
        OutputStream os = new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath() + "/gif-" + noof + ".gif");
        MotionView motionView = new MotionView(this);


        Bitmap bitmap = motionView.getThumbnailImage(Color.MAGENTA);
        Bitmap bitmap2 = motionView.getThumbnailImage(Color.CYAN);
        Bitmap bitmap3 = motionView.getThumbnailImage(Color.RED);
        Bitmap bitmap4 = motionView.getThumbnailImage(Color.WHITE);

        writer.prepareForWrite(os, -1, -1);
        writer.writeFrame(os, bitmap);
        writer.writeFrame(os, bitmap2);
        writer.writeFrame(os, bitmap3);
        writer.writeFrame(os, bitmap4);
        writer.finishWrite(os);

        progressDialog.dismiss();
        //   showgifpreview();


    }


    private void showgifpreview() {

        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/gif-" + noof + ".gif");

        gifDialog =
                new MaterialDialog.Builder(this)
                        .customView(R.layout.layout_gif_custom, true)
                        .build();
        GifImageView gifvirw = (GifImageView) findViewById(R.id.gify);

        try {
            File x = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/gif-" + 1 + ".gif");
            GifDrawable a = new GifDrawable(x);
            gifvirw.setImageDrawable(a);
        } catch (IOException e) {
            e.printStackTrace();
        }

        gifDialog.show();
    }

}
