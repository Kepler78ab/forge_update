# Survival Updated — 早期工艺 / 锻造 / 格挡 架构方案

> 状态：**Accepted（2026-09-27）**  
> 实现基线：**Minecraft 1.21.11 Fabric**（当前机）；远期目标兼容 **26.x+**（换开发机后再做多版本）  
> 范围：火堆与点火、熔炉燃料门控、工具锐利度、锻造模板与件、护甲重量、盾/剑格挡与双手逻辑  
> 原则：机制优先、数据驱动、类可叠加组合；模型材质先复用原版占位  
>  
> **已锁定默认：** 火堆 H 配方；火把点火不耗火把；烟熏炉纳入点火门控；一期含钢锭骨架；包名随实现迁到 `…survival`；风险表默认值全部采用

---

## 一、需求归纳（问题 → 目标）

| 痛点 | 目标 |
|---|---|
| 木→石→铁一条直线太短 | 用**火堆 / 点火 / 燃料门控 / 锻造件**拉长早期中期节奏 |
| 熔炉即放即烧 | 所有加热装置需**点火**（或岩浆桶直燃） |
| 工具数值扁平 | **锐利度**、模板长度/柄型、重量分离攻击速度与外观 |
| 钻石生态位过强 | 钻石仅战利品；**钢**占高端合成位 |
| 战斗只有左右键 | **盾型 / 剑格挡 / 完美格挡反打 / 双手与副手规则** |

---

## 二、总体架构：五层 + 数据驱动

```
┌─────────────────────────────────────────────────────────┐
│  L5  Player Actions     长按点火 UI / 格挡窗 / 双手轮攻   │
├─────────────────────────────────────────────────────────┤
│  L4  Block Machines     火堆 / 熔炉钩子 / 高炉锻造槽      │
├─────────────────────────────────────────────────────────┤
│  L3  Item Components    锐利度 / 重量 / 武器档案 / 模板  │
├─────────────────────────────────────────────────────────┤
│  L2  Rules & Tags       燃料等级、可烧制表、护甲抗性表    │
├─────────────────────────────────────────────────────────┤
│  L1  Core Services      IgnitionService / SmeltGate /   │
│                         SharpnessService / ForgeService │
│                         GuardService / DualWieldService │
└─────────────────────────────────────────────────────────┘
         ↑ JSON / Tag / Recipe 覆盖默认值
```

**类叠加思路（组合优于继承）：**

- 物品不靠 `extends SwordItem` 堆逻辑，而靠 **Data Component**（1.21）挂属性：
  - `su:sharpness`、`su:weight`、`su:weapon_profile`、`su:armor_class`、`su:handedness`、`su:forge_piece`…
- 行为由 **Service + Mixin 钩子** 读取组件后叠加修饰（伤害、攻速、移速、可否副手）。
- 新武器类型 = 新 JSON 档案 + 已有组件组合，而不是新 Java 子类爆炸。

---

## 三、子系统拆分

### A. 点火与加热门控（Ignition & Heat）

#### A1 火堆 `FirePit`

| 项 | 设计 |
|---|---|
| 方块 | 占位：复用营火模型/材质；自定义 BlockEntity + Screen |
| 合成 | 木棍 **H 型**（`# # / # # / # #` 或你指定的精确 H） |
| UI | 类熔炉：输入 + 燃料 + 输出 |
| 燃料 Tag | `#survival_updated:fire_pit_fuel`（原木/木板/木棍/树苗/木制物品…） |
| 可烧制 | 仅食物 + 木相关产物（含木炭路径）；**不可炼矿** |
| 燃烧条件 | 物品放好 ≠ 工作；需方块处于 `LIT` |
| 点火 | 打火石 / 取火器 / 火把 **右键火堆** → `LIT=true` 后才开始消耗燃料与进度 |
| 熄灭 | 燃料耗尽或可配置雨水熄灭 → 需重新点火 |

**状态机：** `UNLIT` →（点火成功）→ `LIT` →（燃料空）→ `UNLIT`

#### A2 取火器 — **已取消**

不再实现取火器物品与长按小游戏。

#### A3 打火石

保持**原版逻辑**（营火/传送门等），不改成长按、不参与熔炉点火。

#### A4 熔炉 / 烟熏炉门控

`FurnaceBlockEntity` / `SmokerBlockEntity`：

