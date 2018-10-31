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

import static junit.framework.Assert.assertTrue;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import android.support.annotation.NonNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RunWith(RobolectricTestRunner.class)
public class ThreadGuardTest {

    private MainActivity mActivity;
    private ExecutorService mBackground;

    @Before
    public void setUp() {
        mActivity = new MainActivity();
        mBackground = Executors.newSingleThreadExecutor();
    }

    @After
    public void tearDown() {
        mBackground.shutdown();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void forbidWorkerThreadInMainThreadMethod()
            throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final Callback callback = mock(Callback.class);
        mBackground.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    mActivity.toBeCalledOnMainThread();
                } catch (Throwable e) {
                    callback.onError(e);
                }
                latch.countDown();
            }
        });
        latch.await(1000L, TimeUnit.MILLISECONDS);
        verify(callback).onError(any(AssertionError.class));
    }

    @Test(expected = AssertionError.class)
    public void forbidMainThreadInWorkerThreadMethod() {
        mActivity.toBeCalledOnWorkerThread();
    }

    @Test
    public void allowWorkerThreadInWorkerThreadMethod()
            throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        mBackground.execute(new Runnable() {
            @Override
            public void run() {
                mActivity.toBeCalledOnWorkerThread();
                latch.countDown();
            }
        });
        assertTrue(latch.await(1000L, TimeUnit.MILLISECONDS));
    }

    @Test
    public void allowMainThreadInMainThreadMethod() {
        mActivity.toBeCalledOnMainThread();
    }

    private interface Callback {
        void onError(@NonNull Throwable e);
    }
}
