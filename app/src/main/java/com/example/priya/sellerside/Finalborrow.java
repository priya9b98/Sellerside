package com.example.priya.sellerside;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.AWSStartupHandler;
import com.amazonaws.mobile.client.AWSStartupResult;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.sql.Timestamp;

public class Finalborrow extends AppCompatActivity {
    TextView bname,breturn,bborrow,cname,preturn;
    Button predict,borrow,delete;
    ImageView imageView;
    RequestTableDO requestTableDO;
    DynamoDBMapper dynamoDBMapper;
    Bookborrow bookborrow;
    Double lastno=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finalborrow);
        bname=findViewById(R.id.name11);
        bborrow=findViewById(R.id.bordat);
        breturn=findViewById(R.id.retdat);
        cname=findViewById(R.id.pri1);
        preturn=findViewById(R.id.totalprice);
        predict=findViewById(R.id.confirm);
        borrow=findViewById(R.id.button);
        imageView=findViewById(R.id.imageView2);
        delete=findViewById(R.id.deletebtn);

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

        Bundle bundle=getIntent().getBundleExtra("bund");

        final Double bookid=bundle.getDouble("na");
        String email=bundle.getString("cid");
        Double borrowid=bundle.getDouble("borrid");
        String borrowdate=bundle.getString("borrowdate");
        String returndate=bundle.getString("returndate");
        String book_name=bundle.getString("bookname");
        final Double custid=bundle.getDouble("custid");
        String imageurl=bundle.getString("image");

        RequestOptions p=new RequestOptions();
        p.placeholder(R.mipmap.ic_launcher);
        Glide.with(getApplicationContext()).applyDefaultRequestOptions(p).load(imageurl).into(imageView);

        cname.setText(email);
        bname.setText(book_name);
        breturn.setText(returndate);
        bborrow.setText(borrowdate);
         bookborrow=new Bookborrow();
         bookborrow.setActualRetDate("null");
         bookborrow.setBookID(bookid);
         bookborrow.setBorrowId(borrowid);
         bookborrow.setDateClaimToRet(returndate);
         bookborrow.setRating(0.0);
         bookborrow.setSupplierID("1");
         bookborrow.setCustID(custid);
         bookborrow.setDateOfBorrow(borrowdate);
         requestTableDO=new RequestTableDO();
         requestTableDO.setBookId(bookid);
         requestTableDO.setAccepted("pending");
         requestTableDO.setCustEmail(email);
         requestTableDO.setCustId(custid);
         requestTableDO.setDateClaimToRet(returndate);
         requestTableDO.setDateOfBorrow(borrowdate);
         requestTableDO.setImageUrl(imageurl);
         requestTableDO.setRequestId(borrowid);



        predict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer integer=(int)Math.round(bookid);
                Integer integer1=(int)Math.round(custid);
                fetch("http://finalml-env.ie4ws9tnmb.us-east-1.elasticbeanstalk.com/item/1448/385333218");
            }
        });

        borrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addrequest(dynamoDBMapper,bookborrow);
                deletef(dynamoDBMapper,requestTableDO,1);
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletef(dynamoDBMapper,requestTableDO,0);
            }
        });

    }

    private void fetch(String s) {
        Fetch fetch1=new Fetch(Finalborrow.this,this,s);
        fetch1.execute();
    }

    private void deletef(final DynamoDBMapper dynamoDBMapper, final RequestTableDO requestTableDO,final  int i) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                dynamoDBMapper.delete(requestTableDO);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(i==0){
                        Toast.makeText(Finalborrow.this,"Deleted",Toast.LENGTH_SHORT).show();}
                        else{
                            Toast.makeText(Finalborrow.this,"Accepted",Toast.LENGTH_SHORT).show();
                    }

                        Intent in=new Intent(Finalborrow.this,MainActivity.class);
                        startActivity(in);
                    }
                });
            }
        }).start();


    }

    private void addrequest(final DynamoDBMapper dynamoDBMapper, final Bookborrow bookborrow) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                dynamoDBMapper.save(bookborrow);
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                LastIdsDO lastIdsDO=new LastIdsDO();
                lastIdsDO=dynamoDBMapper.load(LastIdsDO.class,"Book_Borrow");
                lastIdsDO.setId(bookborrow.getBorrowId());
                Timestamp entries=new Timestamp(System.currentTimeMillis());
                lastIdsDO.setTimestamp(entries.toString());
                dynamoDBMapper.save(lastIdsDO);
            }
        }).start();
    }
}
