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

/** This interface needs to be implemented by every SCM system which should be
 * connected to Mylyn Reviews. To separate the review-specific logic from 
 * SCM-specific logic, an SCM adapter contains all the concerns to connect to and
 * receive data from SCM systems.
 * An adapter needs to be implemented for each team provider that should be used in
 * combination with Mylyn Reviews.
 *  
 * @author Stefan Reiterer
 *
 */
public interface ISCMAdapter {
	
	/** This method returns a file in a specified revision.
	 * 
	 * @param repositoryLocation Location of the svn repository.
	 * @param monitor The progress monitor which is used.
	 * @param revision The revision in which the file will be returned.
	 * @param path Path of the file.
	 * @return ReaderCreator to create a reader for a file in a given revision.
	 * @throws CoreException
	 */
	IStorage getStorageForRevision(IProgressMonitor monitor,
			IRevision revision, String repositoryLocation, String path) throws CoreException;
    
    /** Determine the list of changesets, which is associated with a given task.
     * To reduce the communication-overhead in the case of centralized organized
     * SCM systems, it is necessary to temporarily store mappings from tasks to changesets
     * local. The locally stored data will frequently be refreshed. 
     * 
     * @param monitor The progress monitor which is used.
     * @param repositoryLocation Changesets are associated with this svn repository
     * @param taskId Task identifier.
     * @param repositoryUrl URL that identifies a task repository.
     * @param forceUpdate Enforce the refreshment of locally stored mappings.
     * @return Changesets for a given task.
     * @throws CoreException
     */
    List<IChangeset> getChangesetsForTask(IProgressMonitor monitor, String repositoryLocation, 
    		String taskId, boolean forceUpdate) throws CoreException;
    
    /** This method returns the identifier of the underlying team provider.
     * This identifier is used to load the plugin.
     * 
     * @return Identifier of the underlying team provider.
     */
    public String getTeamId();
    
    /** This method returns the changeset that is associated with the given revision.
     * 
     * @param monitor 
     * @param revision Revision of the changeset.
     * @param project Shared project.
     * @return Changeset for the given revision.
     * @throws CoreException
     */
    public IChangeset getChangeset (IProgressMonitor monitor, 
			IRevision revision, String repositoryLocation) throws CoreException;

	public void open(IProgressMonitor monitor, String repositoryLocation,
			String rev, File path) throws CoreException;
	
	public List<String> getKnownSVNRepositores();
	
	public void startParserJob(long miliseconds, long startRevision) throws CoreException;

	public void changeParserJobRescheduleTime(long miliseconds) throws CoreException;
	
	public void setMarkedSVNRepositories(List<String> markedRepositories);
	
	public void setStartRevision(long startRevision);
}
