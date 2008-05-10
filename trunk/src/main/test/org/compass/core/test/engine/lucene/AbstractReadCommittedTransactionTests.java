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

package org.compass.core.test.engine.lucene;

import java.util.ArrayList;

import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermEnum;
import org.compass.core.Resource;
import org.compass.core.config.RuntimeCompassSettings;
import org.compass.core.engine.SearchEngine;
import org.compass.core.engine.SearchEngineHits;
import org.compass.core.engine.SearchEngineQuery;
import org.compass.core.lucene.engine.LuceneSearchEngineInternalSearch;
import org.compass.core.spi.InternalCompass;

/**
 * @author kimchy
 */
public abstract class AbstractReadCommittedTransactionTests extends AbstractTransactionEngineTests {

    public void testCreateResourceReadCommitted() {
        getSearchEngine().begin();
        Resource singleId = createSingleIdResource(getSearchEngine());
        getSearchEngine().create(singleId);
        Resource multiId = createMultiIdResource(getSearchEngine());
        getSearchEngine().create(multiId);

        assertSingleIdResourceExists(getSearchEngine());
        assertMulitIdResourceExists(getSearchEngine());
        getSearchEngine().rollback();
    }

    public void testDeleteResource() {
        getSearchEngine().begin();
        assertSingleIdResourceNotExists(getSearchEngine());
        assertMulitIdResourceNotExists(getSearchEngine());
        Resource singleId = createSingleIdResource(getSearchEngine());
        getSearchEngine().create(singleId);
        Resource multiId = createMultiIdResource(getSearchEngine());
        getSearchEngine().create(multiId);

        assertSingleIdResourceExists(getSearchEngine());
        assertMulitIdResourceExists(getSearchEngine());
        getSearchEngine().delete(singleId);
        assertSingleIdResourceNotExists(getSearchEngine());
        assertMulitIdResourceExists(getSearchEngine());

        getSearchEngine().delete(multiId);
        assertSingleIdResourceNotExists(getSearchEngine());
        assertMulitIdResourceNotExists(getSearchEngine());

        multiId = createMultiIdResource(getSearchEngine());
        getSearchEngine().create(multiId);
        assertMulitIdResourceExists(getSearchEngine());

        getSearchEngine().delete(multiId);
        assertSingleIdResourceNotExists(getSearchEngine());
        assertMulitIdResourceNotExists(getSearchEngine());
        getSearchEngine().rollback();
    }

    public void testMultiIdDoubleEntriesReadCommitted() throws Exception {
        getSearchEngine().begin();
        assertMulitIdResourceNotExists(getSearchEngine());
        assertMulitId2ResourceNotExists(getSearchEngine());

        Resource multiId = createMultiIdResource(getSearchEngine());
        getSearchEngine().create(multiId);
        assertMulitIdResourceExists(getSearchEngine());
        assertMulitId2ResourceNotExists(getSearchEngine());

        Resource multiId2 = createMultiIdResource2(getSearchEngine());
        getSearchEngine().create(multiId2);
        assertMulitIdResourceExists(getSearchEngine());
        assertMulitId2ResourceExists(getSearchEngine());

        getSearchEngine().delete(multiId2);
        assertMulitIdResourceExists(getSearchEngine());
        assertMulitId2ResourceNotExists(getSearchEngine());

        getSearchEngine().delete(multiId);
        assertMulitIdResourceNotExists(getSearchEngine());
        assertMulitId2ResourceNotExists(getSearchEngine());
        getSearchEngine().rollback();
    }
    
