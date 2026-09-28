# Roadmap — Survival Updated

机制向更新：优先改**规则与成长曲线**，不做重动画战斗模组。

基线：**1.21.11 Fabric** · 远期：**26.x+**

## 原则

- 一条机制解决一个生存痛点或成长空窗
- 尽量数据驱动（JSON / tag），少硬编码
- 先文档立项 → 再实现 → 再记入 CHANGELOG

## 当前主线（Accepted）

详见：[`features/early_craft_forge_guard.md`](features/early_craft_forge_guard.md)

- [x] **一期** Early Heat（初版已进代码）
- [x] **二期** Tools & Sharpness（初版已进代码）
- [x] **三期** Forge Pipeline（初版已进代码）
- [x] **四期** Armor & Guard（初版已进代码）

## 美术 / 贴图 backlog

- [x] 锻造模板多图标方案 B → **`update_docs/0.0.2` done**
- [ ] 0.0.3：火堆/磨刀石换材质；盾占位；中重甲贴图；创造栏（进行中，待验收）
- [ ] 长柄斧 / 短柄斧 → **`update_docs/0.0.4`**
- [x] 模板改原版 3×3 工作台、移除模板台 → **`update_docs/0.0.5` done**
- [x] 锻造件外观按模板档案 → **`update_docs/0.0.6` done**
- [x] 零件+木棍工作台装柄；冷却/零件金属分色 → **`update_docs/0.0.7` done**
- [ ] **1.0.0**：机制盘点 + 抽象化/拆 3 模组方向（**文档待确认**）→ [`update_docs/1.0.0`](../update_docs/1.0.0/)
- [ ] 重甲独立 3D 装备模型（贴图罩脸已做一版，几何可再开）
- [ ] ~~长柄斧 / 短柄斧（0.0.4）~~ — 方向变更，待 1.0.0 确认是否取消

## 明确不做

- Better Combat 类近战动画与攻击状态机重做
