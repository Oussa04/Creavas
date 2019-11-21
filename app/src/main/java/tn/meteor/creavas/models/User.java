package tn.meteor.creavas.models;

public class User {

    private String name, username, email, firebaseid, description, profile_pic_name, social_link,isFollowed;
    private int howManyUsersIsHeFollowing,howManyUsersFollowingHim,posts;

    public String getIsFollowed() {
        return isFollowed;
    }

    public void setIsFollowed(String isFollowed) {
        this.isFollowed = isFollowed;
    }

    public User() {
    }

    public User(String name, String username, String email, String firebaseid, String description, String profile_pic_name, String social_link, int howManyUsersIsHeFollowing, int howManyUsersFollowingHim, int posts, String isFollowed) {
        this.name = name;
        this.username = username;
        this.email = email;
        this.firebaseid = firebaseid;
        this.description = description;
        this.profile_pic_name = profile_pic_name;
        this.social_link = social_link;
        this.howManyUsersIsHeFollowing = howManyUsersIsHeFollowing;
        this.howManyUsersFollowingHim = howManyUsersFollowingHim;
        this.posts = posts;
        this.isFollowed = isFollowed;
    }

    public int getHowManyUsersIsHeFollowing() {
        return howManyUsersIsHeFollowing;
    }

    public void setHowManyUsersIsHeFollowing(int howManyUsersIsHeFollowing) {
        this.howManyUsersIsHeFollowing = howManyUsersIsHeFollowing;
    }

    public int getHowManyUsersFollowingHim() {
        return howManyUsersFollowingHim;
    }

    public void setHowManyUsersFollowingHim(int howManyUsersFollowingHim) {
        this.howManyUsersFollowingHim = howManyUsersFollowingHim;
    }

    public int getPosts() {
        return posts;
    }

    public void setPosts(int posts) {
        this.posts = posts;
    }

    public User(String name, String username, String email, String firebaseid, String description, String profile_pic_name, String social_link) {
        this.name = name;
        this.username = username;
        this.email = email;
        this.firebaseid = firebaseid;
        this.description = description;
        this.profile_pic_name = profile_pic_name;
        this.social_link = social_link;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirebaseid() {
        return firebaseid;
    }

    public void setFirebaseid(String firebaseid) {
        this.firebaseid = firebaseid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProfile_pic_name() {
        return profile_pic_name;
    }

    public void setProfile_pic_name(String profile_pic_name) {
        this.profile_pic_name = profile_pic_name;
    }

    public String getSocial_link() {
        return social_link;
    }

    public void setSocial_link(String social_link) {
        this.social_link = social_link;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", firebaseid='" + firebaseid + '\'' +
                ", description='" + description + '\'' +
                ", profile_pic_name='" + profile_pic_name + '\'' +
                ", social_link='" + social_link + '\'' +
                ", howManyUsersIsHeFollowing=" + howManyUsersIsHeFollowing +
                ", howManyUsersFollowingHim=" + howManyUsersFollowingHim +
                ", posts=" + posts +
                ", isFollowed=" + isFollowed +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;

        User user = (User) o;

        if (getHowManyUsersIsHeFollowing() != user.getHowManyUsersIsHeFollowing()) return false;
        if (getHowManyUsersFollowingHim() != user.getHowManyUsersFollowingHim()) return false;
        if (getPosts() != user.getPosts()) return false;
        if (!getName().equals(user.getName())) return false;
        if (!getUsername().equals(user.getUsername())) return false;
        if (!getEmail().equals(user.getEmail())) return false;
        if (!getFirebaseid().equals(user.getFirebaseid())) return false;
        if (!getDescription().equals(user.getDescription())) return false;
        if (!getProfile_pic_name().equals(user.getProfile_pic_name())) return false;
        return getSocial_link().equals(user.getSocial_link());
    }

    @Override
    public int hashCode() {
        int result = getName().hashCode();
        result = 31 * result + getUsername().hashCode();
        result = 31 * result + getEmail().hashCode();
        result = 31 * result + getFirebaseid().hashCode();
        result = 31 * result + getDescription().hashCode();
        result = 31 * result + getProfile_pic_name().hashCode();
        result = 31 * result + getSocial_link().hashCode();
        result = 31 * result + getHowManyUsersIsHeFollowing();
        result = 31 * result + getHowManyUsersFollowingHim();
        result = 31 * result + getPosts();
        return result;
    }
}
