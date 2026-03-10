# -*- coding: utf-8 -*-
"""
Windows 防休眠工具
每隔指定分钟按两次大写锁定键防止系统休眠
配置保存在注册表中，不产生任何额外文件
"""

import ctypes
import ctypes.wintypes
import sys
import time
import threading
import tkinter as tk
from tkinter import ttk

# Windows only
if sys.platform != "win32":
    print("此工具仅支持 Windows 系统")
    sys.exit(1)

import winreg

# ============================================================
# 常量
# ============================================================
APP_NAME = "AntiSleep"
REG_PATH = r"Software\AntiSleep"
DEFAULT_INTERVAL = 30  # 分钟
DEFAULT_KEY_DELAY = 50  # 毫秒
KEY_DELAY_OPTIONS = [50, 100, 300]

VK_CAPITAL = 0x14
KEYEVENTF_KEYUP = 0x0002
INPUT_KEYBOARD = 1


# ============================================================
# Windows API 结构体
# ============================================================
class MOUSEINPUT(ctypes.Structure):
    _fields_ = [
        ("dx", ctypes.wintypes.LONG),
        ("dy", ctypes.wintypes.LONG),
        ("mouseData", ctypes.wintypes.DWORD),
        ("dwFlags", ctypes.wintypes.DWORD),
        ("time", ctypes.wintypes.DWORD),
        ("dwExtraInfo", ctypes.POINTER(ctypes.c_ulong)),
    ]


class KEYBDINPUT(ctypes.Structure):
    _fields_ = [
        ("wVk", ctypes.wintypes.WORD),
        ("wScan", ctypes.wintypes.WORD),
        ("dwFlags", ctypes.wintypes.DWORD),
        ("time", ctypes.wintypes.DWORD),
        ("dwExtraInfo", ctypes.POINTER(ctypes.c_ulong)),
    ]


class HARDWAREINPUT(ctypes.Structure):
    _fields_ = [
        ("uMsg", ctypes.wintypes.DWORD),
        ("wParamL", ctypes.wintypes.WORD),
        ("wParamH", ctypes.wintypes.WORD),
    ]


class INPUT(ctypes.Structure):
    class _INPUT(ctypes.Union):
        _fields_ = [
            ("mi", MOUSEINPUT),
            ("ki", KEYBDINPUT),
            ("hi", HARDWAREINPUT),
        ]

    _fields_ = [
        ("type", ctypes.wintypes.DWORD),
        ("_input", _INPUT),
    ]


# ============================================================
# 注册表配置
# ============================================================
def load_config():
    """从注册表读取配置"""
    interval = DEFAULT_INTERVAL
    key_delay = DEFAULT_KEY_DELAY
    try:
        key = winreg.OpenKey(winreg.HKEY_CURRENT_USER, REG_PATH)
        interval, _ = winreg.QueryValueEx(key, "Interval")
        try:
            key_delay, _ = winreg.QueryValueEx(key, "KeyDelay")
        except FileNotFoundError:
            pass
        winreg.CloseKey(key)
    except FileNotFoundError:
        pass
    if interval < 1:
        interval = DEFAULT_INTERVAL
    if key_delay not in KEY_DELAY_OPTIONS:
        key_delay = DEFAULT_KEY_DELAY
    return interval, key_delay


def save_config(interval, key_delay):
    """保存配置到注册表"""
    key = winreg.CreateKey(winreg.HKEY_CURRENT_USER, REG_PATH)
    winreg.SetValueEx(key, "Interval", 0, winreg.REG_DWORD, interval)
    winreg.SetValueEx(key, "KeyDelay", 0, winreg.REG_DWORD, key_delay)
    winreg.CloseKey(key)


