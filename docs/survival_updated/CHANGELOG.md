# Changelog

本文件记录 **Survival Updated** 方向的可见变更。格式参考 [Keep a Changelog](https://keepachangelog.com/)。

---

## [Unreleased]

### Added

- 架构方案 Accepted：`features/early_craft_forge_guard.md`
- **一期 Early Heat（初版）**
  - 火堆（营火占位模型）、H 型木棍合成、类熔炉 UI（木燃料 / 食物与木制品）；火把右键点燃火堆
  - 熔炉 / 烟熏炉：点燃物槽（火把或岩浆桶）+ 燃料槽；无点燃物不开始燃烧
  - 高炉：仅岩浆桶可作燃料；钢锭高炉配方骨架（生铁 → 钢锭）
  - 打火石保持原版；取火器已移除

- **二期 Tools & Sharpness（初版）**
  - `su:sharpness` 数据组件 + 伤害/斧挖掘倍率；铜+出厂偏钝，磨刀石右键恢复
  - 锋利附魔折算为额外倍率；tooltip 显示锐利度
  - 木镐/斧/锄挖掘对齐木剑；石剑攻速下调；钻石工具合成禁用（空 tag 配方）
  - 钢工具（占钻石合成位）+ 绿宝石/红石块/青金石块装饰轻工具骨架
- 基线 MC **1.21.11**；远期目标 **26.x+**

### Changed

- 点火方案调整：取消打火石长按 / 取火器；熔炉·烟熏炉改点燃物槽；高炉仅岩浆桶可燃

### Planned

- 三期～四期：见 `roadmap.md`

---

## [0.1.0-SNAPSHOT] — 2026-09-27

### Changed

- 工程重置为空壳：移除全部攻击状态机、程序化动画、相关 Mixin 与 `weapon_attack` 数据
- 文档迁移至 `docs/survival_updated/`，此后设计与版本管理以此为准

### Removed

- 近战前摇 / 后摇 / 冷却替换方案（原 attack animation 方向）
- `docs/design.md`（攻击重构草案，已废弃）

---

## Abandoned — Attack Animation Fix

曾尝试 Better Combat 风格的攻击阶段 + 动画绑定，因投入产出比低而中止。不再继续该方向。
