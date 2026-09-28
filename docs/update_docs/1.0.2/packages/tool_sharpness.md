# tool_sharpness

| 项 | 说明 |
|---|---|
| 强制 | 否 |
| 组件 | `tool_sharpness:sharpness` |
| 磨刀石 | 石+木板；右键磨至满锐利 |
| 出厂 | 木石金=满；铜铁钻下界=钝 |
| 软挂 | `weapon_expansion:*`、`forge_craft:forged_*`（按 id，无 depends） |
| API | `SharpnessApi.applyDull/applyFull`（锻造运行时反射挂钩） |

不装则工具无锐利倍率与衰减；锻造链仍可玩。
