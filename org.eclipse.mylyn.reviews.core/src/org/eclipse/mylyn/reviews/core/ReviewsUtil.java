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
package org.eclipse.mylyn.reviews.core;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.mylyn.internal.provisional.tasks.core.TasksUtil;
import org.eclipse.mylyn.internal.tasks.core.RepositoryModel;
import org.eclipse.mylyn.reviews.core.model.review.Changeset;
import org.eclipse.mylyn.reviews.core.model.review.Patch;
import org.eclipse.mylyn.reviews.core.model.review.Rating;
import org.eclipse.mylyn.reviews.core.model.review.Review;
import org.eclipse.mylyn.reviews.core.model.review.ReviewPackage;
import org.eclipse.mylyn.reviews.core.model.review.ScopeItem;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryModel;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskAttachment;
import org.eclipse.mylyn.tasks.core.ITaskContainer;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.ITaskDataManager;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;
import org.eclipse.mylyn.tasks.ui.TasksUi;

/**
 * @author Kilian Matt
 * @author Stefan Reiterer
 */
public class ReviewsUtil {

	public static List<ReviewSubTask> getReviewSubTasksFor(
			ITaskContainer taskContainer, ITaskDataManager taskDataManager,
			IRepositoryModel repositoryModel, TaskRepository repository,/*TaskDataModel dataModel,*/
			IProgressMonitor monitor) {
		List<ReviewSubTask> resultList = new ArrayList<ReviewSubTask>();
		try {
			for (ITask subTask : taskContainer.getChildren()) {
				if (subTask.getSummary().startsWith("Review")) { //$NON-NLS-1$

					for (Review review : getReviewAttachmentFromTask(
							taskDataManager, repositoryModel, repository,/*dataModel,*/
							subTask)) {

						Rating rating = Rating.NONE;
						String result = "";
						if (review.getResult() != null) {
							rating = review.getResult().getRating();
							result = review.getResult().getText();
						}

						ReviewSubTask sbt = new ReviewSubTask(
								getPatchFile(review.getScope()),
								getPatchCreationDate(review.getScope()),
								getAuthorString(review.getScope()), subTask
										.getOwner(), rating, result, subTask,
								getChangesets(review.getScope()), review);

						String reviewer = taskDataManager.getTaskData(subTask)
								.getRoot().getMappedAttribute(
										TaskAttribute.USER_ASSIGNED).getValue();
						sbt.setReviewer(reviewer);
						resultList.add(sbt);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultList;

	}

	private static String getPatchFile(EList<ScopeItem> scope) {
		if (scope.size() == 1 && scope.get(0) instanceof Patch) {
			return ((Patch) scope.get(0)).getFileName();
		} else {
			return "";
		}
	}

	private static Date getPatchCreationDate(EList<ScopeItem> scope) {
		if (scope.size() == 1 && scope.get(0) instanceof Patch) {
			return ((Patch) scope.get(0)).getCreationDate();
		} else {
			return null;
		}
	}

	private static String getChangesets(EList<ScopeItem> scope) {
		StringBuilder sb = new StringBuilder();
		for (ScopeItem item : scope) {
			sb.append(((Changeset) item).getRevision());
			sb.append(", ");
		}

		return sb.substring(0, sb.length() - 2);
	}

	private static String getAuthorString(EList<ScopeItem> scope) {
		if (scope.size() == 0) {
			return "none";
		} else if (scope.size() == 1) {
			return scope.get(0).getAuthor();
		} else if (scope.size() < 3) {
			StringBuilder sb = new StringBuilder();
			for (ScopeItem item : scope) {
				sb.append(item.getAuthor());
				sb.append(", ");
			}
			return sb.substring(0, sb.length() - 2);
		} else {
			return "Multiple Authors";
		}
	}

	static List<Review> parseAttachments(TaskAttribute attribute,
			ITaskDataManager manager, IRepositoryModel model, TaskRepository repository, ITask task,
			/*TaskDataModel dataModel,*/ IProgressMonitor monitor) {

		List<Review> reviewList = new ArrayList<Review>();
		try {
			TaskRepository taskRepository = repository;

			AbstractRepositoryConnector connector = TasksUi
					.getRepositoryConnector(taskRepository.getConnectorKind());

			TaskData taskData = TasksUi.getTaskDataManager().getTaskData(
					task);

			InputStream is = connector.getTaskAttachmentHandler().getContent(
					taskRepository, task, attribute,
					new NullProgressMonitor());

			ZipInputStream stream = new ZipInputStream(is);
			while (!stream.getNextEntry().getName().equals(
					ReviewConstants.REVIEW_DATA_FILE)) {
			}

			ResourceSet resourceSet = new ResourceSetImpl();
			resourceSet.getPackageRegistry().put(ReviewPackage.eNS_URI,
					ReviewPackage.eINSTANCE);
			Resource resource = resourceSet.createResource(URI.createURI(""));
			resource.load(stream, null);

			for (EObject item : resource.getContents()) {
				if (item instanceof Review) {
					Review review = (Review) item;

					// the reviewer is the owner of the subtaks
					reviewList.add(review);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return reviewList;
	}

	public static List<Review> getReviewAttachmentFromTask(
			ITaskDataManager taskDataManager, IRepositoryModel model, TaskRepository repository,
			/*TaskDataModel datamodel,*/ ITask task) throws CoreException {

		TaskAttribute lastAttachment = null;
		TaskData taskData = taskDataManager.getTaskData(task);
		if (taskData != null) {
			List<TaskAttribute> attributesByType = taskData
					.getAttributeMapper().getAttributesByType(taskData,
							TaskAttribute.TYPE_ATTACHMENT);

			ITaskAttachment taskAttachment = null;

			for (TaskAttribute attribute : attributesByType) {
				taskAttachment = ((RepositoryModel) model)
						.createTaskAttachment(attribute);
				if (taskAttachment.getFileName().startsWith(
						ReviewConstants.REVIEW_DATA_CONTAINER_NAME)) {

					lastAttachment = attribute;
				}
			}
			if (lastAttachment != null) {
				taskAttachment = ((RepositoryModel) model)
						.createTaskAttachment(lastAttachment);
				return parseAttachments(lastAttachment, taskDataManager, model,
						repository, task, new NullProgressMonitor());
			}

		}
		return new ArrayList<Review>();
	}

	static List<ITargetPathStrategy> strategies;
	static {
		strategies = new ArrayList<ITargetPathStrategy>();
		strategies.add(new SimplePathFindingStrategy());
	}

	public static List<? extends ITargetPathStrategy> getPathFindingStrategies() {
		return strategies;
	}

	public static byte[] createAttachment(Review review) {
		try {
			ResourceSet resourceSet = new ResourceSetImpl();

			Resource resource = resourceSet.createResource(URI
					.createFileURI("")); //$NON-NLS-1$

			resource.getContents().add(review);

			for (int i = 0; i < review.getScope().size(); i++)
				resource.getContents().add(review.getScope().get(i));

			if (review.getResult() != null)
				resource.getContents().add(review.getResult());
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			ZipOutputStream outputStream = new ZipOutputStream(
					byteArrayOutputStream);
			outputStream.putNextEntry(new ZipEntry(
					ReviewConstants.REVIEW_DATA_FILE));
			resource.save(outputStream, null);
			outputStream.closeEntry();
			outputStream.close();
			return byteArrayOutputStream.toByteArray();

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public static boolean hasPrivileges(String username,
			TaskRepository taskRepository) {
		return taskRepository.getUserName().equals(username);
	}

	public static String getTaskOwner(ITask task) {
		return task.getOwner();
	}
}
