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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import bean.SuiJi;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class myYJAdapter extends RecyclerView.Adapter<myYJAdapter.myYJview> {
    private List<SuiJi> products =new ArrayList<>();
    private static RecycleItemClickListener itemClickListener;
    private SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//小写的mm表示的是分钟
    public myYJAdapter(List<SuiJi>list,RecycleItemClickListener clickListener) {
        this.products=list;
        itemClickListener=clickListener;
    }


    @Override
    public myYJview onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.my_youji_items, viewGroup, false);
        return new myYJAdapter.myYJview(view);
    }

    @Override
    public void onBindViewHolder(myYJview myYJview, int position) {
        String time = products.get(position).getUpData().toString();
        int length = time.length();
        time = time.substring(0,length-2);

        myYJview.shijian.setText(time);
        Glide.with(myYJview.itemView.getContext()).load(products.get(position).getContentPt1())
                .into(myYJview.tupian);
        Glide.with(myYJview.itemView.getContext()).load(products.get(position).getUserPT())
                .bitmapTransform(new CropCircleTransformation(myYJview.itemView.getContext()))
                .into(myYJview.yognhutouxia);
        myYJview.biaoti.setText(products.get(position).getTitle());
        myYJview.neirong.setText(products.get(position).getContent1());
        myYJview.biaoqian.setText(products.get(position).getUserName());
        myYJview.dianzanshu.setText(products.get(position).getDianzan());
    }

    @Override
    public int getItemCount() {
        return products==null?0:products.size();
    }
    public static class myYJview extends  RecyclerView.ViewHolder implements View.OnClickListener{

        private ImageView tupian,yognhutouxia;
        private TextView biaoti,neirong,shijian,biaoqian,dianzanshu;


        public myYJview(View itemView){
            super(itemView);
            tupian= (ImageView) itemView.findViewById(R.id.tupian );
            yognhutouxia= (ImageView) itemView.findViewById(R.id.yognhutouxia );
            biaoti= (TextView) itemView.findViewById(R.id.mytitile);
            neirong= (TextView) itemView.findViewById(R.id.woshineirong);
            dianzanshu= (TextView) itemView.findViewById(R.id.dianzanshuliang);
            shijian= (TextView) itemView.findViewById(R.id.shijian);
            biaoqian= (TextView) itemView.findViewById(R.id.yognhumingzi);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onItemClick(v,this.getLayoutPosition());
        }
    }

}