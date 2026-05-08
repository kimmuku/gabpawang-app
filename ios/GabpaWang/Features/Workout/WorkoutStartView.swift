import SwiftUI

/// 운동 시작 — 모드 선택 화면. `WorkoutStartScreen.kt` 포팅.
struct WorkoutStartView: View {
    @Environment(\.appColors) private var colors
    let onBack: () -> Void
    let onStart: (WorkoutConfig) -> Void

    @State private var selected: String = "free"
    @State private var sets: Int = 3
    @State private var counts: [Int] = [30, 25, 20]
    @State private var timedMins: Int = 2
    @State private var guideExpanded = false

    private struct Mode: Identifiable {
        let id: String
        let title: String
        let subtitle: String
        let icon: String
    }
    private let modes: [Mode] = [
        .init(id: "free", title: "자유 모드", subtitle: "원하는 만큼 자유롭게", icon: "🆓"),
        .init(id: "target", title: "세트별 목표", subtitle: "세트당 횟수를 설정", icon: "🎯"),
        .init(id: "timed", title: "타임 모드", subtitle: "정해진 시간 동안", icon: "⏱"),
        .init(id: "challenge", title: "갑빠 챌린지", subtitle: "데일리 챌린지 도전", icon: "🏆")
    ]

    var body: some View {
        VStack(spacing: 0) {
            BackHeader(title: "푸쉬업 시작", onBack: onBack)

            ScrollView {
                VStack(alignment: .leading, spacing: 12) {
                    Text("모드 선택")
                        .font(.system(size: 16, weight: .bold))
                        .foregroundStyle(colors.textPrimary)

                    ForEach(modes) { m in
                        modeCard(m)
                    }

                    if selected == "timed" { timedSection }
                    if selected == "target" { targetSection }

                    Spacer(minLength: 12)

                    Button {
                        guideExpanded.toggle()
                    } label: {
                        HStack(spacing: 6) {
                            Text("📷").font(.system(size: 13))
                            Text("촬영 가이드")
                                .font(.system(size: 13))
                                .foregroundStyle(colors.textSub)
                            Text(guideExpanded ? "▲" : "▼")
                                .font(.system(size: 11))
                                .foregroundStyle(colors.textSub)
                        }
                        .padding(.horizontal, 12)
                        .padding(.vertical, 7)
                        .background(colors.bgCard)
                        .overlay(
                            RoundedRectangle(cornerRadius: 8, style: .continuous)
                                .stroke(colors.borderCard, lineWidth: 1)
                        )
                        .clipShape(RoundedRectangle(cornerRadius: 8, style: .continuous))
                    }
                    .buttonStyle(.plain)

                    if guideExpanded {
                        // 가이드 이미지가 에셋에 있으면 표시.
                        if UIImage(named: "pushup_guide") != nil {
                            Image("pushup_guide")
                                .resizable()
                                .scaledToFit()
                                .clipShape(RoundedRectangle(cornerRadius: 12, style: .continuous))
                        } else {
                            Text("• 휴대폰을 벽에 세워주세요\n• 화면에 어깨와 엉덩이가 함께 잡히도록\n• 전면 카메라로 본인을 비춰주세요")
                                .font(.system(size: 13))
                                .foregroundStyle(colors.textSub)
                                .padding(12)
                                .frame(maxWidth: .infinity, alignment: .leading)
                                .background(colors.bgCard)
                                .clipShape(RoundedRectangle(cornerRadius: 12, style: .continuous))
                        }
                    }

                    Spacer(minLength: 8)
                }
                .padding(.horizontal, 20)
                .padding(.top, 8)
            }

            PrimaryButton(title: "시작") {
                onStart(WorkoutConfig(
                    mode: selected,
                    targetCounts: counts,
                    targetSets: sets,
                    timedSecs: timedMins * 60
                ))
            }
            .padding(.horizontal, 20)
            .padding(.vertical, 16)
        }
        .background(colors.bgDark)
    }

    @ViewBuilder
    private func modeCard(_ m: Mode) -> some View {
        let isSel = selected == m.id
        Button {
            selected = m.id
        } label: {
            HStack(spacing: 12) {
                Text(m.icon).font(.system(size: 28))
                VStack(alignment: .leading, spacing: 2) {
                    Text(m.title)
                        .font(.system(size: 16, weight: .bold))
                        .foregroundStyle(colors.textPrimary)
                    Text(m.subtitle)
                        .font(.system(size: 13))
                        .foregroundStyle(colors.textSub)
                }
                Spacer()
                Circle()
                    .stroke(isSel ? Color.gabpaYellow : colors.borderCard, lineWidth: 2)
                    .frame(width: 22, height: 22)
                    .overlay(
                        Circle()
                            .fill(Color.gabpaYellow)
                            .frame(width: 12, height: 12)
                            .opacity(isSel ? 1 : 0)
                    )
            }
            .padding(.horizontal, 16)
            .padding(.vertical, 14)
            .background(colors.bgCard)
            .overlay(
                RoundedRectangle(cornerRadius: 12, style: .continuous)
                    .stroke(isSel ? Color.gabpaYellow : colors.borderCard, lineWidth: isSel ? 1.5 : 1)
            )
            .clipShape(RoundedRectangle(cornerRadius: 12, style: .continuous))
        }
        .buttonStyle(.plain)
    }

