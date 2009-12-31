package net.mzet.jabiru.service;

import net.mzet.jabiru.roster.RosterItem;
import net.mzet.jabiru.roster.IRosterCallback;

interface IRosterConnection {
	void connect();
	void disconnect();
	boolean isLogged();
	
	List<String> getRosterGroups();
	List<RosterItem> getRosterItem(String jabberid);
	List<RosterItem> getRosterItems(String group);
	
	void registerCallback(IRosterCallback callback);
	void unregisterCallback(IRosterCallback callback);
}
