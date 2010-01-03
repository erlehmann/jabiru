package net.mzet.jabiru.chat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.mzet.jabiru.R;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ChatListAdapter extends ArrayAdapter<ChatItem> {
	
	private ArrayList<ChatItem> chatItems;
	
	public ChatListAdapter(Context context, int resource,
			int textViewResourceId, List<ChatItem> objects) {
		super(context, resource, textViewResourceId, objects);
		chatItems = (ArrayList<ChatItem>) objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		TextView view = convertView == null ? (TextView) inflater.inflate(R.layout.chat_item, null) : (TextView) convertView;
		
		Date time = chatItems.get(position).getTime();
		String body = chatItems.get(position).getBody();
		
		view.setText(String.format("[%02d:%02d:%02d]", time.getHours(), time.getMinutes(), time.getSeconds()) + " - " + body);
		Spannable s = (Spannable) view.getText();
		s.setSpan(new StyleSpan(Typeface.BOLD), 0, 10, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		
		if(chatItems.get(position).getDirection() == ChatItem.DIR_TO) {
			view.setBackgroundColor(Color.rgb(180, 200, 201));
		}
		else {
			view.setBackgroundColor(Color.WHITE);
		}
		
		return view;
	}
}
