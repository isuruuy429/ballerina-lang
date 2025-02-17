/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.ballerinalang.test.packaging;

import org.awaitility.Duration;
import org.ballerinalang.test.BaseTest;
import org.ballerinalang.test.context.BMainInstance;
import org.ballerinalang.test.context.BallerinaTestException;
import org.ballerinalang.test.context.LogLeecher;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.ballerinalang.compiler.util.ProjectDirConstants;
import org.wso2.ballerinalang.programfile.ProgramFileConstants;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.given;
import static org.ballerinalang.test.packaging.PackerinaTestUtils.deleteFiles;
import static org.wso2.ballerinalang.compiler.util.ProjectDirConstants.BLANG_COMPILED_JAR_EXT;
import static org.wso2.ballerinalang.compiler.util.ProjectDirConstants.BLANG_COMPILED_PKG_BINARY_EXT;
import static org.wso2.ballerinalang.compiler.util.ProjectDirConstants.EXEC_SUFFIX;

/**
 * Test cases related to solving dependencies using paths in Ballerina.toml.
 */
public class PathDependencyTestCase extends BaseTest {
    private Path tempHomeDirectory;
    private Path tempTestResources;
    private Map<String, String> envVariables;
    private BMainInstance balClient;
    private String orgName = "bcintegrationtest";
    
    @BeforeClass()
    public void setUp() throws IOException, BallerinaTestException {
        this.tempHomeDirectory = Files.createTempDirectory("bal-test-integration-packaging-pathdep-home-");
        this.tempTestResources = Files.createTempDirectory("bal-test-integration-packaging-pathdep-project-");
        
        // copy resources to a temp
        Path testResources = Paths.get("src", "test", "resources", "packaging", "balopath").toAbsolutePath();
        copyFolder(testResources, this.tempTestResources);
        
        PackerinaTestUtils.createSettingToml(tempHomeDirectory);
        envVariables = addEnvVariables(PackerinaTestUtils.getEnvVariables());
        balClient = new BMainInstance(balServer);
    }
    
    /**
     * Case1: Build TestProject1. Then build TestProject2 which refer to the balo of TestProject1. Run the jar of
     * TestProject2
     *
     * @throws BallerinaTestException Error when executing the commands.
     */
    @Test(description = "Case1: Test path between 2 projects.")
    public void testBaloPathCase1() throws BallerinaTestException {
        Path caseResources = tempTestResources.resolve("case1");
        // Build bee module of TestProject1
        String beeModuleBaloFileName = "bee-" + ProgramFileConstants.IMPLEMENTATION_VERSION + "-any-1.2.0"
                                     + BLANG_COMPILED_PKG_BINARY_EXT;
        
        String module1BuildMsg = "target" + File.separator + "balo" + File.separator + beeModuleBaloFileName;
        LogLeecher beeModuleBuildLeecher = new LogLeecher(module1BuildMsg);
        balClient.runMain("build", new String[]{"-c", "-a"}, envVariables, new String[]{},
                new LogLeecher[]{beeModuleBuildLeecher}, caseResources.resolve("TestProject1").toString());
        beeModuleBuildLeecher.waitForText(5000);
    
        // Build foo module of TestProject2
        String bazModuleBaloFileName = "baz" + EXEC_SUFFIX + BLANG_COMPILED_JAR_EXT;
    
        String bazBuildMsg = "target" + File.separator + "bin" + File.separator + bazModuleBaloFileName;
        LogLeecher bazModuleBuildLeecher = new LogLeecher(bazBuildMsg);
        balClient.runMain("build", new String[]{"-a"}, envVariables, new String[]{},
                new LogLeecher[]{bazModuleBuildLeecher}, caseResources.resolve("TestProject2").toString());
        bazModuleBuildLeecher.waitForText(5000);
    
        // Run and see output
        String msg = "Bar-bzzzz";
        LogLeecher bazRunLeecher = new LogLeecher(msg);
        balClient.runMain("run", new String[] {bazBuildMsg}, envVariables, new String[0],
                new LogLeecher[]{bazRunLeecher}, caseResources.resolve("TestProject2").toString());
        bazRunLeecher.waitForText(10000);
    }
    
