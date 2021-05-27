/*******************************************************************************
 * Copyright (c) 2021 Red Hat, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/

package org.eclipse.m2e.jdt.tests;

import static org.junit.Assert.assertFalse;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.StringBuilder;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.internal.preferences.MavenConfigurationImpl;
import org.eclipse.m2e.tests.common.AbstractMavenProjectTestCase;


public class JavaClasspathTest extends AbstractMavenProjectTestCase {

  @Test
  public void testClasspathWithForwardSlashes() throws CoreException, IOException, InterruptedException {
    ((MavenConfigurationImpl) MavenPlugin.getMavenConfiguration()).setAutomaticallyUpdateConfiguration(true);
    setAutoBuilding(true);
    File pomFileFS = new File(FileLocator.toFileURL(getClass().getResource("/projects/classpathSlashes/pom.xml")).getFile());
    File classpathOriginalFS = new File(FileLocator.toFileURL(getClass().getResource("/projects/classpathSlashes/.classpath")).getFile());

    IProject project = importProject(pomFileFS.getAbsolutePath());
    waitForJobsToComplete();
    IJavaProject javaProject = (IJavaProject) project.getNature(JavaCore.NATURE_ID);
//    assertEquals("1.8", javaProject.getOption(JavaCore.COMPILER_SOURCE, false));


//    IFile classpathWS = project.getFile(".classpath");
//    byte[] bytes = new byte[(int) classpathOriginalFS.length()];
//    try (InputStream stream = classpathWS.getContents()) {
//      stream.read(bytes);
//    }
//    String contentsOriginal = new String(bytes);
    
    StringBuilder textOriginal = new StringBuilder();
    try (BufferedReader br = new BufferedReader(new FileReader(classpathOriginalFS))) {
	   String line;
	   while ((line = br.readLine()) != null) {
	       textOriginal.append(line).append("\n");
	   }
	}
    String contentsOriginal = textOriginal.toString();
    System.out.println("Original .classpath contents: >>>\n" + contentsOriginal + "\n<<<");
    
    
    StringBuilder text = new StringBuilder();
    try (BufferedReader br = new BufferedReader(new FileReader(project.getFile(".classpath").getLocation().toString()))) {
	   String line;
	   while ((line = br.readLine()) != null) {
	       text.append(line).append("\n");
	   }
	}
    String contents = text.toString();
    System.out.println(".classpath contents: >>>\n" + contents + "\n<<<");
    
    IClasspathEntry[] entries = javaProject.getRawClasspath();
    Set<IPath> sourcePaths = new HashSet<>();
    for (IClasspathEntry entry : entries) {
    	if (entry.getEntryKind() == IClasspathEntry.CPE_SOURCE) {
    	    System.out.println("Classpath Source entry path: " + entry.getPath().toString());
    		IPath path = new Path(entry.getPath().toString().replace('\\', '/'));
    	    assertFalse("Duplicating source entry found!", sourcePaths.contains(path));
    	    sourcePaths.add(path);
    	}
    }
    		
    
//    contents = contents.replace("1.8", "11");
//    pomFileWS.setContents(new ByteArrayInputStream(contents.getBytes()), true, false, null);
//    waitForJobsToComplete();
//    assertEquals("11", javaProject.getOption(JavaCore.COMPILER_SOURCE, false));
  }
}
