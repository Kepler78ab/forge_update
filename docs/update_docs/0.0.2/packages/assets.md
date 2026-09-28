# assets（0.0.2）

**改动位置：** `assets/attack_anime_fix/`（本文件仅文档）

## PNG

| 文件 | 说明 |
|---|---|
| `forge_template_{sword_basic\|sword_long\|axe_basic\|pickaxe_basic\|shovel_basic\|hoe_basic}.png` | 整器含柄 + 羊皮纸 |
| `forge_template_overlay_{wood\|netherrack}.png` | 族角标 |
| `forged_{pickaxe\|shovel\|hoe}.png` | 锻工具图标（初版复用钢工具图） |

## 模型 / items

- `forge_template_*_*` ×12（layer0+layer1）
- `items/forge_template.json` select ×12 case
- `forged_pickaxe` / `shovel` / `hoe` models + items

## CLI

`tools_py/pixel_drawer`：`from-text` 羊皮纸 → `apply` stamp 合成。
