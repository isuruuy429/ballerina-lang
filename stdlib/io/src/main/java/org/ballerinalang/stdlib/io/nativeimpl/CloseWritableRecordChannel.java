/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package org.ballerinalang.stdlib.io.nativeimpl;

import org.ballerinalang.jvm.scheduling.Strand;
import org.ballerinalang.jvm.values.ObjectValue;
import org.ballerinalang.jvm.values.connector.NonBlockingCallback;
import org.ballerinalang.model.types.TypeKind;
import org.ballerinalang.natives.annotations.BallerinaFunction;
import org.ballerinalang.natives.annotations.Receiver;
import org.ballerinalang.natives.annotations.ReturnType;
import org.ballerinalang.stdlib.io.channels.base.DelimitedRecordChannel;
import org.ballerinalang.stdlib.io.events.EventContext;
import org.ballerinalang.stdlib.io.events.EventRegister;
import org.ballerinalang.stdlib.io.events.EventResult;
import org.ballerinalang.stdlib.io.events.Register;
import org.ballerinalang.stdlib.io.events.records.CloseDelimitedRecordEvent;
import org.ballerinalang.stdlib.io.utils.IOConstants;
import org.ballerinalang.stdlib.io.utils.IOUtils;

/**
 * Extern function ballerina/io#closeTextRecordChannel.
 *
 * @since 0.982.0
 */
@BallerinaFunction(
        orgName = "ballerina", packageName = "io",
        functionName = "close",
        receiver = @Receiver(type = TypeKind.OBJECT,
                structType = "WritableTextRecordChannel",
                structPackage = "ballerina/io"),
        returnType = {@ReturnType(type = TypeKind.ERROR)},
        isPublic = true
)
public class CloseWritableRecordChannel {

    public static Object close(Strand strand, ObjectValue channel) {
        DelimitedRecordChannel recordChannel = (DelimitedRecordChannel)
                channel.getNativeData(IOConstants.TXT_RECORD_CHANNEL_NAME);
        EventContext eventContext = new EventContext(new NonBlockingCallback(strand));
        CloseDelimitedRecordEvent closeEvent = new CloseDelimitedRecordEvent(recordChannel, eventContext);
        Register register = EventRegister.getFactory().register(closeEvent, CloseWritableRecordChannel::closeChannel);
        eventContext.setRegister(register);
        register.submit();
        return null;
    }

    private static EventResult closeChannel(EventResult<Boolean, EventContext> result) {
        EventContext eventContext = result.getContext();
        NonBlockingCallback callback = eventContext.getNonBlockingCallback();
        Throwable error = eventContext.getError();
        if (null != error) {
            callback.setReturnValues(IOUtils.createError(error.getMessage()));
        } else {
            callback.setReturnValues(null);
        }
        callback.notifySuccess();
        return result;
    }
}
