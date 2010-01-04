package net.mzet.jabiru.service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import net.mzet.jabiru.R;
import net.mzet.jabiru.chat.ChatActivity;
import net.mzet.jabiru.chat.ChatItem;
import net.mzet.jabiru.chat.ChatSession;
import net.mzet.jabiru.chat.IChatCallback;
import net.mzet.jabiru.roster.IRosterCallback;
import net.mzet.jabiru.roster.RosterItem;

import org.jivesoftware.smack.XMPPException;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.preference.PreferenceManager;

public class JabberService extends Service {
	private IRosterConnection.Stub rosterConnection;
	private IChatConnection.Stub chatConnection;
	private JabberConnection jabberConnection;
	private RemoteCallbackList<IRosterCallback> rosterCallbacks = new RemoteCallbackList<IRosterCallback>(); 
	private RemoteCallbackList<IChatCallback> chatCallbacks = new RemoteCallbackList<IChatCallback>(); 
	private IServiceCallback callback;
	private ConcurrentHashMap<String,ChatSession> chats;
	private String activeJID;
	private int nextChatID = 0;
	
	@Override
	public void onCreate() {
		super.onCreate();

		createRosterConnection();
		createChatConnection();
		createCallback();
		jabberConnection = new JabberConnection();
		jabberConnection.registerServiceCallback(callback);
		chats = new ConcurrentHashMap<String,ChatSession>();
	}

	@Override
	public IBinder onBind(Intent intent) {
		if(intent.getDataString() != null && intent.getDataString().equals("chat")) {
			return chatConnection;
		}
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

			@Override
			public void presenceChanged(String jabberid) {
				int n = rosterCallbacks.beginBroadcast();
				for(int i = 0;i < n;i++) {
					try {
						rosterCallbacks.getBroadcastItem(i).presenceChanged(jabberid);
					}
					catch(RemoteException e) {
						e.printStackTrace();
					}
				}
				rosterCallbacks.finishBroadcast();
			}

			@Override
			public void newMessage(String jabberid, String body, Date time) {
				ChatSession chs;
				ChatItem chi = new ChatItem(body, time);
				if(chats.containsKey(jabberid)) {
					chs = chats.get(jabberid);
					chs.queueAdd(chi);
				}
				else {
					chs = new ChatSession(nextChatID++, jabberid, ChatSession.NORMAL, "");
					chs.queueAdd(chi);
					chats.put(jabberid, chs);
					int n;
					n = rosterCallbacks.beginBroadcast();
					for(int i = 0;i < n;i++) {
						try {
							rosterCallbacks.getBroadcastItem(i).chatOpened(jabberid);
						}
						catch(RemoteException e) {
							e.printStackTrace();
						}
					}
					rosterCallbacks.finishBroadcast();
					n = chatCallbacks.beginBroadcast();
					for(int i = 0;i < n;i++) {
						try {
							chatCallbacks.getBroadcastItem(i).opened(jabberid);
						}
						catch(RemoteException e) {
							e.printStackTrace();
						}
					}
					chatCallbacks.finishBroadcast();
				}
				int n = chatCallbacks.beginBroadcast();
				for(int i = 0;i < n;i++) {
					try {
						chatCallbacks.getBroadcastItem(i).messages(jabberid);
					}
					catch(RemoteException e) {
						e.printStackTrace();
					}
				}
				chatCallbacks.finishBroadcast();
				
				if(activeJID == null || !activeJID.equals(jabberid)) {
					createNotify(jabberid, body);
				}
			}
		};
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

			@Override
			public List<String> getRosterGroups() throws RemoteException {
				return jabberConnection.getRosterGroups();
			}

			@Override
			public List<RosterItem> getRosterItems(String group) throws RemoteException {
				return jabberConnection.getRosterItems(group);
			}

			@Override
			public List<RosterItem> getRosterItem(String jabberid)
					throws RemoteException {
				return jabberConnection.getRosterItem(jabberid);
			}
		};
	}
	
	public void createChatConnection() {
		chatConnection = new IChatConnection.Stub() {
			
			@Override
			public void unregisterCallback(IChatCallback callback)
					throws RemoteException {
				chatCallbacks.unregister(callback);
				activeJID = null;
			}
			
			@Override
			public void registerCallback(IChatCallback callback, String jabberid) throws RemoteException {
				chatCallbacks.register(callback);
				activeJID = jabberid;
			}
			
			@Override
			public List<ChatItem> getQueueMessages(String jabberid)
					throws RemoteException {
				if(chats.containsKey(jabberid)) {
					return chats.get(jabberid).queueGet();
				}
				return null;
			}
			
			@Override
			public List<String> getChats() throws RemoteException {
				return null;
			}

			@Override
			public void close(String jabberid) throws RemoteException {
				if(chats.containsKey(jabberid)) {
					chats.remove(jabberid);
				}
			}

			@Override
			public void sendMessage(String jabberid, String body)
					throws RemoteException {
				jabberConnection.sendMessage(jabberid, body);
			}

			@Override
			public void open(String jabberid) throws RemoteException {
				if(!chats.containsKey(jabberid)) {
					ChatSession chs = new ChatSession(nextChatID++, jabberid, ChatSession.NORMAL, "");
					chats.put(jabberid, chs);
				}
					
			}

			@Override
			public int getID(String jabberid) throws RemoteException {
				if(!chats.containsKey(jabberid)) {
					return chats.get(jabberid).getID();
				}
				return -1;
			}
		};
	}
	
	public void createNotify(String jabberid, String body) {
		String from = jabberid;
		
		String title = "New message from " + from;
		Notification notification = new Notification(R.drawable.icon_message, title, System.currentTimeMillis());
		
		Intent intent = new Intent(this, ChatActivity.class);
		intent.setData(Uri.parse(jabberid));
		
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
		notification.setLatestEventInfo(this, title, body, pendingIntent);
		notification.flags = Notification.FLAG_AUTO_CANCEL | Notification.FLAG_ONLY_ALERT_ONCE;
		notification.number = chats.get(jabberid).queueSize();
		
		((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).notify(chats.get(jabberid).getID(), notification);
	}
	
	public void connect() {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);		
		final String[] jabberid = sp.getString("account_jabberid", "@").split("@");
		final String password = sp.getString("account_password", "");
		
		(new Thread() {
			public void run() {
				try {
					if(jabberConnection.connect(jabberid.length > 1 ? jabberid[1] : "", jabberid.length > 0 ? jabberid[0] : "", password)) {
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
