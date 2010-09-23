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

import org.eclipse.compare.patch.IFilePatchResult;
import org.eclipse.compare.patch.PatchConfiguration;
import org.eclipse.compare.structuremergeviewer.DiffNode;
import org.eclipse.compare.structuremergeviewer.Differencer;
import org.eclipse.compare.structuremergeviewer.ICompareInput;

/**
 * @author Kilian Matt
 */
public class ReviewDiffModel {

	// private IFilePatch2 patch;
	@SuppressWarnings("unused")
	private PatchConfiguration configuration;
	private ICompareInput compareInput;
	private IFilePatchResult compareEditorInput = null;

	public ReviewDiffModel(/* IFilePatch2 currentPatch, */
	PatchConfiguration configuration) {
		// patch = currentPatch;
		this.configuration = configuration;
	}

	public String toString() {
		return getFileName();
	}

	private String getFileName() {
		String string = ""; // patch.getTargetPath(configuration).lastSegment();
		return string;
	}

	public ICompareInput getCompareInput() {
		if (compareInput == null) {
			IFilePatchResult patchResult = getCompareEditorInput();
			ICompareInput ci = new DiffNode(Differencer.CHANGE, null, new CompareItem(patchResult,
					CompareItem.Kind.ORIGINAL, toString()), new CompareItem(patchResult, CompareItem.Kind.PATCHED,
					toString()));
			compareInput = ci;
		}
		return compareInput;

	}

	public IFilePatchResult getCompareEditorInput() {
		if (compareEditorInput == null) {
			/*
			 * IPath targetPath = patch.getTargetPath(configuration);
			 * 
			 * for (ITargetPathStrategy strategy : ReviewsUtil
			 * .getPathFindingStrategies()) {
			 * 
			 * if (strategy.matches(targetPath)) { CompareConfiguration config =
			 * new CompareConfiguration(); config.setRightEditable(false);
			 * config.setLeftEditable(false);
			 * 
			 * ReaderCreator rc = strategy.get(targetPath); NullProgressMonitor
			 * monitor = new NullProgressMonitor(); IFilePatchResult result =
			 * patch.apply(rc, configuration, monitor);
			 * 
			 * compareEditorInput = result; } }
			 */
		}
		return compareEditorInput;
	}

}
