package org.rest.client.task;

import java.util.ArrayList;

import org.rest.client.ClientFactory;
import org.rest.client.RestClient;
import org.rest.client.storage.StoreResultCallback;
import org.rest.client.storage.WebSqlAdapter;
import org.rest.client.task.ui.LoaderWidget;

/**
 * The first task during startup. Initialize databases and set handlers to
 * databases. It's call "initTable" method in each DB service which contains
 * create table statement.
 * 
 * @author jarrod
 * 
 */
public class InitializeDatabaseTask implements LoadTask {

	private int dbOpened = 0;
	@SuppressWarnings({ "rawtypes" })
	ArrayList<WebSqlAdapter> databases = new ArrayList<WebSqlAdapter>();

	@SuppressWarnings({ "rawtypes" })
	public InitializeDatabaseTask() {
		ClientFactory factory = RestClient.getClientFactory();
		databases.add((WebSqlAdapter) factory.getHeadersStore());
		databases.add((WebSqlAdapter) factory.getStatusesStore());
		databases.add((WebSqlAdapter) factory.getHistoryRequestStore());
		databases.add((WebSqlAdapter) factory.getUrlHistoryStore());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void run(final TasksCallback callback, final boolean lastRun) {
		final int dbCount = databases.size();
		for (WebSqlAdapter db : databases) {
			db.open(new StoreResultCallback<Boolean>() {

				@Override
				public void onSuccess(Boolean result) {
					callback.onInnerTaskFinished(1);
					dbOpened++;
					if (dbCount == dbOpened) {
						callback.onSuccess();
					}
				}

				@Override
				public void onError(Throwable e) {
					if(lastRun){
						callback.onFatalError("Unable to initialize WebSQL database :( Can't run application.");
						return;
					}
					callback.onFailure(dbOpened);
				}
			});
		}
	}

	@Override
	public int getTasksCount() {
		return databases.size();
	}

	@Override
	public void setLoader(LoaderWidget loaderWidget) {
		// TODO Auto-generated method stub
		
	}

}
