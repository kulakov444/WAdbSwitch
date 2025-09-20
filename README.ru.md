<!--
    SPDX-FileCopyrightText: NONE

    SPDX-License-Identifier: Unlicense
-->
[en](README.md)|ru

<p align="center">
    <img src="./icon.png">
</p>

<h1 align="center">WAdbSwitch</h1>

Это приложение позволяет включать и отключать отладку по wifi через [android broadcast](https://developer.android.com/develop/background-work/background-tasks/broadcasts).

Приложение было создано для эмуляторов терминалов (таких как [termux](https://github.com/termux/termux-app)), которые не могут менять состояние отладки по wifi напрямую или через `cmd settings` (так как для этого требуется `android.permission.MANAGE_USERS`)

Отладку по wifi можно включить вручную, через быстрые настройки, если эта настройка включена в меню `Для разработчиков > Элементы в быстрых настройках`

## Загрузка

[<img src="badges/get-it-on-codeberg.png" alt="Get it on Codeberg" height="96">](https://codeberg.org/kulakov444/WAdbSwitch/releases)
[<img src="badges/get-it-on-github.png" alt="Get it on GitHub" height="96">](https://github.com/kulakov444/WAdbSwitch/releases)

### Проверка

Подлинность скачанного apk можно проверить способами ниже.

#### [apksigner](https://developer.android.com/studio/command-line/apksigner#usage-verify)

```shell
apksigner verify --print-certs WAdbSwitch-*.*.*.apk
```

SHA256 сертификата дожен быть `62c93a6a70c86129d0c8652fd9abca7826f9a11aeaf0ceb2591004606284cc53`

#### [AppVerifier](https://github.com/soupslurpr/AppVerifier)

Данные для проверки:

```
name.kulakov444.wadbswitch
62:C9:3A:6A:70:C8:61:29:D0:C8:65:2F:D9:AB:CA:78:26:F9:A1:1A:EA:F0:CE:B2:59:10:04:60:62:84:CC:53
```

## Установка

Приложение поддерживает Android 11 или выше, так как отладку по wifi нельзя было включить до этого (без `adb tcpip`).

Приложению требуется `android.permission.WRITE_SECURE_SETTINGS` для работы.

Разрешение можно выдать через `adb shell`:

```shell
pm grant name.kulakov444.wadbswitch android.permission.WRITE_SECURE_SETTINGS
```

## Использование

Приложение использует 128 битный флаг закодированный в base64 вместо разрешений android, так как это бы требовало отправителя broadcast объявлять разрешение в манифесте.

broadcast должен быть отправлен пакету `name.kulakov444.wadbswitch` с действием `name.kulakov444.wadbswitch.SWITCH` и двумя extra:

- `flag` Должно совпадать со 128 битным ключом сгенерированным приложением.
- `value` состояние отладки по wifi

Через am:

```shell
PACKAGE=name.kulakov444.wadbswitch
am broadcast
    -a $PACKAGE.SWITCH \
    --es flag $FLAG    \
    --ei value $1      \
    $PACKAGE           \

unset PACKAGE
```

В kotlin:

```kotlin
private fun setWAdb(flag: String, value: Int) {
    val `package` = "name.kulakov444.wadbswitch"
    val intent = Intent()
    intent.action = `package`
    intent.putExtra("flag", flag)
    intent.putExtra("value", value)
    intent.`package` = `package`
    sendBroadcast(intent)
}

```

В java:

```java
private void setWAdb(String flag, int value) {
    String p = "name.kulakov444.wadbswitch";
    Intent intent = new Intent();
    intent.setAction(p + ".SWITCH");
    intent.putExtra("flag", flag);
    intent.putExtra("value", value);
    intent.setPackage(p);
    sendBroadcast(intent);
}

```

## Сборка

Чтобы собрать debug apk запустите `assembleDebug` gradle задачу.

Чтобы собрать release apk запустите `assembleRelease` gradle задачу.

