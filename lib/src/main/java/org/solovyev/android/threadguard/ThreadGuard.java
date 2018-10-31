package org.solovyev.android.threadguard;

import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * <p>
 * Injects checks in methods to detect those that are called on a wrong thread:
 * <ul>
 * <li>Methods annotated with {@link android.support.annotation.MainThread} must be called only on
 * the main application thread</li>
 * <li>Methods annotated with {@link android.support.annotation.WorkerThread} must be called only
 * on a worker thread</li>
 * <li>Methods annotated with {@link android.support.annotation.AnyThread} can be called on any
 * thread</li>
 * </ul>
 * </p>
 *
 * <p>The aspect can be configured to perform different actions when a thread violation occurs.
 * Notable the app crashes on {@link #penaltyDeath()}. {@link #penaltyLog()} prints out the
 * violation into the system log.
 * </p>
 */
@SuppressWarnings("unused")
@Aspect
public final class ThreadGuard {

    @NonNull
    private static final String TAG = "ThreadGuard";
    @NonNull
    private static volatile Penalty sPenalty = Penalty.DEATH;

    private enum Penalty {
        DEATH,
        LOG
    }

    private ThreadGuard() {
    }

    public static void penaltyDeath() {
        sPenalty = Penalty.DEATH;
    }

    public static void penaltyLog() {
        sPenalty = Penalty.LOG;
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
        if (isMainThread()) return;
        onThreadViolation("Must be main thread");
    }

    private static boolean isMainThread() {
        return Looper.getMainLooper() == Looper.myLooper();
    }

    private void onThreadViolation(@NonNull String msg) {
        final Penalty penalty = sPenalty;
        final WrongThreadException e = new WrongThreadException(msg);
        switch (penalty) {
            case DEATH:
                throw e;
            case LOG:
                Log.d(TAG, getStackTrace(e));
                return;
            default:
                throw new IllegalStateException("Unsupported penalty: " + penalty);
        }
    }

    @NonNull
    private static String getStackTrace(@NonNull Exception e) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        pw.flush();
        pw.close();
        return sw.toString();
    }

    @Before("workerThreadMethod() || workerThreadConstructor()")
    public void workerThreadExecute(@NonNull JoinPoint jp) {
        if (!isMainThread()) return;
        onThreadViolation("Must be worker thread");
    }
}
