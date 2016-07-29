package com.restaurantfinder.lazylad91.restaurantfinder;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

/**
 * Created by Parteek on 7/18/2016.
 */
public class Restaurant implements Serializable,Comparable{
    Double lat;
    Double lng;
    String name;
    boolean open_now;
    String photo_reference;
    Float rating;
    String address;

    public int getPrice_level() {
        return price_level;
    }

    public void setPrice_level(int price_level) {
        this.price_level = price_level;
    }

    int price_level;



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isOpen_now() {
        return open_now;
    }

    public void setOpen_now(boolean open_now) {
        this.open_now = open_now;
    }

    public String getPhoto_reference() {
        return photo_reference;
    }

    public void setPhoto_reference(String photo_reference) {
        this.photo_reference = photo_reference;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    @Override
    public String toString() {
        return "Restaurant{" +
                "lat=" + lat +
                ", lng=" + lng +
                ", price_level=" + price_level +
                ", name='" + name + '\'' +
                ", open_now=" + open_now +
                ", photo_reference='" + photo_reference + '\'' +
                ", rating=" + rating +
                ", address='" + address + '\'' +
                '}';
    }

    @Override
    public int compareTo(Object o) {
        Restaurant r = (Restaurant)o;
     return r.getRating().compareTo(this.getRating());
    }
}
