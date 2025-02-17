/*
 *  Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.ballerinalang.test.object;

import org.ballerinalang.model.values.BValue;
import org.ballerinalang.test.util.BAssertUtil;
import org.ballerinalang.test.util.BCompileUtil;
import org.ballerinalang.test.util.BRunUtil;
import org.ballerinalang.test.util.CompileResult;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Negative Test cases for user defined object types with private fields in ballerina.
 */
public class ObjectWithPrivateFieldsNegativeTest {

    @Test(description = "Test runtime object equivalence  field access")
    public void testRuntimeObjEqNegative() {

        CompileResult compileResult = BCompileUtil.compile("test-src/object/ObjectProject",
                "object-private-fields-01-negative");
        BValue[] returns = BRunUtil.invoke(compileResult, "testRuntimeObjEqNegative");

        Assert.assertEquals(returns[0].stringValue(), "{ballerina}TypeCastError {message:\"incompatible types:" +
                " 'org.foo:user' cannot be cast to 'object-private-fields-01-negative:userB'\"}");
    }

    @Test(description = "Test private field access")
    public void testPrivateFieldAccess() {

        CompileResult compileResult = BCompileUtil.compile("test-src/object/ObjectProject",
                "object-private-fields-02-negative");

        BAssertUtil.validateError(compileResult, 0, "attempt to refer to non-accessible symbol 'ssn'", 7, 18);
        BAssertUtil.validateError(compileResult, 1, "undefined field 'ssn' in object 'testorg/org.foo:1.0.0:person'",
                7, 18);
    }

    @Test(description = "Test private object access in public functions")
    public void testPrivateObjAccess1() {
        CompileResult compileResult = BCompileUtil.compile(this, "test-src/object/ObjectProject", "private-field1");

        Assert.assertEquals(compileResult.getErrorCount(), 14);
        String expectedErrMsg1 = "attempt to refer to non-accessible symbol ";
        String expectedErrMsg2 = "attempt to expose non-public symbol ";
        int i = 0;
        BAssertUtil.validateError(compileResult, i++, expectedErrMsg2 + "'ChildFoo'", 5, 5);
        BAssertUtil.validateError(compileResult, i++, expectedErrMsg1 + "'ChildFoo.__init'", 4, 32);
        BAssertUtil.validateError(compileResult, i++, expectedErrMsg1 + "'ChildFoo'", 4, 32);
        BAssertUtil.validateError(compileResult, i++, expectedErrMsg1 + "'ChildFoo.__init'", 4, 32);
        BAssertUtil.validateError(compileResult, i++, expectedErrMsg1 + "'ChildFoo'", 4, 32);
        BAssertUtil.validateError(compileResult, i++, expectedErrMsg1 + "'ParentFoo.__init'", 4, 24);
        BAssertUtil.validateError(compileResult, i++, expectedErrMsg1 + "'PrivatePerson'", 8, 13);
        BAssertUtil.validateError(compileResult, i++, expectedErrMsg1 + "'PrivatePerson.__init'", 12, 43);
        BAssertUtil.validateError(compileResult, i++, expectedErrMsg1 + "'PrivatePerson'", 12, 43);
        BAssertUtil.validateError(compileResult, i++, expectedErrMsg1 + "'PrivatePerson.__init'", 16, 47);
        BAssertUtil.validateError(compileResult, i++, expectedErrMsg1 + "'PrivatePerson'", 16, 47);
        BAssertUtil.validateError(compileResult, i++, expectedErrMsg1 + "'PrivatePerson'", 16, 13);
        BAssertUtil.validateError(compileResult, i++, expectedErrMsg1 + "'PrivatePerson'", 20, 5);
        BAssertUtil.validateError(compileResult, i++, "unknown type 'PrivatePerson'", 20, 5);
    }

    @Test(description = "Test private object access in public functions")
    public void testPrivateObjAccess2() {
        CompileResult compileResult = BCompileUtil.compile(this, "test-src/object/ObjectProject", "private-field2");

        Assert.assertEquals(compileResult.getErrorCount(), 4);
        String expectedErrMsg1 = "attempt to refer to non-accessible symbol ";
        String expectedErrMsg2 = "attempt to expose non-public symbol ";
        int i = 0;
        BAssertUtil.validateError(compileResult, i++, expectedErrMsg2 + "'ChildFoo'", 5, 5);
        BAssertUtil.validateError(compileResult, i++, expectedErrMsg1 + "'FooFamily'", 5, 13);
        BAssertUtil.validateError(compileResult, i++, expectedErrMsg1 + "'address'", 10, 13);
        BAssertUtil.validateError(compileResult, i++,
                "undefined field 'address' in object 'testorg/org.foo.baz:1.0.0:FooEmployee'", 10, 13);
    }
}
