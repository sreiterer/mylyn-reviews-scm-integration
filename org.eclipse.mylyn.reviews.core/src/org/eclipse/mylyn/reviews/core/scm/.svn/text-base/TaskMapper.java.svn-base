package org.eclipse.mylyn.reviews.core.scm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.reviews.core.ReviewsCorePlugin;
import org.eclipse.mylyn.reviews.core.scm.serialization.model.Changeset;
import org.eclipse.mylyn.reviews.core.scm.serialization.model.File;
import org.eclipse.mylyn.reviews.core.scm.serialization.model.Revision;

public class TaskMapper {
	private static Logger logger = Logger.getLogger(TaskMapper.class.getName());
	private static final String svnnature = "org.tigris.subversion.subclipse.core.svnnature";

	public TaskMapper() {
	}

	public Collection<Changeset> getChangesetsForTask(IProgressMonitor monitor,
			String taskId, List<String> knownRepositories) {
		Collection<Changeset> chs = new ArrayList<Changeset>();

		logger.info("iterate over configured repositories!");
		ISCMAdapter adapter = ReviewsCorePlugin.getPlugin().getAdapterManager()
				.getSCMAdapter(svnnature);

		for (String repositoryLocation : knownRepositories) {
			logger.info("check repository " + repositoryLocation
					+ " with adapter " + adapter);

			try {
				Collection<IChangeset> changes = (adapter.getChangesetsForTask(
						monitor, repositoryLocation, taskId, true));

				for (IChangeset c : changes) {
					if (duplicate(chs, c)) {
						chs.add((Changeset) c);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return chs;
	}

	private boolean duplicate(Collection<Changeset> chs, IChangeset c) {
		for (Changeset ch : chs) {
			if (ch.getRevision().equals(c.getRevision())) {
				return false;
			}
		}
		return true;
	}

	public Changeset getChangesetForRevision(IProgressMonitor monitor,
			String revision, String projectUrl) {

		IRevision rev = new Revision(revision);
		ISCMAdapter adapter = ReviewsCorePlugin.getPlugin().getAdapterManager()
				.getSCMAdapter(svnnature);

		try {
			return (Changeset) adapter.getChangeset(monitor, rev, projectUrl);
		} catch (CoreException e) {
			e.printStackTrace();
			return null;
		}
	}

	public IStorage getFileInRevision(IProgressMonitor monitor,
			String revision, String filename, String projectUrl) {

		IStorage ret = null;
		ISCMAdapter adapter = ReviewsCorePlugin.getPlugin().getAdapterManager()
				.getSCMAdapter(svnnature);

		IRevision rev = new Revision(revision);
		try {
			ret = adapter.getStorageForRevision(monitor, rev, projectUrl,
					filename);

			return ret;
		} catch (CoreException e) {
			e.printStackTrace();
			return null;
		}

	}

	public void openEditor(IProgressMonitor monitor, String projectUrl,
			String rev, File file) {

		ISCMAdapter adapter = ReviewsCorePlugin.getPlugin().getAdapterManager()
				.getSCMAdapter(svnnature);

		try {
			adapter.open(monitor, projectUrl, rev, file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<String> getKnownRepositories() {
		ISCMAdapter adapter = ReviewsCorePlugin.getPlugin().getAdapterManager()
				.getSCMAdapter(svnnature);

		try {
			return adapter.getKnownSVNRepositores();
		} catch (Exception e) {
			return new ArrayList<String>();
		}
	}

	public void startMetadataUpdateJob(long miliseconds, long startRevision) {
		ISCMAdapter adapter = ReviewsCorePlugin.getPlugin().getAdapterManager()
				.getSCMAdapter(svnnature);

		try {
			adapter.startParserJob(miliseconds, startRevision);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	public void changeMetadataUpdateInterval(long miliseconds) {
		ISCMAdapter adapter = ReviewsCorePlugin.getPlugin().getAdapterManager()
				.getSCMAdapter(svnnature);

		try {
			adapter.changeParserJobRescheduleTime(miliseconds);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
	
	public void setMarkedSVNRepositories(List<String> markedRepositories) {
		ISCMAdapter adapter = ReviewsCorePlugin.getPlugin().getAdapterManager()
		.getSCMAdapter(svnnature);

		adapter.setMarkedSVNRepositories(markedRepositories);
	}
	
	public void setStartRevision(long startRevision) {
		ISCMAdapter adapter = ReviewsCorePlugin.getPlugin().getAdapterManager()
			.getSCMAdapter(svnnature);

		adapter.setStartRevision(startRevision);
	}
}
