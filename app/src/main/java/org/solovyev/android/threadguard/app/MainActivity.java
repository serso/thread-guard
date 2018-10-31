package org.solovyev.android.threadguard.app;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.annotation.WorkerThread;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(@Nullable Bundle state) {
        super.onCreate(state);

        setContentView(R.layout.activity_main);
        findViewById(R.id.call_main_on_main).setOnClickListener(this);
        findViewById(R.id.call_main_on_worker).setOnClickListener(this);
        findViewById(R.id.call_worker_on_worker).setOnClickListener(this);
        findViewById(R.id.call_worker_on_main).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.call_main_on_main:
                toBeCalledOnMainThread();
                return;
            case R.id.call_main_on_worker:
                AsyncTask.SERIAL_EXECUTOR.execute(new Runnable() {
                    @Override
                    public void run() {
                        toBeCalledOnMainThread();
                    }
                });
                return;
            case R.id.call_worker_on_worker:
                AsyncTask.SERIAL_EXECUTOR.execute(new Runnable() {
                    @Override
                    public void run() {
                        toBeCalledOnWorkerThread();
                    }
                });
                return;
            case R.id.call_worker_on_main:
                toBeCalledOnWorkerThread();
                return;
        }
    }

    @VisibleForTesting
    @MainThread
    void toBeCalledOnMainThread() {
    }

    @VisibleForTesting
    @WorkerThread
    void toBeCalledOnWorkerThread() {
    }
}
