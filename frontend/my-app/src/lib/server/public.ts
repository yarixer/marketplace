import type {
	PageResponse,
	ProductCardResponse,
	PublicProductDetailsResponse,
	PublicProductTagResponse
} from '$lib/types/public';
import { backendRequest } from '$lib/server/backend';

function buildSearchParams(params: Record<string, string | number | null | undefined>) {
	const searchParams = new URLSearchParams();

	for (const [key, value] of Object.entries(params)) {
		if (value === null || value === undefined || value === '') continue;
		searchParams.set(key, String(value));
	}

	const query = searchParams.toString();
	return query ? `?${query}` : '';
}

export async function getLatestProducts(fetchFn: typeof fetch, size = 10) {
	return backendRequest<PageResponse<ProductCardResponse>>(
		fetchFn,
		`/api/public/products${buildSearchParams({ page: 0, size, sort: 'relevance' })}`
	);
}

export async function getPublicProducts(
	fetchFn: typeof fetch,
	params: {
		q?: string;
		tags?: string;
		sort?: string;
		page?: number;
		size?: number;
	}
) {
	return backendRequest<PageResponse<ProductCardResponse>>(
		fetchFn,
		`/api/public/products${buildSearchParams({
			q: params.q,
			tags: params.tags,
			sort: params.sort,
			page: params.page ?? 0,
			size: params.size ?? 20
		})}`
	);
}

export async function getPublicTags(fetchFn: typeof fetch) {
	return backendRequest<PublicProductTagResponse[]>(fetchFn, '/api/public/tags');
}

export async function getPublicProductBySlug(fetchFn: typeof fetch, slug: string) {
	return backendRequest<PublicProductDetailsResponse>(fetchFn, `/api/public/products/${slug}`);
}