package tn.meteor.creavas.kitchen.ui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.ColorInt;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.color.ColorChooserDialog;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.firebase.auth.FirebaseAuth;
import com.iceteck.silicompressorr.SiliCompressor;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.BottomBarTab;
import com.roughike.bottombar.TabSelectionInterceptor;
import com.simplify.ink.InkView;
import com.takwolf.android.aspectratio.AspectRatioLayout;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;
import tn.meteor.creavas.R;
import tn.meteor.creavas.activities.FIltersActivity;
import tn.meteor.creavas.kitchen.ui.adapter.FontsAdapter;
import tn.meteor.creavas.kitchen.utils.FontProvider;
import tn.meteor.creavas.kitchen.viewmodel.Font;
import tn.meteor.creavas.kitchen.viewmodel.Layer;
import tn.meteor.creavas.kitchen.viewmodel.TextLayer;
import tn.meteor.creavas.kitchen.widget.MotionView;
import tn.meteor.creavas.kitchen.widget.entity.ImageEntity;
import tn.meteor.creavas.kitchen.widget.entity.MotionEntity;
import tn.meteor.creavas.kitchen.widget.entity.TextEntity;
import tn.meteor.creavas.models.LayerDB;
import tn.meteor.creavas.models.LayerDB_Table;
import tn.meteor.creavas.models.Template;
import tn.meteor.creavas.utils.AnimatedGIFWriter;
import tn.meteor.creavas.utils.CacheStore;
import tn.meteor.creavas.utils.Constants;
import tn.meteor.creavas.utils.UniversalImageLoader;


public class CreavasActivity extends AppCompatActivity implements TextEditorDialogFragment.OnTextLayerCallback, ColorChooserDialog.ColorCallback,View.OnClickListener {
    private ShowcaseView showcaseView;
    public static final int SELECT_STICKER_REQUEST_CODE = 123;
    public static float minstroke = 2f;
    private int PICK_IMAGE_REQUEST = 3662;
    protected MotionView motionView;
    protected MotionView motionViewGif;

