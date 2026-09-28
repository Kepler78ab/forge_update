"""ASCII palette + grid import/export (shared structure for ascii/json inspect)."""

from __future__ import annotations

import json
import re
from dataclasses import dataclass

from pixel_drawer.canvas import Canvas, Color, color_to_hex, parse_color

# Prefer readable glyphs; avoid space (hard to see in grids).
_GLYPHS = (
		".#ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
		"0123456789@%&*+=-:;!?/\\"
)

_PALETTE_LINE = re.compile(r"^(.)\s*=\s*(#[0-9A-Fa-f]{3,8})\s*$")


@dataclass
class AsciiArt:
	palette: dict[str, Color]
	grid: list[str]

	@property
	def width(self) -> int:
		return len(self.grid[0]) if self.grid else 0

	@property
	def height(self) -> int:
		return len(self.grid)


def parse_ascii(text: str) -> AsciiArt:
	palette: dict[str, Color] = {}
	grid: list[str] = []
	section: str | None = None

	for line_no, raw in enumerate(text.splitlines(), start=1):
		line = raw.rstrip("\n\r")
		stripped = line.strip()
		if not stripped:
			continue

		header = _section_header(stripped)
		if header is not None:
			section = header
			continue

		if section is None:
			raise ValueError(f"line {line_no}: expected '# palette' or '# grid' before content")

		if section == "palette":
			match = _PALETTE_LINE.match(stripped)
			if match:
				ch, hex_color = match.group(1), match.group(2)
				palette[ch] = parse_color(hex_color)
				continue
			if stripped.startswith("#"):
				continue
			raise ValueError(f"line {line_no}: palette entry must be 'C = #RRGGBBAA'")

		if section == "grid":
			grid.append(line)

	if not palette:
		raise ValueError("palette is empty")
	if not grid:
		raise ValueError("grid is empty")
	width = len(grid[0])
	for i, row in enumerate(grid):
		if len(row) != width:
			raise ValueError(f"grid row {i} width {len(row)} != {width}")
		for ch in row:
			if ch not in palette:
				raise ValueError(f"unknown grid character {ch!r} (not in palette)")
	return AsciiArt(palette=palette, grid=grid)


def _section_header(stripped: str) -> str | None:
	if not stripped.startswith("#"):
		return None
	header = stripped.lstrip("#").strip().lower()
	if header in ("palette", "grid"):
		return header
	return None


def ascii_to_canvas(art: AsciiArt) -> Canvas:
	canvas = Canvas.new(art.width, art.height)
	for y, row in enumerate(art.grid):
		for x, ch in enumerate(row):
			canvas.set(x, y, art.palette[ch])
	return canvas


def canvas_to_ascii(canvas: Canvas) -> AsciiArt:
	"""Build a compact palette + grid from canvas pixels."""
	color_to_glyph: dict[Color, str] = {}
	palette: dict[str, Color] = {}
	grid: list[str] = []
	glyph_iter = iter(_GLYPHS)

	def glyph_for(color: Color) -> str:
		if color in color_to_glyph:
			return color_to_glyph[color]
		try:
			g = next(glyph_iter)
		except StopIteration as exc:
			raise ValueError("too many unique colors for ASCII palette glyphs") from exc
		color_to_glyph[color] = g
		palette[g] = color
		return g

	for y in range(canvas.height):
		row = []
		for x in range(canvas.width):
			row.append(glyph_for(canvas.get(x, y)))
		grid.append("".join(row))
	return AsciiArt(palette=palette, grid=grid)


def format_ascii(art: AsciiArt) -> str:
	lines = ["# palette"]
	for ch, color in art.palette.items():
		lines.append(f"{ch} = {color_to_hex(color)}")
	lines.append("")
	lines.append("# grid")
	lines.extend(art.grid)
	lines.append("")
	return "\n".join(lines)


def format_json(art: AsciiArt) -> str:
	payload = {
			"width": art.width,
			"height": art.height,
			"palette": {ch: color_to_hex(c) for ch, c in art.palette.items()},
			"grid": art.grid,
	}
	return json.dumps(payload, indent=2, ensure_ascii=False) + "\n"


def parse_json_art(text: str) -> AsciiArt:
	data = json.loads(text)
	palette = {ch: parse_color(hex_color) for ch, hex_color in data["palette"].items()}
	grid = list(data["grid"])
	return AsciiArt(palette=palette, grid=grid)