    private var timedSection: some View {
        VStack(alignment: .leading, spacing: 8) {
            Text("운동 시간")
                .font(.system(size: 14, weight: .bold))
                .foregroundStyle(colors.textPrimary)
                .padding(.top, 12)
            HStack(spacing: 8) {
                ForEach([1, 2, 3, 5, 10], id: \.self) { m in
                    chip("\(m)분", isSelected: timedMins == m) { timedMins = m }
                }
            }
            counterRow(label: "직접 설정", value: "\(timedMins)분",
                       canDecrement: timedMins > 1,
                       canIncrement: timedMins < 30,
                       onDec: { timedMins = max(1, timedMins - 1) },
                       onInc: { timedMins = min(30, timedMins + 1) })
        }
    }

    private var targetSection: some View {
        VStack(alignment: .leading, spacing: 8) {
            Text("세트 수")
                .font(.system(size: 14, weight: .bold))
                .foregroundStyle(colors.textPrimary)
                .padding(.top, 12)
            HStack(spacing: 8) {
                ForEach(1...5, id: \.self) { n in
                    chip("\(n)", isSelected: sets == n) {
                        sets = n
                        counts = (0..<n).map { idx in counts.indices.contains(idx) ? counts[idx] : 20 }
                    }
                }
            }

            HStack {
                Text("세트별 횟수")
                    .font(.system(size: 14, weight: .bold))
                    .foregroundStyle(colors.textPrimary)
                Spacer()
                HStack(spacing: 6) {
                    ForEach([10, 20, 30, 50], id: \.self) { preset in
                        Button {
                            counts = counts.map { _ in preset }
                        } label: {
                            Text("\(preset)")
                                .font(.system(size: 12, weight: .bold))
                                .foregroundStyle(colors.textSub)
                                .padding(.horizontal, 8)
                                .padding(.vertical, 4)
                                .background(colors.bgCard)
                                .clipShape(Capsule())
                        }
                        .buttonStyle(.plain)
                    }
                }
            }
            .padding(.top, 12)

            ForEach(counts.indices, id: \.self) { idx in
                let c = counts[idx]
                counterRow(label: "\(idx + 1)세트", value: "\(c)개",
                           canDecrement: c > 1,
                           canIncrement: c < 200,
                           onDec: { counts[idx] = max(1, c - 1) },
                           onInc: { counts[idx] = min(200, c + 1) })
            }
        }
    }

    @ViewBuilder
    private func chip(_ text: String, isSelected: Bool, action: @escaping () -> Void) -> some View {
        Button(action: action) {
            Text(text)
                .font(.system(size: 13, weight: .bold))
                .foregroundStyle(isSelected ? .black : colors.textPrimary)
                .padding(.horizontal, 14)
                .padding(.vertical, 8)
                .background(isSelected ? Color.gabpaYellow : colors.bgCard)
                .clipShape(Capsule())
        }
        .buttonStyle(.plain)
    }

    @ViewBuilder
    private func counterRow(label: String, value: String,
                            canDecrement: Bool, canIncrement: Bool,
                            onDec: @escaping () -> Void, onInc: @escaping () -> Void) -> some View {
        HStack {
            Text(label)
                .font(.system(size: 13))
                .foregroundStyle(colors.textSub)
            Spacer()
            HStack(spacing: 12) {
                roundButton("−", enabled: canDecrement, action: onDec)
                Text(value)
                    .font(.system(size: 18, weight: .bold))
                    .foregroundStyle(colors.accent)
                    .frame(minWidth: 52)
                    .multilineTextAlignment(.center)
                roundButton("+", enabled: canIncrement, action: onInc)
            }
        }
        .padding(.horizontal, 16)
        .padding(.vertical, 12)
        .background(colors.bgCard)
        .overlay(
            RoundedRectangle(cornerRadius: 12, style: .continuous)
                .stroke(colors.borderCard, lineWidth: 1)
        )
        .clipShape(RoundedRectangle(cornerRadius: 12, style: .continuous))
    }

    @ViewBuilder
    private func roundButton(_ label: String, enabled: Bool, action: @escaping () -> Void) -> some View {
        Button(action: action) {
            Text(label)
                .font(.system(size: 22, weight: .bold))
                .foregroundStyle(enabled ? colors.textPrimary : colors.textSub)
                .frame(width: 36, height: 36)
                .background(colors.bgCard)
                .clipShape(Circle())
                .overlay(Circle().stroke(colors.borderCard, lineWidth: 1))
        }
        .disabled(!enabled)
        .buttonStyle(.plain)
    }
}
