package tn.meteor.creavas.models;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.io.Serializable;

import tn.meteor.creavas.utils.dbflowTables.
        TemplateDatabase;

/**
 * Created by lilk on 02/12/2017.
 */

@Table(database = TemplateDatabase.class)
@SuppressWarnings("serial")
public class Template extends BaseModel implements Serializable {
    @Column
    @PrimaryKey(autoincrement = true)
    private int id;
    @Column
    private String idUser;
    @Column
    private int backgroundColor;
    @Column
    private String aspectRatio;


    public Template(String idUser, int backgroundColor, String aspectRatio, int aspectRationV) {
        this.idUser = idUser;
        this.aspectRatio = aspectRatio;
        this.backgroundColor = backgroundColor;


    }

    public Template(int id, String idUser, int backgroundColor) {
        this.id = id;
        this.idUser = idUser;
        this.backgroundColor = backgroundColor;
    }

    public Template() {
    }


    public String getAspectRatio() {
        return aspectRatio;
    }

    public void setAspectRatio(String aspectRatio) {
        this.aspectRatio = aspectRatio;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }


    @Override
    public String toString() {
        return "Template{" +
                "id=" + id +
                ", idUser='" + idUser + '\'' +
                ", backgroundColor=" + backgroundColor +
                ", aspectRatio='" + aspectRatio + '\'' +
                '}';
    }
}
