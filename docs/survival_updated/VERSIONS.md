# 版本管理

## 版本号规则

采用语义化版本：`MAJOR.MINOR.PATCH[-SUFFIX]`

| 段 | 何时递增 |
|---|---|
| MAJOR | 不兼容的机制大改 / 打破存档或 API |
| MINOR | 向后兼容的新机制 |
| PATCH | 修复、数值微调、文档 |
| SUFFIX | 开发中用 `SNAPSHOT`；预发布可用 `alpha.N` / `beta.N` |

与 Gradle 对齐：`gradle.properties` → `mod_version`。

## 当前版本

| 项 | 值 |
|---|---|
| 模组版本 | `0.1.0-SNAPSHOT` |
| Minecraft（本机基线） | `1.21.11` |
| Yarn | `1.21.11+build.4` |
| Fabric Loader | `0.18.4` |
| Fabric API | `0.141.3+1.21.11` |
| 阶段 | 四期 Armor & Guard 初版已进代码 |
| 远期 | 兼容 Minecraft **26.x+**（换开发机后处理） |

## 版本检查清单（发版前）

- [ ] `CHANGELOG.md` 已写本版本条目
- [ ] `VERSIONS.md`「当前版本」已更新
- [ ] `gradle.properties` 的 `mod_version` 一致
- [ ] 当前 `docs/update_docs` 开放批次已勾完或遗留已转到下一批
- [ ] 客户端 / 服务端各跑通一次
- [ ] 无未完成的实验性 Mixin 留在正式版
