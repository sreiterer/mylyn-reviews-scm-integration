package org.eclipse.mylyn.reviews.core.scm;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.reviews.core.ReviewsCorePlugin;
import org.eclipse.mylyn.reviews.core.planning.IPlanningUtils;
import org.eclipse.mylyn.reviews.core.scm.serialization.model.Changeset;
import org.eclipse.mylyn.reviews.core.scm.serialization.model.File;
import org.eclipse.mylyn.reviews.core.scm.serialization.model.Revision;
import org.eclipse.mylyn.reviews.core.scm.ws.IMappingData;
import org.eclipse.mylyn.reviews.core.scm.ws.IWSMantisConnector;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * This is a helper class for the access of data from the scm system and mantis
 * bug tracking system. It provides all necessary methods to establish
 * task-changeset mappings.
 * 
 * @author Stefan Reiterer
 * 
 */
public class TaskMapper {
	private static Logger logger = Logger.getLogger(TaskMapper.class.getName());
	private static final String svnnature = "org.tigris.subversion.subclipse.core.svnnature";
	private IWSMantisConnector client;
	@SuppressWarnings("unused")
	private TaskRepository repository;
	private IPlanningUtils planningUtils;

	/**
	 * Initializes the class with the given task repository.
	 * 
	 * @param repository
	 *            Mantis task repository.
	 */
	@SuppressWarnings("deprecation")
	public TaskMapper(TaskRepository repository) {
		this.repository = repository;
		client = ReviewsCorePlugin.getPlugin().getAdapterManager().getMantisConnector();

		init(repository.getUserName(), repository.getPassword(), repository.getRepositoryUrl());
	}

	/**
	 * Default constructor. It is not possible to use the mantis webservice.
	 */
	public TaskMapper() {
		planningUtils = ReviewsCorePlugin.getPlugin().getAdapterManager().getPlanningUtils();
	}

	/**
	 * Initializes the class with the given credentials. The credentials are
	 * required for the login into the mantis bug tracking system.
	 * 
	 * @param username
	 *            MantisBT username.
	 * @param password
	 *            MantisBT password.
	 */
	public TaskMapper(String username, String password, String repositoryUrl) {
		init(username, password, repositoryUrl);
	}

	/**
	 * Initializes the helper class.
	 * 
	 * @param username
	 *            MantisBT username
	 * @param password
	 *            MantisBT password
	 */
	public void init(String username, String password, String repositoryUrl) {
		client.init(username, password, repositoryUrl);
		planningUtils = ReviewsCorePlugin.getPlugin().getAdapterManager().getPlanningUtils();
	}

