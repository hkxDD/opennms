/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2017-2017 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2017 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/

package org.opennms.core.ipc.common.aws.sqs;

import org.osgi.service.cm.ConfigurationAdmin;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class AmazonSQSConfigBean implements AmazonSQSConfig {
    private final String queuePrefix;
    private final String region;
    private final String accessKey;
    private final String secretKey;
    private final boolean useHttp;
    private final AmazonSQSQueueConfig sinkQueueConfig;

    public AmazonSQSConfigBean() {
        this(getConfigMapFromSystemProperties());
    }

    public AmazonSQSConfigBean(ConfigurationAdmin configAdmin) throws IOException {
        // Use the system properties as initial values and overwrite
        // the values with the properties from org.opennms.core.ipc.aws.sqs.cfg
        this(getConfigMapFromConfigAdmin(configAdmin, getConfigMapFromSystemProperties()));
    }

    protected AmazonSQSConfigBean(Map<String,String> sqsConfig) {
        queuePrefix = sqsConfig.get(AmazonSQSConstants.AWS_QUEUE_NAME_PREFIX);
        region = sqsConfig.get(AmazonSQSConstants.AWS_REGION);
        accessKey = sqsConfig.get(AmazonSQSConstants.AWS_ACCESS_KEY_ID);
        secretKey = sqsConfig.get(AmazonSQSConstants.AWS_SECRET_ACCESS_KEY);
        useHttp = Boolean.TRUE.toString().equals(sqsConfig.get(AmazonSQSConstants.AWS_USE_HTTP));
        sinkQueueConfig = new AmazonSQSQueueConfig(sqsConfig);
    }

    private static Map<String, String> getConfigMapFromSystemProperties() {
        final Map<String,String> sqsConfig = new HashMap<>();
        System.getProperties().entrySet().stream()
                .filter(e -> e.getKey() != null && e.getKey() instanceof String)
                .filter(e -> ((String)e.getKey()).startsWith(AmazonSQSConstants.AWS_CONFIG_PID))
                .forEach(e -> sqsConfig.put(((String)e.getKey()).substring(
                        AmazonSQSConstants.AWS_CONFIG_SYS_PROP_PREFIX.length()), (String)e.getValue()));
        return sqsConfig;
    }

    private static Map<String, String> getConfigMapFromConfigAdmin(ConfigurationAdmin configAdmin, Map<String, String> sqsConfig) throws IOException {
        final Dictionary<String, Object> properties = configAdmin.getConfiguration(AmazonSQSConstants.AWS_CONFIG_PID).getProperties();
        if (properties != null) {
            final Enumeration<String> keys = properties.keys();
            while (keys.hasMoreElements()) {
                final String key = keys.nextElement();
                sqsConfig.put(key, (String)properties.get(key));
            }
        }
        return sqsConfig;
    }

    @Override
    public String getQueuePrefix() {
        return queuePrefix;
    }

    @Override
    public String getRegion() {
        return region;
    }

    @Override
    public boolean hasStaticCredentials() {
        return accessKey != null && secretKey != null;
    }

    @Override
    public String getAccessKey() {
        return accessKey;
    }

    @Override
    public String getSecretKey() {
        return secretKey;
    }

    @Override
    public boolean isUseHttp() {
        return useHttp;
    }

    @Override
    public AmazonSQSQueueConfig getSinkQueueConfig() {
        return sinkQueueConfig;
    }

    @Override
    public String toString() {
        return "AmazonSQSConfigBean{" +
                "queuePrefix='" + queuePrefix + '\'' +
                ", region='" + region + '\'' +
                ", accessKey='" + accessKey != null ? "********" : accessKey + '\'' +
                ", secretKey='" + secretKey != null ? "********" : secretKey + '\'' +
                ", useHttp=" + useHttp +
                ", sinkQueueConfig=" + sinkQueueConfig +
                '}';
    }
}
