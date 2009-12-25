package net.mzet.jabiru.roster;

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
