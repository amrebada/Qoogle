package com.roic.amr_ebada.qoogle;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;


public class ResultScreen extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private TextView pages, total, pageNum;
    private Button next, prev;
    private ListView result;


    private int[] extras = new int[4];
    private int nb_page, start, end, totalInt;
    private int CurrentPage = 1;
    private String query;
    private ArrayList<String> TextList, MP3List;
    private ArrayAdapter<String> adapter;
    private MediaPlayer player;

    private JsonHandler jsonHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_screen);

        //declaration
        jsonHandler = new JsonHandler();

        //Initialization
        try {
            init();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void init() throws UnsupportedEncodingException, JSONException {
        // receive Info about Query
        Intent intent = getIntent();
        extras = intent.getIntArrayExtra(MainScreen.RESULT_KEYS);
        query = intent.getStringExtra(MainScreen.QUERY_KEY);
        // text.setText("nb_page:"+extras[0]+"\n"+"start:"+extras[1]+"\n"+"end:"+extras[2]+"\n"+"total:"+extras[3]);
        nb_page = extras[0];
        start = extras[1];
        end = extras[2];
        totalInt = extras[3];

        //define UI Component
        pages = (TextView) findViewById(R.id.pages);
        total = (TextView) findViewById(R.id.total);
        pageNum = (TextView) findViewById(R.id.pageNum);
        next = (Button) findViewById(R.id.nextBt);
        prev = (Button) findViewById(R.id.prevBt);
        result = (ListView) findViewById(R.id.resultList);

        next.setOnClickListener(this);
        prev.setOnClickListener(this);

        prev.setEnabled(false);
        prev.setBackgroundColor(Color.parseColor("#FFEEEEEE"));

        if (nb_page == 1) {
            next.setEnabled(false);
            next.setBackgroundColor(Color.parseColor("#FFEEEEEE"));
        }

        pages.setText("Pages:" + nb_page);
        total.setText("Total Result:" + totalInt);
        pageNum.setText("Page Number:" + CurrentPage);


        TextList = new ArrayList<String>();
        MP3List = new ArrayList<String>();

        TextList.add("Loading...");
        TextList.add("Loading...");
        //set List View Items

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, TextList);
        result.setAdapter(adapter);
        result.setOnItemClickListener(this);
        result.setAlpha(0.3f);


        refresh();

        player = new MediaPlayer();
    }

    private void refresh() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //Get first page Item
                try {
                    loadPage(CurrentPage);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                result.post(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();

                    }
                });
                pageNum.post(new Runnable() {
                    @Override
                    public void run() {
                        pageNum.setText("Page Number:" + CurrentPage);
                    }
                });
            }
        }).start();
    }

    private void loadPage(int current) throws UnsupportedEncodingException, JSONException {

        TextList.clear();
        MP3List.clear();

        String input = jsonHandler.readHTTP(query, current, true);
        JSONObject json = new JSONObject(input);
        int[] cureentresult = jsonHandler.loadInfo(json);
        for (int i = cureentresult[1]; i <= cureentresult[2]; i++) {
            TextList.add(jsonHandler.loadAya(i, json));
            MP3List.add(jsonHandler.loadMP3(i, json));
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.nextBt:

                CurrentPage++;
                pageNum.setText("Page Number: Waiting ....");
                refresh();
                if (CurrentPage == nb_page) {
                    next.setEnabled(false);
                    next.setBackgroundColor(Color.parseColor("#FFEEEEEE"));
                }
                prev.setEnabled(true);
                prev.setBackgroundColor(Color.rgb(0, 0, 0));
                break;
            case R.id.prevBt:
                CurrentPage--;
                pageNum.setText("Page Number: Waiting ....");
                refresh();
                if (CurrentPage == 1) {
                    prev.setEnabled(false);
                    prev.setBackgroundColor(Color.parseColor("#FFEEEEEE"));
                }
                next.setEnabled(true);
                next.setBackgroundColor(Color.rgb(0, 0, 0));
                break;
        }
    }

    int prevItem = 11;

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Toast.makeText(this, "Preparing .....", Toast.LENGTH_LONG).show();
        if (player.isPlaying()) {
            player.pause();
        }
        if (i != prevItem) {

            try {

                player = new MediaPlayer();
                player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                player.setDataSource(MP3List.get(i));
                player.prepare();
                player.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
            prevItem = i;
        }
    }
}
