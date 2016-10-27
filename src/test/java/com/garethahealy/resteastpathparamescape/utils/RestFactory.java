/*-
 * #%L
 * GarethHealy :: RESTEasy PathParam Escape
 * %%
 * Copyright (C) 2013 - 2016 Gareth Healy
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
/*
 * Copyright 2015-2016 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.garethahealy.resteastpathparamescape.utils;

import java.net.URI;
import java.net.URISyntaxException;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jboss.resteasy.client.jaxrs.ClientHttpEngine;
import org.jboss.resteasy.client.jaxrs.ProxyBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient4Engine;

public class RestFactory<T> {

    private final ClassLoader classLoader;
    private Class<T> apiClassType;

    public RestFactory(Class<T> clz) {
        classLoader = null;
        apiClassType = clz;
    }

    public RestFactory(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public T createAPI(URI uri) {
        return this.createAPI(uri, null, null);
    }

    public T createAPI(String url, String userName, String password) throws URISyntaxException {
        return createAPI(new URI(url), userName, password);
    }

    public T createAPI(URI uri, String userName, String password) {
        HttpClient httpClient = HttpClientBuilder.create().build();

        ClientHttpEngine engine = new ApacheHttpClient4Engine(httpClient, getBasicAuthContext(uri, userName, password));
        ResteasyClient client = new ResteasyClientBuilder()
            .httpEngine(engine)
            .register(JacksonJaxbJsonProvider.class)
            .register(RestRequestFilter.class)
            .build();

        ProxyBuilder<T> proxyBuilder = client
            .target(uri)
            .proxyBuilder(apiClassType);

        if (classLoader != null) {
            proxyBuilder = proxyBuilder.classloader(classLoader);
        }

        return proxyBuilder.build();
    }

    protected HttpClientContext getBasicAuthContext(URI uri, String userName, String password) {
        HttpHost targetHost = new HttpHost(uri.getHost(), uri.getPort());
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(
            new AuthScope(targetHost.getHostName(), targetHost.getPort()),
            new UsernamePasswordCredentials(userName, password));

        // Create AuthCache instance
        // Generate BASIC scheme object and add it to the local auth cache
        AuthCache authCache = new BasicAuthCache();
        authCache.put(targetHost, new BasicScheme());

        // Add AuthCache to the execution context
        HttpClientContext context = HttpClientContext.create();
        context.setCredentialsProvider(credsProvider);
        context.setAuthCache(authCache);

        return context;
    }
}
