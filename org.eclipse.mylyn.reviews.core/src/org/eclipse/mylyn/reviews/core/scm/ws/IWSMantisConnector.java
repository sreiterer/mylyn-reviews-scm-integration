package org.eclipse.mylyn.reviews.core.scm.ws;

import java.util.List;

public interface IWSMantisConnector {
	void init(String username, String password, String repositoryUrl);
	
	List<IMappingData> getChangesets(String taskId) throws Exception;
	
	IChangesetData getChangeset(String revision, String url) throws Exception;
	
}
