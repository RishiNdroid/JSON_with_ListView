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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
public class Fragment_two_recycler_View extends Fragment {

    RecyclerView recyclerView;
    MyRecyclerViewAdapter recyclerViewAdapter;
    ArrayList<MyContacts> myContactsList;
    Button button;
    MyTask myTask;

    public Fragment_two_recycler_View() {
        // Required empty public constructor
    }

    public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder>{

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = getActivity().getLayoutInflater().inflate(R.layout.row, parent, false);
            ViewHolder viewHolder = new ViewHolder(v);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
        MyContacts myContacts = myContactsList.get(position);
            holder.tv1.setText(""+myContacts.getSno());
            holder.tv2.setText(myContacts.getName());
            holder.tv3.setText(myContacts.getEmail());
            holder.tv4.setText(myContacts.getPhone());
        }

        @Override
        public int getItemCount() {
            return myContactsList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView tv1,tv2, tv3, tv4;
        public ViewHolder(View itemView) {
            super(itemView);
            tv1 = (TextView) itemView.findViewById(R.id.textViewSno);
            tv2 = (TextView) itemView.findViewById(R.id.textViewName);
            tv3 = (TextView) itemView.findViewById(R.id.textViewEmail);
            tv4 = (TextView) itemView.findViewById(R.id.textViewPhone);
        }
    }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_fragment_two_recycler__view, container, false);
        button = (Button) v.findViewById(R.id.buttonGET_DATA);
        recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view1);
        recyclerViewAdapter = new MyRecyclerViewAdapter();
        myTask = new MyTask();
        myContactsList = new ArrayList<MyContacts>();
        recyclerView.setAdapter(recyclerViewAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

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
                Log.d("B34", "Malformed");
                e.printStackTrace();
            } catch (IOException e) {
                Log.d("B34", "IO");
                e.printStackTrace();
            } catch (SecurityException e){
                Log.d("B34", "Security");
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
                recyclerViewAdapter.notifyDataSetChanged();

            } catch (JSONException e) {
                Log.d("B34", "JSON");
                e.printStackTrace();
            }
            super.onPostExecute(s);
        }
    }
}
