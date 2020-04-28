/*
package com.example.cafeteriaappmuc;

import android.os.AsyncTask;
import android.util.Log;

import com.example.cafeteriaappmuc.Activities.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class TaskRequest {


    // method to get direction using httpurlconnection
    private String requestDirection(String reqUrl) throws IOException {
        String responseString = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(reqUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            //Get the response request
            inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuffer stringBuffer = new StringBuffer();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }

            responseString = stringBuffer.toString();
            bufferedReader.close();
            inputStreamReader.close();


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
        return responseString;
    }
    */
/**
     * TaskRequestDirection and TarskParser are used for the AsyncTask to get time to walk
     * TODO: put them in their own class??
     *//*

    // creates AsyncTask to call request Direction
    public class TaskRequestDirections extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String responseString = "";
            try {
                responseString = requestDirection(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return responseString;
        }

       */
/* // parse json result
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //parse json result here
            MainActivity.TaskParser taskParser = new MainActivity.TaskParser();
            taskParser.execute(s);
        }
*//*

    }



    public class TaskParser extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            JSONObject jsonObject = null;
            String durationAndDistance = null;
            String distance = null;
            ArrayList<String> listDistance = new ArrayList<>();
            try {
                jsonObject = new JSONObject(strings[0]);
                durationAndDistance = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("duration").get("text").toString();
                durationAndDistance += "-";
                durationAndDistance += jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("distance").get("value").toString();

            } catch (JSONException e) {
                e.printStackTrace();
            }

            //listDistance =[duration, distance]
            return durationAndDistance;
        }

        protected void onPostExecute(String durationAndDistance) {
            Log.d("DURATION", durationAndDistance);
            // String foodService = "";
            String[] durationAndDistanceSplit = durationAndDistance.split("-");
            String duration = durationAndDistanceSplit[0];
            int distance = Integer.parseInt(durationAndDistanceSplit[1]);
            arrayList.add(new MyDataListMain(services.get(counterDisplayFoodServiceInList), duration, 5));
            displayMainFoodServicesList();
            counterDisplayFoodServiceInList++;

        }
    }
}
*/
