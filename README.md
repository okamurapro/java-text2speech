RECAIUS 音声合成APIサンプルアプリケーション(Java版)
====

このプロジェクトは、RECAIUSの音声合成APIの利用方法を理解するためのサンプルアプリケーションです。

## Description
このプロジェクトのアプリケーションを動作させる場合は、事前にRECAIUS APIのディベロッパー登録が必要です。
登録は、[こちらのサイト](https://developer.recaius.io/jp/top.html)から行えます。

ディレクトリの構成は下記のとおりです。

```
README.md          //
TextToSpeech.java  // RECAIUS APIを呼び出すJavaプログラム                
lib/               // Javaプログラムが利用するライブラリ
start.bat          // サンプルを起動するbatchファイル（Windows用）
start.sh           // サンプルを起動するbashファイル（Mac用）
```

## Requirement

JDK 1.8.0_25で動作確認をしています。
JDKのダウンロードは[こちらから](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)行えます。

## Usage & Install

### Mac(OS X)

1. start.shの下記の部分にデベロッパー登録時に払い出されるサービス利用IDとパスワードを設定して下さい

```
export RECAIUS_TTS_ID=音声合成サービス利用ID
export RECAIUS_TTS_PASSWORD=音声合成サービス利用パスワード
```

2. bashファイルを実行します

```
start.sh
```

### Windows

1. start.batの下記の部分にデベロッパー登録時に払い出されるサービス利用IDとパスワードを設定して下さい

```
set RECAIUS_TTS_ID=音声合成サービス利用ID
set RECAIUS_TTS_PASSWORD=音声合成サービス利用パスワード
```

2. batファイルを実行します

```
start.bat
```

## Licence
* MIT
    * This software is released under the MIT License, see LICENSE.txt.
本プログラムを利用して宣伝などを行う場合、[こちらのサイト](https://developer.recaius.io/jp/contact.html)からご連絡ください。

## Author

[recaius-dev-jp](https://github.com/recaius-dev-jp)
    
