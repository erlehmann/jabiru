package net.mzet.jabiru.service;

import net.mzet.jabiru.roster.IRosterCallback;

interface IRosterConnection {
	void connect();
	void disconnect();
	boolean isLogged();
	void registerCallback(IRosterCallback callback);
	void unregisterCallback(IRosterCallback callback);
}
