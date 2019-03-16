package in.iodev.karna;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;


public class FloatingService extends Service {

    private WindowManager mWindowManager;
    private View mFloatingView;
    private Button PauseButton,PlayButton;
    private boolean AdsOn=true;
    private ImageView BannerImage;
    SharedPreferences preferences;

    public FloatingService() {
    }

    //Test
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //Inflate the floating view layout we created
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.layout_floating_widget, null);
        preferences=getDefaultSharedPreferences(getApplicationContext());
        //Add the view to the window.
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        //Specify the view position
        params.gravity = Gravity.TOP | Gravity.LEFT;        //Initially view will be added to top-left corner
        params.x = 0;
        params.y = 100;

        //Add the view to the window
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mFloatingView, params);
        PlayButton=mFloatingView.findViewById(R.id.playbtn);
        PauseButton=mFloatingView.findViewById(R.id.pausebtn);
        BannerImage=mFloatingView.findViewById(R.id.bannerImage);
        PauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdsOn=false;
                BannerImage.setVisibility(View.GONE);
                PauseButton.setVisibility(View.GONE);
                PlayButton.setVisibility(View.VISIBLE);
            }
        });
        PlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdsOn=true;
                BannerImage.setVisibility(View.VISIBLE);
                PauseButton.setVisibility(View.VISIBLE);
                PlayButton.setVisibility(View.GONE);
            }
        });
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        if(AdsOn){
                            JSONObject objects=new JSONObject();
                            try{
                                objects.put("Username",preferences.getString("user",""));
                            }catch (Exception e){e.printStackTrace();}
                            Log.d("Timer","Works");
                            String url="https://9nvv7wpamb.execute-api.ap-southeast-1.amazonaws.com/Development/get-ad";

                            new AdCall().execute(url,objects.toString());
                        }
                    }
                },
                5000, 5000);
//        while(AdsOn) {
//            Handler handler = new Handler();
//            handler.postDelayed(new Runnable() {
//                public void run() {
//                    if (AdsOn) {
//                        adRequest = new AdRequest.Builder().build();
//                        mAdView.loadAd(adRequest);
//                    }
//                }
//            }, 2000);
//        }

        //The root element of the collapsed view layout
//        final View collapsedView = mFloatingView.findViewById(R.id.collapse_view);
//        //The root element of the expanded view layout
//        final View expandedView = mFloatingView.findViewById(R.id.expanded_container);


        //Set the close button
//        ImageView closeButtonCollapsed = (ImageView) mFloatingView.findViewById(R.id.close_btn);
//        closeButtonCollapsed.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //close the service and remove the from from the window
//                stopSelf();
//            }
//        });
//
//        //Set the view while floating view is expanded.
//        //Set the play button.
//        ImageView playButton = (ImageView) mFloatingView.findViewById(R.id.play_btn);
//        playButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(FloatingService.this, "Playing the song.", Toast.LENGTH_LONG).show();
//            }
//        });

//        //Set the next button.
//        ImageView nextButton = (ImageView) mFloatingView.findViewById(R.id.next_btn);
//        nextButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(FloatingService.this, "Playing next song.", Toast.LENGTH_LONG).show();
//            }
//        });
//
//        //Set the pause button.
//        ImageView prevButton = (ImageView) mFloatingView.findViewById(R.id.prev_btn);
//        prevButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(FloatingService.this, "Playing previous song.", Toast.LENGTH_LONG).show();
//            }
//        });

        //Set the close button
//        ImageView closeButton = (ImageView) mFloatingView.findViewById(R.id.close_button);
//        closeButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                collapsedView.setVisibility(View.VISIBLE);
//                expandedView.setVisibility(View.GONE);
//            }
//        });
//
//        //Open the application on thi button click
//        ImageView openButton = (ImageView) mFloatingView.findViewById(R.id.open_button);
//        openButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //Open the application  click.
//                Intent intent = new Intent(FloatingService.this, MainActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
//
//                //close the service and remove view from the view hierarchy
//                stopSelf();
//            }
//        });
//
        //Drag and move floating view using user's touch action.
        mFloatingView.findViewById(R.id.root_container).setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        //remember the initial position.
                        initialX = params.x;
                        initialY = params.y;

                        //get the touch location
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        int Xdiff = (int) (event.getRawX() - initialTouchX);
                        int Ydiff = (int) (event.getRawY() - initialTouchY);

                        //The check for Xdiff <10 && YDiff< 10 because sometime elements moves a little while clicking.
                        //So that is click event.
//                        if (Xdiff < 10 && Ydiff < 10) {
//                            if (isViewCollapsed()) {
//                                //When user clicks on the image view of the collapsed layout,
//                                //visibility of the collapsed layout will be changed to "View.GONE"
//                                //and expanded view will become visible.
//                                collapsedView.setVisibility(View.GONE);
//                                expandedView.setVisibility(View.VISIBLE);
//                            }
//                        }
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        //Calculate the X and Y coordinates of the view.
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);

                        //Update the layout with new X & Y coordinate
                        mWindowManager.updateViewLayout(mFloatingView, params);
                        return true;
                }
                return false;
            }
        });
//    }
//
//    /**
//     * Detect if the floating view is collapsed or expanded.
//     *
//     * @return true if the floating view is collapsed.
//     */
//    private boolean isViewCollapsed() {
//        return mFloatingView == null || mFloatingView.findViewById(R.id.collapse_view).getVisibility() == View.VISIBLE;
//    }
//
    }
    class AdCall extends AsyncTask<String, Void, String> {
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
                URL url = new URL(responseObject.getString("ImageURL"));

                Picasso.get()
                        .load(responseObject.getString("ImageURL"))
                        .into(BannerImage);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }
}
