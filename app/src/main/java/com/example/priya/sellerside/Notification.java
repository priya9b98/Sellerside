package com.example.priya.sellerside;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.AWSStartupHandler;
import com.amazonaws.mobile.client.AWSStartupResult;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Notification extends AppCompatActivity {
    List<RequestTableDO> req;
    ListView listView;
    DynamoDBMapper dynamoDBMapper;
    Button refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        req=new ArrayList<>();
        req=null;
        refresh=findViewById(R.id.btnrefresh);
        listView=findViewById(R.id.list);
        AWSMobileClient.getInstance().initialize(this, new AWSStartupHandler() {
            @Override
            public void onComplete(AWSStartupResult awsStartupResult) {
                AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(AWSMobileClient.getInstance().getCredentialsProvider());
                dynamoDBMapper = DynamoDBMapper.builder()
                        .dynamoDBClient(dynamoDBClient)
                        .awsConfiguration(
                                AWSMobileClient.getInstance().getConfiguration())
                        .build();
            }
        }).execute();

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findlist(dynamoDBMapper);
            }
        });




    }

    private void findlist(final DynamoDBMapper dynamoDBMapper) {
        Map<String, AttributeValue> ev=new HashMap<>();
        ev.put(":val1", new AttributeValue().withN("0"));

        final DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression("Book_Id > :val1").withExpressionAttributeValues(ev);

        new Thread(new Runnable() {
            @Override
            public void run() {

                List<RequestTableDO> replies =  dynamoDBMapper.scan(RequestTableDO.class, new DynamoDBScanExpression());
                req=replies;
            }
        }).start();
        while(req==null){}
        Requestadapter requestadapter=new Requestadapter(getApplicationContext(),req,dynamoDBMapper);
        listView.setAdapter(requestadapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent it = new Intent(Notification.this, Finalborrow.class);
                TextView book_name=findViewById(R.id.bookname);
                Bundle b = new Bundle();
                b.putDouble("na", req.get(position).getBookId());
                 b.putString("cid",req.get(position).getCustEmail());
                 b.putString("bookname",book_name.getText().toString());
                b.putDouble("borrid",req.get(position).getRequestId());
                b.putString("image",req.get(position).getImageUrl());
                b.putString("borrowdate",req.get(position).getDateOfBorrow());
                b.putString("returndate",req.get(position).getDateClaimToRet());
                b.putDouble("custid",req.get(position).getCustId());
                it.putExtra("bund", b);
                startActivity(it);
            }
                            });
    }}