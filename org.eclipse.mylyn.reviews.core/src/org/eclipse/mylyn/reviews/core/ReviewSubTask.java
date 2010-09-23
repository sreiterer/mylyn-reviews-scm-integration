/*******************************************************************************
 * Copyright (c) 2010 Research Group for Industrial Software (INSO), Vienna University of Technology
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kilian Matt (Research Group for Industrial Software (INSO), Vienna University of Technology) - initial API and implementation
 *     Stefan Reiterer (Research Group for Industrial Software (INSO), Vienna University of Technology) - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.reviews.core;

import java.util.Date;

import org.eclipse.mylyn.reviews.core.model.review.Rating;
import org.eclipse.mylyn.reviews.core.model.review.Review;
import org.eclipse.mylyn.tasks.core.ITask;

/**
 * @author Kilian Matt
 * @author Stefan Reiterer
 */
public class ReviewSubTask {

	private String comment;
	private ITask task;
	private Date creationDate;

	public ReviewSubTask(String patchFile, Date creationDate, String author,
			String reviewer, Rating result, String comment, ITask task,
			String changesets, Review review) {
		super();
		this.patchFile = patchFile;
		if (creationDate != null)
			this.creationDate = new Date(creationDate.getTime());
		else
			this.creationDate = new Date();
		this.author = author;
		this.reviewer = reviewer;
		this.result = result;
		this.comment = comment;
		this.task = task;
		this.review = review;

		this.changesets = changesets;
	}

	private String changesets;
	private String patchFile;
	private String author;
	private String reviewer;
	private Rating result;
	private Review review;

	public String getPatchFile() {
		return patchFile;
	}

	public String getChangesets() {
		return changesets;
	}

	public void setChangesets(String changesets) {
		this.changesets = changesets;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public String getPatchDescription() {
		return String.format("%s %s", patchFile, creationDate);
	}

	public String getAuthor() {
		return author;
	}

	public String getReviewer() {
		return reviewer;
	}

	public Rating getResult() {
		return result;
	}

	public String getComment() {
		return comment;
	}

	public ITask getTask() {
		return task;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public void setTask(ITask task) {
		this.task = task;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public void setPatchFile(String patchFile) {
		this.patchFile = patchFile;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public void setReviewer(String reviewer) {
		this.reviewer = reviewer;
	}

	public void setResult(Rating result) {
		this.result = result;
	}
	
	public Review getReview() {
		return review;
	}

	public void setReview(Review review) {
		this.review = review;
	}

	public String toString() {
		return "Review Subtask " + patchFile + " by " + author + " revd by "
				+ reviewer + " rated as " + result;
	}
}
