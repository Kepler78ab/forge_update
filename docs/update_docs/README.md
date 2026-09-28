# update_docs — 批次文档台账

本目录只做**更新追溯**：按版本建批次，可用 `packages/` 按包写说明。  
**代码 / 资源 / 长期设计仍改原位置**（`src/`、`docs/survival_updated/`），这里不放实现副本。

| 位置 | 职责 |
|---|---|
| `survival_updated/` | 长期设计、CHANGELOG、路线图 |
| `src/` / resources | 实际改动 |
| `update_docs/<ver>/` | 本批目标、TASKS、按包说明与回链 |

批次号与 `mod_version` 可不同；批次 README 写明对应 `mod_version`。

## 目录

```
docs/update_docs/
├── README.md
├── INDEX.md
└── <batch>/
    ├── README.md
    ├── TASKS.md
    └── packages/          ← 可选；按包的文档说明（不是代码目录）
        ├── survival.forge.md
        ├── assets.md
        └── …
```

## 包名对照（packages 文件名）

| 文件 | 对应 |
|---|---|
| `survival.heat` / `tools` / `forge` / `combat` / `registry` | Java 包 |
| `mixin` / `client` | Mixin / 客户端 |
| `assets` | `assets` + `data` 资源 |

未改动的包不必建空文件；在批次 README「未触及」列一行即可。

## 新开批次

1. `INDEX.md` 追加一行  
2. 新建 `<batch>/README.md` + `TASKS.md`，需要时加 `packages/*.md`  
3. 实现改原位置；此处勾任务、写包说明  
4. 收口后 INDEX 标 `done`，遗留转下一批  
