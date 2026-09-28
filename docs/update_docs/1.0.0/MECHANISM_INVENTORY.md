# 1.0.0 机制盘点（现状快照）

> 基线：Fabric **1.21.11** · 命名空间暂仍 `attack_anime_form` · Java 包 `…/survival/`  
> 图例：**代码** = 已进仓库；**文档** = 仅设计；**部分** = 有代码但不完整 / 占位

---

## 〇、总览

| 大系统 | 成熟度 | 一句话 |
|---|---|---|
| Early Heat（火堆 / 点火 / 高炉燃料） | 代码 | 早期热源与熔炉门控 |
| Tools & Sharpness（锐利度 / 磨刀石 / 原版补丁） | 代码 | 工具耐用打磨层 |
| Forge Pipeline（模板→件→装柄） | 代码 | 锻造成长链（将大改） |
| Armor & Guard（甲档 / 格挡 / 双持 / 盾） | 代码 | 近战防御与双手规则 |
| 装饰 / 钢工具 | 代码 | 钢 + 绿宝石/红石/青金石套 |
| 长短斧 / 短剑 / 弓模板等 | 文档 | 未做或已排期未写 |

---

## 一、Early Heat

| 机制 | 状态 | 关键 ID / 类 | 用途 |
|---|---|---|---|
| 火堆 | 代码 | `fire_pit` · `FirePit*` | 早期「炉」：木燃料 + 食物/木制品；需点燃 |
| 火把点火堆 | 代码 | `IgnitionService` | 火把右键点燃，不消耗 |
| 熔炉/烟熏炉点燃物槽 | 代码 | `Igniter*` · `AbstractFurnace*Mixin` | 无点燃物不开始烧 |
| 高炉仅岩浆桶燃料 | 代码 | `BlastFurnaceBlockEntityMixin` | 高热门槛 |
| 燃料等级 tag | 代码 | `fuel_low/mid/high` 等 | 数据驱动热档 |
| 钢锭骨架 | 代码 | `steel_ingot` · 高炉配方 | 钢材料入口 |
| 取火器 / 打火石长按 | 文档（已取消） | — | 不再做 |
| 模板族 DIRT/CLAY/BRICK | 文档 | features §C1 | 代码仅 WOOD / NETHERRACK |

---

## 二、Tools & Sharpness

| 机制 | 状态 | 关键 ID / 类 | 用途 |
|---|---|---|---|
| 锐利度组件 | 代码 | `sharpness` · `SharpnessData/Service` | 伤/斧挖倍率；衰减；锋利附魔折算 |
| 磨刀石 | 代码 | `whetstone` | 右键恢复锐利度 |
| 原版工具补丁 | 代码 | `ToolPatches` | 木镐斧锄挖对齐木剑；石剑攻速；铜+出厂偏钝 |
| 钻石合成禁用 | 代码 | 覆盖 `minecraft:recipe/diamond_*` | 仅禁合成 |
| 出厂锐利度挂原版工具 | 代码 | `ToolPatches.applyFactorySharpness` | 木石金满；铜→下界偏钝 |

---

## 三、Forge Pipeline（锻造）

**现行链路（代码）：**  
工作台 3×3 模板 → 高炉模板槽+金属 → 燃烧件 → 水/水锅冷却 → 右键工作台→零件 → **零件+木棍工作台** → `forged_*`（偏钝）→ 磨刀石。

