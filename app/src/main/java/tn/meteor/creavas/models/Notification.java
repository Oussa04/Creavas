package tn.meteor.creavas.models;

/**
 * Created by lilk on 09/01/2018.
 */

public class Notification {


    private int id;
    private String idUser;
    private String user_image;
    private String date_notification;
    private int idPost;
    private String post_thumbnail;
    private String notification_type;
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getUser_image() {
        return user_image;
    }

    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }

    public String getDate_notification() {
        return date_notification;
    }

    public void setDate_notification(String date_notification) {
        this.date_notification = date_notification;
    }

    public int getIdPost() {
        return idPost;
    }

    public void setIdPost(int idPost) {
        this.idPost = idPost;
    }

    public String getPost_thumbnail() {
        return post_thumbnail;
    }

    public void setPost_thumbnail(String post_thumbnail) {
        this.post_thumbnail = post_thumbnail;
    }

    public String getNotification_type() {
        return notification_type;
    }

    public void setNotification_type(String notification_type) {
        this.notification_type = notification_type;
    }

    public Notification(int id, String idUser, String user_image, String date, String notification_typen, String username) {
        this.id = id;
        this.idUser = idUser;
        this.user_image = user_image;
        this.date_notification = date;
        this.username=username;
        this.notification_type = notification_type;
    }

    public Notification(int id, String idUser, String user_image, String date, int idPost, String post_thumbnail, String notification_type,String username) {
        this.id = id;
        this.idUser = idUser;
        this.user_image = user_image;
        this.date_notification = date;
        this.idPost = idPost;
        this.username=username;

        this.post_thumbnail = post_thumbnail;
        this.notification_type = notification_type;
    }


}
