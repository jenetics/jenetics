/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author:
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 */
package org.jenetics.gradle.plugin

import org.gradle.api.*
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.artifacts.dsl.ArtifactHandler
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.component.SoftwareComponentContainer
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.ConfigurableFileTree
import org.gradle.api.file.CopySpec
import org.gradle.api.file.FileTree
import org.gradle.api.initialization.dsl.ScriptHandler
import org.gradle.api.invocation.Gradle
import org.gradle.api.logging.Logger
import org.gradle.api.logging.LoggingManager
import org.gradle.api.plugins.Convention
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.api.plugins.PluginContainer
import org.gradle.api.resources.ResourceHandler
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.WorkResult
import org.gradle.process.ExecResult

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.5
 * @version 1.5 &mdash; <em>$Date: 2014-02-15 $</em>
 */
class ProjectAdapter implements Project {

	private Project project

	ProjectAdapter() {
	}

	public void setProject(final Project project) {
		this.project = project
	}

	@Override
	public String absoluteProjectPath(final String path) {
		return project.absoluteProjectPath(path)
	}

	@Override
	public void afterEvaluate(final Action<? super Project> action) {
		project.afterEvaluate(action)
	}

	@Override
	public void afterEvaluate(final Closure closure) {
		project.afterEvaluate(closure)
	}

	@Override
	public void allprojects(final Action<? super Project> action) {
		project.allprojects(action)
	}

	@Override
	public void allprojects(final Closure closure) {
		project.allprojects(closure)
	}

	@Override
	public AntBuilder ant(final Closure closure) {
		return project.ant(closure)
	}

	@Override
	public void apply(final Closure closure) {
		project.apply(closure)
	}

	@Override
	public void apply(final Map<String, ?> plugin) {
		project.apply(plugin)
	}

	@Override
	public void artifacts(final Closure closure) {
		project.artifacts(closure)
	}

	@Override
	public void beforeEvaluate(final Action<? super Project> action) {
		project.beforeEvaluate(action)
	}

	@Override
	public void beforeEvaluate(final Closure closure) {
		project.beforeEvaluate(closure)
	}

	@Override
	public void buildscript(final Closure closure) {
		project.buildscript(closure)
	}

	@Override
	public Project childrenDependOnMe() {
		return project.childrenDependOnMe()
	}

	@Override
	public int compareTo(final Project other) {
		return project.compareTo(other)
	}

	@Override
	public void configurations(final Closure closure) {
		project.configurations(closure)
	}

	@Override
	public Iterable<?> configure(final Iterable<?> it, final Closure closure) {
		return project.configure(it, closure)
	}

	@Override
	public <T> Iterable<T> configure(final Iterable<T> it, final Action<? super T> action) {
		return project.configure(it, action)
	}

	@Override
	public Object configure(final Object o, final Closure closure) {
		return project.configure(o, closure)
	}

	@Override
	public <T> NamedDomainObjectContainer<T> container(
		final Class<T> type,
		final Closure closure
	) {
		return project.container(type, closure)
	}

	@Override
	public <T> NamedDomainObjectContainer<T> container(
		final Class<T> type,
		final NamedDomainObjectFactory<T> factory
	) {
		return project.container(type, factory)
	}

	@Override
	public <T> NamedDomainObjectContainer<T> container(final Class<T> type) {
		return project.container(type)
	}

	@Override
	public WorkResult copy(final Closure closure) {
		return project.copy(closure)
	}

	@Override
	public CopySpec copySpec(final Closure closure) {
		return project.copySpec(closure)
	}

	@Override
	public AntBuilder createAntBuilder() {
		return project.createAntBuilder()
	}

	@Override
	public void defaultTasks(final String... tasks) {
		project.defaultTasks(tasks)
	}

	@Override
	public boolean delete(final Object... files) {
		return project.delete(files)
	}

	@Override
	public void dependencies(final Closure closure) {
		project.dependencies(closure)
	}

