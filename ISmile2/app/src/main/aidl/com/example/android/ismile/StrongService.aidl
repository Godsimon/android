// StrongService.aidl
package com.example.android.ismile;

// Declare any non-default types here with import statements

interface StrongService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
     void startService();
     void stopService();

    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);
}
