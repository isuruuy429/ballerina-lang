/*
 *  Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

// This is a generated file. Not intended for manual editing.
package io.ballerina.plugins.idea.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import io.ballerina.plugins.idea.psi.impl.BallerinaTopLevelDefinition;

public interface BallerinaGlobalVariableDefinition extends BallerinaTopLevelDefinition {

  @Nullable
  BallerinaExpression getExpression();

  @Nullable
  BallerinaTypeName getTypeName();

  @Nullable
  BallerinaChannelType getChannelType();

  @Nullable
  PsiElement getAssign();

  @Nullable
  PsiElement getSemicolon();

  @Nullable
  PsiElement getFinal();

  @Nullable
  PsiElement getIdentifier();

  @Nullable
  PsiElement getListener();

  @Nullable
  PsiElement getPublic();

  @Nullable
  PsiElement getVar();

}
