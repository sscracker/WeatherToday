package com.example.weathertoday;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class CheckAirActivity extends AppCompatActivity {
    private TextView checkCoTextView;
    private TextView messageTextView;
    private Button checkAirQualityButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_air);
        checkCoTextView = findViewById(R.id.resultCo);
        checkAirQualityButton = findViewById(R.id.checkQualityAirButton);
        messageTextView = findViewById(R.id.messageTextView);


        checkAirQualityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://api.openweathermap.org/data/2.5/air_pollution?lat=50&lon=52&appid=096e19cf4ddd77c34562b02c67aa3db8";
                new getUrlData().execute(url);


            }
        });
    }

    private class getUrlData extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            checkCoTextView.setText("Ожидайте...");
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpsURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(strings[0]);
                connection = (HttpsURLConnection) url.openConnection();
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(inputStream));

                StringBuffer buffer = new StringBuffer();
                String str = "";

                while ((str = reader.readLine()) != null) {
                    buffer.append(str).append("\n");
                    return buffer.toString();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("list");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    int aqi = obj.getJSONObject("main").getInt("aqi");
                    checkCoTextView.setText("Co: " + obj.getJSONObject("components").getDouble("co") + " μg/m3" + "\n" +
                            "NO: " + obj.getJSONObject("components").getDouble("no") + " μg/m3" + "\n" +
                            "N02: " + obj.getJSONObject("components").getDouble("no2") + " μg/m3" + "\n" +
                            "O3: " + obj.getJSONObject("components").getDouble("o3") + " μg/m3" + "\n" +
                            "SO2: " + obj.getJSONObject("components").getDouble("so2") + " μg/m3" + "\n" +
                            "PM25: " + obj.getJSONObject("components").getDouble("pm2_5") + " μg/m3" + "\n" +
                            "PM10: " + obj.getJSONObject("components").getDouble("pm10") + " μg/m3" + "\n" +
                            "NH3: " + obj.getJSONObject("components").getDouble("nh3") + " μg/m3" + "\n" +
                            "AQI Status: " + aqi);

                    if (aqi == 1 || aqi == 2 || aqi == 3) {
                        messageTextView.setText("Today is a good day for a walk." + "\n" + "Air pollution is normal!");
                    } else {
                        messageTextView.setText("Today is a bad day for a walk" + "\n" + "Air pollution is dangerous");
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }


}



