package org.solovyev.android.threadguard;

import android.os.Looper;
import android.support.annotation.NonNull;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

@SuppressWarnings("unused")
@Aspect
public final class ThreadGuard {

    private ThreadGuard() {
    }

    private static boolean isMainThread() {
        return Looper.getMainLooper() == Looper.myLooper();
    }

    @Pointcut("within(@android.support.annotation.WorkerThread *)")
    public void workerThreadWithinAnnotatedClass() {
    }

    @Pointcut("execution(!synthetic * *(..)) && workerThreadWithinAnnotatedClass()")
    public void workerThreadMethodInsideAnnotatedType() {
    }

    @Pointcut("execution(!synthetic *.new(..)) && workerThreadWithinAnnotatedClass()")
    public void workerThreadConstructorInsideAnnotatedType() {
    }

    @Pointcut("execution(@android.support.annotation.WorkerThread * *(..)) ||"
            + " workerThreadMethodInsideAnnotatedType()")
    public void workerThreadMethod() {
    }

    @Pointcut("execution(@android.support.annotation.WorkerThread *.new(..)) ||"
            + " workerThreadConstructorInsideAnnotatedType()")
    public void workerThreadConstructor() {
    }

    @Pointcut("within(@android.support.annotation.MainThread *)")
    public void mainThreadWithinAnnotatedClass() {
    }

    @Pointcut("execution(!synthetic * *(..)) && mainThreadWithinAnnotatedClass()")
    public void mainThreadMethodInsideAnnotatedType() {
    }

    @Pointcut("execution(!synthetic *.new(..)) && mainThreadWithinAnnotatedClass()")
    public void mainThreadConstructorInsideAnnotatedType() {
    }

    @Pointcut("execution(@android.support.annotation.MainThread * *(..)) ||"
            + " mainThreadMethodInsideAnnotatedType()")
    public void mainThreadMethod() {
    }

    @Pointcut("execution(@android.support.annotation.MainThread *.new(..)) ||"
            + " mainThreadConstructorInsideAnnotatedType()")
    public void mainThreadConstructor() {
    }

    @Before("mainThreadMethod() || mainThreadConstructor()")
    public void mainThreadExecute(@NonNull JoinPoint jp) {
        if (!isMainThread()) {
            throw new AssertionError("Must be main thread");
        }
    }

    @Before("workerThreadMethod() || workerThreadConstructor()")
    public void workerThreadExecute(@NonNull JoinPoint jp) {
        if (isMainThread()) {
            throw new AssertionError("Must be worker thread");
        }
    }
}
