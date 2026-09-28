"""
model_builder — generate simple Minecraft item/block JSON models for this mod.

Commands:
  shield   Generate a box-based shield item model (square / large variants)
  tabletop Generate a 16x16 crafting-table-like top PNG with an N×N grid

Examples:
  python -m model_builder shield --style square -o out/light_shield.json
  python -m model_builder shield --style large -o out/heavy_shield.json
  python -m model_builder tabletop --cells 5 -o out/template_bench_top.png
"""

from __future__ import annotations

import argparse
import json
import sys
from pathlib import Path

try:
	from PIL import Image
except ImportError:
	Image = None  # type: ignore


def gen_shield_model(*, width: float, height: float, thickness: float, texture: str) -> dict:
	"""Axis-aligned shield plate centered on Y, thin on Z (held like a board)."""
	# Coordinates in 0..16 block space; plate centered at (8,8)
	x0 = 8.0 - width / 2.0
	x1 = 8.0 + width / 2.0
	y0 = 8.0 - height / 2.0
	y1 = 8.0 + height / 2.0
	z0 = 8.0 - thickness / 2.0
	z1 = 8.0 + thickness / 2.0
	return {
		"credit": "model_builder shield",
		"textures": {
			"particle": texture,
			"shield": texture,
		},
		"elements": [
			{
				"from": [x0, y0, z0],
				"to": [x1, y1, z1],
				"faces": {
					"north": {"uv": [0, 0, 16, 16], "texture": "#shield"},
					"south": {"uv": [0, 0, 16, 16], "texture": "#shield"},
					"east": {"uv": [0, 0, 2, 16], "texture": "#shield"},
					"west": {"uv": [0, 0, 2, 16], "texture": "#shield"},
					"up": {"uv": [0, 0, 16, 2], "texture": "#shield"},
					"down": {"uv": [0, 0, 16, 2], "texture": "#shield"},
				},
			}
		],
		"display": {
			"thirdperson_righthand": {
				"rotation": [0, 90, 0],
				"translation": [2, 0, 0],
				"scale": [0.55, 0.55, 0.55],
			},
			"thirdperson_lefthand": {
				"rotation": [0, 90, 0],
				"translation": [2, 0, 0],
				"scale": [0.55, 0.55, 0.55],
			},
			"firstperson_righthand": {
				"rotation": [0, 180, 5],
				"translation": [-2, 1, -4],
				"scale": [0.7, 0.7, 0.7],
			},
			"firstperson_lefthand": {
				"rotation": [0, 180, 5],
				"translation": [-2, 1, -4],
				"scale": [0.7, 0.7, 0.7],
			},
			"gui": {
				"rotation": [15, -25, -5],
				"translation": [0, 0, 0],
				"scale": [0.75, 0.75, 0.75],
			},
			"ground": {
				"translation": [0, 2, 0],
				"scale": [0.35, 0.35, 0.35],
			},
			"fixed": {
				"rotation": [0, 180, 0],
				"scale": [0.7, 0.7, 0.7],
			},
		},
	}


def cmd_shield(args: argparse.Namespace) -> int:
	if args.style == "square":
		# nearly square plate (light shield)
		w, h, t = 12.0, 12.0, 1.0
		tex = args.texture or "attack_anime_fix:item/light_shield_face"
	elif args.style == "large":
		# larger than vanilla-ish board (heavy shield)
		w, h, t = 14.0, 16.0, 1.25
		tex = args.texture or "attack_anime_fix:item/heavy_shield_face"
	else:
		w, h, t = args.width, args.height, args.thickness
		tex = args.texture or "attack_anime_fix:item/light_shield_face"

	doc = gen_shield_model(width=w, height=h, thickness=t, texture=tex)
	out = Path(args.output)
	out.parent.mkdir(parents=True, exist_ok=True)
	out.write_text(json.dumps(doc, indent=2) + "\n", encoding="utf-8")
	print(f"wrote {out} ({w}x{h}x{t})")
	return 0


def cmd_tabletop(args: argparse.Namespace) -> int:
	if Image is None:
		print("Pillow required: pip install pillow", file=sys.stderr)
		return 1
	cells = int(args.cells)
	size = 16
	img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
	px = img.load()
	# wood frame colors
	border = (101, 67, 33, 255)
	plank = (140, 98, 50, 255)
	slot = (40, 28, 18, 255)
	line = (70, 48, 24, 255)
	for y in range(size):
		for x in range(size):
			if x == 0 or y == 0 or x == size - 1 or y == size - 1:
				px[x, y] = border
			else:
				px[x, y] = plank
	# grid inset 2..13 → 12px for cells
	inner0, inner1 = 2, 14
	span = inner1 - inner0
	# draw cell separators
	for i in range(cells + 1):
		# vertical / horizontal lines at fractional positions
		t = inner0 + int(round(i * span / cells))
		t = min(max(t, inner0), inner1 - 1)
		for y in range(inner0, inner1):
			px[t, y] = line
		for x in range(inner0, inner1):
			px[x, t] = line
	# darken cell centers slightly
	cell = span / cells
	for cy in range(cells):
		for cx in range(cells):
			x0 = inner0 + int(cx * cell) + 1
			y0 = inner0 + int(cy * cell) + 1
			x1 = inner0 + int((cx + 1) * cell)
			y1 = inner0 + int((cy + 1) * cell)
			for y in range(y0, min(y1, inner1)):
				for x in range(x0, min(x1, inner1)):
					if px[x, y] == plank:
						px[x, y] = slot
	out = Path(args.output)
	out.parent.mkdir(parents=True, exist_ok=True)
	img.save(out)
	print(f"wrote {out} ({cells}x{cells} grid)")
	return 0


def build_parser() -> argparse.ArgumentParser:
	p = argparse.ArgumentParser(prog="model_builder")
	sub = p.add_subparsers(dest="cmd", required=True)

	s = sub.add_parser("shield", help="Generate box shield item model JSON")
	s.add_argument("--style", choices=("square", "large", "custom"), default="square")
	s.add_argument("--width", type=float, default=12.0)
	s.add_argument("--height", type=float, default=12.0)
	s.add_argument("--thickness", type=float, default=1.0)
	s.add_argument("--texture", default="")
	s.add_argument("-o", "--output", required=True)
	s.set_defaults(func=cmd_shield)

	t = sub.add_parser("tabletop", help="Generate NxN grid tabletop PNG")
	t.add_argument("--cells", type=int, default=5)
	t.add_argument("-o", "--output", required=True)
	t.set_defaults(func=cmd_tabletop)

	return p


def main(argv: list[str] | None = None) -> int:
	parser = build_parser()
	args = parser.parse_args(argv)
	return int(args.func(args))


if __name__ == "__main__":
	raise SystemExit(main())
