package com.example.zx.youjiandroid;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Administrator on 2017/12/8.
 */

public class FanKuiActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView jieshufankui;
    private TextView tijiaofankui;
    private EditText fankuineirong;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fankui);
        jieshufankui = (ImageView) findViewById(R.id.jieshufankui);
        jieshufankui.setOnClickListener(this);
        tijiaofankui = (TextView) findViewById(R.id.tijiaofankui);
        tijiaofankui.setOnClickListener(this);
        fankuineirong = (EditText) findViewById(R.id.fankuineirong);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tijiaofankui:
                String neirong = fankuineirong.getText().toString();
                if (neirong.equals("")){
                    Toast.makeText(this,"内容不能为空",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(this,"提交成功",Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            case R.id.jieshufankui:
                finish();
                break;
        }
    }
}
