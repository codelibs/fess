/*
 * Copyright 2012-2019 CodeLibs Project and the Others.
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
package org.codelibs.fess.score;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.codelibs.core.misc.Pair;
import org.codelibs.fess.exception.FessSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.GenericJson;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.analyticsreporting.v4.AnalyticsReporting;
import com.google.api.services.analyticsreporting.v4.AnalyticsReportingScopes;
import com.google.api.services.analyticsreporting.v4.model.ColumnHeader;
import com.google.api.services.analyticsreporting.v4.model.DateRangeValues;
import com.google.api.services.analyticsreporting.v4.model.GetReportsRequest;
import com.google.api.services.analyticsreporting.v4.model.GetReportsResponse;
import com.google.api.services.analyticsreporting.v4.model.MetricHeaderEntry;
import com.google.api.services.analyticsreporting.v4.model.Report;
import com.google.api.services.analyticsreporting.v4.model.ReportRequest;
import com.google.api.services.analyticsreporting.v4.model.ReportRow;

public class GoogleAnalyticsScoreBooster extends ScoreBooster {
    private static final Logger logger = LoggerFactory.getLogger(GoogleAnalyticsScoreBooster.class);

    private JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

    private String applicationName = "Fess Score Booster";

    private String keyFileLocation;

    private String serviceAccountEmail;

    private AnalyticsReporting analyticsReporting = null;

    private final List<Pair<String[], ReportRequest>> reportRequesList = new ArrayList<>();

    @PostConstruct
    public void init() {
        if (!Paths.get(keyFileLocation).toFile().exists()) {
            logger.info("GA Key File does not exist.");
            return;
        }

        if (reportRequesList.isEmpty()) {
            logger.info("No reports.");
            return;
        }

        analyticsReporting = initializeAnalyticsReporting();
        enable();
    }

    private AnalyticsReporting initializeAnalyticsReporting() {
        try {
            final HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            final GoogleCredential credential =
                    new GoogleCredential.Builder().setTransport(httpTransport).setJsonFactory(jsonFactory)
                            .setServiceAccountId(serviceAccountEmail).setServiceAccountPrivateKeyFromP12File(new File(keyFileLocation))
                            .setServiceAccountScopes(AnalyticsReportingScopes.all()).build();

            return new AnalyticsReporting.Builder(httpTransport, jsonFactory, credential).setApplicationName(applicationName).build();
        } catch (final Exception e) {
            throw new FessSystemException("Failed to initialize GA.", e);
        }
    }

    public void setJsonFactory(final JsonFactory jsonFactory) {
        this.jsonFactory = jsonFactory;
    }

    public void setApplicationName(final String applicationName) {
        this.applicationName = applicationName;
    }

    public void setKeyFileLocation(final String keyFileLocation) {
        this.keyFileLocation = keyFileLocation;
    }

    public void setServiceAccountEmail(final String serviceAccountEmail) {
        this.serviceAccountEmail = serviceAccountEmail;
    }

    @Override
    public long process() {
        long counter = 0;
        for (final Pair<String[], ReportRequest> entry : reportRequesList) {
            final GetReportsRequest getReport = new GetReportsRequest().setReportRequests(Arrays.asList(entry.getSecond()));
            try {
                final GetReportsResponse response = analyticsReporting.reports().batchGet(getReport).execute();
                if (logger.isDebugEnabled()) {
                    logger.debug(toPrettyString(response));
                }
                for (final Report report : response.getReports()) {
                    final List<ReportRow> rows = report.getData().getRows();
                    final String[] baseUrls = entry.getFirst();
                    if (rows == null) {
                        logger.info("No data found for " + String.join(",", baseUrls));
                        continue;
                    }

                    final ColumnHeader header = report.getColumnHeader();
                    final List<String> dimensionHeaders = header.getDimensions();
                    final List<MetricHeaderEntry> metricHeaders = header.getMetricHeader().getMetricHeaderEntries();
                    for (final ReportRow row : rows) {
                        final List<DateRangeValues> metrics = row.getMetrics();
                        for (int j = 0; j < metrics.size(); j++) {
                            String path = null;
                            Long count = null;

                            final List<String> dimensions = row.getDimensions();
                            for (int i = 0; i < dimensionHeaders.size() && i < dimensions.size(); i++) {
                                final String name = dimensionHeaders.get(i);
                                if ("ga:pagePath".equals(name)) {
                                    path = dimensions.get(i);
                                }
                            }

                            final DateRangeValues values = metrics.get(j);
                            for (int k = 0; k < values.getValues().size() && k < metricHeaders.size(); k++) {
                                final String name = metricHeaders.get(k).getName();
                                if ("ga:pageviews".equals(name)) {
                                    count = Long.parseLong(values.getValues().get(k));
                                }
                            }

                            if (path != null && count != null) {
                                for (final String baseUrl : baseUrls) {
                                    try {
                                        final String url = normalizeUrl(path, baseUrl);
                                        final Map<String, Object> params = new HashMap<>();
                                        params.put("url", url);
                                        params.put("count", count);
                                        counter += updateScore(params);
                                    } catch (final Exception e) {
                                        logger.warn("Invalid url: " + baseUrl + " + " + path, e);
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (final IOException e) {
                logger.warn("Failed to access GA.", e);
            }
        }
        flush();
        return counter;
    }

    protected String normalizeUrl(final String path, final String baseUrl) throws MalformedURLException {
        return new URL(baseUrl + path.toString()).toString();
    }

    private String toPrettyString(final GenericJson json) {
        try {
            return json.toPrettyString();
        } catch (final IOException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to parse json.", e);
            }
            return e.getMessage();
        }
    }

    public void addReportRequest(final List<String> urls, final ReportRequest request) {
        reportRequesList.add(new Pair<>(urls.toArray(new String[urls.size()]), request));
    }

    public void addReportRequest(final String url, final ReportRequest request) {
        reportRequesList.add(new Pair<>(new String[] { url }, request));
    }
}
