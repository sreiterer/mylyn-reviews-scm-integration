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
package org.eclipse.mylyn.reviews.ui.editors;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.compare.CompareUI;
import org.eclipse.compare.contentmergeviewer.TextMergeViewer;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.reviews.core.ReviewsUtil;
import org.eclipse.mylyn.reviews.core.model.review.Changeset;
import org.eclipse.mylyn.reviews.core.model.review.Rating;
import org.eclipse.mylyn.reviews.core.model.review.Review;
import org.eclipse.mylyn.reviews.core.model.review.ReviewFactory;
import org.eclipse.mylyn.reviews.core.model.review.ReviewResult;
import org.eclipse.mylyn.reviews.core.scm.IChangeset;
import org.eclipse.mylyn.reviews.core.scm.TaskMapper;
import org.eclipse.mylyn.reviews.core.scm.serialization.model.File;
import org.eclipse.mylyn.reviews.ui.Images;
import org.eclipse.mylyn.reviews.ui.UiUtil;
import org.eclipse.mylyn.reviews.ui.editors.compare.ChangesetCompareItem;
import org.eclipse.mylyn.reviews.ui.editors.compare.RevisionCompareEditorInput;
import org.eclipse.mylyn.reviews.ui.editors.compare.RevisionCompareInput;
import org.eclipse.mylyn.reviews.ui.editors.compare.ViewMode;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

/**
 * Create the part of the review tab in the task editor that provides the
 * functionality of performing code reviews.
 * 
 * @author Kilian Matt
 * @author Stefan Reiterer
 */
public class EditorSupport {
	public static final int REVISION_WIDTH = 70;
	public static final int DESCRIPTION_WIDTH = 500;
	public static final int AUTOR_WIDTH = 200;
	public static final int DATE_WIDTH = 300;
	public static final int OPERATION_WIDTH = 70;
	public static final int FILENAME_WIDTH = 500;

	private ReviewSubmitHandler handler;
	private Changeset selectedChangeset;
	private TaskRepository repository;

	/**
	 * Constructor that initializes the class with the given parameters.
	 * 
	 * @param input
	 *            Input of the task editor.
	 * @param handler
	 *            Handler for the submit button.
	 */
	public EditorSupport(ReviewTaskEditorInput input, ReviewSubmitHandler handler) {
		this.input = input;
		this.handler = handler;
		repository = getEditorInput().getModel().getTaskRepository();
	}

	private ReviewTaskEditorInput input;

	private TableViewer fileList;
	private TextMergeViewer viewer;
	private Composite container;
	private Section contentSection;
	private ComboViewer ratingList;

	/**
	 * Create the controls that are required for performing code reviews.
	 * 
	 * @param parent Parent control.
	 * @return 
	 */
	public Control createPartControl(Composite parent) {
		parent.setLayout(new GridLayout(1, true));
		parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		FormToolkit toolkit = new FormToolkit(parent.getDisplay());
		return createPartControl(parent, toolkit);
	}

	/** Create controls for review page.
	 * 
	 * @param parent Parent control.
	 * @param toolkit Toolkit for the creation of form controls.
	 * @return Page elements.
	 */
	public Control createPartControl(Composite parent, FormToolkit toolkit) {
		try {
			container = toolkit.createComposite(parent, SWT.NONE);
			container.setLayout(new GridLayout(1, true));
			container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

			Composite body = toolkit.createComposite(container);
			body.setLayout(new GridLayout(1, true));
			body.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

			// Changeset section
			createChangesetSection(body, toolkit);

			// Content section
			createContentList(body, toolkit);

			// Review section
			createReviewSection(body, toolkit);

		} catch (Exception e) {
			e.printStackTrace();

		}
		return container;
	}

