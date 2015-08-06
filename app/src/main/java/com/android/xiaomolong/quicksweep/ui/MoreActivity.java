package com.android.xiaomolong.quicksweep.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.xiaomolong.quicksweep.R;

public class MoreActivity extends Activity implements OnClickListener {
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.more);

		LinearLayout left = (LinearLayout) findViewById(R.id.left);
		left.setOnClickListener(this);
		TextView title = (TextView) findViewById(R.id.title);
		title.setText("更多设置");

		findViewById(R.id.feedback).setOnClickListener(this);
		findViewById(R.id.recommended).setOnClickListener(this);
		findViewById(R.id.aboutus).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.left:
			finish();
			break;
		case R.id.feedback:
			Intent email = new Intent(android.content.Intent.ACTION_SEND);
			email.setType("plain/text");
			String[] emailReciver = new String[] { "1413129987@qq.com" };

			String emailSubject = "从问道分享来的文章";
			String emailBody = "texttexttexttext";
			// 设置邮件默认地址
			email.putExtra(android.content.Intent.EXTRA_EMAIL, emailReciver);
			// 设置邮件默认标题
			email.putExtra(android.content.Intent.EXTRA_SUBJECT, emailSubject);
			// 设置要默认发送的内容
			email.putExtra(android.content.Intent.EXTRA_TEXT, emailBody);
			// 调用系统的邮件系统
			startActivity(Intent.createChooser(email, "请选择邮件发送软件"));
			break;
		case R.id.recommended:
			initShareIntent();
			break;
		case R.id.aboutus:
			startActivity(new Intent(getApplicationContext(),
					AboutUsActivity.class));
			break;

		default:
			break;
		}

	}

	private void initShareIntent() {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		List<ResolveInfo> resInfo = getPackageManager().queryIntentActivities(
				intent, 0);
		if (!resInfo.isEmpty()) {
			List<Intent> targetedShareIntents = new ArrayList<Intent>();
			for (ResolveInfo info : resInfo) {
				Intent targeted = new Intent(Intent.ACTION_SEND);
				targeted.setType("text/plain");
				ActivityInfo activityInfo = info.activityInfo;

				// judgments :activityInfo.packageName, activityInfo.name, etc.
				// com.tencent.mm 微信
				// com.qzone 空间
				// com.tencent.WBlog 腾讯微博
				// com.tencent.mobileqq qq
				if (activityInfo.packageName.contains("com.sina.weibo")
						|| activityInfo.packageName.contains("com.tencent.mm")
						|| activityInfo.packageName.contains("com.qzone")
						|| activityInfo.packageName
								.contains("com.tencent.WBlog")) {
					targeted.putExtra(
							Intent.EXTRA_TEXT,
							"我发现一款很实用的应用【快扫】。当您进入商场，看到提供一个免费的WiFi，却无奈不知道密码，还需要咨询柜台，快扫帮您解决这个尴尬，扫一扫，快速自动连接WIFI。");
					targeted.setPackage(activityInfo.packageName);
					targetedShareIntents.add(targeted);
				}

			}
			Intent chooserIntent = Intent.createChooser(
					targetedShareIntents.remove(0), "选择");
			if (chooserIntent == null) {
				return;
			}
			chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
					targetedShareIntents.toArray(new Parcelable[] {}));
			try {
				startActivity(chooserIntent);
			} catch (android.content.ActivityNotFoundException ex) {
				Toast.makeText(this, "Can't find sharecomponent to share",
						Toast.LENGTH_SHORT).show();
			}
		}
	}
}
