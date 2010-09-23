/*******************************************************************************
 * Copyright (c) 2010 Research Group for Industrial Software (INSO), Vienna University of Technology
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Stefan Reiterer (Research Group for Industrial Software (INSO), Vienna University of Technology) - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.reviews.core.scm;

import java.util.List;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.reviews.core.scm.serialization.model.File;

/**
 * This interface needs to be implemented by every SCM system which should be
 * connected to Mylyn Reviews. To separate the review-specific logic from
 * SCM-specific logic, an SCM adapter contains all the concerns to connect to
 * and receive data from SCM systems. An adapter needs to be implemented for
 * each team provider that should be used in combination with Mylyn Reviews.
 * 
 * @author Stefan Reiterer
 * 
 */
public interface ISCMAdapter {
	/**
	 * This method returns a file in a specified revision.
	 * 
	 * @param repositoryLocation
	 *            Location of the svn repository.
	 * @param monitor
	 *            The progress monitor which is used.
	 * @param revision
	 *            The revision in which the file will be returned.
	 * @param path
	 *            Path of the file.
	 * @return ReaderCreator to create a reader for a file in a given revision.
	 * @throws CoreException
	 */
	IStorage getStorageForRevision(IProgressMonitor monitor, IRevision revision, String repositoryLocation, String path)
			throws CoreException;

	/**
	 * This method returns the identifier of the underlying team provider. This
	 * identifier is used to load the plugin.
	 * 
	 * @return Identifier of the underlying team provider.
	 */
	String getTeamId();

	/**
	 * This method returns the changeset that is associated with the given
	 * revision.
	 * 
	 * @param monitor
	 * @param revision
	 *            Revision of the changeset.
	 * @param project
	 *            Shared project.
	 * @return Changeset for the given revision.
	 * @throws CoreException
	 */
	IChangeset getChangeset(IProgressMonitor monitor, IRevision revision, String repositoryLocation)
			throws CoreException;

	/**
	 * Opens an editor with the given file in the given revision.
	 * 
	 * @param monitor
	 * @param repositoryLocation
	 *            Url of the SCM repository
	 * @param rev
	 *            Identifier of the revision
	 * @param path
	 *            path of the file
	 * @throws CoreException
	 */
	void open(IProgressMonitor monitor, String repositoryLocation, String rev, File path) throws CoreException;

	/**
	 * Returns a list of configured (by the team provier) scm repositories.
	 * 
	 * @return List of scm repositories
	 */
	List<String> getKnownSVNRepositores();

	/**
	 * Returns a list of all known users of the given scm repository.
	 * 
	 * @param monitor
	 * @param repositoryLocation
	 *            Location of the scm repository
	 * @return
	 */
	List<String> getSCMUsers(IProgressMonitor monitor, String repositoryLocation);
}