# ============================================================
# 模拟按键
# ============================================================
def press_capslock(delay_ms):
    """按下并释放一次 Caps Lock"""
    extra = ctypes.c_ulong(0)

    inp_down = INPUT()
    inp_down.type = INPUT_KEYBOARD
    inp_down._input.ki.wVk = VK_CAPITAL
    inp_down._input.ki.wScan = 0
    inp_down._input.ki.dwFlags = 0
    inp_down._input.ki.time = 0
    inp_down._input.ki.dwExtraInfo = ctypes.pointer(extra)

    inp_up = INPUT()
    inp_up.type = INPUT_KEYBOARD
    inp_up._input.ki.wVk = VK_CAPITAL
    inp_up._input.ki.wScan = 0
    inp_up._input.ki.dwFlags = KEYEVENTF_KEYUP
    inp_up._input.ki.time = 0
    inp_up._input.ki.dwExtraInfo = ctypes.pointer(extra)

    ctypes.windll.user32.SendInput(1, ctypes.pointer(inp_down), ctypes.sizeof(INPUT))
    time.sleep(delay_ms / 1000.0)
    ctypes.windll.user32.SendInput(1, ctypes.pointer(inp_up), ctypes.sizeof(INPUT))


def toggle_capslock_twice(delay_ms):
    """按两次 Caps Lock（开关一次，状态恢复）"""
    press_capslock(delay_ms)
    time.sleep(delay_ms / 1000.0)
    press_capslock(delay_ms)


