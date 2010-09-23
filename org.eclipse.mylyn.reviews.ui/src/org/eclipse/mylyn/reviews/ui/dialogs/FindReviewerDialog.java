package org.eclipse.mylyn.reviews.ui.dialogs;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

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
import org.eclipse.mylyn.reviews.core.planning.IPlanningUtils;
import org.eclipse.mylyn.reviews.core.planning.User;
import org.eclipse.mylyn.reviews.core.scm.IChangeset;
import org.eclipse.mylyn.reviews.core.scm.TaskMapper;
import org.eclipse.mylyn.reviews.ui.UiUtil;
import org.eclipse.mylyn.reviews.ui.editors.TableLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Dialog that integrates the functionality of the planning plug-in into the ui
 * plug-in.
 * 
 * @author Stefan Reiterer
 * 
 */
public class FindReviewerDialog extends StatusDialog {
	private Text reviewerText;
	private TableViewer reviewerList;
	private List<IChangeset> changesets;
	private String selectedReviewer;
	private ProgressMonitorDialog monitor;
	private IPlanningUtils planningUtils;

	/**
	 * Constructor that initializes the dialog with the given values.
	 * 
	 * @param parent
	 *            Parent shell.
	 * @param changesets
	 *            Changesets, for which a review tasks should be created.
	 */
	public FindReviewerDialog(Shell parent, List<IChangeset> changesets) {
		super(parent);
		TaskMapper mapper = new TaskMapper();

		this.changesets = changesets;
		monitor = new ProgressMonitorDialog(parent);
		planningUtils = mapper.getPlanningUtils();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	protected void okPressed() {
		super.okPressed();
	}

	/**
	 * Set window settings.
	 */
	protected Control createContents(Composite parent) {
		parent.setSize(350, 400);
		super.createContents(parent);

		return parent;
	}

	/**
	 * Return the selected reviewer.
	 * 
	 */
	public String getReviewer() {
		return selectedReviewer;
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

		// Create GridLayout with 4 Columns.
		Composite formArea = new Composite(composite, SWT.FILL);
		formArea.setLayout(new GridLayout(5, true));
		formArea.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false, 1, 1));

		// Assigned to
		Label reviewerLabel = new Label(formArea, SWT.FILL);
		reviewerLabel.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 1, 1));
		reviewerLabel.setText(Messages.FindReviewerDialog_Reviewer);

		Label emptyLabel = new Label(formArea, SWT.FILL);
		emptyLabel.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 1, 1));
		emptyLabel.setText("");

		reviewerText = new Text(formArea, SWT.BORDER);
		reviewerText.setText(""); //$NON-NLS-1$
		reviewerText.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 3, 1));
		reviewerText.addListener(SWT.KeyUp, new Listener() {

			public void handleEvent(Event arg0) {
				selectedReviewer = reviewerText.getText();
				keyListenerReviewerText();
			}

		});
		reviewerText.setFocus();

		// Empty row
		Label empty2 = new Label(composite, SWT.NORMAL);
		empty2.setText(""); //$NON-NLS-1$

		Label listLabel = new Label(composite, SWT.NORMAL);
		listLabel.setText("Reviewer pool:"); //$NON-NLS-1$

		// Create table
		createReviewerTable(composite);

		return parent;
	}

	private void createReviewerTable(Composite formArea) {

		reviewerList = new TableViewer(formArea, SWT.FULL_SELECTION);

		reviewerList.getTable().setHeaderVisible(true);
		reviewerList.setContentProvider(new IStructuredContentProvider() {

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}

			public void dispose() {
			}

			@SuppressWarnings("unchecked")
			public Object[] getElements(Object inputElement) {
				List<String> reviewers = (List<String>) inputElement;

				return reviewers.toArray(new String[0]);
			}
		});

		reviewerList.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				if (selection.getFirstElement() instanceof String) {
					Object[] oSelectedReviewer = selection.toArray();
					selectedReviewer = (String) oSelectedReviewer[0];
					reviewerText.setText(selectedReviewer);
					keyListenerReviewerText();
				}
			}
		});

		UiUtil.createColumn(reviewerList, Messages.FindReviewerDialog_Username, 280);

		reviewerList.setLabelProvider(new TableLabelProvider() {
			final int COLUMN_USER = 0;

			public String getColumnText(Object element, int columnIndex) {
				String username = (String) element;
				switch (columnIndex) {
				case COLUMN_USER:
					return username;

				default:
					return null;
				}
			}

			public Image getColumnImage(Object element, int columnIndex) {
				return null;
			}
		});
		reviewerList.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		refreshTable();
	}

	private void refreshTable() {
		final List<String> repos = planningUtils.getRepositoriesFromChangesets(changesets);
		final TaskMapper mapper = new TaskMapper();
		List<String> users = new ArrayList<String>();
		final List<String> tUsers = new ArrayList<String>();

		try {
			monitor.run(true, true, new IRunnableWithProgress() {

				public void run(IProgressMonitor prog) throws InvocationTargetException, InterruptedException {
					prog.beginTask(Messages.FindReviewerDialog_Progressbar_Title, repos.size());
					int i = 0;
					for (String repository : repos) {
						prog.worked(i++);

						List<String> users = null;

						try {
							if (planningUtils.isConfigAvailable(repository)) {
								users = getStringList(planningUtils.getPlanningConfiguration(new NullProgressMonitor(),
										repository).getGroup().getUsers());
							} else {
								users = mapper.getSCMRepositoryUsers(new NullProgressMonitor(), repository);
							}
						} catch (CoreException e) {
							// FIXME: improve exception handling
							e.printStackTrace();
						}

						tUsers.addAll(users);
					}
				}

			});
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		users = planningUtils.removeDuplicates(tUsers);

		users.removeAll(planningUtils.getAuthors(changesets));

		selectedReviewer = planningUtils.getRandomUser(users);
		reviewerText.setText(selectedReviewer);
		keyListenerReviewerText();

		reviewerList.setInput(users);
	}

	/** Convert a list of users into a list of strings.
	 * 
	 * @param users List of {@link User}s
	 * @return List of {@link String}s
	 */
	protected List<String> getStringList(List<User> users) {
		List<String> ret = new ArrayList<String>();

		for (User user : users) {
			ret.add(user.getUsername());
		}

		return ret;
	}

	private void keyListenerReviewerText() {

		if (reviewerText.getText().equals("")) { //$NON-NLS-1$
			updateStatus(new Status(IStatus.ERROR, this.getClass().getName(), "")); //$NON-NLS-1$
		} else {
			updateStatus(new Status(IStatus.OK, this.getClass().getName(),
					Messages.CreateReviewForChangesetsDialog_ReviewCanBeCreated));
		}
	}

}
