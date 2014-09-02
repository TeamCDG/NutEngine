package cdg.nut.util.ai;

import java.util.Comparator;

public class PFTileComperator implements Comparator<PFTile> {

	

	@Override
	public int compare(PFTile t0, PFTile t1) {
		if(t0.getF() == t1.getF())
			return 0;
		else if(t0.getF() > t1.getF())
			return 1;
		else
			return -1;
	}

}
