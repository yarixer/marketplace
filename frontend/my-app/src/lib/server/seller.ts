import type {
	SellerDashboardResponse,
	SellerMonthlySalesPointResponse,
	SellerProductDetailsResponse,
	SellerProductListItemResponse,
	SellerProductStatsRowResponse,
	SellerProductSubmitReviewResponse
} from '$lib/types/api';
import { backendRequest } from '$lib/server/backend';

export async function getSellerDashboard(fetchFn: typeof fetch, accessToken: string) {
	return backendRequest<SellerDashboardResponse>(fetchFn, '/api/seller/dashboard', {
		token: accessToken
	});
}

export async function getSellerProducts(fetchFn: typeof fetch, accessToken: string) {
	return backendRequest<SellerProductListItemResponse[]>(fetchFn, '/api/seller/products', {
		token: accessToken
	});
}

export async function getSellerProduct(fetchFn: typeof fetch, accessToken: string, productId: number) {
	return backendRequest<SellerProductDetailsResponse>(
		fetchFn,
		`/api/seller/products/${productId}`,
		{
			token: accessToken
		}
	);
}

export async function createSellerProduct(
	fetchFn: typeof fetch,
	accessToken: string,
	payload: { title: string }
) {
	return backendRequest<SellerProductDetailsResponse>(fetchFn, '/api/seller/products', {
		method: 'POST',
		token: accessToken,
		json: payload
	});
}

export async function updateSellerDraft(
	fetchFn: typeof fetch,
	accessToken: string,
	productId: number,
	payload: {
		title?: string;
		shortDescription?: string;
		description?: string;
		priceMinor?: number;
		tagSlugs?: string[];
	}
) {
	return backendRequest<SellerProductDetailsResponse>(
		fetchFn,
		`/api/seller/products/${productId}/draft`,
		{
			method: 'PUT',
			token: accessToken,
			json: payload
		}
	);
}

export async function uploadSellerArchive(
	fetchFn: typeof fetch,
	accessToken: string,
	productId: number,
	file: File
) {
	const formData = new FormData();
	formData.set('file', file);

	return backendRequest<SellerProductDetailsResponse>(
		fetchFn,
		`/api/seller/products/${productId}/draft/archive`,
		{
			method: 'POST',
			token: accessToken,
			body: formData
		}
	);
}

export async function uploadSellerImages(
	fetchFn: typeof fetch,
	accessToken: string,
	productId: number,
	files: File[]
) {
	const formData = new FormData();

	for (const file of files) {
		formData.append('files', file);
	}

	return backendRequest<SellerProductDetailsResponse>(
		fetchFn,
		`/api/seller/products/${productId}/draft/images`,
		{
			method: 'POST',
			token: accessToken,
			body: formData
		}
	);
}

export async function deleteSellerDraftImage(
	fetchFn: typeof fetch,
	accessToken: string,
	productId: number,
	imageId: number
) {
	return backendRequest<SellerProductDetailsResponse>(
		fetchFn,
		`/api/seller/products/${productId}/draft/images/${imageId}`,
		{
			method: 'DELETE',
			token: accessToken
		}
	);
}

export async function submitSellerReview(
	fetchFn: typeof fetch,
	accessToken: string,
	productId: number
) {
	return backendRequest<SellerProductSubmitReviewResponse>(
		fetchFn,
		`/api/seller/products/${productId}/submit-review`,
		{
			method: 'POST',
			token: accessToken
		}
	);
}

export async function getSellerMonthlySales(fetchFn: typeof fetch, accessToken: string) {
	return backendRequest<SellerMonthlySalesPointResponse[]>(
		fetchFn,
		'/api/seller/stats/monthly-sales',
		{
			token: accessToken
		}
	);
}

export async function getSellerProductStats(fetchFn: typeof fetch, accessToken: string) {
	return backendRequest<SellerProductStatsRowResponse[]>(
		fetchFn,
		'/api/seller/stats/products',
		{
			token: accessToken
		}
	);
}