1. UI 增加 **点燃物槽**（燃料槽左侧）：仅收 **火把 / 灵魂火把 / 岩浆桶**。
2. 槽内有合法点燃物时，才允许开始燃烧（普通燃料仍放燃料槽）。
3. 燃料耗尽熄灭后，再次启动仍需点燃物在槽内。
4. 打火石右键方块**不再**用于点燃熔炉（避免与开 UI / 放置冲突）。

#### A5 高炉

| 规则 | 说明 |
|---|---|
| 驱动燃料 | **仅岩浆桶**（煤炭/木炭/煤炭块等 `getFuelTime=0`） |
| 点燃槽 | 无（不挂点燃物槽） |
| 钢锭 | 仍走高炉；因仅岩浆可燃，等价于强制高热 |

火堆：仍可用火把右键点燃（独立 UI，不走熔炉点燃槽）。

---

### B. 工具基础与锐利度（Tools & Sharpness）

#### B1 原版工具数值补丁（数据/Mixin）

| 工具 | 调整 |
|---|---|
| 木镐/斧/锄 | 挖掘能力对齐**木剑**档（弱）；**木铲除外**保持可铲逻辑 |
| 石质工具 | 其余不变；**石剑 attackSpeed 降低** |
| 钻石工具 | **移除合成配方**；战利品表 / 结构刷 |
| 钢工具 | 新材料，占原钻石合成生态位（数值另表） |
| 绿宝石 / 红石 / 青金石工具 | 「装饰轻武器」：挖/伤≈木；攻速更快；红石/青金石需**块**合成 |

#### B2 磨刀石 `WhetstoneBlock`

| 项 | 设计 |
|---|---|
| 占位 | 复用砂轮模型/材质 |
| 合成 | 1 石块 + 1 木板 |
| 用途 | 铜及以上工具/武器：**打磨提升或恢复锐利度** |
| UI | 简化：放入工具 → 消耗时间或材料 → 写入 `su:sharpness` |

#### B3 锐利度组件 `SharpnessComponent`

```
sharpness: float        // 当前值
maxSharpness: float     // 材质上限
decayPerDamage: float   // 随耐久/攻击衰减
```

- **剑 / 通用近战**：锐利度 → 伤害倍率  
- **斧**：锐利度 → 伤害 + 挖掘速度  
- **铜+** 出厂锐利度偏低或 0，未打磨有惩罚  
- 原版 **锋利附魔**：附魔时/持有时转换为模组锐利度（或攻击时把 enchant level 折算写入），并随耐久下降；磨刀可部分恢复

**叠加顺序（伤害示例）：**

```
baseDamage
  × materialTier
  × sharpnessFactor(sharpness)
  × armorClassMultiplier(weaponProfile, targetArmorClass)   // 后期
  + enchantConvertedSharpness（若采用加法档）
```

---

### C. 锻造模板与金属件（Forge Pipeline）— 核心成长链（初版已进代码）

> 初版取舍：剑/斧各 1 模板；族 WOOD / NETHERRACK；金属铜铁钢；钢工具快捷合成暂保留。

这是最长的组合链，拆成**不可跳步**的工序：

```
[原版工作台 3×3] 用同质材料排模板（shaped 配方）
        ↓ 得到 ForgeTemplateItem（一次性，含 profile + materialFamily）
[高炉 + 模板槽 + 金属输入]
        ↓ 仅生成「燃烧中的 X 金属 · Y 质地件」BurningPiece
        （无模板 → 只能出普通锭，不能出武器件）
[右键水源/水锅冷却]
        ↓ CooledPiece（铜/铁/钢分色）
[右键工作台 → 零件]
        ↓ MetalPart（刃 / 斧头 / …）
[工作台：零件 + 木棍] → RawWeapon（未打磨）
[磨刀石] → 可用武器
```

#### C1 模板材料族 `TemplateFamily`

| Family | 可排材料（同类） | 可锻造金属 |
|---|---|---|
| `WOOD` | 任意木头 | 铜、铁…（规则表） |
| `DIRT` | 泥土类 | … |
| `CLAY` | 粘土 | … |
| `BRICK` | 红砖 | … |
| `NETHERRACK` | 下界岩 | **钢件强制要求此族** |

模板本身一次性消耗。

#### C2 模板档案 `WeaponTemplateProfile`（JSON）

```json
{
  "id": "survival_updated:long_sword",
  "handedness": "two_hand",
  "slot": "sword_long",
  "base_attack_speed": 1.2,
  "model": "long_sword",
  "guard_profile": "none",
  "weight_speed_curve": "ingot_count"
}
```

预设类型（可扩展）：

- 剑：长剑 / 短剑 / 普通剑  
- 斧：长柄斧 / 短柄斧  
- 弓 / 箭矢模板  
- （盾模板可二期）

