package com.eaglesakura.gradle.task

import org.gradle.testfixtures.ProjectBuilder

public class FirebaseRemoteConfigGenTaskTest extends GroovyTestCase {

    public void testGenClasses() {
        def project = ProjectBuilder.builder().build();
        def task = (FirebaseRemoteConfigGenTask) project.task("genConofig", type: FirebaseRemoteConfigGenTask);
        task.outDirectory = new File("gen");

        def GeneratedProp = task.newConfig("com.eaglesakura.GeneratedFirebaseConfig");
        GeneratedProp.stringProperty("string_value", "nil");
        GeneratedProp.doubleProperty("double_value", 1.2345);
        GeneratedProp.doubleProperty("floatValue", 1.23f);
        GeneratedProp.longProperty("longValue", 12345);
        GeneratedProp.intProperty("intValue", 123);

        def SettingClass = task.newConfig("com.eaglesakura.db.GeneratedFirebaseConfig");
        SettingClass.booleanProperty("boolValue", false);
        SettingClass.enumProperty("enumValue", TestEnum.class.getName(), TestEnum.Hoge.name());

        task.execute();
    }
}
