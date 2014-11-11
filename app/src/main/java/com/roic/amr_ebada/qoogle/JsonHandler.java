package com.roic.amr_ebada.qoogle;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by amr-ebada on 05/11/2014.
 */
public class JsonHandler {

    private StringBuilder AyaParsing, allAyas;
    private String query;

    public void setQuery(String query) {
        this.query = query;
    }

    public String readHTTP(String queryWord, int page, boolean isMorePages) throws UnsupportedEncodingException {


        StringBuilder builder = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet;
        if (isMorePages) {
            httpGet = new HttpGet("http://www.alfanous.org/jos2?query=" + URLEncoder.encode(queryWord, "utf-8") + "&page=" + URLEncoder.encode(page + "", "utf-8"));
        } else {
            httpGet = new HttpGet("http://www.alfanous.org/jos2?query=" + URLEncoder.encode(queryWord, "utf-8"));
        }
        try {
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

    public boolean isFounded(JSONObject json) throws JSONException {
        JSONObject reader = (JSONObject) json;
        JSONObject search = (JSONObject) reader.get("search");
        JSONObject interval = (JSONObject) search.get("interval");
        int start = (Integer) interval.get("start");
        if (start == -1) {
            return false;
        } else {
            return true;
        }
    }


    public int[] loadInfo(JSONObject json) throws JSONException {
        int[] result = new int[4];

        // load Number Of Pages
        JSONObject reader = (JSONObject) json;
        JSONObject search = (JSONObject) reader.get("search");
        JSONObject interval = (JSONObject) search.get("interval");
        result[0] = (Integer) interval.get("nb_pages");
        result[1] = (Integer) interval.get("start");
        result[2] = (Integer) interval.get("end");
        result[3] = (Integer) interval.get("total");

        return result;

    }

    public String loadAya(int i, JSONObject json) throws JSONException {
        JSONObject reader = (JSONObject) json;
        JSONObject search = (JSONObject) reader.get("search");
        JSONObject ayas = (JSONObject) search.get("ayas");
        JSONObject ayaObject = (JSONObject) ayas.get("" + i);
        JSONObject surhObject = (JSONObject) ayaObject.get("sura");
        JSONObject ayaInfo = (JSONObject) ayaObject.get("aya");
        int id = (Integer) ayaInfo.get("id");
        String MP3Link = (String) ayaInfo.get("recitation");
        String text = (String) ayaInfo.get("text");
        String surah = (String) surhObject.get("arabic_name");

        StringBuilder ayaty = new StringBuilder();

        ayaty.append("سورة:" + surah + "\t" + "رقم الأية:" + id + "\n{" + text + "}");
        while (ayaty.indexOf("<") != -1) {
            ayaty.delete(ayaty.indexOf("<"), ayaty.indexOf("<") + 26);
            ayaty.delete(ayaty.indexOf("<"), ayaty.indexOf("<") + 7);
        }

        return ayaty.toString();
    }


    public String loadMP3(int i, JSONObject json) throws JSONException {
        JSONObject reader = (JSONObject) json;
        JSONObject search = (JSONObject) reader.get("search");
        JSONObject ayas = (JSONObject) search.get("ayas");
        JSONObject ayaObject = (JSONObject) ayas.get("" + i);
        JSONObject ayaInfo = (JSONObject) ayaObject.get("aya");
        String MP3Link = (String) ayaInfo.get("recitation");

        StringBuilder ayaty = new StringBuilder();


        ayaty.append(MP3Link);


        return ayaty.toString();
    }
}