    public void testFindResourceReadCommitted() {
        getSearchEngine().begin();
        assertSingleIdResourceNotExists(getSearchEngine());
        assertMulitIdResourceNotExists(getSearchEngine());
        Resource singleId = createSingleIdResource(getSearchEngine());
        getSearchEngine().create(singleId);
        Resource multiId = createMultiIdResource(getSearchEngine());
        getSearchEngine().create(multiId);

        SearchEngineQuery query = getSearchEngine().queryBuilder().queryString(PROPERTY_VAL1 + ":" + VALUE_VAL1).toQuery();
        SearchEngineHits hits = query.hits();
        assertEquals(1, hits.getLength());
        Resource r = (Resource) hits.getResource(0);
        assertEquals(VALUE_ID1, r.getProperty(PROPERTY_ID1).getStringValue());

        query = getSearchEngine().queryBuilder().queryString(PROPERTY_VAL2 + ":" + VALUE_VAL2).toQuery();
        hits = query.hits();
        assertEquals(1, hits.getLength());
        r = (Resource) hits.getResource(0);
        assertEquals(VALUE_ID2, r.getProperty(PROPERTY_ID2).getStringValue());

        query = getSearchEngine().queryBuilder().queryString(PROPERTY_VAL4 + ":" + VALUE_VAL4).toQuery();
        hits = query.hits();
        assertEquals(2, hits.getLength());

        getSearchEngine().delete(r);
        query = getSearchEngine().queryBuilder().queryString(PROPERTY_VAL2 + ":" + VALUE_VAL2).toQuery();
        hits = query.hits();
        assertEquals(0, hits.getLength());

        query = getSearchEngine().queryBuilder().queryString(PROPERTY_VAL4 + ":" + VALUE_VAL4).toQuery();
        hits = query.hits();
        assertEquals(1, hits.getLength());
        getSearchEngine().rollback();
    }

    public void testDeleteResourceWithCommitReadCommitted() {
        getSearchEngine().begin();
        assertSingleIdResourceNotExists(getSearchEngine());
        assertMulitIdResourceNotExists(getSearchEngine());

        Resource singleId = createSingleIdResource(getSearchEngine());
        getSearchEngine().create(singleId);
        Resource multiId = createMultiIdResource(getSearchEngine());
        getSearchEngine().create(multiId);

        assertSingleIdResourceExists(getSearchEngine());
        assertMulitIdResourceExists(getSearchEngine());
        getSearchEngine().delete(singleId);
        assertSingleIdResourceNotExists(getSearchEngine());
        assertMulitIdResourceExists(getSearchEngine());

        getSearchEngine().commit(true);

        getSearchEngine().begin();
        assertSingleIdResourceNotExists(getSearchEngine());
        assertMulitIdResourceExists(getSearchEngine());
        getSearchEngine().commit(true);

        getSearchEngine().begin();
        getSearchEngine().delete(multiId);
        assertSingleIdResourceNotExists(getSearchEngine());
        assertMulitIdResourceNotExists(getSearchEngine());
        getSearchEngine().commit(true);

        getSearchEngine().begin();
        assertSingleIdResourceNotExists(getSearchEngine());
        assertMulitIdResourceNotExists(getSearchEngine());
        getSearchEngine().commit(true);
    }

    public void testDeleteResource2WithCommitReadCommitted() {
        getSearchEngine().begin();
        assertSingleIdResourceNotExists(getSearchEngine());
        assertMulitIdResourceNotExists(getSearchEngine());
        assertSingleId2ResourceNotExists(getSearchEngine());
        assertMulitId2ResourceNotExists(getSearchEngine());

        Resource singleId = createSingleIdResource(getSearchEngine());
        getSearchEngine().create(singleId);
        Resource multiId = createMultiIdResource(getSearchEngine());
        getSearchEngine().create(multiId);

        getSearchEngine().commit(true);

        getSearchEngine().begin();
        Resource singleId2 = createSingleIdResource2(getSearchEngine());
        getSearchEngine().create(singleId2);
        Resource multiId2 = createMultiIdResource2(getSearchEngine());
        getSearchEngine().create(multiId2);

        assertSingleIdResourceExists(getSearchEngine());
        assertMulitIdResourceExists(getSearchEngine());
        assertSingleId2ResourceExists(getSearchEngine());
        assertMulitId2ResourceExists(getSearchEngine());

        getSearchEngine().delete(singleId);
        assertSingleIdResourceNotExists(getSearchEngine());
        assertMulitIdResourceExists(getSearchEngine());
        assertSingleId2ResourceExists(getSearchEngine());

        getSearchEngine().delete(singleId2);
        assertSingleIdResourceNotExists(getSearchEngine());
        assertMulitIdResourceExists(getSearchEngine());
        assertSingleId2ResourceNotExists(getSearchEngine());
        getSearchEngine().commit(true);

    }

