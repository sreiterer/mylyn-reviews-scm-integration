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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.internal.tasks.core.data.TaskDataManager;
import org.eclipse.mylyn.reviews.core.ReviewSubTask;
import org.eclipse.mylyn.reviews.core.ReviewsUtil;
import org.eclipse.mylyn.reviews.core.model.review.Changeset;
import org.eclipse.mylyn.reviews.core.model.review.Rating;
import org.eclipse.mylyn.reviews.core.model.review.Review;
import org.eclipse.mylyn.reviews.core.model.review.ReviewFactory;
import org.eclipse.mylyn.reviews.core.model.review.ReviewResult;
import org.eclipse.mylyn.reviews.core.scm.TaskMapper;
import org.eclipse.mylyn.reviews.ui.Images;
import org.eclipse.mylyn.reviews.ui.UiUtil;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskContainer;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ListSelectionDialog;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

/**
 * Create part of review page that is capable to visualize the review summary
 * for the current task.
 * 
 * @author Kilian Matt
 * @author Stefan Reiterer
 */
@SuppressWarnings("restriction")
public class ReviewSummaryTaskEditorPart extends AbstractTaskEditorPart {
	public static final String ID_PART_REVIEWSUMMARY = "org.eclipse.mylyn.reviews.ui.editors.parts.reviewsummary"; //$NON-NLS-1$
	private static final int SUMMARY_REVIEWID_WIDTH = 75;
	private static final int SUMMARY_CHANGESETS_WIDTH = 75;
	private static final int SUMMARY_AUTHOR_WIDTH = 150;
	private static final int SUMMARY_REVIEWER_WIDTH = 150;
	private static final int SUMMARY_RESULT_WIDTH = 75;
	private static final int SUMMARY_COMMENT_WIDTH = 500;

	private List<ReviewSubTask> selectedReviews = new ArrayList<ReviewSubTask>();
	private Shell shell = null;
	private ProgressMonitorDialog monitor;

	/**
	 * Default constructor.
	 */
	public ReviewSummaryTaskEditorPart() {
		setPartName(Messages.ReviewSummaryTaskEditorPart_Partname);
	}

