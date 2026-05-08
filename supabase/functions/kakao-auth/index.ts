// Supabase Edge Function — 카카오 로그인 토큰을 Supabase 세션으로 교환.
//
// 흐름:
//   1) iOS/Android 클라이언트가 카카오 SDK로 로그인 → access_token 획득
//   2) 클라이언트가 이 함수에 access_token을 POST
//   3) 함수는 카카오 API로 사용자 정보를 가져오고, 해당 카카오 ID로
//      Supabase auth 사용자(이메일 형식 `kakao_{id}@kakao.local`)를 upsert.
//   4) admin API로 sign-in URL을 만들지 않고, 클라이언트가 즉시 로그인할 수 있도록
//      `signInWithOtp` 토큰 발급 또는 직접 세션 토큰을 return.
//
// 배포:  supabase functions deploy kakao-auth
// 호출:  POST {SUPABASE_URL}/functions/v1/kakao-auth
//        body: { "access_token": "<kakao access token>" }
//
// 필요 환경변수 (Supabase Dashboard > Settings > Edge Functions):
//   SUPABASE_URL                  (자동 주입)
//   SUPABASE_SERVICE_ROLE_KEY     (Settings > API > service_role key)

import { serve } from "https://deno.land/std@0.224.0/http/server.ts";
import { createClient } from "https://esm.sh/@supabase/supabase-js@2";

const KAKAO_USER_API = "https://kapi.kakao.com/v2/user/me";

interface KakaoMe {
  id: number;
  properties?: { nickname?: string; profile_image?: string };
  kakao_account?: { email?: string };
}

const corsHeaders = {
  "Access-Control-Allow-Origin": "*",
  "Access-Control-Allow-Headers":
    "authorization, x-client-info, apikey, content-type",
  "Access-Control-Allow-Methods": "POST, OPTIONS",
};

serve(async (req) => {
  if (req.method === "OPTIONS") {
    return new Response(null, { headers: corsHeaders });
  }

  try {
    const { access_token } = await req.json();
    if (!access_token) {
      return json({ error: "access_token required" }, 400);
    }

    // 1) 카카오로 사용자 정보 조회
    const kakaoResp = await fetch(KAKAO_USER_API, {
      headers: { Authorization: `Bearer ${access_token}` },
    });
    if (!kakaoResp.ok) {
      return json({ error: "kakao verify failed" }, 401);
    }
    const me = (await kakaoResp.json()) as KakaoMe;
    const kakaoId = String(me.id);
    const email = me.kakao_account?.email ?? `kakao_${kakaoId}@kakao.local`;

    // 2) admin API로 사용자 upsert
    const admin = createClient(
      Deno.env.get("SUPABASE_URL")!,
      Deno.env.get("SUPABASE_SERVICE_ROLE_KEY")!,
    );

    // 이미 가입된 사용자라면 그대로 사용. 없으면 createUser.
    let userId: string | null = null;
    const { data: list } = await admin.auth.admin.listUsers({ page: 1, perPage: 1000 });
    const existing = list?.users.find((u) =>
      u.user_metadata?.kakao_id === kakaoId || u.email === email
    );
    if (existing) {
      userId = existing.id;
    } else {
      const { data: created, error: createErr } = await admin.auth.admin.createUser({
        email,
        email_confirm: true,
        user_metadata: {
          kakao_id: kakaoId,
          nickname: me.properties?.nickname,
          avatar_url: me.properties?.profile_image,
          provider: "kakao",
        },
      });
      if (createErr) return json({ error: createErr.message }, 500);
      userId = created.user.id;
    }

    // 3) magic link 토큰을 즉시 검증 가능한 형태로 발급.
    //    클라이언트는 이 토큰을 받아 `supabase.auth.verifyOtp`로 세션을 시작한다.
    const { data: linkData, error: linkErr } = await admin.auth.admin.generateLink({
      type: "magiclink",
      email,
    });
    if (linkErr) return json({ error: linkErr.message }, 500);

    return json({
      user_id: userId,
      email,
      // 토큰 해시. 클라이언트는 verifyOtp({ type: 'magiclink', token_hash, email })로 세션 획득.
      token_hash: linkData.properties?.hashed_token,
    });
  } catch (e) {
    return json({ error: (e as Error).message }, 500);
  }
});

function json(body: unknown, status = 200): Response {
  return new Response(JSON.stringify(body), {
    status,
    headers: { ...corsHeaders, "Content-Type": "application/json" },
  });
}
