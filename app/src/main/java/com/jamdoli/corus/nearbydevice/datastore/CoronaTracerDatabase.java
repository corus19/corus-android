package com.jamdoli.corus.nearbydevice.datastore;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {NearByDeviceEntity.class}, version = 1)
public abstract class CoronaTracerDatabase extends RoomDatabase {

    private static final String DB_NAME = "nearby_devices";

    private static CoronaTracerDatabase coronaTracerDatabase;

    public abstract NearByDeviceDao nearByDeviceDao();

    public static CoronaTracerDatabase getDatabase(final Context context) {
        synchronized (CoronaTracerDatabase.class) {
            if (coronaTracerDatabase == null) {
                coronaTracerDatabase = Room.databaseBuilder(context.getApplicationContext(),
                        CoronaTracerDatabase.class, DB_NAME)
                        .allowMainThreadQueries()
                        .build();
            }
        }
        return coronaTracerDatabase;
    }

    public static NearByDeviceDao getNearByDeviceDao(final Context context) {
        return getDatabase(context).nearByDeviceDao();
    }
}
