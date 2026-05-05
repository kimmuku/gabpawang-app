# 06. 푸쉬업 카운터 모듈 통합

> **이 모듈은 사용자가 이미 보유 중인 비전 모델을 사용합니다.**
> 이 문서는 보유 모델을 앱에 어떻게 끼워 넣을지에 대한 명세입니다.

## 핵심 시나리오

1. 사용자가 휴대폰을 벽에 세움 (카메라가 바닥을 향함)
2. 셀카 모드(전면 카메라)로 본인이 엎드리는 자세를 비춤
3. 푸쉬업 1회마다 카운트 +1 (시각/청각 피드백)
4. 세트 종료, 휴식, 다음 세트…

## 인터페이스 설계

기존 모델을 어떤 형태로 받았든, 앱에서는 **이 인터페이스만** 보면 됩니다.

```kotlin
package com.gabpaking.pushup

interface PushupCounter {
    /** 카운터 시작 — 이전 카운트 0으로 리셋 */
    fun start()

    /** 카운터 중지 — 자원 해제 */
    fun stop()

    /** 일시정지 (자세 인식 유지하지만 카운트 멈춤) */
    fun pause()
    fun resume()

    /** 프레임 분석 (CameraX ImageAnalysis 콜백에서 호출) */
    fun analyze(image: ImageProxy)

    /** 이벤트 스트림 */
    val events: SharedFlow<CounterEvent>
}

sealed interface CounterEvent {
    /** 자세가 잡혔을 때 */
    data object Ready : CounterEvent
    /** 카운트 증가 */
    data class Count(
        val total: Int,
        val cadenceMs: Long,    // 직전 카운트와의 간격
        val formScore: Float    // 0.0~1.0 자세 점수 (없으면 -1)
    ) : CounterEvent
    /** 자세를 잃음 */
    data object Lost : CounterEvent
    /** 모델 오류 */
    data class Error(val message: String, val recoverable: Boolean) : CounterEvent
}
```

## CameraX 통합

```kotlin
@Composable
fun WorkoutRunningScreen(viewModel: WorkoutRunningViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }

    LaunchedEffect(Unit) {
        val provider = ProcessCameraProvider.getInstance(context).get()
        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }
        val analysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(STRATEGY_KEEP_ONLY_LATEST)
            .setTargetResolution(Size(640, 480))
            .build()
        analysis.setAnalyzer(Executors.newSingleThreadExecutor()) { image ->
            viewModel.counter.analyze(image)
            image.close()
        }
        provider.bindToLifecycle(
            lifecycleOwner,
            CameraSelector.DEFAULT_FRONT_CAMERA,
            preview, analysis
        )
    }

    Box(Modifier.fillMaxSize()) {
        AndroidView({ previewView }, Modifier.fillMaxSize())
        WorkoutOverlay(viewModel)
    }
}
```

## 보유 모델 연결 (3가지 옵션)

### 옵션 A — TFLite (.tflite 파일)
```kotlin
class TfLitePushupCounter @Inject constructor(
    @ApplicationContext context: Context
) : PushupCounter {
    private val interpreter = Interpreter(loadModel(context, "pushup_counter.tflite"))
    private val _events = MutableSharedFlow<CounterEvent>(extraBufferCapacity = 16)
    override val events = _events.asSharedFlow()
    private var count = 0
    private var lastDownState = false

    override fun analyze(image: ImageProxy) {
        val input = preprocess(image)              // 1x224x224x3
        val output = Array(1) { FloatArray(2) }    // [up, down] 확률
        interpreter.run(input, output)
        val isDown = output[0][1] > 0.7f
        // 상→하→상 전이 시 카운트
        if (lastDownState && !isDown) {
            count += 1
            _events.tryEmit(CounterEvent.Count(count, 0L, output[0][1]))
        }
        lastDownState = isDown
    }
    // ... start/stop/pause
}
```

### 옵션 B — ML Kit Pose Detection
관절 키포인트로 직접 카운트 로직 작성 (어깨/팔꿈치 각도).

### 옵션 C — 자체 백엔드 추론
프레임을 서버로 전송 (네트워크 의존, 권장 X).

> **사용자 모델이 어느 형태인지 알려주시면** 정확한 통합 코드를 채워드립니다.

## 폴백 / 안정성

1. **수동 카운트 버튼**: 화면 우하단에 "+1" 항상 노출. 모델 실패해도 운동 중단 X
2. **자세 미인식 5초 이상**: 안내 토스트 "휴대폰을 벽에 세우고 카메라가 본인을 향하게 해주세요"
3. **밝기 부족**: ML 모델이 신호 약할 때 토스트
4. **배터리/발열**: 5분마다 체크, 임계 시 해상도 다운

## 권한

`AndroidManifest.xml`:
```xml
<uses-permission android:name="android.permission.CAMERA" />
<uses-feature android:name="android.hardware.camera.front" android:required="true" />
```

워크아웃 진입 시 권한 요청 흐름:
1. `rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission())`
2. 거절 시: 풀스크린 안내 + "설정 열기" 버튼

## 프라이버시 (반드시 준수)

- **카메라 영상은 절대 서버로 전송하지 않음** — 온디바이스 추론 only
- 운동 종료 후 프레임 버퍼 즉시 폐기
- 설정 화면에 "카메라는 카운팅에만 사용되며 어떠한 영상도 저장·전송하지 않습니다" 명시
- Play Store Data Safety 신고 시 "Camera: Not collected" 체크

## 테스트 모드

`debug` 빌드에서는 `FakePushupCounter` 주입 (Hilt qualifier). 카메라 없이 1초마다 카운트 증가 시뮬.

```kotlin
class FakePushupCounter : PushupCounter {
    private var count = 0
    private val _events = MutableSharedFlow<CounterEvent>(extraBufferCapacity = 16)
    override val events = _events.asSharedFlow()
    override fun start() {
        scope.launch {
            while (isActive) { delay(1500); count++; _events.emit(CounterEvent.Count(count, 1500L, 0.95f)) }
        }
    }
    // ...
}
```
