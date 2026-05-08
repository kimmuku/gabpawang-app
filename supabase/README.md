# Supabase Edge Functions

## kakao-auth

카카오 로그인 토큰 → Supabase 세션 교환.

### 배포

```bash
# 처음 한 번
supabase login
supabase link --project-ref <YOUR_PROJECT_REF>

# 함수 배포
supabase functions deploy kakao-auth

# 환경변수 (서비스 롤 키는 자동 주입되지만 별도 설정도 가능)
supabase secrets set SOME_OTHER=...
```

### 호출 방법

```
POST {SUPABASE_URL}/functions/v1/kakao-auth
Content-Type: application/json

{ "access_token": "<카카오 access token>" }
```

응답:
```json
{
  "user_id": "<uuid>",
  "email": "kakao_123@kakao.local",
  "token_hash": "<hashed_token>"
}
```

클라이언트는 받은 `token_hash`로 즉시 Supabase 세션을 시작:

```swift
// iOS
try await SupabaseClientProvider.client.auth.verifyOTP(
    email: email,
    token: tokenHash,
    type: .magiclink
)
```

```kotlin
// Android
supabase.auth.verifyEmailOtp(
    type = OtpType.Email.MAGIC_LINK,
    email = email,
    token = tokenHash
)
```
