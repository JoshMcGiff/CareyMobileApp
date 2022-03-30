package com.compscicoolkids.carey;
import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;


public class FetchURL extends AsyncTask<String, Void, String> {
    private View addRunsView;

    public FetchURL(View v) {
        this.addRunsView= v;
    }

    @Override
    protected void onPostExecute(String s) {
        Log.d("ROUTE", s);
        super.onPostExecute(s);
        try{
            JSONObject jsonObject = new JSONObject(s);
            final JSONArray rowData = jsonObject.getJSONArray("rows");
            final JSONObject row = rowData.getJSONObject(0);
            final JSONArray elementsData = row.getJSONArray("elements");
            final JSONObject element = elementsData.getJSONObject(0);
            final JSONObject distance = element.getJSONObject("distance");

            Log.d("ROUTE", distance.getString("text"));
            TextView txtDistance = addRunsView.findViewById(R.id.distance_ran);
            txtDistance.setText(distance.getString("text"));

        }catch(Exception e){

        }
    }

    private String downloadUrl(String urlStr) throws IOException {
        String data = "";
        try {
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
            Log.d("mylog", "Background task data " + data.toString());
        } catch (Exception e) {
            Log.d("Background Task", e.toString());
        }
        return data;
    }
}