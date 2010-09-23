package org.eclipse.mylyn.reviews.ui.editors.compare;

import org.eclipse.compare.ITypedElement;
import org.eclipse.compare.structuremergeviewer.DiffNode;
import org.eclipse.compare.structuremergeviewer.Differencer;
import org.eclipse.compare.structuremergeviewer.ICompareInput;
import org.eclipse.compare.structuremergeviewer.ICompareInputChangeListener;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.graphics.Image;

/**
 * This class represents the input of an eclipse compare editor.
 * 
 * @author Stefan Reiterer
 * 
 */
public class RevisionCompareInput implements ICompareInput {
	// left item (file) in the compare editor
	private ChangesetCompareItem left;

	// right item (file) in the compare editor
	private ChangesetCompareItem right;

	public RevisionCompareInput(ChangesetCompareItem left, ChangesetCompareItem right) {
		this.left = left;
		this.right = right;
	}

	/**
	 * This method generates the input for the compare editor. It returns a
	 * DiffNode that represents the difference between the two CompareItem
	 * objects.
	 * 
	 * @param pm
	 *            ProgressMonitor for loading the compare editor.
	 */
	protected Object prepareInput(IProgressMonitor pm) {
		DiffNode node = new DiffNode(null, Differencer.CHANGE, null, left, right);
		return node;
	}

	/**
	 * This method returns the title of the compare editor.
	 */
	public String getTitle() {
		return Messages.getString("RevisionCompareInput.Compare_Revision") + left.getName() + Messages.getString("RevisionCompareInput.With_Previous_Revision"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void addCompareInputChangeListener(ICompareInputChangeListener arg0) {
	}

	public void copy(boolean arg0) {
	}

	public ITypedElement getAncestor() {
		return null;
	}

	public Image getImage() {
		return null;
	}

	public int getKind() {
		return 0;
	}

	public ITypedElement getLeft() {
		return left;
	}

	public String getName() {
		return Messages.getString("RevisionCompareInput.Compare_Revisions"); //$NON-NLS-1$
	}

	public ITypedElement getRight() {
		return right;
	}

	public void removeCompareInputChangeListener(ICompareInputChangeListener arg0) {
	}
}
