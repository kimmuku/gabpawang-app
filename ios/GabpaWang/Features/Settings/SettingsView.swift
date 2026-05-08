import SwiftUI

/// 설정 화면. `SettingsScreen.kt` 포팅.
struct SettingsView: View {
    @Environment(\.appColors) private var colors
    @EnvironmentObject var appState: AppState
    let onNav: (Screen) -> Void

    var body: some View {
        VStack(spacing: 0) {
            HStack {
                Text("설정")
                    .font(GabpaTypography.titleXL)
                    .foregroundStyle(colors.textPrimary)
                Spacer()
            }
            .padding(.horizontal, 16)
            .padding(.top, 8)

            ScrollView {
                VStack(spacing: 12) {
                    SectionTitle(text: "운동 설정")
                    Toggle(isOn: $appState.voiceEnabled) {
                        Text("음성 카운트")
                            .font(.system(size: 14, weight: .medium))
                            .foregroundStyle(colors.textPrimary)
                    }
                    .toggleStyle(SwitchToggleStyle(tint: .gabpaYellow))
                    .padding(.horizontal, 16)

                    SectionTitle(text: "테마")
                    Toggle(isOn: $appState.isDarkTheme) {
                        Text("다크 모드")
                            .font(.system(size: 14, weight: .medium))
                            .foregroundStyle(colors.textPrimary)
                    }
                    .toggleStyle(SwitchToggleStyle(tint: .gabpaYellow))
                    .padding(.horizontal, 16)

                    SectionTitle(text: "계정")
                    Button {
                        Task { await AuthRepository.shared.signOut() }
                    } label: {
                        HStack {
                            Text("로그아웃")
                                .font(.system(size: 14))
                                .foregroundStyle(.gabpaRedAlert)
                            Spacer()
                        }
                        .padding(.horizontal, 16)
                        .padding(.vertical, 12)
                        .background(colors.bgCard)
                    }
                    .buttonStyle(.plain)

                    SectionTitle(text: "정보")
                    HStack {
                        Text("버전")
                            .font(.system(size: 13))
                            .foregroundStyle(colors.textSub)
                        Spacer()
                        Text(version)
                            .font(.system(size: 13))
                            .foregroundStyle(colors.textPrimary)
                    }
                    .padding(.horizontal, 16)
                    .padding(.vertical, 12)
                    .background(colors.bgCard)

                    Text("카메라는 카운팅에만 사용되며 어떠한 영상도 저장·전송하지 않습니다.")
                        .font(.system(size: 11))
                        .foregroundStyle(colors.textSub)
                        .padding(.horizontal, 16)
                        .padding(.top, 16)
                        .multilineTextAlignment(.center)
                        .frame(maxWidth: .infinity)
                }
                .padding(.top, 8)
                .padding(.bottom, 24)
            }

            BottomNav(active: .settings, onNav: onNav)
        }
        .background(colors.bgDark)
    }

    private var version: String {
        let v = Bundle.main.infoDictionary?["CFBundleShortVersionString"] as? String ?? "1.0"
        let b = Bundle.main.infoDictionary?["CFBundleVersion"] as? String ?? "1"
        return "\(v) (\(b))"
    }
}
