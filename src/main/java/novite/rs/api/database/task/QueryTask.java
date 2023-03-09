package novite.rs.api.database.task;

import novite.rs.api.database.DatabaseTaskEngine.QueryPriority;

public class QueryTask {

	private QueryPriority priority = QueryPriority.NORMAL;

	public QueryTask(QueryPriority priority) {
		this.priority = priority;
	}

	public void execute() {
	}

	public QueryPriority getPriority() {
		return priority;
	}
}