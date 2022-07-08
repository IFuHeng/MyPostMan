package com.changhong.chpostman.task;

public class TaskObserver<T> implements TaskListener<T> {
    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public void onPreExecute(GenericTask task) {

    }

    @Override
    public void onPostExecute(GenericTask task, TaskResult result) {

    }

    @Override
    public void onProgressUpdate(GenericTask task, T param) {

    }

    @Override
    public void onCancelled(GenericTask task) {

    }
}
