package com.example.zappycode.whatstheweather;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    EditText editText;
    TextView resultTextView;
    String main, description;
    int temperature, minTemp,maxTemp,feelsLike,humidity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editText);
        resultTextView = findViewById(R.id.resultTextView);
    }

    public void getWeather(View view) {
        try {
            DownloadTask task = new DownloadTask();
            String encodedCityName = URLEncoder.encode(editText.getText().toString(), "UTF-8");

            task.execute("http://api.openweathermap.org/data/2.5/weather?q=" + encodedCityName + "&appid=96b6f9d01adc1bd1fc9650c9a9832e46   ");

            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"Could not find weather :(",Toast.LENGTH_SHORT).show();
        }
    }

    public class DownloadTask extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {

                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();

                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }

                return result;

            } catch (Exception e) {
                e.printStackTrace();

                Toast.makeText(getApplicationContext(),"Could not find weather :(",Toast.LENGTH_SHORT).show();

                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject jsonObject = new JSONObject(s);

                try{

                    String weatherInfo = jsonObject.getString("weather");
                    JSONObject j3= new JSONObject(jsonObject.getString("main"));


                    Log.i("Weather content", weatherInfo);
                    Log.i("Temperature", j3.toString());


                    JSONArray arr = new JSONArray(weatherInfo);
//                JSONArray arr1 = new JSONArray(temp);

                    String message = "";

                    for (int i=0; i < arr.length(); i++) {
                        JSONObject jsonPart = arr.getJSONObject(i);

                        main = jsonPart.getString("main");
                        description = jsonPart.getString("description");

                        if (!main.equals("") && !description.equals("")) {
                            message += main + ": " + description + "\r\n";
                        }
                    }


                    temperature=j3.getInt("temp")-273;
                    minTemp = j3.getInt("temp_min")-273;
                    maxTemp = j3.getInt("temp_max") -273;
                    feelsLike = j3.getInt("feels_like")-273;
                    humidity =j3.getInt("humidity") ;


                    message +="Minimum Temperature: "+ String.valueOf(minTemp) +"\r\n";
                    message +="Maximum Temperature: "+ String.valueOf(maxTemp)+"\r\n";
                    message +="Feels Like: "+String.valueOf(feelsLike)+"\r\n";
                    message +="Humidity: "+String.valueOf(humidity)+"% \r\n";

                    if (!message.equals("")) {
                        resultTextView.setText(message);
                    } else {
                        Toast.makeText(getApplicationContext(),"Could not find weather :(",Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),"It is not a city :( ",Toast.LENGTH_SHORT).show();
                }




            } catch (Exception e) {

                Toast.makeText(getApplicationContext(),"Could not find weather :(",Toast.LENGTH_SHORT).show();

                e.printStackTrace();
            }

        }
    }
}
