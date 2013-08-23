package org.jenetics.gradle.task;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

public class JavadocColorizeTask extends DefaultTask {


	@TaskAction
	public void run() {
		System.out.println("Executing: " + getClass().getName());
	}

}
