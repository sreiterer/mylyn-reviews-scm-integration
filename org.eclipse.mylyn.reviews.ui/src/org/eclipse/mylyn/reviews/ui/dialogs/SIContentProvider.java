package org.eclipse.mylyn.reviews.ui.dialogs;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.reviews.core.ReviewSubTask;
import org.eclipse.mylyn.reviews.core.model.review.ScopeItem;

/**
 * Content provider for {@link ScopeItem}s.
 * 
 * @author Stefan Reiterer
 * 
 */
public class SIContentProvider implements IStructuredContentProvider {

	/**
	 * Input changed listener.
	 */
	public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
	}

	/**
	 * Dispose.
	 */
	public void dispose() {
	}

	/**
	 * Convert the given list of {@link ReviewSubTask}s into an array.
	 */
	@SuppressWarnings("unchecked")
	public Object[] getElements(Object obj) {
		List<ReviewSubTask> st = (List<ReviewSubTask>) obj;
		return st.toArray();
	}
}