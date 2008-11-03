/*
 * Copyright 2004-2006 the original author or authors.
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
 */

package org.compass.core;

import org.compass.core.CompassTransaction.TransactionIsolation;
import org.compass.core.config.CompassSettings;

/**
 * The main interface between a Java application and Compass.
 * <p>
 * Provides the basic operations with semantic mapped objects (save, delete, and
 * load/get). The session provides operations on both the objects levels and
 * Resource levels (indexed object model). The CompassSession operations are
 * delegated to the underlying SearchEngine, so no direct access to the
 * SearchEngine is needed.
 * </p>
 *
 * <p>
 * Implementations will not be thread safe, Instead each thread/transaction
 * should obtain its own instance from a Compass.
 * </p>
 *
 * <p>
 * If the CompassSession throws an exception, the transaction must be rolled
 * back and the session discarded. The internal state of the CompassSession
 * might not be consistent with the search engine if discarded.
 * </p>
 *
 * <p>
 * Please see the CompassTemplate class for easier programmatic control using
 * the template design pattern.
 *
 * @author kimchy
 * @see org.compass.core.Resource
 * @see org.compass.core.Compass
 */

public interface CompassSession extends CompassOperations {

    /**
     * Indicates that the session will be used for read only operations. Allowing to optimize
     * search and read.
     */
    void setReadOnly();

    /**
     * Returns <code>true</code> if the session is read only.
     *
     * @see #setReadOnly() 
     */
    boolean isReadOnly();

    /**
     * Returns a resource factory allowing to create resources and properties.
     */
    ResourceFactory resourceFactory();

    /**
     * Runtimes settings that apply on the session level.
     *
     * @return Runtime settings applies on the session level
     * @see org.compass.core.config.RuntimeCompassEnvironment
     * @see org.compass.core.lucene.RuntimeLuceneEnvironment
     */
    CompassSettings getSettings();

    /**
     * Begin a unit of work and return the associated CompassTranscation object.
     * If a new underlying transaction is required, begin the transaction.
     * Otherwise continue the new work in the context of the existing underlying
     * transaction. The class of the returned CompassTransaction object is
     * determined by the property <code>compass.transaction.factory</code>.
     *
     * @return a CompassTransaction instance
     * @throws CompassException
     * @see CompassTransaction
     */
    CompassTransaction beginTransaction() throws CompassException;

    /**
     * Begin a unit of work and return the associated CompassTranscation object.
     * If a new underlying transaction is required, begin the transaction.
     * Otherwise continue the new work in the context of the existing underlying
     * transaction. The class of the returned CompassTransaction object is
     * determined by the property <code>compass.transaction.factory</code>.
     * <p>
     * Also accepts the transcation isolation of the transaction.
     *
     * @param transactionIsolation
     * @return a CompassTransaction instance
     * @throws CompassException
     * @see CompassTransaction
     */
    CompassTransaction beginTransaction(TransactionIsolation transactionIsolation) throws CompassException;

    /**
     * Begins a unit of work using a Compass local transaction. Very handy when using
     * transaction strategy other than local transaction factory but still wish to use
     * a local one for example to perform serach (which will be faster as it won't start
     * and externa transaction).
     */
    CompassTransaction beginLocalTransaction() throws CompassException;

    /**
     * Creats a new query builder, used to build queries programmatically.
     *
     * @return The query builder.
     */
    CompassQueryBuilder queryBuilder() throws CompassException;

    /**
     * Creats a new query filter builder, used to build filters of queries
     * programmatically.
     *
     * @return The query filter builder.
     */
    CompassQueryFilterBuilder queryFilterBuilder() throws CompassException;

    /**
     * Creates a new terms frequencies builder used to get terms names and
     * freqs for a list of property names.
     *
     * <p>Note, term frequencies are updated to reflect latest changes to the index
     * only after an optimization as take place (note, calling optimize might not
     * cause optimization).
     *
     * @param names The property names
     * @return A term freqs builder
     * @throws CompassException
     */
    CompassTermFreqsBuilder termFreqsBuilder(String... names) throws CompassException;

    /**
     * Returns an Analyzer helper. Can be used to help analyze given texts.
     *
     * @return the analyzer helper
     * @throws CompassException
     */
    CompassAnalyzerHelper analyzerHelper() throws CompassException;

    /**
     * Closes the CompassSession. Note, if this session is "contained" within another session,
     * it won't actually be closed, and defer closing the session to the other session.
     *
     * @throws CompassException
     * @see org.compass.core.Compass#openSession()
     */
    void close() throws CompassException;

    /**
     * Returns <code>true</code> if the session is closed. Note, if this session
     * "joined" another session, it won't actually be closed, and defer closing
     * the session to the outer session.
     */
    boolean isClosed();
}
