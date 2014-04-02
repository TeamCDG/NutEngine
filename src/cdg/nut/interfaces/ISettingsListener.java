package cdg.nut.interfaces;

import cdg.nut.util.settings.SettingsKeys;

public interface ISettingsListener {
	public <T> void valueChanged(SettingsKeys key, T value);
}
