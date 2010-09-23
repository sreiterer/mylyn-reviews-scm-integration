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
package org.eclipse.mylyn.reviews.ui;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskMigrator;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.reviews.core.ReviewsUtil;
import org.eclipse.mylyn.reviews.core.model.review.Review;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;
import org.eclipse.mylyn.tasks.core.sync.SubmitJob;
import org.eclipse.mylyn.tasks.core.sync.SynchronizationJob;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;

/**
 * This Job can be used to create a review sub task for a given parent task.
 * 
 * @author Kilian Matt
 * @author Stefan Reiterer
 */
@SuppressWarnings("restriction")
public class CreateTask extends Job {
	private static Logger logger = Logger.getLogger(CreateTask.class.getName());

	private TaskDataModel model;
	private Review review;
	private String reviewer;
	private TaskRepository taskRepository;
	private AbstractRepositoryConnector connector;
	private ITask parent;
	private ITask newRepoTask;

	/**
	 * @return the newRepoTask
	 */
	public ITask getNewRepoTask() {
		return newRepoTask;
	}

	/**
	 * Constructor that initializes the Job with the given values.
	 * 
	 * @param model
	 *            Task data model.
	 * @param review
	 *            Review that needs to be submitted.
	 */
	public CreateTask(TaskDataModel model, Review review) {
		super(Messages.CreateTask_Title);
		this.model = model;
		this.review = review;
		this.reviewer = "";
		this.taskRepository = model.getTaskRepository();

		this.connector = TasksUi.getRepositoryConnector(taskRepository.getConnectorKind());
	}

	/**
	 * Constructor that initializes the Job with the given values.
	 * 
	 * @param model
	 *            Task data model.
	 * @param review
	 *            Review that needs to be submitted.
	 * @param reviewer
	 *            Name of the reviewer. (Mantis username)
	 * @param parent
	 *            Parent task.
	 */
	public CreateTask(TaskDataModel model, Review review, String reviewer, ITask parent) {
		super(Messages.CreateTask_Title);
		this.model = model;
		this.review = review;
		this.reviewer = reviewer;
		this.taskRepository = model.getTaskRepository();
		this.parent = parent;

		this.connector = TasksUi.getRepositoryConnector(taskRepository.getConnectorKind());
	}

	/**
	 * Create new review sub-task.
	 */
	protected IStatus run(final IProgressMonitor monitor) {
		try {

			final ITask newLocalTask = TasksUiUtil.createOutgoingNewTask(taskRepository.getConnectorKind(),
					taskRepository.getRepositoryUrl());

			TaskAttributeMapper mapper = connector.getTaskDataHandler().getAttributeMapper(taskRepository);

			final TaskData data = new TaskData(mapper, taskRepository.getConnectorKind(), taskRepository
					.getRepositoryUrl(), ""); //$NON-NLS-1$

			connector.getTaskDataHandler().initializeSubTaskData(taskRepository, data, model.getTaskData(),
					new NullProgressMonitor());

			data.getRoot().createMappedAttribute(TaskAttribute.SUMMARY).setValue(
					"Review of " + model.getTask().getTaskKey() + " " + model.getTask().getSummary()); //$NON-NLS-1$ //$NON-NLS-2$

			data.getRoot().createMappedAttribute(TaskAttribute.DESCRIPTION).setValue("Result");
			// data.getRoot().createMappedAttribute(TaskAttribute.COMMENT_NEW)
			// .setValue("Result");
			data.getRoot().createMappedAttribute(TaskAttribute.USER_ASSIGNED).setValue(reviewer);
			newLocalTask.setOwner(reviewer);

			// needs to be lower case for mantis
			data.getRoot().createMappedAttribute(TaskAttribute.STATUS).setValue("new"); //$NON-NLS-1$
			data.getRoot().createMappedAttribute(TaskAttribute.VERSION).setValue(
					model.getTaskData().getRoot().getMappedAttribute(TaskAttribute.VERSION).getValue());
			data.getRoot().createMappedAttribute(TaskAttribute.PRODUCT).setValue(
					model.getTaskData().getRoot().getMappedAttribute(TaskAttribute.PRODUCT).getValue());

			final byte[] attachmentBytes = ReviewsUtil.createAttachment(review);
			final SubmitJob submitJob = TasksUiInternal.getJobFactory().createSubmitTaskJob(connector, taskRepository,
					newLocalTask, data, new TreeSet<TaskAttribute>());

			submitJob.schedule();
			submitJob.join();
			Thread.sleep(300);

			if (submitJob.getStatus() == null) {
				newRepoTask = submitJob.getTask();

				TaskMigrator migrator = new TaskMigrator(newLocalTask);
				migrator.setDelete(true);
				migrator.execute(newRepoTask);

				TaskAttribute attachmentAttribute = data.getAttributeMapper().createTaskAttachment(data);
				try {
					ReviewCommentTaskAttachmentSource attachment = new ReviewCommentTaskAttachmentSource(
							attachmentBytes);

					monitor.subTask(Messages.CreateTask_UploadingAttachment);
					connector.getTaskAttachmentHandler().postContent(taskRepository, newRepoTask, attachment,
							"review result", //$NON-NLS-1$
							attachmentAttribute, monitor);

				} catch (CoreException e) {
					e.printStackTrace();
				}

				Set<TaskRepository> repos = new HashSet<TaskRepository>();
				repos.add(taskRepository);
				SynchronizationJob jb = TasksUiPlugin.getTaskJobFactory().createSynchronizeRepositoriesJob(repos);
				jb.setFullSynchronization(true);
				jb.schedule();
				jb.join();

				Thread.sleep(1000);
				Set<ITask> tasks = new HashSet<ITask>();
				tasks.add(parent);
				SynchronizationJob jb1 = TasksUiPlugin.getTaskJobFactory().createSynchronizeTasksJob(connector, tasks);
				jb1.schedule();
				jb1.join();

			} else {
				logger.log(Level.SEVERE, "Cannot upload task attachment");
			}

			return new Status(IStatus.OK, ReviewsUiPlugin.PLUGIN_ID, Messages.CreateTask_Success);

		} catch (Exception e) {
			e.printStackTrace();
			return new Status(IStatus.ERROR, ReviewsUiPlugin.PLUGIN_ID, e.getMessage());
		}
	}
}
