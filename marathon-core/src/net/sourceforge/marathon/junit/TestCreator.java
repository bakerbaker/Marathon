/*******************************************************************************
 *  
 *  Copyright (C) 2010 Jalian Systems Private Ltd.
 *  Copyright (C) 2010 Contributors to Marathon OSS Project
 * 
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Library General Public
 *  License as published by the Free Software Foundation; either
 *  version 2 of the License, or (at your option) any later version.
 * 
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Library General Public License for more details.
 * 
 *  You should have received a copy of the GNU Library General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 *  Project website: http://www.marathontesting.com
 *  Help: Marathon help forum @ http://groups.google.com/group/marathon-testing
 * 
 *******************************************************************************/
package net.sourceforge.marathon.junit;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.prefs.Preferences;

import junit.framework.Test;
import junit.framework.TestSuite;
import net.sourceforge.marathon.Constants;
import net.sourceforge.marathon.api.IConsole;
import net.sourceforge.marathon.api.ScriptModelClientPart;
import net.sourceforge.marathon.util.FilePatternMatcher;

public class TestCreator {
    private static String hideFilePattern = "";
    private static FilePatternMatcher hiddenFPM;
    static {
        Preferences prefs = Preferences.userNodeForPackage(Constants.class);
        hideFilePattern = prefs.get(Constants.PREF_JUNIT_HIDEFILES, "\\..* .*\\.class \\Q__init__.py\\E \\QExploratoryTests\\E");
        hiddenFPM = new FilePatternMatcher(TestCreator.hideFilePattern);
    }
    private String sourcePath;
    private String suffix;
    private boolean acceptChecklist;
    private final IConsole console;

    public TestCreator(boolean acceptChecklist, IConsole console) throws IOException {
        this.console = console;
        sourcePath = new File(System.getProperty(Constants.PROP_TEST_DIR)).getCanonicalPath();
        suffix = ScriptModelClientPart.getModel().getSuffix();
        this.acceptChecklist = acceptChecklist;
    }

    public File getFile(String testcase) {
        String filename = convertDotFormat(testcase);
        if (filename.endsWith("AllTests")) {
            filename = filename.substring(0, filename.length() - 9);
        } else {
            filename += suffix;
        }
        return new File(filename);
    }

    private String convertDotFormat(String filename) {
        return sourcePath + File.separatorChar + filename.replace('.', File.separatorChar);
    }

    public Test getTest(List<String> testCases) {
        TestSuite suite;
        if (testCases.size() == 1) {
            Test test = getTest(testCases.get(0));
            if (test != null && test instanceof TestSuite)
                return test;
        }
        suite = new TestSuite("Marathon Test");
        for (int i = 0; i < testCases.size(); i++) {
            Test test = getTest(testCases.get(i));
            if (test != null)
                suite.addTest(test);
        }
        return suite;
    }

    public Test getTest(String name) {
        try {
            return getTest(getFile(name), name);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Test getTest(File file, String name) throws IOException {
        if (file.isFile()) {
            if (isDDT(file))
                return new MarathonDDTestSuite(file, acceptChecklist, console);
            return new MarathonTestCase(file, acceptChecklist, console);
        }
        File[] fileList = file.listFiles(new FileFilter() {
            public boolean accept(File file) {
                if (file.isDirectory() && !hiddenFPM.isMatch(file))
                    return true;
                return (file.getName().endsWith(suffix) && !hiddenFPM.isMatch(file) && ScriptModelClientPart.getModel().isTestFile(
                        file));
            }
        });
        if (fileList == null) {
            throw new Error("Could not list files for " + file);
        }
        if (fileList.length == 0)
            return null;
        Arrays.sort(fileList, new Comparator<File>() {
            public boolean equals(Object obj) {
                return false;
            }

            public int hashCode() {
                return super.hashCode();
            }

            public int compare(File f1, File f2) {
                if (f1.isDirectory() == f2.isDirectory()) {
                    return f1.getName().compareTo(f2.getName());
                }
                return f1.isDirectory() ? -1 : 1;
            }
        });
        TestSuite suite = new TestSuite(name);
        for (int i = 0; i < fileList.length; i++) {
            try {
                Test test = getTest(fileList[i], getTestName(fileList[i]));
                if (test != null)
                    suite.addTest(test);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return suite;
    }

    private boolean isDDT(File file) throws IOException {
        try {
            DDTestRunner runner = new DDTestRunner(console, file);
            return runner.isDDT();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private String getTestName(File file) throws Exception {
        return createDotFormat(file);
    }

    private String createDotFormat(File file) throws Exception {
        String filename = file.getCanonicalPath();
        if (!filename.startsWith(sourcePath))
            throw new IOException("Test file not in test directory");
        filename = filename.substring(sourcePath.length() + 1);
        filename = filename.replace(File.separatorChar, '.');
        if (file.isDirectory())
            return filename + ".AllTests";
        else {
            return filename.substring(0, filename.length() - 3);
        }
    }

    public static String getHideFilePattern() {
        return hideFilePattern;
    }

    public static void setHideFilePattern(String hideFilePattern) {
        if (hideFilePattern == null)
            TestCreator.hideFilePattern = "\\..* .*\\.class \\Q__init__.py\\E \\QExploratoryTests\\E";
        else
            TestCreator.hideFilePattern = hideFilePattern;
        hiddenFPM = new FilePatternMatcher(TestCreator.hideFilePattern);
    }

    public void setAcceptChecklist(boolean acceptChecklist) {
        this.acceptChecklist = acceptChecklist;
    }
}
