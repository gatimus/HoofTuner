package io.github.gatimus.hooftuner.pvl;

import android.util.Log;

import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class APIWorker {

    public static final String API_ROOT = "http://ponyvillelive.com/api/";
    public static final String API_STATUS = API_ROOT + "index/status";
    public static final String API_STATION = API_ROOT + "station/list";
    public static final String API_STATION_CAT = API_STATION + "/category/";
    public static final String API_STATION_ID = API_ROOT + "station/index/id/";
    public static final String API_STATION_CODE = API_ROOT + "station/index/station/";
    public static final String API_NP = API_ROOT + "nowplaying";
    public static final String API_NP_ID = API_NP + "/index/id/";
    public static final String API_NP_CODE = API_NP + "/index/station/";

    public static final String STATION_TYPE_AUDIO = "audio";
    public static final String STATION_TYPE_VIDEO = "video";


    public boolean getStatus(){
        APIStatus status = new Gson().fromJson(getResponse(API_STATUS).result, APIStatus.class);
        return status.online;
    }

    public List<Station> listStations(){
        List<Station> stationList = new ArrayList<Station>();
        stationList =  Arrays.asList(new Gson().fromJson(getResponse(API_STATION).result, Station[].class));
        return stationList;
    }

    public List<Station> listStations(String stationType){
        List<Station> stationList = new ArrayList<Station>();
        stationList =  Arrays.asList(new Gson().fromJson(getResponse(API_STATION_CAT + stationType).result, Station[].class));
        return stationList;
    }

    public Station getStation(int id){
        return new Gson().fromJson(getResponse(API_STATION_ID + String.valueOf(id)).result, Station.class);
    }

    public Station getStation(String shortcode){
        return new Gson().fromJson(getResponse(API_STATION_CODE + shortcode).result, Station.class);
    }

    public NowPlaying getNowPlaying(){
        //TODO
        return null;
    }

    public NowPlaying getNowPlaying(int id){
        return new Gson().fromJson(getResponse(API_NP_ID + String.valueOf(id)).result, NowPlaying.class);
    }

    public NowPlaying getNowPlaying(String shortcode){
        return new Gson().fromJson(getResponse(API_NP_CODE + shortcode).result, NowPlaying.class);
    }

    public APIResponse getResponse(String url){
        APIResponse apiResponse = null;
        try {
            HttpResponse httpResponse = new DefaultHttpClient().execute(new HttpGet(url));
            if(httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                Reader reader = new InputStreamReader(httpResponse.getEntity().getContent());
                apiResponse = new Gson().fromJson(reader, APIResponse.class);
            }
        }catch (Exception e){
            Log.e(getClass().getSimpleName(), e.toString());
        }
        return apiResponse;
    }

}
