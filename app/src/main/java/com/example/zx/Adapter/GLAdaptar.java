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

import bean.SuiJi;
import util.timeutil;

/**
 * Created by Administrator on 2017/11/13.
 */

public class GLAdaptar extends RecyclerView.Adapter<GLAdaptar.GLview> {
    private List<SuiJi> products =new ArrayList<>();
    private static RecycleItemClickListener itemClickListener;
    private SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//小写的mm表示的是分钟
    public GLAdaptar(List<SuiJi>list,RecycleItemClickListener clickListener) {
        this.products=list;
        itemClickListener=clickListener;
    }


    @Override
    public GLview onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.gonglve_itms, viewGroup, false);
        return new GLAdaptar.GLview(view);
    }

    @Override
    public void onBindViewHolder(GLview GLview, int position) {
        String time = products.get(position).getUpData();
        int length = time.length();
        time = time.substring(0,length-2);
        try {
            Date date =sdf.parse(time);
            GLview.shijian.setText(timeutil.getTimeFormatText(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
      Glide.with(GLview.itemView.getContext()).load(products.get(position).getContentPt1())
              .into(GLview.tupian);
        GLview.biaoti.setText(products.get(position).getTitle());
        GLview.neirong.setText(products.get(position).getContent1());
        GLview.biaoqian.setText(products.get(position).getUserName());
    }

    @Override
    public int getItemCount() {
       return products==null?0:products.size();
    }
    public static class GLview extends  RecyclerView.ViewHolder implements View.OnClickListener{

        private ImageView tupian;
        private TextView biaoti,neirong,shijian,biaoqian;


        public GLview(View itemView){
            super(itemView);
            tupian= (ImageView) itemView.findViewById(R.id.gonglve_tupian );
            biaoti= (TextView) itemView.findViewById(R.id.gonglve_biaoti);
            neirong= (TextView) itemView.findViewById(R.id.gonglve_neirong);
            shijian= (TextView) itemView.findViewById(R.id.gonglve_shijian);
            biaoqian= (TextView) itemView.findViewById(R.id.biaoqian_gonglve);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onItemClick(v,this.getLayoutPosition());
        }
    }

}
