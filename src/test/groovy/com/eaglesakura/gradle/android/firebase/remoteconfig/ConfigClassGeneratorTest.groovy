package com.eaglesakura.gradle.android.firebase.remoteconfig

class ConfigClassGeneratorTest extends GroovyTestCase {

    /**
     * 指定されたプロパティをキャメルケースへと変換する
     */
    public void testMethodNameGenerate() {
        assertEquals(ConfigClassGenerator.toCamelCase("testMethod"), "TestMethod");
        assertEquals(ConfigClassGenerator.toCamelCase("test_case_method"), "TestCaseMethod");
    }
}
