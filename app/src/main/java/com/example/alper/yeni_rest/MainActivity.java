package com.example.alper.yeni_rest;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.Response;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;


public class MainActivity extends AppCompatActivity {
    TextView sehir_isim;
    TextView hava_durumu;
    TextView last_update;
    TextView dereceText;
    TextView yarin_hava;
    ImageView iccon;
    ImageButton inf;


    RequestQueue requestQueue;  // This is our requests queue to process our HTTP requests.
    double longi;
    double lati;
                                               //BURADA
    String baseUrl = "http://api.openweathermap.org/data/2.5/forecast?";
    String appId = "&APPID=c63fb17f536fbe92a9dadfffae71271c";
    String url;
    private LocationManager locationManager;
    private LocationListener listener;
    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.sehir_isim = (TextView) findViewById(R.id.sehir_isim);  //Bulunan şehir
        this.hava_durumu = (TextView) findViewById(R.id.hava_durumu);  // Sıcaklık
        this.last_update = (TextView) findViewById(R.id.last_update);  // Son güncelleme
        this.dereceText = (TextView) findViewById(R.id.derece);  // Latitude.
        this.yarin_hava = (TextView) findViewById(R.id.tomorrow);  // Longitude.
        this.iccon = (ImageView) findViewById(R.id.iccon);  // Longitude.
        this.inf=(ImageButton) findViewById(R.id.button1) ;

        requestQueue = Volley.newRequestQueue(this);  // This setups which we will need to make HTTP requests.

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        inf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                builder1.setMessage("Veriler openweather.com üzerinden alınmıştır.\nHücresel veriden alınan konumu kullanır. \n\n github.com/alpergenc");
                builder1.setCancelable(true);



                builder1.setNegativeButton(
                        "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();
            }
        });




        listener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {

                longi=location.getLongitude();
                lati=location.getLatitude();
                degerOku();
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };



    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onStart() {
        super.onStart();
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, listener);
        degerOku();

    }


    private void sehirYaz(String sehir_ismi) {

        this.sehir_isim.setText(sehir_ismi);
    }

    private void konumYok() {
       updateYaz("Konum bilgisi alınıyor...");
    }

    private void sicaklikYaz(String sicak) {
        sicak = sicak.replace(".","");
        int h = Integer.valueOf(sicak);
        h=h/100;
        this.hava_durumu.setText(h + "°");
    }
    private void updateYaz(String update) {
        this.last_update.setText(update);
    }

    private void havaDurumuYaz(String durum) {
        this.dereceText.setText(durum);
    }

    private void yarinYaz(String sicak,String durum) {
        sicak = sicak.replace(".","");
        int h = Integer.valueOf(sicak);
        h=h/100;
        this.yarin_hava.setText(" Yarın: " + h + "°   "+durum );
    }
    private void setRepoListText(String str) {
        this.sehir_isim.setText(str);
    }

    private void degerOku() {

         this.url = this.baseUrl + "lat="+lati+"&lon="+longi +"&units=metric"+ appId;
        Log.e("Volley", "Trying1.");
        // Doc: https://developer.android.com/training/volley/index.html
        JsonObjectRequest arrReq = new JsonObjectRequest(Request.Method.GET, url,
                new Response.Listener<JSONObject>() {
                    @SuppressLint("ResourceType")
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("Volley", "Trying");

                        if (response.length() > 0) {
                                try {

                                    Log.e("Volley", "Trying3.");
                                    JSONArray list = response.getJSONArray("list");  //list'in icine gir
                                    JSONObject sicaklikj= list.getJSONObject(0).getJSONObject("main");
                                    String sicak = sicaklikj.get("temp").toString();

                                    JSONObject sicakliky= list.getJSONObject(8).getJSONObject("main");
                                    String y_sicak = sicakliky.get("temp").toString();

                                    JSONObject zaman= list.getJSONObject(0);
                                    String u_time = zaman.get("dt_txt").toString();

                                    JSONObject city = response.getJSONObject("city");
                                    String c_name = city.get("name").toString();

                                    JSONObject hdurum= list.getJSONObject(0).getJSONArray("weather").getJSONObject(0);
                                    String durum = hdurum.get("main").toString();

                                    JSONObject hdurum_y= list.getJSONObject(0).getJSONArray("weather").getJSONObject(0);
                                    String durum_y = hdurum_y.get("main").toString();

                                    if(c_name.equals("Earth")){
                                        konumYok();
                                    }else {
                                        sehirYaz(c_name);
                                        sicaklikYaz(sicak);
                                        updateYaz("Last Update: " +u_time);

                                        switch (durum){
                                            case "Clear":
                                                iccon.setImageResource ( R.drawable.clearbg);
                                                durum="Açık";
                                                break;
                                            case "Few clouds":

                                                iccon.setImageResource ( R.drawable.scatteredbg);
                                                durum="Az Bulutlu";
                                                break;
                                            case "Scattered clouds":
                                                iccon.setImageResource ( R.drawable.scatteredbg);
                                                durum="Parçalı bulutlu";
                                                break;
                                            case "Clouds":
                                                iccon.setImageResource ( R.drawable.scatteredbg);
                                                durum="Bulutlu";
                                                break;
                                            case "Broken clouds":
                                                iccon.setImageResource ( R.drawable.scatteredbg);
                                                durum="Çok Bulutlu";
                                                break;
                                            case "Shower rain":
                                                iccon.setImageResource ( R.drawable.rainbg);
                                                durum="Hafif Yağmurlu";
                                                break;
                                            case "Rain":
                                                iccon.setImageResource ( R.drawable.rainbg);
                                                durum="Yağmurlu";
                                                break;
                                            case "Thunderstorm":
                                                iccon.setImageResource ( R.drawable.thunderbg);
                                                durum="Fırtına";
                                                break;
                                            case "Snow":
                                                iccon.setImageResource ( R.drawable.snowbg);
                                                durum="Karlı";
                                                break;
                                            case "Mist":
                                                iccon.setImageResource ( R.drawable.mistbg);
                                                durum="Sisli";
                                                break;
                                        }
                                        switch (durum_y){
                                            case "Clear":
                                                durum_y="Açık";
                                                break;
                                            case "Few clouds":
                                                durum_y="Az Bulutlu";
                                                break;
                                            case "Scattered clouds":
                                                durum_y="Parçalı bulutlu";
                                                break;
                                            case "Clouds":
                                                durum_y="Bulutlu";
                                                break;
                                            case "Broken clouds":
                                                durum_y="Çok Bulutlu";
                                                break;
                                            case "Shower rain":
                                                durum_y="Hafif Yağmurlu";
                                                break;
                                            case "Rain":
                                                durum_y="Yağmurlu";
                                                break;
                                            case "Thunderstorm":
                                                durum_y="Fırtına";
                                                break;
                                            case "Snow":
                                                durum_y="Karlı";
                                                break;
                                            case "Mist":
                                                durum_y="Sisli";
                                                break;
                                        }

                                        yarinYaz(y_sicak,durum_y);
                                        havaDurumuYaz(durum);

                                    }
                                } catch (JSONException e) {
                                    Log.e("Volley", "Invalid JSON Object.");
                                }

                        } else {
                            setRepoListText("Hata oluştu.");
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        setRepoListText("REST API çağırılamadı");
                        Log.e("Volley", error.toString());
                    }
                }
        );
        requestQueue.add(arrReq);
    }
};
