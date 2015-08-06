package com.android.xiaomolong.quicksweep.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.xiaomolong.quicksweep.R;
import com.android.xiaomolong.quicksweep.zxing.encoding.EncodingHandler;
import com.google.zxing.WriterException;

public class CreateCodeActivity extends Activity implements OnClickListener {
	String ssid, password;
	String contentString;
	ImageView code_image;
	Bitmap qrCodeBitmap;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_code);

		code_image = (ImageView) findViewById(R.id.code_image);
		LinearLayout left = (LinearLayout) findViewById(R.id.left);
		left.setOnClickListener(this);
		TextView title = (TextView) findViewById(R.id.title);
		title.setText("输入密钥");
		LinearLayout right = (LinearLayout) findViewById(R.id.right);
		right.setVisibility(View.VISIBLE);
		right.setOnClickListener(this);
		TextView right_txt = (TextView) findViewById(R.id.right_txt);
		right_txt.setText("保存");

		if (this.getIntent() != null) {
			ssid = this.getIntent().getStringExtra("ssid");
			password = this.getIntent().getStringExtra("password");
			contentString = ssid + "," + password;
			// 根据字符串生成二维码图片并显示在界面上，第二个参数为图片的大小（350*350）

			try {
				qrCodeBitmap = EncodingHandler.createQRCode(contentString, 350);
			} catch (WriterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			code_image.setImageBitmap(qrCodeBitmap);
		}
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.left:
			finish();
			break;
		case R.id.right:

			saveBitmap(qrCodeBitmap);

			break;

		default:
			break;
		}

	}

	/** 保存方法 */
	public void saveBitmap(Bitmap qrCodeBitmap) {
		Log.e("wxl", "保存图片");
		String SavePath = getSDCardPath() + "/QuickSweep";
		String filepath = SavePath + "/" + ssid + "+.png";
		File path = new File(SavePath);
		File file = new File(filepath);
		if (!path.exists()) {
			path.mkdirs();
		}
		if (file.exists()) {
			file.delete();
		}
		try {
			file.createNewFile();
			FileOutputStream out = new FileOutputStream(file);
			qrCodeBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
			out.flush();
			out.close();
			Log.i("wxl", "已经保存");
			Toast.makeText(CreateCodeActivity.this, "保存成功", Toast.LENGTH_SHORT)
					.show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 获取SDCard的目录路径功能
	 */
	private String getSDCardPath() {
		File sdcardDir = null;
		// 判断SDCard是否存在
		boolean sdcardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
		if (sdcardExist) {
			sdcardDir = Environment.getExternalStorageDirectory();
		}
		return sdcardDir.toString();
	}
}
