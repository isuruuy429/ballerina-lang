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

import ballerina/runtime;
import ballerina/'lang\.int as ints;
import ballerina/'lang\.value as val;

type Teacher record {
    string name;
    int age;
    string status;
    string batch;
    string school;
};

type TeacherOutput record {
    string name;
    int age;
    int sumAge;
    int count;
};

int index = 0;
stream<Teacher> inputStream = new;
stream<TeacherOutput> outputStream = new;

TeacherOutput[] globalTeacherOutputArray = [];

function startAggregationWithGroupByQuery() returns TeacherOutput[] {

    Teacher[] teachers = [];
    Teacher t1 = { name: "Mohan", age: 30, status: "single", batch: "LK2014", school: "Hindu College" };
    Teacher t2 = { name: "Raja", age: 45, status: "single", batch: "LK2014", school: "Hindu College" };
    Teacher t3 = { name: "Bobo", age: 15 , status: "single", batch: "LK2014", school: "Foo College" };
    teachers[0] = t1;
    teachers[1] = t2;
    teachers[2] = t3;
    teachers[3] = t1;
    teachers[4] = t1;
    teachers[5] = t3;
    teachers[6] = t1;
    teachers[7] = t2;
    teachers[8] = t3;
    teachers[9] = t1;

    foo();

    outputStream.subscribe(function(TeacherOutput e) {printTeachers(e);});
    foreach var t in teachers {
        inputStream.publish(t);
    }

    int count = 0;
    while(true) {
        runtime:sleep(500);
        count += 1;
        if((globalTeacherOutputArray.length()) == 7 || count == 10) {
            break;
        }
    }
    return globalTeacherOutputArray;
}


//  ------------- Query to be implemented -------------------------------------------------------
//  from inputStream where inputStream.age > 25
//  select inputStream.name, inputStream.age, sum (inputStream.age) as sumAge, count() as count
//  group by inputStream.name
//      => (TeacherOutput [] o) {
//            outputStream.publish(o);
//      }
//

function foo() {
    forever {
        from inputStream where inputStream.age > 25 window length(5)
        select inputStream.name, inputStream.age, <int>ints:fromString(val:toString(sum(inputStream.age))) as sumAge,
        <int>ints:fromString(count().toString()) as count
        group by inputStream.name => (TeacherOutput [] teachers) {
            foreach var t in teachers {
                outputStream.publish(t);
            }
        }
    }
}

function printTeachers(TeacherOutput e) {
    addToGlobalEmployeeArray(e);
}

function addToGlobalEmployeeArray(TeacherOutput e) {
    globalTeacherOutputArray[index] = e;
    index = index + 1;
}