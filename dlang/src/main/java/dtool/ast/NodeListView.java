package dtool.ast;

import dtool.util.ArrayView;
import melnorme.utilbox.misc.IteratorUtil;

import java.util.Iterator;

/**
 * Utility class for lists of nodes.
 * Has additional info saying if parsing encountered an endingseparator or not;
 */
public class NodeListView<T extends IASTNode> extends ArrayView<T> {
	
	public final boolean hasEndingSeparator;
	
	public NodeListView(T[] array, boolean hasEndingSeparator) {
		super(array);
		this.hasEndingSeparator = hasEndingSeparator;
	}
	
	public static <T> Iterator<T> getIteratorSafe(Iterable<T> nodeList) {
		return nodeList == null ? IteratorUtil.<T>emptyIterator() : nodeList.iterator();
	}
	
}
