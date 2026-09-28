"""Atomic canvas operations from JSON ops documents."""

from __future__ import annotations

import json
from pathlib import Path
from typing import Any

from pixel_drawer.canvas import Canvas, parse_color, parse_size


def load_ops_doc(text: str) -> dict[str, Any]:
	data = json.loads(text)
	if not isinstance(data, dict):
		raise ValueError("ops document must be a JSON object")
	if "ops" in data and not isinstance(data["ops"], list):
		raise ValueError("'ops' must be a list")
	return data


def canvas_from_ops_doc(data: dict[str, Any], input_path: str | Path | None = None) -> Canvas:
	if input_path is not None:
		canvas = Canvas.load(input_path)
	else:
		size_text = data.get("size", "16x16")
		w, h = parse_size(str(size_text))
		canvas = Canvas.new(w, h)
	apply_ops(canvas, data.get("ops", []), base_dir=_base_dir(input_path, data))
	return canvas


def apply_ops(canvas: Canvas, ops: list[dict[str, Any]], base_dir: Path | None = None) -> None:
	for i, op in enumerate(ops):
		if not isinstance(op, dict) or "op" not in op:
			raise ValueError(f"ops[{i}] must be an object with 'op'")
		name = op["op"]
		try:
			_apply_one(canvas, op, base_dir)
		except Exception as exc:
			raise ValueError(f"ops[{i}] ({name}): {exc}") from exc


def _base_dir(input_path: str | Path | None, data: dict[str, Any]) -> Path | None:
	if "_ops_path" in data:
		return Path(data["_ops_path"]).parent
	if input_path is not None:
		return Path(input_path).parent
	return None


def _apply_one(canvas: Canvas, op: dict[str, Any], base_dir: Path | None) -> None:
	name = op["op"]
	if name == "fill":
		canvas.fill(parse_color(op["color"]))
	elif name == "set":
		canvas.set(int(op["x"]), int(op["y"]), parse_color(op["color"]))
	elif name == "rect":
		canvas.fill_rect(int(op["x"]), int(op["y"]), int(op["w"]), int(op["h"]), parse_color(op["color"]))
	elif name == "line":
		canvas.line(
				int(op["x0"]), int(op["y0"]), int(op["x1"]), int(op["y1"]),
				parse_color(op["color"]),
		)
	elif name == "replace":
		count = canvas.replace(parse_color(op["from"]), parse_color(op["to"]))
		# count available for callers via exception message only; silent ok
		_ = count
	elif name == "stamp":
		path = Path(op["path"])
		if not path.is_absolute() and base_dir is not None:
			path = base_dir / path
		other = Canvas.load(path)
		key = op.get("transparent_key")
		transparent_key = parse_color(key) if key else None
		canvas.stamp(other, int(op.get("x", 0)), int(op.get("y", 0)), transparent_key)
	else:
		raise ValueError(f"unknown op {name!r}")
