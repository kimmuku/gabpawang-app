import SwiftUI

/// 캐릭터 화면. `CharacterScreen.kt` 포팅.
struct CharacterView: View {
    @Environment(\.appColors) private var colors
    let charStage: Int
    let totalPushups: Int
    let onBack: () -> Void

    var body: some View {
        VStack(spacing: 0) {
            BackHeader(title: "내 캐릭터", onBack: onBack)
            ScrollView {
                VStack(spacing: 16) {
                    GabpaCharacter(stage: charStage, size: 220, glow: true)
                        .padding(.top, 16)

                    Text(STAGE_NAMES[charStage])
                        .font(GabpaTypography.titleL)
                        .foregroundStyle(colors.textPrimary)
                    Text("누적 \(totalPushups)개")
                        .font(.system(size: 14))
                        .foregroundStyle(colors.textSub)

                    let next = nextThresholdFor(stage: charStage)
                    let cur = thresholdFor(stage: charStage)
                    let progressIntoStage = max(totalPushups - cur, 0)
                    let stageRange = max(next - cur, 1)
                    let remaining = max(next - totalPushups, 0)

                    VStack(alignment: .leading, spacing: 6) {
                        HStack {
                            Text("\(charStage)단계").foregroundStyle(colors.textSub)
                                .font(.system(size: 13))
                            Spacer()
                            Text("레벨업까지 \(remaining)개")
                                .font(.system(size: 14, weight: .heavy))
                                .foregroundStyle(colors.accent)
                        }
                        GabpaProgressBar(
                            value: Double(progressIntoStage),
                            max: Double(stageRange),
                            height: 12
                        )
                    }
                    .padding(.horizontal, 24)
                    .padding(.top, 8)

                    SectionTitle(text: "진화 로드맵")
                    LazyVGrid(columns: [.init(.flexible()), .init(.flexible())], spacing: 12) {
                        ForEach(1...10, id: \.self) { stage in
                            roadmapCell(stage: stage)
                        }
                    }
                    .padding(.horizontal, 16)
                    .padding(.bottom, 24)
                }
            }
        }
        .background(colors.bgDark)
    }

    private enum CellState { case done, current, locked }

    @ViewBuilder
    private func roadmapCell(stage: Int) -> some View {
        let state: CellState =
            stage < charStage ? .done :
            stage == charStage ? .current : .locked
        VStack(spacing: 6) {
            ZStack {
                GabpaCharacter(stage: stage, size: 70)
                    .opacity(state == .locked ? 0.35 : 1)
                if state == .locked {
                    Image(systemName: "lock.fill")
                        .foregroundStyle(.white.opacity(0.7))
                }
            }
            Text("\(stage)단계").font(.system(size: 12, weight: .bold))
                .foregroundStyle(state == .current ? .gabpaYellow : colors.textPrimary)
            Text(STAGE_NAMES[stage]).font(.system(size: 11)).foregroundStyle(colors.textSub)
            Text("\(thresholdFor(stage: stage))개+")
                .font(.system(size: 10))
                .foregroundStyle(colors.textSub)
        }
        .frame(maxWidth: .infinity)
        .padding(12)
        .background(state == .current ? colors.bgCard : colors.bgCard.opacity(0.6))
        .overlay(
            RoundedRectangle(cornerRadius: 14, style: .continuous)
                .stroke(state == .current ? Color.gabpaYellow : colors.borderCard,
                        lineWidth: state == .current ? 1.5 : 1)
        )
        .clipShape(RoundedRectangle(cornerRadius: 14, style: .continuous))
    }
}
