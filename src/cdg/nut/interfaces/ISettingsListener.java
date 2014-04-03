package cdg.nut.interfaces;

import cdg.nut.util.settings.SetKeys;

public interface ISettingsListener {
	public <T> void valueChanged(SetKeys key, T value);
}
