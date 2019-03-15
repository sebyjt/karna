package in.iodev.karna;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class dialog  extends Dialog {

    public SeekBar seekBar;
    TextView seektext;
    String user,percent;
    SharedPreferences preferences;
    public CardView im;
    Context context;
    private JSONObject items=new JSONObject();

    public dialog( Context context,String percent) {
        super(context);this.percent=percent;
        this.context=context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_dialog);
        preferences=getDefaultSharedPreferences(getContext());
        seektext=findViewById(R.id.seektext);
        seekBar=findViewById(R.id.seekBar);
        im=findViewById(R.id.img);

        im.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url="https://9nvv7wpamb.execute-api.ap-southeast-1.amazonaws.com/Development/update-percentage";

                new HTTPAsyncTask2().execute(url,items.toString());
            }
        });
        seekBar.setProgress(Integer.parseInt(percent));
        seektext.setText(percent+"%");
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // TODO Auto-generated method stub
                seektext.setText(String.valueOf(progress));

                user=preferences.getString("user","");
                percent= String.valueOf(progress);
                try {
                    items.put("Username",preferences.getString("user",""));
                    items.put("Percentage",String.valueOf(progress));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }
        });
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
                if(responseObject.getString("Username").equals(user))
                {preferences.edit().putString(user,percent).apply();
                    Intent serviceIntent = new Intent(context, ExampleService.class);


                    ContextCompat.startForegroundService(context, serviceIntent);
                dialog.this.cancel();

                            }



            } catch (JSONException e) {
                e.printStackTrace();
            }    }


    }
}


