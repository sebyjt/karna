package in.iodev.karna;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
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
    public ImageView im;
    private JSONObject items=new JSONObject();

    public dialog( Context context,String percent) {
        super(context);this.percent=percent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_dialog);
        preferences=getDefaultSharedPreferences(getContext());
        seektext=findViewById(R.id.seektext);
        seekBar=findViewById(R.id.seekBar);
        im=findViewById(R.id.imsub);

        im.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url="https://6ghfrrqsb3.execute-api.ap-south-1.amazonaws.com/Dev/userdetails/setpercentage";

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
                dialog.this.cancel();

                            }



            } catch (JSONException e) {
                e.printStackTrace();
            }    }


    }
}


