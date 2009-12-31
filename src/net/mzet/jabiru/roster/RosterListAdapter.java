package net.mzet.jabiru.roster;

import java.util.List;
import java.util.Map;

import net.mzet.jabiru.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

public class RosterListAdapter extends SimpleExpandableListAdapter {
	
	private List<? extends List<? extends Map<String, ?>>> rosterList;
	
	public RosterListAdapter(Context context,
			List<? extends Map<String, ?>> groupData, int expandedGroupLayout,
			int collapsedGroupLayout, String[] groupFrom, int[] groupTo,
			List<? extends List<? extends Map<String, ?>>> childData,
			int childLayout, int lastChildLayout, String[] childFrom,
			int[] childTo) {
		super(context, groupData, expandedGroupLayout, collapsedGroupLayout, groupFrom,
				groupTo, childData, childLayout, lastChildLayout, childFrom, childTo);
		rosterList = childData;
	}
	public RosterListAdapter(Context context,
			List<? extends Map<String, ?>> groupData, int expandedGroupLayout,
			int collapsedGroupLayout, String[] groupFrom, int[] groupTo,
			List<? extends List<? extends Map<String, ?>>> childData,
			int childLayout, String[] childFrom, int[] childTo) {
		super(context, groupData, expandedGroupLayout, collapsedGroupLayout, groupFrom,
				groupTo, childData, childLayout, childFrom, childTo);
		rosterList = childData;
	}
	
	public RosterListAdapter(Context context,
			List<? extends Map<String, ?>> groupData, int groupLayout,
			String[] groupFrom, int[] groupTo,
			List<? extends List<? extends Map<String, ?>>> childData,
			int childLayout, String[] childFrom, int[] childTo) {
		super(context, groupData, groupLayout, groupFrom, groupTo, childData,
				childLayout, childFrom, childTo);
		rosterList = childData;
	}
	
	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		RosterItem rosterItem = (RosterItem) rosterList.get(groupPosition).get(childPosition).get("item");
		
		View childView = convertView == null ? newChildView(isLastChild, parent) : convertView;
		
		((TextView) childView.findViewById(R.id.roster_nick)).setText(rosterItem.getNick());
		((TextView) childView.findViewById(R.id.roster_statusmsg)).setText(rosterItem.getStatusMessage());
		((ImageView) childView.findViewById(R.id.roster_status)).setImageResource(getIconDrawable(rosterItem.getStatus()));
		
		return childView;
	}

	public int getIconDrawable(int status) {
		switch(status) {
		case RosterItem.STATUS_OFFLINE:
			return R.drawable.jabber_offline;
		case RosterItem.STATUS_ONLINE:
			return R.drawable.jabber_online;
		case RosterItem.STATUS_CHAT:
			return R.drawable.jabber_chat;
		case RosterItem.STATUS_AWAY:
			return R.drawable.jabber_away;
		case RosterItem.STATUS_XA:
			return R.drawable.jabber_xa;
		case RosterItem.STATUS_DND:
			return R.drawable.jabber_dnd;
		}
		return R.drawable.jabber_offline;
	}

}