| 机制 | 状态 | 关键 ID / 类 | 用途 |
|---|---|---|---|
| 武器档案 | 代码 | `sword_basic`(单手) · `sword_long`(双手) · `axe_basic` · `pickaxe_basic` · `shovel_basic` · `hoe_basic` | 外形 + 手性 |
| 模板族 | 代码 | `wood` / `netherrack` | 钢强制下界岩族 |
| 金属 | 代码 | `copper` / `iron` / `steel` | 投入与锐利上限 |
| 件阶段 | 代码 | `burning` / `cooled` / `part` | 锻造件生命周期 |
| 锻造模板 | 代码 | `forge_template` · 12 张 shaped | 3×3 排模板（0.0.5） |
| 锻造件 | 代码 | `forge_piece` · 冷却/拆解交互 | 铸造件外观（0.0.6–0.0.7） |
| 高炉模板槽 | 代码 | `ForgeTemplateSlot` 等 | 有模板出燃烧件，无则普通锭 |
| 装柄配方 | 代码 | `haft_weapon` special | 零件 + 木棍 |
| 重量→攻速 | 代码 | `WeightData` | 金属量越多越慢 |
| 锻打成品 | 代码 | `forged_sword/axe/pickaxe/shovel/hoe` | 装柄产物；仅 `sword_basic` 挂剑格挡 |
| 钢工具快捷合成 | 代码（并行） | `steel_*` shaped | 绕过锻造，便于测 |
| `sword_short` | 文档 | — | 明确延后 |
| `axe_short` / `axe_long` | 文档 | 批次 0.0.4 | **未写代码**；新方向建议砍掉 |
| 弓/箭模板 | 文档 | — | 未做 |
| 5×5 模板台 | 已移除 | 0.0.5 | — |
| 数据驱动档案加载 | 部分 | `ModForge` 占位 | 现为枚举硬编码 |

---

## 四、Armor & Guard

| 机制 | 状态 | 关键 ID / 类 | 用途 |
|---|---|---|---|
| 护甲重量档 | 代码 | `armor_class` LIGHT/MEDIUM/HEAVY | 移速惩罚叠加 |
| 剑格挡窗 | 代码 | `GuardProfile.SWORD` · `GuardWindows` · `GuardService` | 完美/衰减/无效；完美→反打窗 |
| 轻盾 | 代码 | `light_shield` | 快举 + 可反打 |
| 重盾 | 代码（原版载体） | `Items.SHIELD` + `HEAVY_SHIELD` | 持续格挡、无定时反打 |
| 双持轮攻 | 代码 | `DualWieldService` | 双单手剑交替 |
| 双持完美窗 ×2 | 代码 | `GuardWindows` | 双剑格挡时完美更长 |
| 双手清副手 | 代码 | `HandednessService` · `sword_long` | 持双手近战清空副手 |
| 弓弩需对侧空手 | 代码 | `Bow/CrossbowItemMixin` | 副手有物不能拉 |
| 武器对甲倍率表 | 文档 | — | 未做 |
| 重甲独立 3D | 部分 | 贴图露脸已做 | 几何 backlog |
| 长剑格挡 | 部分 | 装柄未给 `sword_long` 挂 guard | 与剑格挡耦合点 |

---

## 五、装饰 / 钢 / 材料工具（偏「武器拓展」）

| 机制 | 状态 | 关键 ID | 用途 |
|---|---|---|---|
| 钢工具套 | 代码 | `steel_*` · 材料 STEEL（钻石位） | 合成钢工具；出厂偏钝 |
| 绿宝石工具 | 代码 | `emerald_*` | 装饰轻工具；配方已是**单颗**绿宝石 |
| 红石工具 | 代码 | `redstone_*` | 用红石**块**配方（符合你的「其余用块」） |
| 青金石工具 | 代码 | `lapis_*` | 用青金石**块**配方 |
| 黑曜石工具 | **无** | — | 新需求，待 2.1 |

---

## 六、资源与工程

| 项 | 状态 |
|---|---|
| 创造栏：模板/锻造件变体、盾、三档甲、钢与装饰工具 | 代码 |
| 中英语言 | 代码 |
| 数据组件 | `sharpness` `forge_template` `forge_piece` `weapon_profile` `weight` `armor_class` `guard_profile` |
| Mixin | 熔炉点燃/模板槽、高炉岩浆、锐利/反打、双持、弓弩 |
| `tools_py/pixel_drawer` · `model_builder` | 资源生成工具，可随锻造/美术复用 |

---

## 七、开放批次残留（改方向前）

| 批次 | 状态 | 备注 |
|---|---|---|
| 0.0.1 | open | 验收/台账 |
| 0.0.3 | open | 材质 QA |
| 0.0.4 | open | 长短斧 — **建议随 1.0.0 方向取消** |
| 0.0.2/5/6/7 | done | 可作锻造美术与流程参考 |
