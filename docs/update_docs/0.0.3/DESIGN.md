# 0.0.3 设计：材质与模型

> 状态：**已确认并实现（扩展）**  
> 工具：`tools_py/pixel_drawer`（贴图）+ **`tools_py/model_builder`**（盾 JSON / 5×5 台面；先 build 再改）

---

## 本批内容

### 方块

| 方块 | 做法 |
|---|---|
| 火堆 | 营火几何 + 深色贴图 |
| 磨刀石 | 砂轮几何 + 黑石轮 |
| **模板台** | 工作台几何 + **5×5 台面格**贴图（`model_builder tabletop`） |

### 盾

| 盾 | 做法 |
|---|---|
| 轻盾 / 重盾 | **继续用原版盾 3D 作占位**（`special` shield 模型）；自定义方/大盾暂缓 |

### 盔甲

| 档 | 物品栏 | 穿戴 |
|---|---|---|
| 中甲（铁等） | 覆盖 `assets/minecraft/textures/item/iron_*` 偏冷色 | `humanoid/iron.png` 轻微染色 |
| 重甲（钻/下） | 覆盖 `diamond_*` 物品栏贴图 | `humanoid/diamond|netherite.png` **补脸窗 + 加强袖臂覆盖** |

### 创造栏

- 全部锻造模板变体 + 轻/重盾 + 中甲/重甲原版盔甲套  
- 规则：`.cursor/rules/creative-tab.mdc`（新物品必须登记）

### 不做 / 顺延

- 更精细的独立盔甲几何（非贴图补面）可后续再开  
- 长短柄斧 → 0.0.4
