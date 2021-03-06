package com.example.david.testing;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.johnhiott.darkskyandroidlib.ForecastApi;
import com.johnhiott.darkskyandroidlib.RequestBuilder;
import com.johnhiott.darkskyandroidlib.models.Request;
import com.johnhiott.darkskyandroidlib.models.WeatherResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;



public class MainActivity extends AppCompatActivity {

    //new layout recyclerview
    private RelativeLayout rLayout;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;
    private ArrayList<DataModel> allDataArray;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //double axx= weatherCurrent.ax;
        //double ayy = weatherCurrent.ay;
        final double axx = 36.9741;
        final double ayy = -122.0308;
        final String[] currWeather = new String[1];
        final String[] future1Weather = new String[1];
        final String[] future2Weather = new String[1];
        final String[] future3Weather = new String[1];
        final double[] currTemp = new double[1];
        final double[] future1Temp = new double[1];
        final double[] future2Temp = new double[1];
        final double[] future3Temp = new double[1];
        final String[] currIcon = new String[1];
        final List<String> futureWeatherList = new ArrayList<>();
        final List<Double> futureTempList = new ArrayList<>();
        super.onCreate(savedInstanceState);
        ForecastApi.create("4fb2c715ea744173c72290437de1c776");
        final String apiKey = "4fb2c715ea744173c72290437de1c776";
        setContentView(R.layout.activity_main);

        //Calendar stuff
        //Get Calendar and Date to display NOW and the next 3 hours;
        Calendar calendar = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("hh a");


        final TextView weather_print = (TextView) findViewById(R.id.textView_Weather);

        final RequestBuilder weather = new RequestBuilder();

        final String Latitude = Double.toString(axx);
        final String Longitude = Double.toString(ayy);
        final String baseURL = "https://api.darksky.net/forecast";

        Request request = new Request();
        request.setLat(Latitude);
        request.setLng(Longitude);
        request.setUnits(Request.Units.US);
        request.setLanguage(Request.Language.ENGLISH);
        request.addExcludeBlock(Request.Block.CURRENTLY);
        request.removeExcludeBlock(Request.Block.CURRENTLY);

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = baseURL +"/" +apiKey + "/" + Latitude + "," + Longitude;

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.GET, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        try {
                            JSONObject object = new JSONObject(response);
                            JSONObject current = object.getJSONObject("currently");

                            JSONObject future = object.getJSONObject("hourly");

                            int temp = current.getInt("temperature");
                            currTemp[0] = temp;


                            String summary = current.getString("summary");
                            currWeather[0] = summary;


                            String icon = current.getString("icon");
                            currIcon[0] = icon;

                            JSONArray futureData = future.getJSONArray("data");
                            for (int i = 0; i < futureData.length(); i++) {
                                futureWeatherList.add(futureData.getJSONObject(i).getString("summary"));
                                futureTempList.add(Double.valueOf(futureData.getJSONObject(i).getString("temperature")));
                            }

                            future1Weather[0] = futureWeatherList.get(1);
                            future2Weather[0] = futureWeatherList.get(2);
                            future3Weather[0] = futureWeatherList.get(3);
                            future1Temp[0] = futureTempList.get(1);
                            future2Temp[0] = futureTempList.get(2);
                            future3Temp[0] = futureTempList.get(3);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("Not Working");
            }
        });


        // Add the request to the RequestQueue.
        queue.add(stringRequest);


        weather.getWeather(request, new Callback<WeatherResponse>() {
            String TAG = null;
            @Override
            public void success(WeatherResponse weatherResponse, Response response) {
                weather_print.setText(weatherResponse.getCurrently().getTemperature() + " °F.");
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.d(TAG, "Error while calling: " + retrofitError.getUrl());
                Log.d(TAG, retrofitError.toString());
                weather_print.setText("failure to access weather");
            }
        });



        // Recycler view*************************************************************************
        // RecyclerView displays current and next hours, and the list of buttons for suggested activities.
        allDataArray = new ArrayList<>(); //create Array list

        Date nowHour = calendar.getTime();
        double passTime = nowHour.getTime()/1000L;

        // add the current hours data to the recyclerlist
        allDataArray.add (new DataModel("Now", "Restaurants", "Bars", "Active", "Indoor", currWeather, currTemp, axx, ayy, passTime));
        calendar.add(Calendar.HOUR, 1); //add an hour to the current time
        Date oneHour = calendar.getTime();
        passTime = oneHour.getTime()/1000L;

        // add the next hours data to the recyclerlist
        allDataArray.add (new DataModel(dateFormat.format(oneHour), "Restaurants", "Bars", "Active", "Indoor", future1Weather, future1Temp, axx, ayy, passTime));
        calendar.add(Calendar.HOUR, 1);//add an hour to the current time
        Date twoHour = calendar.getTime();
        passTime = twoHour.getTime()/1000L;

        // add the 2nd next hours data to the recyclerlist
        allDataArray.add ( new DataModel(dateFormat.format(twoHour), "Restaurants", "Bars", "Active", "Indoor", future2Weather, future2Temp, axx, ayy, passTime));
        calendar.add(Calendar.HOUR, 1); //add an hour to the previous time
        Date threeHour = calendar.getTime();
        passTime = threeHour.getTime()/1000L;

        // add the 3rd next hours data to the recyclerlist
        allDataArray.add ( new DataModel(dateFormat.format(threeHour), "Restaurants", "Bars", "Active", "Indoor", future3Weather, future3Temp, axx, ayy, passTime));

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MainAdapter(allDataArray);
        mRecyclerView.setAdapter(mAdapter);

    }
}


