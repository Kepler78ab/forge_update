"""Build model_builder CLI exe via PyInstaller."""

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
	args = [
		"--name=model_builder",
		"--onefile",
		"--console",
		"--clean",
		"--noconfirm",
		f"--paths={ROOT}",
		f"--distpath={ROOT / 'dist'}",
		f"--workpath={ROOT / 'build' / 'mb'}",
		f"--specpath={ROOT / 'build'}",
		"--hidden-import=model_builder",
		str(entry),
	]
	print("PyInstaller", " ".join(args))
	pyi.run(args)
	print(f"OK -> {ROOT / 'dist' / 'model_builder.exe'}")
	return 0


if __name__ == "__main__":
	raise SystemExit(main())
