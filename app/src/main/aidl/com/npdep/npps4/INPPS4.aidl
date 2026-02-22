// INPPS4.aidl
package com.npdep.npps4;

// Declare any non-default types here with import statements
import com.npdep.npps4.IStateCallbackResult;
parcelable ConsoleText;

interface INPPS4 {
	int getStatus();
	oneway void getStatusAsync(IStateCallbackResult resultCb);
    oneway void shutdown();
	ConsoleText pollConsole();
	@nullable String getLastError();
}
