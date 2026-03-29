import type { PageServerLoad } from './$types';
import { getSellerProducts } from '$lib/server/seller';

export const load: PageServerLoad = async ({ locals, fetch }) => {
	const items = locals.accessToken ? await getSellerProducts(fetch, locals.accessToken) : [];

	return {
		items
	};
};