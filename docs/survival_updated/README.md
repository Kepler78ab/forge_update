# Survival Updated

机制向生存更新模组的**文档与版本管理根目录**。  
之后的设计说明、版本记录、变更日志都放在本目录，不再散落在 `docs/` 根下。

## 目录约定

```
docs/survival_updated/
├── README.md           ← 本文件：总览与约定
├── VERSIONS.md         ← 版本号规则 + 当前目标版本
├── CHANGELOG.md        ← 按版本记录的变更日志
├── roadmap.md          ← 机制点子 / 路线图
└── features/           ← 各机制的设计文档（按主题拆分）
    └── .gitkeep
```

## 工作流

1. **先写文档，再写代码**：新机制先在 `features/` 或 `roadmap.md` 立项，确认后再实现。
2. **版本只在本目录维护**：发版时同时改 `VERSIONS.md` 与 `CHANGELOG.md`，并与 `gradle.properties` 的 `mod_version` 对齐。
3. **不回写废弃方案**：已放弃的攻击动画模组不再维护；历史若需备忘可写在 `CHANGELOG.md` 的 Abandoned 段。

## 当前代码状态

工程已清空攻击相关实现，仅保留可启动的 Fabric 空壳。  
模组 Gradle / Java 包名仍为仓库原名，后续若整体重命名为 `survival_updated` 再单独改。