	private void createReviewSection(Composite body, FormToolkit toolkit) {
		Section reviewSection = toolkit.createSection(container, ExpandableComposite.TITLE_BAR
				| ExpandableComposite.TWISTIE | ExpandableComposite.EXPANDED);

		reviewSection.setLayoutData(getLayoutData(false));
		reviewSection.setLayout(new FillLayout());
		reviewSection.setText(Messages.ReviewEditor_Review);

		Composite reviewComposite = toolkit.createComposite(reviewSection);
		reviewComposite.setLayout(new GridLayout(4, false));

		// check review privileges
		if (ReviewsUtil.hasPrivileges(ReviewsUtil.getTaskOwner(getEditorInput().getModel().getTask()), getEditorInput()
				.getModel().getTaskRepository())) {

			// Creation of the rating list
			toolkit.createLabel(reviewComposite, Messages.ReviewEditor_Rating);

			ratingList = new ComboViewer(reviewComposite, SWT.READ_ONLY | SWT.BORDER | SWT.FLAT);
			ratingList.setContentProvider(new ArrayContentProvider());

			ratingList.setLabelProvider(new LabelProvider() {
				public String getText(Object element) {
					return ((Rating) element).getName();
				}

				public Image getImage(Object element) {
					Rating rating = ((Rating) element);
					switch (rating) {
					case FAILED:
						return Images.REVIEW_RESULT_FAILED.createImage();
					case NONE:
						return Images.REVIEW_RESULT_NONE.createImage();
					case PASSED:
						return Images.REVIEW_RESULT_PASSED.createImage();
					case WARNING:
						return Images.REVIEW_RESULT_WARNING.createImage();
					}
					return super.getImage(element);
				}
			});
			ratingList.setInput(Rating.getVisibleRatings());

			if (input.getReview().getResult() != null) {
				Rating rating = input.getReview().getResult().getRating();
				ratingList.setSelection(new StructuredSelection(rating));
			}
		} else {
			String rt = "";

			if (input.getReview().getResult() != null) {
				rt = input.getReview().getResult().getRating().getName();
			}
			Label rat = toolkit.createLabel(reviewComposite, rt);
			rat.setFont(new Font(null, "sans-serif", 10, SWT.BOLD));
			toolkit.createLabel(reviewComposite, "");
		}
		// Review Comment
		toolkit.createLabel(reviewComposite, Messages.ReviewEditor_Comment);
		final Text commentText = toolkit.createText(reviewComposite, "", //$NON-NLS-1$
				SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		GridData gd = getLayoutData(true);// new GridData(SWT.DEFAULT, 60);
		gd.heightHint = 60;
		gd.verticalSpan = 2;
		commentText.setLayoutData(gd);
		commentText.setEnabled(ReviewsUtil.hasPrivileges(ReviewsUtil
				.getTaskOwner(getEditorInput().getModel().getTask()), getEditorInput().getModel().getTaskRepository()));
		if (input.getReview().getResult() != null) {
			commentText.setText(input.getReview().getResult().getText());
		}

		toolkit.createLabel(reviewComposite, ""); //$NON-NLS-1$
		toolkit.createLabel(reviewComposite, ""); //$NON-NLS-1$
		toolkit.createLabel(reviewComposite, ""); //$NON-NLS-1$

		// Submit button
		Button submitButton = toolkit.createButton(reviewComposite, Messages.ReviewEditor_Submit, SWT.PUSH);
		submitButton.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				try {
					Rating rating = (Rating) ((IStructuredSelection) ratingList.getSelection()).getFirstElement();

					ReviewResult reviewResult = ReviewFactory.eINSTANCE.createReviewResult();
					reviewResult.setText(commentText.getText());
					reviewResult.setRating(rating);
					getEditorInput().getReview().setResult(reviewResult);

					handler.doSubmit(getEditorInput());

				} catch (Exception ex) {

					ex.printStackTrace();

				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		submitButton.setImage(TasksUiImages.REPOSITORY_SUBMIT.createImage());
		submitButton.setEnabled(ReviewsUtil.hasPrivileges(ReviewsUtil.getTaskOwner(getEditorInput().getModel()
				.getTask()), getEditorInput().getModel().getTaskRepository()));

		reviewSection.setClient(reviewComposite);
	}

	private void createContentList(Composite body, FormToolkit toolkit) {
		contentSection = toolkit.createSection(container, ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE
				| ExpandableComposite.EXPANDED);
		contentSection.setLayout(new GridLayout(1, true));
		contentSection.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		contentSection.setText(Messages.ReviewEditor_Content);

		fileList = new TableViewer(contentSection, SWT.FULL_SELECTION);
		fileList.getTable().setHeaderVisible(true);
		fileList.setContentProvider(new IStructuredContentProvider() {

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}

			public void dispose() {
			}

			public Object[] getElements(Object inputElement) {

				TaskMapper mapper = new TaskMapper(repository);
				org.eclipse.mylyn.reviews.core.scm.serialization.model.Changeset chs = mapper.getChangesetForRevision(
						new NullProgressMonitor(), selectedChangeset.getRevision(), selectedChangeset.getProjecturl());

				return chs.getFiles().toArray(new File[0]);
			}
		});

		fileList.addDoubleClickListener(new IDoubleClickListener() {

			public void doubleClick(DoubleClickEvent event) {
				ISelection selection = event.getSelection();
				if (selection instanceof IStructuredSelection) {
					IStructuredSelection sel = (IStructuredSelection) selection;
					if (sel.getFirstElement() instanceof File) {
						final File f = (File) sel.getFirstElement();
						// Fill the compare editor
						final TaskMapper mapper = new TaskMapper(repository);
						final IChangeset chs = mapper.getChangesetForRevision(new NullProgressMonitor(),
								selectedChangeset.getRevision(), selectedChangeset.getProjecturl());
						String filename = f.getFilename();
						final String ext = filename.substring(f.getFilename().lastIndexOf(".") + 1);

						switch (f.getMod()) {
						case A:

							mapper.openEditor(new NullProgressMonitor(), selectedChangeset.getProjecturl(), chs
									.getRevision(), f);

							break;
						case M:

							try {
								viewModifiedFile(ViewMode.COMPAREEDITOR, f, chs, ext, mapper);
							} catch (InvocationTargetException e1) {
								e1.printStackTrace();
							} catch (InterruptedException e1) {
								e1.printStackTrace();
							}

							break;
						case D:

							mapper.openEditor(new NullProgressMonitor(), selectedChangeset.getProjecturl(), Long
									.parseLong(chs.getRevision())
									- 1 + "", f);

							break;
						}
					}
				}
			}
		});

		UiUtil.createColumn(fileList, Messages.ReviewTaskEditorPart_Header_Operation, OPERATION_WIDTH);
		UiUtil.createColumn(fileList, Messages.ReviewTaskEditorPart_Header_File, FILENAME_WIDTH);

		fileList.setLabelProvider(new TableLabelProvider() {
			final int COLUMN_OPERATION = 0;
			final int COLUMN_FILENAME = 1;

			public String getColumnText(Object element, int columnIndex) {
				File file = (File) element;
				switch (columnIndex) {
				case COLUMN_OPERATION:
					return file.getMod().toString();
				case COLUMN_FILENAME:
					return file.getFilename();
				default:
					return null;
				}
			}

			public Image getColumnImage(Object element, int columnIndex) {
				return null;
			}
		});
		fileList.getControl().setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true));
		fileList.setInput(selectedChangeset);
		contentSection.setClient(fileList.getControl());
	}

	private void createChangesetSection(Composite body, FormToolkit toolkit) {

		// Review Section
		Section changesetSection = toolkit.createSection(body, ExpandableComposite.TITLE_BAR
				| ExpandableComposite.TWISTIE | ExpandableComposite.EXPANDED);

		changesetSection.setLayout(new GridLayout(1, true));
		changesetSection.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		changesetSection.setText(Messages.ReviewEditor_Changesets);

		TableViewer reviewItemTable = new TableViewer(changesetSection, SWT.FULL_SELECTION | SWT.FILL_EVEN_ODD);
		reviewItemTable.getTable().setHeaderVisible(true);
		reviewItemTable.setContentProvider(new IStructuredContentProvider() {

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}

			public void dispose() {
			}

			public Object[] getElements(Object inputElement) {
				Review review = (Review) inputElement;
				List<Changeset> changesets = new ArrayList<Changeset>();

				for (int i = 0; i < review.getScope().size(); i++)
					changesets.add((Changeset) review.getScope().get(i));

				return changesets.toArray(new Changeset[1]);
			}
		});

		reviewItemTable.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				if (selection.getFirstElement() instanceof Changeset) {
					Object[] oSelectedChs = selection.toArray();
					selectedChangeset = null;
					for (int i = 0; i < oSelectedChs.length; i++)
						selectedChangeset = (Changeset) oSelectedChs[i];

					fileList.setInput(selectedChangeset);
					contentSection.setExpanded(false);
					contentSection.setExpanded(true);
				}
			}
		});

		UiUtil.createColumn(reviewItemTable, Messages.CreateReviewTaskEditorPart_Header_Revision, REVISION_WIDTH);
		UiUtil.createColumn(reviewItemTable, Messages.CreateReviewTaskEditorPart_Header_Description, DESCRIPTION_WIDTH);
		UiUtil.createColumn(reviewItemTable, Messages.CreateReviewTaskEditorPart_Header_Author, AUTOR_WIDTH);
		reviewItemTable.setLabelProvider(new TableLabelProvider() {
			final int COLUMN_REVISION = 0;
			final int COLUMN_DESCRIPTION = 1;
			final int COLUMN_AUTHOR = 2;

			public String getColumnText(Object element, int columnIndex) {
				Changeset changeset = (Changeset) element;
				switch (columnIndex) {
				case COLUMN_REVISION:
					return changeset.getRevision();
				case COLUMN_DESCRIPTION:
					return changeset.getComment();
				case COLUMN_AUTHOR:
					return changeset.getAuthor();
				default:
					return null;
				}
			}

			public Image getColumnImage(Object element, int columnIndex) {
				return null;
			}
		});
		reviewItemTable.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		reviewItemTable.setInput(getEditorInput().getReview());
		changesetSection.setClient(reviewItemTable.getControl());

	}

	private ReviewTaskEditorInput getEditorInput() {
		return input;
	}

	private GridData getLayoutData(boolean growHorizontal) {
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		return gd;
	}

	private void viewModifiedFile(ViewMode mode, final File f, final IChangeset chs, final String ext,
			final TaskMapper mapper) throws InvocationTargetException, InterruptedException {
		ChangesetCompareItem itemOriginal = new ChangesetCompareItem(mapper.getFileInRevision(
				new NullProgressMonitor(), selectedChangeset.getRevision(), f.getFilename(), selectedChangeset
						.getProjecturl()), chs.getDate().getTime(), ext, selectedChangeset.getRevision());

		ChangesetCompareItem itemPrevious;

		if (Integer.parseInt(selectedChangeset.getRevision()) - 1 <= 0) {
			itemPrevious = new ChangesetCompareItem(null, new Date().getTime(), "", "");
		} else {

			Long prev = (Long.parseLong(chs.getRevision()) - 1);
			if (prev <= 0)
				prev = 1l;

			itemPrevious = new ChangesetCompareItem(mapper.getFileInRevision(new NullProgressMonitor(), Long
					.toString(Long.parseLong(chs.getRevision()) - 1), f.getFilename(), selectedChangeset
					.getProjecturl()), chs.getDate().getTime(), ext, Long
					.toString(Long.parseLong(chs.getRevision()) - 1));
		}

		if (mode == ViewMode.INEDITOR) {
			RevisionCompareInput inputMod = new RevisionCompareInput(itemPrevious, itemOriginal);
			viewer.setInput(inputMod);
		} else {
			RevisionCompareEditorInput inputEditor = new RevisionCompareEditorInput(itemPrevious, itemOriginal);
			CompareUI.openCompareEditor(inputEditor);
		}
	}
}
