/*******************************************************************************
 * Copyright (c) 2010 Research Group for Industrial Software (INSO), Vienna University of Technology
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kilian Matt (Research Group for Industrial Software (INSO), Vienna University of Technology) - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.reviews.ui;

import java.util.List;

import org.eclipse.mylyn.internal.tasks.core.RepositoryModel;
import org.eclipse.mylyn.reviews.core.ReviewConstants;
import org.eclipse.mylyn.reviews.ui.editors.CreateReviewTaskEditorPage;
import org.eclipse.mylyn.reviews.ui.editors.ReviewTaskEditorPage;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskAttachment;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPageFactory;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorInput;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.forms.editor.IFormPage;

/**
 * Review editor page for the creation and execution of code reviews.
 * 
 * @author Kilian Matt
 * @author Stefab Reiterer
 */
@SuppressWarnings("restriction")
public class ReviewTaskEditorPageFactory extends AbstractTaskEditorPageFactory {

	public ReviewTaskEditorPageFactory() {
	}

	public boolean canCreatePageFor(TaskEditorInput input) {
		// always show the review tab
		return true;
	}

	public IFormPage createPage(TaskEditor parentEditor) {
		if (isTaskReviewTaks(parentEditor.getTaskEditorInput())) {
			// if the task contains review data
			return new ReviewTaskEditorPage(parentEditor);
		} else {
			// if the task does not contain review data
			return new CreateReviewTaskEditorPage(parentEditor);
		}
	}

	public Image getPageImage() {
		return Images.SMALL_ICON.createImage();
	}

	public String getPageText() {
		return Messages.ReviewTaskEditorPageFactory_PageTitle;
	}

	public int getPriority() {
		return PRIORITY_ADDITIONS;
	}

	private boolean isTaskReviewTaks(TaskEditorInput input) {
		ITask task = input.getTask();
		try {

			TaskData taskData = TasksUi.getTaskDataManager().getTaskData(task);
			if (taskData != null) {
				List<TaskAttribute> attributesByType = taskData.getAttributeMapper().getAttributesByType(taskData,
						TaskAttribute.TYPE_ATTACHMENT);
				for (TaskAttribute attribute : attributesByType) {
					ITaskAttachment taskAttachment = ((RepositoryModel) TasksUi.getRepositoryModel())
							.createTaskAttachment(attribute);
					taskData.getAttributeMapper().updateTaskAttachment(taskAttachment, attribute);
					if (taskAttachment.getFileName().startsWith(ReviewConstants.REVIEW_DATA_CONTAINER_NAME))
						return true;

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}
}
