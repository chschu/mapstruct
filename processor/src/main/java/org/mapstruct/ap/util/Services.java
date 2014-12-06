/**
 *  Copyright 2012-2014 Gunnar Morling (http://www.gunnarmorling.de/)
 *  and/or other contributors as indicated by the @authors tag. See the
 *  copyright.txt file in the distribution for a full listing of all
 *  contributors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.mapstruct.ap.util;

import java.util.ServiceLoader;

import org.mapstruct.spi.Name;

public class Services {

    private Services() {
    }

    /**
     * Returns the default implementation of the specified service provider interface.
     *
     * @param spi The service provider interface.
     * @return The default implementation.
     * @throws IllegalArgumentException If there is no default implementation for the specified service provider
     *             interface.
     */
    public static <T> T get(Class<T> spi) {
        return get( spi, "" );
    }

    /**
     * Returns the implementation of the specified service provider interface, with the {@link Name#value value} of the
     * {@link Name} annotation matching the specified name.
     *
     * @param spi The service provider interface.
     * @param name The {@link Name#value()} to match.
     * @return The implementation with the specified name.
     * @throws IllegalArgumentException If there is no service provider implementation for the specified service
     *             provider interface and name.
     */
    public static <T> T get(Class<T> spi, String name) {
        T matchingImplementation = null;

        for ( T implementation : ServiceLoader.load( spi, spi.getClassLoader() ) ) {
            Name nameAnnotation = implementation.getClass().getAnnotation( Name.class );
            if ( nameAnnotation == null ) {
                throw new IllegalStateException( "The service provider implementation "
                    + implementation.getClass().getCanonicalName() + " does not have the mandatory @Name annotation." );
            }

            if ( nameAnnotation.value().equals( name ) ) {
                if ( matchingImplementation == null ) {
                    matchingImplementation = implementation;
                }
                else {
                    throw new IllegalStateException( "The service provider implementations "
                        + matchingImplementation.getClass().getCanonicalName() + " and "
                        + implementation.getClass().getCanonicalName() + " both have the @Name value '" + name + "'." );
                }
            }
        }

        if ( matchingImplementation == null ) {
            throw new IllegalArgumentException( "No implementation could be found for the service provider interface "
                + spi.getCanonicalName() + " and the @Name value '" + name + "'." );
        }

        return matchingImplementation;
    }
}
