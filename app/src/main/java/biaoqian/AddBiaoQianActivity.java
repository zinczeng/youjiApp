package biaoqian;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.zx.youjiandroid.R;
import com.example.zx.youjiandroid.YJContentActivity;
import com.sortricheditor.ui.GLActivity;

import java.util.ArrayList;

public class AddBiaoQianActivity extends AppCompatActivity {
    ArrayList<String> list;
    EditText et;
    LayoutInflater mInflater;
    private String activity;
    private FlowLayout mFlowLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity=getIntent().getStringExtra("Activity");
        list = getIntent().getStringArrayListExtra("bq");
        Log.e("mm",activity+"");
        setContentView(R.layout.activity_add_biao_qian);
        mFlowLayout = (FlowLayout) findViewById(R.id.id_flowlayout);
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = et.getText().toString();
                if (!str.equals("")) {
                    list.add(str);
                }
                if(activity.equals("YJContentActivity")) {
                    setResult(1, new Intent(AddBiaoQianActivity.this, YJContentActivity.class).putStringArrayListExtra("bg", list));

                }if(activity.equals("GLActivity")){
                    setResult(1, new Intent(AddBiaoQianActivity.this, GLActivity.class).putStringArrayListExtra("bg", list));
                }
                finish();
            }
        });

        mInflater = LayoutInflater.from(this);
        initData();
    }


    private void initData() {
        mFlowLayout.removeAllViews();
        for (int i = 0; i < list.size(); i++) {
            LinearLayout view = (LinearLayout) mInflater.inflate(R.layout.add_tv_item_flow, mFlowLayout, false);
            TextView tv = (TextView) view.findViewById(R.id.tv);
            final int finalI = i;
            view.findViewById(R.id.iv).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mFlowLayout.removeViewAt(finalI);
                    list.remove(finalI);
                    initData();

                }
            });
            tv.setText(list.get(i));
            mFlowLayout.addView(view);
        }
        et = (EditText) mInflater.inflate(R.layout.edit_item_flow, mFlowLayout, false);
        mFlowLayout.addView(et);

    }


}
