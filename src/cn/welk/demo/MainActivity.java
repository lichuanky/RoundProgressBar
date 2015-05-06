package cn.welk.demo;

import java.lang.ref.SoftReference;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import cn.welk.demo.view.RoundProgressBar;

public class MainActivity extends Activity {

    private static final int INTERVAL = 100;
    private static final int DURATION = 100000;

    private static final int MSG_UPDATE_PROGRESSBAR = 0x10;

    private MyHandler mHandler;
    private RoundProgressBar mProgressBar;
    private int mPosition;

    private static class MyHandler extends Handler {

        private SoftReference<MainActivity> softReference;

        public MyHandler(MainActivity activity) {
            softReference = new SoftReference<MainActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MainActivity activity = softReference.get();
            if (activity == null) {
                return;
            }
            switch (msg.what) {
                case MSG_UPDATE_PROGRESSBAR:
                    int progress = (int)((activity.mPosition / (float)DURATION) * 1000.0f);
                    if (activity.mPosition <= DURATION) {
                        activity.mProgressBar.setProgress(progress);
                        activity.mPosition = activity.mPosition + INTERVAL;
                    } else {
                        activity.mPosition = 0;
                    }
                    sendEmptyMessageDelayed(MSG_UPDATE_PROGRESSBAR, INTERVAL);
                    break;
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mHandler = new MyHandler(this);

        mProgressBar = (RoundProgressBar) findViewById(R.id.round_progress);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mHandler.sendEmptyMessage(MSG_UPDATE_PROGRESSBAR);
    }
}