    public void testDeleteResourceWithRollbackReadCommitted() {
        getSearchEngine().begin();
        assertSingleIdResourceNotExists(getSearchEngine());
        assertMulitIdResourceNotExists(getSearchEngine());

        Resource singleId = createSingleIdResource(getSearchEngine());
        getSearchEngine().create(singleId);
        Resource multiId = createMultiIdResource(getSearchEngine());
        getSearchEngine().create(multiId);

        assertSingleIdResourceExists(getSearchEngine());
        assertMulitIdResourceExists(getSearchEngine());
        getSearchEngine().delete(singleId);
        assertSingleIdResourceNotExists(getSearchEngine());
        assertMulitIdResourceExists(getSearchEngine());

        getSearchEngine().commit(true);

        getSearchEngine().begin();
        assertSingleIdResourceNotExists(getSearchEngine());
        assertMulitIdResourceExists(getSearchEngine());
        getSearchEngine().commit(true);

        getSearchEngine().begin();
        getSearchEngine().delete(multiId);
        assertSingleIdResourceNotExists(getSearchEngine());
        assertMulitIdResourceNotExists(getSearchEngine());
        getSearchEngine().rollback();

        getSearchEngine().begin();
        assertSingleIdResourceNotExists(getSearchEngine());
        assertMulitIdResourceExists(getSearchEngine());
        getSearchEngine().commit(true);
    }

    public void testFindResourceWithCommitReadCommitted() {
        getSearchEngine().begin();
        assertSingleIdResourceNotExists(getSearchEngine());
        assertMulitIdResourceNotExists(getSearchEngine());
        Resource singleId = createSingleIdResource(getSearchEngine());
        getSearchEngine().create(singleId);
        Resource multiId = createMultiIdResource(getSearchEngine());
        getSearchEngine().create(multiId);

        SearchEngineQuery query = getSearchEngine().queryBuilder().queryString(PROPERTY_VAL1 + ":" + VALUE_VAL1).toQuery();
        SearchEngineHits hits = query.hits();
        assertEquals(1, hits.getLength());
        Resource r = hits.getResource(0);
        assertEquals(VALUE_ID1, r.getProperty(PROPERTY_ID1).getStringValue());

        getSearchEngine().commit(true);

        getSearchEngine().begin();
        query = getSearchEngine().queryBuilder().queryString(PROPERTY_VAL1 + ":" + VALUE_VAL1).toQuery();
        hits = query.hits();
        assertEquals(1, hits.getLength());
        r = hits.getResource(0);
        assertEquals(VALUE_ID1, r.getProperty(PROPERTY_ID1).getStringValue());

        query = getSearchEngine().queryBuilder().queryString(PROPERTY_VAL2 + ":" + VALUE_VAL2).toQuery();
        hits = query.hits();
        assertEquals(1, hits.getLength());
        r = hits.getResource(0);
        assertEquals(VALUE_ID2, r.getProperty(PROPERTY_ID2).getStringValue());

        getSearchEngine().delete(r);
        assertMulitIdResourceNotExists(getSearchEngine());
        query = getSearchEngine().queryBuilder().queryString(PROPERTY_VAL1 + ":" + VALUE_VAL1).toQuery();
        hits = query.hits();
        assertEquals(1, hits.getLength());
        r = hits.getResource(0);
        assertEquals(VALUE_ID1, r.getProperty(PROPERTY_ID1).getStringValue());
        query = getSearchEngine().queryBuilder().queryString(PROPERTY_VAL2 + ":" + VALUE_VAL2).toQuery();
        hits = query.hits();
        assertEquals(0, hits.getLength());
        getSearchEngine().commit(true);

        getSearchEngine().begin();
        assertMulitIdResourceNotExists(getSearchEngine());
        query = getSearchEngine().queryBuilder().queryString(PROPERTY_VAL1 + ":" + VALUE_VAL1).toQuery();
        hits = query.hits();
        assertEquals(1, hits.getLength());
        r = (Resource) hits.getResource(0);
        assertEquals(VALUE_ID1, r.getProperty(PROPERTY_ID1).getStringValue());
        query = getSearchEngine().queryBuilder().queryString(PROPERTY_VAL2 + ":" + VALUE_VAL2).toQuery();
        hits = query.hits();
        assertEquals(0, hits.getLength());
        query = getSearchEngine().queryBuilder().queryString(PROPERTY_VAL4 + ":" + VALUE_VAL4).toQuery();
        hits = query.hits();
        assertEquals(1, hits.getLength());
        getSearchEngine().commit(true);
    }
    
