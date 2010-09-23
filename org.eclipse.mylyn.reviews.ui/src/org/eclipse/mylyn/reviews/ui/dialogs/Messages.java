/*******************************************************************************
 * Copyright (c) 2010 Research Group for Industrial Software (INSO), Vienna University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Stefan Reiterer (Research Group for Industrial Software (INSO), Vienna University of Technology) - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.reviews.ui.dialogs;

import org.eclipse.osgi.util.NLS;

/**
 * Message bundle
 * 
 * @author Stefan Reiterer
 */
public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.reviews.ui.dialogs.messages"; //$NON-NLS-1$

	public static String CreateReviewForChangeseetsDialog_TITLE;

	public static String CreateReviewForChangeseetsDialog_TitleText;

	public static String CreateReviewForChangeseetsDialog_TitleMessage;

	public static String CreateReviewForChangesetsDialog_AssignChangesetToExistingReview;

	public static String CreateReviewForChangesetsDialog_AssignedTo;

	public static String CreateReviewForChangesetsDialog_AssignedToCOL;

	public static String CreateReviewForChangesetsDialog_Changesets_COL;

	public static String CreateReviewForChangesetsDialog_CreateNewReviewTask;

	public static String CreateReviewForChangesetsDialog_CreateReviewForSelectedChangeset;

	public static String CreateReviewForChangesetsDialog_CreatteReviewForSelectedChangesets;

	public static String CreateReviewForChangesetsDialog_ReviewCanBeCreated;

	public static String CreateReviewForChangesetsDialog_ReviewCanBeCreated2;

	public static String CreateReviewForChangesetsDialog_TaskId;

	public static String FindReviewerDialog_Progressbar_Title;

	public static String FindReviewerDialog_Reviewer;

	public static String FindReviewerDialog_Username;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
