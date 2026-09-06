# 十四键选字：首个可接入原型

这个原型只解决十四键的候选筛选闭环，不改变万象方案、词库、模型或视觉主题。

## 用户可见流程

1. 正常十四键有候选时，点候选栏的 `选字`。
2. 同文保存当前输入串，自动送入万象 Pro 的反查前缀 `` ` ``，切到短暂的 `14select` 键盘。
3. 用户可追加 `7 / 8 / 9 / 0`（一、二、三、四声）以及 `横 / 竖 / 撇 / 捺 / 折`。
4. 点候选后，同文自动回到进入前的十四键；点 `取消` 则恢复进入前的原始拼音串，也回到原键盘。

这里的笔形分别向万象发送 `h / s / p / n / z`。它们是万象 Pro 反查 `hspzn` 的实际编码；界面只显示中文笔形。点划按万象规则归入“捺”。

## 主题包接入契约

下列片段由后续主题包并入其 `behavior.yaml`。`wanxiang_14jian` 和 `wanxiang_14select` 是示例名称；发布包会统一实际名称。它们不是让用户手输反引号的替代方案。

```yaml
preset_keys:
  Select14:
    label: 选字
    send: FUNCTION
    command: enter_14key_selection
    option: wanxiang_14select
  Cancel14Select:
    label: 取消
    send: FUNCTION
    command: cancel_14key_selection

preset_keyboards:
  wanxiang_14select:
    ascii_mode: 0
    lock: true
    keys:
      - { click: '7', label: '一声' }
      - { click: '8', label: '二声' }
      - { click: '9', label: '三声' }
      - { click: '0', label: '四声' }
      - { click: 'h', label: '横' }
      - { click: 's', label: '竖' }
      - { click: 'p', label: '撇' }
      - { click: 'n', label: '捺' }
      - { click: 'z', label: '折' }
      - { click: BackSpace, label: '退格' }
      - { click: Cancel14Select }
```

`Select14` 应放在候选栏、有候选时才显示；不能放在普通十四键的字母区。`14select` 保留候选栏的直接点选，因此笔形/声调筛不够时不需要再开第二层菜单。

## 原型边界和验收

- 只对已有组合、已有候选有效；空输入不切换。
- 自动插入的是万象 Pro 的反查前缀，筛选工作仍由万象完成；同文不猜字、不重排候选。
- 提交候选后只恢复到本次进入前的具体键盘，不改用户的方案/布局偏好。
- 取消会清除临时反查和条件，重放原输入串；不会把原拼音或已上屏文字删除。
- 当前原型不实现 `/` 原码层、句内定位层、完整小鹤形码或删除策略；它们仍分别属于 M2 的后续子任务。
