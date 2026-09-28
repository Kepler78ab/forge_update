package com.example.forgecraft.survival.smelt;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Pure mix engine — materials → linear RGB color + hardness + output id.
 * No Minecraft types; safe to unit-test.
 */
public final class SmeltEngine {
	/** Max-hardness blend factor α (doc §四). */
	public static final float HARDNESS_ALPHA = 0.3f;
	/** Impurity hardness penalty coefficient. */
	public static final float IMPURITY_PENALTY = 0.5f;
	/** Saturation floor after mix (doc §三 optional). */
	public static final float MIN_SATURATION = 0.18f;

	private SmeltEngine() {
	}

	/**
	 * @param composition materialId → positive amount (relative liters / weights)
	 */
	public static SmeltResult smelt(Map<String, Integer> composition) {
		if (composition == null || composition.isEmpty()) {
			return new SmeltResult(120, 120, 120, 1.0f, SmeltOutputs.map(1.0f));
		}

		List<Entry> entries = new ArrayList<>();
		int totalAmount = 0;
		for (Map.Entry<String, Integer> e : composition.entrySet()) {
			if (e.getValue() == null || e.getValue() <= 0) {
				continue;
			}
			SmeltMaterial material = SmeltMaterials.get(e.getKey());
			if (material == null) {
				continue;
			}
			entries.add(new Entry(material, e.getValue()));
			totalAmount += e.getValue();
		}
		if (entries.isEmpty() || totalAmount <= 0) {
			return new SmeltResult(120, 120, 120, 1.0f, SmeltOutputs.map(1.0f));
		}

		int[] override = ColorOverrideTable.lookup(composition);
		int r;
		int g;
		int b;
		if (override != null) {
			r = override[0];
			g = override[1];
			b = override[2];
		} else {
			float[] rgb = mixLinearRgb(entries, totalAmount);
			rgb = ensureMinSaturation(rgb);
			r = clampByte(Math.round(rgb[0]));
			g = clampByte(Math.round(rgb[1]));
			b = clampByte(Math.round(rgb[2]));
		}

		float hardness = mixHardness(entries, totalAmount);
		String outputId = SmeltOutputs.map(hardness);
		return new SmeltResult(r, g, b, hardness, outputId);
	}

	private static float[] mixLinearRgb(List<Entry> entries, int totalAmount) {
		double sumW = 0.0;
		double lr = 0.0;
		double lg = 0.0;
		double lb = 0.0;
		for (Entry entry : entries) {
			double p = entry.amount / (double) totalAmount;
			double w = entry.material.weight() * p;
			sumW += w;
			lr += w * srgbToLinear(entry.material.r());
			lg += w * srgbToLinear(entry.material.g());
			lb += w * srgbToLinear(entry.material.b());
		}
		if (sumW <= 0.0) {
			return new float[]{120, 120, 120};
		}
		return new float[]{
				linearToSrgb(lr / sumW),
				linearToSrgb(lg / sumW),
				linearToSrgb(lb / sumW)
		};
	}

	private static float mixHardness(List<Entry> entries, int totalAmount) {
		double sumW = 0.0;
		double hBase = 0.0;
		float hMax = 0.0f;
		double impurityRatio = 0.0;
		for (Entry entry : entries) {
			double p = entry.amount / (double) totalAmount;
			double w = entry.material.weight() * p;
			sumW += w;
			hBase += w * entry.material.hardness();
			hMax = Math.max(hMax, entry.material.hardness());
			if (entry.material.category() == MaterialCategory.IMPURITY) {
				impurityRatio += p;
			}
		}
		if (sumW <= 0.0) {
			return 1.0f;
		}
		float base = (float) (hBase / sumW);
		float mixed = base * (1.0f - HARDNESS_ALPHA) + hMax * HARDNESS_ALPHA;
		mixed *= (float) (1.0 - impurityRatio * IMPURITY_PENALTY);
		return Math.max(0.0f, mixed);
	}