	/**
	 * Create a list that contains all review tasks that are associated with the
	 * current task.
	 */
	public void createControl(Composite parent, FormToolkit toolkit) {
		shell = parent.getShell();
		monitor = new ProgressMonitorDialog(shell);

		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.horizontalSpan = 4;

		Section summarySection = createSection(parent, toolkit, ExpandableComposite.TITLE_BAR
				| ExpandableComposite.TWISTIE | ExpandableComposite.EXPANDED);
		summarySection.setLayout(new FillLayout(SWT.HORIZONTAL));
		summarySection.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		summarySection.setText(Messages.ReviewSummaryTaskEditorPart_Partname);
		Composite reviewResultsComposite = toolkit.createComposite(summarySection);
		reviewResultsComposite.setLayout(new GridLayout(1, false));
		TableViewer reviewResults = createResultsTableViewer(reviewResultsComposite);

		reviewResults.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		summarySection.setClient(reviewResultsComposite);

		Composite buttonGroup = new Composite(reviewResultsComposite, SWT.NONE);
		buttonGroup.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, true));
		buttonGroup.setLayout(new GridLayout(5, true));

		Button removeChangesets = toolkit.createButton(buttonGroup, "Remove Changeset(s)", SWT.NONE);
		removeChangesets.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 1));
		removeChangesets.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub

			}

			public void widgetSelected(SelectionEvent arg0) {
				removeChangesetFromTask();
			}
		});
		Label empty2 = new Label(buttonGroup, SWT.NONE);
		empty2.setText("");

		// create context menue
		Menu contextMenue = new Menu(reviewResults.getControl());
		org.eclipse.swt.widgets.MenuItem menueItem = new org.eclipse.swt.widgets.MenuItem(contextMenue, SWT.PUSH);
		menueItem.setText("remove changeset ...");
		menueItem.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent arg0) {
			}

			public void widgetSelected(SelectionEvent arg0) {
				removeChangesetFromTask();
			}
		});
		reviewResults.getTable().setMenu(contextMenue);

	}

	/**
	 * Remove changesets from selected task.
	 */
	protected void removeChangesetFromTask() {
		if (selectedReviews.size() != 0) {
			openEditReviewDialog();
		} else {
			MessageDialog
					.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
							"No review selected",
							"Please select a review from which one or more changesets should be removed!");
		}
	}

	/**
	 * Opens a dialog that shows the changesets that can be removed from the
	 * review task. Selected changesets will be removed.
	 */
	@SuppressWarnings("deprecation")
	protected void openEditReviewDialog() {
		@SuppressWarnings("unused")
		TaskMapper mapper = new TaskMapper(getTaskEditorPage().getTaskRepository());

		TaskDataManager taskDataManager = (TaskDataManager) TasksUi.getTaskDataManager();
		try {
			List<Review> reviews = ReviewsUtil.getReviewAttachmentFromTask(taskDataManager, TasksUi
					.getRepositoryModel(), getTaskEditorPage().getTaskRepository(), selectedReviews.get(0).getTask());

			Review review = reviews.get(0);

			ListSelectionDialog dlg = UiUtil.createScopeItemListDialog(shell, review.getScope(),
					"please select the changesets that should be removed.");

			if (dlg.open() == IStatus.OK) {
				List<Changeset> del = getChangesetsFromDlgResult(dlg);
				final Review newReview = UiUtil.removeChangesetsFromReview(review, del);

				// update review task attachment
				try {
					monitor.run(true, true, new IRunnableWithProgress() {

						public void run(IProgressMonitor prog) throws InvocationTargetException, InterruptedException {

							try {
								// update datastructure
								ReviewSubTask reviewSubTask = selectedReviews.get(0);
								ReviewResult result = newReview.getResult();
								if (result == null) {
									result = ReviewFactory.eINSTANCE.createReviewResult();
									result.setText("");
								}
								result.setRating(Rating.REASSIGNED);
								reviewSubTask.setResult(Rating.REASSIGNED);
								newReview.setResult(result);
								reviewSubTask.setReview(newReview);

								UiUtil.updateReview(reviewSubTask, getModel(), prog);
							} catch (CoreException e) {
								e.printStackTrace();
							}
						}

					});

					getTaskEditorPage().refreshFormContent();
				} catch (Exception e) {
					MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
							"Cannot update task", "A problem occurred while updating the task: " + e.getMessage());
				}
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}

	}

	private List<Changeset> getChangesetsFromDlgResult(ListSelectionDialog dlg) {
		List<Changeset> ret = new ArrayList<Changeset>();

		for (Object c : dlg.getResult()) {
			Changeset changeset = (Changeset) c;
			ret.add(changeset);
		}

		return ret;
	}

	private TableViewer createResultsTableViewer(Composite reviewResultsComposite) {
		TableViewer reviewResults = new TableViewer(reviewResultsComposite, SWT.FULL_SELECTION);
		reviewResults.getTable().setHeaderVisible(true);
		UiUtil
				.createColumn(reviewResults, Messages.ReviewSummaryTaskEditorPart_Header_ReviewId,
						SUMMARY_REVIEWID_WIDTH);
		UiUtil.createColumn(reviewResults, Messages.ReviewSummaryTaskEditorPart_Header_Changesets,
				SUMMARY_CHANGESETS_WIDTH);
		UiUtil.createColumn(reviewResults, Messages.ReviewSummaryTaskEditorPart_Header_Author, SUMMARY_AUTHOR_WIDTH);
		UiUtil
				.createColumn(reviewResults, Messages.ReviewSummaryTaskEditorPart_Header_Reviewer,
						SUMMARY_REVIEWER_WIDTH);
		UiUtil.createColumn(reviewResults, Messages.ReviewSummaryTaskEditorPart_Header_Result, SUMMARY_RESULT_WIDTH);
		UiUtil.createColumn(reviewResults, Messages.ReviewSummaryTaskEditorPart_Header_Comment, SUMMARY_COMMENT_WIDTH);

		reviewResults.setContentProvider(new IStructuredContentProvider() {

			public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
			}

			public void dispose() {
			}

			public Object[] getElements(Object inputElement) {
				if (inputElement instanceof ITaskContainer) {
					ITaskContainer taskContainer = (ITaskContainer) inputElement;

					List<ReviewSubTask> reviewSubTasks = ReviewsUtil.getReviewSubTasksFor(taskContainer, TasksUi
							.getTaskDataManager(), TasksUi.getRepositoryModel(), getModel().getTaskRepository(),
							new NullProgressMonitor());

					return reviewSubTasks.toArray(new ReviewSubTask[reviewSubTasks.size()]);

				}
				return null;
			}
		});

		reviewResults.setLabelProvider(new TableLabelProvider() {
			private static final int COLUMN_ID = 0;
			private static final int COLUMN_REVISIONS = 1;
			private static final int COLUMN_AUTHOR = 2;
			private static final int COLUMN_REVIEWER = 3;
			private static final int COLUMN_RESULT = 4;
			private static final int COLUMN_COMMENT = 5;

			public Image getColumnImage(Object element, int columnIndex) {
				if (columnIndex == COLUMN_RESULT) {
					ReviewSubTask subtask = (ReviewSubTask) element;
					switch (subtask.getResult()) {
					case FAILED:
						return Images.REVIEW_RESULT_FAILED.createImage();
					case WARNING:
						return Images.REVIEW_RESULT_WARNING.createImage();
					case PASSED:
						return Images.REVIEW_RESULT_PASSED.createImage();
					case NONE:
						return Images.REVIEW_RESULT_NONE.createImage();
					case REASSIGNED:
						return Images.REVIEW_RESULT_NONE.createImage();
					}
				}
				return null;
			}

			public String getColumnText(Object element, int columnIndex) {
				ReviewSubTask subtask = (ReviewSubTask) element;
				switch (columnIndex) {
				case COLUMN_ID:
					return subtask.getTask().getTaskId();
				case COLUMN_REVISIONS:
					return subtask.getChangesets();
				case COLUMN_AUTHOR:
					return subtask.getAuthor();
				case COLUMN_REVIEWER:
					return subtask.getReviewer();
				case COLUMN_RESULT:
					return subtask.getResult().getName();
				case COLUMN_COMMENT:
					return subtask.getComment();
				default:
					return null;
				}
			}
		});
		reviewResults.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				if (selection.getFirstElement() instanceof ReviewSubTask) {
					Object[] oSelectedChs = selection.toArray();
					selectedReviews = new ArrayList<ReviewSubTask>();
					for (int i = 0; i < oSelectedChs.length; i++)
						selectedReviews.add((ReviewSubTask) oSelectedChs[i]);

				}
			}
		});

		reviewResults.setInput(getModel().getTask());
		reviewResults.addDoubleClickListener(new IDoubleClickListener() {

			public void doubleClick(DoubleClickEvent event) {
				if (!event.getSelection().isEmpty()) {
					ITask task = ((ReviewSubTask) ((IStructuredSelection) event.getSelection()).getFirstElement())
							.getTask();
					TasksUiUtil.openTask(task);
				}
			}
		});
		return reviewResults;
	}

}
