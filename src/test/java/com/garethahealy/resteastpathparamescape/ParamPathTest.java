/*-
 * #%L
 * GarethHealy :: RESTEasy PathParam Escape
 * %%
 * Copyright (C) 2013 - 2018 Gareth Healy
 * %%
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
 * #L%
 */
package com.garethahealy.resteastpathparamescape;

import java.net.URISyntaxException;

import com.garethahealy.resteastpathparamescape.utils.PathHandler;
import com.garethahealy.resteastpathparamescape.utils.RestFactory;

import org.apache.http.NoHttpResponseException;
import org.apache.http.conn.HttpHostConnectException;
import org.junit.Assert;
import org.junit.Test;

public class ParamPathTest {

    public PathHandler createClient() throws URISyntaxException {
        RestFactory<PathHandler> factory = new RestFactory<PathHandler>(PathHandler.class);
        return (PathHandler)factory.createAPI("http://localhost:8080", "admin", "admin");
    }

    @Test
    public void test() throws URISyntaxException {
        try {
            PathHandler handler = createClient();
            handler.getEntityHash("t;unit-testing/e;mzn7vykz");
        } catch (Exception ex) {
            if (ex.getCause() instanceof NoHttpResponseException || ex.getCause() instanceof HttpHostConnectException) {
                //Worked, as attempted to call endpoint but couldn't connect/send
                Assert.assertTrue(true);
            } else {
                Assert.fail(ex.getCause().getMessage());
            }
        }
    }
}
