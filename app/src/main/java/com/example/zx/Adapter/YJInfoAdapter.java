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

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import bean.YJGLContent;

/**
 * Created by Administrator on 2017/12/12.
 */

public class YJInfoAdapter extends RecyclerView.Adapter<YJInfoAdapter.YJInfoview> {
    private String pt;
private List<YJGLContent> products =new ArrayList<>();
private static RecycleItemClickListener itemClickListener;
public YJInfoAdapter(List<YJGLContent>list,RecycleItemClickListener clickListener) {
        this.products=list;
        itemClickListener=clickListener;
        }


@Override
public YJInfoview onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.youjiinfo, viewGroup, false);
        return new YJInfoAdapter.YJInfoview(view);
        }

@Override
public void onBindViewHolder(YJInfoview YJInfoview, int position) {

       pt= "http://oibc7l5du.bkt.clouddn.com/yj.jpg";
   if(!products.get(position).getYJGLContentPT().equals("")) {
        pt=products.get(position).getYJGLContentPT();
        Boolean b = pt.contains(",");
        if(b==true){
            StringTokenizer tokener = new StringTokenizer(pt, ",");
            String[] result1 = new String[tokener.countTokens()];
        pt=result1[0];
        }
    }
    Log.e("ssssssssssssd",pt+"");
        Glide.with(YJInfoview.itemView.getContext()).load(pt)
        .into(YJInfoview.tupian);
    Log.e("sdadsdsd",products.get(position).getParagraphText()+"");
    YJInfoview.neirong.setText(products.get(position).getParagraphText());

        }

@Override
public int getItemCount() {
        return products==null?0:products.size();
        }
public static class YJInfoview extends  RecyclerView.ViewHolder implements View.OnClickListener{

    private ImageView tupian;
    private TextView neirong;


    public YJInfoview(View itemView){
        super(itemView);
        tupian= (ImageView) itemView.findViewById(R.id.info_ig);
        neirong= (TextView) itemView.findViewById(R.id.info_tx);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onItemClick(v,this.getLayoutPosition());
    }
}

}
