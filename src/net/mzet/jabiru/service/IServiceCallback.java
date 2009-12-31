package net.mzet.jabiru.service;

interface IServiceCallback {
	void rosterChanged();
	void presenceChanged(String jabberid);
}
