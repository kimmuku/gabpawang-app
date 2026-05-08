import SwiftUI

/// 기록 화면 — 1RM / 캘린더 / 통계 3탭. `RecordScreen.kt` + `RecordViewModel.kt` 포팅.
struct RecordView: View {
    @Environment(\.appColors) private var colors
    @EnvironmentObject var appVM: AppViewModel
    let onNav: (Screen) -> Void

    @State private var selectedTab: Tab = .oneRM

    enum Tab: String, CaseIterable, Identifiable {
        case oneRM = "1RM"
        case calendar = "캘린더"
        case stats = "통계"
        var id: String { rawValue }
    }

    var body: some View {
        VStack(spacing: 0) {
            HStack {
                Text("기록")
                    .font(GabpaTypography.titleXL)
                    .foregroundStyle(colors.textPrimary)
                Spacer()
            }
            .padding(.horizontal, 16)
            .padding(.top, 8)

            HStack(spacing: 0) {
                ForEach(Tab.allCases) { tab in
                    Button {
                        selectedTab = tab
                    } label: {
                        VStack(spacing: 6) {
                            Text(tab.rawValue)
                                .font(.system(size: 14, weight: .bold))
                                .foregroundStyle(selectedTab == tab ? colors.textPrimary : colors.textSub)
                                .frame(maxWidth: .infinity)
                            Rectangle()
                                .fill(selectedTab == tab ? Color.gabpaYellow : .clear)
                                .frame(height: 2)
                        }
                    }
                    .buttonStyle(.plain)
                }
            }
            .padding(.horizontal, 16)
            .padding(.top, 8)

            ScrollView {
                Group {
                    switch selectedTab {
                    case .oneRM: OneRMTab(sessions: appVM.allSessions(), oneRepMax: appVM.oneRepMax)
                    case .calendar: CalendarTab(sessions: appVM.allSessions())
                    case .stats: StatsTab(
                        totalPushups: appVM.totalPushups,
                        oneRepMax: appVM.oneRepMax,
                        streak: appVM.streak,
                        sessions: appVM.allSessions()
                    )
                    }
                }
                .padding(.horizontal, 16)
                .padding(.top, 12)
                .padding(.bottom, 24)
            }

            BottomNav(active: .record, onNav: onNav)
        }
        .background(colors.bgDark)
    }
}

private struct OneRMTab: View {
    @Environment(\.appColors) private var colors
    let sessions: [WorkoutSessionEntity]
    let oneRepMax: Int

    var body: some View {
        VStack(spacing: 16) {
            VStack(spacing: 6) {
                Text("\(oneRepMax)").font(.system(size: 64, weight: .heavy))
                    .foregroundStyle(.gabpaYellow)
                Text("1회 최고기록")
                    .font(.system(size: 13))
                    .foregroundStyle(colors.textSub)
                if let rank = nationalRankText(maxReps: oneRepMax) {
                    Text(rank)
                        .font(.system(size: 13, weight: .bold))
                        .foregroundStyle(colors.textPrimary)
                }
            }
            .frame(maxWidth: .infinity)
            .padding(20)
            .background(colors.bgCard)
            .clipShape(RoundedRectangle(cornerRadius: 14, style: .continuous))

            SectionTitle(text: "최근 운동")
            VStack(spacing: 8) {
                ForEach(sessions.prefix(10)) { s in
                    HStack {
                        VStack(alignment: .leading, spacing: 2) {
                            Text(s.date.formatted(date: .abbreviated, time: .shortened))
                                .font(.system(size: 13))
                                .foregroundStyle(colors.textPrimary)
                            Text("\(s.sets)세트 · \(s.durationSec / 60)분")
                                .font(.system(size: 11))
                                .foregroundStyle(colors.textSub)
                        }
                        Spacer()
                        Text("\(s.totalReps)회")
                            .font(.system(size: 16, weight: .heavy))
                            .foregroundStyle(.gabpaYellow)
                    }
                    .padding(.horizontal, 12)
                    .padding(.vertical, 10)
                    .background(colors.bgCard)
                    .clipShape(RoundedRectangle(cornerRadius: 12, style: .continuous))
                }
            }
        }
    }
}

