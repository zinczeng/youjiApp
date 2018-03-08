package com.iflytek.voice;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zx.youjiandroid.MainActivity;
import com.example.zx.youjiandroid.R;
import com.iflytek.YouDao.ToastUtils;
import com.iflytek.YouDao.TranslateAdapter;
import com.iflytek.YouDao.TranslateData;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.iflytek.speechyoudao.setting.IatSettings;
import com.iflytek.speechyoudao.util.JsonParser;
import com.iflytek.sunflower.FlowerCollector;
import com.youdao.sdk.app.Language;
import com.youdao.sdk.app.LanguageUtils;
import com.youdao.sdk.ydonlinetranslate.TranslateErrorCode;
import com.youdao.sdk.ydonlinetranslate.TranslateListener;
import com.youdao.sdk.ydonlinetranslate.TranslateParameters;
import com.youdao.sdk.ydonlinetranslate.Translator;
import com.youdao.sdk.ydtranslate.Translate;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import static com.example.zx.youjiandroid.R.array.chose_languages;
import static com.example.zx.youjiandroid.R.array.speaklanguages;


public class IatDeActivity extends Activity implements OnClickListener {
    @SuppressLint("ShowToast")
    private static String TAG = IatDeActivity.class.getSimpleName();
    // 语音听写对象
    private SpeechRecognizer mIat;
    // 语音听写UI
    private RecognizerDialog mIatDialog;
    // 查询列表
    private ListView translateList;
    private Handler waitHandler = new Handler();
    private TranslateAdapter adapter;
    // 用HashMap存储听写结果
    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();
    private InputMethodManager imm;
    private EditText fanyiInputText;
    private TextView fanyiBtn;
    private ImageView back_to_zhushou;
    private Toast mToast;
    private SharedPreferences mSharedPreferences;
    // 引擎类型
    private String mEngineType = SpeechConstant.TYPE_CLOUD;
    static String speaklg = null,laguage=null,input=null;
    private boolean mTranslateEnable = false;
    private Spinner speek, choselg;
    private Translator translator;
    private List<TranslateData> list = new ArrayList<TranslateData>();
    private List<Translate> trslist = new ArrayList<Translate>();
    private ProgressDialog progressDialog = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.iatdemo);
        initview();
        initLayout();
        // 初始化识别无UI识别对象
        // 使用SpeechRecognizer对象，可根据回调消息自定义界面；
        mIat = SpeechRecognizer.createRecognizer(IatDeActivity.this, mInitListener);

        // 初始化听写Dialog，如果只使用有UI听写功能，无需创建SpeechRecognizer
        // 使用UI听写功能，请根据sdk文件目录下的notice.txt,放置布局文件和图片资源
        mIatDialog = new RecognizerDialog(IatDeActivity.this, mInitListener);
