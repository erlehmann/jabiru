package net.mzet.jabiru.chat;

import java.util.Calendar;
import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;

public class ChatItem implements Parcelable {
	public static final int DIR_FROM = 0;
	public static final int DIR_TO = 1;
	
	private int direction;
	private Date time;
	private String nick;
	private String body;
	
	public ChatItem(String body) {
		this(body, null, null);
	}
	
	public ChatItem(String body, Date time) {
		this(body, time, null);
	}
	
	public ChatItem(String body, String nick) {
		this(body, null, nick);
	}
	
	public ChatItem(String body, Date time, String nick) {
		this.time = new Date(time == null ? System.currentTimeMillis() : time.getTime() + Calendar.getInstance().get(Calendar.ZONE_OFFSET) + Calendar.getInstance().get(Calendar.DST_OFFSET));
		this.direction = DIR_FROM;
		this.nick = nick;
		this.body = body;
	}
	
	public ChatItem(Parcel in) {
		nick = in.readString();
		body = in.readString();
		time = (Date) in.readSerializable();
		direction = in.readInt();
	}
	
	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeString(nick);
		out.writeString(body);
		out.writeSerializable(time);
		out.writeInt(direction);
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}
	
	public int getDirection() {
		return direction;
	}
	
	public String getBody() {
		return this.body;
	}
	
	public Date getTime() {
		return this.time;
	}
	
	public String getNick() {
		return this.nick;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@SuppressWarnings("unchecked")
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

		@Override
		public Object createFromParcel(Parcel in) {
			return new ChatItem(in);
		}

		@Override
		public Object[] newArray(int size) {
			return new ChatItem[size];
		}
	};}
