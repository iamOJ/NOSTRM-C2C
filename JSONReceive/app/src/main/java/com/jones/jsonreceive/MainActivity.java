package com.jones.jsonreceive;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import java.io.Console;
import java.util.ArrayList;
import java.util.HashMap;

import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();

    private ProgressDialog pDialog;
    private ListView lv;

    //private static String url = "https://api.androidhive.info/contacts/";
    private String url = "http://192.168.43.24:4100/mi";

    ArrayList<HashMap<String, String>> contactList;
    ArrayList<HashMap<String, String>> blockList;
    SwipeRefreshLayout sf;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contactList = new ArrayList<>();
        blockList = new ArrayList<>();

        lv = (ListView) findViewById(R.id.list);
        sf = findViewById(R.id.swiperefresh);
        sf.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i("Refresh","Refreshing");
                check(findViewById(R.id.button));
            }
        });
        //new GetContacts().execute();
    }



    public void check(View v){
        if(!sf.isRefreshing())
            sf.setRefreshing(true);
        EditText et = (EditText) findViewById(R.id.edit);
        url = et.getText().toString();
        Log.i("url",url);
        Log.i("what","button pressed");
        //new GetContacts().execute();
        new GetBlocks().execute();
    }

    private class GetBlocks extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... voids) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    //JSONArray contacts = jsonObj.getJSONArray("transactions");
                    //JSONArray contacts = jsonObj.getJSONArray("cc");
                    JSONArray blocks = jsonObj.getJSONArray("blocks");
                    System.out.print(blocks.toString());
                    Log.e(TAG, "Response from url: " + blocks.toString());
                    // looping through All Contacts
                    blockList.clear();
                    for (int i = 0; i < blocks.length(); i++) {
                        JSONObject c = blocks.getJSONObject(i);

                        String index = c.getString("index");
                        String prev_hash = c.getString("prev_hash");
                        String time = c.getString("timestamp");
                        JSONObject data = c.getJSONObject("data");
                        //JSONArray data = c.getJSONArray("data");
                        String proof="";
                        try {
                            proof = data.getString("proof-of-work");
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                        JSONArray trans = new JSONArray();
                        try {
                            trans = data.getJSONArray("transactions");

                        }catch(Exception e){
                            e.printStackTrace();
                        }
                        Log.i("proof",proof);
                        Log.i("transactions",trans.toString());
                        /*
                        // Phone node is JSON Object
                        JSONObject phone = c.getJSONObject("phone");
                        String mobile = phone.getString("mobile");
                        String home = phone.getString("home");
                        String office = phone.getString("office");
                        */
                        // tmp hash map for single contact
                        HashMap<String, String> block = new HashMap<>();

                        // adding each child node to HashMap key => value
                        block.put("index", index);
                        block.put("prev_hash", prev_hash);
                        block.put("timestamp", time);


                        // adding contact to contact list

                        blockList.add(block);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }
            if(sf.isRefreshing())
                sf.setRefreshing(false);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
            ListAdapter adapter = new SimpleAdapter(
                    MainActivity.this, blockList,
                    R.layout.list_item, new String[]{"index", "prev_hash",
                    "timestamp"}, new int[]{R.id.index,
                    R.id.prev, R.id.time});

            lv.setAdapter(adapter);
        }
    }

    private class GetContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    //JSONArray contacts = jsonObj.getJSONArray("transactions");
                    JSONArray contacts = jsonObj.getJSONArray("cc");
                    System.out.print(contacts.toString());
                    Log.e(TAG, "Response from url: " + contacts.toString());
                    // looping through All Contacts
                    contactList.clear();
                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);

                        String name = c.getString("name");
                        String age = c.getString("age");
                        String aa = c.getString("aa");


                        /*
                        String id = c.getString("id");
                        String name = c.getString("name");
                        String email = c.getString("email");
                        String address = c.getString("address");
                        String gender = c.getString("gender");

                        // Phone node is JSON Object
                        JSONObject phone = c.getJSONObject("phone");
                        String mobile = phone.getString("mobile");
                        String home = phone.getString("home");
                        String office = phone.getString("office");
                        */
                        // tmp hash map for single contact
                        HashMap<String, String> contact = new HashMap<>();

                        // adding each child node to HashMap key => value
                        contact.put("name", name);
                        contact.put("age", age);
                        contact.put("aa", aa);

                        // adding contact to contact list

                        contactList.add(contact);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }
            if(sf.isRefreshing())
                sf.setRefreshing(false);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
            ListAdapter adapter = new SimpleAdapter(
                    MainActivity.this, contactList,
                    R.layout.list_item, new String[]{"name", "age",
                    "aa"}, new int[]{R.id.index,
                    R.id.prev, R.id.time});

            lv.setAdapter(adapter);
        }

    }
}