    protected View textEntityEditPanel;
    private BottomBar bottomBar;
    private BottomBar imageBar;
    private BottomBar textBar;
    private FontProvider fontProvider;
    private BottomBar drawbar;
    private InkView drawView;
    private String actualAspectRatio = "1:1";
    private MaterialDialog insertTextDialog;
    private EditText textinput;
    private Button go;
    private Button cancel;
    private Toast toast;
    private FirebaseAuth mAuth;
    private BottomBarTab dummy;
    int textColor = Color.BLACK;
    int bgColor = Color.WHITE;
    int drawingColor = Color.BLACK;
boolean isEditing;
    private final MotionView.MotionViewCallback motionViewCallback = new MotionView.MotionViewCallback() {
        @Override
        public void onEntitySelected(@Nullable MotionEntity entity) {
            if (entity instanceof TextEntity) {
                imageBar.setVisibility(View.INVISIBLE);
                drawbar.setVisibility(View.INVISIBLE);
                textBar.setVisibility(View.VISIBLE);

                BottomBarTab dummy = textBar.getTabWithId(R.id.dummy);
                dummy.setVisibility(View.GONE);
                textBar.selectTabWithId(R.id.dummy);

            } else if (entity instanceof ImageEntity) {
                textBar.setVisibility(View.INVISIBLE);
                drawbar.setVisibility(View.INVISIBLE);
                imageBar.setVisibility(View.VISIBLE);

                BottomBarTab dummy = imageBar.getTabWithId(R.id.dummy);
                dummy.setVisibility(View.GONE);
                imageBar.selectTabWithId(R.id.dummy);
            } else {
                imageBar.setVisibility(View.INVISIBLE);
                textBar.setVisibility(View.INVISIBLE);
                drawbar.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void onEntityDoubleTap(@NonNull MotionEntity entity) {
            startTextEntityEditing();
        }
    };
    private ProgressDialog progressDialog;
    Template template;
    int counter=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creavator);
        this.fontProvider = new FontProvider(getResources());
     //   getSupportActionBar().hide();
        //views
        motionView = (MotionView) findViewById(R.id.main_motion_view);
        motionView.setMotionViewCallback(motionViewCallback);
        motionView.setBackgroundColor(bgColor);
        drawView = (InkView) findViewById(R.id.draw);
        drawView.setMinStrokeWidth(minstroke);
        drawView.setVisibility(View.GONE);
        //views
        //bars
        imageBar = (BottomBar) findViewById(R.id.bottomBar_delete);
        textBar = (BottomBar) findViewById(R.id.bottomBar_textbar);
        bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        //bars
        setAspectRatio("1", "1");

        initBottomBar();
        initImageBar();
        initTextBar();
        initDrawBar();
        initImageLoader();
        isEditing=false;

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            template = (Template) intent.getSerializableExtra("template");
            if (template != null) {
                isEditing=true;
                progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("Loading...");
                progressDialog.show();
                bgColor = template.getBackgroundColor();
                motionView.setBackgroundColor(bgColor);
                setAspectRatio(template.getAspectRatio().substring(0, template.getAspectRatio().indexOf(":")), template.getAspectRatio().substring(template.getAspectRatio().indexOf(":") + 1, template.getAspectRatio().length()));
                motionView.invalidate();
                actualAspectRatio = template.getAspectRatio();


                Log.e("---->", template.toString());


                List<LayerDB> layerList = SQLite.select().
                        from(LayerDB.class).where(LayerDB_Table.idTemplate.is(template.getId())).queryList();


                for (int i = 0; i < layerList.size(); i++) {

                    layerList.get(i).toString();
                }
                displayLayers(layerList);

            }


//        Button gif = (Button) findViewById(R.id.gif);
//        gif.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                try {
//                    generateGif();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                gifDialog.show();
//            }
//        });
//
//        Button seegif = (Button)findViewById(R.id.seegif);
//        seegif.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                showgifpreview();
//            }
//        });




        }
        Target viewTarget = new ViewTarget(bottomBar.getId(), this);
        showcaseView = new ShowcaseView.Builder(this)
                .setContentTitle("Creavas")
                .setContentText("Here you can create your own Creavas")
                .setTarget(viewTarget)
                .setOnClickListener(this)
                .setStyle(R.style.CustomNext)
                .singleShot(56)
                .build();
                showcaseView.setButtonText("Next");
    }

    @Override
    public void onClick(View v) {

        switch (counter) {
            case 0:
                showcaseView.setShowcase(new ViewTarget(bottomBar.getTabWithId(R.id.aspect_ratio)), true);
                showcaseView.setContentTitle("Aspect Ratio");
                showcaseView.setContentText("You can use this button to choose an aspect ratio for your Creavas");
                break;

            case 1:
                showcaseView.setShowcase(new ViewTarget(bottomBar.getTabWithId(R.id.add_draw)), true);
                showcaseView.setContentTitle("Draw");
                showcaseView.setContentText("You can use this tab draw whatever you want");
                break;

            case 2:
                showcaseView.setShowcase(new ViewTarget(bottomBar.getTabWithId(R.id.add_stickers)), true);
                showcaseView.setContentTitle("Add Stickers");
                showcaseView.setContentText("You can use this tab to choose sticker for the list ");
                break;

            case 3:
                showcaseView.setShowcase(new ViewTarget(bottomBar.getTabWithId(R.id.background_color)), true);
                showcaseView.setContentTitle("Change background");
                showcaseView.setContentText("You can use this tab to change background color ");
                showcaseView.setButtonText("Close");
                break;

            case 4:
                showcaseView.hide();
                break;
        }
        counter++;
    }


    private void initImageLoader() {
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(this);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

    private void initDrawBar() {

        drawbar = (BottomBar) findViewById(R.id.bottomBar_draw);
        drawbar.setVisibility(View.INVISIBLE);
        dummy = drawbar.getTabWithId(R.id.dummy);
        dummy.setVisibility(View.GONE);

        drawbar.setTabSelectionInterceptor(new TabSelectionInterceptor() {
                                               @Override
                                               public boolean shouldInterceptTabSelection(@IdRes int oldTabId, @IdRes int tabId) {
                                                   if (tabId == R.id.done) {
                                                       addDraw(drawView.getBitmap());
                                                       drawView.setVisibility(View.GONE);
                                                       drawbar.setVisibility(View.INVISIBLE);
                                                       drawView.clear();
                                                   } else if (tabId == R.id.stroke_decrease) {
                                                       minstroke = minstroke - 2f;
                                                       drawView.setMinStrokeWidth(minstroke);
                                                   } else if (tabId == R.id.stroke_increase) {
                                                       minstroke = minstroke + 2f;
                                                       drawView.setMinStrokeWidth(minstroke);
                                                   } else if (tabId == R.id.draw_color) {
                                                       showColorChooserPrimary();
                                                   } else if (tabId == R.id.clear) {
                                                       drawView.clear();
                                                   } else if (tabId == R.id.draw_delete) {
                                                       drawView.clear();
                                                       drawbar.setVisibility(View.INVISIBLE);
                                                       drawView.setVisibility(View.GONE);
                                                   }
                                                   return true;
                                               }
                                           }
        );
    }

    private void initTextBar() {

        textBar.setVisibility(View.INVISIBLE);
        textBar.setTabSelectionInterceptor(new TabSelectionInterceptor() {
                                               @Override
                                               public boolean shouldInterceptTabSelection(@IdRes int oldTabId, @IdRes int tabId) {
                                                   if (tabId == R.id.text_input) {
                                                       startTextEntityEditing();
                                                   } else if (tabId == R.id.font_size_decrease) {
                                                       decreaseTextEntitySize();
                                                   } else if (tabId == R.id.font_size_increase) {
                                                       increaseTextEntitySize();
                                                   } else if (tabId == R.id.font_color) {
                                                       showColorChooserPrimary();
                                                   } else if (tabId == R.id.font_choice) {
                                                       changeTextEntityFont();
                                                   } else if (tabId == R.id.text_delete) {
                                                       motionView.deletedSelectedEntity();
                                                       textBar.setVisibility(View.INVISIBLE);
                                                   } else if (tabId == R.id.bring_front) {
                                                       motionView.moveSelectedFront();

                                                   } else if (tabId == R.id.bring_back) {
                                                       motionView.moveSelectedBack();

                                                   } else if (tabId == R.id.flip) {
                                                       motionView.flipSelectedEntity();

                                                   }
                                                   return true;
                                               }
                                           }
        );
    }

    private void initImageBar() {
        imageBar.setVisibility(View.INVISIBLE);
        imageBar.setTabSelectionInterceptor(new TabSelectionInterceptor() {
                                                @Override
                                                public boolean shouldInterceptTabSelection(@IdRes int oldTabId, @IdRes int tabId) {
                                                    if (tabId == R.id.delete_item) {
                                                        motionView.deletedSelectedEntity();
                                                        imageBar.setVisibility(View.INVISIBLE);
                                                    } else if (tabId == R.id.bring_front) {
                                                        motionView.moveSelectedFront();

                                                    } else if (tabId == R.id.bring_back) {
                                                        motionView.moveSelectedBack();

                                                    } else if (tabId == R.id.flip) {
                                                        motionView.flipSelectedEntity();

                                                    }
                                                    return true;
                                                }
                                            }
        );

    }

    private void initBottomBar() {
        dummy = bottomBar.getTabWithId(R.id.dummy);
        dummy.setVisibility(View.GONE);
        bottomBar.selectTabWithId(R.id.dummy);

        bottomBar.setTabSelectionInterceptor(new TabSelectionInterceptor() {
            @Override
            public boolean shouldInterceptTabSelection(@IdRes int oldTabId, @IdRes int tabId) {
                isDrawing();
                if (tabId == R.id.aspect_ratio) {
                    showAspectRatioDialog();
                } else if (tabId == R.id.background_color) {
                    showColorChooserPrimary();
                } else if (tabId == R.id.add_stickers) {
                    Intent intent = new Intent(getApplicationContext(), StickerSelectActivity.class);
                    startActivityForResult(intent, SELECT_STICKER_REQUEST_CODE);
                } else if (tabId == R.id.add_text) {
                    showTextInputDialog("", 1);
                } else if (tabId == R.id.add_image) {
                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .start(CreavasActivity.this);
                } else if (tabId == R.id.add_draw) {
                    drawView.setVisibility(View.VISIBLE);
                    drawbar.setVisibility(View.VISIBLE);
                } else if (tabId == R.id.finish) {

                    addTemplateAndEntitiesToLocalDB();
                    imageBar.setVisibility(View.INVISIBLE);
                    textBar.setVisibility(View.INVISIBLE);
                    drawbar.setVisibility(View.INVISIBLE);
                }
                return true;
            }
        });

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

    private Button gif;
    public LayerDB lay;
    private Bitmap bmp;
    private ArrayList<Bitmap> bitmapArray = new ArrayList<Bitmap>();

    public void displayLayers(List<LayerDB> layers) {
        CacheStore cacheStore = CacheStore.getInstance();
        motionView.setDrawingCacheEnabled(false);

        motionView.post(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < layers.size(); i++) {
                    lay = new LayerDB();
                    lay = (LayerDB) layers.get(i);
                    if (lay.getType().equals("image")) {
                        Layer layer = new Layer();
                        Layer layer1 = new Layer(0.0f, 0.4f, 0.0f, 0.0f, false);
                        ImageEntity entity = new ImageEntity(layer1, cacheStore.getCacheFile(lay.getIdTemplate() + "-layer-" + i), motionView.getWidth(), motionView.getHeight());
                        motionView.addEntityAndPosition(entity);
                        motionView.getSelectedEntity().getLayer().setY(lay.getPosY());
                        motionView.getSelectedEntity().getLayer().setX(lay.getPosX());
                        motionView.getSelectedEntity().getLayer().setScale(lay.getScale());
                        motionView.getSelectedEntity().getLayer().setRotationInDegrees(lay.getRotationDegree());
                        if (lay.isFlipped()) {
                            motionView.flipSelectedEntity();
                        }

                    } else if (lay.getType().equals("text")) {
                        TextLayer textLayer = new TextLayer();
                        Font font = new Font();
                        font.setColor(lay.getFontColor());
                        font.setSize(lay.getFontSize());
                        font.setTypeface(lay.getFontTypeFace());
                        textLayer.setFont(font);
                        textLayer.setText(lay.getText());
                        final List<String> fonts = fontProvider.getFontNames();
                        TextEntity textEntity = new TextEntity(textLayer, motionView.getWidth(), motionView.getHeight(), fontProvider);
                        motionView.addEntityAndPosition(textEntity);
                        motionView.getSelectedEntity().getLayer().setY(lay.getPosY());
                        motionView.getSelectedEntity().getLayer().setX(lay.getPosX());
                        motionView.getSelectedEntity().getLayer().setRotationInDegrees(lay.getRotationDegree());
                        motionView.getSelectedEntity().getLayer().setScale(lay.getScale());
                        if (lay.isFlipped()) {
                            motionView.flipSelectedEntity();
                        }
                    }
                    if (i == layers.size() - 1 || layers.size() == 0) {
                        progressDialog.dismiss();
                        motionView.invalidate();
                    }
                }
            }
        });

    }

    public void isDrawing() {
        if (drawView.getVisibility() == View.VISIBLE || drawbar.getVisibility() == View.VISIBLE) {
            addDraw(drawView.getBitmap());
            drawView.setVisibility(View.GONE);
            drawbar.setVisibility(View.INVISIBLE);
            textBar.setVisibility(View.INVISIBLE);
            imageBar.setVisibility(View.INVISIBLE);
            drawView.clear();
        }
    }

    private void setAspectRatio(String width, String height) {
        AspectRatioLayout aspectRatioLayout = (AspectRatioLayout) findViewById(R.id.ratioLayout);
        aspectRatioLayout.setAspectRatio(Integer.parseInt(width), Integer.parseInt(height));
        aspectRatioLayout.invalidate();
    }

    public void showTextInputDialog(String initial, int type) {
        //type 1 = initial input (el marra loula)
        //type 2 = edit text
        insertTextDialog = new MaterialDialog.Builder(this)
                .title("Insert drawView text")
                .customView(R.layout.layout_text_input, true)
                .build();

        textinput = insertTextDialog.getCustomView().findViewById(R.id.comment);
        textinput.setText(initial);
        go = insertTextDialog.getCustomView().findViewById(R.id.insert);
        cancel = insertTextDialog.getCustomView().findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertTextDialog.dismiss();
            }
        });
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertTextDialog.dismiss();
                if (type == 1) {
                    addTextSticker(textinput.getText().toString());
                } else {
                    if (textinput.getText().toString().isEmpty()) {
                        motionView.deletedSelectedEntity();
                        textBar.setVisibility(View.INVISIBLE);
                    } else {
                        textChanged(textinput.getText().toString());
                    }
                }


            }
        });
        insertTextDialog.show();


    }

    public void showColorChooserPrimary() {
        if (textBar.getVisibility() == View.VISIBLE) {
            new ColorChooserDialog.Builder(this, R.string.select_color)
                    .titleSub(R.string.select_color)
                    .preselect(textColor)
                    .show(this);
        } else if (drawbar.getVisibility() == View.VISIBLE) {
            new ColorChooserDialog.Builder(this, R.string.select_color)
                    .titleSub(R.string.select_color)
                    .preselect(drawingColor)
                    .show(this);
        } else {
            new ColorChooserDialog.Builder(this, R.string.select_color)
                    .titleSub(R.string.select_color)
                    .preselect(bgColor)
                    .show(this);

        }


    }

    @Override
    public void onColorSelection(@NonNull ColorChooserDialog dialog, @ColorInt int color) {
        if (drawbar.getVisibility() == View.VISIBLE)//drawing
        {
            drawView.setColor(color);
            drawingColor = color;
        } else if (textBar.getVisibility() == View.VISIBLE) {
            TextEntity textEntity = currentTextEntity();
            if (textEntity != null) {
                textEntity.getLayer().getFont().setColor(color);
                textEntity.updateEntity();
                motionView.invalidate();
                textColor = color;
            }

        } else {
            motionView.setBackgroundColor(color);
            bgColor = color;

        }
    }

    @Override
    public void onColorChooserDismissed(@NonNull ColorChooserDialog dialog) {
    }

    private void addSticker(final int stickerResId) {
        motionView.post(new Runnable() {
            @Override
            public void run() {
                Layer layer = new Layer();
                Bitmap pica = BitmapFactory.decodeResource(getResources(), stickerResId);
                ImageEntity entity = new ImageEntity(layer, pica, motionView.getWidth(), motionView.getHeight());
                motionView.addEntityAndPosition(entity);

            }
        });
    }

    private void addImage(Bitmap image) {
        motionView.post(new Runnable() {
            @Override
            public void run() {
                Layer layer = new Layer();
                ImageEntity entity = new ImageEntity(layer, image, motionView.getWidth(), motionView.getHeight());
                motionView.addEntityAndPosition(entity);
            }
        });
    }

    private void addDraw(Bitmap draw) {
        motionView.post(new Runnable() {
            @Override
            public void run() {
                Layer layer = new Layer();

                //ImageEntity entity = new ImageEntity(layer, ImageUtils.TrimDraw(draw), motionView.getWidth(), motionView.getHeight());
                //ImageEntity entity = new ImageEntity(layer, ImageUtils.CropBitmapTransparency(draw), motionView.getWidth(), motionView.getHeight());
                ImageEntity entity = new ImageEntity(layer, draw, motionView.getWidth(), motionView.getHeight());

                motionView.addEntity(entity);

                motionView.moveSelectedBack();
            }
        });
    }


    private void increaseTextEntitySize() {
        TextEntity textEntity = currentTextEntity();
        if (textEntity != null) {
            textEntity.getLayer().getFont().increaseSize(TextLayer.Limits.FONT_SIZE_STEP);
            textEntity.updateEntity();
            motionView.invalidate();
        }
    }

    private void decreaseTextEntitySize() {
        TextEntity textEntity = currentTextEntity();
        if (textEntity != null) {
            textEntity.getLayer().getFont().decreaseSize(TextLayer.Limits.FONT_SIZE_STEP);
            textEntity.updateEntity();
            motionView.invalidate();
        }
    }

    private void changeTextEntityFont() {
        final List<String> fonts = fontProvider.getFontNames();
        FontsAdapter fontsAdapter = new FontsAdapter(this, fonts, fontProvider);
        new AlertDialog.Builder(this)
                .setTitle(R.string.select_font)
                .setAdapter(fontsAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        TextEntity textEntity = currentTextEntity();
                        if (textEntity != null) {
                            textEntity.getLayer().getFont().setTypeface(fonts.get(which));
                            textEntity.updateEntity();
                            motionView.invalidate();
                        }
                    }
                })
                .show();
    }


    private void startTextEntityEditing() {
        TextEntity textEntity = currentTextEntity();
        if (textEntity != null) {
            showTextInputDialog(textEntity.getLayer().getText(), 2);
        }


    }

    @Nullable
    private TextEntity currentTextEntity() {
        if (motionView != null && motionView.getSelectedEntity() instanceof TextEntity) {
            return ((TextEntity) motionView.getSelectedEntity());
        } else {
            return null;
        }
    }


    protected void addTextSticker(String inputText) {
        TextLayer textLayer = createTextLayer(inputText);
        TextEntity textEntity = new TextEntity(textLayer, motionView.getWidth(),
                motionView.getHeight(), fontProvider);
        motionView.addEntityAndPosition(textEntity);
        motionView.invalidate();


    }

    private TextLayer createTextLayer(String inputText) {
        TextLayer textLayer = new TextLayer();
        Font font = new Font();

        font.setColor(TextLayer.Limits.INITIAL_FONT_COLOR);
        font.setSize(TextLayer.Limits.INITIAL_FONT_SIZE);
        font.setTypeface(fontProvider.getDefaultFontName());

        textLayer.setFont(font);

        textLayer.setText(inputText);


        return textLayer;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_STICKER_REQUEST_CODE) {
                if (data != null) {
                    int stickerId = data.getIntExtra(StickerSelectActivity.EXTRA_STICKER_ID, 0);
                    if (stickerId != 0) {
                        addSticker(stickerId);
                    }
                }
            }
        }


        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                try {
                    addImage(SiliCompressor.with(this).getCompressBitmap(resultUri.toString()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }


    private void showToast(String message) {
        if (toast != null) {
            toast.cancel();
            toast = null;
        }
        toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();

    }

    private int selectedIndex = 4;

    private void showAspectRatioDialog() {
        new MaterialDialog.Builder(this)
                .title("Select the Creava size")
                .items(R.array.aspectRation)
                .itemsCallbackSingleChoice(
                        selectedIndex,
                        (dialog, view, which, text) -> {
                            showToast(which + ": " + text);
                            selectedIndex = which;
                            actualAspectRatio = text.toString();
                            setAspectRatio(actualAspectRatio.substring(0, actualAspectRatio.indexOf(":")), actualAspectRatio.substring(actualAspectRatio.indexOf(":") + 1, actualAspectRatio.length()));
                            return true;
                        })
                .positiveText("Set")
                .show();

    }

    @Override
    public void textChanged(@NonNull String text) {
        TextEntity textEntity = currentTextEntity();
        if (textEntity != null) {
            TextLayer textLayer = textEntity.getLayer();
            if (!text.equals(textLayer.getText())) {
                textLayer.setText(text);
                textEntity.updateEntity();
                motionView.invalidate();
            }
        }
    }


    private void addTemplateAndEntitiesToLocalDB() {

        mAuth = FirebaseAuth.getInstance();
        AspectRatioLayout aspectRatioLayout = (AspectRatioLayout) findViewById(R.id.ratioLayout);
        Template template = new Template();
        template.setBackgroundColor(bgColor);
        template.setIdUser(mAuth.getUid());
        template.setAspectRatio(actualAspectRatio);
        CacheStore cacheStore = CacheStore.getInstance();
        Toast.makeText(this, template.getAspectRatio(), Toast.LENGTH_SHORT).show();
        Long lastid = template.insert();

        cacheStore.saveCacheFile(lastid + "", motionView.getThumbnailImage(bgColor), Constants.IMAGE.HIGH_QUALITY);

        List<MotionEntity> entities = motionView.getEntities();
        for (int i = 0; i < entities.size(); i++) {
            LayerDB layerDB = new LayerDB();
            layerDB.setIdTemplate(lastid.intValue());
            layerDB.setRotationDegree(entities.get(i).getLayer().getRotationInDegrees());
            layerDB.setPosX(entities.get(i).getLayer().getX());
            layerDB.setPosY(entities.get(i).getLayer().getY());
            layerDB.setFlipped(entities.get(i).getLayer().isFlipped());
            layerDB.setScale(entities.get(i).getLayer().getScale());
            layerDB.setLayerOrder(i);
            if (entities.get(i) instanceof ImageEntity) {
                layerDB.setFontColor(0);
                layerDB.setText("");
                layerDB.setFontTypeFace("");
                layerDB.setFontSize(0);
                layerDB.setImageLink("");
                CacheStore cacheStore1 = CacheStore.getInstance();

                cacheStore1.saveCacheFile(lastid.intValue() + "-layer-" + i, ((ImageEntity) entities.get(i)).getBitmap(), Constants.IMAGE.HIGH_QUALITY);
                layerDB.setType("image");
            } else if (entities.get(i) instanceof TextEntity) {
                TextEntity a = (TextEntity) entities.get(i);
                layerDB.setFontColor(a.getLayer().getFont().getColor());
                layerDB.setText(a.getLayer().getText());
                layerDB.setFontTypeFace(a.getLayer().getFont().getTypeface());
                layerDB.setFontSize(a.getLayer().getFont().getSize());
                layerDB.setTextHeight(a.getHeight());
                layerDB.setTextWidth(a.getWidth());
                layerDB.setImageLink("");
                layerDB.setType("text");
            }
            Log.e("----------------->", "" + layerDB.insert());
        }

        Intent intent = new Intent(CreavasActivity.this, FIltersActivity.class);
        intent.putExtra("imageuri", lastid + "");


         startActivity(intent);
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

        Bitmap bitmap = motionView.getThumbnailImage(Color.MAGENTA);
        Bitmap bitmap2 = motionView.getThumbnailImage(Color.CYAN);
        Bitmap bitmap3 = motionView.getThumbnailImage(Color.RED);
        Bitmap bitmap4 = motionView.getThumbnailImage(bgColor);

        writer.prepareForWrite(os, -1, -1);
        writer.writeFrame(os, bitmap);
        writer.writeFrame(os, bitmap2);
        writer.writeFrame(os, bitmap3);
        writer.writeFrame(os, bitmap4);
        writer.finishWrite(os);

        progressDialog.dismiss();
        showgifpreview();


    }
}
