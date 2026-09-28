# F0 废弃清单与约定

## 已执行

| 项 | 处理 |
|---|---|
| 火堆 `fire_pit` | **代码与资源移除**（F0） |
| 单位 | **升**；内容物上限 **64 升** |
| 熔炼高炉开关 | 方块属性 `OPEN` **或** 红石供电均可漏出（实现时两者皆可） |
| 燃烧武器 | F6 复用 `forge_piece` 燃烧阶段：可用、火焰附加 I、低伤；冷却→成品 |
| 旧 3×3 `forge_template` + 高炉模板槽 + `haft_weapon` | 模板配方 **F5 已换新**；高炉模板槽 **F6 已移除**；`haft_weapon` 仍保留给旧零件 |
| 锐利度 + 磨刀石 | **已迁出** → 独立包 `tool_sharpness`（1.0.2） |
| 遇水冷却 | **保留**（确认项） |
| Early Heat 火堆相关 tag | 随火堆删除或闲置；熔炉点燃槽逻辑仍保留供原版炉 |

## 仍属 forge_craft、待 F2+ 替换

- `forge_template` / `forge_piece` / 高炉模板槽 / 旧装柄  
- 锐利度 + 磨刀石（锋利包下一批前暂留锻造包）

## 参照

- [`熔炼系统拓展.md`](../../../熔炼系统拓展.md)  
- [`DESIGN_forge_redesign.md`](DESIGN_forge_redesign.md)
