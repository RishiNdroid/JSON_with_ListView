package com.example.rndroid.json_with_listview;


import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class Main_Contacts_Fragment extends Fragment {

    Cursor cursor;
    MyDatabase myDatabase;
    ArrayList<MyContacts> myContactsList;
    Button button;
    ListView listView;
    MyTask myTask;
    MyAdapter myAdapter;
    SimpleCursorAdapter cursorAdapter;

    public Main_Contacts_Fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myDatabase = new MyDatabase(getActivity());// passing context to Databae class constructor
        myDatabase.openDatabase();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_main__contacts_, container, false);
        button = (Button) v.findViewById(R.id.button);
        listView = (ListView) v.findViewById(R.id.listview);
        myTask = new MyTask();
        myContactsList = new ArrayList<MyContacts>();
        myAdapter = new MyAdapter();
        listView.setAdapter(myAdapter);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkInternetConnection() == true){
                if (myTask.getStatus() == AsyncTask.Status.RUNNING || myTask.getStatus() == AsyncTask.Status.FINISHED){
                    Toast.makeText(getActivity(), "Already Running", Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    myTask.execute("http://api.androidhive.info/contacts");
                }
                }else {
                    Toast.makeText(getActivity(), "No Interent", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return v;
    }

    public boolean checkInternetConnection(){
        ConnectivityManager manager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        if (networkInfo.isConnected() == false || networkInfo == null){
            return false;
        }
        else {return true;}
    }

    // 7a -  create AsyncTask Inner Class
    private class MyTask extends AsyncTask<String, Void, String>{

        String line;
        StringBuilder stringBuilder;

        @Override
        protected String doInBackground(String... strings) {

                try {
                    URL myUrl = new URL(strings[0]);
                    HttpURLConnection connection = (HttpURLConnection) myUrl.openConnection();
                    InputStream inputStream = connection.getInputStream();
                    InputStreamReader streamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(streamReader);
                    stringBuilder = new StringBuilder("");
                    line = bufferedReader.readLine();
                    while (line != null){
                        stringBuilder.append(line);
                        line = bufferedReader.readLine();
                    }
                    return stringBuilder.toString();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (SecurityException e){
                    e.printStackTrace();
                }
            return "Something Went wrong";
        }

        @Override
        protected void onPostExecute(String s) {
            String contacts = s;
            //reverse json parsing
            try {
                JSONObject jsonObjectContacts = new JSONObject(s);
                JSONArray jsonArrayContacts = jsonObjectContacts.getJSONArray("contacts");
                for (int i = 0; i< jsonArrayContacts.length(); i++){
                    JSONObject jsonObject = jsonArrayContacts.getJSONObject(i);
                    MyContacts myContacts = new MyContacts();
                    myContacts.setName(jsonObject.getString("name"));
                    myContacts.setEmail(jsonObject.getString("email"));

                    JSONObject jsonObjectPhone = jsonObject.getJSONObject("phone");
                    myContacts.setPhone(jsonObjectPhone.getString("mobile"));

                    myContacts.setSno(i+1);
                    myContactsList.add(myContacts);
                }
                myAdapter.notifyDataSetChanged();

            } catch (JSONException e) {
                e.printStackTrace();
            }
            super.onPostExecute(s);
        }
    }

    private class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return myContactsList.size();
        }

        @Override
        public Object getItem(int i) {
            return myContactsList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            // read data from arraylist based on position
            MyContacts m = myContactsList.get(i);
            View v = getActivity().getLayoutInflater().inflate(R.layout.row, viewGroup, false);
            TextView textViewId = (TextView) v.findViewById(R.id.textViewSno);
            TextView textViewName = (TextView) v.findViewById(R.id.textViewName);
            TextView textViewEmail = (TextView) v.findViewById(R.id.textViewEmail);
            TextView textViewPhone = (TextView) v.findViewById(R.id.textViewPhone);

            textViewName.setText(m.getName());
            textViewEmail.setText(m.getEmail());
            textViewPhone.setText(m.getPhone());
            textViewId.setText(""+m.getSno());
            return v;
        }
    }
}
