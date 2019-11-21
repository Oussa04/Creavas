package tn.meteor.creavas.models;

/**
 * Created by Oussaa on 12/12/2017.
 */

public class Comment {

    private int id;

    private String idUser;

    private String text;

    private int idPost;

    private String username;

    private String userImage;

    public Comment(String idUser, String text, int idPost, String username, String userImage) {
        this.idUser = idUser;
        this.text = text;
        this.idPost = idPost;
        this.username = username;
        this.userImage = userImage;
    }

    public Comment(String idUser, String text, String username, String userImage) {
        this.idUser = idUser;
        this.text = text;
        this.username = username;
        this.userImage = userImage;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getIdPost() {
        return idPost;
    }

    public void setIdPost(int idPost) {
        this.idPost = idPost;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", idUser='" + idUser + '\'' +
                ", text='" + text + '\'' +
                ", idPost=" + idPost +
                ", username='" + username + '\'' +
                '}';
    }
}
