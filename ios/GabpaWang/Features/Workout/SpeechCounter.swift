import Foundation
import AVFoundation

/// 운동 카운트를 한국어 음성으로 읽어준다.
/// `WorkoutRunningScreen.kt`의 `TextToSpeech` 사용 로직 포팅.
final class SpeechCounter {
    private let synth = AVSpeechSynthesizer()
    private let voice = AVSpeechSynthesisVoice(language: "ko-KR")

    init() {
        // 운동 중 미디어 재생을 끄지 않도록 ambient 카테고리로 설정.
        let session = AVAudioSession.sharedInstance()
        try? session.setCategory(.ambient, mode: .default, options: [.mixWithOthers, .duckOthers])
        try? session.setActive(true, options: [])
    }

    /// 안드로이드 `QUEUE_FLUSH`와 같은 동작 — 진행 중인 발화는 끊고 새 텍스트로 대체.
    func speak(_ text: String) {
        if synth.isSpeaking {
            synth.stopSpeaking(at: .immediate)
        }
        let utterance = AVSpeechUtterance(string: text)
        utterance.voice = voice
        utterance.rate = AVSpeechUtteranceDefaultSpeechRate
        utterance.pitchMultiplier = 1.0
        synth.speak(utterance)
    }

    func stop() {
        if synth.isSpeaking {
            synth.stopSpeaking(at: .immediate)
        }
    }
}
