/*
 * Copyright 2009-2015 the CodeLibs Project and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package org.codelibs.fess.helper.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codelibs.core.util.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.FessSystemException;
import org.codelibs.fess.helper.WebManagementHelper;
import org.seasar.framework.util.InputStreamUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TomcatManagementHelperImpl implements WebManagementHelper {
    private static final Logger logger = LoggerFactory
            .getLogger(TomcatManagementHelperImpl.class);

    protected Map<String, SolrInstance> solrInstanceMap = new LinkedHashMap<String, SolrInstance>();

    public void addSolrInstance(final SolrInstance solrInstance) {
        if (!solrInstance.isValid()) {
            throw new FessTomcatManagerException("SolrInstance is invalid: "
                    + solrInstance);
        }
        solrInstanceMap.put(solrInstance.name, solrInstance);
    }

    /* (non-Javadoc)
     * @see org.codelibs.fess.helper.WebManagementHelper#hasSolrInstance()
     */
    @Override
    public boolean hasSolrInstance() {
        return !solrInstanceMap.isEmpty();
    }

    /* (non-Javadoc)
     * @see org.codelibs.fess.helper.WebManagementHelper#getSolrInstanceNameList()
     */
    @Override
    public List<String> getSolrInstanceNameList() {
        final List<String> solrInstanceNameList = new ArrayList<String>();
        solrInstanceNameList.addAll(solrInstanceMap.keySet());
        return solrInstanceNameList;
    }

    /* (non-Javadoc)
     * @see org.codelibs.fess.helper.WebManagementHelper#getStatus(java.lang.String)
     */
    @Override
    public String getStatus(final String name) {
        final SolrInstance solrInstance = solrInstanceMap.get(name);
        if (solrInstance != null) {
            try {
                return solrInstance.status();
            } catch (final Exception e) {
                logger.error("System error on a solr instance (" + name + ").",
                        e);
                return "error";
            }
        }
        return "none";
    }

    /* (non-Javadoc)
     * @see org.codelibs.fess.helper.WebManagementHelper#start(java.lang.String)
     */
    @Override
    public void start(final String name) {
        final SolrInstance solrInstance = solrInstanceMap.get(name);
        if (solrInstance != null) {
            solrInstance.start();
        } else {
            throw new FessTomcatManagerException("Solr instance (" + name
                    + ") is not found.");
        }
    }

    /* (non-Javadoc)
     * @see org.codelibs.fess.helper.WebManagementHelper#stop(java.lang.String)
     */
    @Override
    public void stop(final String name) {
        final SolrInstance solrInstance = solrInstanceMap.get(name);
        if (solrInstance != null) {
            solrInstance.stop();
        } else {
            throw new FessTomcatManagerException("Solr instance (" + name
                    + ") is not found.");
        }
    }

    /* (non-Javadoc)
     * @see org.codelibs.fess.helper.WebManagementHelper#reload(java.lang.String)
     */
    @Override
    public void reload(final String name) {
        final SolrInstance solrInstance = solrInstanceMap.get(name);
        if (solrInstance != null) {
            solrInstance.reload();
        } else {
            throw new FessTomcatManagerException("Solr instance (" + name
                    + ") is not found.");
        }
    }

    public static class SolrInstance {
        public String name;

        public String contextPath;

        public String managerUrl;

        public String schema;

        public String username;

        public String password;

        public void start() {
            final StringBuilder buf = new StringBuilder();
            buf.append(managerUrl);
            if (!managerUrl.endsWith("/")) {
                buf.append('/');
            }
            buf.append("start?path=");
            buf.append(contextPath);
            final String responseBody = getResponseBody(buf.toString());
            if (!responseBody.trim().startsWith("OK")) {
                throw new FessTomcatManagerException(
                        "Failed to start a solr instance. The reponse is \n"
                                + responseBody);
            }
        }

        public void stop() {
            final StringBuilder buf = new StringBuilder();
            buf.append(managerUrl);
            if (!managerUrl.endsWith("/")) {
                buf.append('/');
            }
            buf.append("stop?path=");
            buf.append(contextPath);
            final String responseBody = getResponseBody(buf.toString());
            if (!responseBody.trim().startsWith("OK")) {
                throw new FessTomcatManagerException(
                        "Failed to start a solr instance. The reponse is \n"
                                + responseBody);
            }
        }

        public void reload() {
            final StringBuilder buf = new StringBuilder();
            buf.append(managerUrl);
            if (!managerUrl.endsWith("/")) {
                buf.append('/');
            }
            buf.append("reload?path=");
            buf.append(contextPath);
            final String responseBody = getResponseBody(buf.toString());
            if (!responseBody.trim().startsWith("OK")) {
                throw new FessTomcatManagerException(
                        "Failed to start a solr instance. The reponse is \n"
                                + responseBody);
            }
        }

        public String status() {
            final StringBuilder buf = new StringBuilder();
            buf.append(managerUrl);
            if (!managerUrl.endsWith("/")) {
                buf.append('/');
            }
            buf.append("list");
            final String responseBody = getResponseBody(buf.toString());
            if (!responseBody.trim().startsWith("OK")) {
                throw new FessTomcatManagerException(
                        "Failed to start a solr instance. The reponse is \n"
                                + responseBody);
            }

            final String[] lines = responseBody.split("\n");
            for (final String line : lines) {
                if (line.trim().startsWith(contextPath)) {
                    final String[] data = line.split(":");
                    if (data.length > 1) {
                        return data[1];
                    }
                }
            }
            return "unknown";
        }

        protected String getResponseBody(final String url) {
            // Create an instance of HttpClient.
            final DefaultHttpClient client = new DefaultHttpClient();

            if (username != null && password != null) {
                final Credentials defaultcreds = new UsernamePasswordCredentials(
                        username, password);
                if (schema == null) {
                    schema = Constants.BASIC;
                }
                client.getCredentialsProvider().setCredentials(
                        new AuthScope(AuthScope.ANY_HOST, -1,
                                AuthScope.ANY_REALM, schema), defaultcreds);
            }

            // Create a method instance.
            final HttpGet httpGet = new HttpGet(url);

            try {
                // Execute the method.
                final HttpResponse response = client.execute(httpGet);
                final int statusCode = response.getStatusLine().getStatusCode();

                if (statusCode != HttpStatus.SC_OK) {
                    throw new FessTomcatManagerException("Could not access "
                            + url + ". HTTP Status is " + statusCode + ".");
                }

                final HttpEntity entity = response.getEntity();
                if (entity != null) {
                    // Read the response body.
                    final String value = new String(
                            InputStreamUtil.getBytes(entity.getContent()),
                            Constants.UTF_8);
                    // Release the connection.
                    entity.consumeContent();
                    return value;
                }
                throw new FessTomcatManagerException("No response from " + url);
            } catch (final IOException e) {
                throw new FessTomcatManagerException("Fatal transport error: "
                        + url, e);
            } finally {
                client.getConnectionManager().shutdown();
            }
        }

        public boolean isValid() {
            if (StringUtil.isBlank(name) || StringUtil.isBlank(managerUrl)
                    || StringUtil.isBlank(contextPath)) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            final StringBuilder buf = new StringBuilder();
            buf.append("name:").append(name).append(", ");
            buf.append("managerUrl:").append(managerUrl).append(", ");
            buf.append("contextPath:").append(contextPath).append(", ");
            buf.append("schema:").append(schema).append(", ");
            buf.append("username:").append(username).append(", ");
            buf.append("password:").append(password);
            return buf.toString();
        }
    }

    public static class FessTomcatManagerException extends FessSystemException {

        private static final long serialVersionUID = 1L;

        public FessTomcatManagerException(final String message,
                final Throwable cause) {
            super(message, cause);
        }

        public FessTomcatManagerException(final String message) {
            super(message);
        }

        public FessTomcatManagerException(final Throwable cause) {
            super(cause);
        }

    }
}
