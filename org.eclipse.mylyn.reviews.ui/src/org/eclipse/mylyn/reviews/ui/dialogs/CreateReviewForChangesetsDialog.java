package org.eclipse.mylyn.reviews.ui.dialogs;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.dialogs.StatusDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.reviews.core.ReviewSubTask;
import org.eclipse.mylyn.reviews.core.ReviewsUtil;
import org.eclipse.mylyn.reviews.core.model.review.Rating;
import org.eclipse.mylyn.reviews.core.model.review.Review;
import org.eclipse.mylyn.reviews.core.model.review.ReviewFactory;
import org.eclipse.mylyn.reviews.core.model.review.ReviewResult;
import org.eclipse.mylyn.reviews.core.model.review.ScopeItem;
import org.eclipse.mylyn.reviews.core.model.review.impl.ChangesetImpl;
import org.eclipse.mylyn.reviews.core.model.review.impl.ReviewFactoryImpl;
import org.eclipse.mylyn.reviews.core.scm.IChangeset;
import org.eclipse.mylyn.reviews.ui.CreateTask;
import org.eclipse.mylyn.reviews.ui.UiUtil;
import org.eclipse.mylyn.reviews.ui.editors.TableLabelProvider;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskContainer;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * This dialog allows the user to create a review for an existing changeset.
 * 
 * @author Stefan Reiterer
 * 
 */
public class CreateReviewForChangesetsDialog extends StatusDialog {
	private static final int TASKID_WIDTH = 50;
	private static final int CHANGESET_WIDTH = 140;
	private static final int ASSIGNEDTO_WIDTH = 140;
	private Text assignedTo;
	private TableViewer taskList;

	private TaskDataModel model;
	private List<ReviewSubTask> selectedSubTasks;
	private Button reassingOption;
	private Button newTaskOption;
	private AbstractTaskEditorPage taskEditorPage;
	private List<IChangeset> selectedChangesets;
	private ProgressMonitorDialog monitor;
	private Shell parentShell;
	
	private static Logger logger = Logger.getLogger(CreateReviewForChangesetsDialog.class.getName());

	public CreateReviewForChangesetsDialog(Shell parent, List<IChangeset> selectedChangesets, TaskDataModel model,
			ITask task, AbstractTaskEditorPage taskEditorPage) {
		super(parent);
		this.model = model;
		this.taskEditorPage = taskEditorPage;
		this.selectedChangesets = selectedChangesets;
		parentShell = parent.getShell();
		monitor = new ProgressMonitorDialog(parent);
	}

	public void setTitle(String title) {
		super.setTitle(title);
	}

	/**
	 * Set window settings.
	 */
	protected Control createContents(Composite parent) {
		parent.setSize(430, 500);
		super.createContents(parent);
		return parent;
	}

	/**
	 * Create the contents of the window.
	 */
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		composite.setLayout(new GridLayout(1, false));

		// Empty row
		Label empty1 = new Label(composite, SWT.NORMAL);
		empty1.setText(""); //$NON-NLS-1$

