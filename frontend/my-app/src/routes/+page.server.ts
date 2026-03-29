import type { PageServerLoad } from './$types';
import { getLatestProducts } from '$lib/server/public';

export const load: PageServerLoad = async ({ fetch }) => {
	const latest = await getLatestProducts(fetch, 10);

	return {
		latestProducts: latest.items
	};
};