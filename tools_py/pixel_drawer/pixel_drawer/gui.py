"""
Tkinter GUI for pixel_drawer — uses the same Canvas / ASCII / ops core as the CLI.
"""

from __future__ import annotations

import json
import tkinter as tk
from tkinter import colorchooser, filedialog, messagebox, ttk

from PIL import Image, ImageTk

from pixel_drawer.ascii_format import ascii_to_canvas, canvas_to_ascii, format_ascii, parse_ascii
from pixel_drawer.canvas import Canvas, Color, color_to_hex, parse_color, parse_size
from pixel_drawer.ops import apply_ops, load_ops_doc

TRANSPARENT: Color = (0, 0, 0, 0)
CHECKER_A = (40, 40, 40, 255)
CHECKER_B = (60, 60, 60, 255)


class PixelDrawerApp(tk.Tk):
	def __init__(self) -> None:
		super().__init__()
		self.title("pixel_drawer GUI")
		self.minsize(900, 640)

		self.canvas = Canvas.new(16, 16, TRANSPARENT)
		self.path: str | None = None
		self.zoom = tk.IntVar(value=16)
		self.tool = tk.StringVar(value="pencil")
		self.color_hex = tk.StringVar(value="#FFFFFFFF")
		self.size_text = tk.StringVar(value="16x16")
		self.status = tk.StringVar(value="Ready")
		self._drag_start: tuple[int, int] | None = None
		self._photo: ImageTk.PhotoImage | None = None
		self._dirty = False

		self._build_ui()
		self._redraw()
		self.protocol("WM_DELETE_WINDOW", self._on_close)

	def _build_ui(self) -> None:
		menubar = tk.Menu(self)
		file_menu = tk.Menu(menubar, tearoff=0)
		file_menu.add_command(label="New…", command=self.new_image, accelerator="Ctrl+N")
		file_menu.add_command(label="Open PNG…", command=self.open_png, accelerator="Ctrl+O")
		file_menu.add_command(label="Save", command=self.save_png, accelerator="Ctrl+S")
		file_menu.add_command(label="Save As…", command=self.save_png_as)
		file_menu.add_separator()
		file_menu.add_command(label="Import ASCII…", command=self.import_ascii)
		file_menu.add_command(label="Export ASCII…", command=self.export_ascii)
		file_menu.add_separator()
		file_menu.add_command(label="Exit", command=self._on_close)
		menubar.add_cascade(label="File", menu=file_menu)
		self.config(menu=menubar)

		self.bind_all("<Control-n>", lambda e: self.new_image())
		self.bind_all("<Control-o>", lambda e: self.open_png())
		self.bind_all("<Control-s>", lambda e: self.save_png())
		self.bind_all("<Control-Shift-S>", lambda e: self.save_png_as())

		root = ttk.Frame(self, padding=8)
		root.pack(fill=tk.BOTH, expand=True)

		left = ttk.Frame(root)
		left.pack(side=tk.LEFT, fill=tk.Y)

		ttk.Label(left, text="File").pack(anchor=tk.W)
		ttk.Button(left, text="Open PNG…", command=self.open_png).pack(fill=tk.X, pady=2)
		ttk.Button(left, text="Save", command=self.save_png).pack(fill=tk.X, pady=2)
		ttk.Button(left, text="Save As…", command=self.save_png_as).pack(fill=tk.X, pady=2)
		ttk.Button(left, text="Import ASCII…", command=self.import_ascii).pack(fill=tk.X, pady=2)
		ttk.Button(left, text="Export ASCII…", command=self.export_ascii).pack(fill=tk.X, pady=2)

		ttk.Separator(left, orient=tk.HORIZONTAL).pack(fill=tk.X, pady=8)
		ttk.Label(left, text="Tool").pack(anchor=tk.W)
		for value, label in (
				("pencil", "Pencil (set)"),
				("eraser", "Eraser (transparent)"),
				("rect", "Rect fill"),
				("eyedropper", "Eyedropper"),
				("replace", "Replace color"),
		):
			ttk.Radiobutton(left, text=label, value=value, variable=self.tool).pack(anchor=tk.W)

		ttk.Separator(left, orient=tk.HORIZONTAL).pack(fill=tk.X, pady=8)
		ttk.Label(left, text="Color #RRGGBB[AA]").pack(anchor=tk.W)
		color_row = ttk.Frame(left)
		color_row.pack(fill=tk.X, pady=2)
		ttk.Entry(color_row, textvariable=self.color_hex, width=14).pack(side=tk.LEFT)
		ttk.Button(color_row, text="…", width=3, command=self.pick_color).pack(side=tk.LEFT, padx=4)
		self.color_swatch = tk.Canvas(left, width=64, height=24, highlightthickness=1)
		self.color_swatch.pack(anchor=tk.W, pady=4)
		self._update_swatch()

		ttk.Separator(left, orient=tk.HORIZONTAL).pack(fill=tk.X, pady=8)
		ttk.Label(left, text="New size").pack(anchor=tk.W)
		ttk.Entry(left, textvariable=self.size_text, width=12).pack(anchor=tk.W)
		ttk.Button(left, text="New canvas", command=self.new_image).pack(fill=tk.X, pady=4)

		ttk.Label(left, text="Zoom").pack(anchor=tk.W, pady=(8, 0))
		ttk.Scale(
				left, from_=4, to=32, orient=tk.HORIZONTAL, variable=self.zoom,
				command=lambda _v: self._redraw(),
		).pack(fill=tk.X)

		ttk.Separator(left, orient=tk.HORIZONTAL).pack(fill=tk.X, pady=8)
		ttk.Button(left, text="Fill all", command=self.fill_all).pack(fill=tk.X, pady=2)
		ttk.Button(left, text="Clear (transparent)", command=self.clear_all).pack(fill=tk.X, pady=2)

		center = ttk.Frame(root)
		center.pack(side=tk.LEFT, fill=tk.BOTH, expand=True, padx=8)
		self.view = tk.Canvas(center, bg="#1e1e1e", highlightthickness=0, cursor="crosshair")
		self.view.pack(fill=tk.BOTH, expand=True)
		self.view.bind("<Button-1>", self._on_press)
		self.view.bind("<B1-Motion>", self._on_drag)
		self.view.bind("<ButtonRelease-1>", self._on_release)
		self.view.bind("<Motion>", self._on_motion)

		right = ttk.Frame(root, width=280)
		right.pack(side=tk.RIGHT, fill=tk.BOTH)
		ttk.Label(right, text="Ops JSON (CLI apply)").pack(anchor=tk.W)
		self.ops_text = tk.Text(right, width=36, height=18, wrap=tk.NONE, font=("Consolas", 9))
		self.ops_text.pack(fill=tk.BOTH, expand=True)
		self.ops_text.insert(
				"1.0",
				json.dumps(
						{
								"ops": [
										{"op": "fill", "color": "#00000000"},
										{"op": "rect", "x": 2, "y": 2, "w": 12, "h": 12, "color": "#8C8C8CFF"},
								]
						},
						indent=2,
				),
		)
		ttk.Button(right, text="Apply ops to canvas", command=self.apply_ops_panel).pack(fill=tk.X, pady=4)
		ttk.Button(right, text="Dump ASCII → clipboard", command=self.copy_ascii).pack(fill=tk.X, pady=2)

		ttk.Label(self, textvariable=self.status, anchor=tk.W, padding=4).pack(fill=tk.X, side=tk.BOTTOM)

	def _set_status(self, msg: str) -> None:
		self.status.set(msg)

	def _mark_dirty(self) -> None:
		self._dirty = True
		title = "pixel_drawer GUI *"
		if self.path:
			title += f" — {self.path}"
		self.title(title)

	def _clear_dirty(self) -> None:
		self._dirty = False
		title = "pixel_drawer GUI"
		if self.path:
			title += f" — {self.path}"
		self.title(title)

	def _update_swatch(self) -> None:
		try:
			color = parse_color(self.color_hex.get())
		except ValueError:
			color = (255, 0, 255, 255)
		fill = f"#{color[0]:02x}{color[1]:02x}{color[2]:02x}"
		self.color_swatch.delete("all")
		self.color_swatch.create_rectangle(0, 0, 64, 24, fill=fill, outline="")

	def pick_color(self) -> None:
		try:
			cur = parse_color(self.color_hex.get())
			initial = f"#{cur[0]:02x}{cur[1]:02x}{cur[2]:02x}"
		except ValueError:
			initial = "#ffffff"
		picked = colorchooser.askcolor(color=initial, title="Pick RGB (alpha kept / set FF)")
		if not picked or not picked[1]:
			return
		rgb = picked[1].lstrip("#")
		# Keep existing alpha if current hex valid, else opaque.
		alpha = "FF"
		try:
			alpha = f"{parse_color(self.color_hex.get())[3]:02X}"
		except ValueError:
			pass
		self.color_hex.set(f"#{rgb.upper()}{alpha}")
		self._update_swatch()

	def _current_color(self) -> Color:
		return parse_color(self.color_hex.get())

	def _pixel_at(self, event_x: int, event_y: int) -> tuple[int, int] | None:
		z = max(1, int(self.zoom.get()))
		x, y = event_x // z, event_y // z
		if 0 <= x < self.canvas.width and 0 <= y < self.canvas.height:
			return x, y
		return None

	def _redraw(self) -> None:
		z = max(1, int(self.zoom.get()))
		w, h = self.canvas.width, self.canvas.height
		# Checkerboard under transparency, then nearest-neighbor scale.
		base = self.canvas.image.copy()
		bg = Canvas.new(w, h)
		for yy in range(h):
			for xx in range(w):
				bg.set(xx, yy, CHECKER_A if (xx + yy) % 2 == 0 else CHECKER_B)
		composited = bg.image.copy()
		composited.alpha_composite(base)
		preview = composited.resize((w * z, h * z), resample=Image.Resampling.NEAREST)
		self._photo = ImageTk.PhotoImage(preview)
		self.view.delete("all")
		self.view.config(width=w * z, height=h * z, scrollregion=(0, 0, w * z, h * z))
		self.view.create_image(0, 0, anchor=tk.NW, image=self._photo)
		self._update_swatch()

	def _on_press(self, event: tk.Event) -> None:
		pos = self._pixel_at(event.x, event.y)
		if pos is None:
			return
		tool = self.tool.get()
		x, y = pos
		if tool == "eyedropper":
			c = self.canvas.get(x, y)
			self.color_hex.set(color_to_hex(c))
			self._update_swatch()
			self._set_status(f"Picked {color_to_hex(c)} at ({x},{y})")
			return
		if tool == "replace":
			try:
				src = self.canvas.get(x, y)
				dst = self._current_color()
				n = self.canvas.replace(src, dst)
				self._mark_dirty()
				self._redraw()
				self._set_status(f"Replaced {color_to_hex(src)} → {color_to_hex(dst)} ({n} px)")
			except ValueError as exc:
				messagebox.showerror("Color", str(exc))
			return
		if tool == "rect":
			self._drag_start = pos
			self._set_status(f"Rect start ({x},{y})")
			return
		self._paint_at(x, y)
		self._drag_start = pos

	def _on_drag(self, event: tk.Event) -> None:
		pos = self._pixel_at(event.x, event.y)
		if pos is None:
			return
		tool = self.tool.get()
		if tool in ("pencil", "eraser") and self._drag_start is not None:
			self._paint_at(*pos)

	def _on_release(self, event: tk.Event) -> None:
		pos = self._pixel_at(event.x, event.y)
		tool = self.tool.get()
		if tool == "rect" and self._drag_start is not None and pos is not None:
			x0, y0 = self._drag_start
			x1, y1 = pos
			x, y = min(x0, x1), min(y0, y1)
			w, h = abs(x1 - x0) + 1, abs(y1 - y0) + 1
			try:
				color = self._current_color()
				self.canvas.fill_rect(x, y, w, h, color)
				self._mark_dirty()
				self._redraw()
				self._set_status(f"Rect ({x},{y},{w},{h}) {color_to_hex(color)}")
			except ValueError as exc:
				messagebox.showerror("Rect", str(exc))
		self._drag_start = None

	def _on_motion(self, event: tk.Event) -> None:
		pos = self._pixel_at(event.x, event.y)
		if pos is None:
			return
		x, y = pos
		c = self.canvas.get(x, y)
		self._set_status(f"({x},{y}) {color_to_hex(c)} | tool={self.tool.get()}")

	def _paint_at(self, x: int, y: int) -> None:
		try:
			if self.tool.get() == "eraser":
				self.canvas.set(x, y, TRANSPARENT)
			else:
				self.canvas.set(x, y, self._current_color())
			self._mark_dirty()
			self._redraw()
		except ValueError as exc:
			messagebox.showerror("Paint", str(exc))

	def _confirm_discard(self) -> bool:
		if not self._dirty:
			return True
		return messagebox.askyesno("Unsaved", "Discard unsaved changes?")

	def new_image(self) -> None:
		if not self._confirm_discard():
			return
		try:
			w, h = parse_size(self.size_text.get())
		except ValueError as exc:
			messagebox.showerror("Size", str(exc))
			return
		self.canvas = Canvas.new(w, h, TRANSPARENT)
		self.path = None
		self._clear_dirty()
		self._redraw()
		self._set_status(f"New {w}x{h}")

	def open_png(self) -> None:
		if not self._confirm_discard():
			return
		path = filedialog.askopenfilename(filetypes=[("PNG", "*.png"), ("All", "*.*")])
		if not path:
			return
		try:
			self.canvas = Canvas.load(path)
			self.path = path
			self.size_text.set(f"{self.canvas.width}x{self.canvas.height}")
			self._clear_dirty()
			self._redraw()
			self._set_status(f"Opened {path}")
		except Exception as exc:
			messagebox.showerror("Open", str(exc))

	def save_png(self) -> None:
		if not self.path:
			self.save_png_as()
			return
		try:
			self.canvas.save(self.path)
			self._clear_dirty()
			self._set_status(f"Saved {self.path}")
		except Exception as exc:
			messagebox.showerror("Save", str(exc))

	def save_png_as(self) -> None:
		path = filedialog.asksaveasfilename(
				defaultextension=".png",
				filetypes=[("PNG", "*.png")],
		)
		if not path:
			return
		self.path = path
		self.save_png()

	def import_ascii(self) -> None:
		if not self._confirm_discard():
			return
		path = filedialog.askopenfilename(filetypes=[("Text", "*.txt"), ("All", "*.*")])
		if not path:
			return
		try:
			text = open(path, encoding="utf-8").read()
			art = parse_ascii(text)
			self.canvas = ascii_to_canvas(art)
			self.path = None
			self.size_text.set(f"{self.canvas.width}x{self.canvas.height}")
			self._mark_dirty()
			self._redraw()
			self._set_status(f"Imported ASCII {path}")
		except Exception as exc:
			messagebox.showerror("Import ASCII", str(exc))

	def export_ascii(self) -> None:
		path = filedialog.asksaveasfilename(
				defaultextension=".txt",
				filetypes=[("Text", "*.txt")],
		)
		if not path:
			return
		try:
			text = format_ascii(canvas_to_ascii(self.canvas))
			open(path, "w", encoding="utf-8", newline="\n").write(text)
			self._set_status(f"Exported ASCII {path}")
		except Exception as exc:
			messagebox.showerror("Export ASCII", str(exc))

	def copy_ascii(self) -> None:
		try:
			text = format_ascii(canvas_to_ascii(self.canvas))
			self.clipboard_clear()
			self.clipboard_append(text)
			self._set_status("ASCII copied to clipboard")
		except Exception as exc:
			messagebox.showerror("ASCII", str(exc))

	def fill_all(self) -> None:
		try:
			self.canvas.fill(self._current_color())
			self._mark_dirty()
			self._redraw()
		except ValueError as exc:
			messagebox.showerror("Fill", str(exc))

	def clear_all(self) -> None:
		self.canvas.fill(TRANSPARENT)
		self._mark_dirty()
		self._redraw()
		self._set_status("Cleared")

	def apply_ops_panel(self) -> None:
		try:
			data = load_ops_doc(self.ops_text.get("1.0", tk.END))
			apply_ops(self.canvas, data.get("ops", []))
			self._mark_dirty()
			self._redraw()
			self._set_status(f"Applied {len(data.get('ops', []))} ops")
		except Exception as exc:
			messagebox.showerror("Ops", str(exc))

	def _on_close(self) -> None:
		if self._confirm_discard():
			self.destroy()


def main() -> int:
	app = PixelDrawerApp()
	app.mainloop()
	return 0


if __name__ == "__main__":
	raise SystemExit(main())
