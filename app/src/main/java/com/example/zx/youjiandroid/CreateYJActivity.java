package com.example.zx.youjiandroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Administrator on 2017/11/17.
 */

public class CreateYJActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView back_to_shoucang;
    private TextView next_neirong,hasnumTV;
    private EditText get_youjititle;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_yj);

        initview();
        showchage();
    }

    private void showchage() {


        get_youjititle.addTextChangedListener(new TextWatcher() {
            private   int num =25;
            private CharSequence temp;

            private int selectionStart;

            private int selectionEnd;

            @Override

            public void onTextChanged(CharSequence s, int start, int before,

                                      int count) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count,

                                          int after) {

                temp = s;

            }

            public void afterTextChanged(Editable s) {

                // TODO Auto-generated method stub

                int number = num - s.length();

                hasnumTV.setText("可输入 " + "" + number + " 字");

                selectionStart = get_youjititle.getSelectionStart();

                selectionEnd = get_youjititle.getSelectionEnd();

                if (temp.length() > num) {

                    s.delete(selectionStart - 1, selectionEnd);

                    int tempSelection = selectionEnd;

                    get_youjititle.setText(s);

                    get_youjititle.setSelection(tempSelection);// 设置光标在最后

                }

            }

        });

    }

    private void initview() {
        back_to_shoucang = (ImageView) findViewById(R.id.back_to_shoucang);
        next_neirong = (TextView) findViewById(R.id.next_neirong);
        hasnumTV = (TextView) findViewById(R.id.hasnumTV);
        get_youjititle = (EditText) findViewById(R.id.get_youjititle);
        next_neirong.setOnClickListener(this);
        back_to_shoucang.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.next_neirong:
                String biaoti=get_youjititle.getText().toString();
                if(biaoti.equals("")){
                    Toast.makeText(getApplication(),"游记标题不可以为空哦！",Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(getApplication(),YJContentActivity.class);
                    intent.putExtra("Title",biaoti);
                    intent.putExtra("creat","0");
                    startActivity(intent);
                }
                break;
            case R.id.back_to_shoucang:
            finish();
                break;
        }
    }
}
