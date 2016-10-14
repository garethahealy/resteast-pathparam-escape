package com.garethahealy.resteastpathparamescape;

import java.net.URISyntaxException;

import com.garethahealy.resteastpathparamescape.utils.PathHandler;
import com.garethahealy.resteastpathparamescape.utils.RestFactory;

import org.apache.http.NoHttpResponseException;
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
            if (ex.getCause() instanceof NoHttpResponseException) {
                //Worked, as attempted to call endpoint
                Assert.assertTrue(true);
            } else {
                Assert.fail(ex.getCause().getMessage());
            }
        }
    }
}
