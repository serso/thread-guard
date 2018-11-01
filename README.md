# Description
Protects against calling methods on wrong threads using AspectJ and Android's
`@MainThread`/`@WorkerThread` annotations.

# Usage
Set up AspectA as described here: https://github.com/serso/aspecta.

Import Thread Guard library to your project:
```groovy
dependencies {
  implementation 'org.solovyev.android.threadguard:lib:1.0.1'
}
```

Optionally, you can specify how to penalize the violations:
```java
package org.example.android;

class App extends Application {

    @Override
    void onCreate() {
        ThreadGuard.penaltyDeath();
        // or
        ThreadGuard.penaltyLog();        
    }
}
```
