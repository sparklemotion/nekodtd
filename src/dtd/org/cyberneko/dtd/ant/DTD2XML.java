/* 
 * (C) Copyright 2003, Jeff Turner.  All rights reserved.
 *
 * This file is distributed under an Apache style license. Please
 * refer to the LICENSE file for specific details.
 */

package org.cyberneko.dtd.ant;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Vector;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.taskdefs.Java;
import xni.Writer;

/** 
 * Converts a FileSet of DTDs to their XML representation.
 *
 * @author <a href="jefft@apache.org">Jeff Turner</a>
 * @version $Revision: 1.1.1.1 $ $Date: 2003/06/27 08:45:11 $
 */

public class DTD2XML extends Java {

	private final String NL = System.getProperty("file.separator");
	private Vector filesets = new Vector();
	private File outputDir = new File(System.getProperty("java.io.tmpdir"));
	private boolean preserveDirs = false;
	private String extension =".dtdx";

	/** 
	 * Add a fileset of DTDs to convert.
	 */     
	public void addFileset(FileSet set) {
		filesets.addElement(set);
	}

	/** Set output directory for converted DTDs.  Default is /tmp (or Windows
	 * equivalent)  */
	public void setOutputDir(File out) {
		this.outputDir = out;
	}

	/** Set filename extension for converted DTD files. Default is '.dtdx' */
	public void setExtension(String ext) {
		this.extension = ext;
	}

	/** Set whether to preserve directory structure below the fileset root.
	 * Defaults to false. */
	public void setPreserveDirs(boolean preserveDirs) {
		this.preserveDirs = preserveDirs;
	}


	public void execute() {
		setClassname("xni.Writer");
        //setLogError(true);
        try {
            Class cls = getClass();
            Method method = cls.getMethod("setLogError", new Class[]{boolean.class});
            method.invoke(this, new Object[]{Boolean.TRUE});
        }
        catch (Exception e) {
            // older version of Ant, so ignore
        }
		if (filesets.size() > 0) {
			for (int j = 0; j < filesets.size(); j++) {
				FileSet fs = (FileSet) filesets.elementAt(j);
				DirectoryScanner ds = fs.getDirectoryScanner(getProject());
				File fromDir = fs.getDir(getProject());

				String[] files = ds.getIncludedFiles();
				for (int i = 0; i < files.length; ++i) {
					log("XMLifying "+files[i]+"...", Project.MSG_INFO);
					File fromFile = new File(fromDir, files[i]);
					String fromPath = new File(files[i]).getParent();
					File toFile = new File(
							this.outputDir + NL +
							(preserveDirs && fromPath!=null ? fromPath + NL : "" ) +
							newFilename(fromFile.getName(), this.extension)
							);
					convertFile(fromFile, toFile);
				}
			}
		}
	}

	/** Convert a DTD to its XML representation. */
	private void convertFile(File fromFile, File toFile) {
		toFile.getParentFile().mkdirs();

		setClassname("xni.Writer");
        //setLogError(true);
        try {
            Class cls = getClass();
            Method method = cls.getMethod("setLogError", new Class[]{boolean.class});
            method.invoke(this, new Object[]{Boolean.TRUE});
        }
        catch (Exception e) {
            // older version of Ant, so ignore
        }
		createArg().setLine("-p");
		createArg().setLine("org.cyberneko.dtd.DTDConfiguration");
		createArg().setLine(fromFile.getPath());
		setOutput(toFile);
		setFork(true); // if we don't do this, the output stream isn't reset
		super.execute();
		clearArgs();
	}

	/** Replaces the extension, or appends the extension if none currently exists. */
	private String newFilename(String oldFilename, String ext) {
		int i = oldFilename.indexOf(".");
		if (i == -1) return oldFilename;
		return oldFilename.substring(0, i)+ext;
	}

}