    public void testTermDocs() throws Exception {
        getSearchEngine().begin();
        assertSingleIdResourceNotExists(getSearchEngine());
        assertMulitIdResourceNotExists(getSearchEngine());
        // create an index with data and commit it
        Resource singleId = createSingleIdResource(getSearchEngine());
        getSearchEngine().create(singleId);
        Resource multiId = createMultiIdResource(getSearchEngine());
        getSearchEngine().create(multiId);

        assertSingleIdResourceExists(getSearchEngine());
        assertMulitIdResourceExists(getSearchEngine());
        getSearchEngine().commit(true);

        // start one index engine again, and perform reads
        getSearchEngine().begin();
        assertSingleIdResourceExists(getSearchEngine());
        assertMulitIdResourceExists(getSearchEngine());

        LuceneSearchEngineInternalSearch internalSearch = (LuceneSearchEngineInternalSearch) getSearchEngine().internalSearch(null, null);
        TermEnum termEnum = internalSearch.getReader().terms(new Term(PROPERTY_VAL1, ""));
        try {
            ArrayList tempList = new ArrayList();
            while (PROPERTY_VAL1.equals(termEnum.term().field())) {
                tempList.add(termEnum.term().text());

                if (!termEnum.next()) {
                    break;
                }
            }
            assertEquals(1, tempList.size());
            assertEquals("val1value", tempList.get(0));
        } finally {
            termEnum.close();
        }

        getSearchEngine().commit(true);
    }

    public void testConcurrentReads() throws Exception {
        getSearchEngine().begin();
        assertSingleIdResourceNotExists(getSearchEngine());
        assertMulitIdResourceNotExists(getSearchEngine());
        // create an index with data and commit it
        Resource singleId = createSingleIdResource(getSearchEngine());
        getSearchEngine().create(singleId);
        Resource multiId = createMultiIdResource(getSearchEngine());
        getSearchEngine().create(multiId);

        assertSingleIdResourceExists(getSearchEngine());
        assertMulitIdResourceExists(getSearchEngine());
        getSearchEngine().commit(true);

        // start one index engine again, and perform reads
        getSearchEngine().begin();
        assertSingleIdResourceExists(getSearchEngine());
        assertMulitIdResourceExists(getSearchEngine());

        // start another index engine, and perform reads (checks that is is not
        // locked - conccurrent)
        SearchEngine searchEngine = getSearchEngineFactory().openSearchEngine(new RuntimeCompassSettings(((InternalCompass) compass).getSettings()));
        searchEngine.begin();
        assertSingleIdResourceExists(searchEngine);
        assertMulitIdResourceExists(searchEngine);

        searchEngine.commit(true);
        getSearchEngine().rollback();
    }

    public void testNoDirtyRead() throws Exception {
        getSearchEngine().begin();
        assertSingleIdResourceNotExists(getSearchEngine());
        assertMulitIdResourceNotExists(getSearchEngine());
        // create an index with data, don't commit it yet
        Resource singleId = createSingleIdResource(getSearchEngine());
        getSearchEngine().create(singleId);
        Resource multiId = createMultiIdResource(getSearchEngine());
        getSearchEngine().create(multiId);

        assertSingleIdResourceExists(getSearchEngine());
        assertMulitIdResourceExists(getSearchEngine());

        // start another index engine and check that we can read (as well not
        // read the other transaction data)
        SearchEngine searchEngine = getSearchEngineFactory().openSearchEngine(new RuntimeCompassSettings(((InternalCompass) compass).getSettings()));
        searchEngine.begin();

        assertSingleIdResourceNotExists(searchEngine);
        assertMulitIdResourceNotExists(searchEngine);
        searchEngine.commit(true);

        getSearchEngine().commit(true);
    }

