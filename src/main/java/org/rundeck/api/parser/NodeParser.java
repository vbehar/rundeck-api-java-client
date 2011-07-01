package org.rundeck.api.parser;

import org.dom4j.Node;

/**
 * Interface to be implemented for parsers that handle XML {@link Node}s
 * 
 * @author Vincent Behar
 */
public interface NodeParser<T> {

    /**
     * Parse the given XML {@link Node}
     * 
     * @param node
     * @return any object holding the converted value
     */
    T parseNode(Node node);

}
