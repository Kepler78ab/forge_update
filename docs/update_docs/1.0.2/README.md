# 批次 1.0.2 — 锋利独立包 `tool_sharpness`

| 项 | 值 |
|---|---|
| 状态 | **完成** |
| 对应 mod_version | `0.1.0-SNAPSHOT` |
| 模组 id | `tool_sharpness` |
| 强制 | **否**（可选） |

## 范围

- 锐利度组件 `tool_sharpness:sharpness`
- 磨刀石方块 + 合成
- 原版工具出厂锐利（木石金满；铜+偏钝）
- 木镐/斧/锄挖掘对齐木剑；石剑攻速下调
- 软挂：`weapon_expansion` / `forge_craft` 锻具 id（无硬依赖）
- Mixin：近战伤倍率、斧挖掘倍率、击打衰减

## 与锻造

- 主包已移除锐利度/磨刀石
- 装上本包时，铸造成品经反射 `SharpnessApi.applyDull` 写入硬度对应锐利上限（无硬依赖）

见 [`packages/tool_sharpness.md`](packages/tool_sharpness.md)。
