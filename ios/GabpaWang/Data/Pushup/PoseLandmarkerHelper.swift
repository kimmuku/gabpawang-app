import AVFoundation
import Vision
import CoreImage
import UIKit

/// Apple Vision의 `VNDetectHumanBodyPoseRequest` 래퍼. `PoseLandmarkerHelper.kt`에 대응.
///
/// MediaPipe BlazePose를 그대로 옮기는 대신 iOS에 내장된 Vision을 사용한다.
/// - 모델 다운로드 불필요 (BlazePose 9MB 다운로드 분기가 사라짐)
/// - On-device 추론 — 영상은 절대 외부로 전송되지 않음
final class PoseLandmarkerHelper {
    /// 정규화 좌표(0~1, 좌상단 원점 기준으로 변환)와 신뢰도.
    struct Landmark {
        let x: Float
        let y: Float
        let confidence: Float
    }

    /// 푸시업 카운팅에 필요한 키포인트.
    struct PoseFrame {
        let leftShoulder: Landmark?
        let rightShoulder: Landmark?
        let leftHip: Landmark?
        let rightHip: Landmark?
        /// 디버그/오버레이용. 키는 `VNHumanBodyPoseObservation.JointName.rawValue.rawValue`.
        let allPoints: [String: Landmark]

        var hasShoulders: Bool {
            (leftShoulder != nil) || (rightShoulder != nil)
        }
        var midShoulderY: Float? {
            switch (leftShoulder, rightShoulder) {
            case (let l?, let r?): return (l.y + r.y) / 2
            case (let l?, nil): return l.y
            case (nil, let r?): return r.y
            default: return nil
            }
        }
        var hipsVisible: Bool {
            (leftHip?.confidence ?? 0) > 0.1 || (rightHip?.confidence ?? 0) > 0.1
        }
    }

    private let request: VNDetectHumanBodyPoseRequest

    var onResult: ((PoseFrame) -> Void)?
    var onError: ((Error) -> Void)?

    init() {
        let req = VNDetectHumanBodyPoseRequest()
        req.revision = VNDetectHumanBodyPoseRequestRevision1
        self.request = req
    }

    /// 카메라 프레임 분석. 백그라운드 큐에서 호출.
    func analyze(sampleBuffer: CMSampleBuffer, orientation: CGImagePropertyOrientation = .leftMirrored) {
        guard let pixelBuffer = CMSampleBufferGetImageBuffer(sampleBuffer) else { return }
        let handler = VNImageRequestHandler(
            cvPixelBuffer: pixelBuffer,
            orientation: orientation,
            options: [:]
        )
        do {
            try handler.perform([request])
            handleObservation(request.results?.first)
        } catch {
            onError?(error)
        }
    }

    private func handleObservation(_ obs: VNHumanBodyPoseObservation?) {
        guard let obs = obs else {
            onResult?(PoseFrame(
                leftShoulder: nil, rightShoulder: nil,
                leftHip: nil, rightHip: nil,
                allPoints: [:]
            ))
            return
        }
        do {
            // Vision은 좌하단 원점이라 y를 1 - y로 뒤집어 좌상단 원점으로 맞춘다.
            // (안드로이드 BlazePose는 좌상단 원점이고 PushUpCounter가 그 가정으로 동작.)
            let lShoulder = try landmark(obs, joint: .leftShoulder)
            let rShoulder = try landmark(obs, joint: .rightShoulder)
            let lHip = try landmark(obs, joint: .leftHip)
            let rHip = try landmark(obs, joint: .rightHip)

            // 디버그용 전체 포인트.
            var dict: [String: Landmark] = [:]
            let all = try obs.recognizedPoints(.all)
            for (key, p) in all where p.confidence > 0 {
                dict[key.rawValue.rawValue] = Landmark(
                    x: Float(p.location.x),
                    y: Float(1 - p.location.y),
                    confidence: Float(p.confidence)
                )
            }

            onResult?(PoseFrame(
                leftShoulder: lShoulder,
                rightShoulder: rShoulder,
                leftHip: lHip,
                rightHip: rHip,
                allPoints: dict
            ))
        } catch {
            onError?(error)
        }
    }

    private func landmark(
        _ obs: VNHumanBodyPoseObservation,
        joint: VNHumanBodyPoseObservation.JointName
    ) throws -> Landmark? {
        let p = try obs.recognizedPoint(joint)
        guard p.confidence > 0 else { return nil }
        return Landmark(
            x: Float(p.location.x),
            y: Float(1 - p.location.y),
            confidence: Float(p.confidence)
        )
    }
}
