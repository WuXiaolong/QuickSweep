package com.android.xiaomolong.quicksweep.ui;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.xiaomolong.quicksweep.R;

public class WifiListActivity extends Activity implements OnClickListener {
	ListView mListView;
	List<ScanResult> scanResultList;
	private LayoutInflater mInflater;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wifi_list);

		LinearLayout left = (LinearLayout) findViewById(R.id.left);
		left.setOnClickListener(this);
		TextView title = (TextView) findViewById(R.id.title);
		title.setText("Wifi列表");
		LinearLayout right = (LinearLayout) findViewById(R.id.right);
		right.setVisibility(View.VISIBLE);
		right.setOnClickListener(this);
		TextView right_txt = (TextView) findViewById(R.id.right_txt);
		right_txt.setText("刷新");

		mListView = (ListView) findViewById(R.id.mListView);
		mInflater = LayoutInflater.from(getApplicationContext());
		getScanResults();
	}

	private void getScanResults() {
		WifiAdmin wifiAdmin = new WifiAdmin(getApplicationContext());
		wifiAdmin.openWifi();
		WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

		// 扫描可用的无线网络
		if (wifiManager.startScan()) {
			scanResultList = wifiManager.getScanResults();
			if (scanResultList != null) {
				for (int i = 0; i < scanResultList.size(); i++) {
					Log.d("wxl", i + "==" + scanResultList.get(i).SSID);
				}
				DataAdapter dataAdapter = new DataAdapter();
				mListView.setAdapter(dataAdapter);
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.left:
			finish();
			break;
		case R.id.right:
			getScanResults();
			break;

		default:
			break;
		}

	}

	class DataAdapter extends BaseAdapter {
		DataAdapter() {
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return scanResultList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return scanResultList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {

				holder = new ViewHolder();

				convertView = mInflater.inflate(R.layout.wifi_list_item, null);
				holder.SSID = (TextView) convertView.findViewById(R.id.SSID);
				convertView.setTag(holder);

			} else {

				holder = (ViewHolder) convertView.getTag();
			}
			holder.SSID.setText(scanResultList.get(position).SSID);
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.setClass(WifiListActivity.this,
							SetPasswordActivity.class);
					intent.putExtra("ssid", scanResultList.get(position).SSID);
					startActivity(intent);

				}
			});

			return convertView;
		}

		class ViewHolder {
			public TextView SSID;
		}
	}

}
