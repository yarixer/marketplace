import type { AuthUserResponse, WalletBalanceResponse } from '$lib/types/api';
import { backendRequest } from '$lib/server/backend';

export async function getWallet(fetchFn: typeof fetch, accessToken: string) {
	return backendRequest<WalletBalanceResponse>(fetchFn, '/api/account/wallet', {
		token: accessToken
	});
}

export async function updateProfile(
	fetchFn: typeof fetch,
	accessToken: string,
	payload: { displayName: string }
) {
	return backendRequest<AuthUserResponse>(fetchFn, '/api/account/profile', {
		method: 'PATCH',
		token: accessToken,
		json: payload
	});
}

export async function changePassword(
	fetchFn: typeof fetch,
	accessToken: string,
	payload: {
		oldPassword: string;
		newPassword: string;
		confirmNewPassword: string;
	}
) {
	return backendRequest<void>(fetchFn, '/api/account/change-password', {
		method: 'POST',
		token: accessToken,
		json: payload
	});
}

export async function becomeSeller(
	fetchFn: typeof fetch,
	accessToken: string,
	payload: { publicName: string }
) {
	return backendRequest<AuthUserResponse>(fetchFn, '/api/account/become-seller', {
		method: 'POST',
		token: accessToken,
		json: payload
	});
}