### NotchCompat Android 刘海屏工具类
默认Android O以上才有刘海屏，基本规则为Android O调用各厂商提供的API判断，Android P以上调用官方API判断
目前已适配小米、华为、OPPO、VIVO

##### 使用
- 判断屏幕是否为凹口屏
```NotchCompat.hasDisplayCutout(@NonNull Window window)```
- 获取刘海屏尺寸
```NotchCompat.getDisplayCutoutSize(@NonNull Window window)```
返回一个List<Rect>，因为可能存在多个刘海。
  目前华为、OPPO、VIVO会返回正确的结果，小米的返回结果只有高度可信
- 设置Activity不使用刘海屏区域
```NotchCompat.blockDisplayCutout(@NonNull Window window)```
- 设置Activity使用刘海屏区域
```NotchCompat.immersiveDisplayCutout(@NonNull Window window)```