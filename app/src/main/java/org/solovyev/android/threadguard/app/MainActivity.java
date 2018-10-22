package org.solovyev.android.threadguard.app;

import android.support.annotation.MainThread;
import android.support.annotation.VisibleForTesting;
import android.support.annotation.WorkerThread;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @VisibleForTesting
    @MainThread
    void toBeCalledOnMainThread() {}

    @VisibleForTesting
    @WorkerThread
    void toBeCalledOnWorkerThread() {}
}
