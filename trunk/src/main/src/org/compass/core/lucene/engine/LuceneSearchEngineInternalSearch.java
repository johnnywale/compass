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

    private Searcher searcher;

    protected IndexReader indexReader;

    private List<LuceneIndexHolder> indexHoldersToClose;

    private boolean closeReader;

    private boolean closeSearcher;

    private boolean closed;

    public LuceneSearchEngineInternalSearch() {
        // do nothing, an empty internal search one
    }

    public LuceneSearchEngineInternalSearch(MultiSearcher searcher, List<LuceneIndexHolder> indexHolders) {
        this.searcher = searcher;
        this.indexHoldersToClose = indexHolders;
        Searchable[] searchables = searcher.getSearchables();
        IndexReader[] readers = new IndexReader[searchables.length];
        for (int i = 0; i < searchables.length; i++) {
            readers[i] = ((IndexSearcher) searchables[i]).getIndexReader();
        }
        indexReader = new MultiReader(readers, false);
        this.closeReader = true;
        this.closeSearcher = true;
    }

    public LuceneSearchEngineInternalSearch(LuceneIndexHolder indexHolder, List<LuceneIndexHolder> indexHolders) {
        this.searcher = indexHolder.getIndexSearcher();
        this.indexReader = indexHolder.getIndexReader();
        this.indexHoldersToClose = indexHolders;
        this.closeReader = false;
        this.closeSearcher = false;
    }

    /**
     * Creates a new instance, with a searcher and index holders which will be used
     * to release when calling close.
     */
    public LuceneSearchEngineInternalSearch(IndexReader indexReader, Searcher searcher, List<LuceneIndexHolder> indexHolders) {
        this.indexReader = indexReader;
        this.searcher = searcher;
        this.indexHoldersToClose = indexHolders;
        this.closeReader = true;
        this.closeSearcher = true;
    }

    /**
     * Returns <code>true</code> if it represents an empty index scope.
     */
    public boolean isEmpty() {
        return searcher == null;
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
        return this.indexReader;
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

        if (searcher != null && closeSearcher) {
            try {
                searcher.close();
            } catch (IOException e) {
                // ignore
            }
        }

        if (indexReader != null && closeReader) {
            try {
                indexReader.close();
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