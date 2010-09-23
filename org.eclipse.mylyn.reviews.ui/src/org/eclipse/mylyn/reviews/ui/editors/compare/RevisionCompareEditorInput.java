package org.eclipse.mylyn.reviews.ui.editors.compare;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.structuremergeviewer.DiffNode;
import org.eclipse.compare.structuremergeviewer.Differencer;
import org.eclipse.core.runtime.IProgressMonitor;

/** This class represents the input of an eclipse compare editor.
 * 
 * @author Stefan Reiterer
 *
 */
public class RevisionCompareEditorInput extends CompareEditorInput {
	public RevisionCompareEditorInput(ChangesetCompareItem left, ChangesetCompareItem right) {
		super(new CompareConfiguration());
		this.left = left;
		this.right = right;
	}

	// left item (file) in the compare editor
	private ChangesetCompareItem left;
	
	// right item (file) in the compare editor
	private ChangesetCompareItem right;
	
	
	/** This method generates the input for the compare editor. 
	 * It returns a DiffNode that represents the difference between
	 * the two CompareItem objects.
	 * 
	 * @param pm ProgressMonitor for loading the compare editor.
	 */
	protected Object prepareInput(IProgressMonitor pm) {
		DiffNode node = new DiffNode(null, Differencer.CHANGE, 
		           null, left, right);
        return node;
    }

	/** This method returns the title of the compare editor.
	 */
	public String getTitle() {
		return "Compare revision " + left.getName() + " with previous revision";
	}
}
