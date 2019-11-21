package tn.meteor.creavas.models;

/**
 * Created by Oussaa on 08/12/2017.
 */

public class Post {


    private int id;
    private String idUser;
    private String description;
    private String type;
    private int idTemplate;
    private String theme;
    private String media;
    private String creationDate;

    private String username;
    private String name;
    private int likes;
    private int comments;
    private String userImage;
    private String aspectRatio;
    private int backgroundColor;

    public String getAspectRatio() {
        return aspectRatio;
    }

    public void setAspectRatio(String aspectRatio) {
        this.aspectRatio = aspectRatio;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public Post() {
    }

    public Post(String idUser, String description, String type, int idTemplate, String theme, String media, String creationDate, String username, String name, int likes, int comments, String userImage, String aspectRatio, int backgroundColor) {
        this.idUser = idUser;
        this.description = description;
        this.type = type;
        this.idTemplate = idTemplate;
        this.theme = theme;
        this.media = media;
        this.creationDate = creationDate;
        this.username = username;
        this.name = name;
        this.likes = likes;
        this.comments = comments;
        this.userImage = userImage;
        this.aspectRatio = aspectRatio;
        this.backgroundColor = backgroundColor;
    }

    public Post(int id, String idUser, String description, String type, int idTemplate, String theme, String media, String creationDate) {
        this.id = id;
        this.idUser = idUser;
        this.description = description;
        this.type = type;
        this.idTemplate = idTemplate;
        this.theme = theme;
        this.media = media;
        this.creationDate = creationDate;
    }

    public Post(String idUser, String description, String type, int idTemplate, String theme, String media, String creationDate, String username, String name, int likes, int comments, String userImage) {
        this.idUser = idUser;
        this.description = description;
        this.type = type;
        this.idTemplate = idTemplate;
        this.theme = theme;
        this.media = media;
        this.creationDate = creationDate;
        this.username = username;
        this.name = name;
        this.likes = likes;
        this.comments = comments;
        this.userImage = userImage;
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", idUser='" + idUser + '\'' +
                ", description='" + description + '\'' +
                ", type='" + type + '\'' +
                ", idTemplate=" + idTemplate +
                ", theme='" + theme + '\'' +
                ", media='" + media + '\'' +
                ", creationDate='" + creationDate + '\'' +
                ", username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", likes=" + likes +
                ", comments=" + comments +
                ", userImage='" + userImage + '\'' +
                ", aspectRatio='" + aspectRatio + '\'' +
                ", backgroundColor='" + backgroundColor + '\'' +
                '}';
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getIdTemplate() {
        return idTemplate;
    }

    public void setIdTemplate(int idTemplate) {
        this.idTemplate = idTemplate;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getMedia() {
        return media;
    }

    public void setMedia(String media) {
        this.media = media;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }
}
