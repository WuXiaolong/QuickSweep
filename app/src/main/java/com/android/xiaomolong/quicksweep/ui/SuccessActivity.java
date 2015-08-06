package com.android.xiaomolong.quicksweep.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.xiaomolong.quicksweep.R;

public class SuccessActivity extends Activity implements OnClickListener {
    String result;
    String results[];
    String mSSID, password;
    LinearLayout result_layout;
    WifiAdmin wifiAdmin;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.success);

        LinearLayout left = (LinearLayout) findViewById(R.id.left);
        left.setOnClickListener(this);
        TextView title = (TextView) findViewById(R.id.title);
        title.setText("快扫");
        result_layout = (LinearLayout) findViewById(R.id.result_layout);

        if (this.getIntent() != null) {
            result = this.getIntent().getStringExtra("result");
            results = result.split(",");
            try {
                mSSID = results[0];
                password = results[1];
            } catch (Exception e) {
                Toast.makeText(SuccessActivity.this, "请使用快扫生成的二维码",
                        Toast.LENGTH_LONG).show();
                return;
            }
            //

            wifiAdmin = new WifiAdmin(getApplicationContext());
            wifiAdmin.openWifi();
            Log.d("wxl", "wifiAdmin.checkState()=" + wifiAdmin.checkState());
            Log.d("wxl", "wifiAdmin.getSSID()=" + wifiAdmin.getSSID());
            Log.d("wxl", "wSSID()=" + mSSID);
            int wifiState = wifiAdmin.checkState();
            /**
             * 判断当前是否WiFi连接
             * 如果有，判断是否正在连接一样
             */
            if (wifiState == WifiManager.WIFI_STATE_ENABLED) {
                String ssid = wifiAdmin.getSSID();
                if (TextUtils.equals(ssid, "\"" + mSSID + "\"")) {
                    new AlertDialog.Builder(SuccessActivity.this)
                            .setMessage("当前已经连接WiFi，是否连接XXX")
                            .setPositiveButton("连接", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    connectWiFi();
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                }
            } else {
                connectWiFi();
            }


        }

    }

    private void connectWiFi() {
        boolean isConnect = wifiAdmin.addNetwork(wifiAdmin
                .CreateWifiInfo(mSSID, password, 3));
        if (isConnect) {
            result_layout.setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(SuccessActivity.this, "连接失败", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left:
                finish();
                break;
            default:
                break;
        }

    }
}
