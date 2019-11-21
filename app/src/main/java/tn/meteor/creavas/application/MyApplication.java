package tn.meteor.creavas.application;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.SharedPreferences;

import com.google.firebase.auth.FirebaseAuth;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.MaterialCommunityModule;
import com.joanzapata.iconify.fonts.MaterialModule;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowLog;
import com.raizlabs.android.dbflow.config.FlowManager;

/**
 * Created by lilk on 02/12/2017.
 */

public class MyApplication extends Application {
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    SharedPreferences pref;

    private FirebaseAuth mAuth;
    @Override
    public void onCreate() {
        super.onCreate();
        FlowManager.init(new FlowConfig.Builder(this).build());
        FlowLog.setMinimumLoggingLevel(FlowLog.Level.V);
        Iconify .with(new MaterialModule())
                .with(new MaterialCommunityModule());


    }
}
