package org.solovyev.android.threadguard;

/**
 * Thrown to indicate that a method has been called on a wrong thread.
 */
public class WrongThreadException extends RuntimeException {

    WrongThreadException(String message) {
        super(message);
    }
}
