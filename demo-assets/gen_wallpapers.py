# gen_wallpapers.py
# pip install pillow numpy
from PIL import Image, ImageFilter
import numpy as np
import os, math, random, colorsys, hashlib, logging
from dataclasses import dataclass

# Setup logging
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')

# ---- Config ----
ARTISTS = {
    "indigo":      {"h": 230/360, "s": 0.64, "v": 0.71},
    "light_green": {"h": 115/360, "s": 0.50, "v": 0.85},
    "dark_green":  {"h": 145/360, "s": 0.75, "v": 0.65},
    "purple":      {"h": 275/360, "s": 0.55, "v": 0.85},
    "pink":        {"h": 330/360, "s": 0.55, "v": 0.90},
    "sky_blue":    {"h": 195/360, "s": 0.60, "v": 0.90},
    "red":         {"h": 0/360,   "s": 0.70, "v": 0.85},
    "teal":        {"h": 170/360, "s": 0.55, "v": 0.85},
    "magenta":     {"h": 315/360, "s": 0.60, "v": 0.85},
    "dark":        {"h": 0/360,   "s": 0.05, "v": 0.15},
}
STYLES = ["linear", "radial", "softnoise", "stripes", "checkers"]  # 5 styles
COUNT_PER_ARTIST = 20
SIZES = [
    # ("desktop", 5120, 2880),
    # ("phone",   1200, 2600),
    ("phone",   950, 720),
    # ("tablet",  2732, 2048),
    # ("square",  3000, 3000),
]
OUTDIR = "wallpapers"
GRAIN = 0.02  # subtle grain to avoid banding (0..~0.05)

# ---- Utilities ----
def hsv_to_rgb8(h,s,v):
    r,g,b = colorsys.hsv_to_rgb(h%1.0, max(0,min(1,s)), max(0,min(1,v)))
    return (int(r*255), int(g*255), int(b*255))

def shift_hsv(base, dh=0.0, ds=0.0, dv=0.0):
    return {
        "h": (base["h"] + dh) % 1.0,
        "s": max(0.0, min(1.0, base["s"] + ds)),
        "v": max(0.0, min(1.0, base["v"] + dv)),
    }

def prng(seed_str):
    h = int(hashlib.sha256(seed_str.encode()).hexdigest(), 16)
    rnd = random.Random(h)
    return rnd

def bias_toward(hero_rgb, img_np, alpha=0.25):
    # Mix each pixel toward hero color (alpha small to maintain variety)
    hr, hg, hb = hero_rgb
    mix = img_np*(1-alpha) + np.array([hr, hg, hb], dtype=np.float32)*alpha
    return np.clip(mix, 0, 255)

# ---- Style generators (NumPy/Pillow) ----
def make_linear(w,h,c0,c1,angle_deg,rnd):
    # Create coordinate ramp along angle and lerp c0->c1
    ang = math.radians(angle_deg)
    dx, dy = math.cos(ang), math.sin(ang)
    xs = np.linspace(-0.5, 0.5, w, dtype=np.float32)
    ys = np.linspace(-0.5, 0.5, h, dtype=np.float32)
    X, Y = np.meshgrid(xs, ys)
    t = (X*dx + Y*dy) + 0.5
    t = np.clip(t, 0, 1)
    c0 = np.array(c0, dtype=np.float32)
    c1 = np.array(c1, dtype=np.float32)
    img = (1-t[...,None])*c0 + t[...,None]*c1
    return img

def make_radial(w,h,c_center,c_edge,rnd):
    xs = np.linspace(-1, 1, w, dtype=np.float32)
    ys = np.linspace(-1, 1, h, dtype=np.float32)
    X, Y = np.meshgrid(xs, ys)
    r = np.sqrt(X*X + Y*Y)
    r = np.clip(r, 0, 1)
    c0 = np.array(c_center, dtype=np.float32)
    c1 = np.array(c_edge, dtype=np.float32)
    img = (1-r[...,None])*c0 + r[...,None]*c1
    return img

