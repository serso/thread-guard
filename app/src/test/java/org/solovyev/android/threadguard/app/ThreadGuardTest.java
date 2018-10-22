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
