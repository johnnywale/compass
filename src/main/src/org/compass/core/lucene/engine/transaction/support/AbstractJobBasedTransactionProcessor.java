/*
 * Copyright 2004-2008 the original author or authors.
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

package org.compass.core.lucene.engine.transaction.support;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.lucene.store.Lock;
import org.compass.core.Resource;
import org.compass.core.engine.SearchEngineException;
import org.compass.core.lucene.engine.LuceneSearchEngine;
import org.compass.core.lucene.engine.LuceneSearchEngineHits;
import org.compass.core.lucene.engine.LuceneSearchEngineInternalSearch;
import org.compass.core.lucene.engine.LuceneSearchEngineQuery;
import org.compass.core.spi.InternalResource;
import org.compass.core.spi.ResourceKey;

/**
 * Base class for jobs based ({@link org.compass.core.lucene.engine.transaction.support.TransactionJobs})
 * transaction processor. Mainly used for simple accumelating of {@link org.compass.core.lucene.engine.transaction.support.TransactionJob}s
 * and then processing them during commit/rollback time.
 *
 * <p>Allows for optionl ordering guarantees of transaction operations across several concurrent transactions from the
 * same or from different JVMs. The setting name to control it is <code>maintainOrder</code> and the constructor allows
 * to control its default value.
 *
 * <p>Note, if only search based operations were performed, then prepare/commit/rollback will not be called.
 *
 * @author kimchy
 */
public abstract class AbstractJobBasedTransactionProcessor extends AbstractSearchTransactionProcessor {

    private TransactionJobs jobs;

    private final boolean maintainOrder;

    private final Map<String, Lock> orderLocks;

    /**
     * Constructs a new job based transction processor. The <code>defaultMaintainOrderLock</code> will control if, by
     * default when no setting is specified (setting name is <code>maintainOrder</code>), what will be the default value
     * of it.
     */
    public AbstractJobBasedTransactionProcessor(Log logger, LuceneSearchEngine searchEngine, boolean defaultMaintainOrderLock) {
        super(logger, searchEngine);
        maintainOrder = searchEngine.getSettings().getSettingAsBoolean(getSettingName("maintainOrder"), defaultMaintainOrderLock);
        if (maintainOrder) {
            orderLocks = new HashMap<String, Lock>();
        } else {
            orderLocks = null;
        }
    }

    public void begin() throws SearchEngineException {
        // nothing to do here
    }

    public void prepare() throws SearchEngineException {
        if (jobs == null) {
            return;
        }
        doPrepare(jobs);
    }

    abstract protected void doPrepare(TransactionJobs jobs) throws SearchEngineException;

    public void commit(boolean onePhase) throws SearchEngineException {
        if (jobs == null) {
            return;
        }
        try {
            doCommit(onePhase, jobs);
        } finally {
            clearLocksIfNeeded();
        }
    }

    abstract protected void doCommit(boolean onePhase, TransactionJobs jobs) throws SearchEngineException;

    public void rollback() throws SearchEngineException {
        if (jobs == null) {
            return;
        }
        try {
            doRollback(jobs);
        } finally {
            clearLocksIfNeeded();
        }
    }

    abstract protected void doRollback(TransactionJobs jobs) throws SearchEngineException;

    public void flush() throws SearchEngineException {
    }

    public void create(InternalResource resource) throws SearchEngineException {
        obtainOrderLockIfNeeded(resource.getSubIndex());
        getTransactionJobs().add(new TransactionJob(TransactionJob.Type.CREATE, resource));
    }

    public void update(InternalResource resource) throws SearchEngineException {
        obtainOrderLockIfNeeded(resource.getSubIndex());
        getTransactionJobs().add(new TransactionJob(TransactionJob.Type.UPDATE, resource));
    }

    public void delete(ResourceKey resourceKey) throws SearchEngineException {
        obtainOrderLockIfNeeded(resourceKey.getSubIndex());
        getTransactionJobs().add(new TransactionJob(TransactionJob.Type.DELETE, resourceKey));
    }

    public void delete(LuceneSearchEngineQuery query) throws SearchEngineException {
        flush();
        String[] calcSubIndexes = indexManager.getStore().calcSubIndexes(query.getSubIndexes(), query.getAliases());
        for (String subIndex : calcSubIndexes) {
            obtainOrderLockIfNeeded(subIndex);
            getTransactionJobs().add(new TransactionJob(TransactionJob.Type.DELETE_BY_QUERY, query.getQuery(), subIndex));
        }
    }

    public LuceneSearchEngineHits find(LuceneSearchEngineQuery query) throws SearchEngineException {
        return performFind(query);
    }

    public LuceneSearchEngineInternalSearch internalSearch(String[] subIndexes, String[] aliases) throws SearchEngineException {
        return performInternalSearch(subIndexes, aliases);
    }

    public Resource[] get(ResourceKey resourceKey) throws SearchEngineException {
        return performGet(resourceKey);
    }

    private TransactionJobs getTransactionJobs() {
        if (jobs == null) {
            jobs = new TransactionJobs();
        }
        return jobs;
    }

    private void obtainOrderLockIfNeeded(String subIndex) throws SearchEngineException {
        if (!maintainOrder) {
            return;
        }
        Lock lock = orderLocks.get(subIndex);
        if (lock == null) {
            lock = searchEngine.getSearchEngineFactory().getLuceneIndexManager().getStore().openDirectory(subIndex).makeLock("order.lock");
            try {
                lock.obtain(searchEngine.getSearchEngineFactory().getLuceneSettings().getTransactionLockTimout());
            } catch (IOException e) {
                clearLocksIfNeeded();
                throw new SearchEngineException("Failed to obtain order lock on sub index [" + subIndex + "]", e);
            }
            orderLocks.put(subIndex, lock);
        }
    }

    private void clearLocksIfNeeded() {
        if (!maintainOrder) {
            return;
        }
        for (Map.Entry<String, Lock> entry : orderLocks.entrySet()) {
            try {
                entry.getValue().release();
            } catch (IOException e) {
                logger.warn("Failed to release lock for sub index [" + entry.getKey() + "]", e);
            }
        }
        orderLocks.clear();
    }

}
