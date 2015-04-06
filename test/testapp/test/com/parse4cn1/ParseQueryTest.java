///*
// * Copyright 2015 Chidiebere Okwudire.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package com.parse4cn1;
//
//import java.util.Arrays;
//
///**
// *
// * @author sidiabale
// */
//public class ParseQueryTest extends BaseParseTest {
//    @Override
//    public boolean runTest() throws Exception {
//        testQueryFormat();
//        
//        return true;
//    }
//
//    private void testQueryFormat() throws ParseException {
//        ParseQuery<ParseObject> query = ParseQuery.create("games");
//        String[] names = {"Jang Min Chul", "Sean Plott"};
//        query.addAscendingOrder("loosingScore")
//            .addDescendingOrder("score2")
//            .whereGreaterThan("score1", 6)
//            .whereLessThanOrEqualTo("score2", 2)
//            .whereContainedIn("playerName", Arrays.asList(names));;
//        query.limit(10);
//        query.skip(5);
//        System.out.println(query.toJson());
//    }
//}
