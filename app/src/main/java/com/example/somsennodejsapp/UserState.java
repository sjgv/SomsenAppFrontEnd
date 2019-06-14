package com.example.somsennodejsapp;

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


    protected UserState(){}

    public static synchronized UserState getInstance(){
        if(mInstance == null)
            mInstance = new UserState();
        return mInstance;
    }
}