	@Override
	public void dependsOn(final String dep, boolean override)
		throws UnknownProjectException
	{
		project.dependsOn(dep, override)
	}

	@Override
	public void dependsOn(final String dep) throws UnknownProjectException {
		project.dependsOn(dep)
	}

	@Override
	public Project dependsOnChildren() {
		return project.dependsOnChildren()
	}

	@Override
	public Project dependsOnChildren(boolean flag) {
		return project.dependsOnChildren(flag)
	}

	@Override
	public int depthCompare(final Project prj) {
		return project.depthCompare(prj)
	}

	@Override
	public Project evaluationDependsOn(final String dep)
		throws UnknownProjectException
	{
		return project.evaluationDependsOn(dep)
	}

	@Override
	public void evaluationDependsOnChildren() {
		project.evaluationDependsOnChildren()
	}

	@Override
	public ExecResult exec(final Closure closure) {
		return project.exec(closure)
	}

	@Override
	public File file(final Object path, final PathValidation validator)
		throws InvalidUserDataException
	{
		return project.file(path, validator)
	}

	@Override
	public File file(final Object path) {
		return project.file(path)
	}

	@Override
	public ConfigurableFileTree fileTree(final Closure closure) {
		return project.fileTree(closure)
	}

	@Override
	public ConfigurableFileTree fileTree(final Map<String, ?> tree) {
		return project.fileTree(tree)
	}

	@Override
	public ConfigurableFileTree fileTree(final Object tree, final Closure closure) {
		return project.fileTree(tree, closure)
	}

	@Override
	public ConfigurableFileTree fileTree(final Object tree) {
		return project.fileTree(tree)
	}

	@Override
	public ConfigurableFileCollection files(final Object path, final Closure closure) {
		return project.files(path, closure)
	}

	@Override
	public ConfigurableFileCollection files(final Object... paths) {
		return project.files(paths)
	}

	@Override
	public Project findProject(final String prj) {
		return project.findProject(prj)
	}

	@Override
	public Map<Project, Set<Task>> getAllTasks(boolean flag) {
		return project.getAllTasks(flag)
	}

	@Override
	public Set<Project> getAllprojects() {
		return project.getAllprojects()
	}

	@Override
	public AntBuilder getAnt() {
		return project.getAnt()
	}

	@Override
	public ArtifactHandler getArtifacts() {
		return project.getArtifacts()
	}

	@Override
	public File getBuildDir() {
		return project.getBuildDir()
	}

	@Override
	public File getBuildFile() {
		return project.getBuildFile()
	}

	@Override
	public ScriptHandler getBuildscript() {
		return project.getBuildscript()
	}

	@Override
	public Map<String, Project> getChildProjects() {
		return project.getChildProjects()
	}

	@Override
	public SoftwareComponentContainer getComponents() {
		return project.getComponents()
	}

	@Override
	public ConfigurationContainer getConfigurations() {
		return project.getConfigurations()
	}

	@Override
	public Convention getConvention() {
		return project.getConvention()
	}

	@Override
	public List<String> getDefaultTasks() {
		return project.getDefaultTasks()
	}

	@Override
	public DependencyHandler getDependencies() {
		return project.getDependencies()
	}

	@Override
	public Set<Project> getDependsOnProjects() {
		return project.getDependsOnProjects()
	}

	@Override
	public int getDepth() {
		return project.getDepth()
	}

	@Override
	public String getDescription() {
		return project.getDescription()
	}

	@Override
	public ExtensionContainer getExtensions() {
		return project.getExtensions()
	}

	@Override
	public Gradle getGradle() {
		return project.getGradle()
	}

	@Override
	public Object getGroup() {
		return project.getGroup()
	}

	@Override
	public Logger getLogger() {
		return project.getLogger()
	}

	@Override
	public LoggingManager getLogging() {
		return project.getLogging()
	}

	@Override
	public String getName() {
		return project.getName()
	}

	@Override
	public Project getParent() {
		return project.getParent()
	}

