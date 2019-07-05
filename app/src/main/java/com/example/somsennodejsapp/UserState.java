package com.example.somsennodejsapp;

import android.location.Location;

/*
Keep persistence throughout any activity here
Used for keeping User Info after logging in.
 */
class UserState {
    private static UserState mInstance = null;

    public String token = "";
    public String unique_id = "";
    public String email = "";
    public String name = "";
    public String lastname = "";
    public String state = "";
    public String city = "";
    //public Location location = new Location("user_location");
    public Double lat = -1.0;
    public Double lon = -1.0;


    protected UserState(){}

    public static synchronized UserState getInstance(){
        if(mInstance == null)
            mInstance = new UserState();
        return mInstance;
    }
}
