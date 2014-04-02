package cdg.nut.interfaces;

import cdg.nut.util.settings.SettingsKeys;

public interface ISettingsListener {
	public void valueChanged(SettingsKeys key, Object value);
}
