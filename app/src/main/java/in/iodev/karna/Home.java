package in.iodev.karna;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class Home extends AppCompatActivity {
    RecyclerView ngolist;
    JSONArray array;
    JSONObject object2;
    Recycleadapter adapter;
    boolean running=false;
    Button start;
    String percent,user;
    SharedPreferences preferences;
    TextView ads,gain,generate,donate,name,percentage;
    private static final int IO_BUFFER_SIZE = 4 * 1024;
    private static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ngolist=findViewById(R.id.ngolist);
        start=findViewById(R.id.start);
        ngolist.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {

            //If the draw over permission is not available open the settings screen
            //to grant the permission.
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);
        }

        preferences=getDefaultSharedPreferences(getApplicationContext());

        percent="75";
        running=preferences.getBoolean("running",false);
        if(running)
        {
            start.setText("stop");
        }
        else
            start.setText("start");
        user=preferences.getString("user","");
        ads=findViewById(R.id.adsviewed);
        gain=findViewById(R.id.gained);
        donate=findViewById(R.id.donated);
        generate=findViewById(R.id.generate);
        name=findViewById(R.id.username);
        percentage=findViewById(R.id.percent);
        if(preferences.getBoolean("firstsignin",false))
        {
            percent="25";
            preferences.edit().putString("user","molly").apply();
            JSONObject items=new JSONObject();
            try {
                items.put("Username",preferences.getString("user",""));
                items.put("DisplayName",preferences.getString("DisplayName",""));
                items.put("Percentage",String.valueOf(percent));
                String url="https://9nvv7wpamb.execute-api.ap-southeast-1.amazonaws.com/Development/update-percentage";

                new HTTPAsyncTask().execute(url,items.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else
        {
            JSONObject object2=new JSONObject();
            try {

                object2.put("Username",preferences.getString("user",""));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            new HTTPAsyncTask3().execute("https://9nvv7wpamb.execute-api.ap-southeast-1.amazonaws.com/Development/get-userdetails",object2.toString());
        }
        adapter=new Recycleadapter();
        JSONObject object=new JSONObject();

        try {
            object.put("","");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new HTTPAsyncTask2().execute("https://6ghfrrqsb3.execute-api.ap-south-1.amazonaws.com/Dev/ngo/list",object.toString());
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(running==false)
                {
                    Log.d("login",percent);
                    dialog dialogBox = new dialog(Home.this,percent);
                dialogBox.show();

                //Adding width and blur
                Window window=dialogBox.getWindow();
                WindowManager.LayoutParams lp = dialogBox.getWindow().getAttributes();
                lp.dimAmount=0.8f;
                dialogBox.getWindow().setAttributes(lp);
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialogBox.setCanceledOnTouchOutside(false);
                dialogBox.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        start.setText("Stop");
                        running=true;
                        preferences.edit().putBoolean("running",true).apply();
                        JSONObject object2=new JSONObject();
                        try {

                            object2.put("Username",preferences.getString("user",""));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        new HTTPAsyncTask3().execute("https://9nvv7wpamb.execute-api.ap-southeast-1.amazonaws.com/Development/get-userdetails",object2.toString());
                    }
                });
        }
        else {
            stop(v);
            running=false;
                    preferences.edit().putBoolean("running",false).apply();
            start.setText("start");

                }
            }
        });


    }

    public void stop(View view) {
        Intent serviceIntent = new Intent(this, ExampleService.class);
        Intent floatintent = new Intent(this, FloatingService.class);



        stopService(serviceIntent);
        stopService(floatintent);
    }

    class HTTPAsyncTask2 extends AsyncTask<String, Void, String> {
        String response="Network Error";

        @Override
        protected String doInBackground(String... urls) {
            // params comes from the execute() call: params[0] is the url.

            try {
                response= HTTPPostGet.getJsonResponse(urls[0],urls[1]);
                Log.i("response",response.toString());
                return response;
            } catch (Exception e) {
                e.printStackTrace();
                return "Error!";
            }
            finally {

            }

        }
        @Override
        protected void onPreExecute() {

        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            JSONObject responseObject;
            try {
                responseObject = new JSONObject(response);
                array=responseObject.getJSONArray("Data");
                Log.i("sample", String.valueOf(array.length()));
                ngolist.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            } catch (JSONException e) {
                e.printStackTrace();
            }    }


    }


    public class Recycleadapter extends RecyclerView.Adapter<Recycleadapter.SimpleViewHolder> {


        public Recycleadapter() {


        }

        @Override
        public Recycleadapter.SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ngo_card, parent, false);
            return new SimpleViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final Recycleadapter.SimpleViewHolder holder, final int position) {
            try {

                JSONObject object = null;
                try {
                    object = array.getJSONObject(position);
                    Picasso.get().load(object.getString("ImageURL")).into(holder.im);

                    holder.name.setText(object.getString("Name"));
                    holder.location.setText(object.getString("Location"));
                    holder.cause.setText(object.getString("Cause"));
                    holder.description.setText(object.getString("Description"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
            }


        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public int getItemCount() {
            return array.length();
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        public class SimpleViewHolder extends RecyclerView.ViewHolder {
            ImageView im;
            TextView name,location,cause,description;
            CheckBox check;


            public SimpleViewHolder(View v) {

                super(v);

                im=findViewById(R.id.avatar);
                name=findViewById(R.id.name);
                location=findViewById(R.id.location);
                cause=findViewById(R.id.cause);
                description=findViewById(R.id.description);
                check=findViewById(R.id.checkbox);
     }
        }}


    public  class HTTPAsyncTask3 extends AsyncTask<String, Void, String> {
        String response="Network Error";

        @Override
        protected String doInBackground(String... urls) {
            // params comes from the execute() call: params[0] is the url.

            try {
                response= HTTPPostGet.getJsonResponse(urls[0],urls[1]);
                Log.i("response",response.toString());
                return response;
            } catch (Exception e) {
                e.printStackTrace();
                return "Error!";
            }
            finally {

            }

        }
        @Override
        protected void onPreExecute() {

        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            JSONObject responseObject;
            try {
                responseObject = new JSONObject(response);
                object2=responseObject.getJSONObject("Data");
                percent=object2.getString("Percentage");
                percentage.setText(percent);
                name.setText(object2.getString("DisplayName"));
                ads.setText(object2.getString("AdsViewed"));
                gain.setText(object2.getString("MoneyGained"));
                donate.setText(object2.getString("MoneyDonated"));
                generate.setText(object2.getString("MoneyGenerated"));




            } catch (JSONException e) {
                e.printStackTrace();
            }    }


    }
    class HTTPAsyncTask extends AsyncTask<String, Void, String> {
        String response="Network Error";

        @Override
        protected String doInBackground(String... urls) {
            // params comes from the execute() call: params[0] is the url.

            try {
                response= HTTPPostGet.getJsonResponse(urls[0],urls[1]);
                Log.i("response",response.toString());
                return response;
            } catch (Exception e) {
                e.printStackTrace();
                return "Error!";
            }
            finally {

            }

        }
        @Override
        protected void onPreExecute() {

        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            JSONObject responseObject;
            try {
                responseObject = new JSONObject(response);
                if(responseObject.getString("Username").equals(user))
                {preferences.edit().putString(user,percent).apply();
                    object2=responseObject;
                    percent=object2.getString("Percentage");
                    percentage.setText(percent);
                    name.setText(object2.getString("DisplayName"));
                    ads.setText(object2.getString("AdsViewed"));
                    gain.setText(object2.getString("MoneyGained"));
                    donate.setText(object2.getString("MoneyDonated"));
                    generate.setText(object2.getString("MoneyGenerated"));


                }



            } catch (JSONException e) {
                e.printStackTrace();
            }    }


    }
}