package com.example.zx.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baiduNav.BNMainActivity;
import com.example.zx.youjiandroid.R;
import com.iflytek.voice.IatDeActivity;
import com.parities.ParitiesActivity;
import com.todolist.TodoListActivity;
import com.weather.WeatherMainActivity;
import com.weather.gson.Weather;
import com.weather.util.Utility;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class ZhuShouFragment extends Fragment implements View.OnClickListener {
	private LinearLayout beiwanglu,ditu,fanyi,huilv,city,shou_weatherPhoto;

	private TextView show_city,wenduMin,wenduMax,city_time,city_week,weathera,city_riqi,tv_hengxian,tianqi_tv,tv_shangci,dian;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view= inflater.inflate(R.layout.fragment_zhushou, null);
		initviw(view);

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
		String weatherString = prefs.getString("weather", null);
		if (weatherString != null) {
			Weather weather = Utility.handleWeatherResponse(weatherString);
			showWeatherInfo(weather);
		}
		return view;
	}

	private void showWeatherInfo(Weather weather) {
		tv_hengxian.setVisibility(View.VISIBLE);
		tianqi_tv.setVisibility(View.VISIBLE);
		tv_shangci.setVisibility(View.VISIBLE);
		dian.setVisibility(View.VISIBLE);
		String tianqi = weather.now.more.info;
		String date =getWeek(weather.forecastList.get(0).date);
		city_week.setText(date);
		String cityName = weather.basic.cityName;
		String minwendu = weather.forecastList.get(0).temperature.min+ "℃";
		String maxwendu = weather.forecastList.get(0).temperature.max+ "℃";
		String cityTime =weather.basic.update.updateTime.split(" ")[1];
		show_city.setText(cityName);
		wenduMin.setText(minwendu);
		wenduMax.setText(maxwendu);
		city_time.setText(cityTime);
		city_riqi.setText(weather.forecastList.get(0).date);
		weathera.setText(tianqi);
		if(tianqi.equals("中雨")||tianqi.equals("小雨")||tianqi.equals("大雨")) {
			shou_weatherPhoto.setBackgroundResource(R.drawable.weather_bg_yu);
		}else if(tianqi.equals("晴")) {
			shou_weatherPhoto.setBackgroundResource(R.drawable.weather_bg_qing);
		}else if(tianqi.equals("多云")) {
			shou_weatherPhoto.setBackgroundResource(R.drawable.weather_bg_duoyun);
		}else if(tianqi.equals("雨夹雪")) {
			shou_weatherPhoto.setBackgroundResource(R.drawable.weather_bg_yu_andxue);
		}else if(tianqi.equals("阴")) {
			shou_weatherPhoto.setBackgroundResource(R.drawable.weather_bg_yin);
		}else if(tianqi.equals("雪")||tianqi.equals("小雪")||tianqi.equals("大雪")) {
			shou_weatherPhoto.setBackgroundResource(R.drawable.weather_bg_xue);
		}
	}

	private void initviw(View view) {
		beiwanglu=(LinearLayout)view.findViewById(R.id.chose_beiwanglu);
		shou_weatherPhoto=(LinearLayout)view.findViewById(R.id.shou_weatherPhoto);
		ditu = (LinearLayout) view.findViewById(R.id.chose_ditu);
		fanyi = (LinearLayout) view.findViewById(R.id.chose_fanyi);
		huilv = (LinearLayout) view.findViewById(R.id.chose_huilv);
		city = (LinearLayout) view.findViewById(R.id.city_chose);
		show_city = (TextView) view.findViewById(R.id.show_city);
		tv_hengxian = (TextView) view.findViewById(R.id.tv_hengxian);
		tv_shangci = (TextView) view.findViewById(R.id.tv_shangci);
		tianqi_tv = (TextView) view.findViewById(R.id.tianqi_tv);
		dian = (TextView) view.findViewById(R.id.dian);
		wenduMin = (TextView) view.findViewById(R.id.min_wendu);
		wenduMax = (TextView) view.findViewById(R.id.max_wendu);
		city_time = (TextView) view.findViewById(R.id.city_time);
		city_week = (TextView) view.findViewById(R.id.city_week);
		weathera = (TextView) view.findViewById(R.id.weathera);
		city_riqi = (TextView) view.findViewById(R.id.city_riqi);
		beiwanglu.setOnClickListener(this);
		city.setOnClickListener(this);
		huilv.setOnClickListener(this);
		fanyi.setOnClickListener(this);
		ditu.setOnClickListener(this);
	}

	public void onClick(View view) {
		switch (view.getId()){
			case R.id.chose_beiwanglu:
				Intent intent1 = new Intent(getContext(),TodoListActivity.class);
				startActivity(intent1);
				break;
			case R.id.chose_ditu:
				startActivity(new Intent(getContext(), BNMainActivity.class));
				break;
			case R.id.chose_fanyi:
				startActivity(new Intent(getContext(), IatDeActivity.class));
				break;
			case  R.id.chose_huilv:
				startActivity(new Intent(getContext(), ParitiesActivity.class));
				break;
			case R.id.city_chose:
				startActivity(new Intent(getContext(), WeatherMainActivity.class));
				break;
		}
	}


	//将xxxx-xx-xx转换成星期x
	public String getWeek(String sdate) {
		// 再转换为时间
		Date date = strToDate(sdate);
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		// int hour=c.get(Calendar.DAY_OF_WEEK);
		// hour中存的就是星期几了，其范围 1~7
		// 1=星期日 7=星期六，其他类推
		return new SimpleDateFormat("EEEE").format(c.getTime());
	}

	public Date strToDate(String strDate) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		ParsePosition pos = new ParsePosition(0);
		Date strtodate = formatter.parse(strDate, pos);
		return strtodate;
	}
}
