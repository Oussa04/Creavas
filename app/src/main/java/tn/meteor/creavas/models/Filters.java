package tn.meteor.creavas.models;

import android.graphics.Bitmap;

import net.alhazmy13.imagefilter.ImageFilter;

/**
 * Created by Oussaa on 11/01/2018.
 */

public class Filters {

   private String name;
    private  Bitmap preview;
    private ImageFilter.Filter type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Bitmap getPreview() {
        return preview;
    }

    public void setPreview(Bitmap preview) {
        this.preview = preview;
    }

    public ImageFilter.Filter getType() {
        return type;
    }

    public void setType(ImageFilter.Filter type) {
        this.type = type;
    }

    public Filters(String name, Bitmap preview, ImageFilter.Filter type) {
        this.name = name;
        this.preview = preview;
        this.type = type;
    }

    public Filters() {
    }
}
