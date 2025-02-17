// Copyright (c) 2019 WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
//
// WSO2 Inc. licenses this file to you under the Apache License,
// Version 2.0 (the "License"); you may not use this file except
// in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

import ballerina/'lang\.string as strings;

string str = "Hello Ballerina!";

function testToLower() returns string {
    return str.toLowerAscii();
}

function testLength() returns int {
    return "Hello Ballerina!".length();
}

function testSubString() returns string {
    return str.substring(6, 9);
}

function testIterator() returns string[] {
    string str = "Foo Bar";

    abstract object {
         public function next() returns record {| string value; |}?;
    } itr = str.iterator();

    string[] chars = [];
    int i = 0;
    record {| string value; |}|() elem = itr.next();

    while (elem is record {| string value; |}) {
        chars[i] = elem.value;
        elem = itr.next();
        i += 1;
    }

    return chars;
}

function testStartsWith() returns boolean {
    return strings:startsWith(str, "Hello");
}

function testConcat() returns string {
    return strings:concat("Hello ", "from ", "Ballerina");
}

function testIndexOf(string substr) returns int? {
    return str.indexOf(substr);
}

function testEndsWith(string substr) returns boolean {
    return str.endsWith(substr);
}

function testFromBytes() returns string|error {
    byte[] bytes = [72, 101, 108, 108, 111, 32, 66, 97, 108, 108, 101, 114, 105, 110, 97, 33];
    return strings:fromBytes(bytes);
}

function testJoin() returns string {
    string[] days = ["Sunday", "Monday", "Tuesday"];
    return strings:'join(", ", ...days);
}

function testCodePointCompare(string st1, string st2) returns int {
    return strings:codePointCompare(st1, st2);
}

function testGetCodepoint(string st, int index) returns int {
    return st.getCodePoint(index);
}

function testToCodepointInts(string st) returns int[] {
    return st.toCodePointInts();
}

function testFromCodePointInts(int[] ints) returns string|error {
    return strings:fromCodePointInts(ints);
}

function testSubstringOutRange() returns string {
    return "abcdef".substring(7, 9);
}

function testSubstring(string s, int si, int ei) returns error|string {
    error|string sub = trap s.substring(si, ei);
    return sub;
}