**双手/单手跟模板绑定，不跟重量绑定。**  
双手：不可进副手；主手持双手时副手逻辑关闭。

#### C2.1 模板物品图标（美术）— **方案 B（0.0.2）**

机制上模板随 **材料族 × 武器类型** 组合；图标要能看出模板身份 + 武器类型，并尽量暗示材料族。

| 阶段 | 约定 |
|---|---|
| **0.0.2 采用** | **方案 B**：武器底图 × 武器种数 + 族叠加层 × 族数；`layer0`+`layer1` |
| **底图制作** | **整器含柄**（不去柄）：`steel_*` 经 CLI `stamp` 叠到羊皮纸；长剑可略加长刃尖。用 `tools_py/pixel_drawer` |
| **当前规模** | 档案 6：`sword_basic` / `sword_long` / `axe_basic` / `pickaxe_basic` / `shovel_basic` / `hoe_basic`；族 2 → **6 底 + 2 叠加 = 12 观感**；均有 5×5 图案与 `forged_*` 产物 |
| **暂不加** | `sword_short`；长柄/短柄斧 → **0.0.4** |
| **0.0.3** | 方块世界模型 + 护甲外观（见 `update_docs/0.0.3/DESIGN.md`） |

实现：`items/forge_template.json` 用 `select`（`custom_model_data` 字符串 `{profile}_{family}`）；`ForgeService.createTemplateStack` 写入对应键。台账：`docs/update_docs/0.0.2/`。

#### C3 重量与攻速

- 锻造时投入的锭/矿「等效原版锭数」= `metalUnits`  
- 写入 `WeightComponent.units`  
- **外观**由模板决定；**攻速**由 `metalUnits` 曲线决定（同模板，金属越多越慢）  
- 大剑模板 + 少量金属 → 长得像大剑，打得像轻剑（你提的需求）

#### C4 原版 3×3 模板配方

- 每种 `WeaponProfileId × TemplateFamily` 一张 `crafting_shaped` 数据包配方  
- 结果写入 `forge_template` 组件 + `custom_model_data`（多图标）  
- 自定义模板台方块已移除（见 `update_docs/0.0.5`）  

#### C5 高炉扩展槽

- 高炉 BE 增加 `template` 槽（或侧面输入）  
- 有模板 + 合法金属 + HIGH 燃料（钢）→ 产出 BurningPiece  
- 无模板 → 仅原版锭逻辑  

---

### D. 护甲重量档（Armor Class）— 二期细化数值

| 档 | 锻造投入 | 移速 | 防御 | 模型 |
|---|---|---|---|---|
| Light | 1× 原版用量 | 高 | 低 | 轻甲套 |
| Medium | 2× | 中 | 中 | 中甲套 |
| Heavy | 3× | 低 | 高 | 重甲套 |

组件：`ArmorClassComponent { LIGHT|MEDIUM|HEAVY, material }`  
移速：Attribute Modifier 按档叠加。  
武器对甲伤害倍率：JSON 表 `weapon_profile × armor_class`（后期填数）。

---

### E. 格挡、盾、双手（Guard & Hands）

#### E1 盾档案

| 类型 | 格挡速度 | 挡斧 | 完美格挡反打 |
|---|---|---|---|
| 轻盾（单手） | 快 | 否/弱 | 有（同剑格挡窗） |
| 大盾（原版位） | 慢 | 是 | **无** |

多材质盾 = 同类型档案 + 材质耐久/强度。

#### E2 剑格挡（短剑 / 普通剑；长剑默认无或另表）

右键进入格挡态，相对起手时间分三段：

```
|—— 完美窗 Perfect ——|—— 衰减窗 Decay ——|—— 无效 Invalid ——|
```

- Perfect：伤害全免（或近全免）+ 若同帧/短窗口内按攻击 → **Riposte** 一刀  
- Decay：伤害按时间插值减免  
- Invalid：不减伤（或硬直惩罚，待定）  
- 双剑格挡：完美窗 **×2**

轻盾共用同一套窗逻辑；大盾只有持续格挡、无反打。

#### E3 双手与双持

| 主手 | 副手 | 左键 | 右键 |
|---|---|---|---|
| 单手剑 | 空/非冲突 | 主手攻 | 剑格挡 |
| 单手剑 | 单手剑 | **主副轮流攻** | 双剑格挡 |
| 双手武器 | （禁用） | 双手攻 | 模板定义（可能无格挡） |
| 弓 | （需双手） | 拉弓仅当副手空/允许 | — |

