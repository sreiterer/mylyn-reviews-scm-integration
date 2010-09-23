package org.eclipse.mylyn.reviews.ui;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.reviews.core.ReviewSubTask;
import org.eclipse.mylyn.reviews.core.ReviewsUtil;
import org.eclipse.mylyn.reviews.core.model.review.Changeset;
import org.eclipse.mylyn.reviews.core.model.review.Review;
import org.eclipse.mylyn.reviews.core.model.review.ScopeItem;
import org.eclipse.mylyn.reviews.ui.dialogs.Messages;
import org.eclipse.mylyn.reviews.ui.dialogs.SIContentProvider;
import org.eclipse.mylyn.reviews.ui.dialogs.SILabelProvider;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;
import org.eclipse.mylyn.tasks.core.sync.SynchronizationJob;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ListDialog;
import org.eclipse.ui.dialogs.ListSelectionDialog;

@SuppressWarnings("restriction")
public class UiUtil {

	/**
	 * Create a column with the given title and with.
	 * 
	 * @param parent
	 *            Table in which the column should be created.
	 * @param column
	 *            Title Title of the column.
	 * @param width
	 *            Width of the column.
	 * @return
	 */
	public static TableViewerColumn createColumn(TableViewer parent, String columnTitle, int width) {
		TableViewerColumn column = new TableViewerColumn(parent, SWT.LEFT);
		column.getColumn().setText(columnTitle);
		column.getColumn().setWidth(width);
		column.getColumn().setResizable(true);
		return column;
	}

	/**
	 * This method adds a new attachment to the given review task. The content
	 * of the attachment updates the existing review.
	 * 
	 * @param review
	 *            Review information.
	 * @param model
	 *            model that holds the task data
	 * @param prog
	 *            progress monitor.
	 * @throws CoreException
	 * @throws InterruptedException
	 */
	public static void updateReview(ReviewSubTask review, TaskDataModel model, IProgressMonitor prog)
			throws CoreException, InterruptedException {

		updateReview(review.getReview(), review.getTask(), model, prog);
	}

	/**
	 * This method adds a new attachment to the given review task. The content
	 * of the attachment updates the existing review.
	 * 
	 **/
	public static void updateReview(Review review, ITask subTask, TaskDataModel model, IProgressMonitor prog)
			throws CoreException, InterruptedException {
		prog.beginTask(Messages.CreateReviewForChangesetsDialog_CreatteReviewForSelectedChangesets, 10);
		prog.worked(1);
		byte[] attachmentBytes = ReviewsUtil.createAttachment(review);
		prog.worked(3);

		ReviewCommentTaskAttachmentSource attachment = new ReviewCommentTaskAttachmentSource(attachmentBytes);
		prog.worked(4);

		TaskAttribute attachmentAttribute = model.getTaskData().getAttributeMapper().createTaskAttachment(
				model.getTaskData());

		AbstractRepositoryConnector connector = TasksUi.getRepositoryConnector(model.getTaskRepository()
				.getConnectorKind());
		prog.worked(6);

		connector.getTaskAttachmentHandler().postContent(model.getTaskRepository(), subTask, attachment,
				"review result", //$NON-NLS-1$
				attachmentAttribute, new NullProgressMonitor());
		prog.worked(8);

		// synchronize with repository
		Set<ITask> tsks = new HashSet<ITask>();
		tsks.add(subTask);

		SynchronizationJob jbTask = TasksUiPlugin.getTaskJobFactory().createSynchronizeTasksJob(connector, tsks);
		jbTask.setFullSynchronization(true);
		jbTask.schedule();
		jbTask.join();
		prog.worked(9);

		Set<TaskRepository> repos = new HashSet<TaskRepository>();
		repos.add(model.getTaskRepository());
		SynchronizationJob jb = TasksUiPlugin.getTaskJobFactory().createSynchronizeRepositoriesJob(repos);
		jb.setFullSynchronization(true);
		jb.schedule();
		jb.join();
		prog.worked(10);
		prog.done();
	}

	/**
	 * Remove changesets from review.
	 * 
	 * @param review
	 *            Review object in which the given changesets have been removed.
	 * @param changesets
	 *            Changesets to remove.
	 * @return
	 */
	public static Review removeChangesetsFromReview(Review review, List<Changeset> changesets) {
		Review ret = review;

		ret.getScope().removeAll(changesets);

		return ret;
	}

	/**
	 * Creation of a list dialog.
	 * 
	 * @return initialized {@link ListDialog}
	 */
	public static ListSelectionDialog createScopeItemListDialog(Shell shell, List<ScopeItem> input, String message) {
		SILabelProvider labelProvider = new SILabelProvider();
		SIContentProvider contentProvider = new SIContentProvider();
		ListSelectionDialog dlg = new ListSelectionDialog(shell, input, contentProvider, labelProvider, message);

		return dlg;
	}

	/**
	 * Print the given task for debugging purposes.
	 * 
	 * This method is deprecated, because it is only used for test purposes.
	 * 
	 * @param task
	 *            Task that should be printed.
	 * @throws CoreException
	 */
	@Deprecated
	public static void printTask(ITask task) throws CoreException {

		System.out.println("Task: " + task.getTaskId() + " : " + task.getSummary()); //$NON-NLS-1$ //$NON-NLS-2$
		TaskData taskData = TasksUi.getTaskDataManager().getTaskData(task);

		System.out.println("   Attributes: "); //$NON-NLS-1$
		for (String ta : taskData.getRoot().getAttributes().keySet()) {
			System.out.println("    " + ta + ": " + taskData.getRoot().getAttributes().get(ta).getValue()); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

}
