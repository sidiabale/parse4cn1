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

package com.parse4cn1;

import com.codename1.io.Storage;
import com.parse4cn1.util.ExternalizableParseObject;

/**
 *
 * @author sidiabale
 */
public class SerializationTest extends BaseParseTest {
    
    private final String classGameScore = "GameScore";
    
    @Override
    public boolean runTest() throws Exception {
        testSimpleParseObject();
        
        // TODO: Test all types returned as valid by Parse.isValidType()
        return true;
    }

    @Override
    public void prepare() {
        super.prepare();
        Storage.getInstance().clearStorage();
    }

    private void testSimpleParseObject() throws ParseException {
        
        final ParseObject gameScore = ParseObject.create(classGameScore);
        gameScore.put("score", 1337);
        gameScore.put("playerName", "Sean Plott");
        gameScore.put("cheatMode", false);
        gameScore.save();
        
        Storage.getInstance().writeObject(gameScore.getObjectId(), gameScore.asExternalizable());
        
        Storage.getInstance().clearCache(); // Absolutely necessary to force retrieval from storage
        final ParseObject retrieved = ((ExternalizableParseObject) Storage.getInstance().readObject(
                gameScore.getObjectId())).getParseObject();
        compareParseObjects(gameScore, retrieved);
        gameScore.delete();
    }
}
