import Foundation

/// Stage thresholds — index 0 corresponds to stage 1 boundary.
/// Single source of truth, identical to `STAGE_BOUNDARIES` in `AppState.kt`.
let STAGE_BOUNDARIES: [Int] = [0, 100, 500, 2000, 5000, 10000, 25000, 50000, 100000, 200000]

/// Stage names indexed from 1 (index 0 is unused).
let STAGE_NAMES: [String] = [
    "",
    "초보의 시작",
    "의욕 충만",
    "훈련생",
    "탄탄한 기초",
    "갑빠의 각성",
    "강한 전사",
    "엘리트 챔피언",
    "왕의 위엄",
    "전설의 경지",
    "최강! 갑빠왕"
]

/// Stage subtitles indexed from 1 (index 0 is unused).
let STAGE_SUBTITLES: [String] = [
    "",
    "작지만 용감한 도전자!",
    "조금씩 강해지는 중!",
    "땀 흘리며 성장 중!",
    "기본이 잡히는 단계!",
    "가슴이 커지기 시작!",
    "싸울 준비 완료!",
    "모두가 인정하는 강자!",
    "누구도 넘볼 수 없는 존재!",
    "한계를 초월한 왕!",
    "푸시업의 신! 전설의 완성!"
]

/// Returns the current stage (1...10) based on accumulated push-ups.
func stageFor(total: Int) -> Int {
    var stage = 1
    for (idx, threshold) in STAGE_BOUNDARIES.enumerated() where total >= threshold {
        stage = idx + 1
    }
    return min(max(stage, 1), 10)
}

/// Returns the threshold for a given stage (1...10).
func thresholdFor(stage: Int) -> Int {
    let s = min(max(stage, 1), 10)
    return STAGE_BOUNDARIES[s - 1]
}

/// Returns the threshold of the next stage, or current threshold if already maxed.
func nextThresholdFor(stage: Int) -> Int {
    let s = min(max(stage, 1), 10)
    return s >= 10 ? STAGE_BOUNDARIES[9] : STAGE_BOUNDARIES[s]
}

/// Returns estimated national top-% text based on max single-set reps
/// (Korean adult male reference). Returns nil when no record exists yet.
func nationalRankText(maxReps: Int) -> String? {
    guard maxReps > 0 else { return nil }
    switch maxReps {
    case 100...: return "상위 약 0.2%"
    case 90...:  return "상위 약 0.3%"
    case 80...:  return "상위 약 0.5%"
    case 75...:  return "상위 약 0.8%"
    case 70...:  return "상위 약 1%"
    case 65...:  return "상위 약 1.5%"
    case 60...:  return "상위 약 2.5%"
    case 55...:  return "상위 약 4.5%"
    case 50...:  return "상위 약 7%"
    case 45...:  return "상위 약 10%"
    case 40...:  return "상위 약 14%"
    case 35...:  return "상위 약 22%"
    case 30...:  return "상위 약 32%"
    case 25...:  return "상위 약 45%"
    case 20...:  return "상위 약 57%"
    case 15...:  return "상위 약 70%"
    case 10...:  return "상위 약 85%"
    case 5...:   return "상위 약 92%"
    default:     return "하위권"
    }
}
