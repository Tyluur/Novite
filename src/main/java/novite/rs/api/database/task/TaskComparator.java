package novite.rs.api.database.task;

import java.util.Comparator;

import novite.rs.api.database.DatabaseTaskEngine.QueryPriority;

public class TaskComparator implements Comparator<QueryTask> {

	@Override
	public int compare(QueryTask o1, QueryTask o2) {
		return o1.getPriority() == QueryPriority.IMPORTANT && o2.getPriority() != QueryPriority.IMPORTANT ? 1 : 0;
	}
}