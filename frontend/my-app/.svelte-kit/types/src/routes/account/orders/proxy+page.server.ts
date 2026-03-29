// @ts-nocheck
import type { PageServerLoad } from './$types';
import { getBuyerOrders } from '$lib/server/buyer';

export const load = async ({ locals, fetch }: Parameters<PageServerLoad>[0]) => {
	const items = locals.accessToken ? await getBuyerOrders(fetch, locals.accessToken) : [];

	return {
		items
	};
};