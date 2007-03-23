/**
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.jbi;

import org.apache.camel.Exchange;
import org.apache.servicemix.client.Destination;

import javax.jbi.messaging.MessageExchange;
import javax.jbi.messaging.MessagingException;
import javax.jbi.messaging.NormalizedMessage;
import javax.jbi.messaging.MessageExchangeFactory;
import javax.xml.transform.Source;
import java.util.Map;
import java.util.Set;

/**
 * The binding of how Camel messages get mapped to JBI and back again
 *
 * @version $Revision$
 */
public class JbiBinding {
    /**
     * Extracts the body from the given normalized message
     */
    public Object extractBodyFromJbi(JbiExchange exchange, NormalizedMessage normalizedMessage) {
        // TODO we may wish to turn this into a POJO such as a JAXB/DOM
        return normalizedMessage.getContent();
    }

    /**
     * Creates a JBI {@link MessageExchange} from the given Camel {@link Exchange}
     *
     */
    public MessageExchange makeJbiMessageExchange(Exchange camelExchange, Destination jbiDestination) throws MessagingException {
        MessageExchange jbiExchange = createJbiMessageExchange(camelExchange, jbiDestination);
        NormalizedMessage normalizedMessage = jbiExchange.getMessage("in");
        normalizedMessage.setContent(getJbiInContent(camelExchange));
        addJbiHeaders(jbiExchange, normalizedMessage, camelExchange);
        return jbiExchange;
    }

    public MessageExchange makeJbiMessageExchange(Exchange camelExchange, MessageExchangeFactory exchangeFactory) throws MessagingException {
        MessageExchange jbiExchange = createJbiMessageExchange(camelExchange, exchangeFactory);
        NormalizedMessage normalizedMessage = jbiExchange.getMessage("in");
        normalizedMessage.setContent(getJbiInContent(camelExchange));
        addJbiHeaders(jbiExchange, normalizedMessage, camelExchange);
        return jbiExchange;
    }

    protected MessageExchange createJbiMessageExchange(Exchange camelExchange, MessageExchangeFactory exchangeFactory) throws MessagingException {
        // TODO we should deal with other forms of MEM
        return exchangeFactory.createInOnlyExchange();
    }

    protected MessageExchange createJbiMessageExchange(Exchange camelExchange, Destination jbiDestination) throws MessagingException {
        // TODO we should deal with other forms of MEM
        return jbiDestination.createInOnlyExchange();
    }

    protected Source getJbiInContent(Exchange camelExchange) {
        return camelExchange.getIn().getBody(Source.class);
    }

    protected void addJbiHeaders(MessageExchange jbiExchange, NormalizedMessage normalizedMessage, Exchange camelExchange) {
        Set<Map.Entry<String, Object>> entries = camelExchange.getIn().getHeaders().entrySet();
        for (Map.Entry<String, Object> entry : entries) {
            normalizedMessage.setProperty(entry.getKey(), entry.getValue());
        }
    }
}

