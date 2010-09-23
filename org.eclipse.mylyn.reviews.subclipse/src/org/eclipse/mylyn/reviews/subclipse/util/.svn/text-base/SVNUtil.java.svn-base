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

package org.eclipse.mylyn.reviews.subclipse.util;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.reviews.core.scm.serialization.model.Changeset;
import org.eclipse.mylyn.reviews.core.scm.serialization.model.File;
import org.eclipse.mylyn.reviews.core.scm.serialization.model.ModificationOperation;
import org.tigris.subversion.subclipse.core.ISVNRemoteResource;
import org.tigris.subversion.subclipse.core.commands.GetLogsCommand;
import org.tigris.subversion.subclipse.core.history.ILogEntry;
import org.tigris.subversion.subclipse.core.history.LogEntryChangePath;
import org.tigris.subversion.svnclientadapter.SVNRevision;

public class SVNUtil {
	private static Logger logger = Logger.getLogger(SVNUtil.class.getName());

	/**
	 * This method retrieves all changesets in a given range of revisions from
	 * the scm system.
	 * 
	 * @param monitor
	 *            Progress-monitor for this operation.
	 * 
	 * @param project
	 *            Get the changesets for this project.
	 * 
	 * @param from
	 *            Get all changesets from this revision (incl. this revision).
	 *            If the value of this parameter is null, it represents the
	 *            START revision.
	 * 
	 * @param to
	 *            Get all changesets until this revision (incl. this revision).
	 *            If this value is null, all changesets until the HEAD revision
	 *            will be retrieved.
	 * 
	 * @param path
	 *            Get the revisions for this file in the repository.
	 * 
	 * @return The list of retrieved changesets.
	 */
	public static List<Changeset> getChangesets(IProgressMonitor monitor,
			String repositoryLocation, String from, String to, String path) {

		try {
			ISVNRemoteResource resource = org.tigris.subversion.subclipse.core.SVNProviderPlugin
					.getPlugin().getRepository(repositoryLocation)
					.getRootFolder();

			// the first revision
			SVNRevision startRev = SVNRevision.START;
			if (from != null)
				startRev = SVNRevision.getRevision(from);

			// the last revision
			SVNRevision endRev = SVNRevision.HEAD;
			if (to != null) {
				endRev = SVNRevision.getRevision(to);
			}

			logger.info("[SVNUtil] Fetch revisions from: " + startRev + " to: "
					+ endRev + " for resource " + resource.getName());

			GetLogsCommand logsCmd = new GetLogsCommand(resource,
					SVNRevision.HEAD, startRev, endRev, false, 0, null, false);

			logsCmd.run(monitor);

			ILogEntry[] entries = logsCmd.getLogEntries();
			logger.info("[SVNUtil] number of changesets " + entries.length);

			if (entries == null || entries.length == 0) {
				return new ArrayList<Changeset>();
			} else {
				return getChangesetsFromLogEntries(entries, repositoryLocation);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("Cannot fetch log messages: " + e.getMessage());
			return new ArrayList<Changeset>();
		}
	}

	private static List<Changeset> getChangesetsFromLogEntries(
			ILogEntry[] entries, String repositoryLocation) {
		List<Changeset> ret = new ArrayList<Changeset>();

		for (ILogEntry entry : entries) {
			Changeset change = new Changeset();
			change.setRevision(Long.toString(entry.getRevision().getNumber()));
			change.setFiles(convertChangePathsToFiles(entry
					.getLogEntryChangePaths()));
			change.setMessage(entry.getComment());
			change.setAutor(entry.getAuthor());
			change.setDate(entry.getDate());
			change.setProjectUrl(repositoryLocation);

			ret.add(change);
		}

		return ret;
	}

	public static String removeProjectName(String url) {
		try {
			logger.info("remove project name: " + url);
			return url.substring(url.indexOf("/", 2));
		} catch (Exception e) {
		}
		return url;
	}

	private static List<File> convertChangePathsToFiles(
			LogEntryChangePath[] paths) {
		ArrayList<File> files = new ArrayList<File>();

		for (LogEntryChangePath path : paths) {
			File f = new File();

			f.setFilename(path.getPath());
			ModificationOperation op = null;
			switch (path.getAction()) {
			case 'A':
				op = ModificationOperation.A;
				break;
			case 'D':
				op = ModificationOperation.D;
				break;
			case 'M':
				op = ModificationOperation.M;
				break;
			case 'R':
				op = ModificationOperation.R;
				break;
			}
			f.setMod(op);

			files.add(f);
		}

		return files;
	}

	public static Long getLastRevision(IProgressMonitor monitor,
			String repositoryLocation) {
		logger.info("GET LAST REVISION");
		try {
			ISVNRemoteResource resource = org.tigris.subversion.subclipse.core.SVNProviderPlugin
					.getPlugin().getRepository(repositoryLocation)
					.getRootFolder();

			// the first revision
			SVNRevision startRev = SVNRevision.HEAD;
			SVNRevision endRev = SVNRevision.HEAD;

			logger.info("Fetch revisions from: " + startRev + " to: " + endRev);

			GetLogsCommand logsCmd = new GetLogsCommand(resource,
					SVNRevision.HEAD, startRev, endRev, false, 0, null, false);
			logsCmd.run(monitor);

			if (logsCmd.getLogEntries().length > 0) {
				return logsCmd.getLogEntries()[logsCmd.getLogEntries().length - 1]
						.getRevision().getNumber();
			} else {
				logger.info("Cannot get last revision!");
				return 1l;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return 1l;
		}
	}

	/**
	 * Concatenates two urls.
	 * 
	 * @param url1
	 *            The first url, which may end with a slash or with the name of
	 *            a folder, e.a. 'svn://myslice.at/masterthesis/'
	 * @param url2
	 *            The second url, which may start with a slash or with the name
	 *            of a folder or file, e.a.
	 *            '/trunk/dev/projects/reviewclipse/plugin.xml'
	 * @return The new url
	 */
	public static String concatUrls(String url1, String url2) {
		if (url1.endsWith("/")) {
			if (url2.startsWith("/")) {
				// url1/ + /url2
				return url1 + url2.substring(1);
			} else {
				// url1/ + url2
				return url1 + url2;
			}
		} else {
			if (url2.startsWith("/")) {
				// url1 + /url2
				return url1 + url2;
			} else {
				// url1 + url2
				return url1 + "/" + url2;
			}
		}
	}
}
