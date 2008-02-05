package org.compass.core.lucene.engine;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MultiSearcher;
import org.apache.lucene.search.Searchable;
import org.apache.lucene.search.Searcher;
import org.compass.core.engine.SearchEngineException;
import org.compass.core.engine.SearchEngineInternalSearch;
import org.compass.core.lucene.engine.manager.LuceneIndexHolder;

/**
 * A Lucene specific search "internals", allowing for Lucene {@link IndexReader} and {@link Searcher}
 * access.
 *
 * @author kimchy
 */
public class LuceneSearchEngineInternalSearch implements SearchEngineInternalSearch, LuceneDelegatedClose {

    private MultiSearcher searcher;
    protected MultiReader reader;

    private boolean closed;
    private List<LuceneIndexHolder> indexHoldersToClose;

    /**
     * Creates a new instance, with a searcher and index holders which will be used
     * to release when calling close.
     *
     * @param searcher     The searcher, which is also used to construct the reader
     * @param indexHolders Holders to be released when calling close.
     */
    public LuceneSearchEngineInternalSearch(MultiSearcher searcher, List<LuceneIndexHolder> indexHolders) {
        this.searcher = searcher;
        this.indexHoldersToClose = indexHolders;
    }

    /**
     * Returns <code>true</code> if it represents an empty index scope.
     */
    public boolean isEmpty() {
        return searcher == null || searcher.getSearchables().length == 0;
    }

    /**
     * Returns a Lucene {@link Searcher}.
     */
    public Searcher getSearcher() {
        return this.searcher;
    }

    /**
     * Returns a Lucene {@link IndexReader}.
     */
    public IndexReader getReader() throws SearchEngineException {
        if (reader != null) {
            return this.reader;
        }

        Searchable[] searchables = searcher.getSearchables();
        IndexReader[] readers = new IndexReader[searchables.length];
        for (int i = 0; i < searchables.length; i++) {
            readers[i] = ((IndexSearcher) searchables[i]).getIndexReader();
        }
        reader = new MultiReader(readers, false);
        return this.reader;
    }

    /**
     * Closes this instance of Lucene search "internals". This is an optional operation
     * since Compass will take care of closing it when commit/rollback is called on the
     * transaction.
     */
    public void close() throws SearchEngineException {
        if (closed) {
            return;
        }
        closed = true;

        if (reader != null) {
            // close the multi reader so we dec ref its count
            try {
                reader.close();
            } catch (IOException e) {
                // ignore
            }
        }

        if (indexHoldersToClose != null) {
            for (LuceneIndexHolder indexHolder : indexHoldersToClose) {
                indexHolder.release();
            }
        }
    }

}
