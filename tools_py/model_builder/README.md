# model_builder

生成简易 MC 物品/方块相关 JSON 与台面贴图，供盾、模板台等使用。

## Setup

```bat
cd tools_py\model_builder
python -m venv .venv
.venv\Scripts\pip install -r requirements.txt
```

## CLI

```bat
.venv\Scripts\python.exe -m model_builder shield --style square -o ..\..\src\main\resources\assets\attack_anime_fix\models\item\light_shield.json
.venv\Scripts\python.exe -m model_builder shield --style large -o ..\..\src\main\resources\assets\attack_anime_fix\models\item\heavy_shield.json
.venv\Scripts\python.exe -m model_builder tabletop --cells 5 -o out\template_bench_top.png
```

## Build exe

```bat
.venv\Scripts\python.exe build_mb.py
```

输出：`dist\model_builder.exe`
