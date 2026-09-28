"""RGBA canvas helpers: load/save/pixel I/O with no interpolation."""

from __future__ import annotations

from pathlib import Path
from typing import Iterable

from PIL import Image

Color = tuple[int, int, int, int]


def parse_color(text: str) -> Color:
	"""Parse #RGB, #RRGGBB, or #RRGGBBAA into RGBA 0–255."""
	raw = text.strip()
	if not raw.startswith("#"):
		raise ValueError(f"color must start with #: {text!r}")
	hex_part = raw[1:]
	if len(hex_part) == 3:
		hex_part = "".join(ch * 2 for ch in hex_part) + "FF"
	elif len(hex_part) == 6:
		hex_part += "FF"
	elif len(hex_part) != 8:
		raise ValueError(f"color must be #RGB, #RRGGBB, or #RRGGBBAA: {text!r}")
	try:
		r = int(hex_part[0:2], 16)
		g = int(hex_part[2:4], 16)
		b = int(hex_part[4:6], 16)
		a = int(hex_part[6:8], 16)
	except ValueError as exc:
		raise ValueError(f"invalid hex color: {text!r}") from exc
	return r, g, b, a


def color_to_hex(color: Color) -> str:
	r, g, b, a = color
	return f"#{r:02X}{g:02X}{b:02X}{a:02X}"


def parse_size(text: str) -> tuple[int, int]:
	parts = text.lower().replace(" ", "").split("x")
	if len(parts) != 2:
		raise ValueError(f"size must look like 16x16: {text!r}")
	w, h = int(parts[0]), int(parts[1])
	if w <= 0 or h <= 0:
		raise ValueError(f"size must be positive: {text!r}")
	return w, h


class Canvas:
	def __init__(self, image: Image.Image) -> None:
		if image.mode != "RGBA":
			image = image.convert("RGBA")
		self.image = image

	@classmethod
	def new(cls, width: int, height: int, color: Color = (0, 0, 0, 0)) -> Canvas:
		return cls(Image.new("RGBA", (width, height), color))

	@classmethod
	def load(cls, path: str | Path) -> Canvas:
		with Image.open(path) as img:
			return cls(img.copy())

	@property
	def width(self) -> int:
		return self.image.width

	@property
	def height(self) -> int:
		return self.image.height

	def save(self, path: str | Path) -> None:
		out = Path(path)
		out.parent.mkdir(parents=True, exist_ok=True)
		# Avoid ICC / exotic metadata; keep deterministic RGBA PNG.
		self.image.save(out, format="PNG")

	def get(self, x: int, y: int) -> Color:
		self._check(x, y)
		pixel = self.image.getpixel((x, y))
		assert isinstance(pixel, tuple)
		return int(pixel[0]), int(pixel[1]), int(pixel[2]), int(pixel[3])

	def set(self, x: int, y: int, color: Color) -> None:
		self._check(x, y)
		self.image.putpixel((x, y), color)

	def fill(self, color: Color) -> None:
		self.image.paste(color, (0, 0, self.width, self.height))

	def fill_rect(self, x: int, y: int, w: int, h: int, color: Color) -> None:
		if w <= 0 or h <= 0:
			raise ValueError("rect width/height must be positive")
		for yy in range(y, y + h):
			for xx in range(x, x + w):
				self.set(xx, yy, color)

	def line(self, x0: int, y0: int, x1: int, y1: int, color: Color) -> None:
		# Bresenham
		dx = abs(x1 - x0)
		dy = -abs(y1 - y0)
		sx = 1 if x0 < x1 else -1
		sy = 1 if y0 < y1 else -1
		err = dx + dy
		x, y = x0, y0
		while True:
			self.set(x, y, color)
			if x == x1 and y == y1:
				break
			e2 = 2 * err
			if e2 >= dy:
				err += dy
				x += sx
			if e2 <= dx:
				err += dx
				y += sy

	def replace(self, src: Color, dst: Color) -> int:
		count = 0
		pixels = self.image.load()
		assert pixels is not None
		for y in range(self.height):
			for x in range(self.width):
				if pixels[x, y] == src:
					pixels[x, y] = dst
					count += 1
		return count

	def stamp(
			self,
			other: Canvas,
			x: int,
			y: int,
			transparent_key: Color | None = None,
	) -> None:
		for yy in range(other.height):
			for xx in range(other.width):
				color = other.get(xx, yy)
				if transparent_key is not None and color == transparent_key:
					continue
				if color[3] == 0 and transparent_key is None:
					# Skip fully transparent by default when no key given.
					continue
				tx, ty = x + xx, y + yy
				if 0 <= tx < self.width and 0 <= ty < self.height:
					self.set(tx, ty, color)

	def scaled_preview(self, scale: int) -> Image.Image:
		if scale < 1:
			raise ValueError("scale must be >= 1")
		return self.image.resize(
				(self.width * scale, self.height * scale),
				resample=Image.Resampling.NEAREST,
		)

	def iter_pixels(self) -> Iterable[tuple[int, int, Color]]:
		for y in range(self.height):
			for x in range(self.width):
				yield x, y, self.get(x, y)

	def _check(self, x: int, y: int) -> None:
		if not (0 <= x < self.width and 0 <= y < self.height):
			raise ValueError(f"pixel out of bounds: ({x},{y}) size={self.width}x{self.height}")
