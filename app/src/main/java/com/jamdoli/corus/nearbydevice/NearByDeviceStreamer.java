package com.jamdoli.corus.nearbydevice;

import android.os.AsyncTask;
import android.util.Log;
import com.jamdoli.corus.api.model.BulkDeviceRequest;
import com.jamdoli.corus.api.model.BulkDeviceResponse;
import com.jamdoli.corus.api.model.DeviceRequest;
import com.jamdoli.corus.nearbydevice.datastore.NearByDeviceDao;
import com.jamdoli.corus.nearbydevice.datastore.NearByDeviceEntity;
import com.jamdoli.corus.utils.ApiInterface;
import com.jamdoli.corus.utils.AppSettingsUsingSharedPrefs;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import retrofit2.Call;
import retrofit2.Response;

@Builder
public class NearByDeviceStreamer extends AsyncTask<Integer, Integer, Integer> {

    private static String LOG_TAG = "NearByDeviceStreamer";

    private static final String STREAM_IN_PROGRESS_TAG = "STREAM_IN_PROGRESS_TAG";
    private static final boolean DEFAULT_STREAM_ENABLED = false;
    private static final String STREAM_ENABLED_TAG = "STREAM_ENABLED_TAG";

    private static final int DEFAULT_MAX_SINGLE_UPLOAD_COUNT = 1000;
    private static final String MAX_SINGLE_COUNT_TAG = "MAX_SINGLE_COUNT_TAG";

    private static final int DEFAULT_MIN_SINGLE_UPLOAD_COUNT = 10;
    private static final String MIN_SINGLE_COUNT_TAG = "MIN_SINGLE_COUNT_TAG";

    private final ApiInterface apiClient;
    private final NearByDeviceDao nearByDeviceDao;

    private final AppSettingsUsingSharedPrefs appSettingsUsingSharedPrefs;

    @Override
    protected Integer doInBackground(Integer... params) {

        int maxCount = appSettingsUsingSharedPrefs.getIntValueForKey(MAX_SINGLE_COUNT_TAG, DEFAULT_MAX_SINGLE_UPLOAD_COUNT);
        int minCount = appSettingsUsingSharedPrefs.getIntValueForKey(MIN_SINGLE_COUNT_TAG, DEFAULT_MIN_SINGLE_UPLOAD_COUNT);


        while (true) {

            boolean streamEnabled = appSettingsUsingSharedPrefs.getBooleanValueForKey(STREAM_ENABLED_TAG, DEFAULT_STREAM_ENABLED);
            List<NearByDeviceEntity> nearByDeviceEntities = nearByDeviceDao.get(maxCount);
            if (!streamEnabled || nearByDeviceEntities.size() < minCount) {
                break;
            }

            BulkDeviceRequest bulkDeviceRequest = getBulkDeviceRequest(nearByDeviceEntities);

            Call<BulkDeviceResponse> bulkDeviceResponseCall = apiClient.bulkDeviceUpload(bulkDeviceRequest);
            try {
                Response<BulkDeviceResponse> bulkDeviceResponseWrapper = bulkDeviceResponseCall.execute();

                if (bulkDeviceResponseWrapper.isSuccessful()) {
                    break;
                }


                nearByDeviceDao.deleteAll(nearByDeviceEntities.get(nearByDeviceEntities.size() - 1).getId());
            } catch (IOException e) {
                Log.e(LOG_TAG, "Unexpected error in streaming data.", e);
                break;
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Integer result) {

        appSettingsUsingSharedPrefs.setBooleanValueForKey(STREAM_IN_PROGRESS_TAG, false);
    }

    private BulkDeviceRequest getBulkDeviceRequest(List<NearByDeviceEntity> nearByDeviceEntities) {

        List<DeviceRequest> deviceRequests = new ArrayList<>(nearByDeviceEntities.size());
        for (NearByDeviceEntity nearByDeviceEntity : nearByDeviceEntities) {
            deviceRequests.add(getDeviceRequest(nearByDeviceEntity));
        }

        return BulkDeviceRequest.builder().deviceRequests(deviceRequests).build();
    }

    private DeviceRequest getDeviceRequest(NearByDeviceEntity nearByDeviceEntity) {
        return DeviceRequest.builder()
                .bluetoothSignature(nearByDeviceEntity.getBluetoothSignature())
                .name(nearByDeviceEntity.getName())
                .timestamp(nearByDeviceEntity.getTimestamp())
                .lat(nearByDeviceEntity.getLat())
                .lng(nearByDeviceEntity.getLng())
                .rssi(nearByDeviceEntity.getRssi())
                .txPower(nearByDeviceEntity.getTxPower())
                .build();
    }
}
