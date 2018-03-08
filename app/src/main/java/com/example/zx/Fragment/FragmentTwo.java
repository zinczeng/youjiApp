package com.example.zx.Fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.example.zx.Util.AsyncTaskHelper;
import com.example.zx.Util.FastJsonTools;
import com.example.zx.Util.MyConfig;
import com.example.zx.youjiandroid.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bean.YJGLContent;


/**
 * Created by clevo on 2015/7/30.
 */
public class FragmentTwo  extends Fragment {
    private LinearLayout youji_xiangqing;
    private static String YJGLId;
    public static List<YJGLContent> list;
    private AsyncTaskHelper.OnDataDownloadListener downloadListener;
    private AsyncTaskHelper taskHelper;
    private static String URL;
    public static FragmentTwo newInstance(String YJGLid) {
        YJGLId = YJGLid;
        return new FragmentTwo();
    }


    public FragmentTwo() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.detail_fragment_two, container, false);

        gencontent();

        youji_xiangqing = (LinearLayout) view.findViewById(R.id.youji_xiangqing);

        return view;
    }

    /*
拿取攻略内容
*/
    private void gencontent() {
        MyConfig config = new MyConfig("YJGLContentServlet", "selectallct");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("YJGLId", YJGLId);
        String str = JSON.toJSONString(map);
        try {
            URL = config.getURL() + str;
        } catch (Exception e) {
            e.printStackTrace();
        }
        downloadListener = new AsyncTaskHelper.OnDataDownloadListener() {
            @Override

            public void onDataDownload(byte[] result) {
                String jsonString = new String(result); // 返回的字节数组转换为字符串
                Map<String, Object> map = new HashMap<String, Object>();
                map = FastJsonTools.getMap(jsonString);
                String code = map.get("code").toString();
                if (code.equals("1")) {
                    String Result = map.get("result").toString();
                    list = JSON.parseArray(Result, YJGLContent.class);
                    LayoutInflater inflater = LayoutInflater.from(getContext());
                    for (int i = 0; i < list.size(); i++) {
                        TextView tv = (TextView) inflater.inflate(R.layout.layout2, youji_xiangqing, false);
                        tv.setText(list.get(i).getParagraphText());
                        tv.setTextColor(Color.parseColor("#3194D8"));
                        youji_xiangqing.addView(tv);
                    }
                }
                if (code.equals("0")) {
                }
            }
        };
        taskHelper = new AsyncTaskHelper();
        taskHelper.downloadData(getContext(), URL, downloadListener);
    }
}