弓改为 **双手武器**：副手占用或强制空手才能射。

---

## 四、建议包结构（Java）

```
com.example...survival          // 或未来 survival_updated
├── SurvivalUpdatedMod
├── registry/                   // 方块、物品、Screen、Recipe
├── component/                  // Sharpness, Weight, WeaponProfile, ArmorClass, Handedness, ForgePiece...
├── tag/                        // 燃料等级、火堆燃料、木制品烧制...
├── heat/
│   ├── IgnitionService
│   ├── FirePitBlock / FirePitBlockEntity / FirePitScreen
│   ├── FireStarterItem
│   └── FurnaceIgnitionMixin / BlastFuelMixin
├── forge/
│   ├── ForgeTemplateData / ForgePiece*
│   ├── ForgeService
│   └── BlastFurnaceForgeMixin
├── tool/
│   ├── SharpnessService
│   ├── WhetstoneBlock
│   └── ToolStatPatches
├── combat/
│   ├── GuardService
│   ├── GuardWindows (perfect/decay/invalid)
│   ├── DualWieldService
│   └── ShieldProfiles
└── data/                       // codec + reload listeners
```

客户端：取火器摆动条 UI、格挡窗调试叠层（可选）。

---

## 五、数据驱动文件规划

```
data/survival_updated/
  tags/item/
    fire_pit_fuel.json
    fire_pit_smeltable.json
    fuel_level_low|mid|high.json
    wooden_tool_mining_nerf.json
  recipe/
    fire_pit.json
    fire_starter.json
    whetstone.json
    ...
  su_weapon_template/*.json
  su_guard_profile/*.json
  su_armor_class/*.json
  su_weapon_vs_armor/*.json      // 后期
```

---

## 六、关键风险与需你拍板的点

| # | 问题 | 建议默认（可改） |
|---|---|---|
| 1 | 火堆 H 型具体格子？ | 木棍：两边竖列 + 底中一格（经典 H） |
| 2 | 火把点燃火堆：消耗火把吗？ | 不消耗，仅点火 |
| 3 | 取火器失败惩罚？ | 小耐久损耗，不耗物品 |
| 4 | 烟熏炉是否也要点火？ | **要**，与熔炉同一门控 |
| 5 | 铜工具未打磨能否用？ | 能用但锐利度惩罚（伤/速 -30%~50%） |
| 6 | 钻石已有世界如何处理？ | 仅禁合成；已有钻石工具保留 |
| 7 | 完美格挡是否无敌帧共享受伤冷却？ | 完美成功重置短 i-frame，防连段 |
| 8 | 模组 id / 包名是否立刻改 `survival_updated`？ | 文档已用此名；代码可随第一期实现一并改 |
| 9 | 模板排版？ | **原版 3×3 shaped**（0.0.5）；曾用 5×5 模板台已移除 |
| 10 | 一期是否包含护甲三档数值？ | 组件先挂上，数值二期 |

---

## 七、推荐实现分期（确认后按期开工）

### 一期（玩法闭环最短）— Early Heat

1. 火堆方块 + UI + 木燃料/食物木炭  
2. 打火石长按松开、取火器+摆动条  
3. 熔炉/烟熏炉/高炉点火门控 + 高炉燃料等级 + 钢锭配方骨架  

### 二期 — Tools & Sharpness（初版已进代码）

4. 木/石工具补丁、钻石禁合成、钢/装饰工具  
5. 磨刀石 + 锐利度组件 + 锋利附魔折算  

### 三期 — Forge Pipeline（初版已进代码）

6. 3×3 模板配方、模板物品、高炉模板槽  
7. 燃烧件→冷却→零件→装柄→打磨全流程  
8. 重量→攻速曲线；双手标记（数据已挂，行为属四期）  

### 四期 — Armor & Guard

9. 轻/中/重甲移速与模型档  
10. 轻盾/大盾、剑格挡窗、反打、双持轮攻、弓双手  

---

## 八、确认清单（请直接回复选项）

请确认或修改：

1. **分期**：是否按上文一→四期推进，还是要合并？  
2. **火堆 H 配方**与火把是否耗材  
3. **烟熏炉**是否纳入点火门控  
4. **包名**是否第一期就改成 `survival_updated`  
5. **钢**是否一期就做出「煤炭块/岩浆 + 高炉 → 钢锭」物品（工具可二期）  
6. 上述风险表默认值是否 OK  

确认后：把本稿标为 Accepted，再按一期拆具体类图与任务开写代码。
