"""
Build pixel_drawer CLI as a one-file console exe via PyInstaller.

Run from tools_py/pixel_drawer:

  .venv\\Scripts\\python.exe build_pd.py
"""

from __future__ import annotations

import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parent


def main() -> int:
	try:
		import PyInstaller.__main__ as pyi
	except ImportError:
		print("PyInstaller not installed. Run: pip install pyinstaller", file=sys.stderr)
		return 1

	entry = ROOT / "run_cli.py"
	dist = ROOT / "dist"
	work = ROOT / "build" / "pd"
	args = [
			"--name=pixel_drawer",
			"--onefile",
			"--console",
			"--clean",
			"--noconfirm",
			f"--paths={ROOT}",
			f"--distpath={dist}",
			f"--workpath={work}",
			f"--specpath={ROOT / 'build'}",
			"--hidden-import=pixel_drawer",
			"--hidden-import=pixel_drawer.cli",
			"--hidden-import=pixel_drawer.canvas",
			"--hidden-import=pixel_drawer.ascii_format",
			"--hidden-import=pixel_drawer.ops",
			str(entry),
	]
	print("PyInstaller", " ".join(args))
	pyi.run(args)
	print(f"OK -> {dist / 'pixel_drawer.exe'}")
	return 0


if __name__ == "__main__":
	raise SystemExit(main())
