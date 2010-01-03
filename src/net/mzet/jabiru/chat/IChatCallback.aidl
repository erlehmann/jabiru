package net.mzet.jabiru.chat;

interface IChatCallback {
	void opened(String jabberid);
	void messages(String jabberid);
}
