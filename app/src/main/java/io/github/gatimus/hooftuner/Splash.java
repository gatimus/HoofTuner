package io.github.gatimus.hooftuner;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import java.util.List;

import io.github.gatimus.hooftuner.pvl.APIWorker;
import io.github.gatimus.hooftuner.pvl.Station;


public class Splash extends Activity {

    private TextView progressText;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        progressText = (TextView) findViewById(R.id.progressText);
        progressText.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/SourceSansPro-Regular.ttf"));
        intent = new Intent(this, Main.class);
        new PreLoader().execute();
    }

    public class PreLoader extends AsyncTask<Void, String, List<Station>>{

        APIWorker api;

        @Override
        protected void onPreExecute() {
            api = new APIWorker();
        }

        @Override
        protected List<Station> doInBackground(Void... params) {
            List<Station> stations = null;
            if(api.getStatus()){
                publishProgress("online");
                stations = api.listStations();
                publishProgress("done");
            }else{
                publishProgress("down");
            }
            return stations;
        }

        @Override
        protected void onProgressUpdate(String... progress) {
            progressText.setText(progress[0]);
        }

        @Override
        protected void onPostExecute(List<Station> result){
            Global.stations = result;
            startActivity(intent);
        }
    }

}
