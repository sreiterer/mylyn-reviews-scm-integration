/*******************************************************************************
 * Copyright (c) 2010 Research Group for Industrial Software (INSO), Vienna University of Technology
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

import org.eclipse.mylyn.reviews.ui.CreateTask;

/**
 * Submit handler for task editor.
 * 
 * @author Kilian Matt
 * @author Stefan Reiterer
 */
public class NewReviewSubmitHandler implements ReviewSubmitHandler {

	/**
	 * Submit the current task.
	 */
	public void doSubmit(ReviewTaskEditorInput editorInput) {

		new CreateTask(((NewReviewTaskEditorInput) editorInput).getModel(), editorInput.getReview()).schedule();
	}

}
