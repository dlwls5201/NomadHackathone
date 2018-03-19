package com.example.leejinseong.nomadhackathone;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by leejinseong on 2018. 3. 17..
 */

public class MyApplication extends Application {

    public static boolean DEBUG = false;

    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize Realm. Should only be done once when the application starts.
        Realm.init(this);

        //migration -> http://developer88.tistory.com/77?category=220430
        RealmConfiguration config = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                //.schemaVersion(1)
                .build();

        Realm.setDefaultConfiguration(config);

        this.DEBUG = isDebuggable(this);

    }

    /**
     * get Debug Mode
     *
     * @param context
     * @return
     */
    private boolean isDebuggable(Context context) {
        boolean debuggable = false;

        PackageManager pm = context.getPackageManager();
        try {
            ApplicationInfo appInfo = pm.getApplicationInfo(context.getPackageName(), 0);
            debuggable = (0 != (appInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE));
        } catch (PackageManager.NameNotFoundException e) {
			/* debuggable variable will remain false */
        }

        return debuggable;
    }
}