/*
 * Copyright 2011 Vincent Behar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.rundeck.api.parser;

import java.io.InputStream;
import org.dom4j.Document;
import org.junit.Assert;
import org.junit.Test;
import org.rundeck.api.RundeckApiException;

/**
 * Test the {@link ParserHelper}
 * 
 * @author Vincent Behar
 */
public class ParserHelperTest {

    /**
     * XML with an explicit "error" result should throw an exception
     */
    @Test
    public void loadErrorDocument() throws Exception {
        InputStream input = getClass().getResourceAsStream("error.xml");
        try {
            ParserHelper.loadDocument(input);
            Assert.fail("should have thrown an exception !");
        } catch (RundeckApiException e) {
            Assert.assertEquals("This is the error message", e.getMessage());
        }
    }

    /**
     * XML with an explicit "success" result should NOT throw an exception
     */
    @Test
    public void loadSuccessDocument() throws Exception {
        InputStream input = getClass().getResourceAsStream("success.xml");
        Document document = ParserHelper.loadDocument(input);
        Assert.assertNotNull(document);
    }

    /**
     * XML with no result should NOT throw an exception
     */
    @Test
    public void loadEmptyDocument() throws Exception {
        InputStream input = getClass().getResourceAsStream("empty.xml");
        Document document = ParserHelper.loadDocument(input);
        Assert.assertNotNull(document);
    }

}
