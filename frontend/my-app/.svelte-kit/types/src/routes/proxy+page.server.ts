// @ts-nocheck
import type { PageServerLoad } from './$types';
import { getLatestProducts } from '$lib/server/public';

export const load = async ({ fetch }: Parameters<PageServerLoad>[0]) => {
	const latest = await getLatestProducts(fetch, 10);

	return {
		latestProducts: latest.items
	};
};