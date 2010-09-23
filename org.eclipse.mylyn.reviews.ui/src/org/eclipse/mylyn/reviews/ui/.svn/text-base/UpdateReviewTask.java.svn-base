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

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.reviews.core.ReviewsUtil;
import org.eclipse.mylyn.reviews.ui.editors.ReviewTaskEditorInput;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.sync.SubmitJob;
import org.eclipse.mylyn.tasks.core.sync.SynchronizationJob;
import org.eclipse.mylyn.tasks.ui.TasksUi;

/**
 * Update a given review task with a new review result. (Update task attachment)
 * 
 * @author Stefan Reiterer
 */
@SuppressWarnings("restriction")
public class UpdateReviewTask extends Job {
	private ReviewTaskEditorInput input;
	private AbstractRepositoryConnector connector;

	/** Default constructor. */
	public UpdateReviewTask(ReviewTaskEditorInput editorInput) {
		super(Messages.UpdateReviewTask_Title);
		this.input = editorInput;
		connector = TasksUi.getRepositoryConnector(input.getModel().getTaskRepository().getConnectorKind());
	}

	/**
	 * Upload a new attachment to the review task and add a new comment to the
	 * task.
	 */
	protected IStatus run(IProgressMonitor monitor) {

		byte[] attachmentBytes = ReviewsUtil.createAttachment(input.getReview());
		try {
			ReviewCommentTaskAttachmentSource attachment = new ReviewCommentTaskAttachmentSource(attachmentBytes);

			TaskAttribute attachmentAttribute = input.getModel().getTaskData().getAttributeMapper()
					.createTaskAttachment(input.getModel().getTaskData());

			monitor.subTask(Messages.CreateTask_UploadingAttachment);
			connector.getTaskAttachmentHandler().postContent(input.getModel().getTaskRepository(),
					input.getModel().getTask(), attachment, "review result", //$NON-NLS-1$
					attachmentAttribute, monitor);

			// synchronize with repository
			Set<ITask> tsks = new HashSet<ITask>();
			tsks.add(input.getModel().getTask());
			SynchronizationJob jbTask = TasksUiPlugin.getTaskJobFactory().createSynchronizeTasksJob(connector, tsks);
			jbTask.setFullSynchronization(true);
			jbTask.schedule();
			jbTask.join();

			ITask task = input.getModel().getTask();
			TaskData taskData = TasksUi.getTaskDataManager().getTaskData(task);

			TaskAttribute des = taskData.getRoot().getAttribute("new_comment");
			taskData.getAttributeMapper().setValue(des,
					input.getReview().getResult().getRating() + ":\n" + input.getReview().getResult().getText());

			// synchronize with repository
			Set<TaskRepository> repos = new HashSet<TaskRepository>();
			repos.add(input.getModel().getTaskRepository());
			SynchronizationJob jb = TasksUiPlugin.getTaskJobFactory().createSynchronizeRepositoriesJob(repos);
			jb.setFullSynchronization(true);
			jb.schedule();
			jb.join();

			SubmitJob submit = TasksUiInternal.getJobFactory().createSubmitTaskJob(connector,
					input.getModel().getTaskRepository(), task, taskData, input.getModel().getChangedOldAttributes());
			submit.schedule();
			submit.join();
			connector.updateTaskFromTaskData(input.getModel().getTaskRepository(), task, taskData);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new Status(IStatus.OK, ReviewsUiPlugin.PLUGIN_ID, Messages.CreateTask_Success);
	}

}
