"""CLI entry for pixel_drawer."""

from __future__ import annotations

import argparse
import sys
from pathlib import Path

from pixel_drawer.ascii_format import (
		ascii_to_canvas,
		canvas_to_ascii,
		format_ascii,
		format_json,
		parse_ascii,
)
from pixel_drawer.canvas import Canvas, parse_color, parse_size
from pixel_drawer.ops import canvas_from_ops_doc, load_ops_doc


def main(argv: list[str] | None = None) -> int:
	parser = argparse.ArgumentParser(
			prog="pixel_drawer",
			description="Deterministic RGBA pixel PNG tools for AI / CLI use.",
	)
	sub = parser.add_subparsers(dest="command", required=True)

	p_new = sub.add_parser("new", help="Create a blank PNG")
	p_new.add_argument("--size", default="16x16", help="WxH, default 16x16")
	p_new.add_argument("--color", default="#00000000", help="Fill color")
	p_new.add_argument("-o", "--output", required=True, help="Output PNG path")
	p_new.set_defaults(func=cmd_new)

	p_inspect = sub.add_parser("inspect", help="Dump PNG to ascii/json palette+grid")
	p_inspect.add_argument("-i", "--input", required=True, help="Input PNG")
	p_inspect.add_argument("--format", choices=("ascii", "json"), default="ascii")
	p_inspect.set_defaults(func=cmd_inspect)

	p_from = sub.add_parser("from-text", help="Render ASCII art file to PNG")
	p_from.add_argument("-i", "--input", required=True, help="ASCII art .txt")
	p_from.add_argument("-o", "--output", required=True, help="Output PNG")
	p_from.set_defaults(func=cmd_from_text)

	p_apply = sub.add_parser("apply", help="Apply ops JSON to image (or new canvas)")
	p_apply.add_argument("-i", "--input", help="Input PNG (optional if ops has size)")
	p_apply.add_argument("-o", "--output", required=True, help="Output PNG")
	p_apply.add_argument("--ops", required=True, help="Ops JSON path, or '-' for stdin")
	p_apply.set_defaults(func=cmd_apply)

	p_set = sub.add_parser("set", help="Set one pixel or fill a rectangle")
	p_set.add_argument("-i", "--input", required=True, help="Input PNG")
	p_set.add_argument("-o", "--output", required=True, help="Output PNG")
	p_set.add_argument("--color", required=True, help="Color #RRGGBB[AA]")
	p_set.add_argument("--xy", help="x,y for single pixel")
	p_set.add_argument("--rect", help="x,y,w,h rectangle fill")
	p_set.set_defaults(func=cmd_set)

	p_preview = sub.add_parser("preview", help="Nearest-neighbor scaled preview PNG")
	p_preview.add_argument("-i", "--input", required=True, help="Input PNG")
	p_preview.add_argument("-o", "--output", required=True, help="Preview PNG (not for assets)")
	p_preview.add_argument("--scale", type=int, default=8, help="Integer scale, default 8")
	p_preview.set_defaults(func=cmd_preview)

	args = parser.parse_args(argv)
	try:
		return args.func(args)
	except Exception as exc:
		print(f"error: {exc}", file=sys.stderr)
		return 1


def cmd_new(args: argparse.Namespace) -> int:
	w, h = parse_size(args.size)
	color = parse_color(args.color)
	canvas = Canvas.new(w, h, color)
	canvas.save(args.output)
	print(f"wrote {args.output} ({w}x{h})")
	return 0


def cmd_inspect(args: argparse.Namespace) -> int:
	canvas = Canvas.load(args.input)
	art = canvas_to_ascii(canvas)
	if args.format == "json":
		sys.stdout.write(format_json(art))
	else:
		sys.stdout.write(format_ascii(art))
	return 0


def cmd_from_text(args: argparse.Namespace) -> int:
	text = Path(args.input).read_text(encoding="utf-8")
	art = parse_ascii(text)
	canvas = ascii_to_canvas(art)
	canvas.save(args.output)
	print(f"wrote {args.output} ({art.width}x{art.height})")
	return 0


def cmd_apply(args: argparse.Namespace) -> int:
	if args.ops == "-":
		ops_text = sys.stdin.read()
		ops_path = None
	else:
		ops_path = Path(args.ops)
		ops_text = ops_path.read_text(encoding="utf-8")
	data = load_ops_doc(ops_text)
	if ops_path is not None:
		data["_ops_path"] = str(ops_path.resolve())
	canvas = canvas_from_ops_doc(data, args.input)
	canvas.save(args.output)
	print(f"wrote {args.output} ({canvas.width}x{canvas.height})")
	return 0


def cmd_set(args: argparse.Namespace) -> int:
	if not args.xy and not args.rect:
		raise ValueError("provide --xy x,y or --rect x,y,w,h")
	if args.xy and args.rect:
		raise ValueError("use only one of --xy or --rect")
	canvas = Canvas.load(args.input)
	color = parse_color(args.color)
	if args.xy:
		parts = args.xy.split(",")
		if len(parts) != 2:
			raise ValueError("--xy must be x,y")
		x, y = int(parts[0]), int(parts[1])
		canvas.set(x, y, color)
	else:
		parts = args.rect.split(",")
		if len(parts) != 4:
			raise ValueError("--rect must be x,y,w,h")
		x, y, w, h = (int(p) for p in parts)
		canvas.fill_rect(x, y, w, h, color)
	canvas.save(args.output)
	print(f"wrote {args.output}")
	return 0


def cmd_preview(args: argparse.Namespace) -> int:
	canvas = Canvas.load(args.input)
	preview = canvas.scaled_preview(args.scale)
	out = Path(args.output)
	out.parent.mkdir(parents=True, exist_ok=True)
	preview.save(out, format="PNG")
	print(f"wrote preview {args.output} ({preview.width}x{preview.height})")
	return 0


if __name__ == "__main__":
	raise SystemExit(main())
