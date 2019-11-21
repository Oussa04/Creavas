package tn.meteor.creavas.utils;

/**
 * Created by lilk on 24/11/2017.
 */

public class Constants {


    public static class HTTP {
        public static final String IP_ADDRESS = "192.168.1.15";
        public static final String IMAGE_URL = "http://" + IP_ADDRESS + "/CreavasWebSide/web/images/";
        public static final String BASE_URL = "http://" + IP_ADDRESS + "/CreavasWebSide/web/app_dev.php/";
        public static final String TEST = BASE_URL + "test";
        public static final String USERADD = BASE_URL + "user/add";
        public static final String USERGET = BASE_URL + "user/get";
        public static final String USERSSEARCH = BASE_URL + "user/search";
        public static final String GETFOLLOWED = BASE_URL + "user/getUsersFollowed";
        public static final String GETFOLLOWERS = BASE_URL + "user/getUsersFollowers";
        public static final String USERTOKENDELETE = BASE_URL + "user/deleteToken";

        public static final String UPLOADIMAGE = BASE_URL + "user/uploadimage";
        public static final String UPDATETOKEN = BASE_URL + "user/updateToken";
        public static final String UPLOADPOSTIMAGE = BASE_URL + "posts/uploadmedia";

        public static final String POSTBYID = BASE_URL + "posts/getPost";
        public static final String HOMEPOSTSGET = BASE_URL + "posts/get";
        public static final String USERPOSTSGET = BASE_URL + "posts/getpostuser";
        public static final String HOTPOSTSGET = BASE_URL + "posts/gethotposts";
        public static final String POSTSWITHHASHTAG = BASE_URL + "posts/getposthashtag";
        public static final String POSTSDELETE = BASE_URL + "posts/delete";


        public static final String COMMENTGET = BASE_URL + "comment/get";
        public static final String COMMENTADD = BASE_URL + "comment/add";
        public static final String COMMENTDELETE = BASE_URL + "comment/delete";


        public static final String POSTLIKE = BASE_URL + "like/add";
        public static final String ISLIKED = BASE_URL + "like/check";


        public static final String ADDPOST = BASE_URL + "posts/add";
        public static final String ADDLAYERS = BASE_URL + "posts/addLayers";
        public static final String GETLAYERS = BASE_URL + "posts/getLayers";


        public static final String FOLLOW = BASE_URL + "follow/add";
        public static final String UNFOLLOW = BASE_URL + "follow/delete";


        public static final String GETNOTIFICATIONS = BASE_URL + "notif/get";
        public static final String DELETENOTIF = BASE_URL + "notif/delete";
        public static final String DELETEALL = BASE_URL + "notif/deleteall";



    }

    public static class SHARED_PREFERENCES_NAME {
        public static final String PREFERENCES_USER = "user";

    }

    public static class IMAGE {

        public static final int HIGH_QUALITY = 100;
        public static final int MEDIUM_QUALITY = 60;
        public static final int LOW_QUALITY = 10;


    }

    public static final String TIMESTAMP_FORMAT = "yyyyMMdd_HHmmss";

    public static final String PLACEHOLDER_IMAGE = "http://tsawry.net/images/2017/11/26/Portrait_Placeholder.png";


    // broadcast receiver intent filters
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";

    // id to handle the notification in the notification tray
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;

}
