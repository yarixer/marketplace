import { dev } from '$app/environment';
import type { Cookies } from '@sveltejs/kit';
import type { AuthTokensResponse } from '$lib/types/api';

export const ACCESS_TOKEN_COOKIE = 'models_access_token';
export const REFRESH_TOKEN_COOKIE = 'models_refresh_token';

const ACCESS_TOKEN_FALLBACK_MAX_AGE = 60 * 15;
const REFRESH_TOKEN_FALLBACK_MAX_AGE = 60 * 60 * 24 * 30;

function createCookieOptions(maxAge: number) {
	return {
		path: '/',
		httpOnly: true,
		sameSite: 'lax' as const,
		secure: !dev,
		maxAge
	};
}

function secondsUntil(isoValue: string, fallback: number) {
	const targetMs = new Date(isoValue).getTime();
	const diffSeconds = Math.floor((targetMs - Date.now()) / 1000);
	return diffSeconds > 0 ? diffSeconds : fallback;
}

export function getAccessToken(cookies: Cookies) {
	return cookies.get(ACCESS_TOKEN_COOKIE) ?? null;
}

export function getRefreshToken(cookies: Cookies) {
	return cookies.get(REFRESH_TOKEN_COOKIE) ?? null;
}

export function setAuthCookies(cookies: Cookies, tokens: AuthTokensResponse) {
	cookies.set(
		ACCESS_TOKEN_COOKIE,
		tokens.accessToken,
		createCookieOptions(
			secondsUntil(tokens.accessTokenExpiresAt, ACCESS_TOKEN_FALLBACK_MAX_AGE)
		)
	);

	cookies.set(
		REFRESH_TOKEN_COOKIE,
		tokens.refreshToken,
		createCookieOptions(
			secondsUntil(tokens.refreshTokenExpiresAt, REFRESH_TOKEN_FALLBACK_MAX_AGE)
		)
	);
}

export function clearAuthCookies(cookies: Cookies) {
	cookies.delete(ACCESS_TOKEN_COOKIE, { path: '/' });
	cookies.delete(REFRESH_TOKEN_COOKIE, { path: '/' });
}