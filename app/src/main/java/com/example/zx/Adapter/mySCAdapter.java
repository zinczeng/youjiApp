package com.example.zx.Adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.zx.masonry.RecycleItemClickListener;
import com.example.zx.youjiandroid.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bean.Collect1;
import util.timeutil;

public class mySCAdapter extends RecyclerView.Adapter<mySCAdapter.mySCview> {
    private List<Collect1> products =new ArrayList<>();
    private static RecycleItemClickListener itemClickListener;
    private SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//小写的mm表示的是分钟
    public mySCAdapter(List<Collect1>list,RecycleItemClickListener clickListener) {
        Log.e("sd",list.size()+"");
        this.products=list;
        Log.e("sd",products.size()+"");
        itemClickListener=clickListener;
    }


    @Override
    public mySCview onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.shoucang_itmes, viewGroup, false);
        return new mySCAdapter.mySCview(view);
    }

    @Override
    public void onBindViewHolder(mySCview mySCview, int position) {
        String time = products.get(position).getUpData();
        int length = time.length();
        time = time.substring(0,length-2);
        try {
            Date date =sdf.parse(time);
            mySCview.shijian.setText(timeutil.getTimeFormatText(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Glide.with(mySCview.itemView.getContext()).load(products.get(position).getContentPt1())
                .into(mySCview.tupian);
        mySCview.biaoti.setText(products.get(position).getTitle());
        mySCview.neirong.setText(products.get(position).getContent1());
        if(products.get(position).getTypes().equals("0")){
            mySCview.biaoqian.setText("游记");
        } else if(products.get(position).getTypes().equals("1")){
            mySCview.biaoqian.setText("攻略");
        }
    }

    @Override
    public int getItemCount() {
        return products==null?0:products.size();
    }
    public static class mySCview extends  RecyclerView.ViewHolder implements View.OnClickListener{

        private ImageView tupian;
        private TextView biaoti,neirong,shijian,biaoqian;


        public mySCview(View itemView){
            super(itemView);
            tupian= (ImageView) itemView.findViewById(R.id.neirongtubiaoaaa);
            biaoti= (TextView) itemView.findViewById(R.id.biaotiaaaa);
            neirong= (TextView) itemView.findViewById(R.id.neirongaaaa);
            shijian= (TextView) itemView.findViewById(R.id.shijiano);
            biaoqian= (TextView) itemView.findViewById(R.id.types);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onItemClick(v,this.getLayoutPosition());
        }
    }

}