//设置听写界面
        mSharedPreferences = getSharedPreferences(IatSettings.PREFER_NAME,
                Activity.MODE_PRIVATE);
        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
    }

    private void initview() {

        translateList = (ListView) findViewById(R.id.comlist1);
        speek = (Spinner) findViewById(R.id.speak_languages_s);
        choselg = (Spinner) findViewById(R.id.chose_languages_s);

        fanyiInputText = (EditText)findViewById(R.id.iat_get);
        fanyiBtn = (TextView) findViewById(R.id.fanyiBtn);
        back_to_zhushou = (ImageView) findViewById(R.id.back_to_zhushou);
        back_to_zhushou.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(IatDeActivity.this, MainActivity.class);
                intent.putExtra("main",3);
                startActivity(intent);
               finish();
            }
        });
        fanyiBtn.setOnClickListener(this);
        adapter = new TranslateAdapter(this, list, trslist);
        imm = (InputMethodManager) this.getSystemService(INPUT_METHOD_SERVICE);
        translateList.setAdapter(adapter);
        speek.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] speaklanguage = getResources().getStringArray(speaklanguages);
                if (speaklanguage[position].equals("英语")) {
                    speaklg = "en_us";
                } else if (speaklanguage[position].equals("中文")) {
                    speaklg = "mandarin";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        choselg.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] choselanguage = getResources().getStringArray(chose_languages);
                laguage =  choselanguage[position];       }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /**
     * 初始化Layout。
     */
    private void initLayout() {
        findViewById(R.id.iat_recognize).setOnClickListener(IatDeActivity.this);
        // 目前仅支持在线听写
        //云端在线识别
        mEngineType = SpeechConstant.TYPE_CLOUD;
    }

    int ret = 0; // 函数调用返回值

    @Override
    public void onClick(View view) {
        if (null == mIat) {
            // 创建单例失败，与 21001 错误为同样原因，参考 http://bbs.xfyun.cn/forum.php?mod=viewthread&tid=9688
            this.showTip("创建对象失败，请确认 libmsc.so 放置正确，且有调用 createUtility 进行初始化");
            return;
        }
        switch (view.getId()) {
            case R.id.iat_recognize:
                // 移动数据分析，收集开始听写事件
                mIatResults.clear();
                FlowerCollector.onEvent(IatDeActivity.this, "iat_recognize");
                // 设置参数
                setParam(speaklg);
                boolean isShowDialog = mSharedPreferences.getBoolean(
                        getString(R.string.pref_key_iat_show), true);

                if (isShowDialog) {
                    // 显示听写对话框
                    mIatDialog.setListener(mRecognizerDialogListener);
                    mIatDialog.show();
                    showTip(getString(R.string.text_begin));
                }
                break;
            case R.id.fanyiBtn:
                input =fanyiInputText.getText().toString();
                query(laguage);
                break;
        }
    }

    /**
     * 初始化监听器。
     */
    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            Log.d(TAG, "SpeechRecognizer init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                showTip("初始化失败，错误码：" + code);
            }

        }
    };

    private void printResult(RecognizerResult results) {
        String text = JsonParser.parseIatResult(results.getResultString());

        String sn = null;
        // 读取json结果中的sn字段
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mIatResults.put(sn, text);

        StringBuffer resultBuffer = new StringBuffer();
        for (String key : mIatResults.keySet()) {
            resultBuffer.append(mIatResults.get(key));
        }
        input= resultBuffer.toString();

    }

    /**
     * 听写UI监听器
     */
    private RecognizerDialogListener mRecognizerDialogListener = new RecognizerDialogListener() {
        public void onResult(RecognizerResult results, boolean isLast) {
            if (mTranslateEnable) {
                printTransResult(results);
            } else {
                printResult(results);
                query(laguage);
                mIatDialog.dismiss();
            }

        }

        /**
         * 识别回调错误.
         */
        public void onError(SpeechError error) {
            if (mTranslateEnable && error.getErrorCode() == 14002) {
                showTip(error.getPlainDescription(true) + "\n请确认是否已开通翻译功能");
            } else {
                showTip(error.getPlainDescription(true));
            }
        }

    };


    private void showTip(final String str) {
        mToast.setText(str);
        mToast.show();
    }

    /**
     * 参数设置
     *
     * @return
     */
    public void setParam(String speaklg) {
        // 清空参数
        mIat.setParameter(SpeechConstant.PARAMS, null);

        // 设置听写引擎
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
        // 设置返回结果格式
        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");

        this.mTranslateEnable = mSharedPreferences.getBoolean(this.getString(R.string.pref_key_translate), false);
        if (mTranslateEnable) {
            Log.i(TAG, "translate enable");
            mIat.setParameter(SpeechConstant.ASR_SCH, "1");
            mIat.setParameter(SpeechConstant.ADD_CAP, "translate");
            mIat.setParameter(SpeechConstant.TRS_SRC, "its");
        }
        if (speaklg.equals("en_us")) {
            // 设置语言
            mIat.setParameter(SpeechConstant.LANGUAGE, "en_us");
            mIat.setParameter(SpeechConstant.ACCENT, null);
            if (mTranslateEnable) {
                mIat.setParameter(SpeechConstant.ORI_LANG, "en");
                mIat.setParameter(SpeechConstant.TRANS_LANG, "cn");
            }
        } else if (speaklg.equals("mandarin")) {
            // 设置语言
            mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
            // 设置语言区域
            mIat.setParameter(SpeechConstant.ACCENT, "mandarin");
            if (mTranslateEnable) {
                mIat.setParameter(SpeechConstant.ORI_LANG, "cn");
                mIat.setParameter(SpeechConstant.TRANS_LANG, "en");
            }
        }
        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mIat.setParameter(SpeechConstant.VAD_BOS, mSharedPreferences.getString("iat_vadbos_preference", "4000"));

        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mIat.setParameter(SpeechConstant.VAD_EOS, mSharedPreferences.getString("iat_vadeos_preference", "1000"));

        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIat.setParameter(SpeechConstant.ASR_PTT, mSharedPreferences.getString("iat_punc_preference", "1"));

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mIat.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/iat.wav");
    }

    private void printTransResult(RecognizerResult results) {
        String trans = JsonParser.parseTransResult(results.getResultString(), "dst");
        String oris = JsonParser.parseTransResult(results.getResultString(), "src");

        if (TextUtils.isEmpty(trans) || TextUtils.isEmpty(oris)) {
            showTip("解析结果失败，请确认是否已开通翻译功能。");
        } else {
         //   mResultText.setText("原始语言:\n" + oris + "\n目标语言:\n" + trans);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (null != mIat) {
            // 退出时释放连接
            mIat.cancel();
            mIat.destroy();
        }
    }

    @Override
    protected void onResume() {
        // 开放统计 移动数据统计分析
        FlowerCollector.onResume(IatDeActivity.this);
        FlowerCollector.onPageStart(TAG);
        super.onResume();
    }

    @Override
    protected void onPause() {
        // 开放统计 移动数据统计分析
        FlowerCollector.onPageEnd(TAG);
        FlowerCollector.onPause(IatDeActivity.this);
        super.onPause();
    }

    private void query(String language) {
        // 源语言或者目标语言其中之一必须为中文,目前只支持中文与其他几个语种的互译
       // String from = "中文";
        String to = language;


       // Language langFrom = LanguageUtils.getLangByName(from);
        // 若设置为自动，则查询自动识别源语言，自动识别不能保证完全正确，最好传源语言类型
         Language langFrosm = LanguageUtils.getLangByName("自动");

        Language langTo = LanguageUtils.getLangByName(to);

        TranslateParameters tps = new TranslateParameters.Builder()
                .source("rFB1tXJ9be5JUSoKE93WA1mIWtfUYykB").from(langFrosm).to(langTo).timeout(3000).build();// appkey可以省略

        translator = Translator.getInstance(tps);

        showLoadingView("正在查询");

        translator.lookup(input, new TranslateListener() {

            @Override
            public void onResult(Translate result, String input) {
                TranslateData td = new TranslateData(
                        System.currentTimeMillis(), result);
                list.add(td);
                trslist.add(result);
                adapter.notifyDataSetChanged();
                translateList.setSelection(list.size() - 1);
                dismissLoadingView();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        imm.hideSoftInputFromWindow(
                                fanyiInputText.getWindowToken(), 0);
                    }
                }, 100);
                fanyiInputText.setText("");
            }

            @Override
            public void onError(TranslateErrorCode error) {
                ToastUtils.show("查询错误:" + error.name());
                dismissLoadingView();
            }
        });
    }
    private void showLoadingView(final String text) {
        waitHandler.post(new Runnable() {

            @Override
            public void run() {
                if (progressDialog != null && !progressDialog.isShowing()) {
                    progressDialog.setMessage(text);
                    progressDialog.show();
                }
            }
        });

    }


    private void dismissLoadingView() {
        waitHandler.post(new Runnable() {

            @Override
            public void run() {
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
            }
        });

    }
}
