package org.eclipse.mylyn.reviews.core.planning;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="Group")
@XmlAccessorType(XmlAccessType.FIELD)
public class Group {

	@XmlAttribute(name="repositoryUrl", required=true)
	private String repositoryUrl;
	
	private List<User> users;

	public Group() {
		super();
		users = new ArrayList<User>();
	}

	public Group(String repositoryUrl, List<User> users) {
		super();
		this.repositoryUrl = repositoryUrl;
		this.users = users;
	}

	/* (non-Javadoc)
	 * @see com.frequentis.mylyn.reviews.planning.model.IGroup#getRepositoryUrl()
	 */
	public String getRepositoryUrl() {
		return repositoryUrl;
	}

	/* (non-Javadoc)
	 * @see com.frequentis.mylyn.reviews.planning.model.IGroup#setRepositoryUrl(java.lang.String)
	 */
	public void setRepositoryUrl(String repositoryUrl) {
		this.repositoryUrl = repositoryUrl;
	}

	/* (non-Javadoc)
	 * @see com.frequentis.mylyn.reviews.planning.model.IGroup#getUsers()
	 */
	public List<User> getUsers() {
		return users;
	}

	/* (non-Javadoc)
	 * @see com.frequentis.mylyn.reviews.planning.model.IGroup#setUsers(java.util.List)
	 */
	public void setUsers(List<User> users) {
		this.users = users;
	}

	/* (non-Javadoc)
	 * @see com.frequentis.mylyn.reviews.planning.model.IGroup#addUser(com.frequentis.mylyn.reviews.planning.model.User)
	 */
	public void addUser(User user) {
		this.users.add(user);
	}

	/* (non-Javadoc)
	 * @see com.frequentis.mylyn.reviews.planning.model.IGroup#addUsers(java.util.ArrayList)
	 */
	public void addUsers(ArrayList<User> selectedUsers) {
		this.users.addAll(selectedUsers);
	}

}
