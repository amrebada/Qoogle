package com.roic.amr_ebada.qoogle;

import android.app.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Message;
import android.os.Handler;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;



public class MainScreen extends Activity implements View.OnClickListener {


    public static final String RESULT_KEYS = "resultformmain";
    public static final String QUERY_KEY = "queryfrommain";


    private EditText query;
    private Button search, about, help;


    private String Title;
    private String Info;
    private String data;
    private JsonHandler queryInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        //Initialization all components
        init();
    }

    private void init() {
        query = (EditText) findViewById(R.id.query);
        search = (Button) findViewById(R.id.search);
        about = (Button) findViewById(R.id.aboutBt);
        help = (Button) findViewById(R.id.helpBt);

        search.setOnClickListener(this);
        about.setOnClickListener(this);
        help.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.search:
                // Search About Ayat In Query EditText
                ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo active = conMgr.getActiveNetworkInfo();
                if (active != null && active.isConnected()) {
                    try {
                        search();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(this, "Please,Turn ON the Internet", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.aboutBt:
                // Show About Dialogue
                show(1);
                break;
            case R.id.helpBt:
                // Show About Dialogue
                show(2);
                break;
        }

    }

    private void show(int i) {
        Title = "";
        Info = "";
        switch (i) {
            case 1:
                Title = "About";
                Info = ">My name is Amr Ebada .\n" +
                        "I'm software engineer.\n" +
                        "I know Java EE ,Android ,PHP , FrontEnd web programming.\n" +
                        "Contacts:\n" +
                        "   Email -> amr.app.engine@gmail.com\n" +
                        "   fb.com/eng.amr.oseb";
                break;
            case 2:
                Title = "Help!!";
                Info = "Qoogle is an Quraan Search Engine." + "\n" +
                        "enter words in search bar and hit Search Button ,and enjoy with result";
                break;
        }
        new AlertDialog.Builder(this).setTitle(Title).setMessage(Info).setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        }).show();

    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            String result = bundle.getString("myKey");
            Toast.makeText(MainScreen.this,result,Toast.LENGTH_SHORT).show();
            search.setEnabled(true);
            search.setBackgroundColor(Color.parseColor("#ff9cff87"));
            search.setText("Search");
        }
    };
    private void search() throws UnsupportedEncodingException, JSONException {


        Thread exe = new Thread() {
            @Override
            public void run() {
                super.run();
                queryInfo = new JsonHandler();
                String input = null;
                try {
                    input = queryInfo.readHTTP(query.getText().toString(), 0, false);
                    JSONObject root = new JSONObject(input);
                    if (queryInfo.isFounded(root)) {
                        int ayasInfo[] = queryInfo.loadInfo(root);

                        Intent intent = new Intent(MainScreen.this, ResultScreen.class);
                        intent.putExtra(RESULT_KEYS, ayasInfo);
                        intent.putExtra(QUERY_KEY, query.getText().toString());
                        startActivity(intent);
                    } else {

                        Message msg = handler.obtainMessage();
                        Bundle bundle = new Bundle();
                        bundle.putString("myKey", "Not Found");
                        msg.setData(bundle);
                        handler.sendMessage(msg);
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };
        search.setEnabled(false);
        search.setBackgroundColor(Color.RED);
        search.setText("Loading");
        exe.start();

    }


    @Override
    protected void onPause() {
        super.onPause();
        search.setEnabled(true);
        search.setBackgroundColor(Color.parseColor("#ff9cff87"));
        search.setText("Search");
    }
}
