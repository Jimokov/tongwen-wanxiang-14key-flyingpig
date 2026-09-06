# 十四键 `/` 原码层：首个可接入原型

万象 Pro 原生以 `/` 开头识别命令。例如 `/flypy` 切换小鹤、`/pinyin` 切回全拼、`/rc...` 调用日历时间、`/sym...` 与 `/emoji...` 查询符号。十四键的问题不是万象缺少这些命令，而是 `/` 后面的字母仍被折叠，令 `/rc` 变成了错误编码。

## 用户可见流程

1. 普通十四键、且没有未完成拼音时，点候选栏的 `/` 命令按钮。
2. 同文自动送入 `/`，切到临时完整 26 键层；屏幕可用短标记显示“命令”。
3. 输入 `rc`、`flypy`、`sym.arrow` 等万象原生代码，直接点候选或按当前输入框的完成键。
4. 命令上屏后自动回进入前的十四键；点 `取消` 会丢弃整个尚未提交的命令并返回。

有未完成的中文组合时，按钮不进入命令层。这是有意的保护：不把正在输入的中文静默清掉。用户先选候选或清空组合，再执行命令。

## 主题包接入契约

```yaml
preset_keys:
  RawCommand14:
    label: '/'
    send: FUNCTION
    command: enter_14key_raw_command
    option: wanxiang_14command
  Cancel14Command:
    label: 取消
    send: FUNCTION
    command: cancel_14key_raw_command

preset_keyboards:
  wanxiang_14command:
    __include: /preset_keyboards/default
    ascii_mode: 0
    lock: true
    # 在该键盘的底栏增加：{ click: Cancel14Command, label: 取消 }
```

`RawCommand14` 应位于候选栏或工具面板，不占十四键的拼音主键位。`wanxiang_14command` 是临时层而不是另一个用户可选布局；它保持当前万象方案，因此 `/flypy` 等命令仍由万象处理。

## 验收边界

- `/rc`、`/flypy`、`/pinyin` 和 `/sym...` 可输入完整原始字母，绝不经过十四键折叠映射。
- 候选提交或取消后回到发起命令的具体十四键布局。
- 取消不向目标应用提交 `/`，也不改已上屏文字。
- 初版不自动根据退格是否删到空串退出；键盘始终有清晰的 `取消`。退格自动退出可在真机验证 Rime 状态事件后作为小优化加入。
