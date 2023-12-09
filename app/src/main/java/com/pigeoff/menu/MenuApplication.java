package com.pigeoff.menu;

import android.app.Application;
import android.content.Context;

import androidx.room.Room;

import com.pigeoff.menu.database.MenuDatabase;

import org.acra.ACRA;
import org.acra.config.CoreConfigurationBuilder;
import org.acra.config.HttpSenderConfigurationBuilder;
import org.acra.config.ToastConfigurationBuilder;
import org.acra.data.StringFormat;
import org.acra.sender.HttpSender;

public class MenuApplication extends Application {

    public MenuDatabase database;

    @Override
    public void onCreate() {
        super.onCreate();
        database = Room.databaseBuilder(this, MenuDatabase.class, "database")
                .build();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        ACRA.init(this, new CoreConfigurationBuilder()
                .withBuildConfigClass(BuildConfig.class)
                .withReportFormat(StringFormat.JSON)
                .withPluginConfigurations(
                        new ToastConfigurationBuilder()
                                .withText(getString(R.string.crash_messge))
                                .build()
                )
                .withPluginConfigurations(
                        new HttpSenderConfigurationBuilder()
                                .withUri(BuildConfig.ACRA_ENDPOINT)
                                .withBasicAuthLogin(BuildConfig.ACRA_USER)
                                .withBasicAuthPassword(BuildConfig.ACRA_PASSWORD)
                                .withHttpMethod(HttpSender.Method.POST)
                                .build()
                ));
    }
}