    public void testUnrepeatableReads() throws Exception {
        getSearchEngine().begin();
        assertSingleIdResourceNotExists(getSearchEngine());
        assertMulitIdResourceNotExists(getSearchEngine());

        // start an index engine, with an index that has no data
        SearchEngine searchEngine = getSearchEngineFactory().openSearchEngine(new RuntimeCompassSettings(((InternalCompass) compass).getSettings()));
        searchEngine.begin();
        assertSingleIdResourceNotExists(searchEngine);
        assertMulitIdResourceNotExists(searchEngine);

        Resource singleId = createSingleIdResource(getSearchEngine());
        getSearchEngine().create(singleId);
        Resource multiId = createMultiIdResource(getSearchEngine());
        getSearchEngine().create(multiId);

        assertSingleIdResourceExists(getSearchEngine());
        assertMulitIdResourceExists(getSearchEngine());
        getSearchEngine().commit(true);

        // check for repeatable reads
        assertSingleIdResourceExists(searchEngine);
        assertMulitIdResourceExists(searchEngine);
        searchEngine.commit(true);
    }

    public void testTwoPhaseCreatePrepareRollback() {
        // create an index with data and commit it
        getSearchEngine().begin();
        Resource singleId = createSingleIdResource(getSearchEngine());
        getSearchEngine().create(singleId);
        Resource multiId = createMultiIdResource(getSearchEngine());
        getSearchEngine().create(multiId);

        assertSingleIdResourceExists(getSearchEngine());
        assertMulitIdResourceExists(getSearchEngine());

        getSearchEngine().prepare();
        getSearchEngine().rollback();

        getSearchEngine().begin();
        assertSingleIdResourceNotExists(getSearchEngine());
        assertMulitIdResourceNotExists(getSearchEngine());
        getSearchEngine().rollback();
    }

    public void testTwoPhaseDeletePrepareRollback() {
        // create an index with data and commit it
        getSearchEngine().begin();
        Resource singleId = createSingleIdResource(getSearchEngine());
        getSearchEngine().create(singleId);
        Resource multiId = createMultiIdResource(getSearchEngine());
        getSearchEngine().create(multiId);

        assertSingleIdResourceExists(getSearchEngine());
        assertMulitIdResourceExists(getSearchEngine());

        getSearchEngine().prepare();
        getSearchEngine().commit(false);

        getSearchEngine().begin();
        assertSingleIdResourceExists(getSearchEngine());
        assertMulitIdResourceExists(getSearchEngine());
        getSearchEngine().rollback();

        
        getSearchEngine().begin();
        getSearchEngine().delete(singleId);
        assertSingleIdResourceNotExists(getSearchEngine());
        getSearchEngine().prepare();
        getSearchEngine().rollback();

        getSearchEngine().begin();
        assertSingleIdResourceExists(getSearchEngine());
        assertMulitIdResourceExists(getSearchEngine());
        getSearchEngine().rollback();
    }

    public void testTwoPhasePrepareCommit() {
        // create an index with data and commit it
        getSearchEngine().begin();
        Resource singleId = createSingleIdResource(getSearchEngine());
        getSearchEngine().create(singleId);
        Resource multiId = createMultiIdResource(getSearchEngine());
        getSearchEngine().create(multiId);

        assertSingleIdResourceExists(getSearchEngine());
        assertMulitIdResourceExists(getSearchEngine());

        getSearchEngine().prepare();
        getSearchEngine().commit(false);

        getSearchEngine().begin();
        assertSingleIdResourceExists(getSearchEngine());
        assertMulitIdResourceExists(getSearchEngine());
        getSearchEngine().rollback();
    }

    public void testCreateAndFindWithinTransaction() {
        getSearchEngine().begin();
        Resource singleId = createSingleIdResource(getSearchEngine());
        getSearchEngine().create(singleId);

        SearchEngineHits hits = getSearchEngine().queryBuilder().term(PROPERTY_VAL1, VALUE_VAL1).hits();
        assertEquals(1, hits.getLength());

        getSearchEngine().rollback();
    }
}