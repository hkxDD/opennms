/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2017 The OpenNMS Group, Inc.
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

package org.opennms.netmgt.telemetry.adapters.netflow.ipfix;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.google.common.base.MoreObjects;

public final class Set<R extends Record> implements Iterable<R> {

    public interface RecordParser<R extends Record> {
        R parse(final ByteBuffer buffer);

        int getMinimumRecordLength();
    }

    /*
     +--------------------------------------------------+
     | Set Header                                       |
     +--------------------------------------------------+
     | record                                           |
     +--------------------------------------------------+
     | record                                           |
     +--------------------------------------------------+
      ...
     +--------------------------------------------------+
     | record                                           |
     +--------------------------------------------------+
     | Padding (opt.)                                   |
     +--------------------------------------------------+
    */

    public final SetHeader header;
    public final List<R> records;

    Set(final SetHeader header,
        final RecordParser<R> parser,
        final ByteBuffer buffer) {
        this.header = header;

        final List<R> records = new LinkedList<>();
        while (buffer.remaining() >= parser.getMinimumRecordLength()) {
            records.add(parser.parse(buffer));
        }
        this.records = Collections.unmodifiableList(records);
    }

    public int size() {
        return this.header.length;
    }

    public boolean isValid() {
        if (!this.header.isValid()) {
            return false;
        }

        for (final Record record : this.records) {
            if (!record.isValid()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public Iterator<R> iterator() {
        return this.records.iterator();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("header", header)
                .add("records", records)
                .toString();
    }
}