		// create review task option
		newTaskOption = new Button(composite, SWT.RADIO);
		newTaskOption.setSelection(true);
		newTaskOption.setText(Messages.CreateReviewForChangesetsDialog_CreateNewReviewTask);
		newTaskOption.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event event) {
				Button bE = (Button) event.widget;
				taskList.getTable().setEnabled(!bE.getSelection());
				assignedTo.setEnabled(bE.getSelection());

				updateStatus(new Status(IStatus.ERROR, this.getClass().getName(), "")); //$NON-NLS-1$

				if ((newTaskOption.getSelection() && !assignedTo.getText().equals("")) //$NON-NLS-1$
						|| (reassingOption.getSelection() && selectedSubTasks.size() > 0)) {
					updateStatus(new Status(IStatus.OK, this.getClass().getName(),
							Messages.CreateReviewForChangesetsDialog_ReviewCanBeCreated));
				}
			}
		});

		// Create GridLayout with 4 Columns.
		Composite formArea = new Composite(composite, SWT.FILL);
		formArea.setLayout(new GridLayout(4, true));

		// Assigned to
		Label assignLabel = new Label(formArea, SWT.NORMAL);
		assignLabel.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false, 1, 1));
		assignLabel.setText(Messages.CreateReviewForChangesetsDialog_AssignedTo);
		assignedTo = new Text(formArea, SWT.BORDER | SWT.FILL);
		assignedTo.setText(""); //$NON-NLS-1$
		assignedTo.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false, 2, 1));
		assignedTo.addListener(SWT.KeyUp, new Listener() {

			public void handleEvent(Event arg0) {
				keyListenerAssignTo();
			}

		});
		assignedTo.setFocus();

		// Planning plug-in button
		Button findReviewer = new Button(formArea, SWT.PUSH);
		findReviewer.setText("find Reviewer...");
		findReviewer.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent arg0) {
			}

			public void widgetSelected(SelectionEvent arg0) {
				openPlanningDialog();
			}

		});

		// Empty row
		Label empty2 = new Label(composite, SWT.NORMAL);
		empty2.setText(""); //$NON-NLS-1$

		// create reassign option
		reassingOption = new Button(composite, SWT.RADIO);
		reassingOption.setText(Messages.CreateReviewForChangesetsDialog_AssignChangesetToExistingReview);

		// Create table
		createTaskTable(composite);

		updateStatus(new Status(IStatus.ERROR, this.getClass().getName(), "")); //$NON-NLS-1$

		return parent;
	}

	protected void openPlanningDialog() {
		FindReviewerDialog dlg = new FindReviewerDialog(parentShell, selectedChangesets);
		dlg.setTitle("Find reviewer");
		int status = dlg.open();

		if (status == IStatus.OK) {
			logger.info("ok pressed: " + dlg.getReviewer());
			assignedTo.setText(dlg.getReviewer());
		}
		keyListenerAssignTo();
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	protected void okPressed() {

		if (getNewTaskOption().getSelection()) {
			createReviewForSelectedChangesets(getAssignedTo().getText());
		} else {
			reassignReviewForSelectedChangesets(getSelectedSubTasks().get(0));
		}

		super.okPressed();
	}

	private void keyListenerAssignTo() {

		if (assignedTo.getText().equals("")) { //$NON-NLS-1$
			updateStatus(new Status(IStatus.ERROR, this.getClass().getName(), "")); //$NON-NLS-1$
		} else {
			updateStatus(new Status(IStatus.OK, this.getClass().getName(),
					Messages.CreateReviewForChangesetsDialog_ReviewCanBeCreated));
		}
	}

	private void createTaskTable(Composite formArea) {

		taskList = new TableViewer(formArea, SWT.FULL_SELECTION);

		taskList.getTable().setHeaderVisible(true);
		taskList.setContentProvider(new IStructuredContentProvider() {

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}

			public void dispose() {
			}

			@SuppressWarnings("unchecked")
			public Object[] getElements(Object inputElement) {
				List<ReviewSubTask> subTasks = (List<ReviewSubTask>) inputElement;

				return subTasks.toArray(new ReviewSubTask[0]);
			}
		});

		taskList.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				if (selection.getFirstElement() instanceof ReviewSubTask) {
					Object[] oSelectedSubTasks = selection.toArray();
					selectedSubTasks = new ArrayList<ReviewSubTask>();
					for (int i = 0; i < oSelectedSubTasks.length; i++)
						selectedSubTasks.add((ReviewSubTask) oSelectedSubTasks[i]);

					if (selectedChangesets.size() == 0) {
						updateStatus(new Status(IStatus.ERROR, this.getClass().getName(), "")); //$NON-NLS-1$
					} else {
						updateStatus(new Status(IStatus.OK, this.getClass().getName(),
								Messages.CreateReviewForChangesetsDialog_ReviewCanBeCreated2));
					}
				}
			}
		});

		UiUtil.createColumn(taskList, Messages.CreateReviewForChangesetsDialog_TaskId, TASKID_WIDTH);
		UiUtil.createColumn(taskList, Messages.CreateReviewForChangesetsDialog_Changesets_COL, CHANGESET_WIDTH);
		UiUtil.createColumn(taskList, Messages.CreateReviewForChangesetsDialog_AssignedToCOL, ASSIGNEDTO_WIDTH);

		taskList.setLabelProvider(new TableLabelProvider() {
			final int COLUMN_TASKID = 0;
			final int COLUMN_CHANGESET = 1;
			final int COLUMN_ASSIGNEDTO = 2;

			public String getColumnText(Object element, int columnIndex) {
				ReviewSubTask task = (ReviewSubTask) element;
				switch (columnIndex) {
				case COLUMN_TASKID:
					return task.getTask().getTaskId();
				case COLUMN_CHANGESET:
					return task.getChangesets();
				case COLUMN_ASSIGNEDTO:
					return task.getReviewer();
				default:
					return null;
				}
			}

			public Image getColumnImage(Object element, int columnIndex) {
				return null;
			}
		});
		taskList.getControl().setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true));

		refreshTable();
		taskList.getTable().setEnabled(false);
	}

	private void refreshTable() {
		// Input
		List<ReviewSubTask> subTasks = ReviewsUtil.getReviewSubTasksFor((ITaskContainer) model.getTask(), TasksUi
				.getTaskDataManager(), TasksUi.getRepositoryModel(), taskEditorPage.getTaskRepository(),
				new NullProgressMonitor());
		taskList.setInput(subTasks);
	}

	/**
	 * @return the assignedTo
	 */
	public Text getAssignedTo() {
		return assignedTo;
	}

	/**
	 * @param assignedTo
	 *            the assignedTo to set
	 */
	public void setAssignedTo(Text assignedTo) {
		this.assignedTo = assignedTo;
	}

	/**
	 * @return the selectedSubTasks
	 */
	public List<ReviewSubTask> getSelectedSubTasks() {
		return selectedSubTasks;
	}

	/**
	 * @param selectedSubTasks
	 *            the selectedSubTasks to set
	 */
	public void setSelectedSubTasks(List<ReviewSubTask> selectedSubTasks) {
		this.selectedSubTasks = selectedSubTasks;
	}

	/**
	 * @return the reassingOption
	 */
	public Button getReassingOption() {
		return reassingOption;
	}

	/**
	 * @param reassingOption
	 *            the reassingOption to set
	 */
	public void setReassingOption(Button reassingOption) {
		this.reassingOption = reassingOption;
	}

	/**
	 * @return the newTaskOption
	 */
	public Button getNewTaskOption() {
		return newTaskOption;
	}

	/**
	 * @param newTaskOption
	 *            the newTaskOption to set
	 */
	public void setNewTaskOption(Button newTaskOption) {
		this.newTaskOption = newTaskOption;
	}

	@SuppressWarnings( { "deprecation" })
	private void createReviewForSelectedChangesets(final String reviewer) {
		final Review review = ReviewFactory.eINSTANCE.createReview();

		for (int i = 0; i < selectedChangesets.size(); i++) {
			ChangesetImpl csImpl = (ChangesetImpl) ReviewFactoryImpl.init().createChangeset();

			csImpl.setComment(selectedChangesets.get(i).getMessage());
			csImpl.setProjecturl(selectedChangesets.get(i).getProjectUrl());
			csImpl.setRevision(selectedChangesets.get(i).getRevision());
			csImpl.setAuthor(selectedChangesets.get(i).getAutor());

			review.getScope().add(csImpl);
		}

		try {
			try {
				monitor.run(true, true, new IRunnableWithProgress() {

					public void run(IProgressMonitor prog) throws InvocationTargetException, InterruptedException {

						prog.beginTask(Messages.CreateReviewForChangesetsDialog_CreateReviewForSelectedChangeset, 10);
						CreateTask createTask = new CreateTask(model, review, reviewer, taskEditorPage.getTask());

						prog.worked(3);
						createTask.schedule();
						prog.worked(4);
						createTask.join();
						prog.worked(7);
						ITask subTask = createTask.getNewRepoTask();
						if (subTask == null) {
							logger.log(Level.FINER, "created subtask is null!");
						} else {
							try {
								UiUtil.updateReview(review, subTask, model, new NullProgressMonitor());
							} catch (CoreException e) {
								e.printStackTrace();
							}
						}
						prog.done();

					}
				});

				taskEditorPage.refreshFormContent();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}

		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	@SuppressWarnings( { "deprecation" })
	private void reassignReviewForSelectedChangesets(final ReviewSubTask rst) {

		Review review = rst.getReview();

		for (int i = 0; i < selectedChangesets.size(); i++) {
			ChangesetImpl csImpl = (ChangesetImpl) ReviewFactoryImpl.init().createChangeset();

			csImpl.setComment(selectedChangesets.get(i).getMessage());
			csImpl.setProjecturl(selectedChangesets.get(i).getProjectUrl());
			csImpl.setRevision(selectedChangesets.get(i).getRevision());
			csImpl.setAuthor(selectedChangesets.get(i).getAutor());

			if (!reviewAlreadyExists(review, csImpl))
				review.getScope().add(csImpl);
		}

		// Change the state of the review sub task
		ReviewResult result = review.getResult();
		if (result == null) {
			result = ReviewFactory.eINSTANCE.createReviewResult();
			result.setText("");
		}
		result.setRating(Rating.REASSIGNED);
		review.setResult(result);
		review.setResult(result);

		try {

			monitor.run(true, true, new IRunnableWithProgress() {

				public void run(IProgressMonitor prog) throws InvocationTargetException, InterruptedException {

					try {
						UiUtil.updateReview(rst, model, prog);
					} catch (CoreException e) {
						e.printStackTrace();
					}
				}

			});

			taskEditorPage.refreshFormContent();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean reviewAlreadyExists(Review review, ChangesetImpl csImpl) {
		for (ScopeItem item : review.getScope()) {
			if (csImpl.getRevision().equals(((ChangesetImpl) item).getRevision()))
				return true;
		}
		return false;
	}

}
