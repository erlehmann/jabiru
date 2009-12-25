package net.mzet.jabiru.settings;

import net.mzet.jabiru.R;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class AccountSettings extends PreferenceActivity {

	private EditTextPreference pref_jabberid;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.accountsettings);
		
		fillJabberID();
		
		pref_jabberid = (EditTextPreference) findPreference("account_jabberid");
		pref_jabberid.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				if(newValue != null && ((CharSequence) newValue).toString().contains("@")) {
					pref_jabberid.setSummary((CharSequence) newValue);
					return true;
				}
				showToastNotification(R.string.account_settings_jabberid_error);
				return false;
			}
		});
	}

	protected void showToastNotification(int message) {
		Toast tmptoast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
		tmptoast.show();
	}

	
	private void fillJabberID() {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		Preference p = findPreference("account_jabberid");
		p.setSummary(sp.getString("account_jabberid", getString(R.string.account_settings_jabberid_enter)));
	}

}
