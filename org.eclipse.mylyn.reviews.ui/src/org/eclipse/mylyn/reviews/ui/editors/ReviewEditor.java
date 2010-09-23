/*******************************************************************************
 * Copyright (c) 2010 Research Group for Industrial Software (INSO), Vienna University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kilian Matt (Research Group for Industrial Software (INSO), Vienna University of Technology) - initial API and implementation
 *     Stefan Reiterer (Research Group for Industrial Software (INSO), Vienna University of Technology)
 *******************************************************************************/
package org.eclipse.mylyn.reviews.ui.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

/**
 * Review editor.
 * 
 * @author Kilian Matt
 * @author Stefan Reiterer
 */
public class ReviewEditor extends EditorPart {

	/** Editor part id. */
	public static final String ID = "org.eclipse.mylyn.reviews.ui.editors.ReviewEditor"; //$NON-NLS-1$

	/**
	 * Dispose
	 */
	public void dispose() {
		super.dispose();
	}

	/**
	 * The dirty flag if the model has been changed
	 */
	private boolean dirty = false;

	/**
	 * The managed form for this editor
	 */
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		setSite(site);
		setInput(input);
		setPartName(input.getName());

	}

	/**
	 * Create part controls for performing code reviews.
	 */
	public void createPartControl(Composite parent) {
		new EditorSupport((ReviewTaskEditorInput) getEditorInput(), new NewReviewSubmitHandler())
				.createPartControl(parent);
	}

	/**
	 * Perform save on task editor page.
	 */
	public void doSave(IProgressMonitor monitor) {
		setDirty(false);
	}

	/**
	 * Perform save as on task editor page.
	 */
	public void doSaveAs() {
		// not allowed
	}

	/**
	 * Determine if it is allowed to safe the curren task.
	 */
	public boolean isSaveAsAllowed() {
		// not allowed
		return false;
	}

	/**
	 * Is task in dirty state.
	 */
	public boolean isDirty() {
		return dirty;
	}

	/**
	 * Define if task is in dirty state.
	 * 
	 * @param dirty
	 *            True if task is in dirty state, otherwise false.
	 */
	public void setDirty(boolean dirty) {
		if (dirty == true && !isDirty()) {
			this.dirty = true;
			firePropertyChange(IEditorPart.PROP_DIRTY);
		} else if (dirty == false && isDirty()) {
			this.dirty = false;
			firePropertyChange(IEditorPart.PROP_DIRTY);
		}
	}

	/**
	 * Set focus on page.
	 */
	public void setFocus() {
	}
}
