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

package org.compass.needle.gigaspaces.store;

import java.io.IOException;

import com.j_spaces.core.IJSpace;
import com.j_spaces.core.client.SpaceFinder;
import junit.framework.TestCase;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.store.IndexOutput;
import org.apache.lucene.store.Lock;
import org.apache.lucene.store.LockObtainFailedException;

/**
 * @author kimchy
 */
public class GigaSpaceDirectoryTests extends TestCase {

    private IJSpace space;

    protected void setUp() throws Exception {
        space = (IJSpace) SpaceFinder.find("/./luceneDirecotry");
        space.clear(null, null);
    }

    public void test1Buffer() throws Exception {
        GigaSpaceDirectory dir = new GigaSpaceDirectory(space, "test", 1);
        insertData(dir);
        verifyData(dir);
    }

    public void test3Buffer() throws Exception {
        GigaSpaceDirectory dir = new GigaSpaceDirectory(space, "test", 3);
        insertData(dir);
        verifyData(dir);
    }

    public void test10Buffer() throws Exception {
        GigaSpaceDirectory dir = new GigaSpaceDirectory(space, "test", 10);
        insertData(dir);
        verifyData(dir);
    }

    public void test15Buffer() throws Exception {
        GigaSpaceDirectory dir = new GigaSpaceDirectory(space, "test", 15);
        insertData(dir);
        verifyData(dir);
    }

    public void test40Buffer() throws Exception {
        GigaSpaceDirectory dir = new GigaSpaceDirectory(space, "test", 40);
        insertData(dir);
        verifyData(dir);
    }

    public void testSimpeLocking() throws Exception {
        GigaSpaceDirectory dir = new GigaSpaceDirectory(space, "test", 40);
        Lock lock = dir.makeLock("testlock");
        assertFalse(lock.isLocked());
        assertTrue(lock.obtain(2000));
        assertTrue(lock.isLocked());
        try {
            assertFalse(lock.obtain(2000));
            fail();
        } catch (LockObtainFailedException e) {
            // all is well
        }
        lock.release();
        assertFalse(lock.isLocked());
    }

    private void insertData(GigaSpaceDirectory dir) throws IOException {
        byte[] test = new byte[]{1, 2, 3, 4, 5, 6, 7, 8};
        IndexOutput indexOutput = dir.createOutput("value1");
        indexOutput.writeInt(-1);
        indexOutput.writeLong(10);
        indexOutput.writeInt(0);
        indexOutput.writeInt(0);
        indexOutput.writeBytes(test, 8);
        indexOutput.writeBytes(test, 5);

        indexOutput.seek(28);
        indexOutput.writeByte((byte) 8);
        indexOutput.seek(30);
        indexOutput.writeBytes(new byte[]{1, 2}, 2);

        indexOutput.close();
    }

    private void verifyData(GigaSpaceDirectory dir) throws IOException {
        byte[] test = new byte[]{1, 2, 3, 4, 5, 6, 7, 8};
        assertTrue(dir.fileExists("value1"));
        assertEquals(33, dir.fileLength("value1"));

        IndexInput indexInput = dir.openInput("value1");
        assertEquals(-1, indexInput.readInt());
        assertEquals(10, indexInput.readLong());
        assertEquals(0, indexInput.readInt());
        assertEquals(0, indexInput.readInt());
        indexInput.readBytes(test, 0, 8);
        assertEquals((byte) 1, test[0]);
        assertEquals((byte) 8, test[7]);
        indexInput.readBytes(test, 0, 5);
        assertEquals((byte) 8, test[0]);
        assertEquals((byte) 5, test[4]);

        indexInput.seek(28);
        assertEquals((byte) 8, indexInput.readByte());
        indexInput.seek(30);
        assertEquals((byte) 1, indexInput.readByte());

        indexInput.close();
    }
}
