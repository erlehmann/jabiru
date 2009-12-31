package net.mzet.jabiru.roster;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import org.jivesoftware.smack.packet.Presence;

import android.os.Parcel;
import android.os.Parcelable;

public class RosterItem implements Parcelable,Comparable<RosterItem> {
	public static final int STATUS_OFFLINE = 0;
	public static final int STATUS_CHAT = 1;   
	public static final int STATUS_ONLINE = 2; 
	public static final int STATUS_AWAY = 3;   
	public static final int STATUS_XA = 4;     
	public static final int STATUS_DND = 5;
    
	private String jabberid;
	private String nick;
	private HashMap<String,Integer> statuses;
	private HashMap<String,String> statusMessages;
	private HashMap<String,Integer> priorities;
	private ArrayList<String> resources;
	private ArrayList<String> groups;
	
	public RosterItem(String jabberid) {
		this.jabberid = jabberid;
		this.groups = new ArrayList<String>();
		this.nick = jabberid;
		this.resources = new ArrayList<String>();
		this.statuses = new HashMap<String,Integer>();
		this.statusMessages = new HashMap<String,String>();
		this.priorities = new HashMap<String,Integer>();
	}
	
	public RosterItem(Parcel in) {
		this.jabberid = in.readString();
		this.nick = in.readString();
		this.groups = new ArrayList<String>(Arrays.asList((String[]) in.readArray(this.getClass().getClassLoader())));
		this.resources = new ArrayList<String>(Arrays.asList((String[]) in.readArray(this.getClass().getClassLoader())));
		this.statuses = new HashMap<String,Integer>();
		this.statusMessages = new HashMap<String,String>();
		this.statuses = new HashMap<String,Integer>();
		
		for(String resource : resources) {
			statuses.put(resource, in.readInt());
			statusMessages.put(resource, in.readString());
			priorities.put(resource, in.readInt());
		}
		if(nick.equalsIgnoreCase("carbik")) {
			System.out.println("parcel out - " + resources.size());
		}
		System.out.println("Parcel out");
	}
	
	@Override
	public void writeToParcel(Parcel out, int flags) {
		System.out.println("Parcel in");
		if(nick.equalsIgnoreCase("carbik")) {
			System.out.println("parcel in - " + resources.size());
		}
		out.writeString(jabberid);
		out.writeString(nick);
		out.writeArray(groups.toArray());
		out.writeArray(resources.toArray());
		for(String resource : resources) {
			out.writeInt(statuses.get(resource));
			out.writeString(statusMessages.get(resource));
			out.writeInt(priorities.get(resource));
		}
	}
	
	public void setNick(String nick) {
		this.nick = nick;
	}
	
	public void setStatus(String resource, Presence presence) {
		int status = STATUS_OFFLINE;
		
		if(presence.getType() == Presence.Type.available) {
			status = STATUS_ONLINE;
			if(presence.getMode() == Presence.Mode.available) {
				status = STATUS_ONLINE;
			}
			else if(presence.getMode() == Presence.Mode.away) {
				status = STATUS_AWAY;
			}
			else if(presence.getMode() == Presence.Mode.chat) {
				status = STATUS_CHAT;
			}
			else if(presence.getMode() == Presence.Mode.dnd) {
				status = STATUS_DND;
			}
			else if(presence.getMode() == Presence.Mode.xa) {
				status = STATUS_XA;
			}
		}
		if(status > 0) {
			statuses.put(resource, status);
			priorities.put(resource, presence.getPriority());
			//System.out.println(presence.getStatus());
			if(presence.getStatus() != null) {
				setStatusMessage(resource, presence.getStatus());
			}
			if(!resources.contains(resource)) {
				resources.add(resource);
				Collections.sort(resources, new PriorityComparator(this));
			}
		}
		else if(status == 0) {
			if(resources.contains(resource)) {
				resources.remove(resource);
			}
			if(statuses.containsKey(resource)) {
				statuses.remove(resource);
			}
			if(priorities.containsKey(resource)) {
				priorities.remove(resource);
			}
			if(statusMessages.containsKey(resource)) {
				statusMessages.remove(resource);
			}
		}
	}
	
	public void setStatusMessage(String resource, String statusMessage) {
		statusMessages.put(resource, statusMessage);
		//System.out.println(resource + "- " + statusMessage);
	}
	
	public String getJabberID() {
		return jabberid;
	}
	
	public String getNick() {
		return nick;
	}
	
	public String getHighestResource() {
		return resources.size() == 0 ? null : resources.get(0);
	}
	
	public int getStatus() {
		return getStatus(getHighestResource());
	}
	
	public int getStatus(String resource) {
		return statuses.containsKey(resource) ? statuses.get(resource) : 0; 
	}
	
	public String getStatusMessage() {
		return getStatusMessage(getHighestResource());
	}

	public String getStatusMessage(String resource) {
		return statusMessages.containsKey(resource) ? statusMessages.get(resource) : null; 
	}
	
	public ArrayList<String> getGroups() {
		return groups;
	}
	
	public HashMap<String,Integer> getPriorites() {
		return priorities;
	}
	
	public HashMap<String,Integer> getStatuses() {
		return statuses;
	}

	@Override
	public int describeContents() {
		return 0;
	}
	
	@SuppressWarnings("unchecked")
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

		@Override
		public Object createFromParcel(Parcel in) {
			return new RosterItem(in);
		}

		@Override
		public Object[] newArray(int size) {
			return new RosterItem[size];
		}
	};
	
	@Override
	public int compareTo(RosterItem item) {
		return this.nick.compareToIgnoreCase((item.getNick()));
	}
}

class PriorityComparator implements Comparator<String> {
	
	private RosterItem rosterItem;
	
	public PriorityComparator(RosterItem rosterItem) {
		this.rosterItem = rosterItem;
	}
	
	@Override
	public int compare(String arg0, String arg1) {
		if(rosterItem.getPriorites().get(arg0) == rosterItem.getPriorites().get(arg0)) {
			return rosterItem.getStatuses().get(arg0).compareTo(rosterItem.getStatuses().get(arg1));
		}
		else {
			return rosterItem.getPriorites().get(arg1).compareTo(rosterItem.getPriorites().get(arg0));
		}
		
	}
	
}