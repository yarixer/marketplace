// @ts-nocheck
import type { PageServerLoad } from './$types';
import { getSellerProducts } from '$lib/server/seller';

export const load = async ({ locals, fetch }: Parameters<PageServerLoad>[0]) => {
	const items = locals.accessToken ? await getSellerProducts(fetch, locals.accessToken) : [];

	return {
		items
	};
};