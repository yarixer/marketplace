// @ts-nocheck
import type { PageServerLoad } from './$types';
import { getAdminProducts } from '$lib/server/admin';

export const load = async ({ locals, fetch }: Parameters<PageServerLoad>[0]) => {
	const items = locals.accessToken ? await getAdminProducts(fetch, locals.accessToken) : [];

	return {
		items
	};
};