    /**
     * Case2: Build TestProject1. Then build TestProject2 which has 2 modules. Module m1 imports and uses module bee of
     * TestProject1. Modules m2 uses m1. Run the jar if m2.
     *
     * @throws BallerinaTestException Error when executing the commands.
     */
    @Test(description = "Case2: Test path between 2 projects where 3 modules are involved and imported as a chain.")
    public void testBaloPathCase2() throws BallerinaTestException {
        Path caseResources = tempTestResources.resolve("case2");
        // Build bee module of TestProject1
        String beeModuleBaloFileName = "bee-" + ProgramFileConstants.IMPLEMENTATION_VERSION + "-any-1.2.0"
                                       + BLANG_COMPILED_PKG_BINARY_EXT;
        
        String module1BuildMsg = "target" + File.separator + "balo" + File.separator + beeModuleBaloFileName;
        LogLeecher beeModuleBuildLeecher = new LogLeecher(module1BuildMsg);
        balClient.runMain("build", new String[]{"-c", "--all"}, envVariables, new String[]{},
                new LogLeecher[]{beeModuleBuildLeecher}, caseResources.resolve("TestProject1").toString());
        beeModuleBuildLeecher.waitForText(5000);
        
        // Build modules of TestProject2
        String m1ModuleBaloFileName = "m1-" + ProgramFileConstants.IMPLEMENTATION_VERSION + "-any-2.0.0"
                                       + BLANG_COMPILED_PKG_BINARY_EXT;
        String m2ModuleExecutableFileName = "m2" + EXEC_SUFFIX + BLANG_COMPILED_JAR_EXT;
        
        String m1BuildMsg = "target" + File.separator + "balo" + File.separator + m1ModuleBaloFileName;
        String m2BuildMsg = "target" + File.separator + "bin" + File.separator + m2ModuleExecutableFileName;
        LogLeecher m1ModuleBuildLeecher = new LogLeecher(m1BuildMsg);
        LogLeecher m2ModuleBuildLeecher = new LogLeecher(m2BuildMsg);
        balClient.runMain("build", new String[]{"-a"}, envVariables, new String[]{},
                new LogLeecher[]{m1ModuleBuildLeecher, m2ModuleBuildLeecher},
                caseResources.resolve("TestProject2").toString());
        m1ModuleBuildLeecher.waitForText(5000);
        m2ModuleBuildLeecher.waitForText(5000);
        
        // Run and see output
        LogLeecher beeOutputLeecher = new LogLeecher("bzzz");
        LogLeecher m1OutputLeecher = new LogLeecher("This is org2/m1");
        balClient.runMain("run", new String[] {m2BuildMsg}, envVariables, new String[0],
                new LogLeecher[]{beeOutputLeecher, m1OutputLeecher}, caseResources.resolve("TestProject2").toString());
        beeOutputLeecher.waitForText(10000);
        m1OutputLeecher.waitForText(10000);
    }
    
    /**
     * Case3: Build TestProject1 which has a native module. Then build TestProject2 which refer to the native module
     * balo. Run the jar of TestProject2
     *
     * @throws BallerinaTestException Error when executing the commands.
     */
    @Test(description = "Case3: Test path between 2 projects which the import is a native.")
    public void testBaloPathCase3() throws BallerinaTestException {
        Path caseResources = tempTestResources.resolve("case3");
        // Build bee module of TestProject1
        String toml4jModuleBaloFileName = "toml4j-" + ProgramFileConstants.IMPLEMENTATION_VERSION + "-java-0.7.2"
                                       + BLANG_COMPILED_PKG_BINARY_EXT;

        String toml4jBuildMsg = "target" + File.separator + "balo" + File.separator + toml4jModuleBaloFileName;
        LogLeecher toml4jBuildLeecher = new LogLeecher(toml4jBuildMsg);
        balClient.runMain("build", new String[]{"--all", "-c"}, envVariables, new String[]{},
                new LogLeecher[]{toml4jBuildLeecher}, caseResources.resolve("TestProject1").toString());
        toml4jBuildLeecher.waitForText(5000);

        // Build foo module of TestProject2
        String bazModuleBaloFileName = "baz" + EXEC_SUFFIX + BLANG_COMPILED_JAR_EXT;

        String bazBuildMsg = "target" + File.separator + "bin" + File.separator + bazModuleBaloFileName;
        LogLeecher bazModuleBuildLeecher = new LogLeecher(bazBuildMsg);
        balClient.runMain("build", new String[]{"-a"}, envVariables, new String[]{},
                new LogLeecher[]{bazModuleBuildLeecher}, caseResources.resolve("TestProject2").toString());
        bazModuleBuildLeecher.waitForText(5000);

        // Run and see output
        LogLeecher bazRunLeecher = new LogLeecher("cat");
        balClient.runMain("run", new String[] {bazBuildMsg}, envVariables, new String[0],
                new LogLeecher[]{bazRunLeecher}, caseResources.resolve("TestProject2").toString());
        bazRunLeecher.waitForText(10000);
    }
    