	/** Keep hue, lift saturation if muddy. */
	private static float[] ensureMinSaturation(float[] rgb) {
		float[] hsv = rgbToHsv(rgb[0] / 255f, rgb[1] / 255f, rgb[2] / 255f);
		if (hsv[1] < MIN_SATURATION && hsv[2] > 0.05f) {
			hsv[1] = MIN_SATURATION;
			float[] out = hsvToRgb(hsv[0], hsv[1], hsv[2]);
			return new float[]{out[0] * 255f, out[1] * 255f, out[2] * 255f};
		}
		return rgb;
	}

	private static double srgbToLinear(int c) {
		double s = c / 255.0;
		return Math.pow(s, 2.2);
	}

	private static float linearToSrgb(double linear) {
		return (float) (Math.pow(Math.max(0.0, linear), 1.0 / 2.2) * 255.0);
	}

	private static int clampByte(int v) {
		return Math.max(0, Math.min(255, v));
	}

	private static float[] rgbToHsv(float r, float g, float b) {
		float max = Math.max(r, Math.max(g, b));
		float min = Math.min(r, Math.min(g, b));
		float delta = max - min;
		float h = 0f;
		if (delta > 1e-6f) {
			if (max == r) {
				h = ((g - b) / delta) % 6f;
			} else if (max == g) {
				h = (b - r) / delta + 2f;
			} else {
				h = (r - g) / delta + 4f;
			}
			h /= 6f;
			if (h < 0f) {
				h += 1f;
			}
		}
		float s = max <= 1e-6f ? 0f : delta / max;
		return new float[]{h, s, max};
	}

	private static float[] hsvToRgb(float h, float s, float v) {
		float c = v * s;
		float x = c * (1f - Math.abs((h * 6f) % 2f - 1f));
		float m = v - c;
		float rp;
		float gp;
		float bp;
		float h6 = h * 6f;
		if (h6 < 1f) {
			rp = c;
			gp = x;
			bp = 0f;
		} else if (h6 < 2f) {
			rp = x;
			gp = c;
			bp = 0f;
		} else if (h6 < 3f) {
			rp = 0f;
			gp = c;
			bp = x;
		} else if (h6 < 4f) {
			rp = 0f;
			gp = x;
			bp = c;
		} else if (h6 < 5f) {
			rp = x;
			gp = 0f;
			bp = c;
		} else {
			rp = c;
			gp = 0f;
			bp = x;
		}
		return new float[]{rp + m, gp + m, bp + m};
	}

	/** Normalize composition so values sum to {@code liters} (or keep ratios if liters matches). */
	public static Map<String, Integer> scaleToLiters(Map<String, Integer> composition, int liters) {
		Map<String, Integer> cleaned = new LinkedHashMap<>();
		int sum = 0;
		for (Map.Entry<String, Integer> e : composition.entrySet()) {
			if (e.getValue() != null && e.getValue() > 0 && SmeltMaterials.contains(e.getKey())) {
				cleaned.put(e.getKey(), e.getValue());
				sum += e.getValue();
			}
		}
		if (cleaned.isEmpty() || liters <= 0) {
			return Map.of();
		}
		if (sum == liters) {
			return cleaned;
		}
		Map<String, Integer> scaled = new LinkedHashMap<>();
		int assigned = 0;
		List<String> keys = new ArrayList<>(cleaned.keySet());
		for (int i = 0; i < keys.size(); i++) {
			String id = keys.get(i);
			if (i == keys.size() - 1) {
				scaled.put(id, Math.max(1, liters - assigned));
			} else {
				int part = Math.max(1, Math.round(cleaned.get(id) * (float) liters / sum));
				scaled.put(id, part);
				assigned += part;
			}
		}
		return scaled;
	}

	private record Entry(SmeltMaterial material, int amount) {
	}
}
