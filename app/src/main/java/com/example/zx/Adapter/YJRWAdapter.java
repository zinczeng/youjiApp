package com.example.zx.Adapter;

import android.support.v7.widget.RecyclerView;
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

import bean.Review1;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import util.timeutil;

/**
 * Created by Administrator on 2017/11/14.
 */

public class YJRWAdapter extends RecyclerView.Adapter<YJRWAdapter.YJview> {
    private List<Review1> products =new ArrayList<>();
    private static RecycleItemClickListener itemClickListener;
    private SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//小写的mm表示的是分钟
    public YJRWAdapter(List<Review1>list, RecycleItemClickListener clickListener) {
        this.products=list;
        itemClickListener=clickListener;
    }


    @Override
    public YJRWAdapter.YJview onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.yjreview_items, viewGroup, false);
        return new YJRWAdapter.YJview(view);
    }

    @Override
    public void onBindViewHolder(YJRWAdapter.YJview YJview, int position) {
        String time = products.get(position).getReviewTime();
        Glide.with(YJview.itemView.getContext()).load(products.get(position).getUserPT())
                .bitmapTransform(new CropCircleTransformation(YJview.itemView.getContext()))
                .into(YJview.yonghutouxiang);
        YJview.mingzi.setText(products.get(position).getUserName());
        YJview.neirong.setText(products.get(position).getContent());
        int length = time.length();
        time = time.substring(0,length-2);
        try {
            Date date =sdf.parse(time);
           YJview.shijian.setText(timeutil.getTimeFormatText(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return products==null?0:products.size();
    }
    public static class YJview extends  RecyclerView.ViewHolder implements View.OnClickListener{

        private ImageView yonghutouxiang;
        private TextView mingzi,neirong,shijian;


        public YJview(View itemView){
            super(itemView);
            yonghutouxiang= (ImageView) itemView.findViewById(R.id.pingluntouxiang2 );
            mingzi= (TextView) itemView.findViewById(R.id.pingluningzi2);
            neirong= (TextView) itemView.findViewById(R.id.yonghupinglu2);
            shijian= (TextView) itemView.findViewById(R.id.punlunshijian2);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onItemClick(v,this.getLayoutPosition());
        }
    }

}