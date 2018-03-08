package com.example.zx.masonry;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.zx.youjiandroid.R;

import java.util.List;

import bean.SuiJi;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by clevo on 2015/7/27.
 */

public class MasonryAdapter extends RecyclerView.Adapter<MasonryAdapter.MasonryView>{
    private List<SuiJi> products;
    private static RecycleItemClickListener itemClickListener;
    private static View view;

    public MasonryAdapter(List<SuiJi> list,RecycleItemClickListener clickListener) {
        products=list;
        itemClickListener=clickListener;
    }

    @Override
    public MasonryView onCreateViewHolder(ViewGroup viewGroup, int i) {
         view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.home_masonry_item, viewGroup, false);


        return new MasonryView(view);
    }

    @Override
    public void onBindViewHolder(MasonryView masonryView, int position) {

        Glide.with(masonryView.itemView.getContext()).load(products.get(position).getUserPT())
                .bitmapTransform(new CropCircleTransformation(view.getContext()))
                .into(masonryView.avatarImageView2);
        Glide.with(masonryView.itemView.getContext()).load(products.get(position).getContentPt1())
                .into(masonryView.imageView);
        masonryView.textView.setText(products.get(position).getTitle());
        masonryView.yonghuming.setText(products.get(position).getUserName());
        masonryView.dianzanshu.setText(products.get(position).getDianzan());

    }


    @Override
    public int getItemCount() {
        return products==null?0:products.size();
    }

    //viewholder
    public static class MasonryView extends  RecyclerView.ViewHolder implements View.OnClickListener{

        private ImageView imageView;
        private TextView textView,yonghuming,dianzanshu;
        private ImageView avatarImageView2;

       public MasonryView(View itemView){
           super(itemView);
           avatarImageView2 = (ImageView)itemView.findViewById(R.id.show_user_photo_);
           imageView= (ImageView) itemView.findViewById(R.id.masonry_item_img );
           textView= (TextView) itemView.findViewById(R.id.masonry_item_title);
           yonghuming= (TextView) itemView.findViewById(R.id.youji_yonghuming);
           dianzanshu= (TextView) itemView.findViewById(R.id.youji_dianzanshu);
           itemView.setOnClickListener(this);

       }

        @Override
        public void onClick(View v) {
            itemClickListener.onItemClick(v,this.getLayoutPosition());
        }
    }


}
