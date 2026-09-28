# 批次 1.0.3 — 锻造拆包改名 `forge_craft`

| 项 | 值 |
|---|---|
| 状态 | **完成** |
| 对应 mod_version | `0.1.0-SNAPSHOT` |
| 模组 id | `forge_craft`（原 `attack_anime_fix`） |
| 工程目录 | `forge-craft/` |
| Java 包 | `com.example.forgecraft` |

## 变更

- 根目录不再持有游戏源码；根 = 多模块聚合（docs + settings）
- 锻造链整包迁入 `forge-craft`，资源命名空间 `forge_craft`
- `tool_sharpness` / `sword_guard` 软挂 id 改为 `forge_craft:forged_*`

## IDEA 启动

**和以前一样用 Loom 的 Minecraft Client，但入口换成 `forge-craft`：**

1. 打开本仓库后点 **Reload Gradle Project**（同步）
2. 等 Loom 生成运行配置
3. 运行配置下拉选 **`forge-craft: Minecraft Client`**（或 Gradle 面板 `forge-craft` → `Tasks` → `fabric` → `runClient`）
4. 绿色三角运行

**同开其它包（锋利 / 压制 / 拓展 / 格挡）：**

- 方式 A：先对各子模块执行一次 `jar`，把生成的 jar 放进 `forge-craft/run/mods/`
- 方式 B：在 IDEA 的 Fabric/Loom 运行配置里把其它模块 jar 加到 Mods 列表  
- 只测锻造时，只跑 `forge-craft` 即可

旧的根工程 `attack_anime_fix: Minecraft Client` 配置会失效，删掉或不要再用。
