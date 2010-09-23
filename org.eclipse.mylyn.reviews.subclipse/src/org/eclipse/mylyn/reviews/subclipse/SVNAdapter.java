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

package org.eclipse.mylyn.reviews.subclipse;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.reviews.core.scm.IChangeset;
import org.eclipse.mylyn.reviews.core.scm.IRevision;
import org.eclipse.mylyn.reviews.core.scm.ISCMAdapter;
import org.eclipse.mylyn.reviews.core.scm.serialization.model.Changeset;
import org.eclipse.mylyn.reviews.core.scm.serialization.model.File;
import org.eclipse.mylyn.reviews.subclipse.util.SVNUtil;
import org.tigris.subversion.subclipse.core.ISVNRemoteFile;
import org.tigris.subversion.subclipse.core.ISVNRemoteResource;
import org.tigris.subversion.subclipse.core.ISVNRepositoryLocation;
import org.tigris.subversion.subclipse.core.SVNProviderPlugin;
import org.tigris.subversion.subclipse.core.commands.GetRemoteResourceCommand;
import org.tigris.subversion.subclipse.core.repo.SVNRepositories;
import org.tigris.subversion.subclipse.core.resources.RemoteFile;
import org.tigris.subversion.subclipse.ui.actions.OpenRemoteFileAction;
import org.tigris.subversion.svnclientadapter.SVNRevision;
import org.tigris.subversion.svnclientadapter.SVNUrl;

/**
 * Subversion adapter that connects the mylyn reviews with the svn system.
 * 
 * @author Stefan Reiterer
 * 
 */
public class SVNAdapter implements ISCMAdapter {
	private static final int USER_LOGMSG_TOLERANCE = 400;
	private static Logger logger = Logger.getLogger(SVNAdapter.class.getName());

	/**
	 * Default constructor.
	 */
	public SVNAdapter() {
	}

	/** {@link ISCMAdapter#getChangeset(IProgressMonitor, IRevision, String)} */
	public IChangeset getChangeset(IProgressMonitor monitor, IRevision revision, String repositoryLocation)
			throws CoreException {
		List<Changeset> changesets = new ArrayList<Changeset>();

		changesets = SVNUtil.getChangesets(monitor, repositoryLocation, revision.getContentIdentifier(), revision
				.getContentIdentifier(), "");

		if (changesets.size() > 0) {
			return changesets.get(0);
		} else {
			logger.info("Cannot get metadata for chageset!");
			return null;
		}
	}

	/**
	 * Get a remote resource in a given revision.
	 */
	private ISVNRemoteResource getRemoteResource(IProgressMonitor monitor, ISVNRepositoryLocation location,
			SVNRevision revision, SVNUrl url) throws CoreException {

		// get the remote resource
		GetRemoteResourceCommand command = new GetRemoteResourceCommand(location, url, revision);

		command.run(null);

		ISVNRemoteResource resource = command.getRemoteResource();
		if (resource == null) {
			Status status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Cannot get remote resource!");
			logger.info("Cannot get remote resource!");
			throw new CoreException(status);
		}

		return resource;
	}

	/**
	 * Get the plug-in id of the eclipse team provider (subclipse).
	 */
	public String getTeamId() {
		return SVNProviderPlugin.PROVIDER_ID;
	}

	/**
	 * {@link ISCMAdapter#getStorageForRevision(IProgressMonitor, IRevision, String, String)}
	 */
	public IStorage getStorageForRevision(IProgressMonitor monitor, IRevision revNo, String repositoryLocation,
			String path) throws CoreException {

		logger.info("GET FILE FOR REVISION!");
		monitor.worked(1);
		try {
			ISVNRepositoryLocation location = org.tigris.subversion.subclipse.core.SVNProviderPlugin.getPlugin()
					.getRepository(repositoryLocation);

			SVNRevision revision = null;
			if (revNo.getContentIdentifier() == null) {
				revision = SVNRevision.HEAD;
			} else {
				revision = new SVNRevision.Number(Long.parseLong(revNo.getContentIdentifier()));
			}
			monitor.worked(3);

			SVNUrl url = new SVNUrl(SVNUtil.concatUrls(repositoryLocation, path));

			logger.info("FILE URL: " + url);
			ISVNRemoteResource resource = getRemoteResource(monitor, location, revision, url);
			monitor.worked(6);

			// check if the resource is a file
			if (resource.isFolder()) {
				throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID,
						"The given path describes a folder and not a file!"));
			}

