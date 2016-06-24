package com.eaglesakura.gradle.android.firebase.remoteconfig

import com.eaglesakura.tool.generator.CodeWriter
import com.eaglesakura.util.StringUtil

public class ConfigClassGenerator {
    def classPackageName = "com.example";
    def className = "SampleConfigClass";
    def superClass = ""
    File outDirectory = null;

    /**
     * 頭の１文字目を大文字にする
     */
    static String toCamelCase(String base) {
        CNV_SNAKE:
        // スネークケースをキャメルケースに変換する
        {
            def split = base.split("_");
            if (split.length > 1) {
                String temp = "";
                for (def item : split) {
                    temp += "${item.substring(0, 1).toUpperCase()}${item.substring(1)}";
                }

                base = temp;
            }
        }

        return "${base.substring(0, 1).toUpperCase()}${base.substring(1)}";
    }

    /**
     * 保持しているプロパティ一覧
     */
    private List<Property> properties = new ArrayList<Property>();

    public AndroidPropGenTask() {
    }

    public void floatProperty(String propName, float propDefaultValue) {
        properties.add(new Property("${propName}", propName, propDefaultValue) {
            @Override
            String generateGetter() {
                return "public float get${toCamelCase(name)}(){ return (float)mRemoteConfig.getDouble(\"${key}\"); }";
            }
        })
    }

    public void doubleProperty(String propName, double propDefaultValue) {
        properties.add(new Property("${propName}", propName, propDefaultValue) {
            @Override
            String generateGetter() {
                return "public double get${toCamelCase(name)}(){ return mRemoteConfig.getDouble(\"${key}\"); }";
            }
        })
    }

    public void booleanProperty(String propName, boolean propDefaultValue) {
        properties.add(new Property("${propName}", propName, propDefaultValue) {
            @Override
            String generateGetter() {
                return "public boolean is${toCamelCase(name)}(){ return mRemoteConfig.getBoolean(\"${key}\"); }";
            }
        })
    }

    public void intProperty(String propName, int propDefaultValue) {
        properties.add(new Property("${propName}", propName, propDefaultValue) {
            @Override
            String generateGetter() {
                return "public int get${toCamelCase(name)}(){ return (int)mRemoteConfig.getLong(\"${key}\"); }";
            }
        })
    }

    public void longProperty(String propName, long propDefaultValue) {
        properties.add(new Property("${propName}", propName, propDefaultValue) {
            @Override
            String generateGetter() {
                return "public long get${toCamelCase(name)}(){ return mRemoteConfig.getLong(\"${key}\"); }";
            }
        })
    }

    public void stringProperty(String propName, String propDefaultValue) {
        properties.add(new Property("${propName}", propName, propDefaultValue) {
            @Override
            String generateGetter() {
                return "public String get${toCamelCase(name)}(){ return mRemoteConfig.getString(\"${key}\"); }";
            }
        })
    }

    /**
     * enum型のPropertiesを生成する
     * @param propName
     * @param enumFullName
     * @param propDefaultValue
     */
    public void enumProperty(String propName, final String enumFullName, String propDefaultValue) {
        properties.add(new Property("${propName}", propName, propDefaultValue) {
            @Override
            String generateGetter() {
                return "public ${enumFullName} get${toCamelCase(name)}(){ try{ return ${enumFullName}.valueOf(mRemoteConfig.getString(\"${key}\")); }catch(Exception e){ return null; } }";
            }
        })
    }

    public void build() {

        File srcRootDirectory = outDirectory;

        FILE_CHECK:
        {
            // 規定の経路を生成する
            String[] dirs = classPackageName.split("\\.");
            for (String s : dirs) {
                srcRootDirectory = new File(srcRootDirectory, s);
            }
            srcRootDirectory.mkdirs();

            // ファイル名指定
            srcRootDirectory = new File(srcRootDirectory, "${className}.java");
        }

        CodeWriter writer = new CodeWriter(srcRootDirectory);

        // packagename
        writer.writeLine("package ${classPackageName};").newLine();

        // import
        writer.writeLine("import com.google.firebase.remoteconfig.FirebaseRemoteConfig;")
        writer.writeLine("import java.util.HashMap;")
        writer.newLine();

        // class name
        if (StringUtil.isEmpty(superClass)) {
            writer.writeLine("public class ${className} {").pushIndent(true);
        } else {
            writer.writeLine("public class ${className} extends ${superClass} {").pushIndent(true);
        }

        // プロパティIDを出力
        PROP_ID:
        {
            writer.newLine();

            // Firebase
            writer.writeLine("final FirebaseRemoteConfig mRemoteConfig = FirebaseRemoteConfig.getInstance();");
            writer.newLine();

            for (def prop : properties) {
                writer.writeLine("public static final String ID_${prop.name.toUpperCase()} = \"${prop.key}\";");
            }
            writer.newLine();
        }

        // コンストラクタと初期化
        INIT:
        {
            writer.writeLine("public ${className}(){").newLine().pushIndent(true);

            // 格納先を用意する
            writer.newLine();
            writer.writeLine("HashMap<String, Object> defValues = new HashMap<>();")

            // Propertiesを出力する
            for (Property prop : properties) {
                writer.writeLine("defValues.put(\"${prop.key}\", \"${prop.defaultValue}\");");
            }

            // Firebaseへ渡す
            writer.writeLine("mRemoteConfig.setDefaults(defValues);")

            // メソッドを閉じる
            writer.popIndent(true).writeLine("}");
        }

        // アクセサメソッドを生成する
        Accr:
        {
            for (Property prop : properties) {
                writer.writeLine(prop.generateGetter());
            }
        }
        writer.popIndent(true).writeLine("}");

        // 生成完了
        writer.commit();
    }

    /**
     * 設定項目を指定する
     */
    static abstract class Property {
        final String defaultValue;

        final String key;

        final String name;

        Property(String key, String name, String defaultValue) {
            this.key = key;
            this.name = name;
            this.defaultValue = defaultValue;
        }

        /**
         * getter用コードを生成する
         */
        abstract String generateGetter();
    }
}