/*
 * This file is part of the DITA Open Toolkit project hosted on
 * Sourceforge.net. See the accompanying license.txt file for
 * applicable licenses.
 */

/*
 * (c) Copyright IBM Corp. 2010 All Rights Reserved.
 */
package org.dita.dost.writer;
/**
 * Interface IDitaTranstypeIndexWriter.
 * 
 */
public interface IDitaTranstypeIndexWriter {

    /**
     * Get index file name.
     * @param outputFileRoot root
     * @return index file name
     */
    public String getIndexFileName(String outputFileRoot);

}
