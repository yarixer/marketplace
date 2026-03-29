import type { PageServerLoad } from './$types';
import { getBuyerOrders } from '$lib/server/buyer';

export const load: PageServerLoad = async ({ locals, fetch }) => {
	const items = locals.accessToken ? await getBuyerOrders(fetch, locals.accessToken) : [];

	return {
		items
	};
};