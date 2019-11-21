package tn.meteor.creavas.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.squareup.picasso.Picasso;
import com.volokh.danylo.hashtaghelper.HashTagHelper;

import java.util.ArrayList;
import java.util.List;

import at.markushi.ui.CircleButton;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tn.meteor.creavas.R;
import tn.meteor.creavas.activities.MainHomeActivity;
import tn.meteor.creavas.activities.UserCreavasActivity;
import tn.meteor.creavas.adapters.CommentAdapter;
import tn.meteor.creavas.models.Comment;
import tn.meteor.creavas.models.LayerDB;
import tn.meteor.creavas.models.Post;
import tn.meteor.creavas.models.Template;
import tn.meteor.creavas.models.User;
import tn.meteor.creavas.services.CommentsClient;
import tn.meteor.creavas.services.Postlike;
import tn.meteor.creavas.services.PostsClient;
import tn.meteor.creavas.services.ServiceFactory;
import tn.meteor.creavas.services.response.PostlikeResult;
import tn.meteor.creavas.services.response.Result;
import tn.meteor.creavas.utils.CacheStore;
import tn.meteor.creavas.utils.Constants;

/**
 * Created by Oussaa on 11/12/2017.
 */

public class PostFragment extends Fragment implements CommentAdapter.CommentDeleteListener {

    Unbinder unbinder;

    private ImageView post;
    public CircleImageView userpic;
    public CircleButton like;
    public CircleButton comment;
    public CircleButton useTemplate;
    RecyclerView commentsRecyclerView;
    List<Comment> commentList;
    CommentAdapter commentAdapter;
    private TextView description, username, date;
    Post thePost;
    View v;
    private boolean liked = false;

    public static PostFragment newInstance() {
        PostFragment fragment = new PostFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        mAuth = FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
        ButterKnife.bind(getActivity());
        //   ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
    }

