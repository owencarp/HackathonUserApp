package com.ford.ocarpen4.hackathonuserapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.util.Date;


public class MainActivity extends Activity{

    boolean userRequest = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Initialize Firebase
        Firebase.setAndroidContext(this);
        final Firebase mFirebaseRef = new Firebase("https://fordeyespi.firebaseio.com/EyeSPI");

        //Grab UI elements
        final Button mImageRequestButton = (Button) findViewById(R.id.imageRequestButton);
        final Button mSaveFiles = (Button) findViewById(R.id.saveFiles);
        //Set up message listener
        mFirebaseRef.child("message").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot != null){
                    //Check to see if new message
                    try {
                        //New message Check if it's for me
                        Log.i("message", "new message");
                        int id = Integer.parseInt(dataSnapshot.child("id").getValue().toString());
                        if(id != 1){
                            //Message not for me, ignore
                            Log.i("message", "message not for me");
                            return;
                        }else{
                            //Message for me
                            Log.i("message", "message is for me + " + dataSnapshot.toString());
                            final String picId = dataSnapshot.child("picID").getValue().toString();
                            Log.i("message", mFirebaseRef.child("picture").child(picId).toString());
                            //pull image from Firebase
                            Query imageQuery = mFirebaseRef.child("picture").child(picId).orderByChild("picID");
                            imageQuery.addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                    if(dataSnapshot != null){
                                        String picB64String = dataSnapshot.getValue().toString();
                                        displayNewImage(picB64String);
                                    }
                                }

                                @Override
                                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                                }

                                @Override
                                public void onChildRemoved(DataSnapshot dataSnapshot) {

                                }

                                @Override
                                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                                }

                                @Override
                                public void onCancelled(FirebaseError firebaseError) {

                                }
                            });
                            //Image displayed delete message
                            mFirebaseRef.child("message").child(dataSnapshot.getKey()).removeValue();
                        }
                    }catch(Exception e){
                        Log.i("message", e.toString());
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        //Set up request image button
        mImageRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //set status
                userRequest = true;
                //Set loading icon
                findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
                //Build FirebaseRequest (hardcoded to request self now)
                FirebaseRequest request = new FirebaseRequest();
                try{
                    request.id = 2;
                    request.picID = "";
                    request.FL = 0;
                    request.FR = 0;
                    request.RL = 0;
                    request.RC = 0;
                    request.RR = 0;
                }catch(Exception e){
                    Log.i("message", e.toString());
                }
                mFirebaseRef.child("message").push().setValue(request);
            }
        });

        //Launch saved files activity
        mSaveFiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), SavedPictures.class);
                startActivity(i);
            }
        });
    }


    public void displayNewImage(final String base64String){
        final ImageView mNewImageView = (ImageView) findViewById(R.id.newImageView);
        final TextView mStatusView = (TextView) findViewById(R.id.statusView);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                byte[] decodedString = Base64.decode(base64String, Base64.DEFAULT);
                Bitmap decodeByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                mNewImageView.setImageBitmap(decodeByte);
                findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                if(userRequest) {
                    mStatusView.setText("Status : Requested Image Received");
                }else{
                    mStatusView.setText("Status : Motion Sensor Triggered");
                }
            }
        });
        //Save file to internal storage Filename is request/motion detected + date/time
        String filename = new String();
        if(userRequest) {
            filename = "Request - ";
        }else{
            filename = "Motion - ";
        }
        filename += DateFormat.getDateTimeInstance().format(new Date());
        FileOutputStream outputStream;

        try{
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(base64String.getBytes());
            outputStream.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
        userRequest = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
