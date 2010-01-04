package net.mzet.jabiru.chat;

import java.util.ArrayList;
import java.util.HashMap;

import net.mzet.jabiru.R;
import net.mzet.jabiru.service.IChatConnection;
import net.mzet.jabiru.service.JabberService;
import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

public class ChatActivity extends ListActivity {

	static HashMap<String,ChatListAdapter> listAdapters = new HashMap<String,ChatListAdapter>();
	
	private ServiceConnection serviceConnection;
	private Intent serviceIntent;
	private ChatAdapter serviceAdapter;
	
	private IChatCallback.Stub callback;
	private Handler handler = new Handler();
	private String jabberid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat);
		
		registerJabberService();
		createCallback();

		jabberid = getIntent().getDataString();
		if(listAdapters.containsKey(jabberid)) {
			setListAdapter(listAdapters.get(jabberid));
		}
		else {
			ChatListAdapter aa = new ChatListAdapter(this, R.layout.chat_item, R.id.chat_item, new ArrayList<ChatItem>());
			listAdapters.put(jabberid, aa);
			setListAdapter(aa);
		}
		setTitle("Chatting with " + jabberid);
		
		findViewById(R.id.chat_close).setOnClickListener(createCloseListener());
		findViewById(R.id.chat_message).setOnKeyListener(createEditorListener());
	}

	@Override
	protected void onPause() {
		super.onPause();
		if(serviceAdapter != null) {
			serviceAdapter.unregisterCallback(callback);
		}
		unbindJabberService();
	}

	@Override
	protected void onResume() {
		super.onResume();
		bindJabberService();
	}
	
	private void registerJabberService() {
		serviceIntent = new Intent(this, JabberService.class);
		serviceIntent.setAction("net.mzet.jabiru.JABBERSERVICE");
		serviceIntent.setData(Uri.parse("chat"));
		serviceConnection = new ServiceConnection() {

			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				serviceAdapter = new ChatAdapter(IChatConnection.Stub.asInterface(service));
				serviceAdapter.registerCallback(callback, jabberid);
				loadQueue();
			}

			@Override
			public void onServiceDisconnected(ComponentName name) {
			}
		};
	}
	
	private void bindJabberService() {
		bindService(serviceIntent, serviceConnection, 0);
	}
	
	private void unbindJabberService() {
		unbindService(serviceConnection);
	}

	private void loadQueue() {
		handler.post(new Runnable() {
			@Override
			public void run() {
				ArrayList<ChatItem> queue = (ArrayList<ChatItem>) serviceAdapter.getQueueMessages(jabberid);
				if(queue == null) {
					//System.out.println("about to open");
					serviceAdapter.open(jabberid);
					//System.out.println("think it's open");
					queue = (ArrayList<ChatItem>) serviceAdapter.getQueueMessages(jabberid);
				}
				for(ChatItem message : queue) {
					listAdapters.get(jabberid).add(message);
				}
				listAdapters.get(jabberid).notifyDataSetChanged();
			}
		});
	}
	
	private void createCallback() {
		callback = new IChatCallback.Stub() {
			@Override
			public void opened(String jabberid) throws RemoteException {
			}
			
			@Override
			public void messages(String jabberid) throws RemoteException {
				loadQueue();
			}
		};
	}
	
	public View.OnClickListener createCloseListener() {
		return new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				serviceAdapter.close(jabberid);
				listAdapters.remove(jabberid);
				if(listAdapters.size() == 0) {
					finish();
				}
			}
		};
	}
	
	public View.OnKeyListener createEditorListener() {
		return new View.OnKeyListener() {
			@Override
			public boolean onKey(View vie, int keyCode, KeyEvent event) {
				if(event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
					EditText edit = (EditText) findViewById(R.id.chat_message);
					String body = edit.getText().toString();
					ChatItem chi = new ChatItem(body);
					chi.setDirection(ChatItem.DIR_TO);
					serviceAdapter.sendMessage(jabberid, body);
					listAdapters.get(jabberid).add(chi);
					edit.setText(null);
					
					return true;
				}
				return false;
			}
		};
	}
	
}
