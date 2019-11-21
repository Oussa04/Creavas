package tn.meteor.creavas.services.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResultPost {

    @SerializedName("idPost")
    @Expose
    private int idPost;

    @SerializedName("idTemplate")
    @Expose
    private int idTemplate;


    public int getIdPost() {
        return idPost;
    }

    public void setIdPost(int idPost) {
        this.idPost = idPost;
    }

    public int getIdTemplate() {
        return idTemplate;
    }

    public void setIdTemplate(int idTemplate) {
        this.idTemplate = idTemplate;
    }


    @Override
    public String toString() {
        return "ResultPost{" +
                "idPost=" + idPost +
                ", idTemplate=" + idTemplate +
                '}';
    }
}