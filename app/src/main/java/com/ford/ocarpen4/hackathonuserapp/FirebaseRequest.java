package com.ford.ocarpen4.hackathonuserapp;

/**
 * Created by ocarpen4 on 7/28/2015.
 */
public class FirebaseRequest {
    public int id;
    public String picID;
    public int FL;
    public int FR;
    public int RL;
    public int RC;
    public int RR;

    FirebaseRequest(){}

    FirebaseRequest(int id, String picId, int FL, int FR, int RL, int RC, int RR){
        this.id = id;
        this.picID = picId;
        this.FL = FL;
        this.FR = FR;
        this.RL = RL;
        this.RC = RC;
        this.RR = RR;
    }
}
