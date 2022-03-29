package com.example.weathertoday;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    private EditText yourCityEditText;
    private Button checkWeatherButton;
    private TextView temperatureTextView;
    private TextView temperatureRealFeelTextView;
    private TextView windTextView;
    private TextView cloudTextView;
    private Button goToAirQuality;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        yourCityEditText = findViewById(R.id.yourCity);
        checkWeatherButton = findViewById(R.id.checkWeatherButton);
        temperatureTextView = findViewById(R.id.resultTemperature);
        temperatureRealFeelTextView = findViewById(R.id.resultTemperatureRealFeel);
        windTextView = findViewById(R.id.windTextView);
        cloudTextView = findViewById(R.id.cloudTextView);
        goToAirQuality = findViewById(R.id.goToAirQuality);

        goToAirQuality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,CheckAirActivity.class);
                startActivity(intent);
            }
        });


        checkWeatherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (yourCityEditText.getText().toString().trim().equals("")){
                    Toast.makeText(MainActivity.this, "Please enter your city", Toast.LENGTH_LONG).show();
                }
                else {
                    String currentCity = yourCityEditText.getText().toString().trim();
                    String key = "096e19cf4ddd77c34562b02c67aa3db8";
                    String url = "https://api.openweathermap.org/data/2.5/weather?q=" + currentCity+ "&appid=" + key + "&units=metric";
                    new getUrlData().execute(url);
                }
            }
        });
    }

    private class getUrlData extends AsyncTask<String, String, String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            temperatureTextView.setText("Ожидайте...");
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

                while ((str = reader.readLine()) != null){
                    buffer.append(str).append("\n");
                    return buffer.toString();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if (connection != null){
                    connection.disconnect();
                }
                if (reader != null){
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
                temperatureTextView.setText("Temperature: " + jsonObject.getJSONObject("main")
                        .getDouble("temp") + " °C");
                temperatureRealFeelTextView.setText("Real Feel: " + jsonObject.getJSONObject("main")
                        .getDouble("temp_min") +" °C");
                windTextView.setText("Wind speed: " + jsonObject.getJSONObject("wind").getString("speed") + " m/s");
                JSONArray jsonArray = jsonObject.getJSONArray("weather");
                JSONObject weather = jsonArray.getJSONObject(0);
                cloudTextView.setText("Description: " + weather.getString("description"));



            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }
}