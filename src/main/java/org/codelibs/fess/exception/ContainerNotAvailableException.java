/*
 * Copyright 2012-2025 CodeLibs Project and the Others.
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
package org.codelibs.fess.exception;

/**
 * Exception thrown when a dependency injection container or component is not available.
 * This exception indicates that the required container or a specific component within it cannot be accessed.
 */
public class ContainerNotAvailableException extends FessSystemException {

    private static final long serialVersionUID = 1L;

    /** The name of the component that is not available. */
    private String componentName;

    /**
     * Constructor with component name.
     * @param componentName The name of the component that is not available.
     */
    public ContainerNotAvailableException(final String componentName) {
        super(componentName + " is not available.");
    }

    /**
     * Constructor with component name and cause.
     * @param componentName The name of the component that is not available.
     * @param cause The cause of the exception.
     */
    public ContainerNotAvailableException(final String componentName, final Throwable cause) {
        super(componentName + " is not available.", cause);
        this.componentName = componentName;
    }

    /**
     * Constructor with cause only.
     * @param cause The cause of the exception.
     */
    public ContainerNotAvailableException(final Throwable cause) {
        super("Container is not available.");
        componentName = "container";
    }

    /**
     * Gets the name of the component that is not available.
     * @return The component name.
     */
    public String getComponentName() {
        return componentName;
    }

}
