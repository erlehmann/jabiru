package net.mzet.jabiru.service;

import net.mzet.jabiru.roster.IRosterCallback;

import org.jivesoftware.smack.XMPPException;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;

public class JabberService extends Service {
	private IRosterConnection.Stub rosterConnection;
	private JabberConnection jabberConnection;
	private RemoteCallbackList<IRosterCallback> rosterCallbacks = new RemoteCallbackList<IRosterCallback>(); 
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		jabberConnection = new JabberConnection();
		createRosterConnection();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return rosterConnection;
	}
	
	public void createRosterConnection() {
		rosterConnection = new IRosterConnection.Stub() {
			public void registerCallback(IRosterCallback callback) {
				rosterCallbacks.register(callback);
			}
			
			public void unregisterCallback(IRosterCallback callback) {
				rosterCallbacks.unregister(callback);
			}
			
			public void connect() throws RemoteException {
				JabberService.this.connect();
			}

			@Override
			public void disconnect() throws RemoteException {
				JabberService.this.disconnect();
			}

			@Override
			public boolean isLogged() throws RemoteException {
				return jabberConnection.isLogged();
			}
		};
	}
	
	public void connect() {
		(new Thread() {
			public void run() {
				try {
					if(jabberConnection.connect()) {
						connectOk();
					}
					else {
						connectFail();
					}
				}
				catch(XMPPException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	public void disconnect() {
		jabberConnection.disconnect();
		int n = rosterCallbacks.beginBroadcast();
		for(int i = 0;i < n;i++) {
			try {
				rosterCallbacks.getBroadcastItem(i).disconnect();
			}
			catch(RemoteException e) {
				e.printStackTrace();
			}
		}
		rosterCallbacks.finishBroadcast();
	}
	
	public void connectOk() {
		int n = rosterCallbacks.beginBroadcast();
		for(int i = 0;i < n;i++) {
			try {
				rosterCallbacks.getBroadcastItem(i).connectOk();
			}
			catch(RemoteException e) {
				e.printStackTrace();
			}
		}
		rosterCallbacks.finishBroadcast();
	}

	public void connectFail() {
		int n = rosterCallbacks.beginBroadcast();
		for(int i = 0;i < n;i++) {
			try {
				rosterCallbacks.getBroadcastItem(i).connectFail();
			}
			catch(RemoteException e) {
				e.printStackTrace();
			}
		}
		rosterCallbacks.finishBroadcast();
	}
}
