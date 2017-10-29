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

package org.opennms.netmgt.telemetry.adapters.jti;

import java.lang.reflect.Constructor;
import org.opennms.features.telemetry.adapters.factory.api.AdapterFactory;
import org.opennms.netmgt.telemetry.adapters.api.Adapter;

public class JtiAdapterFactory implements AdapterFactory {
	

    public Adapter createAdapter(String className) {

        final Object adapterInstance;
        try {
            final Class<?> clazz = Class.forName(className);
            Constructor<?> ctor = clazz.getConstructor();
            adapterInstance = ctor.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(
                    String.format("Failed to instantiate adapter with class name '%s'.", className, e));
        }

        final Adapter adapter = (Adapter) adapterInstance;
        return adapter;
    }

}
