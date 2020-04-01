package com.jamdoli.corus.nearbydevice.datastore;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface NearByDeviceDao {

    @Query("SELECT * FROM near_by_device_entity order by id asc limit :count")
    List<NearByDeviceEntity> get(int count);

    @Insert
    long insert(NearByDeviceEntity nearByDeviceEntity);

    @Insert
    void insertAll(NearByDeviceEntity... nearByDeviceEntities);

    @Query("DELETE FROM near_by_device_entity where id <= :id")
    void deleteAll(long id);
}
