package net.mzet.jabiru.chat;

import java.util.List;

import android.os.RemoteException;
import net.mzet.jabiru.service.IChatConnection;

public class ChatAdapter {
	private IChatConnection serviceStub;

	public ChatAdapter(IChatConnection serviceStub) {
		this.serviceStub = serviceStub;
	}
	
	public List<ChatItem> getQueueMessages(String jabberid) {
		try {
			return serviceStub.getQueueMessages(jabberid);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void close(String jabberid) {
		try {
			serviceStub.close(jabberid);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public void open(String jabberid) {
		try {
			serviceStub.open(jabberid);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public void sendMessage(String jabberid, String body) {
		try {
			serviceStub.sendMessage(jabberid, body);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public void registerCallback(IChatCallback callback) {
		try {
			serviceStub.registerCallback(callback);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	public void unregisterCallback(IChatCallback callback) {
		try {
			serviceStub.unregisterCallback(callback);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
}
