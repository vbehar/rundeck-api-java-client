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
