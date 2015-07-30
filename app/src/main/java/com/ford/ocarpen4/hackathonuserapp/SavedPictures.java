package com.ford.ocarpen4.hackathonuserapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class SavedPictures extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_pictures);
        //Get file array
        File directory = this.getFilesDir();
        File[] files = directory.listFiles();

        final ListView listView = (ListView) findViewById(R.id.listView);
        final ArrayList<String> fileList = new ArrayList<String>();
        for(File f : files){
            fileList.add(f.getName());
        }

        final StableArrayAdapter adapter = new StableArrayAdapter(this, android.R.layout.simple_list_item_1, fileList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                final String filename = (String) parent.getItemAtPosition(position);
                String base64String = new String();
                try{
                    FileInputStream fis = openFileInput(filename);
                    base64String = convertStreamToString(fis);
                    fis.close();
                }catch(Exception e){
                    e.printStackTrace();
                }
                //Convert b64 to image
                byte[] decodedString = Base64.decode(base64String, Base64.DEFAULT);
                Bitmap decodeByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                ImageView picture = new ImageView(getApplicationContext());
                picture.setImageBitmap(decodeByte);

                AlertDialog.Builder builder =
                        new AlertDialog.Builder(SavedPictures.this).
                                setMessage(filename).
                                setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).
                                setView(picture);
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }


    private class StableArrayAdapter extends ArrayAdapter<String> {

        HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

        public StableArrayAdapter(Context context, int textViewResourceId,
                                  List<String> objects) {
            super(context, textViewResourceId, objects);
            for (int i = 0; i < objects.size(); ++i) {
                mIdMap.put(objects.get(i), i);
            }
        }

        @Override
        public long getItemId(int position) {
            String item = getItem(position);
            return mIdMap.get(item);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_saved_pictures, menu);
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
