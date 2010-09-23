package org.eclipse.mylyn.reviews.ui.dialogs;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.mylyn.reviews.core.model.review.Changeset;
import org.eclipse.mylyn.reviews.core.model.review.ScopeItem;
import org.eclipse.swt.graphics.Image;

/**
 * Label provider for {@link ScopeItem}s.
 * 
 * @author Stefan Reiterer
 * 
 */
public class SILabelProvider implements ILabelProvider {

	/**
	 * Remove listener on label provider.
	 */
	public void removeListener(ILabelProviderListener arg0) {
	}

	public boolean isLabelProperty(Object arg0, String arg1) {
		return false;
	}

	/**
	 * Dispose.
	 */
	public void dispose() {
	}

	/**
	 * Add listener to label provider.
	 */
	public void addListener(ILabelProviderListener arg0) {
	}

	/**
	 * Get label text for changeset.
	 */
	public String getText(Object element) {
		Changeset changeset = (Changeset) element;
		return changeset.getRevision() + " - " + changeset.getComment();
	}

	/**
	 * Get the label image.
	 */
	public Image getImage(Object arg0) {
		return null;
	}
}
