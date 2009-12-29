package net.mzet.jabiru.service;

import java.util.List;

import net.mzet.jabiru.roster.IRosterCallback;
import net.mzet.jabiru.roster.RosterItem;

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
	private IServiceCallback callback;
	
	@Override
	public void onCreate() {
		super.onCreate();

		createRosterConnection();
		createCallback();
		jabberConnection = new JabberConnection();
		jabberConnection.registerServiceCallback(callback);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return rosterConnection;
	}
	
	public void createCallback() {
		callback = new IServiceCallback() {
			
			@Override
			public void rosterChanged() {
				int n = rosterCallbacks.beginBroadcast();
				for(int i = 0;i < n;i++) {
					try {
						rosterCallbacks.getBroadcastItem(i).rosterChanged();
					}
					catch(RemoteException e) {
						e.printStackTrace();
					}
				}
				rosterCallbacks.finishBroadcast();
			}
		};
	}
	
	public void createRosterConnection() {
		rosterConnection = new IRosterConnection.Stub() {
			public void registerCallback(IRosterCallback callback) {
				rosterCallbacks.register(callback);
			}
			
			public void unregisterCallback(IRosterCallback callback) {
				System.out.println("unregister callback");
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

			@Override
			public List<String> getRosterGroups() throws RemoteException {
				return jabberConnection.getRosterGroups();
			}

			@Override
			public List<RosterItem> getRosterItems(String group) throws RemoteException {
				return jabberConnection.getRosterItems(group);
			}
		};
	}
	
	public void connect() {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);		
		final String[] jabberid = sp.getString("account_jabberid", "@").split("@");
		final String password = sp.getString("account_password", "");
		
		(new Thread() {
			public void run() {
				try {
					if(jabberConnection.connect(jabberid.length > 1 ? jabberid[1] : "", jabberid[0], password)) {
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
		System.out.println("connectOk");
		int n = rosterCallbacks.beginBroadcast();
		System.out.println(n);
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
