package net.mzet.jabiru.roster;

interface IRosterCallback {
	void connectOk();
	void connectFail();
	void rosterChanged();
	void disconnect();
}
