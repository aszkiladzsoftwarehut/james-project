/****************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one   *
 * or more contributor license agreements.  See the NOTICE file *
 * distributed with this work for additional information        *
 * regarding copyright ownership.  The ASF licenses this file   *
 * to you under the Apache License, Version 2.0 (the            *
 * "License"); you may not use this file except in compliance   *
 * with the License.  You may obtain a copy of the License at   *
 *                                                              *
 *   http://www.apache.org/licenses/LICENSE-2.0                 *
 *                                                              *
 * Unless required by applicable law or agreed to in writing,   *
 * software distributed under the License is distributed on an  *
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY       *
 * KIND, either express or implied.  See the License for the    *
 * specific language governing permissions and limitations      *
 * under the License.                                           *
 ****************************************************************/

package org.apache.james.mailbox.store;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;

import org.apache.james.mailbox.MessageUid;
import org.apache.james.mailbox.extractor.TextExtractor;
import org.apache.james.mailbox.model.SearchQuery;
import org.apache.james.mailbox.store.mail.model.MailboxMessage;
import org.apache.james.mailbox.store.search.MessageSearches;
import org.junit.Before;
import org.junit.Test;

public class SearchUtilsRFC822Test {

    private static final String FROM_ADDRESS = "Harry <harry@example.org";

    private static final String SUBJECT_PART = "Mixed";

    private static final String CUSTARD = "CUSTARD";

    private static final String RHUBARD = "Rhubard";

    private static final String BODY = "This is a simple email\r\n "
            + "It has " + RHUBARD + ".\r\n" + "It has " + CUSTARD + ".\r\n"
            + "It needs naught else.\r\n";

    MailboxMessage row;

    Collection<MessageUid> recent;

    private MessageSearches messageSearches;

    @Before
    public void setUp() throws Exception {
        recent = new ArrayList<>();
        MessageBuilder builder = new MessageBuilder();
        builder.header("From", "Alex <alex@example.org");
        builder.header("To", FROM_ADDRESS);
        builder.header("Subject", "A " + SUBJECT_PART + " Multipart Mail");
        builder.header("Date", "Thu, 14 Feb 2008 12:00:00 +0000 (GMT)");
        builder.body = Charset.forName("us-ascii").encode(BODY).array();
        row = builder.build();
        
        Iterator<MailboxMessage> messages = null;
        SearchQuery query = null; 
        TextExtractor textExtractor = null;
        messageSearches = new MessageSearches(messages, query, textExtractor);
    }


    @Test
    public void testBodyShouldMatchPhraseInBody() throws Exception {
        assertTrue(messageSearches.isMatch(SearchQuery.bodyContains(CUSTARD), row,
                recent));
        assertFalse(messageSearches.isMatch(SearchQuery
                .bodyContains(CUSTARD + CUSTARD), row, recent));
    }

    @Test
    public void testBodyMatchShouldBeCaseInsensitive() throws Exception {
        assertTrue(messageSearches.isMatch(SearchQuery.bodyContains(RHUBARD), row,
                recent));
        assertTrue(messageSearches.isMatch(SearchQuery.bodyContains(RHUBARD
                .toLowerCase(Locale.US)), row, recent));
        assertTrue(messageSearches.isMatch(SearchQuery.bodyContains(RHUBARD
                .toLowerCase(Locale.US)), row, recent));
    }

    @Test
    public void testBodyShouldNotMatchPhraseOnlyInHeader() throws Exception {
        assertFalse(messageSearches.isMatch(SearchQuery.bodyContains(FROM_ADDRESS),
                row, recent));
        assertFalse(messageSearches.isMatch(SearchQuery.bodyContains(SUBJECT_PART),
                row, recent));
    }

    @Test
    public void testTextShouldMatchPhraseInBody() throws Exception {
        assertTrue(messageSearches.isMatch(SearchQuery.mailContains(CUSTARD), row,
                recent));
        assertFalse(messageSearches.isMatch(SearchQuery
                .mailContains(CUSTARD + CUSTARD), row, recent));
    }

    @Test
    public void testTextMatchShouldBeCaseInsensitive() throws Exception {
        assertTrue(messageSearches.isMatch(SearchQuery.mailContains(RHUBARD), row,
                recent));
        assertTrue(messageSearches.isMatch(SearchQuery.mailContains(RHUBARD
                .toLowerCase(Locale.US)), row, recent));
        assertTrue(messageSearches.isMatch(SearchQuery.mailContains(RHUBARD
                .toLowerCase(Locale.US)), row, recent));
    }

    @Test
    public void testBodyShouldMatchPhraseOnlyInHeader() throws Exception {
        assertTrue(messageSearches.isMatch(SearchQuery.mailContains(FROM_ADDRESS),
                row, recent));
        assertTrue(messageSearches.isMatch(SearchQuery.mailContains(SUBJECT_PART),
                row, recent));
    }
}
