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

public class GLRWAdaptar extends RecyclerView.Adapter<GLRWAdaptar.RWview> {
    private List<Review1> products =new ArrayList<>();
    private static RecycleItemClickListener itemClickListener;
    private SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//小写的mm表示的是分钟
    public GLRWAdaptar(List<Review1>list,RecycleItemClickListener clickListener) {
        this.products=list;
        itemClickListener=clickListener;
    }


    @Override
    public GLRWAdaptar.RWview onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.glreview_itmes, viewGroup, false);
        return new GLRWAdaptar.RWview(view);
    }

    @Override
    public void onBindViewHolder(GLRWAdaptar.RWview RWview, int position) {
        String time = products.get(position).getReviewTime();
        Glide.with(RWview.itemView.getContext()).load(products.get(position).getUserPT())
                .bitmapTransform(new CropCircleTransformation(RWview.itemView.getContext()))
                .into(RWview.yonghutouxiang);
        RWview.mingzi.setText(products.get(position).getUserName());
        RWview.neirong.setText(products.get(position).getContent());
        int length = time.length();
        time = time.substring(0,length-2);
        try {
            Date date =sdf.parse(time);
            RWview.shijian.setText(timeutil.getTimeFormatText(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return products==null?0:products.size();
    }
    public static class RWview extends  RecyclerView.ViewHolder implements View.OnClickListener{

        private ImageView yonghutouxiang;
        private TextView mingzi,neirong,shijian;


        public RWview(View itemView){
            super(itemView);
            yonghutouxiang= (ImageView) itemView.findViewById(R.id.pingluntouxiang );
            mingzi= (TextView) itemView.findViewById(R.id.pingluningzi);
            neirong= (TextView) itemView.findViewById(R.id.yonghupinglu);
            shijian= (TextView) itemView.findViewById(R.id.punlunshijian);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onItemClick(v,this.getLayoutPosition());
        }
    }

}