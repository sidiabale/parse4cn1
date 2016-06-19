/*
 * Copyright 2015 Chidiebere Okwudire.
 *
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
 */
package com.parse4cn1.nativeinterface;

import com.codename1.system.NativeInterface;

/**
 * Provides access to native functionality related to Parse installations.
 * This functionality is to be provided by native Parse SDKs.
 */
public interface ParseInstallationNative extends NativeInterface {
    
    /**
     * Initializes the Parse SDK.
     * <p>This method may raise an exception, e.g., if the Parse SDK is already 
     * initialized and it is the responsibility of the caller to handle any such exceptions.
     * 
     * @param apiEndpoint The path to the Parse backend, e.g. "your_parse_backend_website_url"/parse.
     * @param applicationId The Parse application ID.
     * @param clientKey The Parse client key.
     */
    public void initialize(final String apiEndpoint, final String applicationId, final String clientKey);
    
    /**
     * Retrieves the installation ID of the current installation. An installation 
     * should be created on the fly and persisted to Parse if one does not exist.
     * 
     * <p>It is recommended to perform the save operation in a background thread
     * to prevent blocking. It is up to the caller to retry if an immediate attempt 
     * to retrieve the installation using the ID returned by this method fails.
     * 
     * @return The current installation's ID.
     */
    public String getInstallationId();
}
