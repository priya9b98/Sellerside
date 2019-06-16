package com.example.priya.sellerside;



import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread;

public class Fetch extends AsyncTask<Void,Void,List<String>> {
    String data ="";
    private Finalborrow activity;
    Context mc;
    String fdate;
    String adu;
    Fetch(Finalborrow context,Context mc,String ur){
        activity=context;
        this.mc=mc;
        this.adu=ur;
    }
    @Override
    protected List<String> doInBackground(Void... voids) {
        try {
            URL url = new URL(adu);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            //httpURLConnection.setRequestMethod("GET");
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line="";
            fdate="";


            while(line != null){
                line = bufferedReader.readLine();
                data = data + line;
            }
            //    line = bufferedReader.readLine();
            //  i++;
            int i=0;
           /*while(j<6){
                if(data.charAt(i)=='<' ){
                    i=i+3;
                    list.add(f);
                     f="";
                     j++;
                }
                else
                   f=f+Character.toString(data.charAt(i));
                i++;
            }
*/
           while (data.charAt(i)!='n'){
               fdate=fdate+Character.toString(data.charAt(i));
               i++;
           }

            runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    // Stuff that updates the UI
                    TextView txtView = (TextView) ((Finalborrow)mc).findViewById(R.id.totalprice);
                    txtView.setText(fdate);

                }
            });





        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


}


