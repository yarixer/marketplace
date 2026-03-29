// @ts-nocheck
import type { PageServerLoad } from './$types';
import { getPublicProducts, getPublicTags } from '$lib/server/public';

export const load = async ({ fetch, url }: Parameters<PageServerLoad>[0]) => {
	const q = url.searchParams.get('q') ?? '';
	const sort = url.searchParams.get('sort') ?? 'relevance';
	const page = Number(url.searchParams.get('page') ?? '0');
	const tags = url.searchParams.getAll('tags');

	const [products, availableTags] = await Promise.all([
		getPublicProducts(fetch, {
			q,
			sort,
			page: Number.isNaN(page) ? 0 : page,
			size: 20,
			tags: tags.length ? tags.join(',') : undefined
		}),
		getPublicTags(fetch)
	]);

	return {
		products,
		availableTags,
		filters: {
			q,
			sort,
			page: Number.isNaN(page) ? 0 : page,
			tags
		},
		currentUrl: `${url.pathname}${url.search}`
	};
};