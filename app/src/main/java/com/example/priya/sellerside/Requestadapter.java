package com.example.priya.sellerside;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;



public class Requestadapter extends BaseAdapter {

    public interface OnItemClickListener {
        void onItemClick(RequestTableDO item);
    }

    Context mcontext;
    DynamoDBMapper dynamoDBMapper;
    public List<RequestTableDO> list;
    String Cname=null;
    String bname=null;
    String bauthor=null;
    //private OnItemClickListener onItemClickListener;

    public Requestadapter(Context mcontext,List<RequestTableDO> l,DynamoDBMapper dynamoDBMapper){
        this.mcontext=mcontext;
        this.list=l;
        this.dynamoDBMapper=dynamoDBMapper;

    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView= LayoutInflater.from(mcontext).inflate(R.layout.booklist,null);
        TextView book_name,cust_name,author,bdate;
        ImageView book_image;
        book_image=convertView.findViewById(R.id.bookpic);
        book_name=convertView.findViewById(R.id.bookname);
        cust_name=convertView.findViewById(R.id.custname);
        bdate=convertView.findViewById(R.id.bookr);
        author=convertView.findViewById(R.id.bookaut);
        setcustname(list.get(position).getCustEmail(),cust_name);
        setbook(list.get(position).getBookId(),book_name,author);
        bdate.setText(list.get(position).getDateOfBorrow());
        RequestOptions p=new RequestOptions();
        p.placeholder(R.mipmap.ic_launcher);
        Glide.with(mcontext).applyDefaultRequestOptions(p).load(list.get(position).getImageUrl()).into(book_image);

        return convertView;
    }

    private void setbook(final Double bookId,TextView author,TextView book_name) {
        bname=null;
        bauthor=null;
        new Thread(new Runnable() {
            @Override
            public void run() {
                BooksDO booksDO=new BooksDO();
                booksDO=dynamoDBMapper.load(BooksDO.class,bookId);
                bname=booksDO.getBookName();
                bauthor=booksDO.getAuthor();
            }
        }).start();
        while(bname==null){}
        author.setText(bname);
        book_name.setText(bauthor);
    }

    private void setcustname(final String custemail,TextView cust_name) {
        Cname=null;
        new Thread(new Runnable() {
            @Override
            public void run() {
                CustomerTableDo customerTableDos=  new CustomerTableDo();
                customerTableDos=dynamoDBMapper.load(CustomerTableDo.class,custemail);
                Cname=customerTableDos.getCustName();
            }
        }).start();
        while(Cname==null){}
        cust_name.setText(Cname);
    }
}