def make_softnoise(w,h,hero_rgb,rnd):
    # Value noise by octave summation, then map to a hero palette
    base = np.zeros((h,w), dtype=np.float32)
    for f, amp in [(4,0.5),(8,0.25),(16,0.15),(32,0.10)]:
        small = rnd.random();  # just to perturb state
        noise = rnd.random()
        grid = rnd.random()
        # actual noise: random array downsampled and bilinear upscaled
        n = rnd.random()
        small = (rnd.random()*0.8+0.2)
        a = np.random.RandomState(rnd.randint(0, 10**9)).randint(0,256,(max(1,h//f), max(1,w//f))) / 255.0
        a = Image.fromarray((a*255).astype(np.uint8)).resize((w,h), Image.BILINEAR)
        base += (np.asarray(a, dtype=np.float32)/255.0) * amp
    base = base - base.min()
    base = base / (base.max()+1e-6)
    # Map grayscale to two hero-related colors
    c0 = np.array(hero_rgb, dtype=np.float32)
    c1 = np.clip(c0 * (rnd.uniform(0.7,0.95)), 0, 255)
    img = (1-base[...,None])*c0 + base[...,None]*c1
    # Heavy blur to create smooth blobs
    pil = Image.fromarray(img.astype(np.uint8), mode="RGB").filter(ImageFilter.GaussianBlur(radius=32))
    return np.asarray(pil, dtype=np.float32)

def make_stripes(w,h,hero_rgb,sec_rgb,rnd):
    img = np.zeros((h,w,3), dtype=np.float32)
    # Random stripe orientation
    ang = math.radians(rnd.choice([0,15,30,45,60,75,90,105,120,135]))
    dx, dy = math.cos(ang), math.sin(ang)
    stripe_w = rnd.randint(80, 220)
    for y in range(h):
        for x in range(w):
            u = (x*dx + y*dy)
            band = int((u/stripe_w)) & 1
            img[y,x] = hero_rgb if band==0 else sec_rgb
    # Optional small blur for polish
    pil = Image.fromarray(img.astype(np.uint8), mode="RGB").filter(ImageFilter.GaussianBlur(radius=2))
    return np.asarray(pil, dtype=np.float32)

def make_checkers(w,h,hero_rgb,sec_rgb,rnd):
    img = np.zeros((h,w,3), dtype=np.float32)
    cw = rnd.randint(60, 180)
    ch = rnd.randint(60, 180)
    for y in range(h):
        for x in range(w):
            cx = (x//cw) & 1
            cy = (y//ch) & 1
            img[y,x] = hero_rgb if (cx ^ cy)==0 else sec_rgb
    pil = Image.fromarray(img.astype(np.uint8), mode="RGB")
    return np.asarray(pil, dtype=np.float32)

def add_grain(img_np, rnd, strength=GRAIN):
    if strength <= 0: return img_np
    noise = (rnd.normalvariate(0,1) * 0)  # advance state
    n = np.random.RandomState(rnd.randint(0, 10**9)).randn(*img_np.shape[:2], 1).astype(np.float32)
    return np.clip(img_np + n*255*strength, 0, 255)

# ---- Main generation per image ----
def generate_one(artist_name, base_hsv, idx):
    rnd = prng(f"{artist_name}:{idx}")
    # Secondary color: small hue shift ±20°, modest s/v tweaks
    dh = rnd.uniform(-20/360, 20/360)
    sec = shift_hsv(base_hsv, dh=dh, ds=rnd.uniform(-0.1,0.1), dv=rnd.uniform(-0.1,0.1))
    hero_rgb = np.array(hsv_to_rgb8(base_hsv["h"], base_hsv["s"], base_hsv["v"]), dtype=np.float32)
    sec_rgb  = np.array(hsv_to_rgb8(sec["h"], sec["s"], sec["v"]), dtype=np.float32)

    style = STYLES[idx % len(STYLES)]
    angle = rnd.choice([15,30,45,60,75,90,105,120,135,150])

    return style, hero_rgb, sec_rgb, angle

def render(style, w, h, hero_rgb, sec_rgb, angle, rnd):
    if style == "linear":
        img = make_linear(w,h, hero_rgb, sec_rgb, angle, rnd)
        img = bias_toward(hero_rgb, img, alpha=0.18)
    elif style == "radial":
        img = make_radial(w,h, hero_rgb, sec_rgb, rnd)
        img = bias_toward(hero_rgb, img, alpha=0.10)
    elif style == "softnoise":
        img = make_softnoise(w,h, hero_rgb, rnd)
        img = bias_toward(hero_rgb, img, alpha=0.10)
    elif style == "stripes":
        img = make_stripes(w,h, hero_rgb, sec_rgb, rnd)
        img = bias_toward(hero_rgb, img, alpha=0.08)
    elif style == "checkers":
        img = make_checkers(w,h, hero_rgb, sec_rgb, rnd)
        img = bias_toward(hero_rgb, img, alpha=0.05)
    else:
        raise ValueError(style)
    img = add_grain(img, rnd, strength=GRAIN)
    return Image.fromarray(img.astype(np.uint8), mode="RGB")

def main():
    os.makedirs(OUTDIR, exist_ok=True)
    
    total_artists = len(ARTISTS)
    total_wallpapers = total_artists * COUNT_PER_ARTIST * len(SIZES)
    logging.info(f"Starting wallpaper generation: {total_artists} artists × {COUNT_PER_ARTIST} designs × {len(SIZES)} sizes = {total_wallpapers} total wallpapers")
    
    wallpaper_count = 0
    
    for artist_idx, (artist, base) in enumerate(ARTISTS.items(), 1):
        logging.info(f"[{artist_idx}/{total_artists}] Processing artist: {artist}")
        artist_dir = os.path.join(OUTDIR, artist)
        os.makedirs(artist_dir, exist_ok=True)
        
        # Track style counters for proper numbering
        style_counters = {style: 0 for style in STYLES}
        
        for i in range(COUNT_PER_ARTIST):
            style, hero_rgb, sec_rgb, angle = generate_one(artist, base, i)
            style_counters[style] += 1
            logging.info(f"  Design {i+1}/{COUNT_PER_ARTIST}: {style} style")
            
            for tag, w, h in SIZES:
                wallpaper_count += 1
                rnd = prng(f"{artist}:{i}:{tag}")
                img = render(style, w, h, hero_rgb, sec_rgb, angle, rnd)
                fn = f"{style.capitalize()} {style_counters[style]:02d}.png"
                filepath = os.path.join(artist_dir, fn)
                img.save(filepath, optimize=True)
                logging.info(f"    [{wallpaper_count}/{total_wallpapers}] Created: {fn} ({w}×{h})")
    
    logging.info(f"Wallpaper generation completed! Created {wallpaper_count} wallpapers in {OUTDIR}/")

if __name__ == "__main__":
    main()