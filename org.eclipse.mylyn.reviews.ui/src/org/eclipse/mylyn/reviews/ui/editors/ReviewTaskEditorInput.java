/*******************************************************************************
 * Copyright (c) 2010 Research Group for Industrial Software (INSO), Vienna University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kilian Matt (Research Group for Industrial Software (INSO), Vienna University of Technology) - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.reviews.ui.editors;

import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylyn.reviews.core.model.review.Review;
import org.eclipse.mylyn.reviews.ui.Images;
import org.eclipse.mylyn.reviews.ui.ReviewDiffModel;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

/**
 * Input data type for review editor.
 * 
 * @author Kilian Matt
 */
public class ReviewTaskEditorInput implements IEditorInput {

	private Review review;
	private TaskDataModel model;

	/**
	 * Initialze data type with given parameters.
	 * 
	 * @param model
	 *            Task data model.
	 * @param review
	 *            Review data.
	 */
	public ReviewTaskEditorInput(TaskDataModel model, Review review) {
		this.review = review;
		this.model = model;
	}

	/**
	 * Getter method for task data model.
	 * 
	 * @return The task data model.
	 */
	public TaskDataModel getModel() {
		return model;
	}

	/**
	 * Setter method for task data model.
	 * 
	 * @param model
	 *            The task data model.
	 */
	public void setModel(TaskDataModel model) {
		this.model = model;
	}

	/**
	 * Getter method for review content.
	 * 
	 * @return The review content.
	 */
	public Review getReview() {
		return review;
	}

	/**
	 * Check whether editor input exists.
	 */
	public boolean exists() {
		return false;
	}

	/**
	 * Definition of image descriptor.
	 */
	public ImageDescriptor getImageDescriptor() {
		return Images.SMALL_ICON;
	}

	public String getName() {
		// TODO
		return Messages.ReviewTaskEditorInput_New_Review;// "Review of" +
		// model.getTask().getTaskKey()
		// + " " +
		// model.getTask().toString();
	}

	public IPersistableElement getPersistable() {
		return null;
	}

	public String getToolTipText() {
		return Messages.NewReviewTaskEditorInput_Tooltip;
	}

	public List<ReviewDiffModel> getScope() {
		return null;
	}

	@SuppressWarnings("unchecked")
	public Object getAdapter(Class arg0) {
		return null;
	}

}
