/*
*  Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.ballerinalang.jvm.util;

import org.ballerinalang.jvm.types.BPackage;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * This file contains a list of constant values used by Ballerina Compiler and the Bytecode interpreter.
 *
 * @since 0.90
 */
public class BLangConstants {

    public static final int MAGIC_NUMBER = 0xBA1DA4CE;
    public static final short VERSION_NUMBER = 26;

    public static final String MAIN_FUNCTION_NAME = "main";
    public static final String INIT_FUNCTION_SUFFIX = "..<init>";
    public static final String CONSTRUCTOR_FUNCTION_SUFFIX = "__init";
    public static final String START_FUNCTION_SUFFIX = ".<start>";
    public static final String STOP_FUNCTION_SUFFIX = ".<stop>";
    public static final String TEST_INIT_FUNCTION_SUFFIX = ".<testinit>";
    public static final String TEST_START_FUNCTION_SUFFIX = ".<teststart>";
    public static final String TEST_STOP_FUNCTION_SUFFIX = ".<teststop>"; 
    public static final String MODULE_INIT_CLASS_NAME = "___init";
    public static final String BALLERINA_CONFIG_PREFIX = "--b7a.";
    public static final String BALLERINA_ARGS_INIT_PREFIX = "--";
    public static final int BALLERINA_ARGS_INIT_PREFIX_LENGTH = BALLERINA_ARGS_INIT_PREFIX.length();
    public static final String CONFIG_SEPARATOR = "=";
    public static final String INVALID_OPTION_ERROR_MESSAGE = "ballerina: unknown option: ";

    public static final String EMPTY = "";
    public static final String ANON_ORG = "$anon";
    public static final String DOT = ".";
    public static final String ORG_NAME_SEPARATOR = "/";
    public static final String VERSION_SEPARATOR = ":";
    

    public static final String BLANG_SRC_FILE_EXT = "bal";
    public static final String BLANG_SRC_FILE_SUFFIX = "." + BLANG_SRC_FILE_EXT;

    public static final String BLANG_EXEC_FILE_EXT = "balx";
    public static final String BLANG_EXEC_FILE_SUFFIX = "." + BLANG_EXEC_FILE_EXT;

    public static final String JAR_FILE_SUFFIX = ".jar";

    public static final String BLANG_COMPILED_PACKAGE_FILE_EXT = "balo";
    public static final String BLANG_COMPILED_PACKAGE_FILE_SUFFIX = "." + BLANG_COMPILED_PACKAGE_FILE_EXT;

    // int, float, string, boolean, decimal, reference type
    public static final int NO_OF_VAR_TYPE_CATEGORIES = 5;
    public static final int INT_OFFSET = 0;
    public static final int FLOAT_OFFSET = 1;
    public static final int STRING_OFFSET = 2;
    public static final int BOOL_OFFSET = 3;
    public static final int REF_OFFSET = 4;

    public static final String USER_REPO_ENV_KEY = "BALLERINA_REPOSITORY";
    public static final String USER_REPO_DEFAULT_DIRNAME = ".ballerina";
    public static final String USER_REPO_ARTIFACTS_DIRNAME = "artifacts";
    public static final String USER_REPO_SRC_DIRNAME = "src";
    public static final String USER_REPO_OBJ_DIRNAME = "obj";
    public static final String USER_REPO_METADATA_DIRNAME = "metadata";

    public static final String BALLERINA_BUILTIN_PKG_PREFIX = "ballerina";

    public static final String USER_HOME = "user.home";
    public static final String BALLERINA_HOME = "ballerina.home";
    public static final String BALLERINA_PACKAGE_PREFIX = "ballerina" + ORG_NAME_SEPARATOR;
    public static final String BALLERINA_BUILTIN_PKG = BALLERINA_PACKAGE_PREFIX + "builtin";
    public static final String BALLERINA_RUNTIME_PKG = BALLERINA_PACKAGE_PREFIX + "runtime";
    public static final String BALLERINA_LANG_ERROR_PKG = BALLERINA_PACKAGE_PREFIX + "lang_error";
    public static final String BALLERINA_MAX_POOL_SIZE_ENV_VAR = "BALLERINA_MAX_POOL_SIZE";


    public static final BPackage BALLERINA_BUILTIN_PKG_ID = new BPackage(BALLERINA_BUILTIN_PKG_PREFIX, "builtin");
    public static final BPackage BALLERINA_RUNTIME_PKG_ID = new BPackage(BALLERINA_BUILTIN_PKG_PREFIX, "runtime");
    public static final BPackage BALLERINA_LANG_ERROR_PKG_ID = new BPackage(BALLERINA_BUILTIN_PKG_PREFIX, "lang.error");

    // Zero value for string
    public static final String STRING_NULL_VALUE = null;
    
    // Empty value for string
    public static final String STRING_EMPTY_VALUE = "";

    public static final Integer BBYTE_MIN_VALUE = 0;
    public static final Integer BBYTE_MAX_VALUE = 255;
    public static final double BINT_MAX_VALUE_DOUBLE_RANGE_MAX = 9223372036854775807.5;
    public static final double BINT_MIN_VALUE_DOUBLE_RANGE_MIN = -9223372036854775807.6;
    public static final BigDecimal BINT_MAX_VALUE_BIG_DECIMAL_RANGE_MAX = new BigDecimal("9223372036854775807.5",
                                                                                         MathContext.DECIMAL128);
    public static final BigDecimal BINT_MIN_VALUE_BIG_DECIMAL_RANGE_MIN = new BigDecimal("-9223372036854775807.6",
                                                                                         MathContext.DECIMAL128);

    public static final String COLON = ":";

    public static final String DEFAULT_WORKER_NAME = "default";

    // ballerina environment properties.
    public static final String UTIL_LOGGING_CONFIG_CLASS_PROPERTY = "java.util.logging.config.class";
    public static final String UTIL_LOGGING_MANAGER_CLASS_PROPERTY = "java.util.logging.manager";
    public static final String UTIL_LOGGING_CONFIG_CLASS_VALUE = "org.ballerinalang.logging.util.LogConfigReader";
    public static final String UTIL_LOGGING_MANAGER_CLASS_VALUE = "org.ballerinalang.logging.BLogManager";


    // runtime related error message constant values
    public static final String INTERNAL_ERROR_MESSAGE =
            "ballerina: Oh no, something really went wrong. Bad. Sad.\n" +
            "\n" +
            "There should be a file named \"ballerina-internal.log\" in the current directory.\n" +
            "If you are able to share with us the code that broke Ballerina then\n" +
            "we would REALLY appreciate if you would report this to us:\n" +
            "go to https://github.com/ballerina-platform/ballerina-lang/issues and\n" +
            "create a bug report with both this log file and the sample code.\n" +
            "\n" +
            "We thank you for helping make us better dancers.";
}