private struct CalendarTab: View {
    @Environment(\.appColors) private var colors
    let sessions: [WorkoutSessionEntity]

    @State private var monthOffset = 0

    var body: some View {
        let cal = Calendar.current
        let baseMonth = cal.date(byAdding: .month, value: monthOffset, to: Date()) ?? Date()
        let comps = cal.dateComponents([.year, .month], from: baseMonth)
        let firstOfMonth = cal.date(from: comps)!
        let daysInMonth = cal.range(of: .day, in: .month, for: firstOfMonth)?.count ?? 30
        let leadingEmpty = (cal.component(.weekday, from: firstOfMonth) - 1)

        let formatter: DateFormatter = {
            let f = DateFormatter()
            f.dateFormat = "yyyy년 M월"
            return f
        }()

        let countsByDay: [Int: Int] = {
            var dict: [Int: Int] = [:]
            for s in sessions {
                let sComps = cal.dateComponents([.year, .month, .day], from: s.date)
                if sComps.year == comps.year && sComps.month == comps.month, let d = sComps.day {
                    dict[d, default: 0] += s.totalReps
                }
            }
            return dict
        }()

        return VStack(spacing: 12) {
            HStack {
                Button { monthOffset -= 1 } label: { Image(systemName: "chevron.left") }
                    .foregroundStyle(colors.textPrimary)
                Spacer()
                Text(formatter.string(from: baseMonth))
                    .font(.system(size: 16, weight: .bold))
                    .foregroundStyle(colors.textPrimary)
                Spacer()
                Button { monthOffset += 1 } label: { Image(systemName: "chevron.right") }
                    .foregroundStyle(monthOffset >= 0 ? colors.textSub : colors.textPrimary)
                    .disabled(monthOffset >= 0)
            }

            HStack {
                ForEach(["일", "월", "화", "수", "목", "금", "토"], id: \.self) { d in
                    Text(d)
                        .font(.system(size: 11))
                        .foregroundStyle(colors.textSub)
                        .frame(maxWidth: .infinity)
                }
            }

            LazyVGrid(columns: Array(repeating: GridItem(.flexible(), spacing: 4), count: 7), spacing: 4) {
                ForEach(0..<leadingEmpty, id: \.self) { _ in Color.clear.frame(height: 36) }
                ForEach(1...daysInMonth, id: \.self) { day in
                    let count = countsByDay[day] ?? 0
                    let opacity = heatOpacity(for: count)
                    VStack(spacing: 0) {
                        Text("\(day)").font(.system(size: 9)).foregroundStyle(colors.textSub)
                        if count > 0 {
                            Text("\(count)").font(.system(size: 13, weight: .bold))
                                .foregroundStyle(.black)
                        }
                    }
                    .frame(maxWidth: .infinity)
                    .frame(height: 36)
                    .background(Color.gabpaYellow.opacity(opacity))
                    .clipShape(RoundedRectangle(cornerRadius: 6, style: .continuous))
                }
            }
        }
    }

    private func heatOpacity(for count: Int) -> Double {
        switch count {
        case 0: return 0.05
        case 1...19: return 0.15
        case 20...49: return 0.30
        case 50...99: return 0.55
        default: return 0.85
        }
    }
}

private struct StatsTab: View {
    @Environment(\.appColors) private var colors
    let totalPushups: Int
    let oneRepMax: Int
    let streak: Int
    let sessions: [WorkoutSessionEntity]

    var body: some View {
        let workoutDays = Set(sessions.map {
            Calendar.current.startOfDay(for: $0.date)
        }).count

        VStack(spacing: 12) {
            HStack(spacing: 12) {
                StatCard(value: "\(totalPushups)", label: "누적 횟수")
                StatCard(value: "\(oneRepMax)", label: "1회 최고")
            }
            HStack(spacing: 12) {
                StatCard(value: "\(workoutDays)", label: "운동일")
                StatCard(value: "\(streak)", label: "연속 일수")
            }
        }
    }
}
