package com.changhong.chpostman.task;

public interface TaskListener<T> {
	String getName();
	void onPreExecute(GenericTask task);
	void onPostExecute(GenericTask task, TaskResult result);
	void onProgressUpdate(GenericTask task, T param);
	void onCancelled(GenericTask task);
}
