package org.eclipse.mylyn.reviews.core.planning;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.reviews.core.scm.IChangeset;

public interface IPlanningUtils {

	/**
	 * Select one user from a list of users (randomly).
	 * 
	 * @param users
	 *            List of users.
	 * @return One user of the given list.
	 */
	public abstract String getRandomUser(List<String> users);

	/**
	 * This method check, if there is a planning configuration available for the
	 * given repository.
	 * 
	 * @param repositoryLocation
	 *            Location of the SVN repository.
	 * @return True if there is a configuration available, otherwise false.
	 * @throws CoreException
	 */
	public abstract boolean isConfigAvailable(String repositoryLocation) throws CoreException;

	/**
	 * Get a list of repositories.
	 * 
	 * @param changesets
	 * @return
	 */
	public abstract List<String> getRepositoriesFromChangesets(List<IChangeset> changesets);

	/**
	 * This method removes duplicate entries in the given list.
	 * 
	 * @param repos
	 *            List that contains duplicate entries.
	 * @return List without duplicate entries.
	 */
	public abstract List<User> removeDuplicateUsers(List<User> repos);

	/**
	 * This method removes duplicate entries in the given list.
	 * 
	 * @param repos
	 *            List that contains duplicate entries.
	 * @return List without duplicate entries.
	 */
	public abstract List<String> removeDuplicates(List<String> repos);

	/**
	 * Get all authors from the given changesets.
	 * 
	 * @param changesets
	 *            List of changests.
	 * @return Authors of the given changests.
	 */
	public abstract List<String> getAuthors(List<IChangeset> changesets);

	public abstract GroupMapping getPlanningConfiguration(IProgressMonitor monitor, String repositoryLocation)
			throws CoreException;

	public abstract GroupMapping getTemporaryConfig(String repositoryLocation);

	public abstract boolean isConfigTemporaryAvailable(String repositoryLocation);

	public abstract String getConfigurationFileName(String repositoryLocation);

	public abstract String invalidateUrl(String Url);

	public abstract void deleteTemporaryConfiguration(String repositoryLocation);

}