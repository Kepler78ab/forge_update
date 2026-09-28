# pixel_drawer — AI / CLI / GUI pixel PNG tool

Deterministic RGBA pixel editor for Minecraft-style textures. No anti-aliasing.

## Setup

```bat
cd tools_py\pixel_drawer
.venv\Scripts\python.exe -m pip install -r requirements.txt
```

PyInstaller (if needed):

```bat
.venv\Scripts\python.exe -m pip install pyinstaller
```

## CLI

```bat
cd tools_py\pixel_drawer
.venv\Scripts\python.exe -m pixel_drawer <command> ...
```

| Command | Purpose |
|---|---|
| `new` | Blank PNG (`--size 16x16`, `--color`, `-o`) |
| `inspect` | PNG → ASCII or JSON palette+grid |
| `from-text` | ASCII art file → PNG |
| `apply` | Ops JSON → PNG |
| `set` | One pixel `--xy` or `--rect` |
| `preview` | Nearest-neighbor upscale (eyes only) |

Coordinates: origin top-left `(0,0)`, x right, y down.

## GUI (manual edit)

Same core as CLI (`Canvas` / ASCII / ops):

```bat
cd tools_py\pixel_drawer
.venv\Scripts\python.exe -m pixel_drawer.gui
```

Features: pencil / eraser / rect / eyedropper / replace, zoom, open/save PNG, import/export ASCII, apply ops JSON panel.

## Build EXE (PyInstaller)

```bat
cd tools_py\pixel_drawer
.venv\Scripts\python.exe build_pd.py
.venv\Scripts\python.exe build_pd_gui.py
```

Outputs:

- `dist\pixel_drawer.exe` — console CLI
- `dist\pixel_drawer_gui.exe` — windowed GUI

## AI workflow

1. `inspect` existing PNG if editing.
2. Author `*.txt` / `*.json`, or paint in GUI.
3. Write into `src/main/resources/assets/attack_anime_fix/textures/item|block/....png`
4. Confirm with Cursor `Read` or GUI / `preview`.

## Examples

```bat
cd tools_py\pixel_drawer
.venv\Scripts\python.exe -m pixel_drawer from-text -i examples\steel_ingot.txt -o out\steel_ingot.png
.venv\Scripts\python.exe -m pixel_drawer apply --ops examples\ops_recolor.json -o out\recolor.png
```