			// create remote file with the correct peg revision
			RemoteFile file = null;
			if (revision instanceof SVNRevision.Number) {
				file = new RemoteFile(null, location, url, revision, (SVNRevision.Number) revision, null, null);
			} else {
				file = new RemoteFile(location, url, revision);
			}
			monitor.worked(7);
			file.setPegRevision(revision);
			file.fetchContents(monitor);

			monitor.worked(9);

			return file.getStorage(monitor);

		} catch (Exception e) {
			logger.info("Resource does not exist in this revision!");
			return null;
		}
	}

	/**
	 * {@link ISCMAdapter#open(IProgressMonitor, String, String, File)}
	 */
	public void open(IProgressMonitor monitor, String repositoryLocation, String rev, File path) throws CoreException {

		logger.info("GET OPEN ACTION: " + rev);
		try {
			ISVNRepositoryLocation location = org.tigris.subversion.subclipse.core.SVNProviderPlugin.getPlugin()
					.getRepository(repositoryLocation);

			// revision number from log entry
			SVNRevision revision = new SVNRevision.Number(Long.parseLong(rev));

			SVNUrl url = new SVNUrl(SVNUtil.concatUrls(repositoryLocation, path.getFilename()));

			// create remote file with the correct peg revision
			final RemoteFile file = new RemoteFile(null, location, url, revision, (SVNRevision.Number) revision, null,
					null);
			// file.setPegRevision(revision);

			// check monitor
			if (monitor.isCanceled()) {
				return;
			}

			logger.info("opening file " + file.getUrl() + " for revision " //$NON-NLS-1$ //$NON-NLS-2$
					+ revision);

			OpenRemoteFileAction action = new OpenRemoteFileAction() {
				protected ISVNRemoteFile[] getSelectedRemoteFiles() {
					return new ISVNRemoteFile[] { file };
				}
			};

			// run open action, may not throw any error
			action.run(null);
		} catch (Exception e) {
			new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Cannot open editor"));
		}
	}

	/**
	 * {@link ISCMAdapter#getKnownSVNRepositores() @
	 */
	public List<String> getKnownSVNRepositores() {
		List<String> knownRepos = new ArrayList<String>();

		SVNRepositories repos = org.tigris.subversion.subclipse.core.SVNProviderPlugin.getPlugin().getRepositories();

		ISVNRepositoryLocation[] locations = repos.getKnownRepositories(new NullProgressMonitor());

		for (int j = 0; j < locations.length; j++) {
			knownRepos.add(locations[j].getRepositoryRoot().toString());

			logger.info("Known svn repository: " + locations[j].getLocation());
		}

		return knownRepos;
	}

	/**
	 * {@link ISCMAdapter#getSCMUsers(IProgressMonitor, String)}
	 */
	public List<String> getSCMUsers(IProgressMonitor monitor, String repositoryLocation) {

		List<String> users = new ArrayList<String>();
		int count = 0;

		Long fromRevision = 1l;
		Long lastRevision = SVNUtil.getLastRevision(monitor, repositoryLocation);

		if (lastRevision > USER_LOGMSG_TOLERANCE) {
			fromRevision = lastRevision - USER_LOGMSG_TOLERANCE;
		}

		monitor.setTaskName("processing commit messages to find users ... ");
		monitor.beginTask("Starting to process commit messages ... ", Integer.parseInt(Long.toString(lastRevision
				- fromRevision + 5)));

		List<Changeset> changesets = SVNUtil.getChangesets(monitor, repositoryLocation, Long.toString(fromRevision),
				null, "");

		for (Changeset c : changesets) {
			monitor.worked(count++);
			if (!users.contains(c.getAutor())) {
				users.add(c.getAutor());
			}
		}

		monitor.setCanceled(true);
		return users;
	}
}