	@Override
	public String getPath() {
		return project.getPath()
	}

	@Override
	public PluginContainer getPlugins() {
		return project.getPlugins()
	}

	@Override
	public Project getProject() {
		return project.getProject()
	}

	@Override
	public File getProjectDir() {
		return project.getProjectDir()
	}

	@Override
	public Map<String, ?> getProperties() {
		return project.getProperties()
	}

	@Override
	public RepositoryHandler getRepositories() {
		return project.getRepositories()
	}

	@Override
	public ResourceHandler getResources() {
		return project.getResources()
	}

	@Override
	public File getRootDir() {
		return project.getRootDir()
	}

	@Override
	public Project getRootProject() {
		return project.getRootProject()
	}

	@Override
	public ProjectState getState() {
		return project.getState()
	}

	@Override
	public Object getStatus() {
		return project.getStatus()
	}

	@Override
	public Set<Project> getSubprojects() {
		return project.getSubprojects()
	}

	@Override
	public TaskContainer getTasks() {
		return project.getTasks()
	}

	@Override
	public Set<Task> getTasksByName(final String task, final boolean flag) {
		return project.getTasksByName(task, flag)
	}

	@Override
	public Object getVersion() {
		return project.getVersion()
	}

	@Override
	public boolean hasProperty(final String prop) {
		return project.hasProperty(prop)
	}

	@Override
	public ExecResult javaexec(final Closure exec) {
		return project.javaexec(exec)
	}

	@Override
	public File mkdir(final Object path) {
		return project.mkdir(path)
	}

	@Override
	public Project project(final String prj, final Closure closure) {
		return project.project(prj, closure)
	}

	@Override
	public Project project(final String prj) throws UnknownProjectException {
		return project.project(prj)
	}

	@Override
	public Object property(final String prop) throws MissingPropertyException {
		return project.property(prop)
	}

	@Override
	public String relativePath(final Object path) {
		return project.relativePath(path)
	}

	@Override
	public String relativeProjectPath(final String path) {
		return project.relativeProjectPath(path)
	}

	@Override
	public void repositories(final Closure closure) {
		project.repositories(closure)
	}

	@Override
	public void setBuildDir(final Object dir) {
		project.setBuildDir(dir)
	}

	@Override
	public void setDefaultTasks(final List<String> tasks) {
		project.setDefaultTasks(tasks)
	}

	@Override
	public void setDescription(final String desc) {
		project.setDescription(desc)
	}

	@Override
	public void setGroup(final Object group) {
		project.setGroup(group)
	}

	@Override
	public void setStatus(final Object status) {
		project.setStatus(status)
	}

	@Override
	public void setVersion(final Object version) {
		project.setVersion(version)
	}

	@Override
	public void subprojects(final Action<? super Project> sub) {
		project.subprojects(sub)
	}

	@Override
	public void subprojects(final Closure closure) {
		project.subprojects(closure)
	}

	@Override
	public FileTree tarTree(final Object tree) {
		return project.tarTree(tree)
	}

	@Override
	public Task task(final Map<String, ?> arg, final String name, final Closure closure) {
		return project.task(arg, name, closure)
	}

	@Override
	public Task task(final Map<String, ?> arg, final String name)
		throws InvalidUserDataException
	{
		return project.task(arg, name)
	}

	@Override
	public Task task(final String name, final Closure closure) {
		return project.task(name, closure)
	}

	@Override
	public Task task(final String name) throws InvalidUserDataException {
		return project.task(name)
	}

	@Override
	public URI uri(final Object uri) {
		return project.uri(uri)
	}

	@Override
	public FileTree zipTree(final Object tree) {
		return project.zipTree(tree)
	}


	def methodMissing(String name, args) {
		project.methodMissing(name, args)
	}

	def propertyMissing(String name, value) {
		project.propertyMissing(name, value)
	}

	def propertyMissing(String name) {
		project.propertyMissing(name)
	}


}
