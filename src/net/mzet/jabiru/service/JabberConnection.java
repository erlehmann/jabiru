package net.mzet.jabiru.service;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

public class JabberConnection {
	private final ConnectionConfiguration xmppConfig;
	private final XMPPConnection xmppConnection;
	
	public JabberConnection() {
		this.xmppConfig = new ConnectionConfiguration("domain", 5222);
		this.xmppConfig.setReconnectionAllowed(true);
		this.xmppConnection = new XMPPConnection(xmppConfig);
	}
	
	public boolean connect() throws XMPPException {
		try {
			xmppConnection.connect();
			xmppConnection.login("user", "password", "Jabiru");
		}
		catch(Exception e) {
			return false;
		}
		return true;
	}

	public void disconnect() {
		xmppConnection.disconnect();
	}

	public boolean isLogged() {
		return xmppConnection != null && xmppConnection.isConnected() && xmppConnection.isAuthenticated(); 
	}
}
