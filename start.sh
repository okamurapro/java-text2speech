#!/bin/bash

: "Setting Environment Variable" && {
   export RECAIUS_TTS_ID=音声合成サービス利用ID
   export RECAIUS_TTS_PASSWORD=音声合成サービス利用パスワード
}
: "Compile TextToSpeech.java" && {
   javac -classpath lib/\* TextToSpeech.java
}
: "Run TextToSpeech" && {
   java -cp './:lib/*' TextToSpeech
}
: "Delete TextToSpeech" && {
   rm -f TextToSpeech.class
}