    /**
     * Case4: 1. Build TestProject1 and push bee to central.
     * 2. Build TestProject2 and push to central where module fee depends on bee.
     * 3. Build TestProject3 which imports fee.
     * 4. Run the jar if fee. Check output.
     * 5. Modify and build bee.
     * 6. Set dependency path to bee in TestProject3.
     * 7. Check if output has changes.
     *
     * @throws BallerinaTestException Error when executing the commands.
     */
    @Test()
    public void testBaloPathCase4() throws BallerinaTestException, IOException {
        Path caseResources = tempTestResources.resolve("case4");
        // Build bee module of TestProject1
        //// change module name
        String beeModuleName = "bee" + PackerinaTestUtils.randomModuleName(10);
        Path testProjBeeModulePath = caseResources.resolve("TestProject1").resolve("src").resolve(beeModuleName);
        Files.createDirectories(caseResources.resolve("TestProject1").resolve("src").resolve(beeModuleName));
        copyFolder(caseResources.resolve("TestProject1").resolve("src").resolve("bee"), testProjBeeModulePath);
        deleteFiles(caseResources.resolve("TestProject1").resolve("src").resolve("bee"));
        
        String beeModuleBaloFileName = beeModuleName + "-" + ProgramFileConstants.IMPLEMENTATION_VERSION + "-any-1.2.0"
                                       + BLANG_COMPILED_PKG_BINARY_EXT;
        
        String module1BuildMsg = "target" + File.separator + "balo" + File.separator + beeModuleBaloFileName;
        LogLeecher beeModuleBuildLeecher = new LogLeecher(module1BuildMsg);
        balClient.runMain("build", new String[]{"-a", "-c"}, envVariables, new String[]{},
                new LogLeecher[]{beeModuleBuildLeecher}, caseResources.resolve("TestProject1").toString());
        beeModuleBuildLeecher.waitForText(5000);
    
        // Push bee module of TestProject1
        String beePushMsg = orgName + "/" + beeModuleName + ":1.2.0 [project repo -> central]";
        LogLeecher beePushLeecher = new LogLeecher(beePushMsg);
        balClient.runMain("push", new String[]{}, envVariables, new String[]{},
                new LogLeecher[]{beePushLeecher}, caseResources.resolve("TestProject1").toString());
        beePushLeecher.waitForText(5000);
    
        
        // Build fee module of TestProject2
        String feeModuleName = "fee" + PackerinaTestUtils.randomModuleName(10);
        //// replace import
        Path feeBalPath = caseResources.resolve("TestProject2").resolve("src").resolve("fee").resolve("impl.bal");
        Stream<String> lines = Files.lines(feeBalPath);
        List<String> replaced = lines.map(line -> line.replaceAll("bee", beeModuleName))
                .collect(Collectors.toList());
        Files.write(feeBalPath, replaced);
        
        //// change module name
        Path testProjFeeModulePath = caseResources.resolve("TestProject2").resolve("src").resolve(feeModuleName);
        Files.createDirectories(caseResources.resolve("TestProject2").resolve("src").resolve(feeModuleName));
        copyFolder(caseResources.resolve("TestProject2").resolve("src").resolve("fee"), testProjFeeModulePath);
        deleteFiles(caseResources.resolve("TestProject2").resolve("src").resolve("fee"));
    
        String feeModuleBaloFileName = feeModuleName + "-" + ProgramFileConstants.IMPLEMENTATION_VERSION + "-any-2.0.0"
                                       + BLANG_COMPILED_PKG_BINARY_EXT;
    
        String feeBaloFilePath = "target" + File.separator + "balo" + File.separator + feeModuleBaloFileName;
        given().with().pollInterval(Duration.TEN_SECONDS).and()
                .with().pollDelay(Duration.FIVE_SECONDS)
                .await().atMost(120, SECONDS).until(() -> {
            balClient.runMain("build", new String[]{"-a", "-c"}, envVariables, new String[]{},
                    new LogLeecher[]{}, caseResources.resolve("TestProject2").toString());
            return Files.exists(caseResources.resolve("TestProject2").resolve(Paths.get(feeBaloFilePath)));
        });
    
        // Push fee module of TestProject2
        String feePushMsg = orgName + "/" + feeModuleName + ":2.0.0 [project repo -> central]";
        LogLeecher feePushLeecher = new LogLeecher(feePushMsg);
        balClient.runMain("push", new String[]{}, envVariables, new String[]{},
                new LogLeecher[]{feePushLeecher}, caseResources.resolve("TestProject2").toString());
        feePushLeecher.waitForText(5000);

        // Build jee module of TestProject3
        //// replace import
        Path jeeBalPath = caseResources.resolve("TestProject3").resolve("src").resolve("jee").resolve("impl.bal");
        lines = Files.lines(jeeBalPath);
        replaced = lines.map(line -> line.replaceAll("fee", feeModuleName))
                .collect(Collectors.toList());
        Files.write(jeeBalPath, replaced);
    
        String jeeModuleBaloFileName = "jee" + EXEC_SUFFIX + BLANG_COMPILED_JAR_EXT;
    
        String jeeExecutableFilePath = "target" + File.separator + "bin" + File.separator + jeeModuleBaloFileName;
        given().with().pollInterval(Duration.TEN_SECONDS).and()
                .with().pollDelay(Duration.FIVE_SECONDS)
                .await().atMost(120, SECONDS).until(() -> {
            balClient.runMain("build", new String[]{"-a"}, envVariables, new String[]{},
                    new LogLeecher[]{}, caseResources.resolve("TestProject3").toString());
            return Files.exists(caseResources.resolve("TestProject3").resolve(Paths.get(jeeExecutableFilePath)));
        });
    
        // Run and see output
        LogLeecher beeRunLeecher = new LogLeecher("bzzz");
        LogLeecher feeRunLeecher = new LogLeecher("feeeee");
        balClient.runMain("run", new String[] {jeeExecutableFilePath}, envVariables, new String[0],
                new LogLeecher[]{beeRunLeecher, feeRunLeecher}, caseResources.resolve("TestProject3").toString());
        beeRunLeecher.waitForText(10000);
        feeRunLeecher.waitForText(10000);
    
        // Update and build bee module of TestProject1
        //// replace code
        Path beeBalPath = caseResources.resolve("TestProject1").resolve("src").resolve(beeModuleName)
                .resolve("say.bal");
        lines = Files.lines(beeBalPath);
        replaced = lines.map(line -> line.replaceAll("bzzz", "buzzz")).collect(Collectors.toList());
        Files.write(beeBalPath, replaced);
    
        beeModuleBuildLeecher = new LogLeecher(module1BuildMsg);
        balClient.runMain("build", new String[]{"-a", "-c"}, envVariables, new String[]{},
                new LogLeecher[]{beeModuleBuildLeecher}, caseResources.resolve("TestProject1").toString());
        beeModuleBuildLeecher.waitForText(5000);
    
        // Build and run TestProject3 with path set to bee balo in TestProject1
        //// replace Ballerina.toml
        Path ballerinaToml = caseResources.resolve("TestProject3").resolve("Ballerina.toml");
        lines = Files.lines(ballerinaToml);
        replaced = lines.map(line -> line.replaceAll("#", ""))
                .map(line -> line.replaceAll("bee", beeModuleName))
                .collect(Collectors.toList());
        Files.write(ballerinaToml, replaced);
        
        given().with().pollInterval(Duration.TEN_SECONDS).and()
                .with().pollDelay(Duration.FIVE_SECONDS)
                .await().atMost(120, SECONDS).until(() -> {
            balClient.runMain("build", new String[]{"-a"}, envVariables, new String[]{},
                    new LogLeecher[]{}, caseResources.resolve("TestProject3").toString());
            return Files.exists(caseResources.resolve("TestProject3").resolve(Paths.get(jeeExecutableFilePath)));
        });
    
        // Run and see output
        beeRunLeecher = new LogLeecher("buzzz");
        feeRunLeecher = new LogLeecher("feeeee");
        balClient.runMain("run", new String[] {jeeExecutableFilePath}, envVariables, new String[0],
                new LogLeecher[]{beeRunLeecher, feeRunLeecher}, caseResources.resolve("TestProject3").toString());
        beeRunLeecher.waitForText(10000);
        feeRunLeecher.waitForText(10000);
    }
    
