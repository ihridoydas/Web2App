package com.example.web2app;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import hotchemi.android.rate.AppRate;

public class MainActivity extends AppCompatActivity {



    private WebView webView;
    private ProgressBar progressBar;
    private FrameLayout frameLayout;


    //firebase
    private TextView url;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference reference = firebaseDatabase.getReference();
    private DatabaseReference childRefernce = reference.child("url");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_main);

        //App rate

        AppRate.with(this)
                .setInstallDays(1)
                .setLaunchTimes(3)
                .setRemindInterval(2)
                .monitor();


        //Internet connection or Not Chacking

        if(!amIConnected()) buildDialog(MainActivity.this).show();
        else {
            setContentView(R.layout.activity_main);
            AppRate.showRateDialogIfMeetsConditions(this);
        }


        //firebase
        FirebaseApp.initializeApp(this);
        url=findViewById(R.id.msguri);
        // Write a message to the database
        childRefernce.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String message = dataSnapshot.getValue(String.class);
                url.setText(message);
                webView.loadUrl(message);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),"Sorry...You have a some problem!!",Toast.LENGTH_SHORT).show();

            }
        });

        //End Firebase




        //Start This is connect to xml file
        webView = findViewById(R.id.webviewId);
        frameLayout =findViewById(R.id.frameLayoutId);
        progressBar=findViewById(R.id.progressBar);
        progressBar.setMax(100);
        webView.setWebViewClient(new HelpClient());
        WebSettings webSettings =webView.getSettings();
        webSettings.setJavaScriptEnabled(true);



        webView.setVerticalScrollBarEnabled(false);
        progressBar.setProgress(0);

        //END  This is connect to xml file


        //Start This is connect to WEBCrome file

       webView.setWebChromeClient(new WebChromeClient(){

           public void onProgressChanged(WebView view,int progress){

               frameLayout.setVisibility(View.VISIBLE);
               progressBar.setProgress(progress);
               setTitle("Loading...");

               if(progress==100){

                   frameLayout.setVisibility(View.GONE);
                   setTitle(view.getTitle());
               }
               super.onProgressChanged(view,progress);
           }
       });

        //END This is connect to WEBCrome file

    }

    //Start This is connect to onBackPressed

    @Override
    public void onBackPressed() {
        if(webView.canGoBack()){
            webView.goBack();
        }else{

            AlertDialog.Builder builder =new AlertDialog.Builder(this);
            builder.setTitle(R.string.exit_name);
            builder.setMessage(R.string.exit);

            builder.setPositiveButton("EXIT", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });

            builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            AlertDialog dialog = builder.show();
        }

    }

    //End This is connect to onBackPressed

    //Start This is connect to WebViewClient

    private class HelpClient extends WebViewClient {

        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

            webView.loadUrl("file:///android_asset/error.html");

        }

        public boolean shouldOverrideUrlLoading(WebView view,String url){

            view.loadUrl(url);

            frameLayout.setVisibility(View.VISIBLE);
            return true;
        }

    }



    //End This is connect to WebViewClient



    //Start This is connect to KeyPressed Integration

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode==KeyEvent.KEYCODE_BACK){

            if(webView.canGoBack()){
                webView.goBack();
                return true;

            }
        }
        return super.onKeyDown(keyCode, event);
    }

    //END This is connect to KeyPressed Integration

    //Start This is onBackpressed Conformation to KeyPressed Integration




    //End This is onBackpressed Conformation to KeyPressed Integration




    private boolean amIConnected(){

        ConnectivityManager connectivityManager=(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo =connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public AlertDialog.Builder buildDialog(Context c) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("No Internet Connection");
        builder.setMessage("You need to have Mobile Data or wifi to access this. Press ok to Exit");

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                finish();
            }
        });

        return builder;
    }



}
