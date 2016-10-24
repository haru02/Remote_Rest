package com.example.froze.remote_rest;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    Button btn;
    CustomAdapter adapter;
    ArrayList<String> datas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView)findViewById(R.id.listView);
        adapter = new CustomAdapter(this);
        listView.setAdapter(adapter);

        btn = (Button)findViewById(R.id.btnGetList);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callOpenApi();
            }
        });

    }

    public void callOpenApi(){
        new AsyncTask<Void, Void, String>(){
            ProgressDialog progress;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progress = new ProgressDialog(MainActivity.this);
                progress.setTitle("다운로드");
                progress.setMessage("downloading");
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progress.setCancelable(false);
                progress.show();
            }

            @Override
            protected String doInBackground(Void... params) {
                String result = "";
                try {
                    result = Remote.getData("http://172.30.1.9/sub/request.jsp");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return result;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                try {
                    JSONObject json = new JSONObject(s);
                    JSONArray rows = json.getJSONArray("root");
                    int rows_count = rows.length();
                    for (int i = 0; i < rows_count; i++) {
                        JSONObject result = (JSONObject) rows.get(i);
                        String value = result.getString("key");
                        datas.add(value);
                    }
                    adapter.notifyDataSetChanged();
                }catch(Exception e){
                    e.printStackTrace();
                }
                progress.dismiss();
            }
        }.execute();
    }


    public class CustomAdapter extends BaseAdapter{

        LayoutInflater inflater;

        public CustomAdapter(Context context){
            inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public Object getItem(int position) {
            return datas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if(convertView == null){
                convertView = inflater.inflate(R.layout.listview_item, null);
            }
            TextView tv = (TextView)convertView.findViewById(R.id.textView);
            tv.setText(datas.get(position));

            return convertView;
        }
    }


}
