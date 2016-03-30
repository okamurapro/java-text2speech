@echo off

rem Setting Environment Variable
set RECAIUS_TTS_ID=音声合成サービス利用ID
set RECAIUS_TTS_PASSWORD=音声合成サービス利用パスワード

rem Compile TextToSpeech.java
javac -encoding UTF-8 -classpath lib/\* TextToSpeech.java

rem Run TextToSpeech
java -cp .;lib/* TextToSpeech
   
rem "Delete TextToSpeech"
del TextToSpeech.class
