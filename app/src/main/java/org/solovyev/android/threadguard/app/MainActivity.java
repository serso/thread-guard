// Copyright 2018 Sergey Solovyev <se.solovyev@gmail.com>
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

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
