# NoN-FirstPerson Compat Patch

# 中文/Chinese translation

此模组由DeepSeek-V4.1-Flash大模型制作而成

一个独立的 Fabric 客户端兼容 Mod：当玩家自己参与 **NeedsOfNature** 的动画时，临时让
**FirstPerson** 不再渲染第一人称下的自机身体；动画结束立刻恢复。

* 目标版本：Minecraft 1.21.11/ Fabric Loader ≥ 0.16
* Mod ID：`nonfpcompat`，纯客户端（`environment: client`）
* 不依赖 Fabric API；对 FirstPerson、NeedsOfNature 均无编译期依赖（全部反射接入）

# 行为

| 情况 | 结果 |
|---|---|
| FirstPerson + NeedsOfNature 都在 | 兼容生效；动画期间隐藏第一人称身体，结束后恢复 |
| 少了任意一个 | Mod 正常加载但自动禁用；进入主菜单时弹一次提示窗（可 Esc / 关闭按钮 / ✕ 关掉，每次启动只弹一次） |
| 两个都在但版本对不上（反射找不到入口） | 同样自动禁用并提示「版本不受支持」，不会崩溃 |

# 实现要点

1. 隐藏身体：把动态代理塞进 `FirstPersonAPI.getActivationHandlers()`。
   `LogicHandler.shouldApplyThirdPerson` 每帧遍历这个列表，任一
   `ActivationHandler.preventFirstperson()` 返回 `true` 就退回原版第一人称视角。
2. 判断动画状态：每帧查询
   `ClientAnimationRuntime.isActorPendingOrActive(localPlayer.getUuid())`
   （回退到 `isActorActive`）。它是纯静态查询，覆盖「已排队 / 正在播放」整个窗口，
   因此开始隐藏和结束恢复都是自动的，不需要监听开始/结束事件。
3. 主菜单提示：Mixin `TitleScreen#init` 尾部触发一次，延迟一个 tick 后
   `setScreen(new MissingDepsScreen(...))`，避免在 init 内部重入。

# 构建

```bash
需要 JDK 21 + Gradle 8.14+（本目录用 Loom 1.11.8）
gradle build
产物：build/libs/nonfpcompat-1.0.0.jar
```

# Bug

部分动画第一人称头部显示，并导致的视角穿模问题，并不属于此mod的问题，属于动画本身问题，经过实机测试，在未安装此mod与FirstPerson mod，第一人称动画过程中头部依然显示，并导致的视角穿模

# English/英文翻译

This mod is made with the DeepSeek-V4.1-Flash large language model.
 
An independent Fabric client-side compatible mod: When the player participates in **NeedsOfNature** animations, temporarily disable the **FirstPerson** mod's rendering of the player's own body in first person; restore rendering immediately once the animation finishes.
 
- Target version: Minecraft 1.21.11 / Fabric Loader ≥ 0.16
- Mod ID:  nonfpcompat , client-only ( environment: client )
- No Fabric API dependency; no compile-time dependencies on FirstPerson or NeedsOfNature (all integrations via reflection)
 
# Behaviour
 
| Scenario | Result |
|---|---|
| Both FirstPerson and NeedsOfNature are present | Compatibility takes effect; hide the first-person body during animations and restore it afterward  |
| Either mod is missing | Mod loads normally but disables itself automatically; a popup notification appears once when reaching the main menu (dismissible with Esc / close button / ✕, only shown once per launch)  |
| Both mods are present but version mismatch (reflection fails to find entry points) | Automatically disables itself and shows an "unsupported version" warning, no crashes  |
 
# Implementation Notes
 
1. Body hiding: Inject a dynamic proxy into  FirstPersonAPI.getActivationHandlers() .
 LogicHandler.shouldApplyThirdPerson  iterates over this list every frame. If any
 ActivationHandler.preventFirstperson()  returns  true , the view falls back to vanilla first person.
2. Animation state check: Query every frame via
 ClientAnimationRuntime.isActorPendingOrActive(localPlayer.getUuid()) 
(fallback to  isActorActive ). This is a pure static check covering the full window of "queued / actively playing" animations.
Hiding and restoring happen automatically with no need to hook start/end events.
3. Main menu notification: Triggered at the end of Mixin  TitleScreen#init , delayed by one tick before calling
 setScreen(new MissingDepsScreen(...))  to avoid reentrancy inside init.
 
# Build

```bash
Requires JDK 21 + Gradle 8.14+ (this project uses Loom 1.11.8)
gradle build
Output artifact: build/libs/nonfpcompat-1.0.0.jar
```

# Bug

Some animations show the head in first person, causing view clipping issues. This is not a problem with this mod, but a problem with the animations themselves.After in-game testing, without this mod and the FirstPerson mod installed, the head still shows during first-person animations, causing view clipping.
