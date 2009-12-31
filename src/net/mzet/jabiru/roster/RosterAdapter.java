package net.mzet.jabiru.roster;

import java.util.ArrayList;

import android.os.RemoteException;

import net.mzet.jabiru.service.IRosterConnection;

public class RosterAdapter {
	private IRosterConnection serviceStub;
	
	public RosterAdapter(IRosterConnection serviceStub) {
		this.serviceStub = serviceStub;
	}
	
	public void connect() {
		try {
			serviceStub.connect();
		}
		catch(RemoteException e) {
			e.printStackTrace();
		}
	}
	
	public void disconnect() {
		try {
			serviceStub.disconnect();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	public boolean isLogged() {
		try {
			return serviceStub.isLogged();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public ArrayList<String> getRosterGroups() {
		try {
			return (ArrayList<String>) serviceStub.getRosterGroups();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return null;
	}

	public ArrayList<RosterItem> getRosterItem(String jabberid) {
		try {
			return (ArrayList<RosterItem>) serviceStub.getRosterItem(jabberid);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public ArrayList<RosterItem> getRosterItems(String group) {
		try {
			return (ArrayList<RosterItem>) serviceStub.getRosterItems(group);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void registerCallback(IRosterCallback callback) {
		try {
			serviceStub.registerCallback(callback);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	public void unregisterCallback(IRosterCallback callback) {
		try {
			serviceStub.registerCallback(callback);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
}