    private User currentUser;
    private HashTagHelper mTextHashTagHelper;
    SharedPreferences sharedpreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_post, container, false);
        unbinder = ButterKnife.bind(this, v);
        post = (ImageView) v.findViewById(R.id.post);
        userpic = (CircleImageView) v.findViewById(R.id.userpic);
        like = (CircleButton) v.findViewById(R.id.like);
        useTemplate = (CircleButton) v.findViewById(R.id.useTemplate);
        comment = (CircleButton) v.findViewById(R.id.comment);
        description = (TextView) v.findViewById(R.id.description);
        username = (TextView) v.findViewById(R.id.username);
        date = (TextView) v.findViewById(R.id.time);
        commentList = new ArrayList<Comment>();
        commentAdapter = new CommentAdapter(getActivity(), this, commentList);
        sharedpreferences = getActivity().getSharedPreferences("selectedHashtag", Context.MODE_PRIVATE);

        mTextHashTagHelper = HashTagHelper.Creator.create(getResources().getColor(R.color.colorAccent), new HashTagHelper.OnHashTagClickListener() {
            @Override
            public void onHashTagClicked(String hashTag) {
                MainHomeActivity activity = (MainHomeActivity) getActivity();
                activity.homeNavigationBar.changeCurrentItem(1);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("hashtag", hashTag); // Storing hashtag
                editor.commit();
            }
        });
        mTextHashTagHelper.handle(description);
        commentsRecyclerView = (RecyclerView) v.findViewById(R.id.recycler_view_comment);
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        commentsRecyclerView.setNestedScrollingEnabled(false);
        Bundle bundle = this.getArguments();
        String postSelected = bundle.getString("postSelected", "");
        Gson gson = new Gson();
        thePost = gson.fromJson(postSelected, Post.class);
        Picasso.with(getActivity()).load(Constants.HTTP.IMAGE_URL + "templates/" + thePost.getMedia()).into(post);
        if (thePost.getUserImage().contains("https://") || thePost.getUserImage().contains("http://")) {
            Picasso.with(getActivity()).load(thePost.getUserImage()).into(userpic);
        } else {
            Picasso.with(getActivity()).load(Constants.HTTP.IMAGE_URL + "users/" + thePost.getUserImage()).into(userpic);
        }
        description.setText(thePost.getDescription());
        username.setText(thePost.getUsername());
        date.setText(thePost.getCreationDate());
        if (thePost.getIdTemplate() == 0) { // template is private
            useTemplate.setVisibility(View.GONE);
        }
        useTemplate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTemplateAndEntitiesToLocalDB(thePost.getIdTemplate());
            }
        });
        commentsRecyclerView.setAdapter(commentAdapter);
        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendComment();
            }
        });
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickLike();
            }
        });
        loadComments();
        checkLiked();
        return v;
    }

    private void loadComments() {
        CommentsClient commentsClient = ServiceFactory.getApiClient().create(CommentsClient.class);
        JsonObject idPost = new JsonObject();
        idPost.addProperty("idPost", thePost.getId());

        Call<List<Comment>> call = commentsClient.getComments(idPost);
        call.enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                commentList.clear();

                commentList.addAll(response.body());

                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {

            }
        });

    }

    private void checkLiked() {
        like.setClickable(false);

        Postlike postlike = ServiceFactory.getApiClient().create(Postlike.class);
        JsonObject idPost = new JsonObject();
        idPost.addProperty("idPost", thePost.getId());
        idPost.addProperty("idUser", mAuth.getCurrentUser().getUid());
        Call<PostlikeResult> call = postlike.checkLiked(idPost);
        call.enqueue(new Callback<PostlikeResult>() {
            @Override
            public void onResponse(Call<PostlikeResult> call, Response<PostlikeResult> response) {

                Log.e("RESSSSS", response.body().getResult());
                if (response.body().getResult().equals("liked")) {
                    like.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                    v.refreshDrawableState();
                    like.setClickable(true);
                    v.invalidate();
                } else if (response.body().getResult().equals("disliked")) {
                    like.setImageResource(R.drawable.ic_favorite_black_24dp);
                    like.refreshDrawableState();
                    like.setClickable(true);
                    v.refreshDrawableState();
                    v.invalidate();
                }
            }


            @Override
            public void onFailure(Call<PostlikeResult> call, Throwable t) {

            }
        });
    }

    private void onClickLike() {
        like.setClickable(false);

        Postlike postlike = ServiceFactory.getApiClient().create(Postlike.class);
        JsonObject idPost = new JsonObject();
        idPost.addProperty("idPost", thePost.getId());
        idPost.addProperty("idUser", mAuth.getUid());
        Call<PostlikeResult> call = postlike.addRemovePostlike(idPost);
        call.enqueue(new Callback<PostlikeResult>() {
            @Override
            public void onResponse(Call<PostlikeResult> call, Response<PostlikeResult> response) {

                if (response.body().getResult().equals("liked")) {
                    like.setImageResource(R.drawable.ic_favorite_black_24dp);
//                    dislike.setVisibility(View.INVISIBLE);
//                    like.setVisibility(View.VISIBLE);
                    like.setClickable(true);

                    v.refreshDrawableState();

                    v.invalidate();
                } else if (response.body().getResult().equals("disliked")) {
                    like.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                    //like.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_favorite_border_black_24dp));
                    like.refreshDrawableState();
                    like.setClickable(true);

//                    dislike.setVisibility(View.VISIBLE);
//                    like.setVisibility(View.INVISIBLE);
                    v.refreshDrawableState();
                    v.invalidate();
                }
            }


            @Override
            public void onFailure(Call<PostlikeResult> call, Throwable t) {

            }
        });
    }

    private MaterialDialog commentDialog;
    private EditText commentEditText;
    private Button ok;
    private Button cancel;

    private void sendComment() {


        commentDialog =
                new MaterialDialog.Builder(getActivity())
                        .title("Comment this post")
                        .customView(R.layout.layout_send_comment, true)
                        .build();
        commentEditText = commentDialog.getCustomView().findViewById(R.id.comment);
        ok = commentDialog.getCustomView().findViewById(R.id.ok);
        cancel = commentDialog.getCustomView().findViewById(R.id.cancel);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String m_Text = "";
                m_Text = commentEditText.getText().toString();
                CommentsClient commentsClient = ServiceFactory.getApiClient().create(CommentsClient.class);
                JsonObject idPost = new JsonObject();
                idPost.addProperty("idPost", thePost.getId());
                idPost.addProperty("idUser", mAuth.getUid());
                idPost.addProperty("text", m_Text);
                Call<Result> call = commentsClient.addComment(idPost);
                call.enqueue(new Callback<Result>() {
                    @Override
                    public void onResponse(Call<Result> call, Response<Result> response) {

                        loadComments();

                    }

                    @Override
                    public void onFailure(Call<Result> call, Throwable t) {
                        loadComments();
                    }
                });
                commentDialog.dismiss();

            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commentDialog.hide();

            }
        });

        commentDialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    FirebaseAuth mAuth;

    private void addTemplateAndEntitiesToLocalDB(int idTemplate) {


        PostsClient client = ServiceFactory.getApiClient().create(PostsClient.class);
        JsonObject parameters = new JsonObject();
        parameters.addProperty("idTemplate", idTemplate);
        Call<List<LayerDB>> call = client.getLayers(parameters);
        Toast.makeText(getActivity(), "Getting Template...", Toast.LENGTH_SHORT).show();
        call.enqueue(new Callback<List<LayerDB>>() {
            @Override
            public void onResponse(Call<List<LayerDB>> call, Response<List<LayerDB>> response) {

                CacheStore cacheStore = CacheStore.getInstance();
                Template template = new Template();
                template.setBackgroundColor(thePost.getBackgroundColor());
                template.setAspectRatio(thePost.getAspectRatio());
                template.setIdUser(mAuth.getUid());
                templateToWorkOn = new Template();
                templateToWorkOn = template;
                Long lastid = template.insert();

//                for (int i = 0; i < response.body().size(); i++) {
//                    response.body().get(i).setIdTemplate(lastid.intValue());
//                    response.body().get(i).insert();
//                    if (response.body().get(i).getType().equals("image")) {
//
//                        ImageLoader imageLoader = ImageLoader.getInstance();
//                        int finalI = i;
//                        imageLoader.loadImage(Constants.HTTP.IMAGE_URL + "templates/" + response.body().get(i).getImageLink(), new SimpleImageLoadingListener() {
//                            @Override
//                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                                cacheStore.saveCacheFile(lastid.intValue() + "-layer-" + finalI, loadedImage, Constants.IMAGE.HIGH_QUALITY);
//
//                            }
//                        });
//
//
//                    }
//
//
                //   }
                for (int i = 0; i < response.body().size(); i++) {

                    if (response.body().get(i).getType().equals("image")) {
                        howManyImages++;
                        Log.d("howManyImages", "->" + howManyImages);
                    }
                }
                for (int i = 0; i < response.body().size(); i++) {
                    response.body().get(i).setIdTemplate(lastid.intValue());
                    LayerDB ldb = new LayerDB();
                    ldb= response.body().get(i);
                    ldb.setId(0);
                    ldb.insert();
                    int k = 0;
                    SaveImageToCacheTask saveImageToCacheTask = new SaveImageToCacheTask();
                    if (response.body().get(i).getType().equals("image")) {

                        SaveImageParams params = new SaveImageParams(response.body().get(i), lastid, i);

                        saveImageToCacheTask.execute(params);
                        if (howManyImages == 0) {
                            Intent intent = new Intent(getActivity(), UserCreavasActivity.class);
                            startActivity(intent);


                        }

                    }


                }
                cacheStore.saveCacheFile(lastid + "", ((BitmapDrawable) post.getDrawable()).getBitmap(), Constants.IMAGE.HIGH_QUALITY);


            }

            @Override
            public void onFailure(Call<List<LayerDB>> call, Throwable t) {

            }
        });


    }

    private int howManyImages = 0;
    private Template templateToWorkOn;


    private static class SaveImageParams {
        LayerDB layer;
        long lastTemplateId;
        int layerOrder;

        SaveImageParams(LayerDB layer, long lastTemplateId, int layerOrder) {
            this.layer = layer;
            this.lastTemplateId = lastTemplateId;
            this.layerOrder = layerOrder;
        }
    }

    private class SaveImageToCacheTask extends AsyncTask<SaveImageParams, Integer, Integer> {


        @Override
        protected Integer doInBackground(SaveImageParams... arg0) {
            CacheStore cacheStore = CacheStore.getInstance();
            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.loadImage(Constants.HTTP.IMAGE_URL + "templates/" + arg0[0].layer.getImageLink(), new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    cacheStore.saveCacheFile(arg0[0].lastTemplateId + "-layer-" + arg0[0].layerOrder, loadedImage, Constants.IMAGE.HIGH_QUALITY);
                }
            });
            Log.d("doInBackground", "->" + howManyImages);

            return howManyImages--;
        }

        @Override
        protected void onPostExecute(Integer aVoid) {
            super.onPostExecute(aVoid);
            Log.d("onPostExecute", "->" + aVoid);

            if (aVoid == 1) {
                Intent intent = new Intent(getActivity(), UserCreavasActivity.class);
                startActivity(intent);


            }
        }
    }


    @Override
    public void onCommentDeleted(Comment comment) {
        commentList.remove(commentList.indexOf(comment));
        commentAdapter.notifyDataSetChanged();
        CommentsClient client = ServiceFactory.getApiClient().create(CommentsClient.class);
        JsonObject parameters = new JsonObject();
        Call<Result> call = client.deleteComment(comment);
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, retrofit2.Response<Result> response) {
                if (response.isSuccessful()) {
                    if (response.body().getResult().equals("success")) {


                    } else
                        Toast.makeText(getActivity(), "FAIL / SUCCESS", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(getActivity(), "FAIL KHLAS", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {

            }
        });

    }

}
