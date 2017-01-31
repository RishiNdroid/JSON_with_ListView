package com.example.rndroid.json_with_listview;


import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
public class List_Fragment extends Fragment {

    MyDatabase myDatabase;
    MyContacts myContacts;
    Button button;
    RecyclerView recyclerView;
    MyTask myTask;
    MyRecyclerViewAdapter myRecyclerViewAdapter;
    ArrayList<MyContacts> myContactsArrayList;

    public List_Fragment() {
        // Required empty public constructor
    }

    public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.MyViewHolder>{
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getActivity().getLayoutInflater().inflate(R.layout.row, parent, false);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            myContacts = myContactsArrayList.get(position);
            holder.tv1.setText(""+myContacts.getSno());
            holder.tv2.setText(myContacts.getName());
            holder.tv3.setText(myContacts.getEmail());
            holder.tv4.setText(myContacts.getPhone());
        }

        @Override
        public int getItemCount() {
            return myContactsArrayList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder{
                public TextView tv1,tv2, tv3, tv4;
                public MyViewHolder(View itemView) {
                    super(itemView);
                    tv1 = (TextView) itemView.findViewById(R.id.textViewSno);
                    tv2 = (TextView) itemView.findViewById(R.id.textViewName);
                    tv3 = (TextView) itemView.findViewById(R.id.textViewEmail);
                    tv4 = (TextView) itemView.findViewById(R.id.textViewPhone);
            }
        }
    }

    public class MyTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            try {
                URL myUrl = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) myUrl.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String line = bufferedReader.readLine();
                StringBuilder resultStringBuilder = new StringBuilder("");
                while(line != null){
                    resultStringBuilder.append(line);
                    line = bufferedReader.readLine();
                }
                return resultStringBuilder.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            String resultJSON = s;
            try {
                JSONObject jsonObjectRoot = new JSONObject(resultJSON);
                JSONArray jsonArrayContacts = jsonObjectRoot.getJSONArray("contacts");

                for (int i = 0; i< jsonArrayContacts.length(); i++){
                    JSONObject jsonObjectDetails = jsonArrayContacts.getJSONObject(i);
                    JSONObject jsonObjectPhone = jsonObjectDetails.getJSONObject("phone");
                    myDatabase.insertContact(jsonObjectDetails.getString("name"), jsonObjectDetails.getString("email"),
                            jsonObjectPhone.getString("mobile"));
                }
                setDataToContacts();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void setDataToContacts(){
        Cursor cursor = myDatabase.getContacts();
        if(cursor != null){
            while (cursor.moveToNext()){
                myContacts = new MyContacts();
                myContacts.setSno(cursor.getInt(cursor.getColumnIndex("_id")));
                myContacts.setName(cursor.getString(cursor.getColumnIndex("name")));
                myContacts.setEmail(cursor.getString(cursor.getColumnIndex("email")));
                myContacts.setPhone(cursor.getString(cursor.getColumnIndex("phone")));
                myContactsArrayList.add(myContacts);
            }
        }else Toast.makeText(getActivity(), "Cursor Null", Toast.LENGTH_SHORT).show();
        myRecyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myDatabase = new MyDatabase(getActivity());
        myDatabase.openDatabase();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list_, container, false);
        // initialize all variables
        button = (Button) v.findViewById(R.id.button_database);
        recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view_database);
        myContactsArrayList = new ArrayList<MyContacts>();
        myTask = new MyTask();
        myRecyclerViewAdapter = new MyRecyclerViewAdapter();
        // set adapter to recyclerview and setup layout manager
        recyclerView.setAdapter(myRecyclerViewAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkInternetConnection()){
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

    @Override
    public void onDestroy() {
        myDatabase.close();
        super.onDestroy();
    }

    public boolean checkInternetConnection(){
        ConnectivityManager manager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        if (networkInfo.isConnected() == false || networkInfo == null){
            return false;
        }
        else {return true;}
    }

}
