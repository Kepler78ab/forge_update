# 0.0.5：锻造模板改为原版 3×3 工作台

> 状态：**已实现（T1 + A）**  
> 动机：不用自定义 5×5 模板台；用原版工作台 3×3 合成锻造模板。

---

## 一、目标

| 项 | 现况（改前） | 目标（已落地） |
|---|---|---|
| 排模板 | `TemplateBench` 5×5 | **原版工作台 3×3 shaped 配方** |
| 材料族 | WOOD / NETHERRACK | 仍同族；tag 不变 |
| 模板台方块 | 5×5 UI | **已移除** |

---

## 二、方案（已选）

**T1 + A**：每种 `WeaponProfileId × TemplateFamily` 一张 `crafting_shaped` 数据包配方；结果带 `forge_template` 组件 + `custom_model_data`；删除 `TemplateBench` 全栈。

创造栏仍用 `ForgeService.createTemplateStack` 列出全部变体。

---

## 三、3×3 图案

| 档案 | 3×3（`W` = 材料 tag） |
|---|---|
| `sword_basic` | 左列三格 |
| `sword_long` | 中列三格 + 底行加宽 |
| `axe_basic` | 顶/中 `WW.`，底 `.W.` |
| `pickaxe_basic` | 顶 `WWW`，中/底 `.W.` |
| `shovel_basic` | 中列三格 |
| `hoe_basic` | 顶 `WW.`，中/底 `.W.` |

材料 tag：`template_family_wood` / `template_family_netherrack`。

配方路径：`data/attack_anime_fix/recipe/forge_template_<profile>_<family>.json`（共 12 张）。
