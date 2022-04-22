package com.compscicoolkids.carey;
import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;


public class FetchURL extends AsyncTask<String, Void, String> {
    //async task used for calling google maps distance matrix api
    @SuppressLint("StaticFieldLeak")
    private final View addRunsView;

    public FetchURL(View v) {
        this.addRunsView= v;
    }

    @Override
    protected void onPostExecute(String s) {
        Log.d("ROUTE", s);
        super.onPostExecute(s);
        try{
            //get the data we need from what the call returned
            JSONObject jsonObject = new JSONObject(s);
            final JSONArray rowData = jsonObject.getJSONArray("rows");
            final JSONObject row = rowData.getJSONObject(0);
            final JSONArray elementsData = row.getJSONArray("elements");
            final JSONObject element = elementsData.getJSONObject(0);
            final JSONObject distance = element.getJSONObject("distance");

            TextView txtDistance = addRunsView.findViewById(R.id.distance_ran);
            //set the distance text on screen to what was returned
            txtDistance.setText(distance.getString("text"));
            Button addRun = addRunsView.findViewById(R.id.add_run);
            addRun.setClickable(true);
            EditText minutesInput = addRunsView.findViewById(R.id.minutes_ran);
            String content = minutesInput.getText().toString();
            if(!content.equals("")){
                //allow user to add run if they have already set minutes input
                addRun.setVisibility(View.VISIBLE);
            }
        }catch(Exception e){

        }
    }

    private String downloadUrl(String urlStr) throws IOException {
        String data = "";
        try {
            //create http request and execute
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(urlStr)
                    .method("GET", null)
                    .build();
            Response response = client.newCall(request).execute();
            data = response.body().string();
        } catch (Exception e) {
            Log.d("mylog", "Exception downloading URL: " + e.toString());
            return "error";
        }
        return data;
    }

    @Override
    protected String doInBackground(String... strings) {
        String data = "";
        try {
            data = downloadUrl(strings[0]);
        } catch (Exception e) {
            Log.d("Background Task", e.toString());
        }
        return data;
    }
}