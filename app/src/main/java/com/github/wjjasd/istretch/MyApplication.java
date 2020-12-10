package com.github.wjjasd.istretch;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

//림 초기화 위해 어플리케이션 클래스 재정의
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);

    }
}
