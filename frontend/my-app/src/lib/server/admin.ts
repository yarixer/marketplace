import type {
	AdminModerationDetailsResponse,
	AdminModerationPendingItemResponse,
	AdminProductDetailsResponse,
	AdminProductListItemResponse,
	AdminUserDetailsResponse,
	AdminUserListItemResponse
} from '$lib/types/api';
import { backendRequest } from '$lib/server/backend';

const ADMIN_MANUAL_CREDIT_PATH = '/api/admin/wallet/manual-credit';

export async function getAdminPendingRevisions(fetchFn: typeof fetch, accessToken: string) {
	return backendRequest<AdminModerationPendingItemResponse[]>(
		fetchFn,
		'/api/admin/moderation/revisions/pending',
		{
			token: accessToken
		}
	);
}

export async function getAdminPendingRevision(
	fetchFn: typeof fetch,
	accessToken: string,
	revisionId: number
) {
	return backendRequest<AdminModerationDetailsResponse>(
		fetchFn,
		`/api/admin/moderation/revisions/${revisionId}`,
		{
			token: accessToken
		}
	);
}

export async function approveAdminPendingRevision(
	fetchFn: typeof fetch,
	accessToken: string,
	revisionId: number
) {
	return backendRequest<void>(
		fetchFn,
		`/api/admin/moderation/revisions/${revisionId}/approve`,
		{
			method: 'POST',
			token: accessToken
		}
	);
}

export async function rejectAdminPendingRevision(
	fetchFn: typeof fetch,
	accessToken: string,
	revisionId: number,
	payload: { rejectionReason: string }
) {
	return backendRequest<void>(
		fetchFn,
		`/api/admin/moderation/revisions/${revisionId}/reject`,
		{
			method: 'POST',
			token: accessToken,
			json: payload
		}
	);
}

export async function getAdminUsers(fetchFn: typeof fetch, accessToken: string) {
	return backendRequest<AdminUserListItemResponse[]>(fetchFn, '/api/admin/users', {
		token: accessToken
	});
}

export async function getAdminUser(fetchFn: typeof fetch, accessToken: string, userId: number) {
	return backendRequest<AdminUserDetailsResponse>(fetchFn, `/api/admin/users/${userId}`, {
		token: accessToken
	});
}

export async function updateAdminUser(
	fetchFn: typeof fetch,
	accessToken: string,
	userId: number,
	payload: {
		displayName?: string;
		enabled?: boolean;
		roles?: string[];
	}
) {
	return backendRequest<AdminUserDetailsResponse>(fetchFn, `/api/admin/users/${userId}`, {
		method: 'PATCH',
		token: accessToken,
		json: payload
	});
}

export async function getAdminProducts(fetchFn: typeof fetch, accessToken: string) {
	return backendRequest<AdminProductListItemResponse[]>(fetchFn, '/api/admin/products', {
		token: accessToken
	});
}

export async function getAdminProduct(
	fetchFn: typeof fetch,
	accessToken: string,
	productId: number
) {
	return backendRequest<AdminProductDetailsResponse>(fetchFn, `/api/admin/products/${productId}`, {
		token: accessToken
	});
}

export async function updateAdminProductState(
	fetchFn: typeof fetch,
	accessToken: string,
	productId: number,
	state: 'ACTIVE' | 'ARCHIVED'
) {
	return backendRequest<AdminProductDetailsResponse>(
		fetchFn,
		`/api/admin/products/${productId}/state`,
		{
			method: 'PATCH',
			token: accessToken,
			json: { state }
		}
	);
}

export async function manualAdminWalletCredit(
	fetchFn: typeof fetch,
	accessToken: string,
	payload: {
		userEmail: string;
		amountMinor: number;
		note: string;
	}
) {
	return backendRequest<{
		targetUserEmail: string;
		creditedAmountMinor: number;
		newAvailableMinor: number;
	}>(fetchFn, '/api/admin/wallet/credit', {
		method: 'POST',
		token: accessToken,
		json: payload
	});
}