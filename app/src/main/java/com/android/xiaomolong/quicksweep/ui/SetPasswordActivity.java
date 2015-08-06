package com.android.xiaomolong.quicksweep.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.xiaomolong.quicksweep.R;

public class SetPasswordActivity extends Activity implements OnClickListener {
	EditText ssid, password;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.set_password);

		LinearLayout left = (LinearLayout) findViewById(R.id.left);
		left.setOnClickListener(this);
		TextView title = (TextView) findViewById(R.id.title);
		title.setText("输入密钥");
		LinearLayout right = (LinearLayout) findViewById(R.id.right);
		right.setVisibility(View.VISIBLE);
		right.setOnClickListener(this);
		TextView right_txt = (TextView) findViewById(R.id.right_txt);
		right_txt.setText("下一步");

		ssid = (EditText) findViewById(R.id.ssid);
		password = (EditText) findViewById(R.id.password);
		if (this.getIntent() != null) {
			ssid.setText(this.getIntent().getStringExtra("ssid"));
		}
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.left:
			finish();
			break;
		case R.id.right:
			if (!"".equals(password.getText().toString())) {
				Intent intent = new Intent();
				intent.setClass(SetPasswordActivity.this,
						CreateCodeActivity.class);
				intent.putExtra("ssid", ssid.getText().toString());
				intent.putExtra("password", password.getText().toString());
				startActivity(intent);
				finish();
			} else {
				Toast.makeText(SetPasswordActivity.this, "输入密钥",
						Toast.LENGTH_SHORT).show();
			}
			break;

		default:
			break;
		}

	}
}
