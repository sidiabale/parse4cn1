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
 * Provides access to native functionality related to Parse push notifications.
 * This functionality is to be provided by native Parse SDKs.
 * 
 * <p>As a rule-of-thumb, functionality implemented here should be impossible in 
 * a cross-platform manner using the REST API.
 * 
 */
public interface ParsePushNative extends NativeInterface {

    /**
     * Sets the value of the icon badge for iOS apps.
     * <p>The implementation should also update the badge field of the current 
     * installation in the Parse backend.
     * @param value The new badge value.
     */
    public void setBadge(int value);
}