    /**
     * Case5: Build TestProject1. Then build TestProject2 which refer to the balo of TestProject1. Then try to push baz
     * of TestProject2 to central with balo path dependency
     *
     * @throws BallerinaTestException Error when executing the commands.
     */
    @Test(description = "Case5: Push with path dependency.", expectedExceptions = BallerinaTestException.class)
    public void testBaloPathCase5() throws BallerinaTestException {
        Path caseResources = tempTestResources.resolve("case5");
        // Build bee module of TestProject1
        String beeModuleBaloFileName = "bee-" + ProgramFileConstants.IMPLEMENTATION_VERSION + "-any-1.2.0"
                                       + BLANG_COMPILED_PKG_BINARY_EXT;
        
        String module1BuildMsg = "target" + File.separator + "balo" + File.separator + beeModuleBaloFileName;
        LogLeecher beeModuleBuildLeecher = new LogLeecher(module1BuildMsg);
        balClient.runMain("build", new String[]{"-a", "-c"}, envVariables, new String[]{},
                new LogLeecher[]{beeModuleBuildLeecher}, caseResources.resolve("TestProject1").toString());
        beeModuleBuildLeecher.waitForText(5000);
        
        // Build foo module of TestProject2
        String bazModuleBaloFileName = "baz" + EXEC_SUFFIX + BLANG_COMPILED_JAR_EXT;
        
        String bazBuildMsg = "target" + File.separator + "bin" + File.separator + bazModuleBaloFileName;
        LogLeecher bazModuleBuildLeecher = new LogLeecher(bazBuildMsg);
        balClient.runMain("build", new String[]{"-a"}, envVariables, new String[]{},
                new LogLeecher[]{bazModuleBuildLeecher}, caseResources.resolve("TestProject2").toString());
        bazModuleBuildLeecher.waitForText(5000);
    
        // Push baz module of TestProject2
        String bazPushMsg = "[project repo -> central]";
        LogLeecher bazPushLeecher = new LogLeecher(bazPushMsg);
        balClient.runMain("push", new String[]{}, envVariables, new String[]{},
                new LogLeecher[]{bazPushLeecher}, caseResources.resolve("TestProject2").toString());
        bazPushLeecher.waitForText(5000);
    }
    
    /**
     * Get environment variables and add ballerina_home as a env variable the tmp directory.
     *
     * @return env directory variable array
     */
    private Map<String, String> addEnvVariables(Map<String, String> envVariables) {
        envVariables.put(ProjectDirConstants.HOME_REPO_ENV_KEY, tempHomeDirectory.toString());
        envVariables.put("BALLERINA_DEV_STAGE_CENTRAL", "true");
        return envVariables;
    }
    
    public  void copyFolder(Path src, Path dest) throws IOException {
        Files.walk(src).forEach(source -> copy(source, dest.resolve(src.relativize(source))));
    }
    
    private void copy(Path source, Path dest) {
        try {
            Files.copy(source, dest, REPLACE_EXISTING);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    
    @AfterClass
    private void cleanup() throws Exception {
        deleteFiles(this.tempHomeDirectory);
        deleteFiles(this.tempTestResources);
    }
}
