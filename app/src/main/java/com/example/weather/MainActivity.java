package com.example.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.lang.String;

public class MainActivity extends AppCompatActivity{
    TextView cityText;
    TextView result;

    public void getWeather(View view) {
        try {
            DownloadTask task = new DownloadTask();
            task.execute("https://openweathermap.org/data/2.5/weather?q=" + cityText.getText().toString() + "&appid=439d4b804bc8187953eb36d2a8c26a02");

            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(cityText.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Could not retrieve weather", Toast.LENGTH_SHORT).show();
        }
    }

    public class DownloadTask extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... urls){
            String result="";
            URL url;
            HttpURLConnection urlConnection=null;
            try{
                url=new URL(urls[0]);
                urlConnection=(HttpURLConnection)url.openConnection();
                InputStream in= urlConnection.getInputStream();
                InputStreamReader reader=new InputStreamReader(in);
                int data=reader.read();
                while(data!=-1){
                    char current=(char)data;
                    result= result+ current;
                    data=reader.read();
                }
                return result;
            }catch(Exception e)
            {
                e.printStackTrace();
               /* Activity.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(Main,"Could not find weather", Toast.LENGTH_SHORT).show();
                    }
                });*/
               // Toast.makeText(MainActivity,"Could not find weather", Toast.LENGTH_SHORT).show();
                runOnUiThread(new Runnable() {
                    public void run() {
                        final Toast toast = Toast.makeText(getApplicationContext(), "Could not retrieve weather ", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });

                return null;
            }

        }
        @Override
        protected void onPostExecute(String s){
            super.onPostExecute(s);
            try{
                JSONObject jsonObject=new JSONObject(s);
                String weatherInfo=jsonObject.getString("weather");
                Log.i("weather",weatherInfo);
                JSONArray arr=new JSONArray(weatherInfo);
                String message="";
                for(int i=0;i<arr.length();i++)
                {
                    JSONObject jsonPart=arr.getJSONObject(i);

                    String main=jsonPart.getString("main");
                    String description=jsonPart.getString("description");
                    Log.i("main",jsonPart.getString("main"));
                    Log.i("description",jsonPart.getString("description"));
                    if(!main.equals("") && !description.equals(""))
                        message += main+" : "+ description+"\r\n";
                }
                result.setText(message);

            }catch(Exception e){
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),"Could not retrieve weather", Toast.LENGTH_SHORT).show();
            }

        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cityText=findViewById(R.id.cityText);
        result=findViewById(R.id.weatherText);
        DownloadTask task=new DownloadTask();
        task.execute("https://openweathermap.org/data/2.5/weather?q="+cityText.getText().toString()+"&appid=439d4b804bc8187953eb36d2a8c26a02");
    }
}
