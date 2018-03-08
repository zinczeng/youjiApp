package com.parities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.zx.youjiandroid.MainActivity;
import com.example.zx.youjiandroid.R;

/**
 * Created by Administrator on 2017/11/1.
 */

public class ParitiesActivity extends AppCompatActivity implements View.OnClickListener {
    TextView n1, n2, n3, n4, n5, n6, n7, n8, n9, n0, n, DEL,rmb,hkb,usd,eur,jpy,gbp;
ImageView back_tozhushou2;
int jishuqi=1;
    double HK=1.1800,US=0.1513,EU=0.1302,JP=17.2500,GB=0.1138,num=0.0;
    boolean f=false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parities);
        initview();
    }

    public void showmoney( int number){
        if(f==false){
            num=num*10+number;
        }else if(f==true){
            num=num+number/(Math.pow(10,jishuqi));
        }
        rmb.setText(String.valueOf(num));
        hkb.setText(String.valueOf(num*HK));
        usd.setText(String.valueOf(num*US));
        eur.setText(String.valueOf(num*EU));
        jpy.setText(String.valueOf(num*JP));
        gbp.setText(String.valueOf(num*GB));
    }
    private void initview() {
        n1 = (TextView) findViewById(R.id.num1);
        n2 = (TextView) findViewById(R.id.num2);
        n3 = (TextView) findViewById(R.id.num3);
        n4 = (TextView) findViewById(R.id.num4);
        n5 = (TextView) findViewById(R.id.num5);
        n6 = (TextView) findViewById(R.id.num6);
        n7 = (TextView) findViewById(R.id.num7);
        n8 = (TextView) findViewById(R.id.num8);
        n9 = (TextView) findViewById(R.id.num9);
        n0 = (TextView) findViewById(R.id.num0);
        n = (TextView) findViewById(R.id.numdian);
        DEL = (TextView) findViewById(R.id.numshanchu);
        rmb = (TextView) findViewById(R.id.RMB);
        hkb = (TextView) findViewById(R.id.HKB);
        usd = (TextView) findViewById(R.id.USD);
        eur = (TextView) findViewById(R.id.EUR);
        jpy = (TextView) findViewById(R.id.JPY);
        gbp = (TextView) findViewById(R.id.GBP);
        back_tozhushou2 = (ImageView) findViewById(R.id.back_tozhushou2);
        n1.setOnClickListener(this);
        n2.setOnClickListener(this);
        n3.setOnClickListener(this);
        n4.setOnClickListener(this);
        n5.setOnClickListener(this);
        n6.setOnClickListener(this);
        n7.setOnClickListener(this);
        n8.setOnClickListener(this);
        n9.setOnClickListener(this);
        n0.setOnClickListener(this);
        n.setOnClickListener(this);
        DEL.setOnClickListener(this);
        back_tozhushou2.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_tozhushou2:
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("main",3);
                startActivity(intent);
                finish();
                break;
            case R.id.num1:
                showmoney(Integer.valueOf(n1.getText().toString()));
                if(f==true){
                    jishuqi++;
                }
                break;
            case R.id.num2:
                showmoney(Integer.valueOf(n2.getText().toString()));
                if(f==true){
                    jishuqi++;
                }
                break;
            case R.id.num3:
                showmoney(Integer.valueOf(n3.getText().toString()));
                if(f==true){
                    jishuqi++;
                }
                break;
            case R.id.num4:
                showmoney(Integer.valueOf(n4.getText().toString()));
                if(f==true){
                    jishuqi++;
                }
                break;
            case R.id.num5:
                showmoney(Integer.valueOf(n5.getText().toString()));
                if(f==true){
                    jishuqi++;
                }
                break;
            case R.id.num6:
                showmoney(Integer.valueOf(n6.getText().toString()));
                if(f==true){
                    jishuqi++;
                }
                break;
            case R.id.num7:
                showmoney(Integer.valueOf(n7.getText().toString()));
                if(f==true){
                    jishuqi++;
                }
                break;
            case R.id.num8:
                showmoney(Integer.valueOf(n8.getText().toString()));
                if(f==true){
                    jishuqi++;
                }
                break;
            case R.id.num9:
                showmoney(Integer.valueOf(n9.getText().toString()));
                if(f==true){
                    jishuqi++;
                }
                break;
            case R.id.num0:
                showmoney(Integer.valueOf(n0.getText().toString()));
                if(f==true){
                    jishuqi++;
                }
                break;
            case R.id.numdian:
                f=true;
                break;
            case R.id.numshanchu:
                num=0.0;
                showmoney(0);
                f=false;
                break;
        }
    }
}
