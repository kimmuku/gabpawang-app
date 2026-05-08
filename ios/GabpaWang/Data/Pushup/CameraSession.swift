import AVFoundation
import UIKit

/// AVCaptureSession을 SwiftUI에서 쉽게 다룰 수 있게 감싼 헬퍼.
/// `WorkoutRunningScreen.kt`의 CameraX 섹션을 대체한다.
final class CameraSession: NSObject {
    let session = AVCaptureSession()
    private let videoOutput = AVCaptureVideoDataOutput()
    private let queue = DispatchQueue(label: "com.gabpawang.camera.queue")

    /// 매 프레임마다 호출. UI 갱신은 호출자가 메인 큐로 옮길 것.
    var onFrame: ((CMSampleBuffer) -> Void)?

    private(set) var isRunning = false
    private(set) var position: AVCaptureDevice.Position = .front

    override init() {
        super.init()
    }

    /// 권한 요청. callback에 권한 결과를 전달.
    static func requestPermission() async -> Bool {
        switch AVCaptureDevice.authorizationStatus(for: .video) {
        case .authorized: return true
        case .notDetermined:
            return await AVCaptureDevice.requestAccess(for: .video)
        default: return false
        }
    }

    func configure(position: AVCaptureDevice.Position = .front) throws {
        self.position = position
        session.beginConfiguration()
        session.sessionPreset = .vga640x480

        // 입력
        session.inputs.forEach { session.removeInput($0) }
        guard let device = AVCaptureDevice.default(.builtInWideAngleCamera, for: .video, position: position) else {
            throw NSError(domain: "CameraSession", code: 1,
                          userInfo: [NSLocalizedDescriptionKey: "카메라를 찾을 수 없습니다."])
        }
        let input = try AVCaptureDeviceInput(device: device)
        if session.canAddInput(input) { session.addInput(input) }

        // 출력
        session.outputs.forEach { session.removeOutput($0) }
        videoOutput.alwaysDiscardsLateVideoFrames = true
        videoOutput.videoSettings = [
            kCVPixelBufferPixelFormatTypeKey as String:
                Int(kCVPixelFormatType_32BGRA)
        ]
        videoOutput.setSampleBufferDelegate(self, queue: queue)
        if session.canAddOutput(videoOutput) { session.addOutput(videoOutput) }

        if let connection = videoOutput.connection(with: .video) {
            if connection.isVideoOrientationSupported {
                connection.videoOrientation = .portrait
            }
            if connection.isVideoMirroringSupported {
                connection.isVideoMirrored = (position == .front)
            }
        }
        session.commitConfiguration()
    }

    func start() {
        guard !isRunning else { return }
        queue.async {
            self.session.startRunning()
            self.isRunning = self.session.isRunning
        }
    }

    func stop() {
        guard isRunning else { return }
        queue.async {
            self.session.stopRunning()
            self.isRunning = false
        }
    }

    func toggleCamera() throws {
        let newPos: AVCaptureDevice.Position = (position == .front) ? .back : .front
        try configure(position: newPos)
    }
}

extension CameraSession: AVCaptureVideoDataOutputSampleBufferDelegate {
    func captureOutput(
        _ output: AVCaptureOutput,
        didOutput sampleBuffer: CMSampleBuffer,
        from connection: AVCaptureConnection
    ) {
        onFrame?(sampleBuffer)
    }
}
