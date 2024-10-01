# Beangle Tools

## 引入 tools plugin
使用sbt构建时，在project/plugins.sbt中添加

    addSbtPlugin("org.beangle.tools" % "sbt-beangle-tools" % "0.0.15")

该开发包有如下几个插件

### 1. OrmPlugin 格式检查

生成模型对应的数据库ddl。

    ormDDL

生成两个版本数据库的模型的差异ddl

    ormDdlDiff
