package net.mzet.jabiru.roster;

import net.mzet.jabiru.roster.RosterItem;

interface IRosterCallback {
	void connectOk();
	void connectFail();
	void rosterChanged();
	void chatOpened(String jabberid);
	void presenceChanged(String jabberid);
	void disconnect();
}
