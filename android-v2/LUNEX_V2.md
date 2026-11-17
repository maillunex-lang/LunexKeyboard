# Lunex Keyboard 2.0

Personal Android IME using the independent GPL-3.0 source of CleverKeys (https://github.com/tribixbite/CleverKeys), not proprietary Yandex or Apple assets. Original license notices remain.

Russian and English layouts are enabled by default with a dedicated globe layout-switch key, language switching on the spacebar, a number row, local clipboard history and six glass-inspired themes. The original CleverKeys CTC+geometric swipe code is retained. The Russian language pack (dictionary + CTC model) is shipped in assets and installed with the upstream validated importer at first use, offline.

Glass is a simulated translucent appearance with high-contrast keys, not guaranteed blurring of the app behind the keyboard. The package ai.lunex.keyboard.v2.debug installs alongside the old debug HeliBoard fork.

Build using JDK17 and Android SDK 34: gradlew assembleDebug. Test on a physical Android for language switch, Russian/English swipe, clipboard, settings, landscape, secure fields and theme legibility.
