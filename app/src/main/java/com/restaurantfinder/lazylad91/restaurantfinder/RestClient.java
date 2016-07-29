package com.restaurantfinder.lazylad91.restaurantfinder;


import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Parteek on 7/18/2016.
 */
public class RestClient {

    public static String makeHttpCall(String url1){
        URL url = null;
        HttpsURLConnection urlConnection=null;
        try {
            url = new URL(url1);
             urlConnection = (HttpsURLConnection) url.openConnection();

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            return convertStreamToString(in);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            urlConnection.disconnect();
        }

        return null;
    }
    public static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;

        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Log.d("result",sb.toString());
        return sb.toString();
    }

    public static ArrayList<Restaurant> parseJsonInBO(String result){

        ArrayList<Restaurant> restaurantList = new ArrayList<Restaurant>();

        try {
            //JSONObject json = new JSONObject (result);
            Log.d("result12", result);
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = (JSONArray) jsonObject.get("results");
           // Log.d("jsonarraylenght",Integer.toString(jsonArr.length()));
            for(int i=0;i<jsonArray.length();i++) {
                JSONObject jsonOBJ = jsonArray.getJSONObject(i);
                Restaurant restaurant = new Restaurant();
                if(jsonOBJ.has("name")) {
                    restaurant.setName(jsonOBJ.get("name").toString());
                }
                if(jsonOBJ.has("rating")) {
                    restaurant.setRating(Float.parseFloat(jsonOBJ.get("rating").toString()));
                }
                else {
                    restaurant.setRating(Float.parseFloat("0"));
                }
                if(jsonOBJ.has("price_level")) {
                    restaurant.setPrice_level(Integer.parseInt((jsonOBJ.get("price_level").toString())));
                }
                if(jsonOBJ.has("vicinity")) {
                    restaurant.setAddress(jsonOBJ.get("vicinity").toString());
                }
                if(jsonOBJ.has("geometry")) {
                    restaurant.setLat(Double.parseDouble(jsonOBJ.getJSONObject("geometry").getJSONObject("location").get("lat").toString()));
                    restaurant.setLng(Double.parseDouble(jsonOBJ.getJSONObject("geometry").getJSONObject("location").get("lng").toString()));
                }

                if(jsonOBJ.has("photos")) {
                    restaurant.setPhoto_reference(jsonOBJ.getJSONArray("photos").getJSONObject(0).getString("photo_reference").toString());
                }
                if(jsonOBJ.has("opening_hours")){
                    restaurant.setOpen_now(jsonOBJ.getJSONObject("opening_hours").getBoolean("open_now"));
                }
                restaurantList.add(restaurant);


            }/*{
                SmarterParkBO smarterParkBO = new SmarterParkBO();
                          Log.d("call12", "1");
            }*/
            }
        catch (Exception ex){
            ex.printStackTrace();
        }
        Collections.sort(restaurantList);
        return restaurantList;
    }
}