	/**
	 * This method returns all changesets which are associated with the given
	 * mantis task. The method makes only use of the mantis web service (there
	 * is no communication with the scm system).
	 * 
	 * @param monitor
	 *            Progress monitor
	 * @param taskId
	 *            Task identifier
	 * @return
	 */
	public Collection<Changeset> getChangesetsForTaskWSOnly(IProgressMonitor monitor, String taskId) {
		Collection<Changeset> chs = new ArrayList<Changeset>();

		try {
			List<IMappingData> mappings = client.getChangesets(taskId);
			for (IMappingData md : mappings) {
				logger.log(Level.FINE, "change -> " + md.getChange_id());

				Changeset changeset = new Changeset();
				changeset.setAutor(md.getAuthor());
				changeset.setRevision(md.getChange_id());
				changeset.setMessage(md.getDescription());
				changeset.setProjectUrl(md.getUrl());

				SimpleDateFormat format = new SimpleDateFormat("yyyy MM dd HH:mm:ss");
				Date date = format.parse(md.getDate());

				changeset.setDate(date);
				chs.add(changeset);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return chs;
	}

	/**
	 * This method returns all changesets that are associated with the given
	 * task. Task-changeset mappings are received from the mantis web service.
	 * Changeset information is reveiced from the underlying scm system.
	 * 
	 * @param monitor
	 *            Progress monitor
	 * @param taskId
	 *            Task identifier
	 * @return
	 */
	public Collection<Changeset> getChangesetsForTask(IProgressMonitor monitor, String taskId) {
		Collection<Changeset> chs = new ArrayList<Changeset>();

		logger.info("iterate over configured repositories!");
		ISCMAdapter adapter = ReviewsCorePlugin.getPlugin().getAdapterManager().getSCMAdapter(svnnature);

		try {
			List<IMappingData> mappings = client.getChangesets(taskId);
			for (IMappingData md : mappings) {

				Changeset changeset = (Changeset) adapter.getChangeset(monitor, new Revision(md.getChange_id()), md
						.getUrl());

				chs.add(changeset);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return chs;
	}

	/**
	 * Get the changeset that corresponds to the given revision and scm
	 * repository.
	 * 
	 * @param monitor
	 *            Progress monitor
	 * @param revision
	 *            Identifier of the changeset
	 * @param projectUrl
	 *            Url of the scm repository
	 * @return
	 */
	public Changeset getChangesetForRevision(IProgressMonitor monitor, String revision, String projectUrl) {
		logger.log(Level.FINE, "Get changeset for revision: " + revision + " _ " + projectUrl);

		IRevision rev = new Revision(revision);
		ISCMAdapter adapter = ReviewsCorePlugin.getPlugin().getAdapterManager().getSCMAdapter(svnnature);

		try {
			return (Changeset) adapter.getChangeset(monitor, rev, projectUrl);
		} catch (CoreException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * This method returns a file in the given revision.
	 * 
	 * @param monitor
	 *            Progress monitor
	 * @param revision
	 *            identifier of the file's version
	 * @param filename
	 *            Url of the file
	 * @param projectUrl
	 *            Url of the scm repository
	 * @return
	 */
	public IStorage getFileInRevision(IProgressMonitor monitor, String revision, String filename, String projectUrl) {

		IStorage ret = null;
		ISCMAdapter adapter = ReviewsCorePlugin.getPlugin().getAdapterManager().getSCMAdapter(svnnature);

		IRevision rev = new Revision(revision);
		try {
			ret = adapter.getStorageForRevision(monitor, rev, projectUrl, filename);

			return ret;
		} catch (CoreException e) {
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * This method opens an subclipse editor that contains the given file in the
	 * given revision.
	 * 
	 * @param monitor
	 *            Progress monitor
	 * @param projectUrl
	 *            Url of the scm repository
	 * @param rev
	 *            Revision of the file
	 * @param file
	 *            File in the given revision
	 */
	public void openEditor(IProgressMonitor monitor, String projectUrl, String rev, File file) {

		ISCMAdapter adapter = ReviewsCorePlugin.getPlugin().getAdapterManager().getSCMAdapter(svnnature);

		try {
			adapter.open(monitor, projectUrl, rev, file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method returns a list of all repositories that are configured in the
	 * subclipse plugin.
	 * 
	 * @return List of all configured SVN repositories.
	 */
	public List<String> getKnownRepositories() {
		ISCMAdapter adapter = ReviewsCorePlugin.getPlugin().getAdapterManager().getSCMAdapter(svnnature);

		try {
			return adapter.getKnownSVNRepositores();
		} catch (Exception e) {
			return new ArrayList<String>();
		}
	}

	/**
	 * Get all SVN users for the given repository.
	 * 
	 * @param monitor
	 * @param repositoryLocation
	 *            Location of the svn repository.
	 * @return
	 */
	public List<String> getSCMRepositoryUsers(IProgressMonitor monitor, String repositoryLocation) {
		ISCMAdapter adapter = ReviewsCorePlugin.getPlugin().getAdapterManager().getSCMAdapter(svnnature);

		return adapter.getSCMUsers(monitor, repositoryLocation);
	}

	/**
	 * Get the implementation of the extension point planningutils.
	 * 
	 * @return Review planning utilities.
	 */
	public IPlanningUtils getPlanningUtils() {
		return planningUtils;
	}
}
