package net.mzet.jabiru.service;

import java.util.Date;

interface IServiceCallback {
	void rosterChanged();
	void presenceChanged(String jabberid);
	void newMessage(String jabberid, String body, Date time);
}
