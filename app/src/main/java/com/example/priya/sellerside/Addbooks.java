package com.example.priya.sellerside;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.AWSStartupHandler;
import com.amazonaws.mobile.client.AWSStartupResult;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

import java.sql.Timestamp;

public class Addbooks extends AppCompatActivity {
    DynamoDBMapper dynamoDBMapper;
    EditText bname,bprice,byear,burlimage,bauthor,bgenre,brate;
    Button addbook;
    String nameb,price,year,image1,author,genre,rate;
    Double bookid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addbooks);

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
        bname=findViewById(R.id.namee);
        bprice=findViewById(R.id.price);
        bauthor=findViewById(R.id.author);
        byear=findViewById(R.id.year);
        burlimage=findViewById(R.id.imageurl);
        bgenre=findViewById(R.id.Genre);
        brate=findViewById(R.id.rating);
        nameb=bname.getText().toString();
        price=bprice.getText().toString();
        bookid=null;
        year=byear.getText().toString();
        image1=burlimage.getText().toString();
        author=bauthor.getText().toString();
        genre=bgenre.getText().toString();
        rate=brate.getText().toString();

        addbook=findViewById(R.id.btnaddbook);
        addbook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        LastIdsDO lastIdsDO=new LastIdsDO();
                        lastIdsDO=dynamoDBMapper.load(LastIdsDO.class,"Books");
                        bookid=lastIdsDO.getId()+1;
                    }
                }).start();
                while (bookid==null){}
                BooksDO booksDO=new BooksDO();
                booksDO.setAuthor(author);
                booksDO.setBookName(nameb);
                booksDO.setGenre(genre);
                booksDO.setISBN(bookid);
                booksDO.setImageUrl(image1);
                booksDO.setPublicationYear(year);
                booksDO.setPrice(price);
                updatetotable(dynamoDBMapper,booksDO);
                updatelastid(dynamoDBMapper,bookid);

            }
        });

    }

    private void updatelastid(final DynamoDBMapper dynamoDBMapper, final Double bookid) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                LastIdsDO lastIdsDO=new LastIdsDO();
                lastIdsDO=dynamoDBMapper.load(LastIdsDO.class,"Books");
                lastIdsDO.setId(bookid);
                Timestamp entries=new Timestamp(System.currentTimeMillis());
                lastIdsDO.setTimestamp(entries.toString());
                dynamoDBMapper.save(lastIdsDO);

            }
        }).start();
    }

    private void updatetotable(final DynamoDBMapper dynamoDBMapper, final BooksDO booksDO) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                dynamoDBMapper.save(booksDO);
            }
        }).start();
    }
}
