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

import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.AmazonSQSException;

import java.util.Properties;

public interface AmazonSQSManager {

    String getSinkQueueUrlAndCreateIfNecessary(String moduleId);

    String getRpcRequestQueueNameAndCreateIfNecessary(String moduleId, String location);

    String getRpcReplyQueueNameAndCreateIfNecessary(String moduleId, String location);

    AmazonSQS getSQSClient();

    SQSConnectionFactory getSQSConnectionFactory();

    /**
     * Send message.
     * <p>This is a blocking operation. If AWS is unreachable,
     * the method will keep retrying indefinitely until the message is delivered
     * or the thread is interrupted.</p>
     *
     * @param queueUrl the queue URL
     * @param body the message body
     * @return the message ID
     */
    String sendMessage(String queueUrl, String body) throws InterruptedException;

}
