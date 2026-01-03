// INPPS4.aidl
package com.npdep.npps4;

// Declare any non-default types here with import statements
parcelable ConsoleText;

interface INPPS4 {
    void shutdown();
	ConsoleText pollConsole();
}
