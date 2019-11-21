package tn.meteor.creavas.models;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import tn.meteor.creavas.utils.dbflowTables.LayerDatabase;

/**
 * Created by lilk on 02/12/2017.
 */
@Table(database = LayerDatabase.class)
public class LayerDB extends BaseModel{

    @Column
    @PrimaryKey(autoincrement = true)
    private int id;
    @Column
    private int idTemplate;
    @Column
    private int layerOrder;
    @Column
    private String type;
    @Column
    private float rotationDegree;
    @Column
    private float scale;
    @Column
    private float posX;
    @Column
    private float posY;
    @Column
    private Boolean isFlipped;
    @Column
    private String imageLink;
    @Column
    private int  fontColor;
    @Column
    private float fontSize;
    @Column
    private float textHeight;
    @Column
    private float textWidth;
    @Column
    private String fontTypeFace;
    @Column
    private String text;




    public LayerDB(int idTemplate, int layerOrder, String type, float rotationDegree, float scale, float posX, float posY, Boolean isFlipped, String imageLink) {
        this.idTemplate = idTemplate;
        this.layerOrder = layerOrder;
        this.type = type;
        this.rotationDegree = rotationDegree;
        this.scale = scale;
        this.posX = posX;
        this.posY = posY;
        this.isFlipped = isFlipped;
        this.imageLink = imageLink;
    }

    public LayerDB(int id, int idTemplate, int layerOrder, String type, float rotationDegree, float scale, float posX, float posY, Boolean isFlipped, int fontColor, float fontSize, String fontTypeFace, String text, float textHeight, float textWidth) {
        this.id = id;
        this.idTemplate = idTemplate;
        this.layerOrder = layerOrder;
        this.type = type;
        this.rotationDegree = rotationDegree;
        this.scale = scale;
        this.posX = posX;
        this.posY = posY;
        this.isFlipped = isFlipped;
        this.fontColor = fontColor;
        this.fontSize = fontSize;
        this.fontTypeFace = fontTypeFace;
        this.text = text;
        this.textHeight=textHeight;
        this.textWidth=textWidth;
    }

    public LayerDB() {
    }


    public float getTextHeight() {
        return textHeight;
    }

    public void setTextHeight(float textHeight) {
        this.textHeight = textHeight;
    }

    public float getTextWidth() {
        return textWidth;
    }

    public void setTextWidth(float textWidth) {
        this.textWidth = textWidth;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdTemplate() {
        return idTemplate;
    }

    public void setIdTemplate(int idTemplate) {
        this.idTemplate = idTemplate;
    }

    public int getLayerOrder() {
        return layerOrder;
    }

    public void setLayerOrder(int layerOrder) {
        this.layerOrder = layerOrder;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public float getRotationDegree() {
        return rotationDegree;
    }

    public void setRotationDegree(float rotationDegree) {
        this.rotationDegree = rotationDegree;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public float getPosX() {
        return posX;
    }

    public void setPosX(float posX) {
        this.posX = posX;
    }

    public float getPosY() {
        return posY;
    }

    public void setPosY(float posY) {
        this.posY = posY;
    }

    public Boolean isFlipped() {
        return isFlipped;
    }

    public void setFlipped(Boolean flipped) {
        isFlipped = flipped;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public int getFontColor() {
        return fontColor;
    }

    public void setFontColor(int fontColor) {
        this.fontColor = fontColor;
    }

    public float getFontSize() {
        return fontSize;
    }

    public void setFontSize(float fontSize) {
        this.fontSize = fontSize;
    }

    public String getFontTypeFace() {
        return fontTypeFace;
    }

    public void setFontTypeFace(String fontTypeFace) {
        this.fontTypeFace = fontTypeFace;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }


    @Override
    public String toString() {
        return "LayerDB{" +
                "id=" + id +
                ", idTemplate=" + idTemplate +
                ", layerOrder=" + layerOrder +
                ", type='" + type + '\'' +
                ", rotationDegree=" + rotationDegree +
                ", scale=" + scale +
                ", posX=" + posX +
                ", posY=" + posY +
                ", isFlipped=" + isFlipped +
                ", imageLink='" + imageLink + '\'' +
                ", fontColor=" + fontColor +
                ", fontSize=" + fontSize +
                ", textHeight=" + textHeight +
                ", textWidth=" + textWidth +
                ", fontTypeFace='" + fontTypeFace + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
