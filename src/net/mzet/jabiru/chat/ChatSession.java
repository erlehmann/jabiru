package net.mzet.jabiru.chat;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ChatSession {
	public static final int NORMAL = 0;
	public static final int MUC = 1;
	public static final int MUC_PM = 2;
	
	private ConcurrentHashMap<String,ChatSession> childs;
	ConcurrentLinkedQueue<ChatItem> queue;
	private String jabberid;
	private String name;
	private int type;
	private int id;
	
	public ChatSession(int id, String jabberid, int type, String name) {
		this.id = id;
		this.jabberid = jabberid;
		this.type = type;
		this.childs = new ConcurrentHashMap<String,ChatSession>();
		this.queue = new ConcurrentLinkedQueue<ChatItem>();
		this.name = name;
	}
	
	public int getID() {
		return id;
	}
	
	public String getJabberID() {
		return jabberid;
	}
		
	public int getType() {
		return type;
	}

	public String getName() {
		return name;
	}

	public ConcurrentHashMap<String,ChatSession> getChilds() {
		return this.childs;
	}
	
	public void queueAdd(ChatItem item) {
		queue.add(item);
	}
	
	public int queueSize() {
		return queue.size();
	}
	
	public ArrayList<ChatItem> queueGet() {
		ArrayList<ChatItem> chil = new ArrayList<ChatItem>(); 
		ChatItem chi;
		
		while((chi = queue.poll()) != null) {
			chil.add(chi);
		}
		
		return chil;
	}
}
