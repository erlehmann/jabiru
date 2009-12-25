package net.mzet.jabiru.service;

import net.mzet.jabiru.roster.IRosterCallback;

import org.jivesoftware.smack.XMPPException;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.preference.PreferenceManager;

public class JabberService extends Service {
	private IRosterConnection.Stub rosterConnection;
	private JabberConnection jabberConnection;
	private RemoteCallbackList<IRosterCallback> rosterCallbacks = new RemoteCallbackList<IRosterCallback>(); 
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		
		String[] jabberid = sp.getString("account_jabberid", "@").split("@");
		String password = sp.getString("account_password", "");
		
		jabberConnection = new JabberConnection(jabberid[1], jabberid[0], password);
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
