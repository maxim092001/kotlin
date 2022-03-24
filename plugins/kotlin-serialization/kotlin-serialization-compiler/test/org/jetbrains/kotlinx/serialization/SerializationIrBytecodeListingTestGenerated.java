/*
 * Copyright 2010-2021 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlinx.serialization;

import com.intellij.testFramework.TestDataPath;
import org.jetbrains.kotlin.test.JUnit3RunnerWithInners;
import org.jetbrains.kotlin.test.KotlinTestUtils;
import org.jetbrains.kotlin.test.util.KtTestUtil;
import org.jetbrains.kotlin.test.TestMetadata;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.regex.Pattern;

/** This class is generated by {@link org.jetbrains.kotlin.generators.tests.TestsPackage}. DO NOT MODIFY MANUALLY */
@SuppressWarnings("all")
@TestMetadata("plugins/kotlin-serialization/kotlin-serialization-compiler/testData/codegen")
@TestDataPath("$PROJECT_ROOT")
@RunWith(JUnit3RunnerWithInners.class)
public class SerializationIrBytecodeListingTestGenerated extends AbstractSerializationIrBytecodeListingTest {
    private void runTest(String testDataFilePath) throws Exception {
        KotlinTestUtils.runTest(this::doTest, this, testDataFilePath);
    }

    public void testAllFilesPresentInCodegen() throws Exception {
        KtTestUtil.assertAllTestsPresentByMetadataWithExcluded(this.getClass(), new File("plugins/kotlin-serialization/kotlin-serialization-compiler/testData/codegen"), Pattern.compile("^(.+)\\.kt$"), null, true);
    }

    @TestMetadata("Basic.kt")
    public void testBasic() throws Exception {
        runTest("plugins/kotlin-serialization/kotlin-serialization-compiler/testData/codegen/Basic.kt");
    }

    @TestMetadata("Delegated.kt")
    public void testDelegated() throws Exception {
        runTest("plugins/kotlin-serialization/kotlin-serialization-compiler/testData/codegen/Delegated.kt");
    }

    @TestMetadata("Sealed.kt")
    public void testSealed() throws Exception {
        runTest("plugins/kotlin-serialization/kotlin-serialization-compiler/testData/codegen/Sealed.kt");
    }
}
