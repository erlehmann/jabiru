package net.mzet.jabiru.service;

import net.mzet.jabiru.chat.IChatCallback;
import net.mzet.jabiru.chat.ChatItem;

interface IChatConnection {
	List<String> getChats();
	List<ChatItem> getQueueMessages(String jabberid);
	void sendMessage(String jabberid, String body);
	void open(String jabberid);
	void close(String jabberid);
	
	void registerCallback(IChatCallback callback);
	void unregisterCallback(IChatCallback callback);
}
