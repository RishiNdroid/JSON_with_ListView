package com.example.rndroid.json_with_listview;

import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
///*
//* 1- take bellow permittions in manifest -
//*       ACCESS_NETWORK_STATE AND INTERNET
//* 2 -
//* 6 -
//* 7a - go to fragment java file , create an inner class for AsyncTask - public class MyTask extends AsyncTask<String, void, String>{
//*     override doInBackGround() and onPost() }
//* 7b - create a inner class for custom adapter in fragment java ;
//* 8 - declare required variables in fragment java file above constructor.
//* 9 - initialize all variable in oncreate and prepare one button click listner - button, lstview, ArratList<Contacts> al, MyAdapter m, MyTask myTask;
//* View v = inflate....
//* but, list, = findbyid
//* al = new ArrayList<Contacts>
//*     list.setAdapter(m);
//*     myTask = new MyTask();
//*     but.setOnClick...{  }
//* 10 - write a method check internet connection in main fragment.
//* 11 - go to but.onClick, call above method and if internet available then Start async Task, by passing "http://api.androidhive.info/contacts"
//* 12 - Now go to AsyncTask
//*   a - implement - doInBackground()- for connecting to server
//*   b - implement onPostExecute() - for reverse JSON parsing,
//*                                   pass data to arraylist<Contacts>
//*                                   notify adapter
//* 13 - implement custom adapter life cycle methods.
//*   getCount()
//*   getItem()
//*   getItemID()
//*   getView() - important method - (a)read data from araylist based on position
//*                                  (b)(GETTER)
//*                                  (c) load row.xml and load all views
//*                                  (d) fill data on to above views
//* */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Main_Contacts_Fragment contacts_fragment = new Main_Contacts_Fragment();
//        Fragment_two_recycler_View fragmentTwoRecyclerView = new Fragment_two_recycler_View();
        List_Fragment list_fragment = new List_Fragment();
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().add(R.id.container1, list_fragment).commit();
    }
}
