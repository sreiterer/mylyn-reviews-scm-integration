package org.eclipse.mylyn.reviews.core.planning;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="GroupMapping")
@XmlAccessorType(XmlAccessType.FIELD)
public class GroupMapping {
	
	private Group group;
	private List<User> users;
	
	public GroupMapping(Group group, List<User> users) {
		super();
		
		this.group = group;
		this.users = users;
	}

	
	public GroupMapping() {
		super();
		group = new Group();
		users = new ArrayList<User>();
	}


	/* (non-Javadoc)
	 * @see com.frequentis.mylyn.reviews.planning.model.IGroupMapping#getGroup()
	 */
	public Group getGroup() {
		return group;
	}

	/* (non-Javadoc)
	 * @see com.frequentis.mylyn.reviews.planning.model.IGroupMapping#setGroup(com.frequentis.mylyn.reviews.planning.model.Group)
	 */
	public void setGroup(Group group) {
		this.group = group;
	}

	/* (non-Javadoc)
	 * @see com.frequentis.mylyn.reviews.planning.model.IGroupMapping#getUsers()
	 */
	public List<User> getUsers() {
		return users;
	}

	/* (non-Javadoc)
	 * @see com.frequentis.mylyn.reviews.planning.model.IGroupMapping#setUsers(java.util.List)
	 */
	public void setUsers(List<User> users) {
		this.users = users;
	}


	/* (non-Javadoc)
	 * @see com.frequentis.mylyn.reviews.planning.model.IGroupMapping#addUser(com.frequentis.mylyn.reviews.planning.model.User)
	 */
	public void addUser(User user) {
		this.users.add(user);
	}


	/* (non-Javadoc)
	 * @see com.frequentis.mylyn.reviews.planning.model.IGroupMapping#addUsers(java.util.ArrayList)
	 */
	public void addUsers(ArrayList<User> selectedGroupUsers) {
		this.users.addAll(selectedGroupUsers);
	}
	
	
}
