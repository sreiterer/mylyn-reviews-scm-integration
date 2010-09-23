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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.mylyn.reviews.core.scm.IChangeset;
import org.eclipse.mylyn.reviews.core.scm.TaskMapper;
import org.eclipse.mylyn.reviews.core.scm.serialization.model.Changeset;
import org.eclipse.mylyn.reviews.ui.UiUtil;
import org.eclipse.mylyn.reviews.ui.dialogs.CreateReviewForChangesetsDialog;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;
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
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

/**
 * Part of the task editor that provides the functionality for the creation of
 * review tasks.
 * 
 * @author Kilian Matt
 * @author Stefan Reiterer
 */
public class CreateReviewTaskEditorPart extends AbstractTaskEditorPart {
	public static final int REVISION_WIDTH = 50;
	public static final int DESCRIPTION_WIDTH = 400;
	public static final int AUTOR_WIDTH = 100;
	public static final int DATE_WIDTH = 250;

	public static final String ID_PART_CREATEREVIEW = "org.eclipse.mylyn.reviews.ui.editors.parts.createreview"; //$NON-NLS-1$

	private List<IChangeset> selectedChangesets;

	/**
	 * Default zero argument constructor.
	 */
	public CreateReviewTaskEditorPart() {
		selectedChangesets = new ArrayList<IChangeset>();
		setPartName(Messages.CreateReviewTaskEditorPart_ReviewItems);
	}

	/**
	 * Create list of changesets and the button for the creation/reassignment of
	 * reviews.
	 */
	public void createControl(Composite parent, FormToolkit toolkit) {
		try {
			Section section = createSection(parent, toolkit, ExpandableComposite.TWISTIE
					| ExpandableComposite.TITLE_BAR | ExpandableComposite.EXPANDED);
			section.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			section.setLayout(new FillLayout());
			Composite composite = toolkit.createComposite(section);
			composite.setLayout(new GridLayout(1, false));
			TableViewer reviewItemTable = new TableViewer(composite, SWT.FULL_SELECTION | SWT.MULTI);
			reviewItemTable.getTable().setHeaderVisible(true);
			reviewItemTable.setContentProvider(new ArrayContentProvider());
			reviewItemTable.addSelectionChangedListener(new ISelectionChangedListener() {

				public void selectionChanged(SelectionChangedEvent event) {
					IStructuredSelection selection = (IStructuredSelection) event.getSelection();
					if (selection.getFirstElement() instanceof Changeset) {
						Object[] oSelectedChs = selection.toArray();
						selectedChangesets = new ArrayList<IChangeset>();
						for (int i = 0; i < oSelectedChs.length; i++)
							selectedChangesets.add((IChangeset) oSelectedChs[i]);

					}
				}
			});

			UiUtil.createColumn(reviewItemTable, Messages.CreateReviewTaskEditorPart_Header_Revision, REVISION_WIDTH);
			UiUtil.createColumn(reviewItemTable, Messages.CreateReviewTaskEditorPart_Header_Description,
					DESCRIPTION_WIDTH);
			UiUtil.createColumn(reviewItemTable, Messages.CreateReviewTaskEditorPart_Header_Author, AUTOR_WIDTH);
			UiUtil.createColumn(reviewItemTable, Messages.CreateReviewTaskEditorPart_Header_Date, DATE_WIDTH);
			reviewItemTable.setLabelProvider(new TableLabelProvider() {
				final int COLUMN_REVISION = 0;
				final int COLUMN_DESCRIPTION = 1;
				final int COLUMN_AUTHOR = 2;
				final int COLUMN_DATE = 3;

				public String getColumnText(Object element, int columnIndex) {
					IChangeset changeset = (IChangeset) element;
					switch (columnIndex) {
					case COLUMN_REVISION:
						return changeset.getRevision();
					case COLUMN_DESCRIPTION:
						return changeset.getMessage();
					case COLUMN_AUTHOR:
						return changeset.getAutor();
					case COLUMN_DATE:
						return changeset.getDate().toString();
					default:
						return null;
					}
				}

				public Image getColumnImage(Object element, int columnIndex) {
					return null;
				}
			});
			reviewItemTable.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

			Composite buttonComposite = toolkit.createComposite(composite);
			buttonComposite.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, true));
			buttonComposite.setLayout(new GridLayout(5, true));

			Button submitButton = toolkit.createButton(buttonComposite,
					Messages.CreateReviewTaskEditorPart_Create_Review, SWT.PUSH);
			submitButton.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, true, 1, 1));
			submitButton.addSelectionListener(new SelectionListener() {
				public void widgetSelected(SelectionEvent e) {
					createReview();
				}

				public void widgetDefaultSelected(SelectionEvent e) {
					widgetDefaultSelected(e);
				}
			});

			section.setClient(composite);
			reviewItemTable.setInput(getChangesets());

			// create context menue for table
			Menu contextMenue = new Menu(reviewItemTable.getControl());
			org.eclipse.swt.widgets.MenuItem menueItem = new org.eclipse.swt.widgets.MenuItem(contextMenue, SWT.PUSH);
			menueItem.setText("create review ...");
			menueItem.addSelectionListener(new SelectionListener() {

				public void widgetDefaultSelected(SelectionEvent arg0) {
				}

				public void widgetSelected(SelectionEvent arg0) {
					createReview();
				}
			});
			reviewItemTable.getTable().setMenu(contextMenue);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Trigger the creation of reviews.
	 */
	protected void createReview() {
		if (selectedChangesets.size() != 0) {
			openCreateReviewDialog();
		} else {
			MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					Messages.createReviewTaskEditorPart_NoChangesetSelected_title,
					Messages.createReviewTaskEditorPart_NoChangesetSelected_message);
		}
	}

	/**
	 * This method opens an dialog that provides the functionality for creation
	 * review tasks based on the selected changesets.
	 */
	protected void openCreateReviewDialog() {

		CreateReviewForChangesetsDialog dlg = new CreateReviewForChangesetsDialog(PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getShell(), selectedChangesets, getModel(), getTaskEditorPage().getTask(),
				getTaskEditorPage());

		dlg.setTitle(org.eclipse.mylyn.reviews.ui.dialogs.Messages.CreateReviewForChangeseetsDialog_TITLE);
		dlg.open();
	}

	private List<Changeset> getChangesets() {

		TaskDataModel model = getModel();
		String taskId = model.getTask().getTaskId();

		// get the changesets that are associated with the given task
		TaskMapper mapper = new TaskMapper(getModel().getTaskRepository());

		return (List<Changeset>) mapper.getChangesetsForTaskWSOnly(new NullProgressMonitor(), taskId);
	}
}
