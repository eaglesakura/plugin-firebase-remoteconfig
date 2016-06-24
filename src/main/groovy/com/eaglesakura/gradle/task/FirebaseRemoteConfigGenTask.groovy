package com.eaglesakura.gradle.task

import com.eaglesakura.gradle.android.firebase.remoteconfig.ConfigClassGenerator
import com.eaglesakura.tool.log.Logger
import com.eaglesakura.util.StringUtil
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * 複数の設定ファイルを生成するためのTask
 */
public class FirebaseRemoteConfigGenTask extends DefaultTask {
    def superClass = "";
    def outDirectory = new File("gen");

    List<ConfigClassGenerator> configs = new ArrayList<>();

    /**
     * 新たなプロパティを生成する
     * @param fullClassName
     * @return
     */
    public ConfigClassGenerator newConfig(String fullClassName) {
        ConfigClassGenerator result = new ConfigClassGenerator();

        int lastDotIndex = fullClassName.lastIndexOf('.');
        result.classPackageName = fullClassName.substring(0, lastDotIndex);
        result.className = fullClassName.substring(lastDotIndex + 1);

        // 一覧に登録する
        configs.add(result);

        return result;
    }

    @TaskAction
    public void generate() {
        Logger.initialize();
        Logger.outLogLevel = 0;

        for (def config : configs) {
            if (!StringUtil.isEmpty(superClass)) {
                config.superClass = superClass;
            }

            if (outDirectory != null && config.outDirectory == null) {
                config.outDirectory = outDirectory;
            }

            Logger.out "classPackageName(${config.classPackageName})"
            Logger.out "className(${config.className})"
            Logger.out "superClass(${config.superClass})"
            Logger.out "outDirectory(${config.outDirectory.absolutePath})"

            config.build();
        }
    }

}
