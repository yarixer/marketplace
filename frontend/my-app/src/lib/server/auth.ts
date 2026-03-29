import type { AuthTokensResponse, AuthUserResponse } from '$lib/types/api';
import { backendRequest } from '$lib/server/backend';

export async function loginWithPassword(
	fetchFn: typeof fetch,
	payload: { email: string; password: string }
) {
	return backendRequest<AuthTokensResponse>(fetchFn, '/api/auth/login', {
		method: 'POST',
		json: payload
	});
}

export async function registerAccount(
	fetchFn: typeof fetch,
	payload: { email: string; displayName: string; password: string }
) {
	return backendRequest<AuthTokensResponse>(fetchFn, '/api/auth/register', {
		method: 'POST',
		json: payload
	});
}

export async function refreshSession(fetchFn: typeof fetch, refreshToken: string) {
	return backendRequest<AuthTokensResponse>(fetchFn, '/api/auth/refresh', {
		method: 'POST',
		json: { refreshToken }
	});
}

export async function getCurrentUser(fetchFn: typeof fetch, accessToken: string) {
	return backendRequest<AuthUserResponse>(fetchFn, '/api/account/me', {
		token: accessToken
	});
}