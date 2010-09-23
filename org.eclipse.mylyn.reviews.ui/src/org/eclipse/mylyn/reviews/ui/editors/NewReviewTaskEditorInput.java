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

import org.eclipse.mylyn.reviews.core.model.review.ReviewFactory;
import org.eclipse.mylyn.reviews.core.model.review.ScopeItem;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;

/** Input for task editor.
 * 
 * @author Kilian Matt
 * @author Stefan Reiterer
 */
public class NewReviewTaskEditorInput extends ReviewTaskEditorInput {

	private TaskDataModel model;

	/** Create task editor input.
	 * 
	 * @param model Task data model
	 * @param patch Scope item as input.
	 */
	public NewReviewTaskEditorInput(TaskDataModel model, ScopeItem patch) {
		super(model, ReviewFactory.eINSTANCE.createReview());
		this.model = model;
		getReview().getScope().add(patch);
	}

	public String getName() {
		return Messages.NewReviewTaskEditorInput_ReviewPrefix + model.getTask().getTaskKey() + " " //$NON-NLS-2$
				+ model.getTask().toString();
	}

	public TaskDataModel getModel() {
		return model;
	}
}
