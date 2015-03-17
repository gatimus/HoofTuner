package io.github.gatimus.hooftuner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.List;

import io.github.gatimus.hooftuner.pvl.PonyvilleLive;
import io.github.gatimus.hooftuner.pvl.Response;
import io.github.gatimus.hooftuner.pvl.Station;
import io.github.gatimus.hooftuner.pvl.Status;
import retrofit.Callback;
import retrofit.RetrofitError;

public class Splash extends Activity implements Callback<Response<Status>>{

    private PVLTextView progressText;
    private Intent intent;
    private PonyvilleLive.PonyvilleLiveInterface ponyvilleLiveInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().hide();
        setContentView(R.layout.activity_splash);
        progressText = (PVLTextView) findViewById(R.id.progressText);
        intent = new Intent(this, Main.class);
        ponyvilleLiveInterface = PonyvilleLive.getPonyvilleLiveInterface();
        ponyvilleLiveInterface.getStatus(this);
    }

    @Override
    public void success(Response<Status> statusResponse, retrofit.client.Response response) {
        progressText.setText(statusResponse.result.timestamp.toString());
        if(statusResponse.result.online){
            ponyvilleLiveInterface.listStations( new Callback<Response<List<Station>>>() {

                @Override
                public void success(Response<List<Station>> stationResponse, retrofit.client.Response response) {
                    Global.stations = stationResponse.result;
                    progressText.setText("Done");
                    startActivity(intent);
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e(getClass().getSimpleName(), error.toString());
                    progressText.setText("Celestia is not accepting letters :(");
                }
            });
        } else {
            Log.e(getClass().getSimpleName(), "API down");
            progressText.setText("Celestia is not accepting letters :(");
        }
    }

    @Override
    public void failure(RetrofitError error) {
        Log.e(getClass().getSimpleName(), error.toString());
        progressText.setText("Celestia is not accepting letters :(");
    }
}
