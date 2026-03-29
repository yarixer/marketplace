import type {
	BuyerDownloadLinkResponse,
	BuyerLibraryItemResponse,
	BuyerOrderResponse,
	BuyerPurchaseHistoryRowResponse
} from '$lib/types/api';
import { backendRequest } from '$lib/server/backend';

export async function getPurchaseHistory(fetchFn: typeof fetch, accessToken: string) {
	return backendRequest<BuyerPurchaseHistoryRowResponse[]>(fetchFn, '/api/buyer/purchase-history', {
		token: accessToken
	});
}

export async function getBuyerOrders(fetchFn: typeof fetch, accessToken: string) {
	return backendRequest<BuyerOrderResponse[]>(fetchFn, '/api/buyer/orders', {
		token: accessToken
	});
}

export async function getBuyerLibrary(fetchFn: typeof fetch, accessToken: string) {
	return backendRequest<BuyerLibraryItemResponse[]>(fetchFn, '/api/buyer/library', {
		token: accessToken
	});
}

export async function getBuyerDownloadLink(
	fetchFn: typeof fetch,
	accessToken: string,
	productId: number
) {
	return backendRequest<BuyerDownloadLinkResponse>(
		fetchFn,
		`/api/buyer/library/${productId}/download-link`,
		{
			token: accessToken
		}
	);
}

export async function createBuyerOrder(
	fetchFn: typeof fetch,
	accessToken: string,
	productId: number
) {
	return backendRequest<{ orderId: number; status: string }>(
		fetchFn,
		'/api/buyer/orders',
		{
			method: 'POST',
			token: accessToken,
			json: { productId }
		}
	);
}

export async function mockCompleteBuyerOrder(
	fetchFn: typeof fetch,
	accessToken: string,
	orderId: number
) {
	return backendRequest<{ orderId: number; status: string }>(
		fetchFn,
		`/api/buyer/orders/${orderId}/mock-complete`,
		{
			method: 'POST',
			token: accessToken
		}
	);
}