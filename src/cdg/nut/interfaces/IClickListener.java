package cdg.nut.interfaces;

import cdg.nut.util.enums.MouseButtons;

public interface IClickListener {
	public void onClick(int x, int y, MouseButtons button, int grabx, int graby);
}
