/*
 * This file is part of the DITA Open Toolkit project hosted on
 * Sourceforge.net. See the accompanying license.txt file for
 * applicable licenses.
 */
/*
 * (c) Copyright IBM Corp. 2010 All Rights Reserved.
 */
package org.dita.dost.writer;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.custommonkey.xmlunit.XMLUnit;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import org.dita.dost.TestUtils;
import org.dita.dost.exception.DITAOTException;
import org.dita.dost.index.IndexTerm;
import org.dita.dost.module.Content;
import org.dita.dost.module.ContentImpl;


public class TestEclipseIndexWriter {

    private static File tempDir;
    private static final File resourceDir = TestUtils.getResourceDir(TestEclipseIndexWriter.class);
    private static final File expDir = new File(resourceDir, "exp");

    @BeforeClass
    public static void setUp() throws IOException {
        tempDir = TestUtils.createTempDir(TestEclipseIndexWriter.class);
        TestUtils.resetXMLUnit();
    }

    @Test
    public void testwrite() throws DITAOTException, SAXException, IOException {
        final Content content = new ContentImpl();
        final IndexTerm indexterm1 = new IndexTerm();
        indexterm1.setTermName("name1");
        indexterm1.setTermKey("indexkey1");
        final IndexTerm indexterm2 = new IndexTerm();
        indexterm2.setTermName("name2");
        indexterm2.setTermKey("indexkey2");
        indexterm1.addSubTerm(indexterm2);
        final Collection<IndexTerm> collection = new ArrayList<IndexTerm>();
        collection.add(indexterm1);
        content.setCollection(collection);

        final EclipseIndexWriter indexWriter = new EclipseIndexWriter();
        indexWriter.setContent(content);
        final File outFile = new File(tempDir, "index.xml");
        indexWriter.write(outFile.getAbsolutePath());

        XMLUnit.setIgnoreWhitespace(true);
        assertXMLEqual(new InputSource(new File(expDir, "index.xml").toURI().toString()),
                new InputSource(outFile.toURI().toString()));
    }

    @AfterClass
    public static void tearDown() throws IOException {
        TestUtils.forceDelete(tempDir);
    }

}
