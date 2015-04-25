package io.github.gatimus.hooftuner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import io.github.gatimus.hooftuner.pvl.PonyvilleLive;
import io.github.gatimus.hooftuner.pvl.Response;
import io.github.gatimus.hooftuner.pvl.Station;
import io.github.gatimus.hooftuner.pvl.StationList;
import io.github.gatimus.hooftuner.pvl.Status;
import retrofit.Callback;
import retrofit.RetrofitError;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class Splash extends Activity implements Callback<Response<Status>>{

    private TextView progressText;
    private Intent intent;
    private PonyvilleLive.PonyvilleLiveInterface ponyvilleLiveInterface;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().hide();
        setContentView(R.layout.activity_splash);
        progressText = (TextView) findViewById(R.id.progressText);
        intent = new Intent(this, Main.class);
        ponyvilleLiveInterface = PonyvilleLive.getPonyvilleLiveInterface();
        ponyvilleLiveInterface.getStatus(this);
    }

    @Override
    public void success(Response<Status> statusResponse, retrofit.client.Response response) {
        progressText.setText(statusResponse.result.timestamp.toString());
        if(statusResponse.result.online){
            if(Cache.stations.isEmpty()){
                ponyvilleLiveInterface.listStations(Station.AUDIO, new Callback<Response<StationList>>() {
                    @Override
                    public void success(Response<StationList> stationResponse, retrofit.client.Response response) {
                        Cache.stations = stationResponse.result;
                        progressText.setText("Done");
                        startActivity(intent);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.e(getClass().getSimpleName(), error.toString());
                        progressText.setText("Celestia is not accepting letters :( (Check network)");
                    }
                });
            } else startActivity(intent);

        } else {
            Log.e(getClass().getSimpleName(), "API down");
            progressText.setText("Celestia is not accepting letters :( (PVL! may be down)");
        }
    }

    @Override
    public void failure(RetrofitError error) {
        Log.e(getClass().getSimpleName(), error.toString());
        progressText.setText("Celestia is not accepting letters :( (Check network)");
    }
}