# ============================================================
# 托盘图标（pystray）
# ============================================================
def create_tray_image():
    """用 Pillow 生成一个简单的托盘图标"""
    from PIL import Image, ImageDraw
    img = Image.new("RGBA", (64, 64), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    # 绿色圆形底
    draw.ellipse([4, 4, 60, 60], fill=(34, 139, 34, 255))
    # 白色 Z
    draw.text((20, 12), "Z", fill=(255, 255, 255, 255))
    draw.text((28, 24), "z", fill=(255, 255, 255, 255))
    draw.text((36, 36), "z", fill=(200, 200, 200, 255))
    return img


# ============================================================
# 主应用
# ============================================================
class AntiSleepApp:
    def __init__(self):
        self.interval, self.key_delay = load_config()
        self.running = True
        self.paused = False
        self.remaining_seconds = self.interval * 60
        self.stop_event = threading.Event()
        self.tray_icon = None

        self._build_gui()
        self._start_worker()
        self._start_initial_test()
        self._start_countdown_updater()

    # ---- GUI ----
    def _build_gui(self):
        self.root = tk.Tk()
        self.root.title("Windows 防休眠工具")
        self.root.resizable(False, False)
        self.root.protocol("WM_DELETE_WINDOW", self._minimize_to_tray)

        frame = ttk.Frame(self.root, padding=20)
        frame.grid()

        # 标题
        ttk.Label(frame, text="Windows 防休眠工具", font=("Microsoft YaHei", 14, "bold")).grid(
            row=0, column=0, columnspan=3, pady=(0, 15)
        )

        # 间隔设置
        ttk.Label(frame, text="触发间隔:").grid(row=1, column=0, sticky="e", padx=5)
        self.interval_var = tk.StringVar(value=str(self.interval))
        interval_entry = ttk.Entry(frame, textvariable=self.interval_var, width=8, justify="center")
        interval_entry.grid(row=1, column=1, padx=5)
        ttk.Label(frame, text="分钟").grid(row=1, column=2, sticky="w")

        # 按键间隔
        ttk.Label(frame, text="按键间隔:").grid(row=2, column=0, sticky="e", padx=5, pady=(10, 0))
        self.delay_var = tk.StringVar(value=f"{self.key_delay}ms")
        delay_combo = ttk.Combobox(
            frame, textvariable=self.delay_var, width=8, state="readonly", justify="center",
            values=[f"{d}ms" for d in KEY_DELAY_OPTIONS],
        )
        delay_combo.grid(row=2, column=1, padx=5, pady=(10, 0))
        ttk.Label(frame, text="").grid(row=2, column=2)

        # 保存按钮
        ttk.Button(frame, text="保存配置", command=self._save_settings).grid(
            row=3, column=0, columnspan=3, pady=10
        )

        # 状态
        self.status_var = tk.StringVar(value="● 运行中")
        status_label = ttk.Label(frame, textvariable=self.status_var, font=("Microsoft YaHei", 11))
        status_label.grid(row=4, column=0, columnspan=3, pady=5)

        # 倒计时
        self.countdown_var = tk.StringVar(value="")
        ttk.Label(frame, textvariable=self.countdown_var, font=("Consolas", 12)).grid(
            row=5, column=0, columnspan=3, pady=5
        )

        # 按钮行
        btn_frame = ttk.Frame(frame)
        btn_frame.grid(row=6, column=0, columnspan=3, pady=(10, 0))

        self.pause_btn = ttk.Button(btn_frame, text="暂停", command=self._toggle_pause)
        self.pause_btn.pack(side="left", padx=5)

        ttk.Button(btn_frame, text="最小化到托盘", command=self._minimize_to_tray).pack(side="left", padx=5)

        ttk.Button(btn_frame, text="退出", command=self._quit).pack(side="left", padx=5)

    def _save_settings(self):
        try:
            val = int(self.interval_var.get())
            if val < 1:
                val = 1
            self.interval = val
        except ValueError:
            self.interval_var.set(str(self.interval))
            return

        delay_str = self.delay_var.get().replace("ms", "")
        try:
            delay_val = int(delay_str)
            if delay_val in KEY_DELAY_OPTIONS:
                self.key_delay = delay_val
        except ValueError:
            pass

        save_config(self.interval, self.key_delay)
        self.remaining_seconds = self.interval * 60
        self.interval_var.set(str(self.interval))

    def _toggle_pause(self):
        self.paused = not self.paused
        if self.paused:
            self.status_var.set("● 已暂停")
            self.pause_btn.config(text="恢复")
        else:
            self.remaining_seconds = self.interval * 60
            self.status_var.set("● 运行中")
            self.pause_btn.config(text="暂停")
        self._update_tray_menu()

    def _minimize_to_tray(self):
        self.root.withdraw()
        if self.tray_icon is None:
            self._create_tray()

    def _show_window(self):
        self.root.deiconify()
        self.root.lift()
        self.root.focus_force()

    def _create_tray(self):
        try:
            import pystray
        except ImportError:
            # 如果没有 pystray，退回到仅隐藏窗口
            return

        def on_show(icon, item):
            self.root.after(0, self._show_window)

        def on_pause(icon, item):
            self.root.after(0, self._toggle_pause)

        def on_quit(icon, item):
            self.root.after(0, self._quit)

        self.tray_icon = pystray.Icon(
            APP_NAME,
            create_tray_image(),
            "防休眠工具",
            menu=pystray.Menu(
                pystray.MenuItem("显示窗口", on_show, default=True),
                pystray.MenuItem(
                    lambda item: "恢复" if self.paused else "暂停",
                    on_pause,
                ),
                pystray.MenuItem("退出", on_quit),
            ),
        )
        threading.Thread(target=self.tray_icon.run, daemon=True).start()

    def _update_tray_menu(self):
        if self.tray_icon is not None:
            try:
                self.tray_icon.update_menu()
            except Exception:
                pass

    # ---- 工作线程 ----
    def _start_worker(self):
        def worker():
            while not self.stop_event.is_set():
                if not self.paused:
                    if self.remaining_seconds <= 0:
                        toggle_capslock_twice(self.key_delay)
                        self.remaining_seconds = self.interval * 60
                    else:
                        self.remaining_seconds -= 1
                self.stop_event.wait(1)

        t = threading.Thread(target=worker, daemon=True)
        t.start()

    def _start_initial_test(self):
        """启动 3 秒后自动执行一次，用于验证功能"""
        def test():
            time.sleep(3)
            if not self.stop_event.is_set():
                toggle_capslock_twice(self.key_delay)

        threading.Thread(target=test, daemon=True).start()

    def _start_countdown_updater(self):
        def update():
            if self.paused:
                self.countdown_var.set("已暂停")
            else:
                mins, secs = divmod(self.remaining_seconds, 60)
                self.countdown_var.set(f"下次触发: {mins:02d}:{secs:02d}")
            self.root.after(500, update)

        update()

    # ---- 退出 ----
    def _quit(self):
        self.stop_event.set()
        if self.tray_icon is not None:
            try:
                self.tray_icon.stop()
            except Exception:
                pass
        self.root.quit()
        self.root.destroy()

    def run(self):
        self.root.mainloop()


if __name__ == "__main__":
    app = AntiSleepApp()
    app.run()
