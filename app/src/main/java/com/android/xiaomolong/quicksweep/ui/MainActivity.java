package com.android.xiaomolong.quicksweep.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.xiaomolong.quicksweep.R;
import com.android.xiaomolong.quicksweep.zxing.camera.CameraManager;
import com.android.xiaomolong.quicksweep.zxing.decoding.CaptureActivityHandler;
import com.android.xiaomolong.quicksweep.zxing.decoding.InactivityTimer;
import com.android.xiaomolong.quicksweep.zxing.view.ViewfinderView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.io.IOException;
import java.util.Vector;

public class MainActivity extends Activity implements Callback, OnClickListener {

    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.10f;
    private boolean vibrate;
    private Button btn_more;
    LinearLayout more_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CameraManager.init(getApplication());
        more_layout = (LinearLayout) findViewById(R.id.more_layout);
        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
        btn_more = (Button) this.findViewById(R.id.btn_more);
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
        // findViewById(R.id.button1).setOnClickListener(new OnClickListener() {
        //
        // @Override
        // public void onClick(View v) {
        // // startActivity(new Intent(getApplicationContext(),
        // // WifiListActivity.class));
        // }
        // WifiAdmin wifiAdmin = new WifiAdmin(getApplicationContext());
        // wifiAdmin.openWifi();
        // wifiAdmin.addNetwork(wifiAdmin.CreateWifiInfo(
        // "TP-LINK_PocketAP_7FE37E", "iamwuxiaolong", 3));
        // WifiManager wifiManager = (WifiManager)
        // getSystemService(Context.WIFI_SERVICE);
        // if (wifiManager.startScan()) // 扫描可用的无线网络
        // {
        // List<ScanResult> scanResultList = wifiManager
        // .getScanResults();
        // if (scanResultList != null) {
        // for (int i = 0; i < scanResultList.size(); i++) {
        // Log.d("wxl", i + "==" + scanResultList.get(i).SSID);
        // }
        // }
        // }
        // }
        // });
        findViewById(R.id.btn_more).setOnClickListener(this);
        findViewById(R.id.newqs).setOnClickListener(this);
        findViewById(R.id.more).setOnClickListener(this);
        findViewById(R.id.exit).setOnClickListener(this);
//        Log.d("wxl", "getInfo==" + getInfo());
    }

    private String getInfo() {
        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        String maxText = info.getMacAddress();
        String ipText = intToIp(info.getIpAddress());
        String status = "";
        Log.d("wxl", "wifi.getWifiState()==" + wifi.getWifiState());
        if (wifi.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
            status = "WIFI_STATE_ENABLED";
        }
        String ssid = info.getSSID();
        int networkID = info.getNetworkId();
        int speed = info.getLinkSpeed();
        return "mac：" + maxText + "\n\r"
                + "ip：" + ipText + "\n\r"
                + "wifi status :" + status + "\n\r"
                + "ssid :" + ssid + "\n\r"
                + "net work id :" + networkID + "\n\r"
                + "connection speed:" + speed + "\n\r"
                ;
    }

    private String intToIp(int ip) {
        return (ip & 0xFF) + "." + ((ip >> 8) & 0xFF) + "." + ((ip >> 16) & 0xFF) + "."
                + ((ip >> 24) & 0xFF);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_more:
                more_layout
                        .setVisibility(more_layout.getVisibility() == View.VISIBLE ? View.GONE
                                : View.VISIBLE);
                break;
            case R.id.newqs:
                more_layout
                        .setVisibility(more_layout.getVisibility() == View.VISIBLE ? View.GONE
                                : View.VISIBLE);
                startActivity(new Intent(getApplicationContext(),
                        WifiListActivity.class));
                break;
            case R.id.more:
                more_layout
                        .setVisibility(more_layout.getVisibility() == View.VISIBLE ? View.GONE
                                : View.VISIBLE);
                startActivity(new Intent(getApplicationContext(),
                        MoreActivity.class));
                break;
            case R.id.exit:
                finish();
                System.exit(0);
                break;

            default:
                break;
        }

    }

    @SuppressWarnings("deprecation")
    protected void onResume() {
        super.onResume();
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;

        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;

        // quit the scan view
        // btn_more.setOnClickListener(new OnClickListener() {
        //
        // @Override
        // public void onClick(View v) {
        // MainActivity.this.finish();
        // }
        // });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    /**
     * Handler scan result
     *
     * @param result
     * @param barcode
     */
    public void handleDecode(Result result, Bitmap barcode) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        String resultString = result.getText();
        // FIXME
        if (resultString.equals("")) {
            Toast.makeText(MainActivity.this, "Scan failed!",
                    Toast.LENGTH_SHORT).show();
        } else {
            // System.out.println("Result:"+resultString);

            Intent intent = new Intent();
            intent.setClass(MainActivity.this, SuccessActivity.class);
            intent.putExtra("result", resultString);
            startActivity(intent);

        }
        // MainActivity.this.finish();
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats,
                    characterSet);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;

    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();

    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(
                    R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final OnCompletionListener beepListener = new OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

}
