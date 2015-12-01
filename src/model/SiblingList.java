package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Krzysztof on 30.11.2015.
 */
public class SiblingList extends ArrayList<Sibling> {
	public int sum(){
		int count = 0;
		for (Sibling sibling : this) {
			count += sibling.getCount();
		}
		return count;
	}
}
