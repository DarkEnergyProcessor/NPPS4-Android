// INPPS4.aidl
package com.npdep.npps4;

// Declare any non-default types here with import statements
parcelable ConsoleText;

interface INPPS4 {
	boolean isRunning();
    void shutdown();
	ConsoleText pollConsole();
}
