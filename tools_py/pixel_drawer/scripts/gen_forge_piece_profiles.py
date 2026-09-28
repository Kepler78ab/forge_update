"""Generate forge_piece textures: burning_<profile>; {cooled|part}_<profile>_<metal>."""

from __future__ import annotations

import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(ROOT))

from pixel_drawer.canvas import Canvas, Color  # noqa: E402

REPO = ROOT.parents[1]
TEX = REPO / "src/main/resources/assets/attack_anime_fix/textures/item"

PARCHMENT = {
	(0xF0, 0xE0, 0xC0, 0xFF),
	(0xC9, 0xA6, 0x6A, 0xFF),
	(0xA6, 0x7C, 0x4A, 0xFF),
}

WOOD = {
	(0x49, 0x36, 0x15, 0xFF),
	(0x68, 0x4E, 0x1E, 0xFF),
	(0x89, 0x67, 0x27, 0xFF),
	(0x28, 0x1E, 0x0B, 0xFF),
	(0x2A, 0x1A, 0x0A, 0xFF),
}

PROFILES: dict[str, Path] = {
	"sword_basic": TEX / "forged_sword.png",
	"sword_long": TEX / "forge_template_sword_long.png",
	"axe_basic": TEX / "forged_axe.png",
	"pickaxe_basic": TEX / "forged_pickaxe.png",
	"shovel_basic": TEX / "forged_shovel.png",
	"hoe_basic": TEX / "forged_hoe.png",
}

BURNING: list[Color] = [
	(0x3A, 0x10, 0x08, 0xFF),
	(0x8A, 0x28, 0x08, 0xFF),
	(0xE8, 0x5A, 0x10, 0xFF),
	(0xFF, 0xB0, 0x30, 0xFF),
	(0xFF, 0xF0, 0xA0, 0xFF),
]

# cooled / part palettes per metal (dark → bright)
METAL_COOLED: dict[str, list[Color]] = {
	"copper": [
		(0x3A, 0x18, 0x0A, 0xFF),
		(0x6A, 0x32, 0x14, 0xFF),
		(0xA0, 0x58, 0x2A, 0xFF),
		(0xC8, 0x78, 0x40, 0xFF),
		(0xE8, 0xA0, 0x68, 0xFF),
	],
	"iron": [
		(0x18, 0x1C, 0x24, 0xFF),
		(0x36, 0x40, 0x4E, 0xFF),
		(0x5A, 0x68, 0x78, 0xFF),
		(0x7E, 0x8E, 0x9C, 0xFF),
		(0xA0, 0xAE, 0xBA, 0xFF),
	],
	"steel": [
		(0x14, 0x18, 0x22, 0xFF),
		(0x3A, 0x48, 0x5A, 0xFF),
		(0x6A, 0x7C, 0x90, 0xFF),
		(0x9A, 0xAE, 0xC2, 0xFF),
		(0xD0, 0xE0, 0xF0, 0xFF),
	],
}

METAL_PART: dict[str, list[Color]] = {
	"copper": [
		(0x4A, 0x20, 0x0C, 0xFF),
		(0x8A, 0x42, 0x1C, 0xFF),
		(0xC0, 0x6E, 0x38, 0xFF),
		(0xE0, 0x98, 0x58, 0xFF),
		(0xF8, 0xC0, 0x88, 0xFF),
	],
	"iron": [
		(0x22, 0x28, 0x34, 0xFF),
		(0x52, 0x60, 0x70, 0xFF),
		(0x8A, 0x9A, 0xAA, 0xFF),
		(0xC0, 0xCC, 0xD8, 0xFF),
		(0xEC, 0xF2, 0xF8, 0xFF),
	],
	"steel": [
		(0x1A, 0x22, 0x30, 0xFF),
		(0x4A, 0x5C, 0x72, 0xFF),
		(0x8A, 0xA0, 0xB8, 0xFF),
		(0xC4, 0xD6, 0xE8, 0xFF),
		(0xF0, 0xF6, 0xFF, 0xFF),
	],
}


def luminance(c: Color) -> float:
	r, g, b, _ = c
	return (0.2126 * r + 0.7152 * g + 0.0722 * b) / 255.0


def sample_palette(stops: list[Color], t: float) -> Color:
	t = max(0.0, min(1.0, t))
	scaled = t * (len(stops) - 1)
	i = int(scaled)
	if i >= len(stops) - 1:
		return stops[-1]
	frac = scaled - i
	a, b = stops[i], stops[i + 1]
	return (
		int(a[0] + (b[0] - a[0]) * frac),
		int(a[1] + (b[1] - a[1]) * frac),
		int(a[2] + (b[2] - a[2]) * frac),
		255,
	)


def in_handle_zone(profile: str, x: int, y: int) -> bool:
	if profile in ("sword_basic", "sword_long"):
		return y >= 12 or (y >= 11 and x <= 6) or (y >= 14)
	if profile == "axe_basic":
		return y >= 8 or (y >= 6 and x <= 4)
	if profile == "pickaxe_basic":
		return y >= 5
	if profile == "shovel_basic":
		return y >= 7 and x <= (y - 1)
	if profile == "hoe_basic":
		return y >= 5 and x <= (y + 1)
	return False


def is_parchment(c: Color) -> bool:
	if c in PARCHMENT:
		return True
	r, g, b, a = c
	return a > 0 and r > 180 and g > 150 and b < 200 and abs(r - g) < 40


def casting_silhouette(src: Canvas, profile: str) -> Canvas:
	out = Canvas.new(src.width, src.height, (0, 0, 0, 0))
	for y in range(src.height):
		for x in range(src.width):
			c = src.get(x, y)
			if c[3] == 0 or is_parchment(c):
				continue
			if in_handle_zone(profile, x, y):
				continue
			if c in WOOD and in_handle_zone(profile, x, y):
				continue
			out.set(x, y, c)
	return out


def recolor(src: Canvas, stops: list[Color]) -> Canvas:
	lums = [
		luminance(src.get(x, y))
		for y in range(src.height)
		for x in range(src.width)
		if src.get(x, y)[3] > 0
	]
	if not lums:
		return Canvas.new(src.width, src.height, (0, 0, 0, 0))
	lo, hi = min(lums), max(lums)
	span = hi - lo if hi > lo else 1.0
	out = Canvas.new(src.width, src.height, (0, 0, 0, 0))
	for y in range(src.height):
		for x in range(src.width):
			c = src.get(x, y)
			if c[3] == 0:
				continue
			t = (luminance(c) - lo) / span
			out.set(x, y, sample_palette(stops, t))
	return out


def main() -> int:
	wrote = 0
	for profile, base_path in PROFILES.items():
		if not base_path.is_file():
			print(f"missing base {base_path}", file=sys.stderr)
			return 1
		base = casting_silhouette(Canvas.load(base_path), profile)

		dest = TEX / f"forge_piece_burning_{profile}.png"
		recolor(base, BURNING).save(dest)
		print(f"wrote {dest.relative_to(REPO)}")
		wrote += 1

		for metal, stops in METAL_COOLED.items():
			dest = TEX / f"forge_piece_cooled_{profile}_{metal}.png"
			recolor(base, stops).save(dest)
			print(f"wrote {dest.relative_to(REPO)}")
			wrote += 1

		for metal, stops in METAL_PART.items():
			dest = TEX / f"forge_piece_part_{profile}_{metal}.png"
			recolor(base, stops).save(dest)
			print(f"wrote {dest.relative_to(REPO)}")
			wrote += 1

	print(f"total {wrote}")
	return 0


if __name__ == "__main__":
	raise SystemExit(main())
