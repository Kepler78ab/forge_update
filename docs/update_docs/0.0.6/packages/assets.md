# assets（0.0.6）

## PNG

`forge_piece_{burning|cooled|part}_{sword_basic|sword_long|axe_basic|pickaxe_basic|shovel_basic|hoe_basic}.png` ×18

生成：`tools_py/pixel_drawer/scripts/gen_forge_piece_profiles.py`  
剪掉木柄 / 羊皮纸，只留刃/头铸造件剪影，再按 stage 重着色。

## 模型 / items

- `models/item/forge_piece_*_*.json` ×18
- `items/forge_piece.json` select ×18 case；fallback 旧圆饼
